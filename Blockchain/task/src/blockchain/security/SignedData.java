package blockchain.security;

import java.security.PublicKey;
import java.util.Arrays;

public record SignedData(byte[] data, byte[] signature, PublicKey publicKey) {

    @Override
    public byte[] data() {
        return Arrays.copyOf(data, data.length);
    }
}
