package org.jchien.twitchbrowser.util;

import javax.servlet.ServletException;

/**
 * @author jchien
 */
public class NotFoundException extends ServletException {
    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }
}
