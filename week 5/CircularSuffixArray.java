import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

/******************************************************************************
 *  Name: Rhett Zhang
 *  Date: 2020/01/24
 *
 *  Compilation:  javac-algs4 CircularSuffixArray.java
 *  Execution:    java-algs4 CircularSuffixArray
 *  Dependencies: BinaryStdOut.java
 *
 *  a fundamental data structure known as the circular suffix array
 *  used to efficiently implement the key component in the Burrows-Wheeler
 *  transform. It describes the abstraction of a sorted array of the n circular
 *  suffixes of a string of length n.
 *
 *  % java-algs4 CircularSuffixArray
 *
 ******************************************************************************/

public class CircularSuffixArray {
    private final int[] csIndex;
    private final int size;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException("null string");

        this.size = s.length();

        CircularSuffix[] csArray = new CircularSuffix[this.size];
        csIndex = new int[this.size];

        for (int i = 0; i < this.size; i++)
            csArray[i] = new CircularSuffix(s, i);

        Arrays.sort(csArray);

        for (int i = 0; i < this.size; i++)
            this.csIndex[i] = csArray[i].first;
    }

    // length of s
    public int length() {
        return this.size;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= size)
            throw new IllegalArgumentException("index out of bound");
        return this.csIndex[i];
    }

    private class CircularSuffix implements Comparable<CircularSuffix> {
        private final String s;
        private final int first;

        public CircularSuffix(String s, int first) {
            this.s = s;
            this.first = first;
        }

        @Override
        public int compareTo(CircularSuffix cs) {
            int n = s.length();
            char c1, c2;
            for (int i = 0; i < n; i++) {
                c1 = this.s.charAt((first + i) % n);
                c2 = cs.s.charAt((cs.first + i) % n);
                if (c1 > c2) return 1;
                else if (c1 < c2) return -1;
            }

            return 0;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        String s = "ABRACADABRA!";
        CircularSuffixArray csArray = new CircularSuffixArray(s);
        int size = csArray.length();
        StdOut.printf("size: %d \n", size);
        for (int i = 0; i < size; i++)
            StdOut.println(csArray.index(i));
    }
}
