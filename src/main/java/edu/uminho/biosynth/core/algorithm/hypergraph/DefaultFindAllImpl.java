package edu.uminho.biosynth.core.algorithm.hypergraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperGraph;

public class DefaultFindAllImpl<T> implements FindAll<T> {
	
	private Logger LOGGER = LoggerFactory.getLogger(DefaultFindAllImpl.class);
	
	@Override
	public final List<T> findAll( DiHyperGraph<String, T> H, Set<String> sources) {
		LOGGER.debug(String.format("Prunning %s edges ...", H.order()));
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
		
		LOGGER.debug(String.format("Reachable states: %s", F));
//		faTime += System.currentTimeMillis() - start;
//		System.out.println("FA TOOK: " + faTime);
		return ret;
	}
}
