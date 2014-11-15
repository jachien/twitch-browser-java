package org.jchien.twitchbrowser.servlet;

import com.google.api.client.util.Lists;
import org.jchien.twitchbrowser.listener.TwitchBrowserContext;
import org.jchien.twitchbrowser.model.HomeModel;
import org.jchien.twitchbrowser.settings.Settings;
import org.jchien.twitchbrowser.twitch.TwitchApiService;
import org.jchien.twitchbrowser.twitch.TwitchStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author jchien
 */
public class HomeServlet extends HttpServlet {
    private TwitchApiService twitchApiService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        final ServletContext ctx = config.getServletContext();
        final TwitchBrowserContext tbCtx = (TwitchBrowserContext)ctx.getAttribute(TwitchBrowserContext.CTX);
        twitchApiService = tbCtx.getTwitchApiService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        final Settings settings = Settings.getSettings(req);

        // refresh cookie duration
        final Cookie cookie = settings.getCookie();
        resp.addCookie(cookie);

        final List<String> gameNames = settings.getGameNames();
        final List<TwitchStream> streams = getStreams(gameNames);

        final HomeModel model = new HomeModel();
        model.setStreamList(streams);
        req.setAttribute("model", model);

        req.getRequestDispatcher("index.jsp").forward(req, resp);
    }

    private List<TwitchStream> getStreams(List<String> gameNames) throws IOException {
        final List<TwitchStream> streams = Lists.newArrayList();
        for (String gameName : gameNames) {
            streams.addAll(twitchApiService.getStreams(gameName, 20));
        }
        Collections.sort(streams, new Comparator<TwitchStream>() {
            @Override
            public int compare(TwitchStream o1, TwitchStream o2) {
                return o2.getNumViewers() - o1.getNumViewers();
            }
        });
        return streams;
    }
}
