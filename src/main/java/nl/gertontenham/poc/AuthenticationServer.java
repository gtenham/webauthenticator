package nl.gertontenham.poc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class AuthenticationServer extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServer.class);

    @Override
    public void start(Future<Void> future) {
        HttpServerOptions httpServerOptions =
                new HttpServerOptions()
                        .setCompressionSupported(true)
                        .setCompressionLevel(HttpServerOptions.DEFAULT_COMPRESSION_LEVEL);

        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.route("/login").handler(this::loginHandler);
        router.route("/token").handler(this::tokenHandler);
        router.route("/*").handler(StaticHandler.create());

        vertx.createHttpServer(httpServerOptions)
                .websocketHandler(webSocketHandler())
                .requestHandler(router)
                .listen(8888, result -> {
                    if (result.succeeded()) {
                        logger.info("Server [" + deploymentID() + "] running on port " + 8888);
                    } else {
                        future.fail(result.cause());
                    }
                });
    }

    private Handler<ServerWebSocket> webSocketHandler() {

        return new Handler<ServerWebSocket>() {
            @Override
            public void handle(final ServerWebSocket ws) {
                if (ws.path().equalsIgnoreCase("/messaging/event")) {
                    final String clientHandlerId = ws.binaryHandlerID();
                    logger.info("Client connected [" + clientHandlerId + "]");
                    ws.textMessageHandler(rs -> {
                        // Incoming message
                        logger.info("Message from client [" + clientHandlerId + "] => " + rs);
                    });
                } else {
                    ws.reject();
                }
            }
        };
    }

    private void loginHandler(RoutingContext context) {
        if (context.request().method().equals(HttpMethod.GET)) {
            context.response()
                    .setStatusCode(200)
                    .end("Welkom bij login");
        } else {
            context.response()
                    .setStatusCode(405)
                    .end("Method not allowed here");
        }
    }

    private void tokenHandler(RoutingContext context) {
        if (context.request().method().equals(HttpMethod.GET)) {
            context.response()
                    .setStatusCode(200)
                    .end("Welkom bij token");
        } else {
            context.response()
                    .setStatusCode(405)
                    .end("Method not allowed here");
        }
    }

    private static byte[] PBKDF2WithHmacSHA256( final char[] chars, final byte[] salt, final int iterations ) {

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA256" );
            PBEKeySpec spec = new PBEKeySpec( chars, salt, iterations, 256 );
            SecretKey key = skf.generateSecret( spec );
            byte[] res = key.getEncoded( );
            return res;
        } catch ( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }
}
