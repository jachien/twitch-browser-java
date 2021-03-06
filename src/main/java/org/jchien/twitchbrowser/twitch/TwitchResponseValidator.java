package org.jchien.twitchbrowser.twitch;

/**
 * @author jchien
 */
public class TwitchResponseValidator {
    public static boolean isValid(TwitchStream stream) {
        return !isEmpty(stream.getChannelUrl())
                && !isEmpty(stream.getDisplayName())
                && !isEmpty(stream.getGameName())
                && !isEmpty(stream.getName())
                && !isEmpty(stream.getPreviewUrl())
                && !isEmpty(stream.getStatus());
    }

    private static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }
}
