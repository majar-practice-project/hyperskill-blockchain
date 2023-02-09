package blockchain.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class KeyGenerator {
    private final KeyPairGenerator generator;

    public KeyGenerator(KeyPairGenerator generator) {
        this.generator = generator;
    }

    public KeyPair generate() {
        return generator.generateKeyPair();
    }
}
