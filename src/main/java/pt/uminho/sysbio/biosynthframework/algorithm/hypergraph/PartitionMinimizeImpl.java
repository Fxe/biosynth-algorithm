package pt.uminho.sysbio.biosynthframework.algorithm.hypergraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperEdge;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperGraph;


public class PartitionMinimizeImpl<E> extends AbstractMinimizeKernel<E> {
	private static Logger LOGGER = LoggerFactory.getLogger(PartitionMinimizeImpl.class);
	private int bucket_size = 1;
	
	@Override
	public final DiHyperGraph<String, E> minimize( DiHyperGraph<String, E> P, Set<E> Rf, Set<String> T, Set<String> S) {
		if (DefaultFindPathImpl.nSolutions > DefaultFindPathImpl.LIMIT) return null;
//		if (TRACE) System.out.print( Rf.size() + "##");
//		if ( Rf.size() > maxDepth) return null;
		Set<String> T_ = new HashSet<String> (T);
		for (E edge : Rf) {
			DiHyperEdge<String, E> hyperEdge = P.getArc(edge);
			T_.addAll(hyperEdge.inLinks());
		}
		this.dummy();
		// 1. F <- FindAll(P, S)
		List<E> F = this.findAll.findAll(P, S); //this.findAll(P, S);
		// 2. P' <- P
		DiHyperGraph<String, E> P_ = new DiHyperGraph<String, E> ( P);
		
		// Uk Y(Fk)
		Set<String> Y_union_F = new HashSet<String> ();
		for (E r : F) {
			Y_union_F.addAll( P.Y(r));
		}
		// IF T ~C Uk Y(Fk)
		if ( !Y_union_F.containsAll(T_)) {
			DiHyperGraph<String, E> emptyHPG = new DiHyperGraph<String, E> ();
			return emptyHPG;
		}
		//ELSE
		
		//r in P
		F.clear();
		F.addAll(P_.getEdges());
		for (E e : Rf) {
			F.remove(e);
		}
		// F = all r in P_ and not int Rf
		/*
		int i;
		int middle = F.size() / 2;
		List<E> bucket = new ArrayList<E> ();
		for (i = 0; i < middle; i++) {
			bucket.add( F.get(i));
		}*/
		partition(F, P_, S, T_);
		
		/*
		//for (int i = (F.size() - 1); i >= 0; i--) {
		for (int i = 0; i < F.size(); i++) {
//System.out.println("IIII" + F.get(i));
			//if ( !Rf.contains(F.get(i))) {
			List<E> bucket = new ArrayList<E> ();
			//System.out.println("BUCKET");
			for (int j = i; j < F.size() && bucket.size() < bucket_size; j++) {
				bucket.add(F.get(j));
				//System.out.println("b_size :" + bucket.size());
			}
			//System.out.println( "bucket:" + bucket);
			
			
			//TRY TO REMOVE ENTIRE BUCKET
			DiHyperGraph<String, E> aux_b = new DiHyperGraph<String, E> (P_);
			for (int m = 0; m < bucket.size(); m++) {
				aux_b.removeEdge( bucket.get(m));
			}
			List<E> F__ = findAll( aux_b, S);
			Set<String> Y_union_f_ = new HashSet<String> ();
			for (E k : F__) Y_union_f_.addAll( P.Y(k));
			if ( Y_union_f_.containsAll(T_)) {
System.out.print("B" + bucket.size()); // ENTIRE BUCKET REMOVED
				for (int m = 0; m < bucket.size(); m++) {
					
					P_.removeEdge(bucket.get(m));
				}
				
			} else { //IF FAIL REMOVE 1 BY 1
				
				for (int m = 0; m < bucket.size(); m++) {
					DiHyperGraph<String, E> aux = new DiHyperGraph<String, E> (P_);
					aux.removeEdge(bucket.get(m));
					List<E> F_ = findAll( aux, S);
					Set<String> Y_union_f = new HashSet<String> ();
					for (E k : F_) Y_union_f.addAll( P.Y(k));
					if ( Y_union_f.containsAll(T_)) {
System.out.print("P"); // BUCKET ELEMENT REMOVED
						P_.removeEdge(bucket.get(m));
					} else {
System.out.print("A"); // THIS ONE MUST STAY
					}
				}
			}
			i += (bucket.size() - 1);
			
if (VERBOSE) System.out.print(".");
			//}
		}*/
		
		if (P_ != null && P_.size() > 1) DefaultFindPathImpl.nSolutions++;
		
		LOGGER.debug("Solution: " + P_.toString());
		return P_;
	}
	
	
	public void dummy() {
		
	}
	
	public void partition(List<E> bucket, DiHyperGraph<String, E> P_, Set<String> S, Set<String> T) {
		if (bucket.size() <= bucket_size) {
			singleTonTest(bucket, P_, S, T);
			return;
		}
		
		DiHyperGraph<String, E> aux_b = new DiHyperGraph<String, E> (P_);
		for (int m = 0; m < bucket.size(); m++) {
			aux_b.removeEdge( bucket.get(m));
		}
		List<E> F = this.findAll.findAll(aux_b, S); //findAll( aux_b, S);
		Set<String> Y_union_f_ = new HashSet<String> ();
		for (E k : F) Y_union_f_.addAll( P_.Y(k));
		if ( Y_union_f_.containsAll(T)) {
//if (TRACE) System.out.print("B" + bucket.size()); // ENTIRE BUCKET REMOVED
			for (int m = 0; m < bucket.size(); m++) {
				P_.removeEdge(bucket.get(m));
			}
			return;
		} else {
			List<E> bucketLeft = new ArrayList<E> ();
			List<E> bucketRigth = new ArrayList<E> ();
			int middle = bucket.size() / 2;
			int i;
			for (i = 0; i < middle; i++) {
				bucketLeft.add(bucket.get(i));
			}
			for (int k = i; k < bucket.size(); k++) {
				bucketRigth.add(bucket.get(k));
			}
//System.out.println("LEFT: " + bucketLeft.size() + " RIGHT: " + bucketRigth.size() + " BUCKET: " + bucket.size());
			partition(bucketLeft, P_, S, T);
			partition(bucketRigth, P_, S, T);
		}
	}
	
	public void singleTonTest(List<E> bucket, DiHyperGraph<String, E> P_, Set<String> S, Set<String> T) {
		for (int m = 0; m < bucket.size(); m++) {
			DiHyperGraph<String, E> aux = new DiHyperGraph<String, E> (P_);
			aux.removeEdge(bucket.get(m));
			List<E> F = this.findAll.findAll(aux, S); //findAll( aux, S);
			Set<String> Y_union_f = new HashSet<String> ();
			for (E k : F) Y_union_f.addAll( P_.Y(k));
			if ( Y_union_f.containsAll(T)) {
//if (TRACE) System.out.print("P"); // BUCKET ELEMENT REMOVED
				P_.removeEdge(bucket.get(m));
			} else {
//if (TRACE) System.out.print("A"); // THIS ONE MUST STAY
			}
		}
	}
}
