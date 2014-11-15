package org.jchien.twitchbrowser.listener;

import org.jchien.twitchbrowser.twitch.TwitchApiService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author jchien
 */
public class StartupListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        final TwitchApiService twitchApiService = new TwitchApiService();
        final TwitchBrowserContext tbCtx = new TwitchBrowserContext(twitchApiService);
        final ServletContext ctx = servletContextEvent.getServletContext();
        ctx.setAttribute(TwitchBrowserContext.CTX, tbCtx);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
