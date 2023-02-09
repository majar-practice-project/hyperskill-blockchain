package blockchain.domain;

import blockchain.domain.transaction.BalanceTracker;
import blockchain.domain.transaction.Transaction;
import blockchain.security.MessageVerifier;
import blockchain.security.RSASecurityFactory;
import blockchain.security.SignedData;
import blockchain.supplier.DataBuffer;
import blockchain.supplier.MessageDataSerializationUtil;
import blockchain.timer.Timer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class BlockChainAppendDirector {
    private final int minerNum;
    private final long LOWER_BOUND = 100;
    private final long UPPER_BOUND = 400;
    private final Timer timer = new Timer();
    private final BlockingQueue<Block> queue;
    private final BlockChain chain;
    private final DataBuffer messages;
    private final MessageVerifier messageVerifier = RSASecurityFactory.getInstance().getMessageVerifier();
    private final BalanceTracker balanceTracker;
    private ExecutorService executor;
    // todo can't find better place to put this
    private long recentDuration = 0;
    private int nDiff = 0;

    public BlockChainAppendDirector(int minerNum, BlockChain chain, DataBuffer messages) {
        this.minerNum = minerNum;
        this.chain = chain;
        this.messages = messages;
        queue = new ArrayBlockingQueue<>(minerNum);
        balanceTracker = new BalanceTracker(minerNum);
    }

    public Block appendNewBlock() {
        timer.start();
        submitTasks();

        Block newBlock = getGeneratedBlock();
        recentDuration = timer.stop();

        adjustProvedPrefix();
        return newBlock;
    }

    private void submitTasks() {
        executor = Executors.newFixedThreadPool(minerNum);
        queue.clear();


        Random RAND = new Random(System.nanoTime());
        long id = chain.getChain().size();
        String prevHashValue = chain.getLastHashValue();
        String provedPrefix = chain.getProvedPrefix();
        List<SignedData> bufferedSignedData = messages.flush();

        long maxId = chain.getMessageId();

        List<Transaction> acceptedData = bufferedSignedData.stream()
                .filter(messageVerifier::verify)
                .map(msg -> MessageDataSerializationUtil.deserialize(msg.data()))
                .filter(data -> data.id() < maxId && balanceTracker.transact(data.senderNum(), data.receiverNum(), data.amount()))
                .toList();

        for (int i = 0; i < minerNum; i++) {
            int minerNumber = i;
            executor.submit(() -> {
                int magicNumber;
                Block newBlock = new Block(id, acceptedData, prevHashValue, minerNumber);

                do {
                    if (Thread.interrupted()) return;
                    magicNumber = RAND.nextInt();
                    newBlock.setMagicNumber(magicNumber);
                } while (!newBlock.getBlockHash().startsWith(provedPrefix));
                queue.add(newBlock);
            });
        }
        executor.shutdown();
    }

    private Block getGeneratedBlock() {
        try {
            Block newBlock;
            do {
                newBlock = queue.take();
            } while (!chain.addNewBlock(newBlock));

            balanceTracker.reward(newBlock.getProducerMinerId());
            executor.shutdownNow();
            return newBlock;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void adjustProvedPrefix() {
        nDiff = 0;
        if (recentDuration < LOWER_BOUND) {
            chain.increaseProvePrefixLength();
            nDiff = 1;
        } else if (recentDuration > UPPER_BOUND) {
            chain.decreaseProvePrefixLength();
            nDiff = -1;
        }
    }

    public long getRecentDuration() {
        return recentDuration;
    }

    public int getnDiff() {
        return nDiff;
    }

    public Supplier<Long> getIdSupplier() {
        return chain::getMessageId;
    }
}
