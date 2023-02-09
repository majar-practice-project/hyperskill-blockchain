package blockchain.security;

public interface SecurityAbstractFactory {
    KeyGenerator getKeyGenerator();

    DataSigner getDataSigner();

    MessageVerifier getMessageVerifier();
}
