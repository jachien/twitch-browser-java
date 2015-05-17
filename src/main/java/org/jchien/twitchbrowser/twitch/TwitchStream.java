package org.jchien.twitchbrowser.twitch;

import com.google.gson.*;
import org.jchien.twitchbrowser.util.AnnotatedPathDeserializer;
import org.jchien.twitchbrowser.util.JsonPath;

/**
 * @author jchien
 */
public class TwitchStream {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(TwitchStream.class, new AnnotatedPathDeserializer<>(TwitchStream.class))
            .create();

    @JsonPath(path={"viewers"})
    private int numViewers = 0;

    @JsonPath(path={"channel", "status"})
    private String status = "";

    @JsonPath(path={"channel", "display_name"})
    private String displayName = "";

    @JsonPath(path={"game"})
    private String gameName = "";

    @JsonPath(path={"channel", "name"})
    private String name = "";

    @JsonPath(path={"channel", "url"})
    private String channelUrl = "";

    @JsonPath(path={"preview", "medium"})
    private String previewUrl = "";

    private TwitchStream() {
        // use parseFrom(String jsonString) to create
    }

    TwitchStream(int numViewers, String status, String displayName, String gameName, String name, String channelUrl, String previewUrl) {
        this.numViewers = numViewers;
        this.status = status;
        this.displayName = displayName;
        this.gameName = gameName;
        this.name = name;
        this.channelUrl = channelUrl;
        this.previewUrl = previewUrl;
    }

    public static TwitchStream parseFrom(String jsonString) {
        return GSON.fromJson(jsonString, TwitchStream.class);
    }

    private String getBackupChannelUrl() {
        // the twitch api frequently has a bug where a stream will be missing channel/status, channel/game, and channel/url
        // status is okay to be blank
        // channel/game is redundant from another game field which never appears to be missing
        // we'll construct the channel url from the name
        return "http://www.twitch.tv/" + name;
    }

    public int getNumViewers() {
        return numViewers;
    }

    public String getStatus() {
        return status;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getGameName() {
        return gameName;
    }

    public String getName() {
        return name;
    }

    public String getChannelUrl() {
        if (channelUrl != null && channelUrl.length() > 0) {
            return channelUrl;
        }
        return getBackupChannelUrl();
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    @Override
    public String toString() {
        return "TwitchStream{" +
                "numViewers=" + numViewers +
                ", status='" + status + '\'' +
                ", displayName='" + displayName + '\'' +
                ", gameName='" + gameName + '\'' +
                ", name='" + name + '\'' +
                ", channelUrl='" + getChannelUrl() + '\'' +
                ", previewUrl='" + previewUrl + '\'' +
                '}';
    }

    public static void main(String[] args) {
        String json = "    {\n" +
                "      \"_id\": 11776738560,\n" +
                "      \"game\": \"StarCraft II: Heart of the Swarm\",\n" +
                "      \"viewers\": 27566,\n" +
                "      \"created_at\": \"2014-11-13T18:04:43Z\",\n" +
                "      \"preview\": {\n" +
                "        \"small\": \"http:\\/\\/static-cdn.jtvnw.net\\/previews-ttv\\/live_user_taketv-80x45.jpg\",\n" +
                "        \"medium\": \"http:\\/\\/static-cdn.jtvnw.net\\/previews-ttv\\/live_user_taketv-320x180.jpg\",\n" +
                "        \"large\": \"http:\\/\\/static-cdn.jtvnw.net\\/previews-ttv\\/live_user_taketv-640x360.jpg\",\n" +
                "        \"template\": \"http:\\/\\/static-cdn.jtvnw.net\\/previews-ttv\\/live_user_taketv-{width}x{height}.jpg\"\n" +
                "      },\n" +
                "      \"_links\": {\n" +
                "        \"self\": \"https:\\/\\/api.twitch.tv\\/kraken\\/streams\\/taketv\"\n" +
                "      },\n" +
                "      \"channel\": {\n" +
                "        \"mature\": false,\n" +
                "        \"status\": \"[MAIN] Homestory Cup X - Day 1\",\n" +
                "        \"broadcaster_language\": \"en\",\n" +
                "        \"display_name\": \"TaKeTV\",\n" +
                "        \"game\": \"StarCraft II: Heart of the Swarm\",\n" +
                "        \"delay\": 0,\n" +
                "        \"language\": \"de\",\n" +
                "        \"_id\": 30186974,\n" +
                "        \"name\": \"taketv\",\n" +
                "        \"created_at\": \"2012-04-30T21:39:56Z\",\n" +
                "        \"updated_at\": \"2014-11-13T21:17:39Z\",\n" +
                "        \"logo\": \"http:\\/\\/static-cdn.jtvnw.net\\/jtv_user_pictures\\/taketv-profile_image-9c8116e72285d7b0-300x300.jpeg\",\n" +
                "        \"banner\": \"http:\\/\\/static-cdn.jtvnw.net\\/jtv_user_pictures\\/taketv-channel_header_image-8f5fa61dc32c3ad2-640x125.jpeg\",\n" +
                "        \"video_banner\": \"http:\\/\\/static-cdn.jtvnw.net\\/jtv_user_pictures\\/taketv-channel_offline_image-c6b24ceef68b0336-640x360.png\",\n" +
                "        \"background\": null,\n" +
                "        \"profile_banner\": \"http:\\/\\/static-cdn.jtvnw.net\\/jtv_user_pictures\\/taketv-profile_banner-cac2fa6d2b34ea2a-480.png\",\n" +
                "        \"profile_banner_background_color\": \"#010108\",\n" +
                "        \"partner\": true,\n" +
                "        \"url\": \"http:\\/\\/www.twitch.tv\\/taketv\",\n" +
                "        \"views\": 76994599,\n" +
                "        \"followers\": 54028,\n" +
                "        \"_links\": {\n" +
                "          \"self\": \"https:\\/\\/api.twitch.tv\\/kraken\\/channels\\/taketv\",\n" +
                "          \"follows\": \"https:\\/\\/api.twitch.tv\\/kraken\\/channels\\/taketv\\/follows\",\n" +
                "          \"commercial\": \"https:\\/\\/api.twitch.tv\\/kraken\\/channels\\/taketv\\/commercial\",\n" +
                "          \"stream_key\": \"https:\\/\\/api.twitch.tv\\/kraken\\/channels\\/taketv\\/stream_key\",\n" +
                "          \"chat\": \"https:\\/\\/api.twitch.tv\\/kraken\\/chat\\/taketv\",\n" +
                "          \"features\": \"https:\\/\\/api.twitch.tv\\/kraken\\/channels\\/taketv\\/features\",\n" +
                "          \"subscriptions\": \"https:\\/\\/api.twitch.tv\\/kraken\\/channels\\/taketv\\/subscriptions\",\n" +
                "          \"editors\": \"https:\\/\\/api.twitch.tv\\/kraken\\/channels\\/taketv\\/editors\",\n" +
                "          \"teams\": \"https:\\/\\/api.twitch.tv\\/kraken\\/channels\\/taketv\\/teams\",\n" +
                "          \"videos\": \"https:\\/\\/api.twitch.tv\\/kraken\\/channels\\/taketv\\/videos\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n";
        TwitchStream tsm = TwitchStream.parseFrom(json);
        System.out.println(tsm);

        json = "    {\n" +
                "      \"_id\": 14431101712,\n" +
                "      \"game\": \"Hearthstone: Heroes of Warcraft\",\n" +
                "      \"viewers\": 157,\n" +
                "      \"created_at\": \"2015-05-15T20:00:10Z\",\n" +
                "      \"video_height\": 540,\n" +
                "      \"average_fps\": 29.9618320611,\n" +
                "      \"_links\": {\n" +
                "        \"self\": \"https:\\/\\/api.twitch.tv\\/kraken\\/streams\\/polipotame\"\n" +
                "      },\n" +
                "      \"preview\": {\n" +
                "        \"small\": \"http:\\/\\/static-cdn.jtvnw.net\\/previews-ttv\\/live_user_polipotame-80x45.jpg\",\n" +
                "        \"medium\": \"http:\\/\\/static-cdn.jtvnw.net\\/previews-ttv\\/live_user_polipotame-320x180.jpg\",\n" +
                "        \"large\": \"http:\\/\\/static-cdn.jtvnw.net\\/previews-ttv\\/live_user_polipotame-640x360.jpg\",\n" +
                "        \"template\": \"http:\\/\\/static-cdn.jtvnw.net\\/previews-ttv\\/live_user_polipotame-{width}x{height}.jpg\"\n" +
                "      },\n" +
                "      \"channel\": {\n" +
                "        \"_id\": 74535022,\n" +
                "        \"name\": \"polipotame\",\n" +
                "        \"created_at\": \"2014-11-05T15:30:38Z\",\n" +
                "        \"updated_at\": \"2015-05-15T21:15:58Z\",\n" +
                "        \"_links\": {\n" +
                "          \"self\": \"https:\\/\\/api.twitch.tv\\/kraken\\/users\\/polipotame\"\n" +
                "        },\n" +
                "        \"display_name\": \"Polipotame\",\n" +
                "        \"logo\": \"http:\\/\\/static-cdn.jtvnw.net\\/jtv_user_pictures\\/polipotame-profile_image-46e1a1b341f4e5de-300x300.jpeg\",\n" +
                "        \"bio\": \"nothing\",\n" +
                "        \"type\": \"user\"\n" +
                "      }\n" +
                "    }";
        tsm = TwitchStream.parseFrom(json);
        System.out.println(tsm);
    }
}