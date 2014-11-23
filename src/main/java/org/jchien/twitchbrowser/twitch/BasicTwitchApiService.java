package org.jchien.twitchbrowser.twitch;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.Lists;
import com.google.gson.*;
import org.apache.log4j.Logger;
import org.jchien.twitchbrowser.util.AnnotatedPathDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author jchien
 */
public class BasicTwitchApiService implements TwitchApiService {
    private static final Logger LOG = Logger.getLogger(BasicTwitchApiService.class);

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(TwitchStream.class, new AnnotatedPathDeserializer<>(TwitchStream.class))
            .create();

    private static final String HOST = "https://api.twitch.tv";
    private static final String STREAMS_ENDPOINT = "/kraken/streams";

    @Override
    public List<TwitchStream> getStreams(String gameName, int limit) throws IOException {
        final HttpRequest httpReq = buildRequest(gameName, limit);

        final HttpResponse httpResp;

        final long start = System.currentTimeMillis();
        try {
            httpResp = httpReq.execute();
        } catch (Exception e) {
            throw new IOException("failed to make http request for \"" + gameName + "\" to " + httpReq.getUrl(), e);
        }
        final long elapsed = System.currentTimeMillis() - start;
        LOG.info("took " + elapsed + " ms to make query for \"" + gameName + "\"");

        return parseResponse(httpResp, gameName);
    }

    private HttpRequest buildRequest(String gameName, int limit) throws IOException {
        final HttpRequestFactory httpReqFactory = HTTP_TRANSPORT.createRequestFactory();

        final GenericUrl url = new GenericUrl(HOST + STREAMS_ENDPOINT)
                .set("game", gameName)
                .set("limit", limit);

        final HttpHeaders headers = new HttpHeaders()
                .setAccept("application/vnd.twitchtv.v3+json")
                .set("Client-ID", "Twitch Browser https://github.com/jachien/twitch-browser-java");

        final HttpRequest httpReq = httpReqFactory.buildGetRequest(url)
                .setHeaders(headers);

        return httpReq;
    }

    private List<TwitchStream> parseResponse(HttpResponse httpResp, String gameName) throws IOException {
        if (200 != httpResp.getStatusCode()) {
            throw new IOException("unable to parse stream, error code " + httpResp.getStatusCode());
        }

        final List<TwitchStream> tsmList = Lists.newArrayList();
        final Charset contentCharset = httpResp.getContentCharset();
        final InputStream is = httpResp.getContent();
        try (final InputStreamReader isr = new InputStreamReader(is, contentCharset)) {
            final JsonParser jsonParser = new JsonParser();
            final JsonElement json = jsonParser.parse(isr);
            final JsonObject root = json.getAsJsonObject();
            final JsonArray streams = root.getAsJsonArray("streams");
            for (JsonElement stream : streams) {
                try {
                    final TwitchStream tsm = TwitchStream.parseFrom(GSON.toJson(stream));
                    tsmList.add(tsm);
                } catch (Exception e) {
                    LOG.error("failed to parse results for query " + gameName + ":\n" + root, e);
                }
            }
        }

        return tsmList;
    }
}
