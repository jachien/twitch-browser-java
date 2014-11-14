package org.jchien.twitchbrowser.settings;

import com.google.api.client.util.Lists;
import com.google.appengine.repackaged.com.google.common.base.Function;
import com.google.appengine.repackaged.com.google.common.base.Joiner;
import com.google.appengine.repackaged.com.google.common.base.Splitter;
import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;
import com.google.appengine.repackaged.com.google.common.collect.Iterables;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author jchien
 */
public class Settings {
    // stored as url_encoded_game_name1:url_encoded_game_name2:url_encoded_game_name3...
    private static final String COOKIE_NAME = "SETTINGS";

    private static final int MAX_COOKIE_AGE = Integer.MAX_VALUE;

    //private static final Settings EMPTY_COOKIE = new Settings(Lists.<String>newArrayListWithCapacity(0));

    private static final List<String> DEFAULT_GAMES = ImmutableList.<String>builder()
            .add("Dota 2")
            .add("Hearthstone: Heroes of Warcraft")
            .add("Starcraft II: Heart of the Swarm")
            .build();

    private static final Settings EMPTY_COOKIE = new Settings(DEFAULT_GAMES);

    private final List<String> gameNames;

    private Settings(@Nonnull List<String> gameNames) {
        this.gameNames = gameNames;
    }

    public List<String> getGameNames() {
        return gameNames;
    }

    @Nonnull
    public static Settings getSettings(HttpServletRequest req) {
        final Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    final Iterable<String> encodedIterable = Splitter.on(":").split(cookie.getValue());
                    final Iterable<String> decodedIterable = Iterables.transform(encodedIterable, new Function<String, String>() {
                        @Nullable
                        @Override
                        public String apply(@Nullable String s) {
                            return urlDecode(s);
                        }
                    });
                    final List<String> gameNames = Lists.newArrayList(decodedIterable);
                    return new Settings(gameNames);
                }
            }
        }

        return EMPTY_COOKIE;
    }

    public Cookie getCookie()  {
        final String encoded = Joiner.on(":").join(Iterables.transform(gameNames, new Function<String, String>() {
            @Nullable
            @Override
            public String apply(@Nullable String s) {
                return urlEncode(s);
            }
        }));

        final Cookie cookie = new Cookie(COOKIE_NAME, encoded);
        cookie.setMaxAge(MAX_COOKIE_AGE);
        return cookie;
    }

    private static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // shouldn't happen
            return null;
        }
    }

    private static String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // shouldn't happen
            return null;
        }
    }
}
