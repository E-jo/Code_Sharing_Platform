package blockchain;

import blockchain.solvents.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // Settings
        final int MINER_SEND_MONEY_CHANCE = 10;
        final int MINER_SEND_MONEY_MAX = 100;
        final long PERSON_SLEEP = 1000L;
        final long SHOP_SLEEP = 3000L;
        final int PERSON_SEND_MONEY_MAX = 100;
        final int WAIT_TERMINATION_SEC = 15;

        // Create keys if they are not exist
        try {
            File file1 = new File(PathsConstants.PUBLIC_KEY);
            File file2 = new File(PathsConstants.PRIVATE_KEY);
            if (!(file1.exists() && file2.exists())) {
                GenerateKeys gk = new GenerateKeys(1024);
                gk.createKeys();
                gk.writeToFile(PathsConstants.PUBLIC_KEY, gk.getPublicKey().getEncoded());
                gk.writeToFile(PathsConstants.PRIVATE_KEY, gk.getPrivateKey().getEncoded());
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            System.err.println(e.getMessage());
        }

        // Create blockchain
        final Blockchain blockchain = Blockchain.getInstance();

        // Retrieve the world
        World world = blockchain.getWorld();

        // Generate random
        final Random random = new Random();

        // Blockchain loading
        ExecutorService executor = Executors.newFixedThreadPool(world.solvents.size() - world.workers.size());
        for (Miner miner : world.miners) {
            executor.submit(() -> {
                try {
                    while (blockchain.continueWorking()) {
                        miner.createBlock();
                        if (random.nextInt(MINER_SEND_MONEY_CHANCE) == 0) {
                            miner.sendMoney(world.randomChoice(miner, World.Identify.ALL), random.nextInt(MINER_SEND_MONEY_MAX + 1));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        for (Person person : world.people) {
            executor.submit(() -> {
                try {
                    while (blockchain.continueWorking()) {
                        person.sleep(PERSON_SLEEP);
                        person.sendMoney(world.randomChoice(person, World.Identify.PEOPLE_AND_SHOPS), random.nextInt(PERSON_SEND_MONEY_MAX + 1));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        for (Shop shop : world.shops) {
            executor.submit(() -> {
                try {
                    while (blockchain.continueWorking()) {
                        shop.sleep(SHOP_SLEEP);
                        for (Worker worker : shop.getWorkers()) {
                            shop.sendMoney(worker, worker.getSalary());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // Stop executor and wait for termination
        executor.shutdown();
        try {
            executor.awaitTermination(WAIT_TERMINATION_SEC, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}