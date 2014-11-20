package org.jchien.twitchbrowser.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jchien
 */
public class RequestUtils {
    public static String getFullRequestUri(HttpServletRequest req) {
        if (req.getQueryString() == null) {
            return req.getRequestURI();
        }
        return req.getRequestURI() + "?" + req.getQueryString();
    }
}
