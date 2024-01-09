package com.luifrangm.aes;

import com.luifrangm.aes.components.CryptoUtilAesGcm;
import com.luifrangm.aes.enums.KeyLengthEnum;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {

        final String input =
            "Luis Francisco";

        System.out.println("Mensaje en claro: " + input);

        String res =
            CryptoUtilAesGcm.encrypt(
                input.getBytes(StandardCharsets.UTF_8),
                "mi_password",
                KeyLengthEnum.AES_256);

        System.out.println("Mensaje cifrado: " + res);

        System.out.println("Mensaje descifrado: " +
            CryptoUtilAesGcm.decrypt(res,"mi_password",KeyLengthEnum.AES_256));

        System.out.println("Done.");
    }
}