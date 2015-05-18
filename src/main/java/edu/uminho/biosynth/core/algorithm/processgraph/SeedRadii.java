package edu.uminho.biosynth.core.algorithm.processgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.core.components.optimization.Solution;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.pgraph.OperatingUnit;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.pgraph.ProcessGraph;

public class SeedRadii {
	public final static Logger LOGGER = LoggerFactory.getLogger(SeedRadii.class);
	
	protected Set<String> S;
	protected Set<String> T;
	protected Set<OperatingUnit<String>> O;
	protected ProcessGraph<String> G;
	protected Set<String> M;
	protected Set<String> B;
	
	private Set<OperatingUnit<String>> I;
	private int level;
	
	private int radius;
	private Solution solution;
	
	private Set<String> totalRxn = new HashSet<> ();
	private Map<Integer, Set<String>> map = new HashMap<> ();
	
	public SeedRadii(ProcessGraph<String> graph, Set<String> sources, Set<String> targets, int radius) {
		this.S = sources;
		this.T = targets;
		this.O = graph.getO();
		
		this.I = new HashSet<OperatingUnit<String>> ();
		this.M = new HashSet<String> ();
		this.B = new HashSet<String> (T);
		this.radius = radius;
//		DecisionMapping<String> deltaMap = new DecisionMapping<String>(targets, O)
	}
	
	public void expand() {
		Set<String> rxnAtCurrentLevel = new HashSet<> ();
//		G.
	}
}
