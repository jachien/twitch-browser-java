package org.jchien.twitchbrowser.listener;

import org.apache.log4j.Logger;
import org.jchien.twitchbrowser.twitch.TwitchApiService;

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
        final TwitchApiService twitchApiService = new TwitchApiService();
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
