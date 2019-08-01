package nl.gertontenham.poc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class AuthenticationService extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Override
    public void start(Future<Void> future) {
        logger.info("Authentication Service [" + deploymentID() + "] running ");
    }


}
