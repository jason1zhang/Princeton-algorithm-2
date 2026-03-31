import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.SET;

import java.util.Arrays;

/******************************************************************************
 *  Name: Rhett Zhang
 *  Date: 2020/01/25
 *
 *  Compilation:  javac-algs4 BurrowsWheeler.java
 *  Execution:    java-algs4 BurrowsWheeler
 *  Dependencies: BinaryStdOut.java
 *
 *  Given the standings in a sports division at some point during the season,
 *  determine which teams have been mathematically eliminated
 *  from winning their division.
 *
 *  % java-algs4 BurrowsWheeler
 *
 ******************************************************************************/

public class BurrowsWheeler {
    private static final int EXTENDED_ASCII = 256;  // number of input chars

    // no need to instantiate
    private BurrowsWheeler() {
    }

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String input = BinaryStdIn.readString();
        CircularSuffixArray csArray = new CircularSuffixArray(input);
        int size = csArray.length();
        int first = -1;

        for (int i = 0; i < size; i++) {
            if (csArray.index(i) == 0) {
                first = i;
                break;
            }
        }

        BinaryStdOut.write(first);

        int charIdx;
        for (int i = 0; i < size; i++) {
            // compute the index of the last column in the sorted suffixes array
            charIdx = (csArray.index(i) - 1 + size) % size;
            BinaryStdOut.write(input.charAt(charIdx));
        }
        BinaryStdOut.close();   // don't forget to close the binary output stream
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        char[] t = (BinaryStdIn.readString()).toCharArray();
        int size = t.length;

        // utilize key-indexed counting algorithm
        int[] count = new int[EXTENDED_ASCII + 1];

        // compute frequency counts
        for (int i = 0; i < size; i++)
            count[t[i] + 1]++;

        // compute cumulates
        for (int r = 0; r < EXTENDED_ASCII; r++)
            count[r + 1] += count[r];

        // construct the next[]
        int[] next = new int[size];
        char[] aux = new char[size];

        for (int i = 0; i < size; i++) {
            aux[count[t[i]]++] = t[i];

            // to construct next[] and aux[] at the same time, a little bit awkward
            count[t[i]]--;

            next[count[t[i]]++] = i;
        }

        /*
        for (int i = 0; i < size; i++)
            StdOut.printf("%c \t", aux[i]);
        StdOut.println();
        */

        char[] origStr = new char[size];
        int nextIndex = first;
        // reconstruct the original input string from next[] array and first
        for (int k = 0; k < size; k++) {
            origStr[k] = aux[nextIndex];
            nextIndex = next[nextIndex];

            BinaryStdOut.write(origStr[k]);
        }

        BinaryStdOut.close();

    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    private static void inverseTransform_old() {
        int first = BinaryStdIn.readInt();
        char[] t = (BinaryStdIn.readString()).toCharArray();
        int size = t.length;

        char[] firstCols = Arrays.copyOf(t, size);
        Arrays.sort(firstCols);

        int[] next = new int[size];

        // use a set to store the found indices
        // to solve the ambiguity for the same characters
        SET<Integer> setIndex = new SET<Integer>();

        // construct the next[] from t[] and first
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (firstCols[i] == t[j] && !setIndex.contains(j)) {
                    next[i] = j;
                    setIndex.add(j);
                    break;
                }
            }
        }

        char[] origStr = new char[size];
        int nextIndex = first;
        // reconstruct the original input string from next[] array and first
        for (int k = 0; k < size; k++) {
            origStr[k] = firstCols[nextIndex];
            nextIndex = next[nextIndex];

            BinaryStdOut.write(origStr[k]);
        }

        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
