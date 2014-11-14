package org.jchien.twitchbrowser.model;

import org.jchien.twitchbrowser.twitch.TwitchStream;

import java.util.List;

/**
 * @author jchien
 */
public class HomeModel {
    private List<TwitchStream> streamList;

    public List<TwitchStream> getStreamList() {
        return streamList;
    }

    public void setStreamList(List<TwitchStream> streamList) {
        this.streamList = streamList;
    }
}
