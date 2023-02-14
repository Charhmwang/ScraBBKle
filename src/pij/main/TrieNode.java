package pij.main;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    public Character c;
    public Map<Character, TrieNode> children = new HashMap<>();
    public Boolean isLeaf = false;
    private Integer wordsVal;

    public TrieNode(Boolean isLeaf) { this.isLeaf = isLeaf; }
    public TrieNode(Character c) { this.c = c; }
}
