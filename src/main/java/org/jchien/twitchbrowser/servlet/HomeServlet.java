package org.jchien.twitchbrowser.servlet;

import org.jchien.twitchbrowser.model.HomeModel;
import org.jchien.twitchbrowser.twitch.TwitchApiService;
import org.jchien.twitchbrowser.twitch.TwitchStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author jchien
 */
public class HomeServlet extends HttpServlet {
    private final TwitchApiService twitchApiService = new TwitchApiService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final List<TwitchStream> streams = twitchApiService.getStreams("Dota 2", 20);

        for (TwitchStream tsm : streams) {
            System.out.println(tsm);
        }


        final HomeModel model = new HomeModel();
        model.setStreamList(streams);
        req.setAttribute("model", model);

        req.getRequestDispatcher("index.jsp").forward(req, resp);
    }
}
