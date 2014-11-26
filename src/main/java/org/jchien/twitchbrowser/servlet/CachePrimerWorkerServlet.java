package org.jchien.twitchbrowser.servlet;

import org.apache.log4j.Logger;
import org.jchien.twitchbrowser.listener.TwitchBrowserContext;
import org.jchien.twitchbrowser.twitch.CachingTwitchApiService;
import org.jchien.twitchbrowser.twitch.TwitchApiService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * Worker used to keep cache warm for an individual game.
 *
 * @author jchien
 */
public class CachePrimerWorkerServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(CachePrimerWorkerServlet.class);

    public static final String GAME_PARAM = "game";

    private TwitchApiService twitchApiService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        twitchApiService = TwitchBrowserContext.get(config).getTwitchApiService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String[] gameNames = req.getParameterValues(GAME_PARAM);
        if (gameNames == null || gameNames.length == 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("eager caching " + Arrays.toString(gameNames));
        }
        for (String gameName : gameNames) {
            twitchApiService.getStreams(gameName, CachingTwitchApiService.GAME_STREAM_LIMIT, true);
        }
    }
}
