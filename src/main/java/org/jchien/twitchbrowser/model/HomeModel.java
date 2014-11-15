package org.jchien.twitchbrowser.model;

import org.jchien.twitchbrowser.twitch.TwitchStream;

import java.util.List;

/**
 * @author jchien
 */
public class HomeModel {
    private List<TwitchStream> streamList;

    private int numGames;

    private String formattedTimingString;

    public List<TwitchStream> getStreamList() {
        return streamList;
    }

    public void setStreamList(List<TwitchStream> streamList) {
        this.streamList = streamList;
    }

    public int getNumGames() {
        return numGames;
    }

    public void setNumGames(int numGames) {
        this.numGames = numGames;
    }

    public String getFormattedTimingString() {
        return formattedTimingString;
    }

    public void setFormattedTimingString(String formattedTimingString) {
        this.formattedTimingString = formattedTimingString;
    }
}
