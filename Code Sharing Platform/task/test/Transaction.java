package blockchain;

import blockchain.solvents.Solvent;

import java.io.Serializable;
import java.security.PublicKey;

/**
 * The class represents a transaction that has a sender, a receiver and an amount in coins.
 * Transaction has an id and a signature with a public key to check whether it is valid.
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = Serialization.TRANSACTION;
    private final Solvent sender;
    private final Solvent receiver;
    private final long amount;
    private final String hashString;
    private final byte[] signature;
    private final PublicKey publicKey;
    private long id;

    public Transaction(Solvent sender, Solvent receiver, long amount, long id) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.id = id;
        hashString = sender.getName() + amount + receiver.getName();
        signature = Cryptography.sign(hashString, PathsConstants.PRIVATE_KEY);
        publicKey = Cryptography.getPublic(PathsConstants.PUBLIC_KEY);
    }

    // getters
    public Solvent getSender() {
        return sender;
    }

    public Solvent getReceiver() {
        return receiver;
    }

    public long getAmount() {
        return amount;
    }

    public String getHashString() {
        return hashString;
    }

    public byte[] getSignature() {
        return signature;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public long getId() {
        return id;
    }

    // setters
    public void setId(long id) {
        this.id = id;
    }
}