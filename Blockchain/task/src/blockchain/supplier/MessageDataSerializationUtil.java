package blockchain.supplier;

import blockchain.domain.transaction.Transaction;

import java.io.*;

public class MessageDataSerializationUtil {
    public static byte[] serialize(Transaction data) {
        try (ByteArrayOutputStream bas = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bas)) {
            oos.writeObject(data);
            oos.flush();
            return bas.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Transaction deserialize(byte[] bytes) {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (Transaction) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
