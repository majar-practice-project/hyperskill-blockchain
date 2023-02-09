package blockchain.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BlockChain {
    private static final String DEFAULT_BLOCK_HASH = String.valueOf(0);
    private List<Block> chain = new ArrayList<>();
    private int provedPrefixLength = 0;
    private long nextMessageId = 0;
    private long maxExpiredId = -1;

    private String provedPrefix = String.valueOf(0).repeat(provedPrefixLength);

    public synchronized boolean addNewBlock(Block newBlock) {
        if (!validateBlockChain(newBlock)) return false;

        chain.add(newBlock);
        maxExpiredId = newBlock.getMaxMessageId().orElse(maxExpiredId);
        return true;
    }

    private boolean validateBlockChain(Block newBlock) {
        if (!newBlock.getBlockHash().startsWith(provedPrefix)) return false;

        if (chain.isEmpty()) {
            return Objects.equals(newBlock.getPrevBlockHash(), DEFAULT_BLOCK_HASH)
                    && newBlock.getMinMessageId().orElse(0)>maxExpiredId;
        }

        Block prevBlock = chain.get(chain.size() - 1);
        return newBlock.getPrevBlockHash().equals(prevBlock.getBlockHash())
                && newBlock.getMinMessageId().orElse(maxExpiredId+1)>maxExpiredId;
    }

    public List<Block> getChain() {
        return Collections.unmodifiableList(chain);
    }

    public String getLastHashValue() {
        return chain.isEmpty() ? DEFAULT_BLOCK_HASH : chain.get(chain.size() - 1).getBlockHash();
    }

    public String getProvedPrefix() {
        return provedPrefix;
    }

    public void increaseProvePrefixLength(){
        assert  provedPrefixLength < 60;
        provedPrefixLength++;
        provedPrefix = String.valueOf(0).repeat(provedPrefixLength);
    }

    public void decreaseProvePrefixLength(){
        assert  provedPrefixLength > 0;
        provedPrefixLength--;
        provedPrefix = String.valueOf(0).repeat(provedPrefixLength);
    }

    public synchronized long getMessageId(){
        return nextMessageId++;
    }
}
