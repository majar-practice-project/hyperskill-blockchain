package blockchain;

import blockchain.domain.Block;
import blockchain.domain.BlockChain;
import blockchain.domain.BlockChainAppendDirector;
import blockchain.domain.transaction.RandomTransactionGenerator;
import blockchain.security.KeyGenerator;
import blockchain.security.RSASecurityFactory;
import blockchain.supplier.DataBuffer;
import blockchain.supplier.TransactionSupplier;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.stream.Collectors;

public class Main {
    private static int MINER_NUM = 10;
    private static int BLOCK_NUM = 15;

    public static void main(String[] args) {
        start();
    }

    private static void start() {
        DataBuffer buffer = new DataBuffer();
        BlockChainAppendDirector director = new BlockChainAppendDirector(MINER_NUM, new BlockChain(), buffer);
        TransactionSupplier supplier = new TransactionSupplier(buffer, director.getIdSupplier(), new RandomTransactionGenerator(MINER_NUM));
        supplier.start();

        for (int i = 0; i < BLOCK_NUM; i++) {
            getAndShowNewBlock(director);
        }
        supplier.stop();
    }

    private static void getAndShowNewBlock(BlockChainAppendDirector director) {
        Block block = director.appendNewBlock();

        System.out.printf("""
                        Block:
                        Created by: miner%1$d
                        miner%d gets 100 VC
                        Id: %d
                        Timestamp: %d
                        Magic number: %d
                        Hash of the previous block:
                        %s
                        Hash of the block:
                        %s
                        Block data: %s
                        Block was generating for %d seconds
                        """,
                block.getProducerMinerId(), block.getId(), block.getTimestamp(), block.getMagicNumber(),
                block.getPrevBlockHash(), block.getBlockHash(), getMessages(block), director.getRecentDuration() / 1000);

        switch (director.getnDiff()) {
            case -1 -> System.out.println("N was decreased by 1\n");
            case 0 -> System.out.println("N stays the same\n");
            case 1 -> System.out.println("N was increased by 1\n");
            default ->
                    throw new RuntimeException("n should be either be incremented or decremented by 1, or stay the same");
        }
    }

    private static String getMessages(Block block) {
        if (block.getData().size() == 0) return "no messages";
        return "\n" + block.getData().stream()
                .map(data -> String.format("miner%d sent %d VC to miner%d", data.senderNum(), data.amount(), data.receiverNum()))
                .collect(Collectors.joining("\n"));
    }

    /**
     * unrelated to the project
     */
    private static void assymetricEncryption() throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        KeyGenerator generator = RSASecurityFactory.getInstance().getKeyGenerator();
        KeyPair pair = generator.generate();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();

        byte[] text = "happy birthday".getBytes();
        System.out.println(new String(text));

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedText = cipher.doFinal(text);
        System.out.println(new String(encryptedText));

        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedText = cipher.doFinal(encryptedText);
        System.out.println(new String(decryptedText));
    }
}
