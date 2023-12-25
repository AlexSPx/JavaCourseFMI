package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class RijndaelTest {
    @Test
    void testEncryptAndDecryptInMemory() throws Exception {
        SecretKey secretKey = generateSecretKey();
        Rijndael rijndael = new Rijndael(secretKey);

        String data = "Encryption test 101";

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            rijndael.encrypt(inputStream, outputStream);

            inputStream.reset();

            try (ByteArrayInputStream encryptedInputStream = new ByteArrayInputStream(outputStream.toByteArray());
                 ByteArrayOutputStream decryptedOutputStream = new ByteArrayOutputStream()) {

                rijndael.decrypt(encryptedInputStream, decryptedOutputStream);

                assertEquals(data, decryptedOutputStream.toString(), "Decrypted content should match original content");
            }
        }
    }

    @Test
    void testEncryptIncorrectKey() throws Exception {
        SecretKey secretKey = null;
        Rijndael rijndael = new Rijndael(secretKey);
        String data = "Random string";

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            assertThrows(CipherException.class,
                    () -> rijndael.encrypt(inputStream,outputStream),
                    "Invalid SecretKey");

        }
    }

    @Test
    void testDecryptIncorrectKey() throws Exception {
        SecretKey secretKey = null;
        Rijndael rijndael = new Rijndael(secretKey);
        String data = "Random string";

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            assertThrows(CipherException.class,
                    () -> rijndael.decrypt(inputStream,outputStream),
                    "Invalid SecretKey");

        }
    }

    private SecretKey generateSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }
}