package edu.uminho.biosynth.core.algorithm.hypergraph;

import java.util.Set;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperGraph;

public interface Minimize<T> {
	
	public void setFindAllKernel(FindAll<T> findAll);
	public DiHyperGraph<String, T> minimize( DiHyperGraph<String, T> P, Set<T> Rf, Set<String> T, Set<String> S);
}
