import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;


/******************************************************************************
 *  Name: Rhett Zhang
 *  Date: 2020/01/12
 *
 *  Compilation:  javac-algs4 BoggleSolver.java
 *  Execution:    java-algs4 BoggleSolver
 *  Dependencies: StdOut.java
 *
 *  Boggle is a word game designed by Allan Turoff and distributed by Hasbro.
 *  It involves a board made up of 16 cubic dice, where each die has a letter
 *  printed on each of its 6 sides. At the beginning of the game,
 *  the 16 dice are shaken and randomly distributed into a 4-by-4 tray,
 *  with only the top sides of the dice visible.
 *
 *  % java-algs4 BoggleSolver
 *
 ******************************************************************************/

public class BoggleSolver {
    // private final TST<Integer> st;      // dictionary represented in TST
    private final TrieST26<Integer> st;      // dictionary represented in 26-way trie
    private static final int R = 26;        // 26 capital letter from A to Z

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        // st = new TST<Integer>();
        st = new TrieST26<>();
        for (int i = 0; i < dictionary.length; i++)
            st.put(dictionary[i], i);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        int rows = board.rows();
        int cols = board.cols();

        boolean[] marked = new boolean[rows * cols];
        SET<String> set = new SET<String>();            // store the valid words

        int curRow, curCol;     // row and col for current dice

        // to be able to iterate over each adjacency list, keeping track of which
        // vertex in each adjacency list needs to be explored next
        Iterator<Integer>[] adj = (Iterator<Integer>[]) new Iterator[rows * cols];
        Bag<Integer>[] adjBoard = adjDices(board);  // adjacent indices list
        for (int v = 0; v < rows * cols; v++)
            adj[v] = adjBoard[v].iterator();

        // non-recurisve depth-first search using an explicit stack
        Stack<Integer> stack = new Stack<Integer>();

        int s;                  // source dice index
        int t;                  // popped dice index
        char curChar;           // current character
        String curStr;          // current string built from current character
        boolean isQ;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                StringBuilder sb = new StringBuilder();
                curChar = board.getLetter(i, j);

                // handle the special character 'Q'
                if (curChar == 'Q') {
                    curStr = "QU";
                    isQ = true;
                }
                else {
                    curStr = "" + curChar;
                    isQ = false;
                }

                sb.append(curStr);

                s = i * cols + j;
                marked[s] = true;
                stack.push(s);

                Node startNode = null;
                Node curNode = null;

                while (!stack.isEmpty()) {

                    int v = stack.peek();
                    if (adj[v].hasNext()) {

                        int w = adj[v].next();
                        if (!marked[w]) {

                            // convert index back to row and col number
                            curRow = w / cols;
                            curCol = w - curRow * cols;
                            curChar = board.getLetter(curRow, curCol);

                            // handle the special character 'Q'
                            if (curChar == 'Q') {
                                curStr = "QU";
                                isQ = true;
                            }
                            else {
                                curStr = "" + curChar;
                                isQ = false;
                            }

                            // critical backtracking optimization:
                            // when the current path corresponds to a string
                            // that is not a prefix of any word in the dictionary,
                            // there is no need to expand the path further.

                            // Queue<String> queueST = (Queue<String>) st
                            //         .keysWithPrefix(sb.toString() + curStr);
                            // if (!queueST.isEmpty()) {

                            // x = st.hasKeysWithPrefix(sb.toString() + curStr);

                            curNode = st.getPrefixNodeFrom(sb.toString() + curStr, startNode, isQ);

                            if (curNode != null) {
                                // critical fix: put the marked true here, not before
                                marked[w] = true;
                                stack.push(w);
                                sb.append(curStr);

                                if (sb.length() >= 3 && st.contains(sb.toString()))
                                    set.add(sb.toString());

                                startNode = curNode;
                            }
                        }
                    }
                    else {

                        startNode = null;

                        t = stack.pop();
                        marked[t] = false;

                        // re-obtain the iterator. This is a critical step.
                        adj[t] = adjBoard[t].iterator();

                        int tRow = t / cols;
                        int tCol = t - tRow * cols;

                        // handle the special character 'Q'
                        if (board.getLetter(tRow, tCol) == 'Q')
                            sb.delete(sb.length() - 2, sb.length());
                        else
                            sb.deleteCharAt(sb.length() - 1);
                    }
                }

                // done with current dice, and re-initialize the marked array
                for (int k = 0; k < marked.length; k++)
                    marked[k] = false;

                // re-initialize the Iterator array after one pass
                for (int v = 0; v < rows * cols; v++)
                    adj[v] = adjBoard[v].iterator();
            }
        }

        return set;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!st.contains(word))
            return 0;

        switch (word.length()) {
            case 1:
            case 2:
                return 0;
            case 3:
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            default:
                return 11;
        }
    }

    // Precompute the Boggle graph, i.e., the set of cubes adjacent to each cube.
    // But don't necessarily use a heavyweight Graph object
    private Bag<Integer>[] adjDices(BoggleBoard board) {
        int rows = board.rows();
        int cols = board.cols();

        // adjacent indices list
        Bag<Integer>[] adj = (Bag<Integer>[]) new Bag[rows * cols];
        for (int v = 0; v < rows * cols; v++)
            adj[v] = new Bag<Integer>();

        int index, adjIndex;    // convert 2D (row, col) index to 1D index

        // compute the indices of adjacent cubes in eight directions
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                index = i * cols + j;

                // north
                if (validate(board, i - 1, j)) {
                    adjIndex = (i - 1) * cols + j;
                    adj[index].add(adjIndex);
                }

                // northeast
                if (validate(board, i - 1, j + 1)) {
                    adjIndex = (i - 1) * cols + (j + 1);
                    adj[index].add(adjIndex);
                }

                // east
                if (validate(board, i, j + 1)) {
                    adjIndex = i * cols + (j + 1);
                    adj[index].add(adjIndex);
                }

                // southeast
                if (validate(board, i + 1, j + 1)) {
                    adjIndex = (i + 1) * cols + (j + 1);
                    adj[index].add(adjIndex);
                }

                // south
                if (validate(board, i + 1, j)) {
                    adjIndex = (i + 1) * cols + j;
                    adj[index].add(adjIndex);
                }

                // southwest
                if (validate(board, i + 1, j - 1)) {
                    adjIndex = (i + 1) * cols + (j - 1);
                    adj[index].add(adjIndex);
                }

                // west
                if (validate(board, i, j - 1)) {
                    adjIndex = i * cols + (j - 1);
                    adj[index].add(adjIndex);
                }

                // northwest
                if (validate(board, i - 1, j - 1)) {
                    adjIndex = (i - 1) * cols + (j - 1);
                    adj[index].add(adjIndex);
                }
            }
        }

        return adj;
    }

    // check whether board position is valid
    private boolean validate(BoggleBoard board, int i, int j) {
        return (i >= 0 && i < board.rows() && j >= 0 && j < board.cols());
    }

    // R-way trie node
    private static class Node {
        private Object val;
        private Node[] next = new Node[R];
    }

    private class TrieST26<Value> {
        private Node root;      // root of trie
        private int n;          // number of keys in trie

        /**
         * Initializes an empty string symbol table.
         */
        public TrieST26() {
        }

        /**
         * Returns the value associated with the given key.
         *
         * @param key the key
         * @return the value associated with the given key if the key is in the symbol table and
         * {@code null} if the key is not in the symbol table
         * @throws IllegalArgumentException if {@code key} is {@code null}
         */
        public Value get(String key) {
            if (key == null) throw new IllegalArgumentException("argument to get() is null");
            Node x = get(root, key, 0);
            if (x == null) return null;
            return (Value) x.val;
        }

        /**
         * Does this symbol table contain the given key?
         *
         * @param key the key
         * @return {@code true} if this symbol table contains {@code key} and {@code false}
         * otherwise
         * @throws IllegalArgumentException if {@code key} is {@code null}
         */
        public boolean contains(String key) {
            if (key == null) throw new IllegalArgumentException("argument to contains() is null");
            return get(key) != null;
        }

        private Node get(Node x, String key, int d) {
            if (x == null) return null;
            if (d == key.length()) return x;
            char c = key.charAt(d);
            return get(x.next[c - 'A'], key, d + 1);
        }

        /**
         * Inserts the key-value pair into the symbol table, overwriting the old value with the new
         * value if the key is already in the symbol table. If the value is {@code null}, this
         * effectively deletes the key from the symbol table.
         *
         * @param key the key
         * @param val the value
         * @throws IllegalArgumentException if {@code key} is {@code null}
         */
        public void put(String key, Value val) {
            if (key == null) throw new IllegalArgumentException("first argument to put() is null");
            if (val == null) delete(key);
            else root = put(root, key, val, 0);
        }

        private Node put(Node x, String key, Value val, int d) {
            if (x == null) x = new Node();
            if (d == key.length()) {
                if (x.val == null) n++;
                x.val = val;
                return x;
            }
            char c = key.charAt(d);
            x.next[c - 'A'] = put(x.next[c - 'A'], key, val, d + 1);
            return x;
        }

        /**
         * Returns the number of key-value pairs in this symbol table.
         *
         * @return the number of key-value pairs in this symbol table
         */
        public int size() {
            return n;
        }

        /**
         * Is this symbol table empty?
         *
         * @return {@code true} if this symbol table is empty and {@code false} otherwise
         */
        public boolean isEmpty() {
            return size() == 0;
        }

        public Node hasKeysWithPrefix(String prefix) {
            return get(root, prefix, 0);
        }

        /**
         * Gets a prefix node given a prefix and another prefix node to start the search from.
         *
         * @param prefix The prefix to search for.
         * @param start  The node to start the prefix search from. If {@code null} is provided the
         *               search will start from the root node
         * @returns The prefix node corresponding to {@code prefix} or {@code null} if the prefix
         * node does not exists.
         */
        public Node getPrefixNodeFrom(final String prefix, final Node start, boolean isQ) {
            if (start == null)
                return get(root, prefix, 0);
            else if (isQ)
                return get(start, prefix, prefix.length() - 2);
            else
                return get(start, prefix, prefix.length() - 1);
        }

        /**
         * Removes the key from the set if the key is present.
         *
         * @param key the key
         * @throws IllegalArgumentException if {@code key} is {@code null}
         */
        public void delete(String key) {
            if (key == null) throw new IllegalArgumentException("argument to delete() is null");
            root = delete(root, key, 0);
        }

        private Node delete(Node x, String key, int d) {
            if (x == null) return null;
            if (d == key.length()) {
                if (x.val != null) n--;
                x.val = null;
            }
            else {
                char c = key.charAt(d);
                x.next[c - 'A'] = delete(x.next[c - 'A'], key, d + 1);
            }

            // remove subtrie rooted at x if it is completely empty
            if (x.val != null) return x;
            for (int c = 0; c < R; c++)
                if (x.next[c] != null)
                    return x;
            return null;
        }
    }

    /**
     * Unit tests the {@code TrieST} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);

        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
