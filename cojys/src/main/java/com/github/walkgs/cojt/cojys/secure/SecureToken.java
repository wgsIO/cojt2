package com.github.walkgs.cojt.cojys.secure;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface SecureToken {

    SecureRandom RANDOM = new SecureRandom();
    Base64.Encoder ENCODER = Base64.getEncoder();
    Base64.Decoder DECODER = Base64.getDecoder();

    static Token generate() {
        return generate(Token.BUFFER.clone(), true);
    }

    static Token generate(boolean registrable) {
        return generate(Token.BUFFER.clone(), registrable);
    }

    static Token generate(byte[] buffer, boolean registrable) {
        RANDOM.nextBytes(buffer);
        String token = ENCODER.encodeToString(buffer);
        if (!registrable)
            return new Token(buffer, token, false);
        while (Token.TOKENS.contains(token)) {
            RANDOM.nextBytes(buffer);
            token = ENCODER.encodeToString(buffer);
        }
        Token.TOKENS.add(token);
        return new Token(buffer, token, true);
    }

    static Token from(String token) {
        return from(token, true);
    }

    static Token from(String token, boolean registrable) {
        return generate(DECODER.decode(token), registrable);
    }

    @Getter
    @RequiredArgsConstructor
    class Token {

        private static final int TOKEN_LENGTH = 64;
        @SuppressWarnings("MismatchedReadAndWriteOfArray")
        private static final byte[] BUFFER = new byte[TOKEN_LENGTH];

        private static transient final Queue<String> TOKENS = new ConcurrentLinkedQueue<>();

        private final byte[] buffer;
        private final String token;
        @Getter(AccessLevel.NONE)
        private final boolean registrable;

        @Override
        public String toString() {
            return token;
        }

    }

}
