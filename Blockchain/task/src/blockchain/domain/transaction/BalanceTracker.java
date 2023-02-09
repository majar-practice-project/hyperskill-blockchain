package blockchain.domain.transaction;

import java.util.Arrays;

public class BalanceTracker {
    private static final int REWARDING_COINS = 100;
    private static final int STARTING_COINS = 100;
    private final int[] minersBalance;
    public BalanceTracker(int minerTotalNum) {
        minersBalance = new int[minerTotalNum];
        Arrays.fill(minersBalance, STARTING_COINS);
    }

    public void reward(int minerNum) {
        minersBalance[minerNum] += REWARDING_COINS;
    }

    public boolean transact(int senderNum, int receiverNum, int amount) {
        if(minersBalance[senderNum] < amount) return false;

        minersBalance[senderNum] -= amount;
        minersBalance[receiverNum] += amount;
        return true;
    }
}
