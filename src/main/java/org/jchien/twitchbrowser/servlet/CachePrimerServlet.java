package org.jchien.twitchbrowser.servlet;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.collect.Iterables;
import org.apache.log4j.Logger;
import org.jchien.twitchbrowser.listener.TwitchBrowserContext;
import org.jchien.twitchbrowser.twitch.TwitchApiService;
import org.jchien.twitchbrowser.twitch.TwitchGame;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author jchien
 */
public class CachePrimerServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(CachePrimerServlet.class);

    private TwitchApiService twitchApiService;

    private static final int NUM_GAMES_TO_PRIME = 50;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        twitchApiService = TwitchBrowserContext.get(config).getTwitchApiService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Queue taskQueue = QueueFactory.getQueue("cache-primer");
        try {
            final List<TwitchGame> allGames = twitchApiService.getPopularGames(NUM_GAMES_TO_PRIME);
            for (List<TwitchGame> gamesPart : Iterables.partition(allGames, 10)) {
                enqueue(taskQueue, gamesPart);
            }
        } catch(IOException e) {
            LOG.error("failed to get popular games, aborting cache priming", e);
        }
    }

    private void enqueue(Queue taskQueue, List<TwitchGame> games) {
        TaskOptions opts = TaskOptions.Builder.withUrl("/worker/cacheprimer");
        for (TwitchGame game : games) {
            opts = opts.param(CachePrimerWorkerServlet.GAME_PARAM, game.getGameName());
        }
        taskQueue.add(opts);
    }
}
