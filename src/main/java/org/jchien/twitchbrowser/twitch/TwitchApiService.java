package org.jchien.twitchbrowser.twitch;

import java.io.IOException;
import java.util.List;

/**
 * @author jchien
 */
public interface TwitchApiService {
    List<TwitchStream> getStreams(String gameName, int limit) throws IOException;
}
