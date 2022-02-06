package uk.co.paulcourt.blog;

import io.helidon.common.http.DataChunk;
import io.helidon.common.http.MediaType;
import io.helidon.common.reactive.Single;
import io.helidon.media.common.ContentReaders;
import io.helidon.webserver.Handler;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Flow.Publisher;


public class MyFormParamSupport implements Service, Handler {

    private static final MyFormParamSupport INSTANCE = new MyFormParamSupport();

    @Override
    public void update(Routing.Rules rules) {
        rules.any(this);
    }

    @Override
    public void accept(ServerRequest req, ServerResponse res) {
        MediaType reqMediaType = req.headers().contentType().orElse(MediaType.TEXT_PLAIN);
        Charset charset = reqMediaType.charset().map(Charset::forName).orElse(StandardCharsets.UTF_8);

        req.content().registerReader(MyFormParamsImpl.class,
                (chunks, type) -> readContent(reqMediaType, charset, chunks)
                        .map(s -> MyFormParamsImpl.create(s, reqMediaType)).toStage());

        req.next();
    }

    /**
     *
     * @return the singleton instance of {@code FormParamSupport}
     */
    public static MyFormParamSupport create() {
        return INSTANCE;
    }

    private static Single<String> readContent(MediaType mediaType, Charset charset,
            Publisher<DataChunk> chunks) {
          return ContentReaders.readString(chunks, charset);
    }
}
