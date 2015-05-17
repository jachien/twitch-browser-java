package org.jchien.twitchbrowser.twitch;

import com.google.api.client.util.Lists;
import com.google.appengine.api.memcache.*;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author jchien
 */
public class CachingTwitchApiService implements TwitchApiService {
    private static final Logger LOG = Logger.getLogger(CachingTwitchApiService.class);

    private static final String MEMCACHE_NAMESPACE = "twitchbrowser.v1";

    // don't wait longer than this for memcached lookups
    private static final long TIMEOUT_MS = 200;

    // do fresh query if cache result is this old
    private static final long STALE_MS = TimeUnit.MINUTES.toMillis(2);

    // if query fails but cache result is at most this old, use stale cache result (it's still edible)
    // stored as int because that's what memcache uses
    private static final int EXPIRATION_MS = (int)TimeUnit.MINUTES.toMillis(5);

    public static final int GAME_STREAM_LIMIT = 25;

    private static final CacheResult EMPTY_CACHE_RESULT = new CacheResult(null, 0L);

    private final AsyncMemcacheService memcache;

    private final TwitchApiService wrappedService;

    public CachingTwitchApiService(TwitchApiService wrappedService) {
        memcache = MemcacheServiceFactory.getAsyncMemcacheService(MEMCACHE_NAMESPACE);
        memcache.setErrorHandler(new ConsistentLog4jAndContinueErrorHandler(LOG, Level.ERROR));
        this.wrappedService = wrappedService;
    }

    @Override
    public List<TwitchStream> getStreams(String gameName, int limit, boolean forceFresh) throws IOException {
        // assume someone else is enforcing sane upper bounds on limit

        final CacheResult cacheResult;
        if (!forceFresh) {
            cacheResult = getCacheResult(gameName);
        } else {
            cacheResult = EMPTY_CACHE_RESULT;
        }

        final long now = System.currentTimeMillis();
        if (cacheResult.getCacheEntry() != null) {
            final TwitchBrowserProtos.CacheEntry entry = cacheResult.getCacheEntry();
            final long age = getAge(now, entry.getTimestamp());
            if (!isStale(now, entry.getTimestamp())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("cache hit took " + cacheResult.getTiming() + " ms for \"" + gameName + "\", result is " + age + " ms old");
                }
                return demarshalStreams(entry, limit);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("stale cache hit took " + cacheResult.getTiming() + " ms for \"" + gameName + "\", result is " + age + " ms old");
                }
            }
        } else if (!forceFresh) {
            LOG.debug("cache miss took " + cacheResult.getTiming() + " ms for \"" + gameName + "\"");
        } else {
            // intentionally empty body, we didn't do a cache lookup so don't log anything
        }

        // cache miss, stale entry, timeout, or forced fresh request
        try {
            final List<TwitchStream> results = wrappedService.getStreams(gameName, GAME_STREAM_LIMIT, true);

            // update cache
            final TwitchBrowserProtos.CacheEntry cacheEntry = marshalStreams(results, GAME_STREAM_LIMIT, System.currentTimeMillis());
            // don't care about race conditions, this isn't particularly time sensitive
            // assuming results are going to be within a second or two within another
            memcache.put(gameName, cacheEntry.toByteArray(), Expiration.byDeltaMillis(EXPIRATION_MS), MemcacheService.SetPolicy.SET_ALWAYS);

            // we likely queried for more than the requested limit for caching purposes,
            // if so only return the requested limit
            if (results.size() > limit) {
                final List<TwitchStream> truncated = Lists.newArrayListWithCapacity(limit);
                for (int i=0; i < limit; i++) {
                    truncated.add(results.get(i));
                }
                return truncated;
            }

            return results;
        } catch (IOException e) {
            if (cacheResult.getCacheEntry() != null) {
                final long cacheResultAge = getAge(now, cacheResult.getCacheEntry().getTimestamp());
                LOG.error("twitch api query failed, falling back on stale cache result, aged " + cacheResultAge + " ms", e);
                return demarshalStreams(cacheResult.getCacheEntry(), limit);
            } else {
                // nothing to fall back on, rethrow exception
                throw e;
            }
        }
    }

    @Override
    public List<TwitchGame> getPopularGames(int limit) throws IOException {
        // don't want to cache this at this time
        return wrappedService.getPopularGames(limit);
    }

    @Nonnull
    public CacheResult getCacheResult(String gameName) {
        final Future<Object> future = memcache.get(gameName);
        final long cacheStart = System.currentTimeMillis();
        Object cacheResult = null;
        try {
            cacheResult = future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOG.warn("interrupted querying memcache for \"" + gameName + "\"", e);
        } catch (ExecutionException e) {
            LOG.warn("error querying memcache for \"" + gameName + "\"", e);
        } catch (TimeoutException e) {
            // ain't nobody got time for that
            LOG.warn("timeout querying memcache for \"" + gameName + "\"", e);
        }
        final long lookupTime = System.currentTimeMillis() - cacheStart;
        if (cacheResult != null) {
            final byte[] bytes = (byte[]) cacheResult;
            try {
                final TwitchBrowserProtos.CacheEntry entry = TwitchBrowserProtos.CacheEntry.parseFrom(bytes);
                return new CacheResult(entry, lookupTime);
            } catch (InvalidProtocolBufferException e) {
                LOG.warn("corrupted entry in memcache for \"" + gameName + "\"", e);
            }
        }
        return new CacheResult(null, lookupTime);
    }

    private static class CacheResult {
        private final TwitchBrowserProtos.CacheEntry cacheEntry;
        private final long timing;

        private CacheResult(TwitchBrowserProtos.CacheEntry cacheEntry, long timing) {
            this.cacheEntry = cacheEntry;
            this.timing = timing;
        }

        private TwitchBrowserProtos.CacheEntry getCacheEntry() {
            return cacheEntry;
        }

        private long getTiming() {
            return timing;
        }
    }

    private boolean isStale(long currentTimestamp, long cacheTimestamp) {
        return currentTimestamp - cacheTimestamp > STALE_MS;
    }

    private long getAge(long currentTimestamp, long cacheTimestamp) {
        return currentTimestamp - cacheTimestamp;
    }

    private List<TwitchStream> demarshalStreams(TwitchBrowserProtos.CacheEntry cacheEntry, int limit) {
        final int size = Math.min(limit, cacheEntry.getStreamCount());
        final List<TwitchStream> streams = Lists.newArrayListWithCapacity(size);
        for (int i=0; i < size; i++) {
            final TwitchBrowserProtos.StreamEntry streamEntry = cacheEntry.getStream(i);
            final TwitchStream twitchStream = demarshalStream(streamEntry);
            streams.add(twitchStream);
        }
        return streams;
    }

    private TwitchStream demarshalStream(TwitchBrowserProtos.StreamEntry streamEntry) {
        return new TwitchStream(
                streamEntry.getNumViewers(),
                streamEntry.getChannelStatus(),
                streamEntry.getChannelDisplayName(),
                streamEntry.getGameName(),
                streamEntry.getChannelName(),
                streamEntry.getChannelUrl(),
                streamEntry.getPreviewUrl()
        );
    }

    private TwitchBrowserProtos.CacheEntry marshalStreams(List<TwitchStream> twitchStreams, int limit, long currentTime) {
        final TwitchBrowserProtos.CacheEntry.Builder ceb = TwitchBrowserProtos.CacheEntry.newBuilder();
        ceb.setTimestamp(currentTime);

        final int size = Math.min(limit, twitchStreams.size());
        for (int i=0; i < size; i++) {
            final TwitchStream stream = twitchStreams.get(i);
            final TwitchBrowserProtos.StreamEntry.Builder seb = TwitchBrowserProtos.StreamEntry.newBuilder();
            seb.setNumViewers(stream.getNumViewers());
            seb.setChannelStatus(stream.getStatus());
            seb.setChannelDisplayName(stream.getDisplayName());
            seb.setGameName(stream.getGameName());
            seb.setChannelName(stream.getName());
            seb.setChannelUrl(stream.getChannelUrl());
            seb.setPreviewUrl(stream.getPreviewUrl());
            ceb.addStream(seb);
        }
        return ceb.build();
    }

    // copy of ConsistentLogAndContinueErrorHandler but adapted to use log4j instead of JUL
    private static class ConsistentLog4jAndContinueErrorHandler implements ConsistentErrorHandler {
        private final Logger log;
        private final Level level;

        private ConsistentLog4jAndContinueErrorHandler(Logger log, Level level) {
            this.log = log;
            this.level = level;
        }

        @Override
        public void handleDeserializationError(InvalidValueException e) {
            log.log(level, "deserialization error in memcache", e);
        }

        @Override
        public void handleServiceError(MemcacheServiceException e) {
            log.log(level, "service error in memcache", e);
        }
    }
}
