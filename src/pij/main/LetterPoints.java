package pij.main;

import java.util.HashMap;
import java.util.Map;

/**
 * LetterPoints class is a letter points reference for the game,
 * has a map attribute for recording. Singleton class.
 * Object of this class is Immutable: After LetterPoints has been created,
 * one cannot change the value of its attribute.
 *
 * @author Haomeng Wang
 * @version 1.0
 */
public class LetterPoints {

    /** The map recording letters and the corresponding points.
     * Always non-null after object creation */
    private static final Map<Character, Integer> letterMap = new HashMap<>();

    /** LetterPoints instance, set as null initially.
     * Private to be hidden from outside the LetterPoints class.
     */
    private static LetterPoints letterPointsInstance;


    /**
     * For other classes getting the LetterPoints instance.
     * If the instance has never been created, initiate one then return.
     * If the instance has already been initiated, then return the created one.
     * Ensures the LetterPoints instance can be created once only in the program.
     *
     * @return the sole LetterPoints instance
     */
    public synchronized static LetterPoints getInstance() {
        if (letterPointsInstance == null) {
            letterPointsInstance = new LetterPoints();
        }
        return letterPointsInstance;
    }


    /**
     * Constructs a LetterPoints instance.
     * Private constructor ensures instance can only be initiated inside the class.
     */
    private LetterPoints() {
        letterMap.put('A', 1);
        letterMap.put('B', 3);
        letterMap.put('C', 3);
        letterMap.put('D', 2);
        letterMap.put('E', 1);
        letterMap.put('F', 4);
        letterMap.put('G', 2);
        letterMap.put('H', 4);
        letterMap.put('I', 1);
        letterMap.put('J', 8);
        letterMap.put('K', 5);
        letterMap.put('L', 1);
        letterMap.put('M', 3);
        letterMap.put('N', 1);
        letterMap.put('O', 1);
        letterMap.put('P', 3);
        letterMap.put('Q', 10);
        letterMap.put('R', 1);
        letterMap.put('S', 1);
        letterMap.put('T', 1);
        letterMap.put('U', 1);
        letterMap.put('V', 4);
        letterMap.put('W', 4);
        letterMap.put('X', 8);
        letterMap.put('Y', 4);
        letterMap.put('Z', 10);
        letterMap.put('?', 3);
    }


    /**
     * Returns the map recording letters and the corresponding points.
     *
     * @return the map recording letters and the corresponding points
     */
    public static Map<Character, Integer> getMap() { return letterMap; }
}
