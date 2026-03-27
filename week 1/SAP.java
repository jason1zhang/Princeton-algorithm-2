import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private final Digraph G;
    
    /**
     * constructor takes a digraph (not necessarily a DAG)
     */
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException();
        
        this.G = new Digraph(G);
    }
    
    /**
     * length of shortest ancestral path between v and w; -1 if no such path
     */
    public int length(int v, int w) {        
        validateVertex(v);
        validateVertex(w);
        
        BreadthFirstDirectedPaths bfs_v = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfs_w = new BreadthFirstDirectedPaths(G, w);
        
        return findSAP(bfs_v, bfs_w, true);
    }
    
    /**
     * a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
     */
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        
        BreadthFirstDirectedPaths bfs_v = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfs_w = new BreadthFirstDirectedPaths(G, w);
        
        return findSAP(bfs_v, bfs_w, false);
    }
    
    /**
     * length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);
            
        BreadthFirstDirectedPaths bfs_v = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfs_w = new BreadthFirstDirectedPaths(G, w);
        
        return findSAP(bfs_v, bfs_w, true);
    }
    
    /**
     * a common ancestor that participates in shortest ancestral path; -1 if no such path
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);
        
        BreadthFirstDirectedPaths bfs_v = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfs_w = new BreadthFirstDirectedPaths(G, w);
        
        return findSAP(bfs_v, bfs_w, false);
    }
    
    /**
     * A common private function to calculate the shortest length and ancestor,
     * and return what is needed.
     */
    private int findSAP(BreadthFirstDirectedPaths bfs_v, BreadthFirstDirectedPaths bfs_w, boolean returnLength) {
    
        int length = Integer.MAX_VALUE;
        int temp_len;
        int ancestor = -1;
        
        for (int s = 0; s < G.V(); s++) {
            if (bfs_v.hasPathTo(s) && bfs_w.hasPathTo(s)) {
                temp_len = bfs_v.distTo(s) + bfs_w.distTo(s);
                if (temp_len < length) {
                    length = temp_len;
                    ancestor = s;
                }
            }
        }    
        
        if (length == Integer.MAX_VALUE) length = -1;
        
        if (returnLength) return length;
        else return ancestor;
      
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = G.V();
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }   

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("argument is null");
        }
        int V = G.V();
        for (int v : vertices) {
            if (v < 0 || v >= V) {
                throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
            }
        }
    }    
    /**
     * do unit testing of this class
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }        
    }
}