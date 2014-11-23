package org.jchien.twitchbrowser.listener;

import org.apache.log4j.Logger;
import org.jchien.twitchbrowser.twitch.BasicTwitchApiService;
import org.jchien.twitchbrowser.twitch.CachingTwitchApiService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author jchien
 */
public class StartupListener implements ServletContextListener {
    private static final Logger LOG = Logger.getLogger(StartupListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        final CachingTwitchApiService twitchApiService = new CachingTwitchApiService(new BasicTwitchApiService());
        final TwitchBrowserContext tbCtx = new TwitchBrowserContext(twitchApiService);
        final ServletContext ctx = servletContextEvent.getServletContext();
        ctx.setAttribute(TwitchBrowserContext.CTX, tbCtx);
        LOG.info("startup complete");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        LOG.info("shutdown complete");
    }
}
