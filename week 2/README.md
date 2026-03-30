# Seam Carving - Programming Assignment 2

## Overview

This assignment implements **Seam Carving**, a content-aware image resizing technique for Princeton's Algorithms, Part II course (Coursera). The algorithm reduces image size by removing "unimportant" pixels while preserving important content like edges and objects.

## Files

### Source File

| File | Description |
|------|-------------|
| `SeamCarver.java` | Main implementation of seam carving algorithm |

### Testing Utilities

| File | Description |
|------|-------------|
| `ShowEnergy.java` | Visualize energy function of an image |
| `ShowSeams.java` | Visualize seams overlaid on an image |
| `PrintEnergy.java` | Print energy matrix to stdout |
| `PrintSeams.java` | Print seam paths to stdout |
| `ResizeDemo.java` | Demo for content-aware resizing |
| `SCUtility.java` | Utility functions |

### Test Data (`testing/`)

| Image | Size | Description |
|-------|------|-------------|
| `3x4.png` | 3×4 | Small test image |
| `6x5.png` | 6×5 | Small test image |
| `7x3.png` | 7×3 | Small test image |
| `1x1.png` | 1×1 | Minimal size |
| `HJocean.png` | - | Ocean photograph |
| `HJoceanSmall.png` | - | Smaller ocean photo |
| `chameleon.png` | - | Chameleon photograph |
| `logo.png` | - | Logo image |
| `stripes.png` | - | Horizontal stripes |
| `diagonals.png` | - | Diagonal pattern |

### Documentation

| File | Description |
|------|-------------|
| `Programming Assignment 2_ Seam Carving.pdf` | Assignment specification |
| `Programming Assignment 2_ FAQ.pdf` | Frequently asked questions |

## Building and Running

Compile:
```bash
javac-algs4 SeamCarver.java
```

Show energy visualization:
```bash
java-algs4 ShowEnergy testing/6x5.png
```

Show seams on image:
```bash
java-algs4 ShowSeams testing/6x5.png
```

Print energy matrix:
```bash
java-algs4 PrintEnergy testing/6x5.png
```

Print seam paths:
```bash
java-algs4 PrintSeams testing/6x5.png
```

## Key Concepts

### Seam

A **vertical seam** is a path of pixels from top to bottom with exactly one pixel in each row:
```
| | | | | | | |
  \   |   /
    \ | /
      V
```

A **horizontal seam** is a path from left to right with exactly one pixel in each column.

### Energy Function

Energy measures the "importance" of a pixel. High energy pixels (edges) are preserved; low energy pixels (flat regions) can be removed.

**Dual Gradient Energy:**
```
Δx² = (R(x,y-1) - R(x,y+1))² + (G(x,y-1) - G(x,y+1))² + (B(x,y-1) - B(x,y+1))²
Δy² = (R(x-1,y) - R(x+1,y))² + (G(x-1,y) - G(x+1,y))² + (B(x-1,y) - B(x+1,y))²

energy(x, y) = √(Δx² + Δy²)
```

Border pixels have energy = 1000 (never removed first).

## Algorithm

### Finding Minimum Energy Vertical Seam (Dynamic Programming)

1. Build energy grid for all pixels
2. DP recurrence: `distTo[i][j] = energy[i][j] + min(distTo[i-1][j-1], distTo[i-1][j], distTo[i-1][j+1])`
3. Find minimum energy pixel in bottom row
4. Backtrack using `edgeTo` array to reconstruct seam

### Content-Aware Resize

```
while (currentWidth > targetWidth):
    seam = findVerticalSeam()
    removeVerticalSeam(seam)
```

## Implementation Details

### Data Structures

| Structure | Purpose |
|-----------|---------|
| `Picture pic` | Current image |
| `double[][] energyGrid` | Precomputed pixel energies |
| `double[][] distTo` | DP shortest path distances |
| `Pixel[][] edgeTo` | Backpointer for seam reconstruction |

### Optimization

- Bit manipulation for fast RGB extraction (avoids `Color.getRed()` overhead)
- Transpose trick: `findHorizontalSeam()` reuses vertical seam logic

## Score

**100/100** - Perfect score
