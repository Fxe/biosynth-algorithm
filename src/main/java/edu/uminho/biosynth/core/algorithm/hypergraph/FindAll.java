package edu.uminho.biosynth.core.algorithm.hypergraph;

import java.util.List;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperGraph;

public interface FindAll<T> {
	public List<T> findAll( DiHyperGraph<String, T> H, Set<String> sources);
}
