package blockchain;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

public class Block implements Serializable {
    private static final long serialVersionUID = Serialization.BLOCK;
    private final long id;
    private final long timestamp;
    private final String hashPrevBlock;
    private String hash;
    private int magic;
    private Transaction[] transactions;

    public Block(long id, String hashPrevBlock, byte zerosNumber) {
        this.id = id;
        this.hashPrevBlock = hashPrevBlock;
        timestamp = new Date().getTime();
        String hashWithoutMagic = String.valueOf(id) + (hashPrevBlock == null ? 0 : hashPrevBlock);
        String zeros = "0".repeat(zerosNumber);
        Random random = new Random();
        // Mining process
        do {
            magic = random.nextInt();
            hash = StringUtil.applySha256(hashWithoutMagic + magic);
        } while (!hash.startsWith(zeros));
    }

    @Override
    public String toString() {
        return "Id: " + id + "\n" +
                "Timestamp: " + timestamp + "\n" +
                "Magic number: " + magic + "\n" +
                "Hash of the previous block:\n" +
                (hashPrevBlock == null ? 0 : hashPrevBlock) + "\n" +
                "Hash of the block:\n" +
                hash;
    }

    // getters
    public long getId() {
        return id;
    }

    public String getHashPrevBlock() {
        return hashPrevBlock;
    }

    public String getHash() {
        return hash;
    }

    public Transaction[] getTransactions() {
        return transactions;
    }

    // setters
    public void setTransactions(Transaction[] transactions) {
        this.transactions = transactions;
    }
}