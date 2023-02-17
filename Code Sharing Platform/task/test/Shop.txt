package blockchain.solvents;

import blockchain.Blockchain;

public class Shop extends Solvent {
    private final Worker[] workers;

    public Shop(Blockchain blockchain, String name, Worker[] workers) {
        super(blockchain, name);
        this.workers = workers;
    }

    public Worker[] getWorkers() {
        return workers;
    }
}