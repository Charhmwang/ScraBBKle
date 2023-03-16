package pij.main;

import java.util.ArrayList;
import java.util.Random;

/**
 * Initialize the game's tile bag. A TileBag has a list of Tile objects.
 * Object of this class is Immutable: After TileBag has been created,
 * one cannot change the value of its attribute.
 *
 * @author Haomeng Wang
 * @version 1.0
 */
public class TileBag {

    /** The tiles in this TileBag. A list of Tile objects. Always non-null after object creation. */
    private static final ArrayList<Tile> tilesInBag = new ArrayList<>();

    /** TileBag instance, private to be hidden from outside the TileBag class. */
    private final static TileBag instance = new TileBag();


    /**
     * For other classes getting the TileBag instance.
     *
     * @return the TileBag instance
     */
    public static TileBag getInstance() { return instance; }


    /**
     * Constructs a TileBag instance.
     * Private constructor ensures instance can only be initiated inside the class.
     */
    public TileBag() {
        addTiles('A',1,9);
        addTiles('B',3,2);
        addTiles('C',3,2);
        addTiles('D',2,4);
        addTiles('E',1,12);
        addTiles('F',4,2);
        addTiles('G',2,3);
        addTiles('H',4,2);
        addTiles('I',1,9);
        addTiles('J',8,1);
        addTiles('K',5,1);
        addTiles('L',1,4);
        addTiles('M',3,2);
        addTiles('N',1,6);
        addTiles('O',1,8);
        addTiles('P',3,2);
        addTiles('Q',10,1);
        addTiles('R',1,6);
        addTiles('S',1,4);
        addTiles('T',1,6);
        addTiles('U',1,4);
        addTiles('V',4,2);
        addTiles('W',4,2);
        addTiles('X',8,1);
        addTiles('Y',4,2);
        addTiles('Z',10,1);
        addTiles('?', 3, 9);
    }


    /**
     * Adds tiles into this TileBag.
     *
     * @param letter Tile letter
     * @param points points of the letter
     * @param amount amount of the adding tile
     */
    public void addTiles(char letter, int points ,int amount) {
        for (int i = 0 ; i < amount ; i++)
            tilesInBag.add(new Tile(letter, points));
    }


    /**
     * Returns a targeting tile to be taken out from this TileBag, can be null if not found.
     *
     * @return a targeting tile to be taken out from this TileBag or null
     */
    public static Tile takeOutTile(){
        if (isEmpty()) return null;
        Random random = new Random();
        int idx = random.nextInt(tilesInBag.size());
        Tile tile = tilesInBag.get(idx);
        tilesInBag.remove(idx);
        return tile;
    }


    /**
     * Returns a boolean result whether this TileBag is currently empty.
     *
     * @return a boolean result whether this TileBag is currently empty
     */
    public static boolean isEmpty() { return tilesInBag.size() == 0; }
}
