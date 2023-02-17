package blockchain.solvents;

import blockchain.Blockchain;
import blockchain.Serialization;
import blockchain.Transaction;

import java.io.Serializable;

/**
 * The class define objects that are able to pay
 */
public class Solvent implements Serializable {
    private static final long serialVersionUID = Serialization.SOLVENT;
    private final Blockchain blockchain;
    private final String name;
    private long money;

    public Solvent(Blockchain blockchain, String name) {
        this.blockchain = blockchain;
        this.name = name;
        money = 100L;
    }

    /**
     * Send money to other solvent if the balance has enough coins
     *
     * @param receiver The solvent who will receive the money
     * @param amount   The amount of money
     */
    public void sendMoney(Solvent receiver, long amount) {
        if (money - amount >= 0) {
            blockchain.addTransaction(new Transaction(this, receiver, amount, blockchain.getAndIncreaseTransactionCounter()));
            receiver.increaseMoney(amount);
            money -= amount;
        }
    }

    public void increaseMoney(long amount) {
        money += amount;
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // getters
    public Blockchain getBlockchain() {
        return blockchain;
    }

    public String getName() {
        return name;
    }

    public long getMoney() {
        return money;
    }
}