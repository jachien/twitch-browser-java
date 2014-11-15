package org.jchien.twitchbrowser.listener;

import org.jchien.twitchbrowser.twitch.TwitchApiService;

/**
 * @author jchien
 */
public class TwitchBrowserContext {
    public static final String CTX = "TWITCH_BROWSER_CONTEXT";

    private final TwitchApiService twitchApiService;

    public TwitchBrowserContext(TwitchApiService twitchApiService) {
        this.twitchApiService = twitchApiService;
    }

    public TwitchApiService getTwitchApiService() {
        return twitchApiService;
    }
}
