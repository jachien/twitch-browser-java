package org.jchien.twitchbrowser.listener;

import org.jchien.twitchbrowser.twitch.TwitchApiService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * @author jchien
 */
public class TwitchBrowserContext {
    public static final String CTX = "TWITCH_BROWSER_CONTEXT";

    private final TwitchApiService twitchApiService;

    public static TwitchBrowserContext get(ServletConfig config) {
        final ServletContext ctx = config.getServletContext();
        return (TwitchBrowserContext)ctx.getAttribute(TwitchBrowserContext.CTX);
    }

    public TwitchBrowserContext(TwitchApiService twitchApiService) {
        this.twitchApiService = twitchApiService;
    }

    public TwitchApiService getTwitchApiService() {
        return twitchApiService;
    }
}
