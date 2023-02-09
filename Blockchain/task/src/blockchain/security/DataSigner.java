package blockchain.security;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

public class DataSigner {
    private final Signature signer;
    public DataSigner(Signature signer) {
        this.signer = signer;
    }

    public byte[] sign(byte[] data, PrivateKey key){
        try {
            signer.initSign(key);
            signer.update(data);
            return signer.sign();
        } catch (InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }
}
