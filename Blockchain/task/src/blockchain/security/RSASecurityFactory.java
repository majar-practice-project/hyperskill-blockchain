package blockchain.security;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;

public class RSASecurityFactory implements SecurityAbstractFactory{
    private static final RSASecurityFactory INSTANCE = new RSASecurityFactory();
    private static final String ALGORITHM = "RSA";
    private RSASecurityFactory(){}

    @Override
    public KeyGenerator getKeyGenerator() {
        try {
            return new KeyGenerator(KeyPairGenerator.getInstance("RSA"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataSigner getDataSigner() {
        try {
            return new DataSigner(Signature.getInstance("SHA1withRSA"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public MessageVerifier getMessageVerifier() {
        try {
            return new MessageVerifier(Signature.getInstance("SHA1withRSA"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static SecurityAbstractFactory getInstance(){
        return INSTANCE;
    }
}
