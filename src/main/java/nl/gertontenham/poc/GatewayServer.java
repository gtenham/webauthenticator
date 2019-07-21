package nl.gertontenham.poc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import nl.gertontenham.poc.data.EventDTO;
import nl.gertontenham.poc.eventbus.EventCodec;

import java.util.Arrays;

public class GatewayServer extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(GatewayServer.class);

    @Override
    public void start(Future<Void> future) {
        EventBus eventBus = getVertx().eventBus();
        // Register codec for custom message
        eventBus.registerDefaultCodec(EventDTO.class, new EventCodec());

        HttpServerOptions httpServerOptions =
                new HttpServerOptions()
                        .setCompressionSupported(true)
                        .setCompressionLevel(HttpServerOptions.DEFAULT_COMPRESSION_LEVEL);

        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.route("/auth/register").handler(this::registerHandler);
        router.route("/auth/login").handler(this::loginHandler);
        router.route("/auth/token").handler(this::tokenHandler);
        router.route("/*").handler(StaticHandler.create());

        vertx.createHttpServer(httpServerOptions)
                .requestHandler(router)
                .websocketHandler(webSocketHandler())
                .listen(8443, result -> {
                    if (result.succeeded()) {
                        logger.info("Gateway Server [" + deploymentID() + "] running on port " + 8443);
                        future.complete();
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
                    String clientId = "";
                    if (ws.query() != null && !ws.query().isEmpty()) {
                        clientId = Arrays.asList(ws.query().split("&")).stream()
                                .filter(pair -> pair.contains("X-messaging-client-id")).findFirst().get().split("=")[1];
                    }

                    logger.info("Client connected [" + clientHandlerId + "] with clientId [" + clientId + "]");

                    final String senderId = clientId;

                    JsonObject payload = new JsonObject();
                    payload.put("clientHandlerId", clientHandlerId);
                    payload.put("clientId", clientId);

                    EventDTO connectBroadcastEvent = new EventDTO.Builder()
                            .withSenderId(deploymentID())
                            .withType("ClientConnectEvent")
                            .withSubject("ws.clients")
                            .withPayload(payload)
                            .build();
                    ws.writeBinaryMessage(Json.encodeToBuffer(connectBroadcastEvent));

                    ws.binaryMessageHandler(rs -> {
                        // Incoming ws message
                        final EventDTO incomingWsEvent = Json.decodeValue(rs, EventDTO.class);
                        logger.info("Message from client [" + clientHandlerId + "] => " + incomingWsEvent.toString());

                        if (incomingWsEvent.getDeliveryMethod().equalsIgnoreCase("send")) {
                            // send (Point-to-point pattern) to a single registered handler.
                            // If there is more than one handler registered at the address,
                            // one will be chosen using a non-strict round-robin algorithm.
                            vertx.eventBus().send(incomingWsEvent.getSubject(), incomingWsEvent);
                        } else {
                            // Default: publish (publish/subscribe messaging pattern) to every registered handler
                            // Publishing means delivering the message to all handlers
                            // that are registered at that address.
                            vertx.eventBus().publish(incomingWsEvent.getSubject(), incomingWsEvent);
                        }

                    });
                    MessageConsumer<Object> broadcastedConsumer = vertx.eventBus().consumer("ws.clients");
                    MessageConsumer<Object> privateConsumer = vertx.eventBus().consumer("ws.clients." + senderId);

                    broadcastedConsumer.handler(message -> {
                        EventDTO receivedEvent = (EventDTO)message.body();

                        ws.writeBinaryMessage(Json.encodeToBuffer(receivedEvent));
                        logger.info("Consumer address ws.clients handled");
                    });

                    privateConsumer.handler(message -> {
                        EventDTO receivedEvent = (EventDTO)message.body();

                        ws.writeBinaryMessage(Json.encodeToBuffer(receivedEvent));
                        logger.info("Consumer address ws.clients." + senderId + " handled");
                    });

                    ws.closeHandler(rs -> {
                        logger.info("Client disconnected [" + clientHandlerId + "]");
                        broadcastedConsumer.unregister();
                        privateConsumer.unregister();
                    });
                } else {
                    ws.reject();
                }


            }
        };
    }

    private void registerHandler(RoutingContext context) {
        // saving private-public key for an user
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
}
