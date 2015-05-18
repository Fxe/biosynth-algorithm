package pt.uminho.sysbio.biosynthframework.algorithm.processgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.pgraph.OperatingUnit;

public class DecisionMapping<T> {
	private Set<T> M_;
	private Set< OperatingUnit<T>> O_;
	private Map< T, Set< OperatingUnit<T>>> maximalDecisionMapping_;
	
	public DecisionMapping( Set<T> M, Set< OperatingUnit<T>> O) {
		this.M_ = M;
		this.O_ = O;
		this.maximalDecisionMapping_ = new HashMap< T, Set<OperatingUnit<T>>> ();
		for ( T m : M_) {
			this.maximalDecisionMapping_.put(m, SetMap.phi_minus(m, O_));
		}
		
	}
	
	public int size() {
		return this.O_.size();
	}
	
	public Set< OperatingUnit<T>> getDeltaMap(T x) {
		return this.maximalDecisionMapping_.get(x);
	}
	
	public Set< OperatingUnit<T>> getDecisionMappingComplement( T y, Set< OperatingUnit<T>> deltam) {
		HashSet< OperatingUnit<T>> complement = new HashSet<> ( this.getDeltaMap(y));
		complement.removeAll(deltam);
		return complement;
	}
	
	@Override
	public String toString() {
		return maximalDecisionMapping_.toString();
	}
}
