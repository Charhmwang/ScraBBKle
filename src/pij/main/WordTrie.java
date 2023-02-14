package pij.main;

public class WordTrie {
    public TrieNode root;
    public int num_words = 0;
    public WordTrie() {
        root = new TrieNode(false);
    }

    public Boolean searchWord(String word) {
        if (word == null || word.length() == 0) return false;

        word = word.toUpperCase();
        TrieNode cur = root;
        char[] chars = word.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (cur.children.containsKey(chars[i]))
                cur = cur.children.get(chars[i]);
            else return false;
        }
        // Need to check is there a null: null existing to prove
        // is there a word ends up at this character
        return cur.children.containsKey(null);
    }

    public void insertWord(String word) {
        if (word == null || word.length() == 0) return;

        char[] chars = word.toCharArray();
        TrieNode cur = root;
        for (int i = 0; i < chars.length; i++) {
            if (!cur.children.containsKey(chars[i]))
                cur.children.put(chars[i], new TrieNode(false));
            cur = cur.children.get(chars[i]);
        }
        if (cur.children.isEmpty()) cur.isLeaf = true; // If this is no more word continue from this letters sequence
        else cur.children.put(null, null); // If there are more words continue from this character existing
    }
}
