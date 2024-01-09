package com.luifrangm.aes.components;

import com.luifrangm.aes.enums.KeyLengthEnum;
import io.vavr.control.Try;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.KeySpec;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CryptoUtilAesGcm {

    private static final Logger LOG = Logger.getLogger(CryptoUtilAesGcm.class.getName());

    // IV solo 12 - GCM aporta los 4 bytes restantes
    private static final int LENGTH_IV = 12;

    //SALT para enriquecer la clave
    private static final int LENGTH_SALT = 16;

    //Algoritmo para construir la clave a partir de la password + SALT
    private static final String KEY_SECRET_INSTANCE = "PBKDF2WithHmacSHA256";

    //Lista de errores
    private static final String ERROR_GENERATE_KEY = "Error al generar la llave de cifrado";
    private static final String ERROR_ENCRYPTING_DATA = "Error al cifrar los datos";
    private static final String ERROR_DECRYPTING_DATA = "Error descifrando los datos";
    private static final String ERROR_INIT_CHIPER = "Error al inicializar el cifrador";

    private CryptoUtilAesGcm() {}


    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final String ALGORITHM_TYPE = "AES";
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final  int ITERATION_COUNT = 2000;


    public static String encrypt(final byte[] message, final String password, final KeyLengthEnum keyLength) {
        //Genera la SALT aleatoriamente
        final byte[] salt = getNonce(LENGTH_SALT);

        //Calcula la clavepara cifrar
        final SecretKey secretKey = getSecretKey(password,salt,keyLength);

        //Genera un IV aleatoriamente
        final byte[] iv = getNonce(LENGTH_IV);

        //Incia el cifrador en modo CIFRAR, con la clave y el IV
        final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey, iv);

        //Cifra el mensaje en caso de error devuelve una excepcion
        byte[] encryptedData =
        Try.of(()-> // Esta es una libreria de manejo de excepciones funcional.
            cipher.doFinal(message))
            .onFailure(throwable ->
                LOG.log(Level.SEVERE,ERROR_ENCRYPTING_DATA.concat(" {0}"),throwable.getMessage()))
            .getOrElseThrow(()->
                new RuntimeException(ERROR_ENCRYPTING_DATA));

        //concatena el mensaje cifrado con la SALT el IV y retorna el resultado
        return Base64.getEncoder().encodeToString(
            concatArrays(encryptedData,iv,salt));
    }

    public static String decrypt(final String cipherMessage, final String password,final KeyLengthEnum keyLength) {
        final byte[] cipherByte =
            Base64.getDecoder().decode(cipherMessage.getBytes());
        final int total = cipherByte.length;
        int start = total-(LENGTH_IV + LENGTH_SALT);

        //Separa la SALT el IV y el mensaje cifrado en arrays diferentes
        final byte[] salt = Arrays.copyOfRange(cipherByte,total-LENGTH_SALT,total);
        final byte[] strIV = Arrays.copyOfRange(cipherByte,start,total-LENGTH_SALT);
        final byte[] cipherData = Arrays.copyOfRange(cipherByte,0,start);

        //Calcula la clave con el password y la salt recuperada
        SecretKey secretKey = getSecretKey(password,salt,keyLength);

        //Incia el cifrador en modo DESCIFRAR, con la clave calculada y el IV recuperado
        Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey,strIV);

        //Descifra y devuelve el mensaje en claro, en caso de error lanza una excepcion personalizada
        return
            Try.of(()-> // Esta es una libreria de manejo de excepciones funcional.
                new String(cipher.doFinal(cipherData), UTF_8))
                .onFailure(throwable ->
                    LOG.log(Level.SEVERE,ERROR_DECRYPTING_DATA.concat(" {0}"),throwable.getMessage()))
                .getOrElseThrow(()->
                    new RuntimeException(ERROR_DECRYPTING_DATA));
    }

    private static Cipher initCipher(final int mode, final SecretKey secretKey, final byte[] iv) {
        return
            Try.of(()-> {
                final Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(mode, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
                return cipher;
            })
                .onFailure(throwable ->
                    LOG.log(Level.SEVERE,ERROR_INIT_CHIPER.concat(" {0}"), throwable.getMessage()))
                .getOrElseThrow(()-> new RuntimeException(ERROR_INIT_CHIPER));
    }

    // Crea un array de bytes aleatorio
    public static byte[] getNonce(int length) {
        final byte[] nonce = new byte[length];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    // Construye una clave del tamaño especificado a partir de la password + SALT
    private static SecretKey getSecretKey(final String password, final byte[] salt,final KeyLengthEnum keyLength) {
        return
            Try.of(()-> {
                    final KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, keyLength.getLengthKey());
                    final SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_SECRET_INSTANCE);
                    return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM_TYPE);
            })
                .onFailure(throwable ->
                    LOG.log(Level.SEVERE,ERROR_GENERATE_KEY.concat(" {0}"), throwable.getMessage()))
                .getOrElseThrow(()->
                    new RuntimeException(ERROR_GENERATE_KEY));
    }

    //Concatena arrays
    private static byte[] concatArrays(final byte[] encryptedData, final byte[] iv, final byte[] salt) {
        final int LENGTH_ENCRYPTED_DATA = encryptedData.length;
        final byte[] result = new byte[LENGTH_ENCRYPTED_DATA + LENGTH_IV + LENGTH_SALT];
        System.arraycopy(encryptedData,0,result,0,LENGTH_ENCRYPTED_DATA);
        System.arraycopy(iv,0,result,LENGTH_ENCRYPTED_DATA,LENGTH_IV);
        System.arraycopy(salt,0,result,LENGTH_ENCRYPTED_DATA + LENGTH_IV,LENGTH_SALT);
        return result;
    }

}
