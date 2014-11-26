package org.jchien.twitchbrowser.twitch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jchien.twitchbrowser.util.AnnotatedPathDeserializer;
import org.jchien.twitchbrowser.util.JsonPath;

/**
 * @author jchien
 */
public class TwitchGame {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(TwitchGame.class, new AnnotatedPathDeserializer<>(TwitchGame.class))
            .create();

    @JsonPath(path={"game", "name"})
    private String gameName = "";

    @JsonPath(path={"game", "_id"})
    private int id = 0;

    @JsonPath(path={"game", "gamebomb_id"})
    private int gamebombId = 0;

    @JsonPath(path={"viewers"})
    private int numViewers = 0;

    @JsonPath(path={"channels"})
    private int numChannels = 0;

    @JsonPath(path={"game", "box", "small"})
    private String boxArtSmallUrl = "";

    @JsonPath(path={"game", "logo", "small"})
    private String logoSmallUrl = "";

    private TwitchGame() {
    }

    TwitchGame(String gameName, int id, int gamebombId, int numViewers, int numChannels, String boxArtSmallUrl, String logoSmallUrl) {
        this.gameName = gameName;
        this.id = id;
        this.gamebombId = gamebombId;
        this.numViewers = numViewers;
        this.numChannels = numChannels;
        this.boxArtSmallUrl = boxArtSmallUrl;
        this.logoSmallUrl = logoSmallUrl;
    }

    public static TwitchGame parseFrom(String jsonString) {
        return GSON.fromJson(jsonString, TwitchGame.class);
    }

    public String getGameName() {
        return gameName;
    }

    public int getId() {
        return id;
    }

    public int getGamebombId() {
        return gamebombId;
    }

    public int getNumViewers() {
        return numViewers;
    }

    public int getNumChannels() {
        return numChannels;
    }

    public String getBoxArtSmallUrl() {
        return boxArtSmallUrl;
    }

    public String getLogoSmallUrl() {
        return logoSmallUrl;
    }

    @Override
    public String toString() {
        return "TwitchGame{" +
                "gameName='" + gameName + '\'' +
                ", id=" + id +
                ", gamebombId=" + gamebombId +
                ", numViewers=" + numViewers +
                ", numChannels=" + numChannels +
                ", boxArtSmallUrl='" + boxArtSmallUrl + '\'' +
                ", logoSmallUrl='" + logoSmallUrl + '\'' +
                '}';
    }
}
