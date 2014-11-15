package org.jchien.twitchbrowser.twitch;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.Lists;
import com.google.gson.*;
import org.jchien.twitchbrowser.util.AnnotatedPathDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author jchien
 */
public class TwitchApiService {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(TwitchStream.class, new AnnotatedPathDeserializer<>(TwitchStream.class))
            .create();

    private static final String HOST = "https://api.twitch.tv";
    private static final String STREAMS_ENDPOINT = "/kraken/search/streams";

    public List<TwitchStream> getStreams(String gameName, int limit) throws IOException {
        final List<TwitchStream> tsmList = Lists.newArrayList();
        final HttpRequestFactory httpReqFactory = HTTP_TRANSPORT.createRequestFactory();

        final GenericUrl url = new GenericUrl(HOST + STREAMS_ENDPOINT)
                .set("q", gameName)
                .set("limit", limit);

        final HttpHeaders headers = new HttpHeaders()
                .setAccept("application/vnd.twitchtv.v3+json")
                .set("Client-ID", "Twitch Browser https://github.com/jachien/twitch-browser");

        final HttpRequest httpReq = httpReqFactory.buildGetRequest(url)
                .setHeaders(headers);

        final HttpResponse httpResp = httpReq.execute();
        if (200 != httpResp.getStatusCode()) {
            throw new IOException("unable to parse stream, error code " + httpResp.getStatusCode());
        }

        final InputStream is = httpResp.getContent();
        final InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        try {
            final JsonParser jsonParser = new JsonParser();
            final JsonElement json = jsonParser.parse(isr);
            final JsonObject root = json.getAsJsonObject();
            final JsonArray streams = root.getAsJsonArray("streams");
            for (JsonElement stream : streams) {
                TwitchStream tsm = TwitchStream.parseFrom(GSON.toJson(stream));
                tsmList.add(tsm);
            }
        } finally {
            isr.close();
        }

        return tsmList;
    }

    public static void main(String[] args) throws IOException {
        TwitchApiService s = new TwitchApiService();
        List<TwitchStream> tsmList = s.getStreams("Dota 2", 10);
        for (TwitchStream tsm : tsmList) {
            System.out.println(tsm);
        }
    }
}
