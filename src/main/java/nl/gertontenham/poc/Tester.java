package nl.gertontenham.poc;


import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Tester {
    public static void main(String args[]) {

        String password = "1234";
        String beid = "79e4d731-a219-4e4d-ab94-764b79c96839";

        int iterations = 10000;

        char[] passwordChars = password.toCharArray();
        // Generate salt based on beid and password
        byte[] saltBytes = PBKDF2WithHmacSHA256(beid.toCharArray(), password.getBytes(), iterations);
        String hashedSaltString = Hex.encodeHexString(saltBytes);

        byte[] hashedPassword = PBKDF2WithHmacSHA256(passwordChars, saltBytes, iterations);
        String hashedPasswordString = Hex.encodeHexString(hashedPassword);

        System.out.println("Hashed salt: " + hashedSaltString);
        System.out.println("Hashed password: " + hashedPasswordString);
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

    private static byte[] PBKDF2WithHmacSHA512( final char[] chars, final byte[] salt, final int iterations ) {

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec( chars, salt, iterations, 512 );
            SecretKey key = skf.generateSecret( spec );
            byte[] res = key.getEncoded( );
            return res;
        } catch ( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }

}
