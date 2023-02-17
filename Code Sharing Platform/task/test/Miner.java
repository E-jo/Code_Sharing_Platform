package blockchain.solvents;

import blockchain.Block;
import blockchain.Blockchain;

import java.util.Date;

public class Miner extends Solvent {

    public Miner(Blockchain blockchain, String name) {
        super(blockchain, name);
    }

    public void createBlock() {
        long start = new Date().getTime();
        String hash = getBlockchain().getLastBlock() == null ? null : getBlockchain().getLastBlock().getHash();
        Block block = new Block(getBlockchain().getCounter(), hash, getBlockchain().getZerosNumber());
        long end = new Date().getTime();
        getBlockchain().addBlock(block, start, end, this);
    }
}