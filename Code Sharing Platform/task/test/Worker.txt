package blockchain.solvents;

import blockchain.Blockchain;

public class Worker extends Solvent {
    private final long salary;

    public Worker(Blockchain blockchain, String name, long salary) {
        super(blockchain, name);
        this.salary = salary;
    }

    public long getSalary() {
        return salary;
    }
}