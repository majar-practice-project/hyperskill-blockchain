package blockchain.supplier;

import blockchain.security.SignedData;

import java.util.ArrayList;
import java.util.List;

public class DataBuffer {
    private final List<SignedData> buffer = new ArrayList<>();
    public List<SignedData> flush() {
        synchronized (buffer) {
            List<SignedData> list = List.copyOf(buffer);
            buffer.clear();
            return list;
        }
    }

    public void addMessage(SignedData signedData) {
        synchronized (buffer) {
            buffer.add(signedData);
        }
    }
}