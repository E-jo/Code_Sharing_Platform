package blockchain;

import blockchain.Blockchain;
import blockchain.Serialization;
import blockchain.solvents.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * The class represents a real world with people, shops, etc.
 */
public class World implements Serializable {
    private static final long serialVersionUID = Serialization.WORLD;
    public final List<Solvent> solvents = new ArrayList<>();
    public final List<Miner> miners = new ArrayList<>();
    public final List<Person> people = new ArrayList<>();
    public final List<Shop> shops = new ArrayList<>();
    public final List<Worker> workers = new ArrayList<>();
    private final Random random;

    public enum Identify {
        ALL,
        PEOPLE_AND_SHOPS
    }

    public World(Blockchain blockchain) {
        random = new Random();
        for (int i = 1; i <= 10; i++) {
            miners.add(new Miner(blockchain, "miner" + i));
        }
        String[] peopleNames = new String[] {"Nick", "Bob", "Alice"};
        for (String name : peopleNames) {
            people.add(new Person(blockchain, name));
        }
        String[] shopsNames = new String[] {"GamingShop", "BeautyShop", "CarShop", "FastFood", "ShoesShop"};
        for (String name : shopsNames) {
            List<Worker> shopWorkers = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                shopWorkers.add(new Worker(blockchain, name + "_Worker" + i, 10L));
            }
            shopWorkers.add(new Worker(blockchain, name + "_Director", 30L));
            shops.add(new Shop(blockchain, name, shopWorkers.toArray(new Worker[0])));
            workers.addAll(shopWorkers);
        }
        solvents.addAll(miners);
        solvents.addAll(people);
        solvents.addAll(shops);
        solvents.addAll(workers);
    }

    /**
     * Get random solvent excluding the given one
     *
     * @param  exception The solvent to be exclude
     * @param  option    The special identify that shows from which collections we need to return a value
     * @return           The random solvent
     * @see    Solvent
     */
    public Solvent randomChoice(Solvent exception, Identify option) {
        while (true) {
            List<Solvent> list;
            switch (option) {
                case PEOPLE_AND_SHOPS: {
                    list = people.stream().map(el -> (Solvent) el).collect(Collectors.toList());
                    list.addAll(
                            shops.stream().map(el -> (Solvent) el).collect(Collectors.toList())
                    );
                } break;
                default:
                    list = solvents;
                    break;
            }
            Solvent solvent = list.get(random.nextInt(list.size()));
            if (!solvent.equals(exception))
                return solvent;
        }
    }
}