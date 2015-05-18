package edu.uminho.biosynth.core.algorithm.hypergraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperEdge;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperGraph;

public class TreeEliminationMinimizaImpl<E> extends AbstractMinimizeKernel<E> {
	
	private static Logger LOGGER = LoggerFactory.getLogger(TreeEliminationMinimizaImpl.class);
	
	private int bucketSize;
	
	public int getBucketSize() { return bucketSize;}
	public void setBucketSize(int bucketSize) { this.bucketSize = bucketSize;}
	
	public FindAll<E> getFindAll() { return findAll;}
	public void setFindAll(FindAll<E> findAll) { this.findAll = findAll;}

	@Override
	public DiHyperGraph<String, E> minimize( DiHyperGraph<String, E> P, Set<E> Rf, Set<String> T, Set<String> S) {
		Set<String> T_ = new HashSet<String> (T);
		//TODO: CLEAN THIS CODE !
		for (E edge : Rf) {
			DiHyperEdge<String, E> hyperEdge = P.getArc(edge);
			T_.addAll(hyperEdge.inLinks());
		}
		
		List<E> F = findAll.findAll(P, S);
		
		LOGGER.trace("List<E> F = findAll(P, S) " + F);

		DiHyperGraph<String, E> P_ = new DiHyperGraph<String, E> ( P);
		Set<String> Y_union_F = new HashSet<String> ();
		for (E r : F) {
			Y_union_F.addAll( P.Y(r));
		}
		if ( !Y_union_F.containsAll(T_)) {
			DiHyperGraph<String, E> emptyHPG = new DiHyperGraph<String, E> ();
			return emptyHPG;
		}
		
		LOGGER.trace("#" + F.size() + "###" + F);

		for (E e : Rf) {
			F.remove(e);
		}
		
		LOGGER.trace("#" + F.size() + "###" + F);

		for (int i = (F.size() - 1); i >= 0; i--) {
//System.out.println("IIII" + F.get(i));
			//if ( !Rf.contains(F.get(i))) {
			List<E> bucket = new ArrayList<E> ();
			
			//System.out.println("BUCKET");
			for (int j = i; j >= 0 && bucket.size() < bucketSize; j--) {
				
				bucket.add(F.get(j));
				//System.out.println("b_size :" + bucket.size());
			}
			//System.out.println( "bucket:" + bucket);
			
			
			//TRY TO REMOVE ENTIRE BUCKET
			DiHyperGraph<String, E> aux_b = new DiHyperGraph<String, E> (P_);
			for (int m = 0; m < bucket.size(); m++) {
				aux_b.removeEdge( bucket.get(m));
			}
			List<E> F__ = findAll.findAll( aux_b, S);
			Set<String> Y_union_f_ = new HashSet<String> ();
			for (E k : F__) Y_union_f_.addAll( P.Y(k));
			if ( Y_union_f_.containsAll(T_)) {
				
				LOGGER.trace("B"); // ENTIRE BUCKET REMOVED

				for (int m = 0; m < bucket.size(); m++) {
					
					P_.removeEdge(bucket.get(m));
				}
				
			} else { //IF FAIL REMOVE 1 BY 1
				
				for (int m = 0; m < bucket.size(); m++) {
					DiHyperGraph<String, E> aux = new DiHyperGraph<String, E> (P_);
					aux.removeEdge(bucket.get(m));
					List<E> F_ = findAll.findAll( aux, S);
					Set<String> Y_union_f = new HashSet<String> ();
					for (E k : F_) Y_union_f.addAll( P.Y(k));
					if ( Y_union_f.containsAll(T_)) {
						LOGGER.trace("P"); // BUCKET ELEMENT REMOVED
						P_.removeEdge(bucket.get(m));
					} else {
						LOGGER.trace("A"); // THIS ONE MUST STAY
					}
				}
			}
			i -= (bucket.size() - 1);
			
			
			
				/*
				DiHyperEdge<String, E> edge_ = P_.getArc(F.get(i));
				P_.removeEdge(F.get(i));
				List<E> F_ = findAll( P_, S);
				*/
				/*
				DiHyperGraph<String, E> aux = new DiHyperGraph<String, E> (P_);
				aux.removeEdge(F.get(i));*/
				//aux.removeEdge(F.get(i - 1));
				/*
				List<E> F_ = findAll( aux, S);
				for (E k : F_) Y_union_f.addAll( P.Y(k));
				if ( Y_union_f.containsAll(T_)) {
					P_.removeEdge(F.get(i));
				} 
				*/
				/*else {
					P_.addEdge(edge_);
				}
				*/
			LOGGER.trace(".");
			//}
		}
System.out.print(P_);
System.out.print("\n");
		
		
		//LINK HEAD OF RF TO P ?
		/*
		for (E r : Rf) {
			DiHyperGraph<String, E> aux = new DiHyperGraph<String, E> (P_);
			aux.removeEdge(r);
			F = findAll( aux, S);
			Set<String> Y_union_f = new HashSet<String> ();
			for (E k : F) Y_union_f.addAll( P.Y(k));
			if ( Y_union_f.containsAll(T)) {
				DiHyperGraph<String, E> emptyHPG = new DiHyperGraph<String, E> ();
				return emptyHPG;
			}
		}*/
		
		/*
		for ( int i = 0; i < F.size(); i++) {
			E r = F.get(i);
			if ( ! Rf.contains(r)) {
				DiHyperGraph<String, E> p_ = new DiHyperGraph<String, E> ( P_);
				p_.removeEdge(r);
				List<E> f = findAll(p_, S);
				Set<String> Y_union_f = new HashSet<String> ();
				for (E k : f) Y_union_f.addAll( P.Y(k));
				if ( Y_union_f.containsAll(T)) {
					P_.removeEdge(r);
				}
			}
		}*/
//		this.solutionsMap.put(this.solutionCounter++, BioSynthUtils.fpSolutionToGenericSolution(P_, S, T));
//		if ( this.solutionsMap.size() >= dumpSize) {
//			this.dumper.dumpSolutions();
//		}
		return P_;
	}
}
