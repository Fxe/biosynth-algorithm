package pt.uminho.sysbio.biosynthframework.algorithm.hypergraph;

public interface FindPath<T> {
	public void setFindAllKernel(FindAll<T> findAll);
	public void setMinimizeKernel(Minimize<T> minimize);
	
	public void solve();
}
