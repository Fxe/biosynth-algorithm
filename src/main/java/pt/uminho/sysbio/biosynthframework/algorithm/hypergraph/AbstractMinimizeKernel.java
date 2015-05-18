package pt.uminho.sysbio.biosynthframework.algorithm.hypergraph;

import java.util.Set;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperGraph;

public abstract class AbstractMinimizeKernel<T> implements Minimize<T>{

	protected FindAll<T> findAll;
	
	@Override
	public void setFindAllKernel(FindAll<T> findAll) {
		this.findAll = findAll;
	}

	@Override
	public abstract DiHyperGraph<String, T> minimize(DiHyperGraph<String, T> P,
			Set<T> Rf, Set<String> T, Set<String> S);

}
