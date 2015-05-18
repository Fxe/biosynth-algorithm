package pt.uminho.sysbio.metropolis.network.graph.algorithm.shortestpath;

import pt.uminho.sysbio.metropolis.Algorithm;

public interface ShortestPath<V> extends Algorithm<Double> {
	public void setOrigin(V v);
	public void setDestination(V v);
	
	/**
	 * Get the value of the optimal distance (sum of edge weights) to
	 * the destination vertex.
	 * 
	 * @return path distance
	 */
	public double getOptimalCost();
}
