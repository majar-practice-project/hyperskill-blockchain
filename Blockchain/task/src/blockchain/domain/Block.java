package blockchain.domain;

import blockchain.domain.transaction.Transaction;
import blockchain.hash.StringUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.OptionalLong;

public class Block {
    private final long id;
    private final long timestamp = new Date().getTime();
    private final String prevBlockHash;
    private final List<Transaction> transactions;
    private final int producerMinerId;
    private int magicNumber;
    private String blockHash;

    public Block(long id, List<Transaction> transactions, String prevBlockHash, int producerMinerId) {
        this.id = id;
        this.transactions = transactions;
        this.prevBlockHash = prevBlockHash;
        this.blockHash = blockHash();
        this.producerMinerId = producerMinerId;
    }

    private String blockHash() {
        return StringUtil.applySha256(""
                + magicNumber
                + transactions.toString()
                + prevBlockHash
                + producerMinerId
                + id
                + timestamp);
    }

    public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<Transaction> getData() {
        return Collections.unmodifiableList(transactions);
    }

    public String getBlockHash() {
        return blockHash;
    }

    public String getPrevBlockHash() {
        return prevBlockHash;
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
        this.blockHash = blockHash();
    }

    public int getProducerMinerId() {
        return producerMinerId;
    }

    OptionalLong getMinMessageId() {
        return transactions.stream()
                .mapToLong(Transaction::id)
                .min();
    }

    OptionalLong getMaxMessageId() {
        return transactions.stream()
                .mapToLong(Transaction::id)
                .max();
    }
}