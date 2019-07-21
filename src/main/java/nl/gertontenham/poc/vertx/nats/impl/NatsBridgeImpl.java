package nl.gertontenham.poc.vertx.nats.impl;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import nl.gertontenham.poc.vertx.nats.NatsBridge;

import java.io.IOException;

public class NatsBridgeImpl implements NatsBridge {

    private final Vertx vertx;
    private final Context bridgeContext;
    private Connection nc;

    public NatsBridgeImpl(Vertx vertx) {
        this.vertx = vertx;
        bridgeContext = vertx.getOrCreateContext();
    }

    public void start() throws IOException, InterruptedException {
        nc = Nats.connect();
    }
}
