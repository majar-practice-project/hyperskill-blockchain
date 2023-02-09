package blockchain.supplier;

import blockchain.domain.transaction.RandomTransactionGenerator;
import blockchain.domain.transaction.Transaction;
import blockchain.security.DataSigner;
import blockchain.security.RSASecurityFactory;
import blockchain.security.SecurityAbstractFactory;
import blockchain.security.SignedData;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class TransactionSupplier {
    private final DataBuffer messages;
    private ScheduledExecutorService executor;
    private boolean running = false;
    private SupplierWorker supplierWorker;
    public TransactionSupplier(DataBuffer messages, Supplier<Long> idSupplier, RandomTransactionGenerator transactionGenerator){
        this.messages = messages;
        this.supplierWorker = new SupplierWorker(idSupplier, transactionGenerator);
    }

    public void start() {
        if(running) throw new RuntimeException("Starting supplier before stopping the old one");
        running = true;
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(supplierWorker, 0, 200, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if(executor==null) throw new RuntimeException("stopping a supplier before starting");
        executor.shutdownNow();
        running = false;
    }

    class SupplierWorker implements Runnable {
        private final SecurityAbstractFactory factory = RSASecurityFactory.getInstance();
        private final DataSigner dataSigner = factory.getDataSigner();
        private final Supplier<Long> idSupplier;
        private final RandomTransactionGenerator transactionGenerator;
        private final PublicKey publicKey;
        private final PrivateKey privateKey;

        {
            KeyPair keyPair = factory.getKeyGenerator().generate();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
        }

        public SupplierWorker(Supplier<Long> idSupplier, RandomTransactionGenerator transactionGenerator) {
            this.idSupplier = idSupplier;
            this.transactionGenerator = transactionGenerator;
        }


        @Override
        public void run() {
            Long id = idSupplier.get();
            Transaction transaction = transactionGenerator.generate(id);
            byte[] serializedData = MessageDataSerializationUtil.serialize(transaction);

            SignedData msg = new SignedData(serializedData, dataSigner.sign(serializedData, privateKey), publicKey);
            messages.addMessage(msg);
        }
    }
}
