package edu.uminho.biosynth.core.algorithm.processgraph;

import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.pgraph.OperatingUnit;

public interface SolutionStructureGeneration<T> {
	public void solve();
	public Map< Integer, Map< T, Set< OperatingUnit<T>>>> getSolutionStructures();
}
