package pt.uminho.sysbio.biosynthframework.algorithm.network.graph.algorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.BinaryGraph;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.DefaultBinaryEdge;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.DefaultGraphImpl;

public class DefaultDepthFirstSearchImpl<V, E> implements DepthFirstSearch {
	public final BinaryGraph<V, E> G;
	public final Map<V, Boolean> marked;
	public final BinaryGraph<V, E> G_;
	
	public DefaultDepthFirstSearchImpl(BinaryGraph<V, E> graph) {
		this.G = graph;
		this.marked = new HashMap<V, Boolean> ();
		for ( V v : G.getVertices()) {
			this.marked.put(v, false);
		}
		
		this.G_ = new DefaultGraphImpl<V, E>();
	}
	
	public void reset() {
		for ( V v : this.marked.keySet()) this.marked.put(v, false);
		this.G_.clear();
	}
	
	private void mark(V v) {
		this.marked.put(v, true);
	}
	
	private boolean isMarked(V v) {
		return this.marked.get(v);
	}
	
	public void run(V root) {
		this.reset();
		G_.addVertex(root);
		
		Stack<V> S = new Stack<V>();
		S.push(root);
		mark(root);
		while ( !S.isEmpty()) {
			V t = S.pop();
			for (V adj : G.getAdjacentVertices(t)) {
				V u = adj;
				if ( !isMarked(u)) {
					G_.addVertex(u);
					double w = G.getWeight(t, u);
					DefaultBinaryEdge<E, V> edge = new DefaultBinaryEdge<E, V>(null, t, u, w);
					G_.addEdge(edge);
					mark(u);
					S.push(u);
				}
			}
		}
	}
	
	public BinaryGraph<V, E> getSearchTree() {
		return this.G_;
	}
}