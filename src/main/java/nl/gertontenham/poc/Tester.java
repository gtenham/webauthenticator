package nl.gertontenham.poc;


import nl.gertontenham.poc.crypto.Hash;
import org.apache.commons.codec.binary.Hex;

public class Tester {
    public static void main(String args[]) {

        String password = "1234";
        String beid = "79e4d731-a219-4e4d-ab94-764b79c96839";

        int iterations = 10000;

        char[] passwordChars = password.toCharArray();
        // Generate salt based on beid and password
        byte[] saltBytes = Hash.PBKDF2WithHmacSHA512(beid.toCharArray(), password.getBytes(), iterations);
        String hashedSaltString = Hex.encodeHexString(saltBytes);

        byte[] hashedPassword = Hash.PBKDF2WithHmacSHA512(passwordChars, saltBytes, iterations);
        String hashedPasswordString = Hex.encodeHexString(hashedPassword);

        System.out.println("Hashed salt: " + hashedSaltString);
        System.out.println("Hashed password: " + hashedPasswordString);
    }

}
