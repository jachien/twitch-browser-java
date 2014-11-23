package org.jchien.twitchbrowser.twitch;

import com.google.api.client.util.Lists;
import com.google.appengine.api.memcache.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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

    private static final long TIMEOUT_MS = 200;

    private static final long EXPIRATION_MS = TimeUnit.MINUTES.toMillis(1);

    private static final int GAME_STREAM_LIMIT = 100;

    private final AsyncMemcacheService memcache;

    private final TwitchApiService wrappedService;

    public CachingTwitchApiService(TwitchApiService wrappedService) {
        memcache = MemcacheServiceFactory.getAsyncMemcacheService(MEMCACHE_NAMESPACE);
        memcache.setErrorHandler(new ConsistentLog4jAndContinueErrorHandler(LOG, Level.ERROR));
        this.wrappedService = wrappedService;
    }

    @Override
    public List<TwitchStream> getStreams(String gameName, int limit) throws IOException {
        // assume someone else is enforcing sane upper bounds on limit

        final Future<Object> future = memcache.get(gameName);
        try {
            final long cacheStart = System.currentTimeMillis();
            final Object cacheResult = future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            final long cacheTime = System.currentTimeMillis() - cacheStart;
            if (cacheResult != null) {
                final byte[] bytes = (byte[]) cacheResult;
                final TwitchBrowserProtos.CacheEntry entry = TwitchBrowserProtos.CacheEntry.parseFrom(bytes);
                final long now = System.currentTimeMillis();
                if (!isStale(now, entry.getTimestamp())) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("cache hit took " + cacheTime + " ms for \"" + gameName + "\", result is " + getAge(now, entry.getTimestamp()) + " ms old");
                    }
                    return demarshalStreams(entry, limit);
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("stale cache hit took " + cacheTime + " ms for \"" + gameName + "\", result is " + getAge(now, entry.getTimestamp()) + " ms old");
                    }
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("cache miss took " + cacheTime + " ms for \"" + gameName + "\"");
                }
            }
        } catch (InterruptedException e) {
            LOG.warn("interrupted querying memcache for \"" + gameName + "\"", e);
        } catch (ExecutionException e) {
            LOG.warn("error querying memcache for \"" + gameName + "\"", e);
        } catch (TimeoutException e) {
            // ain't nobody got time for that
            LOG.warn("timeout querying memcache for \"" + gameName + "\"", e);
        }

        // cache miss, stale entry, or timeout
        final List<TwitchStream> results = wrappedService.getStreams(gameName, GAME_STREAM_LIMIT);

        // update cache
        final TwitchBrowserProtos.CacheEntry cacheEntry = marshalStreams(results, GAME_STREAM_LIMIT, System.currentTimeMillis());
        memcache.put(gameName, cacheEntry.toByteArray());

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
    }

    private boolean isStale(long currentTimestamp, long cacheTimestamp) {
        return currentTimestamp - cacheTimestamp > EXPIRATION_MS;
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
