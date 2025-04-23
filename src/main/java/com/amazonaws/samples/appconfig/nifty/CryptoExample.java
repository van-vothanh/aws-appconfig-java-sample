package com.amazonaws.samples.appconfig.nifty;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

/**
 * Example class demonstrating various cryptographic operations using Java's built-in security providers.
 * This class shows how to:
 * - List available security providers
 * - Generate symmetric keys (AES)
 * - Encrypt and decrypt data
 * - Create and verify HMAC authentication codes
 */
public class CryptoExample {

    /**
     * List all available security providers.
     * This method demonstrates how to retrieve information about the installed security providers.
     */
    public static void listSecurityProviders() {
        System.out.println("Listing available security providers:");
        for (Provider provider : Security.getProviders()) {
            System.out.println("Provider: " + provider.getName());
            System.out.println("Version: " + provider.getVersion());
            System.out.println("Info: " + provider.getInfo());
            System.out.println();
        }
    }

    /**
     * Generate a new AES key with the specified key size.
     *
     * @param keySize the key size in bits (128, 192, or 256)
     * @return a SecretKey for AES encryption
     * @throws NoSuchAlgorithmException if AES is not supported
     */
    public static SecretKey generateAESKey(int keySize) throws NoSuchAlgorithmException {
        // Create a KeyGenerator instance for AES
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        
        // Initialize with the desired key size
        keyGenerator.init(keySize);
        
        // Generate and return the key
        return keyGenerator.generateKey();
    }

    /**
     * Encrypts the given plaintext using AES in CBC mode with PKCS5 padding.
     *
     * @param plaintext the text to encrypt
     * @param key the AES key to use for encryption
     * @return an EncryptionResult containing the Base64-encoded ciphertext and the IV
     * @throws Exception if any cryptographic operation fails
     */
    public static EncryptionResult encryptAES(String plaintext, SecretKey key) throws Exception {
        // Create a Cipher instance
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        // Generate a random IV (Initialization Vector)
        byte[] ivBytes = new byte[16]; // 16 bytes for AES
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        
        // Initialize the cipher for encryption
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        
        // Encrypt the plaintext
        byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        byte[] ciphertextBytes = cipher.doFinal(plaintextBytes);
        
        // Encode the ciphertext and IV to Base64
        String ciphertext = Base64.getEncoder().encodeToString(ciphertextBytes);
        String ivBase64 = Base64.getEncoder().encodeToString(ivBytes);
        
        return new EncryptionResult(ciphertext, ivBase64);
    }

    /**
     * Decrypts the given ciphertext using AES in CBC mode with PKCS5 padding.
     *
     * @param ciphertext the Base64-encoded ciphertext
     * @param ivBase64 the Base64-encoded IV
     * @param key the AES key to use for decryption
     * @return the decrypted plaintext
     * @throws Exception if any cryptographic operation fails
     */
    public static String decryptAES(String ciphertext, String ivBase64, SecretKey key) throws Exception {
        // Create a Cipher instance
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        // Decode the IV from Base64
        byte[] ivBytes = Base64.getDecoder().decode(ivBase64);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        
        // Initialize the cipher for decryption
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        
        // Decode the ciphertext from Base64 and decrypt
        byte[] ciphertextBytes = Base64.getDecoder().decode(ciphertext);
        byte[] plaintextBytes = cipher.doFinal(ciphertextBytes);
        
        // Convert the decrypted bytes back to a string
        return new String(plaintextBytes, StandardCharsets.UTF_8);
    }

    /**
     * Generates an HMAC (Hash-based Message Authentication Code) for the given message.
     *
     * @param message the message to authenticate
     * @param key the secret key for the HMAC algorithm
     * @param algorithm the HMAC algorithm to use (e.g., "HmacSHA256")
     * @return the Base64-encoded HMAC
     * @throws NoSuchAlgorithmException if the specified algorithm is not available
     * @throws InvalidKeyException if the key is inappropriate for the HMAC
     */
    public static String generateHMAC(String message, SecretKey key, String algorithm) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        // Create a Mac instance
        Mac mac = Mac.getInstance(algorithm);
        
        // Initialize the Mac with the key
        mac.init(key);
        
        // Compute the HMAC
        byte[] macBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        
        // Encode the HMAC to Base64
        return Base64.getEncoder().encodeToString(macBytes);
    }

    /**
     * Verifies an HMAC against a message.
     *
     * @param message the message to verify
     * @param expectedHmacBase64 the expected Base64-encoded HMAC
     * @param key the secret key for the HMAC algorithm
     * @param algorithm the HMAC algorithm used (e.g., "HmacSHA256")
     * @return true if the HMAC is valid, false otherwise
     * @throws NoSuchAlgorithmException if the specified algorithm is not available
     * @throws InvalidKeyException if the key is inappropriate for the HMAC
     */
    public static boolean verifyHMAC(String message, String expectedHmacBase64, SecretKey key, String algorithm) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        // Generate the HMAC for the message
        String computedHmacBase64 = generateHMAC(message, key, algorithm);
        
        // Compare the computed HMAC with the expected HMAC
        return computedHmacBase64.equals(expectedHmacBase64);
    }

    /**
     * Helper class to hold encryption results (ciphertext and IV).
     */
    public static class EncryptionResult {
        private final String ciphertext;
        private final String iv;

        public EncryptionResult(String ciphertext, String iv) {
            this.ciphertext = ciphertext;
            this.iv = iv;
        }

        public String getCiphertext() {
            return ciphertext;
        }

        public String getIv() {
            return iv;
        }
    }

    /**
     * Main method demonstrating the use of the cryptographic functions.
     */
    public static void main(String[] args) {
        try {
            // List available security providers
            listSecurityProviders();
            
            // Generate an AES key
            System.out.println("\n=== AES Key Generation ===");
            SecretKey aesKey = generateAESKey(256);
            System.out.println("AES key generated: " + Base64.getEncoder().encodeToString(aesKey.getEncoded()));
            
            // Encrypt and decrypt a message
            System.out.println("\n=== Encryption and Decryption ===");
            String originalMessage = "This is a secret message that needs encryption!";
            System.out.println("Original message: " + originalMessage);
            
            EncryptionResult encResult = encryptAES(originalMessage, aesKey);
            System.out.println("Encrypted message: " + encResult.getCiphertext());
            System.out.println("IV: " + encResult.getIv());
            
            String decryptedMessage = decryptAES(encResult.getCiphertext(), encResult.getIv(), aesKey);
            System.out.println("Decrypted message: " + decryptedMessage);
            
            // Generate and verify an HMAC
            System.out.println("\n=== HMAC Generation and Verification ===");
            String message = "This message needs authentication";
            System.out.println("Message: " + message);
            
            // Generate an HMAC key
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey hmacKey = keyGen.generateKey();
            System.out.println("HMAC key generated: " + Base64.getEncoder().encodeToString(hmacKey.getEncoded()));
            
            // Generate an HMAC
            String hmac = generateHMAC(message, hmacKey, "HmacSHA256");
            System.out.println("Generated HMAC: " + hmac);
            
            // Verify the HMAC
            boolean isValid = verifyHMAC(message, hmac, hmacKey, "HmacSHA256");
            System.out.println("HMAC verification: " + (isValid ? "Valid" : "Invalid"));
            
            // Try with a tampered message
            String tamperedMessage = message + " (tampered)";
            boolean isValidTampered = verifyHMAC(tamperedMessage, hmac, hmacKey, "HmacSHA256");
            System.out.println("Tampered message HMAC verification: " + (isValidTampered ? "Valid" : "Invalid"));
            
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algorithm not supported: " + e.getMessage());
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            System.err.println("Invalid key: " + e.getMessage());
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            System.err.println("Invalid algorithm parameter: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Crypto operation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

