package pt.uminho.sysbio.biosynthframework.algorithm;

import pt.uminho.sysbio.biosynthframework.core.components.optimization.Solution;
import pt.uminho.sysbio.biosynthframework.core.components.optimization.SolutionSet;

public interface IAlgorithm {
	public void solve();
	public<S extends SolutionSet<? extends Solution>> S getAlgorithmSolution(S emptySolution);
}
