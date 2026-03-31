# Burrows-Wheeler Compression - Princeton Algorithms II Week 5

## Overview

This assignment implements the **Burrows-Wheeler compression algorithm**, a key algorithm used in file compression tools like bzip2. It transforms data to make it highly compressible.

## Project Structure

```
week 5/
├── CircularSuffixArray.java    # Sorts all cyclic rotations of input
├── BurrowsWheeler.java         # Forward and inverse BWT transform
├── MoveToFront.java           # Move-to-front encoding/decoding
├── testing/
│   ├── abra.txt
│   ├── zebra.txt
│   ├── cadabra.txt
│   ├── aesop.txt
│   └── ...                    # Test files
├── Programming Assignment 5_ Burrows–Wheeler.pdf
├── Programming Assignment 5_ FAQ.pdf
└── grader_burrows_100.docx     # 100/100 grade
```

## Building & Running

```bash
javac -cp .:../algs4.jar *.java
```

## Algorithm Components

### 1. CircularSuffixArray

Sorts all cyclic rotations of a string. This is the foundation of the BWT transform.

```java
CircularSuffixArray csArray = new CircularSuffixArray("ABRACADABRA!");
// Returns sorted indices of all circular suffixes
```

**Example:** For "ABRACADABRA!":
```
Index 0:  ABRACADABRA!    (original)
Index 4:  ACADABRA!ABR
Index 2:  ACRADABRA!AB
...
```

### 2. BurrowsWheeler

Implements both forward and inverse transforms.

**Transform (encoding):**
```bash
java -cp .:../algs4.jar BurrowsWheeler - < input.txt > output.bwt
```
- Sorts all circular suffixes
- Outputs last column of sorted matrix + row index of original string

**Inverse Transform (decoding):**
```bash
java -cp .:../algs4.jar BurrowsWheeler + < output.bwt > decoded.txt
```
- Reconstructs original string using first column and row index

### 3. MoveToFront

Additional encoding step after BWT to improve compressibility.

**Encoding:**
```bash
java -cp .:../algs4.jar MoveToFront - < input.bwt > output.mtf
```

**Decoding:**
```bash
java -cp .:../algs4.jar MoveToFront + < output.mtf > decoded.bwt
```

## How It Works

### Burrows-Wheeler Transform

1. **Input:** "ABRACADABRA!"
2. **Create matrix** of all circular suffixes:
   ```
   ABRACADABRA!
   BRACADABRA!A
   RACADABRA!AB
   ...
   ```
3. **Sort rows** lexicographically
4. **Output:** Last column + row number of original string

### Inverse Transform

1. **First column** = sorted last column characters
2. Use **next[] array** (via key-indexed counting) to trace back
3. Follow next[] from `first` row to reconstruct original

### Complete Compression Pipeline

```
Original → BurrowsWheeler.transform() → MoveToFront.encode() → [Entropy coder]
              ↓                              ↓
         last column + first            indices with
         (sorted runs)                  local patterns
```

## Testing

Test files in `testing/` folder include:
- `abra.txt`, `cadabra.txt` - Short test cases
- `aesop.txt` - Longer text
- `amendments.txt` - US Constitution amendments

```bash
# Full compression cycle
java -cp .:../algs4.jar BurrowsWheeler - < testing/abra.txt | \
java -cp .:../algs4.jar MoveToFront - | \
[entropy coding here]

# Decode
[entropy decoding] | \
java -cp .:../algs4.jar MoveToFront + | \
java -cp .:../algs4.jar BurrowsWheeler +
```

## Key Implementation Details

### CircularSuffixArray
- Inner class `CircularSuffix` implements `Comparable`
- `compareTo()` compares cyclic rotations using modulo
- Sort N suffixes, store starting indices

### BurrowsWheeler
- **Transform:** Find row where `index(i) == 0`, output last column chars
- **Inverse:** Uses key-indexed counting to build `next[]` array efficiently (linear time)

### MoveToFront
- Maintains ordered sequence of 256 ASCII characters
- After reading character at index `i`, move it to front
- Results in small indices for recently seen characters

## Reference

- [Burrows-Wheeler Transform](https://en.wikipedia.org/wiki/Burrows%E2%80%93Wheeler_transform)
- [Move-to-front coding](https://en.wikipedia.org/wiki/Move-to-front_transform)
