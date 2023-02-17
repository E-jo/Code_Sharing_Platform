package blockchain;

import blockchain.solvents.Miner;

import java.io.*;
import java.util.*;

/**
 * The class represents a blockchain with blocks that has transactions
 *
 * @see Block
 * @see Transaction
 * @see World
 */
public class Blockchain implements Serializable {
    private static final long serialVersionUID = Serialization.BLOCKCHAIN;
    private final World world;
    private long counter;
    private final List<Block> blocks;
    private byte zerosNumber;
    private int sessionBlocks;
    private final List<Transaction> transactions;
    private long transactionCounter;

    private Blockchain() {
        world = new World(this);
        counter = 1;
        transactionCounter = 0;
        sessionBlocks = 0;
        blocks = new ArrayList<>();
        transactions = new ArrayList<>();
        zerosNumber = 0;
    }

    public synchronized void addBlock(Block block, long start, long end, Miner miner) {
        blocks.add(block);
        if (isValid()) {
            block.setTransactions(transactions.toArray(new Transaction[0]));
            transactions.clear();
            counter++;
            long milliseconds = end - start;
            long seconds = milliseconds / 1000;
            sessionBlocks++;
            System.out.println("Block:");
            System.out.println("Created by: " + miner.getName());
            miner.increaseMoney(100L);
            System.out.println(miner.getName() + " gets 100 VC");
            System.out.println(block);
            System.out.print("Block data: ");
            if (block.getTransactions().length > 0) {
                System.out.println();
                Arrays.stream(block.getTransactions()).forEach(
                        transaction -> System.out.println(
                                transaction.getSender().getName() + " sent " + transaction.getAmount() + " VC to " + transaction.getReceiver().getName()
                                )
                );
            } else {
                System.out.println("No transactions");
            }
            System.out.printf("Block was generating for %d seconds\n", seconds);
            if (milliseconds < 10 && zerosNumber < 5) {
                zerosNumber++;
                System.out.println("N was increased to " + zerosNumber);
            } else if (milliseconds < 50 || zerosNumber == 0) {
                System.out.println("N stays the same");
            } else {
                zerosNumber--;
                System.out.println("N was decreased by 1");
            }
            System.out.println();
            // Write to file
            try {
                SerializationUtils.serialize(this, PathsConstants.BLOCKCHAIN_INFO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            blocks.remove(block);
        }
    }

    public static Blockchain getInstance() {
        try {
            Blockchain blockchain = (Blockchain) SerializationUtils.deserialize(PathsConstants.BLOCKCHAIN_INFO);
            blockchain.setSessionBlocks(0);
            return blockchain;
        } catch (ClassNotFoundException | IOException e) {
            return new Blockchain();
        }
    }

    public synchronized void addTransaction(Transaction transaction) {
        long id = transaction.getId();
        for (Transaction t : transactions) {
            if (t.getId() > id) {
                return;
            }
        }
        for (Transaction t : getLastBlock().getTransactions()) {
            if (t.getId() > id) {
                return;
            }
        }
        if (Cryptography.verifySignature(transaction.getHashString().getBytes(), transaction.getSignature(), transaction.getPublicKey()))
            transactions.add(transaction);
    }

    public boolean isValid() {
        try {
            if (blocks.size() > 1) {
                HashSet<Long> ids = new HashSet<>();
                ids.add(blocks.get(0).getId());
                for (int i = 1; i < blocks.size(); i++) {
                    if (!blocks.get(i).getHashPrevBlock().equals(blocks.get(i - 1).getHash())) {
                        return false;
                    }
                    long id = blocks.get(i).getId();
                    if (ids.contains(id)) {
                        return false;
                    }
                    ids.add(id);
                }
                List<Long> list = new ArrayList<>();
                list.add(-1L);
                for (Block block : blocks) {
                    if (block.getTransactions() == null)
                        continue;
                    for (Transaction transaction : block.getTransactions()) {
                        if (transaction.getId() > list.get(list.size() - 1)) {
                            list.add(transaction.getId());
                        } else {
                            return false;
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // getters
    public Block getLastBlock() {
        return blocks.size() > 0 ? blocks.get(blocks.size() - 1) : null;
    }

    public synchronized boolean continueWorking() {
        return sessionBlocks < 15;
    }

    public long getCounter() {
        return counter;
    }

    public byte getZerosNumber() {
        return zerosNumber;
    }

    public long getAndIncreaseTransactionCounter() {
        return ++transactionCounter;
    }

    public World getWorld() {
        return world;
    }

    // setters
    public void setSessionBlocks(int sessionBlocks) {
        this.sessionBlocks = sessionBlocks;
    }
}