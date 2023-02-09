package blockchain.domain.transaction;

import java.io.Serializable;

public record Transaction(long id, int senderNum, int receiverNum, int amount) implements Serializable {
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", senderNum=" + senderNum +
                ", receiverNum=" + receiverNum +
                ", amount=" + amount +
                '}';
    }
}
