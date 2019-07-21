package nl.gertontenham.poc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start(Future<Void> future) {

        CompositeFuture.all(
                deployHelper(AuthenticationService.class.getName()),
                deployHelper(GatewayServer.class.getName())
            ).setHandler(result -> {
                if(result.succeeded()){
                    future.complete();
                } else {
                    future.fail(result.cause());
                }
            });

    }


    private Future<Void> deployHelper(String name){
        final Future<Void> future = Future.future();
        vertx.deployVerticle(name, res -> {
            if(res.failed()){
                logger.error("Failed to deploy verticle " + name);
                future.fail(res.cause());
            } else {
                logger.info("Deployed verticle " + name);
                future.complete();
            }
        });

        return future;
    }
}
