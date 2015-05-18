package edu.uminho.biosynth.core.algorithm.hypergraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperGraph;

public class DebugFindAllImpl<T> implements FindAll<T> {
	
	private Logger LOGGER = LoggerFactory.getLogger(DefaultFindAllImpl.class);
	
	private Map<String, Integer> unsatisfiedDependencies = new HashMap<> ();
	private Set<T> visitedReactions = new HashSet<> ();
	
	private void reset() {
		visitedReactions.clear();
		unsatisfiedDependencies.clear();
	}
	
	@Override
	public final List<T> findAll( DiHyperGraph<String, T> H, Set<String> sources) {
		this.reset();
//		long start = System.currentTimeMillis();
		
		Map< T, Set<String>> x = new HashMap<T, Set<String>> ();
		Map< String, Set<T>> rev_x = new HashMap<String, Set<T>> ();
		for ( T r : H.getEdges()) {
			Set<String> HX = new HashSet<String> ( H.X(r));
			x.put(r, HX);
			for (String i : H.X(r)) {
				if ( !rev_x.containsKey(i)) {
					rev_x.put(i, new HashSet<T> ());
				}
				rev_x.get(i).add(r);
			}
		}
		LinkedList<String> V = new LinkedList<String> (sources);
		Set<String> D = new HashSet<String> (sources);
		LinkedList<T> F = new LinkedList<T>();
		LOGGER.trace("V: " + V);
		LOGGER.trace("D: " + D);
		LOGGER.trace("Path: " + F);
		while ( !V.isEmpty()) {
			String i = V.getFirst();
			
			LOGGER.trace("Remove Dependency From edges: " + i);
			
			//V i = V.iterator().next();
			V.remove(i);
			D.add(i);
			if ( rev_x.containsKey(i)) {
				for ( T r : rev_x.get(i)) {
					x.get(r).remove(i);
					visitedReactions.add(r);
					if ( x.get(r).isEmpty()) {
						if ( !F.contains(r)) {
							F.addLast(r);
						}
						Set<String> j = new HashSet<String> (H.Y(r));
						
//						LOGGER.trace(j);
						j.removeAll(D);
						
						for ( String v : j) {
							if ( !V.contains(v)) {
								V.addLast(v);
							}
						}
					}
				}
			}
		}
		List<T> ret = F;
		
		visitedReactions.removeAll(ret);
		
		for (T r : visitedReactions) {
			for (String m : x.get(r)) {
				if ( !unsatisfiedDependencies.containsKey(m)) {
					unsatisfiedDependencies.put(m, 1);
				} else {
					int prev = unsatisfiedDependencies.get(m);
					unsatisfiedDependencies.put(m, prev + 1);
				}
			}
		}
//		System.out.println(unsatisfiedDependencies);
		
		Map<Integer, Set<String>> sorted = new TreeMap<> ();
		for (String id : unsatisfiedDependencies.keySet()) {
			int i = unsatisfiedDependencies.get(id);
			if (!sorted.containsKey(i)) sorted.put(i, new HashSet<String> ());
			sorted.get(i).add(id);
		}
		for (Integer i : sorted.keySet()) {
			System.out.println(i + " " + sorted.get(i));
		}
//		faTime += System.currentTimeMillis() - start;
//		System.out.println("FA TOOK: " + faTime);
		return ret;
	}
}
