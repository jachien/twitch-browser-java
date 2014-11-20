package org.jchien.twitchbrowser.servlet;

import com.google.api.client.util.Lists;
import org.apache.log4j.Logger;
import org.jchien.twitchbrowser.listener.TwitchBrowserContext;
import org.jchien.twitchbrowser.model.HomeModel;
import org.jchien.twitchbrowser.settings.Settings;
import org.jchien.twitchbrowser.twitch.TwitchApiService;
import org.jchien.twitchbrowser.twitch.TwitchStream;
import org.jchien.twitchbrowser.util.NotFoundException;
import org.jchien.twitchbrowser.util.RequestUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author jchien
 */
public class HomeServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(HomeServlet.class);

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
        // 404 anything that's not to root path, like requests for favicon.ico (this is the default servlet)
        if (!"/".equals(req.getRequestURI())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("ignoring request to " + RequestUtils.getFullRequestUri(req));
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            throw new NotFoundException();
        }

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        final Settings settings = Settings.getSettings(req);

        // refresh cookie duration
        final Cookie cookie = settings.getCookie();
        resp.addCookie(cookie);

        final List<String> gameNames = settings.getGameNames();
        LOG.info("servicing request for " + gameNames.size() + " games, requested by " + req.getRemoteAddr() + " to " + RequestUtils.getFullRequestUri(req));

        final long startTime = System.currentTimeMillis();
        final List<TwitchStream> streams = getStreams(gameNames);
        final long fetchTime = System.currentTimeMillis() - startTime;
        final String formattedTimingString = getFormattedTimingString(fetchTime);

        final HomeModel model = new HomeModel();
        model.setStreamList(streams);
        model.setNumGames(gameNames.size());
        model.setFormattedTimingString(formattedTimingString);

        req.setAttribute("model", model);

        req.getRequestDispatcher("jsp/index.jsp").forward(req, resp);
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

    private String getFormattedTimingString(long elapsed) {
        final DecimalFormat df = new DecimalFormat("#.000");
        return df.format(elapsed / 1000.0);
    }
}
