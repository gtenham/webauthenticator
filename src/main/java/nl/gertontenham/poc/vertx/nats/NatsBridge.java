package nl.gertontenham.poc.vertx.nats;

import io.vertx.core.Vertx;
import nl.gertontenham.poc.vertx.nats.impl.NatsBridgeImpl;

/**
 * Vert.x Nats.io Bridge. Facilitates sending and receiving messages to/from Nats server.
 */
public interface NatsBridge {

    /**
     * Creates a Bridge.
     *
     * @param vertx
     *          the vertx instance to use
     * @return the (not-yet-started) bridge.
     */
    static NatsBridge create(Vertx vertx) {
        return new NatsBridgeImpl(vertx);
    }


}
