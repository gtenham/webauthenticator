package nl.gertontenham.poc.crypto;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.text.ParseException;

public class JWT {
    private static final Logger logger = LoggerFactory.getLogger(JWT.class);

    /**
     * Verify jwt token against provided secret key
     * @param jwtToken
     * @param key
     * @return true when valid
     */
    public static boolean verifyJWTToken(final String jwtToken, final byte[] key) {
        boolean verified = false;
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwtToken);
            JWSVerifier verifier = new MACVerifier(key);
            verified = signedJWT.verify(verifier);
        } catch (ParseException | JOSEException e) {
            logger.error("Error during verification jwt token", e);
        }
        return verified;
    }

    /**
     * Generate HS256 signed jwt token
     * @param claimsSet
     * @param hs256Key
     * @return String generated signed jwt token
     */
    public static String jwtWithHmacSHA256(final JWTClaimsSet claimsSet, final byte[] hs256Key) {
        String jwt = null;
        try {
            jwt = jwtWithHmac(claimsSet, hs256Key, JWSAlgorithm.HS256);
        } catch (JOSEException e) {
            logger.error("Error during parsing jwt token", e);
        }
        return jwt;
    }

    /**
     * Generate HS512 signed jwt token
     * @param claimsSet
     * @param hs512Key
     * @return String generated signed jwt token
     */
    public static String jwtWithHmacSHA512(final JWTClaimsSet claimsSet, final byte[] hs512Key) {
        String jwt = null;
        try {
            jwt = jwtWithHmac(claimsSet, hs512Key, JWSAlgorithm.HS512);
        } catch (JOSEException e) {
            logger.error("Error during parsing jwt token", e);
        }
        return jwt;
    }

    public static String getJWTSubject(final String jwtToken) {
        String subject = null;
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwtToken);
            subject = signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            logger.error("Error during parsing jwt token", e);
        }
        return subject;
    }

    private static String jwtWithHmac(final JWTClaimsSet claimsSet, final byte[] key, final JWSAlgorithm algorithm) throws JOSEException {
        JWSSigner signer = new MACSigner(key);
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(algorithm), claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }


}
