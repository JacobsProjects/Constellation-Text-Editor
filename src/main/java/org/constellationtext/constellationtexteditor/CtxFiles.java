package org.constellationtext.constellationtexteditor;

//fixed decryption not working when you restart

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.*;
import java.util.Base64;
import java.security.NoSuchAlgorithmException;

public class CtxFiles {
    private static final String ALGORITHM = "AES";
    private SecretKey key;
    private static final String FILE_EXTENSION = ".ctxt";
    
    public CtxFiles() {
        try{
            loadKeyFromFile();
        } catch (Exception e){
        try {
            generateKey();
            saveKeyToFile();
            } catch (Exception genError) {
                System.err.println("Could not load or generate key: " + genError);
            }
        }
    }

    
    private void generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(256);
        key = keyGen.generateKey();
    }
    
    private void saveKeyToFile() throws IOException {
        String keyString = Base64.getEncoder().encodeToString(key.getEncoded());
        Path keyPath = Paths.get(System.getProperty("user.home"), ".ctxt_key");
        Files.writeString(keyPath, keyString);
    }
    
    private void loadKeyFromFile() throws IOException {
        Path keyPath = Paths.get(System.getProperty("user.home"), ".ctxt_key");
        String keyString = Files.readString(keyPath);
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        key = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }
    
    public boolean isCTXTFile(File file) {
        return file != null && file.getName().toLowerCase().endsWith(FILE_EXTENSION);
    }
    
    public String readEncryptedFile(File file) throws IOException {
        try {
            String encryptedContent = Files.readString(file.toPath());
            return decryptText(encryptedContent);
        } catch (Exception e) {
            throw new IOException("Failed to read encrypted file: " + e.getMessage());
        }
    }
    
    public void writeEncryptedFile(File file, String content) throws IOException {
        try {
            String encryptedContent = encryptText(content);
            Files.writeString(file.toPath(), encryptedContent);
        } catch (Exception e) {
            throw new IOException("Failed to write encrypted file: " + e.getMessage());
        }
    }
    
    private String encryptText(String text) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM + "/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    private String decryptText(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM + "/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }
}