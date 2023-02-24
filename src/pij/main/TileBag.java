package pij.main;

import java.util.ArrayList;
import java.util.Random;

public class TileBag {
    public ArrayList<Tile> tilesInBag;

    public TileBag() {
        tilesInBag = new ArrayList<>();
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


    public void addTiles(char letter, int points ,int amount) {
        for (int i = 0 ; i < amount ; i++)
            tilesInBag.add(new Tile(letter, points));
    }


    public Tile takeOutTile(){
        if (isEmpty()) return null;
        Random random = new Random();
        int idx = random.nextInt(tilesInBag.size());
        Tile tile = tilesInBag.get(idx);
        tilesInBag.remove(idx);
        return tile;
    }


    public boolean isEmpty() { return tilesInBag.size() == 0; }
}
