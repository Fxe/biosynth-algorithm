package pt.uminho.sysbio.biosynthframework.algorithm.processgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import pt.uminho.sysbio.biosynthframework.core.components.optimization.Solution;
import pt.uminho.sysbio.biosynthframework.core.components.optimization.SolutionSet;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.pgraph.OperatingUnit;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.pgraph.ProcessGraph;

public class OriginalSolutionStructureGenerationImpl<T> implements SolutionStructureGeneration<T> {

	public static boolean VERBOSE = false;
	
	public int counter = 0;
	
	private Set<T> P_;
	private Set<T> R_;
	private Set<T> M_;
	
	public static int MAX_DEPTH = Integer.MAX_VALUE;
	
	private DecisionMapping<T> deltaMap_;
	
	private final boolean power_set_block;
	
	private Map< Integer, Map< T, Set< OperatingUnit<T>>>> solutionStructures_ =
			new HashMap<Integer, Map<T,Set<OperatingUnit<T>>>> ();
	
	private boolean runtimeBuild;
	private int solutionCounter;
//	private Map<Integer, ISolution> solutionMap;
	private Map<Integer, DateTime> timestamps = new HashMap<> ();
	
	public OriginalSolutionStructureGenerationImpl( ProcessGraph<T> problem) {

		this.P_ = problem.getP();
		this.R_ = problem.getR();
		this.M_ = problem.getM();
		this.deltaMap_ = new DecisionMapping<T>( this.M_, problem.getO());
		this.power_set_block = false;
		this.runtimeBuild = false;
	}
	
	public OriginalSolutionStructureGenerationImpl( ProcessGraph<T> problem, boolean fullPowerSet) {
		if (VERBOSE) System.out.println( "SSG_PURE SETUP ...");
		System.out.println(problem.getO().size());
		this.P_ = problem.getP();
		this.R_ = problem.getR();
		this.M_ = problem.getM();
		this.deltaMap_ = new DecisionMapping<T>( this.M_, problem.getO());
		
		this.power_set_block = !fullPowerSet;
		this.runtimeBuild = false;
		if (VERBOSE) System.out.println( "SSG_PURE SETUP COMPLETE ...");
	}
	
	@Override
	public void solve() {
		if (VERBOSE) System.out.println( "P:" + this.P_);
		if (VERBOSE) System.out.println( "R:" + this.R_);
		if ( P_.isEmpty() ) return;
		this.solutionStructures_.clear();
		
		this.solutionCounter = 0;

		this.SSG_CORE(P_, new HashSet<T>(), new HashMap< T, Set< OperatingUnit<T>>>(), 0);
		
		if (VERBOSE) System.out.println( this.solutionCounter);
	}
	
	public void solve( Map< T, Set< OperatingUnit<T>>> deltam) {
		if ( P_.isEmpty() ) return;
		this.solutionStructures_.clear();
		Map< T, Set< OperatingUnit<T>>> deltaMove = new HashMap<T, Set<OperatingUnit<T>>> (deltam);
		this.SSG_MOVE( new HashSet<T>(P_), new HashSet<T>(), new HashMap< T, Set< OperatingUnit<T>>>(), deltaMove);
	}
	
	public void solve( Set< OperatingUnit<T>> op_set) {
		if ( P_.isEmpty() ) return;
		this.solutionStructures_.clear();
		Set< OperatingUnit<T>> o_set = new HashSet< OperatingUnit<T>> (op_set);
		this.SSG_MOVE( new HashSet<T>(P_), new HashSet<T>(), new HashMap< T, Set< OperatingUnit<T>>>(), o_set);
	}
	
	private void SSG_MOVE( Set<T> p, Set<T> m, Map< T, Set< OperatingUnit<T>>> deltam, Set< OperatingUnit<T>> op_seq) {
		
		while ( !op_seq.isEmpty()) {
			if ( p.isEmpty() ) {
				System.out.println( deltam);
				this.solutionStructures_.put(this.solutionCounter, new HashMap< T, Set<OperatingUnit<T>>> (deltam));
				this.solutionCounter++;
				System.out.println("error1");
				return;
			}
			
			T x = p.iterator().next();
			
			Set< OperatingUnit<T>> maximalDeltaX = new HashSet<OperatingUnit<T>> (this.deltaMap_.getDeltaMap(x));
			maximalDeltaX.retainAll(op_seq);
			
			if ( maximalDeltaX.isEmpty()) {
				System.out.println("error2");
				return;
			}
			op_seq.removeAll( maximalDeltaX);
			deltam.put(x, new HashSet<OperatingUnit<T>> (maximalDeltaX));
			Set<T> aux = new HashSet<T> ( this.R_);
			aux.addAll(m);                         
			aux.add(x);     
			p.addAll( SetMap.psi_minus(maximalDeltaX));
			p.removeAll(aux);  
			m.add(x);	
		}
		
		System.out.println( "P" + p);
		System.out.println( "M" + m);
		System.out.println( "d" + deltam);
		
		this.SSG_CORE( p, m, deltam, 0);
	}
	
	private void SSG_MOVE( Set<T> p, Set<T> m, Map< T, Set< OperatingUnit<T>>> deltam, Map< T, Set< OperatingUnit<T>>> deltaMove) {
		//System.out.println( "p:" + p + " m:" + m + " d[m]" + deltam);
		
		while ( !deltaMove.isEmpty() ) {
			if ( p.isEmpty() ) {
				System.out.println( deltam);
				this.solutionStructures_.put(this.solutionCounter, new HashMap< T, Set<OperatingUnit<T>>> (deltam));
				this.solutionCounter++;
				return;
			}
			
			T x = p.iterator().next();
			Set< OperatingUnit<T>> c = deltaMove.get(x);
			deltam.put(x, new HashSet<OperatingUnit<T>> (c));
			Set<T> aux = new HashSet<T> ( this.R_);
			aux.addAll(m);                         
			aux.add(x);     
			p.addAll( SetMap.psi_minus(c));
			p.removeAll(aux);  
			m.add(x);
			deltaMove.remove(x);
		}
		
		System.out.println( "P" + p);
		System.out.println( "M" + m);
		System.out.println( "d" + deltam);
		
		this.SSG_CORE( p, m, deltam, 0);
			//m.add(x);                               // m U {x}
			//SSG( (p U matin(C)) \ (R U m U {x}), m U {x}, deltaM[ m U {x}]
			//this.SSG_MOVE( _p, _m, _deltam, deltaMove);
	}
	
	private void SSG_CORE( Set<T> p, Set<T> m, Map< T, Set< OperatingUnit<T>>> deltam, int depth) {
		depth++;
//		System.out.println(p + " " + deltam);
		if ( depth > MAX_DEPTH) {
			return;
		}
		
System.out.println( "p:" + p + " m:" + m + " d[m]" + deltam);
		if ( p.isEmpty() ) {
//System.out.println(deltam);
			this.solutionStructures_.put(this.solutionCounter, new HashMap< T, Set<OperatingUnit<T>>> (deltam));
			this.timestamps.put(this.solutionCounter, new DateTime());
			counter++;
			
			if ( this.runtimeBuild) {
//				ISolution solution = BioSynthUtils.ssgSolutionToGenericSolution(deltam, R_, P_);
//				solution.setId( this.solutionCounter);
//				this.solutionMap.put( this.solutionCounter, solution);
//				if ( this.solutionMap.size() >= this.dumpSize) {
//					this.dumper.dumpSolutions();
//					this.solutionStructures_.clear();
//				}
			}
			
			this.solutionCounter++;
//			System.out.println(this.solutionCounter);
			return;
		}
		
		// let x be a element of p ( x < p) "< belongs to"
		T x = p.iterator().next();

		
		Set< Set< OperatingUnit<T>>> _C = new HashSet<Set<OperatingUnit<T>>> ();
		Set< OperatingUnit<T>> xRemMap = new HashSet<OperatingUnit<T>> (this.deltaMap_.getDeltaMap(x));
//System.out.println("DELTA MAX:" + xRemMap);
		Set< OperatingUnit<T>> xOp = new HashSet<OperatingUnit<T>>();
		for ( Set<OperatingUnit<T>> o_l : deltam.values()) {
			for ( OperatingUnit<T> op : o_l) {
				if ( op.getOpposite() != null) {
					xOp.add(op.getOpposite());
				}
			}
		}
//System.out.println("OP       :" + deltam.values());
//System.out.println("XOP       :" + xOp);
		xRemMap.removeAll(xOp);
//System.out.println("XDELTA MAX:" + xRemMap);
		
		for ( int i = 1; i <= xRemMap.size(); i++) {
			Set< Set< OperatingUnit<T>>> C = SetMap.powerSet( xRemMap, i);
			
			Set< OperatingUnit<T>> complement_union = new HashSet<OperatingUnit<T>>();
			Set< OperatingUnit<T>> delta_union = new HashSet<OperatingUnit<T>>();
			for ( T y : m) {
				complement_union.addAll( this.deltaMap_.getDecisionMappingComplement( y, deltam.get(y)));
				delta_union.addAll( deltam.get(y));
			}
			for ( Set< OperatingUnit<T>> c : C) {
				if ( this.test( deltam, m, x, c)) {
					_C.add(c);
				}
			}
			if ( this.power_set_block && !_C.isEmpty()) break;
		}
		
//if ( _C.isEmpty()) System.out.println( "DEAD END");
		for ( Set< OperatingUnit<T>> c : _C) {
			Map< T, Set< OperatingUnit<T>>> _deltam = new HashMap<T, Set<OperatingUnit<T>>> (deltam);
			Set<T> _p = new HashSet<T> (p);
			Set<T> _m = new HashSet<T> (m);
			
			//deltaMap[ m U {x}] = deltaMap[ m ] U {x,c}
			_deltam.put(x, new HashSet<OperatingUnit<T>> (c));
			//SSG( (p U matin(C)) \ (R U m U {x}), m U {x}, deltaM[ m U {x}]
			Set<T> aux = new HashSet<T> ( this.R_); // aux = (R U m U {x})
			aux.addAll(m);                          // aux = (R U m U {x})
			aux.add(x);                             // aux = (R U m U {x})
			_p.addAll( SetMap.psi_minus(c));         // (p U matin(C))
			_p.removeAll(aux);                       // p \ aux
			
			//Set<T> _p = new HashSet<T> (p);
			_m.add(x);
			//m.add(x);                               // m U {x}
			//SSG( (p U matin(C)) \ (R U m U {x}), m U {x}, deltaM[ m U {x}]
			
			this.SSG_CORE( _p, _m, _deltam, depth);
		}

	}
	
	
	
	public boolean isRuntimeBuild() {
		return runtimeBuild;
	}
	public void setRuntimeBuild(boolean runtimeBuild) {
		this.runtimeBuild = runtimeBuild;
	}

//	public void setDumper(SolutionDumper dumper) {
//		this.dumper = dumper;
//	}

	public void setMaxDepth( int i ) {
		MAX_DEPTH = i;
	}
	
	public boolean test( Map< T, Set< OperatingUnit<T>>> deltam, Set<T> m, T x, Set< OperatingUnit<T>> c) {
		Set< OperatingUnit<T>> maximalDelta = new HashSet<OperatingUnit<T>> (this.deltaMap_.getDeltaMap(x));
		maximalDelta.removeAll( c);
		// Any y < m
		for ( T y : m) {
			// c & deltaComp(y) = {0}
			
			Set< OperatingUnit<T>> complement = new HashSet<OperatingUnit<T>>
				(this.deltaMap_.getDecisionMappingComplement(y, deltam.get(y)));
			if (complement.removeAll(c)) return false;
			
			// (delta(x)\c) & deltam(y) = {0}

			if (maximalDelta.removeAll(deltam.get(y))) {
				//System.out.println( "Test Failed !");
				return false;
			}
		}
		
		return true;
	}
	
	
	public Map<Integer, Map< T, Set< OperatingUnit<T>>>> getSolutionStructures() {
		return this.solutionStructures_;
	}
	
////	@Override
//	public Map<Integer, ISolution> getSolutionMap() {
//		return this.solutionMap;
//	}
//	
////	@Override
//	public void setSolutionMap( Map<Integer, ISolution> map) {
//		this.solutionMap = map;
//	}

//	@Override
	public<S extends Solution> SolutionSet<S> getSolutionSet(SolutionSet<S> emptySolutionSet) {
//		ISolutionSet solutionSet = new SolutionSetNew( BioSynthUtils.getUUID(), "SolutionStructureGeneration --delta " + !this.power_set_block, this.solutionStructures_.size());
//		for (Integer solutionId : this.solutionStructures_.keySet()) {
//			Map< T, Set< OperatingUnit<T>>> deltam = this.solutionStructures_.get(solutionId);
//			solutionSet.addSolution(solutionId, BioSynthUtils.ssgSolutionToGenericSolution(deltam, R_, P_));
//			solutionSet.getSolution(solutionId).setCreated_at(timestamps.get(solutionId));
//		}
//		return solutionSet;
		return null;
	}
	
}
