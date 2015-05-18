package pt.uminho.sysbio.biosynthframework.algorithm.network.graph.algorithm.shortestpath;

public interface DijkstraShortestPath<V> extends ShortestPath<V> {

	
	/**
	 * Get the value of the optimal distance (sum of edge weights) to
	 * a specific vertex
	 * 
	 * @param v vertex of the optimal path distance
	 * 
	 * @return path distance
	 */
	public double getOptimalCostTo(V v);
}
