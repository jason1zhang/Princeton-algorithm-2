import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/******************************************************************************
 *  Name: Rhett Zhang
 *  Date: 2020/01/25
 *
 *  Compilation:  javac-algs4 MoveToFront.java
 *  Execution:    java-algs4 MoveToFront
 *  Dependencies: BinaryStdOut.java
 *
 *  The main idea of move-to-front encoding is to maintain an ordered sequence
 *  of the characters in the alphabet by repeatedly reading a character
 *  from the input message; printing the position in the sequence
 *  in which that character appears;
 *  and moving that character to the front of the sequence..
 *
 *  % java-algs4 MoveToFront
 *
 ******************************************************************************/

public class MoveToFront {
    private static final int EXTENDED_ASCII = 256;  // number of input chars

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {

        char[] alphabet = new char[EXTENDED_ASCII];      // the characters in the alphabet

        // Initialize the sequence by making the ith character in the sequence
        // equal to the ith extended ASCII character
        for (int i = 0; i < EXTENDED_ASCII; i++)
            alphabet[i] = (char) i;

        // read each 8-bit character c from standard input, one at a time;
        // output the 8-bit index in the sequence where c appears;
        // and move c to the front.
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();

            for (int i = 0; i < EXTENDED_ASCII; i++) {
                if (c == alphabet[i]) {

                    for (int j = i; j > 0; j--) {
                        alphabet[j] = alphabet[j - 1];
                    }

                    alphabet[0] = c;
                    BinaryStdOut.write(i, 8);
                    break;
                }
            }
        }

        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {

        char[] alphabet = new char[EXTENDED_ASCII];      // the characters in the alphabet

        // Initialize the sequence by making the ith character in the sequence
        // equal to the ith extended ASCII character
        for (int i = 0; i < EXTENDED_ASCII; i++)
            alphabet[i] = (char) i;

        // read each 8-bit character i (but treat it as an integer between 0 and 255)
        // from standard input one at a time; write the ith character in the sequence;
        // and move that character to the front.
        while (!BinaryStdIn.isEmpty()) {
            int index = (int) BinaryStdIn.readChar();

            char c = alphabet[index];
            BinaryStdOut.write(c);

            for (int j = index; j > 0; j--) {
                alphabet[j] = alphabet[j - 1];
            }
            alphabet[0] = c;
        }

        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
