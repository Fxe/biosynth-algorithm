package edu.uminho.biosynth.core.algorithm.hypergraph;

public abstract class AbstractFindPathKernel<T> implements FindPath<T> {
	protected Minimize<T> minimize;
	protected FindAll<T> findAll;
	
	@Override
	public void setFindAllKernel(FindAll<T> findAll) {
		this.findAll = findAll;
	}
	
	@Override
	public void setMinimizeKernel(Minimize<T> minimize) {
		this.minimize = minimize;
	}

	@Override
	public abstract void solve();
}
