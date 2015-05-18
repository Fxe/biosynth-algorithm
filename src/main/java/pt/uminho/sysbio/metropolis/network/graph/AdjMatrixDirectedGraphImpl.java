package pt.uminho.sysbio.metropolis.network.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.BinaryGraph;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.IBinaryEdge;

public class AdjMatrixDirectedGraphImpl<V, E> implements BinaryGraph<V, E> {

	private int vertexCounter;
	private Map<V, Integer> vertexIndexMap = new HashMap<> ();
	private double[][] adjMatrix;

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int order() {
		return adjMatrix.length;
	}

	@Override
	public void clear() {
		this.vertexCounter = 0;
		this.adjMatrix = new double[0][0];
		this.vertexIndexMap.clear();
	}

	@Override
	public void reverseGraph() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addVertex(V v) {
		this.vertexIndexMap.put(v, vertexCounter);
		vertexCounter++;
		return false;
	}

	@Override
	public boolean removeVertex(V v) {
		return this.vertexIndexMap.remove(v) != null;
	}

	@Override
	public Set<V> getVertices() {
		return this.vertexIndexMap.keySet();
	}

	@Override
	public Set<V> getAdjacentVertices(V v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addEdge(V src, V dst, E e) {
		return this.addEdge(src, src, e, 1.0);
	}

	@Override
	public boolean addEdge(V src, V dst, E e, double w) {
		int i = 0;
		int j = 0;
		this.adjMatrix[i][j] = w;
		return false;
	}
	
	public boolean addEdge(int src, int dst, E e, double w) {
		boolean nonZero = this.adjMatrix[src][dst] != 0.0d;
		this.adjMatrix[src][dst] = w;
		return nonZero;
	}

	@Override
	public boolean addEdge(IBinaryEdge<E, V> edge) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E getEdge(V src, V dst) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBinaryEdge<E, V> getEdge(E edge) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IBinaryEdge<E, V>> getEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAll(BinaryGraph<V, E> graph) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWeight(V src, V dst, double w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getWeight(V src, V dst) {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
