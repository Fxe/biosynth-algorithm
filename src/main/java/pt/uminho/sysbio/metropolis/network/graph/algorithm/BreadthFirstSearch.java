package pt.uminho.sysbio.metropolis.network.graph.algorithm;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.BinaryGraph;

public class BreadthFirstSearch {

	private static final Logger LOGGER = LoggerFactory.getLogger(BreadthFirstSearch.class);
	
	public static<V, E> Set<V> run(BinaryGraph<V, E> graph, V vertex) {
		Set<V> res = new HashSet<> ();
		
		if (!graph.getVertices().contains(vertex)) {
			LOGGER.debug(String.format("Vertex %s not found.", vertex));
			return res;
		}
		
		LinkedList<V> queue = new LinkedList<>();
		Set<V> visited = new HashSet<> ();
		queue.addFirst(vertex);
		visited.add(vertex);
		while (!queue.isEmpty()) {
			V v = queue.getFirst();
			queue.remove(v);
			res.add(v);
			for (V adj : graph.getAdjacentVertices(v)) {
				if (!visited.contains(adj)) {
					visited.add(adj);
					queue.addFirst(adj);
				}
			}
		}
		
		return res;
	}
	
//	public static<V, E> Set<IBinaryEdge> run2(BinaryGraph<V, E> graph, V vertex) {
//		Set<IBinaryEdge> res = new HashSet<> ();
//		LinkedList<V> queue = new LinkedList<>();
//		Set<V> visited = new HashSet<> ();
//		queue.addFirst(vertex);
//		visited.add(vertex);
//		while (!queue.isEmpty()) {
//			V v = queue.getFirst();
//			queue.remove(v);
//			res.add(v);
//			for (V adj : graph.getAdjacentVertices(v)) {
//				if (!visited.contains(adj)) {
//					visited.add(adj);
//					queue.addFirst(adj);
//				}
//			}
//		}
//		return res;
//	}
}
