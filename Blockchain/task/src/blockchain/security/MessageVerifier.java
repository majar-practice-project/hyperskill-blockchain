package blockchain.security;

import java.security.InvalidKeyException;
import java.security.Signature;
import java.security.SignatureException;

public class MessageVerifier {
    private final Signature signature;

    public MessageVerifier(Signature signature) {
        this.signature = signature;
    }

    public boolean verify(SignedData signedData) {
        try {
            signature.initVerify(signedData.publicKey());
            signature.update(signedData.data());
            return signature.verify(signedData.signature());
        } catch (InvalidKeyException | SignatureException e) {
            return false;
        }
    }
}
