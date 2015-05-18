package pt.uminho.sysbio.metropolis.network.graph.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.BinaryGraph;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.graph.IBinaryEdge;

public class GraphCluster<V, E> {
	
	private Map<String, List<V>> clustersToListOfVertex = new HashMap<> ();
	private Map<V, String> vertexToClusterId = new HashMap<> ();
	
	public void mergeTwoClusters(String cluster1, String cluster2) {
		List<V> leftCluster = clustersToListOfVertex.remove(cluster1);
		List<V> rightCluster = clustersToListOfVertex.remove(cluster2);
		if (leftCluster != null && rightCluster != null) {
			leftCluster.addAll(rightCluster);
			clustersToListOfVertex.put(cluster1, leftCluster);
			for (V node : leftCluster) vertexToClusterId.put(node, cluster1);
		}
	}
	
	public void generateClusters(BinaryGraph<V, E> referenceGraph) {
		Integer incrementId = 0;
		clustersToListOfVertex.clear();
		vertexToClusterId.clear();
		
		for (IBinaryEdge<E, V> edge : referenceGraph.getEdges()) {			
			V leftNodeEntry = edge.getLeft();
			V rightNodeEntry = edge.getRight();
			
			/*    L    R
			 *    X    X   both exists then merge if L and R not in same cluster (Link edge connects both clusters)
			 *    X    O   exists only on left then add R to the L cluster
			 *    O    X   exists only on right then add L to the R cluster
			 *    O    O   both L and R are new elements create a new cluster with L and R
			 */
			
			if (vertexToClusterId.containsKey(leftNodeEntry) && vertexToClusterId.containsKey(rightNodeEntry)) {
				//X    X
				String leftClusterId = vertexToClusterId.get(leftNodeEntry);
				String rightClusterId = vertexToClusterId.get(rightNodeEntry);
				if (!leftClusterId.equals(rightClusterId)) {
					this.mergeTwoClusters(leftClusterId, rightClusterId);
				}
				
			} else if (!vertexToClusterId.containsKey(leftNodeEntry) && !vertexToClusterId.containsKey(rightNodeEntry)) {
				//O    O
				List<V> newClusters = new ArrayList<> ();
				newClusters.add(leftNodeEntry);
				newClusters.add(rightNodeEntry);
				String clusterId = "cluster_" + incrementId;
				clustersToListOfVertex.put(clusterId, newClusters);
				vertexToClusterId.put(leftNodeEntry, clusterId);
				vertexToClusterId.put(rightNodeEntry, clusterId);
				incrementId++;
			} else if (vertexToClusterId.containsKey(leftNodeEntry)) {
				//X    O
				String clusterId = vertexToClusterId.get(leftNodeEntry);
				clustersToListOfVertex.get(clusterId).add(rightNodeEntry);
				vertexToClusterId.put(rightNodeEntry, clusterId);
			} else {
				//O    X
				String clusterId = vertexToClusterId.get(rightNodeEntry);
				clustersToListOfVertex.get(clusterId).add(leftNodeEntry);
				vertexToClusterId.put(leftNodeEntry, clusterId);
			}
		}
		
		for (V vertex : referenceGraph.getVertices()) {
			if ( !this.vertexToClusterId.containsKey(vertex)) {
				String clusterId = "cluster_" + incrementId++;
				vertexToClusterId.put(vertex, clusterId);
				List<V> newClusters = new ArrayList<> ();
				newClusters.add(vertex);
				clustersToListOfVertex.put(clusterId, newClusters);
			}
		}
	}

	public Map<String, List<V>> getClustersToListOfVertex() {
		return clustersToListOfVertex;
	}

	public Map<V, String> getVertexToClusterId() {
		return vertexToClusterId;
	}
}
