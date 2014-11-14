package org.jchien.twitchbrowser.twitch;

import com.google.gson.*;
import org.jchien.twitchbrowser.util.AnnotatedPathDeserializer;
import org.jchien.twitchbrowser.util.JsonPath;

/**
 * @author jchien
 */
public class TwitchStreamMetadata {
    private static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(TwitchStreamMetadata.class, new AnnotatedPathDeserializer<>(TwitchStreamMetadata.class))
            .create();

    @JsonPath(path={"viewers"})
    private int numViewers;

    @JsonPath(path={"channel", "status"})
    private String status;

    @JsonPath(path={"channel", "display_name"})
    private String displayName;

    @JsonPath(path={"channel", "name"})
    private String name;

    @JsonPath(path={"channel", "url"})
    private String channelUrl;

    @JsonPath(path={"preview", "medium"})
    private String previewUrl;

    private TwitchStreamMetadata() {
        // use parseFrom(String jsonString) to create
    }

    public static TwitchStreamMetadata parseFrom(String jsonString) {
        return gson.fromJson(jsonString, TwitchStreamMetadata.class);
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

    public String getName() {
        return name;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    @Override
    public String toString() {
        return "TwitchStreamMetadata{" +
                "numViewers=" + numViewers +
                ", status='" + status + '\'' +
                ", displayName='" + displayName + '\'' +
                ", name='" + name + '\'' +
                ", channelUrl='" + channelUrl + '\'' +
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
        TwitchStreamMetadata tsm = TwitchStreamMetadata.parseFrom(json);
        System.out.println(tsm);
    }
}