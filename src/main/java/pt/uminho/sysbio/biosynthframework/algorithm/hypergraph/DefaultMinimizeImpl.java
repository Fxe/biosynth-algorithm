package pt.uminho.sysbio.biosynthframework.algorithm.hypergraph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperGraph;

public class DefaultMinimizeImpl<E> extends AbstractMinimizeKernel<E> {
	
	private static Logger LOGGER = LoggerFactory.getLogger(DefaultMinimizeImpl.class);
	
	@Override
	public DiHyperGraph<String, E> minimize( DiHyperGraph<String, E> P, Set<E> Rf, Set<String> T, Set<String> S) {
		LOGGER.debug("Minimizing ...");
		
		List<E> F = findAll.findAll(P, S);
		Set<String> Y_union_F = new HashSet<String> ();
		for (E r : F) {
			Y_union_F.addAll( P.Y(r));
		}
		DiHyperGraph<String, E> P_ = new DiHyperGraph<String, E> ( P);
		if ( !Y_union_F.containsAll(T)) {
			DiHyperGraph<String, E> emptyHPG = new DiHyperGraph<String, E> ();
			return emptyHPG;
		}
		
		//F contains MU
		for (E r : P.getEdges()) {
			if ( !Rf.contains(r)) {
				DiHyperGraph<String, E> P_TEST = new DiHyperGraph<>(P_);
				P_TEST.removeEdge(r);
				List<E> F_ = findAll.findAll(P_TEST, S);
				
				Y_union_F.clear();
				for (E e : F_) {
					Y_union_F.addAll( P.Y(e));
				}
				if ( Y_union_F.containsAll(T)) {
					LOGGER.trace("R");
					P_.removeEdge(r);
				} else {
					LOGGER.trace("A");
				}
			}
		}
		
		LOGGER.debug(P_.toString());
		return P_;
	}
}
