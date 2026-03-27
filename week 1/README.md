# WordNet - Programming Assignment 1

## Overview

This assignment implements a **WordNet semantic lexicon** for Princeton's Algorithms, Part II course (Coursera). WordNet is a lexical database of English nouns, verbs, and adjectives that groups words into *synsets* (sets of synonyms) and defines semantic relationships like hypernymy (IS-A).

## Files

### Source Files

| File | Description |
|------|-------------|
| `WordNet.java` | Main lexicon data structure - stores synsets and hypernym relationships |
| `SAP.java` | Shortest Ancestral Path - finds shortest path between two vertices in a digraph |
| `Outcast.java` | Given a set of nouns, finds the most unrelated noun (outcast) |

### Test Data (`wordnet-testing/`)

| File Pattern | Description |
|--------------|-------------|
| `synsets*.txt` | Synonym sets (id, synonyms, gloss) |
| `hypernyms*.txt` | Hypernym relationships (IS-A edges) |
| `digraph*.txt` | Directed graph adjacency lists |
| `outcast*.txt` | Test input for Outcast |

### Documentation

| File | Description |
|------|-------------|
| `Programming Assignment 1_ WordNet.pdf` | Assignment specification |
| `Programming Assignment 1_ FAQ.pdf` | Frequently asked questions |
| `wordnet-specification.pdf` | Detailed algorithm specification |
| `wordnet-checklist.pdf` | Submission checklist |

## Data Format

**Synsets file** (`synsets.txt`):
```
id,synonyms,gloss
0,event,anything that happens; a phenomenon
1,thing,an entity not further specified
```

**Hypernyms file** (`hypernyms.txt`):
```
hyponym,hypernym1,hypernym2,...
0,1,2
1,2
2
```

## Building and Running

Compile all Java files:
```bash
javac -cp ".:../algs4.jar" *.java
```

Run WordNet test:
```bash
java -cp ".:../algs4.jar" WordNet synsets.txt hypernyms.txt
```

Run SAP test (enter vertex pairs on stdin):
```bash
java -cp ".:../algs4.jar" SAP digraph.txt
```

Run Outcast test:
```bash
java -cp ".:../algs4.jar" Outcast synsets.txt hypernyms.txt outcast5.txt
```

## Key Algorithms

### Shortest Ancestral Path (SAP)

For vertices `v` and `w`:
1. Perform BFS backward from `v` (following reverse hypernym edges)
2. Perform BFS backward from `w`
3. Find vertex `s` reachable by BOTH with minimum `dist(v,s) + dist(w,s)`
4. Return `s` as the Lowest Common Ancestor (LCA)

### Outcast

Given nouns `{a, b, c, d}`, compute:
- `totalDist(a) = distance(a,b) + distance(a,c) + distance(a,d)`
- Return noun with maximum total distance

## Score

**99/100** - 1 point deducted for minor style/optimization issues
