package blockchain.domain.transaction;

import java.util.Random;

public class RandomTransactionGenerator {
    private final int minerTotalNum;
    private final Random rand = new Random(System.nanoTime());

    public RandomTransactionGenerator(int minerTotalNum) {
        this.minerTotalNum = minerTotalNum;
    }

    public Transaction generate(long id) {
        int miner1 = rand.nextInt(minerTotalNum);
        int miner2 = (miner1 + rand.nextInt(minerTotalNum - 1) + 1) % minerTotalNum;
        int amount = rand.nextInt(50) + 1;
        return new Transaction(id, miner1, miner2, amount);
    }
}
