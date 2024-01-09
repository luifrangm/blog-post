package com.luifrangm.aes.enums;


public enum KeyLengthEnum {

    AES_128(128),
    AES_192(192),
    AES_256(256);

    private final int lengthKey;


    KeyLengthEnum(int lengthKey) {
        this.lengthKey = lengthKey;
    }

    public int getLengthKey() {
        return this.lengthKey;
    }

}
