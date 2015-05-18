package pt.uminho.sysbio.biosynthframework.algorithm.hypergraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperEdge;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperGraph;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.ReactionEdge;
import pt.uminho.sysbio.biosynthframework.util.BioSynthUtils;

public class DefaultFindPathImpl<E> extends AbstractFindPathKernel<E> {
	
	private static Logger LOGGER = LoggerFactory.getLogger(DefaultFindPathImpl.class);
	
	public static double fpTime = 0;
	public static double faTime = 0;
	
	public Set<Set<String>> DEBUG_S_LIST = new HashSet<Set<String>> ();
	
	public static boolean TRACE = false;
	public static boolean VERBOSE = false;
	public static final boolean DEBUG_LIMIT = false;
	public static int SOLUTION_COUNT = 0;
	public static int LIMIT = Integer.MAX_VALUE;

	private int maxDepth;
	
//	private SolutionDumper dumper = null;
	private DiHyperGraph<String, E> baseGraph = null;
	private Set<String> T = null;
	private Set<String> S = null;
	 List<DiHyperGraph<String, E>> solutions = null;
	
	private int solutionCounter;
//	private Map<Integer, MSolution> solutionsMap = new HashMap<Integer, MSolution> ();
//	private Map<Integer, Timestamp> timestamps = new HashMap<> ();
	private boolean runtimeBuild;
	
	private int dumpSize = Integer.MAX_VALUE;
	public static int nSolutions = 0;
	
	public DefaultFindPathImpl( DiHyperGraph<String, E> graph, String[] T, String[] S) {
		this.baseGraph = graph;
		this.T = new TreeSet<> (Arrays.asList(T));
		this.S = new TreeSet<> (Arrays.asList(S));
		
		this.maxDepth = Integer.MAX_VALUE;
		this.runtimeBuild = false;
	}
	public DefaultFindPathImpl( DiHyperGraph<String, E> graph, Set<String> T, Set<String> S) {
		this.baseGraph = graph;
		this.T = new TreeSet<> (T);
		this.S = new TreeSet<> (S);
		
		this.maxDepth = Integer.MAX_VALUE;
		this.runtimeBuild = false;
	}
	public DefaultFindPathImpl( DiHyperGraph<String, E> graph, String[] T, String[] S, int bucket_size) {
		this.baseGraph = graph;
		this.T = new HashSet<String> (Arrays.asList(T));
		this.S = new HashSet<String> (Arrays.asList(S));
		
		this.maxDepth = Integer.MAX_VALUE;
//		this.bucket_size = bucket_size;
		this.runtimeBuild = false;
	}
	
	public Set<Set<String>> getSolutions_() {
		
		Set<Set<String>> pathways = new HashSet<> ();
		
		for (DiHyperGraph<String, E> solution : solutions) {
			Set<String> pathway = new HashSet<> ();
			for (DiHyperEdge<String, E> e : solution.getArcs()) {
//				System.out.println(e.getClass().getSimpleName());
				pathway.add(e.getBody().toString());
			}
			pathways.add(pathway);
		}
		
		return pathways;
	}
	
	@Override
	public void solve() {
		 DiHyperGraph<String, E> aux = new DiHyperGraph<String, E>(baseGraph);
		 Set<E> Rf = new HashSet<E>();
		 Set<E> Rn = new HashSet<E>();
		 
		 //this.solutionsMap = new HashMap<Integer, MSolution> ();
		 this.solutionCounter = 0;
		 
//		 if ( this.dumper != null) {
//			 this.dumpSize = this.dumper.getDumpSize();
//			 this.dumper.setSolutionsToDump(this.solutionsMap);
//			 this.dumper.initializeSet();
//		 }
		 
		 long start = System.currentTimeMillis();
		 
		 solutions = this.findPath(aux, Rf, Rn, T, S);
		 
		 fpTime = System.currentTimeMillis() - start;
		 
		 System.out.println(fpTime);
		 System.out.println(faTime);
	}
	
	public int getSolutionsNumber() {
		return this.solutions.size();
	}
	
	public Set<String> findSupp( DiHyperGraph<String, E> H, Set<String> S, Set<String> T) {
		Set<String> D = new HashSet<String> ();
		Set<String> W = new HashSet<String> (T);
		
		while ( !D.containsAll(W)) {
			Set<String> W_ = new HashSet<String> (W);
			W_.removeAll(D);
			if ( W_.isEmpty()) System.err.println( "ERROR");
			String i = W_.iterator().next();
			D.add(i);
			Set<String> aux = new HashSet<String> ();
			for ( E r : H.getEdges()) {
				if ( H.Y(r).contains(i)) {
					Set<String> aux_ = new HashSet<String> (H.X(r));
					aux_.removeAll(S);
					aux_.removeAll(D);
					aux.addAll(aux_);
				}
			}
			W.addAll(aux);
		}
		
		List<E> F  = findAll.findAll(H, S);
		Set<String> D_ = new HashSet<String> ();
		D_.addAll(S);
		for ( E e : F) {
			D_.addAll( H.Y(e));
		}
		D.removeAll(D_);
		
		return D;
	}
	
	public Set<String> findBootstraps( DiHyperGraph<String, E> H, Set<String> sources) {
		Set<String> B = new HashSet<String> ();
		
		List<E> F = findAll.findAll(H, sources);
		Set<String> D = new HashSet<String> (sources);
		for (E e : F) D.addAll( H.Y(e));
		DiHyperGraph<String, E> H_ = new DiHyperGraph<String, E> ();
		
		for ( E r : H.getEdges()) {
			Set<String> in = new HashSet<String> ( H.X(r));
			in.removeAll(D);
			Set<String> out = new HashSet<String> ( H.Y(r));
			out.removeAll(D);
			if ( !out.isEmpty()) {
				DiHyperEdge<String, E> r_ = new DiHyperEdge<String, E> ( in, out, r);
				H_.addEdge(r_);
			}
		}
		String v;
		Set<E> toRemove = new HashSet<E> ();
		while ( (v = existVinH(H_)) != null) {
			toRemove.clear();
			for (E e : H_.getEdges()) {
				DiHyperEdge<String, E> r_ = H_.getArc(e);
				if ( r_.contains(v)) {
					r_.outLinks().remove(v);
					if ( r_.outLinks().isEmpty() || r_.inLinks().contains(v)) {
						toRemove.add(e);
						
					}
				}
			}
			for (E e : toRemove) {
				H_.removeEdge(e);
			}
		}
		
		for ( E r_ : H_.getEdges()) {
			B.addAll( H_.Y(r_));
		}
		
		return B;
	}
	
	
	
	private String existVinH(DiHyperGraph<String, E> H_) {
		Set<String> ret = new HashSet<String> ();
		
		for ( E r : H_.getEdges()) {
			ret.addAll(H_.Y(r));
		}
		for ( E r : H_.getEdges()) {
			ret.removeAll(H_.X(r));
		}
		
		if (ret.isEmpty()) return null;
		
		String v = ret.iterator().next();
		
		return v;
	}

	public final List<E> findAll( DiHyperGraph<String, E> H, Set<String> sources, String target) {
		
//		try {
//			Thread.sleep(1);
//		} catch (InterruptedException iEx) {
//			
//		}
		
		
		long start = System.currentTimeMillis();
		
		Map< E, Set<String>> x = new HashMap<E, Set<String>> ();
		Map< String, Set<E>> rev_x = new HashMap<String, Set<E>> ();
		for ( E r : H.getEdges()) {
			Set<String> HX = new HashSet<String> ( H.X(r));
			x.put(r, HX);
			for (String i : H.X(r)) {
				if ( !rev_x.containsKey(i)) {
					rev_x.put(i, new HashSet<E> ());
				}
				rev_x.get(i).add(r);
			}
		}
		LinkedList<String> V = new LinkedList<String> (sources);
		Set<String> D = new HashSet<String> (sources);
		LinkedList<E> F = new LinkedList<E>();
		while ( !V.isEmpty()) {
			String i = V.getFirst();
			//V i = V.iterator().next();
			V.remove(i);
			D.add(i);
			if ( rev_x.containsKey(i)) {
				for ( E r : rev_x.get(i)) {
					x.get(r).remove(i);
					if ( x.get(r).isEmpty()) {
						if ( !F.contains(r)) {
							F.addLast(r);
						}
						Set<String> j = new HashSet<String> (H.Y(r));
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
		List<E> ret = F;
		DiHyperGraph<String, E> H_ = new DiHyperGraph<>();
		for (E e : ret) {
			H_.addEdge(H.getArc(e));
		}
		boolean testAgain = true;
		while (testAgain) {
			testAgain = false;
			List<E> toRemove = new ArrayList<> ();
			for (E e : ret) {
				DiHyperEdge<String, E> arc = H.getArc(e);
				Set<String> products = arc.outLinks();
				if (!products.contains(target)) {
					int sumInDegree = 0;
//					for (String p : products) sumInDegree += H_.inDegree(p);
					if (sumInDegree == 0) toRemove.add(e);
				}
			}
			if (!toRemove.isEmpty()) {
				testAgain = true;
				ret.removeAll(toRemove);
				for (E e : toRemove) {
					H_.removeEdge(e);
				}
			}
		}
		faTime += System.currentTimeMillis() - start;
//		System.out.println("FA TOOK: " + faTime);
		return ret;
	}
	
	public boolean testAndRemove( DiHyperGraph<String, E> P, DiHyperGraph<String, E> H, List<E> bucket) {
		/*
		DiHyperGraph<String, E> aux = new DiHyperGraph<String, E> (H);
		for (int i = 0; i < bucket.size(); i++) {
			aux.removeEdge( bucket.get(i));
		}
		List<E> F_ = findAll( aux, S);
		Set<String> Y_union_f = new HashSet<String> ();
		for (E k : F_) Y_union_f.addAll( P.Y(k));
		if ( Y_union_f.containsAll(T_)) {
			P_.removeEdge(F.get(i));
		} */
		return false;
	}
	
	public void dummy() {
		
	}
	
	public DiHyperGraph<String, String> minimizeFast() {
		return null;
	}
	

	


	
//	public Map<Integer, MSolution> getSolutions() {
//		return this.solutionsMap;
//	}
//	
//	public void setDumper(SolutionDumper dumper) {
//		if ( dumper != null) {
//			this.dumper = dumper;
//			this.runtimeBuild = true;
//		}
//	}
	
	
	private void test( DiHyperGraph<String, E> H, Set<E> Rn) {
		Set<E> err = new HashSet<E> ();
		for ( E e : H.getEdges()) {
			if ( Rn.contains(e)) {
				//System.out.println( "###############ERROR RN -> " + e);
				err.add(e);
			}
		}
		for ( E e : err) H.removeEdge(e);
	}
	
	public final List<DiHyperGraph<String, E>> findPath( DiHyperGraph<String, E> H, 
			Set<E> Rf, Set<E> Rn, Set<String> T, Set<String> S) {

		LOGGER.trace("FINDPATH BEGIN: " + Rf + " :: " + Rn);

		Set<E> Rn_ = Rn;
		test(H, Rn);
		List<E> F = findAll.findAll(H, S);
		
		LOGGER.trace("FINDALL SIZE: " + F.size());

		Set<E> F_union_Rf = new HashSet<E> (F);
		F_union_Rf.addAll( Rf);
		DiHyperGraph<String, E> H_ = new DiHyperGraph<String, E>();
		for ( E e : F_union_Rf) {
			H_.addEdge( this.baseGraph.getArc(e));
		}
		
	    //DiHyperGraph<String, E> P = minimize( H_, Rf, T, S);
		
		DiHyperGraph<String, E> P = null;
		
		P = minimize.minimize( H_, Rf, T, S);
		LOGGER.info("{} - {}", nSolutions, LIMIT);
//		if (nSolutions > LIMIT) P = null;
//		P.setTimestamp(BioSynthUtils.getTimestamp());
		if ( P == null) return new ArrayList<DiHyperGraph<String,E>> ();
		
if (VERBOSE) System.out.println( "\tMinimize: " + P);
		List<DiHyperGraph<String, E>> En = new ArrayList<DiHyperGraph<String, E>>();
		//int index = 0;
		if ( !P.isEmpty()) {
//if (VERBOSE) System.out.println( P.getEdges());
			En.add(P);
			
			F = findAll.findAll(P, S);
if (VERBOSE) System.out.println(F);
if (VERBOSE) System.out.println("\tFindAll " + F);
			boolean clone = true;
			for (int i = (F.size() - 1); i >= 0; i--) {
				E r = F.get(i);
				if ( !Rf.contains(r)) {
if (VERBOSE) System.out.println( "\tTest Without " + r);
					H.removeEdge(r);
					Rn_.add(r);
					DiHyperGraph<String, E> aux = new DiHyperGraph<String, E> ( H);
					Set<E> rf_ = new HashSet<E> (Rf);
					//Set<E> rn_ = new HashSet<E> (Rn);
					En.addAll(findPath( aux, rf_, Rn_, T, S));
//					nSolutions = En.size();
					Rf.add(r);
					if (clone) {
						Set<E> rr = new HashSet<E> (Rn_);
						Rn_ = rr;
						clone = false;
					}
				}
			}


			/*
			//index++;
			F = findAll(P, S);
			//System.out.println( F);
if (VERBOSE) System.out.println("FindAll " + F);
			//for (int i = 0; i < F.size(); i++) {
			//FIND SISTER NODES
			int i = (F.size() - 1);
			E r = F.get(i);
			if ( !Rf.contains(r)) {
if (VERBOSE) System.out.println( "Test Without " + r);
				H.removeEdge(r);
				Set<E> rf_ = new HashSet<E> (Rf);
				En.addAll(findPath( H, rf_, T, S, null));
				Rf.add(r);
if (VERBOSE) System.out.println( "RF ::" + Rf);
			}
			i--;
			for ( ; i >= 0; i--) {
				r = F.get(i);
				if ( !Rf.contains(r)) {
if (VERBOSE) System.out.println( "Test Without " + r);
					//H.removeEdge(r);
					DiHyperGraph<String, E> aux = new DiHyperGraph<String, E> ( H);
                    aux.removeEdge(r);
					Set<E> rf_ = new HashSet<E> (Rf);
					En.addAll(findPath( aux, rf_, T, S, null));
					Rf.add(r);
if (VERBOSE) System.out.println( "RF ::" + Rf);
				}
			}
			
			*/
		}
		//System.out.println(En.size());
if (VERBOSE) System.out.println( "FINDPATH END: " + En);
		return En;
	}
	
	public int getMaxDepth() {
		return maxDepth;
	}
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	
	public boolean isRuntimeBuild() {
		return runtimeBuild;
	}
	public void setRuntimeBuild(boolean runtimeBuild) {
		this.runtimeBuild = runtimeBuild;
	}
	public List<DiHyperGraph<String, E>> getSolutions() {
		return solutions;
	}
	
//	@Override
//	public SolutionSetNew getSolutionSet() {
//		//if ( this.isRuntimeBuild()) return this.solutionsMap;
//		
//		SolutionSetNew solutionSet; 
//		if ( this.isRuntimeBuild()) {
//			solutionSet = new SolutionSetNew( BioSynthUtils.getUUID(), "FindPath --rfLimit " + this.maxDepth, this.solutionsMap.size());
//			for (Integer solutionId : solutionsMap.keySet()) {
//				MSolution solution = solutionsMap.get(solutionId);
////				solution.setCreated_at(BioSynthUtils.timeStamp());
//				if ( solution != null) {
//					solutionSet.addSolution(solution.getId(), solution);
//				} else {
//					System.err.println("solution is null in fp ...");
//				}
//			}
//		} else {
//			solutionSet = new SolutionSetNew( BioSynthUtils.getUUID(), "FindPath --rfLimit " + this.maxDepth, this.solutions.size());
//			int index = 0;
//			for (DiHyperGraph<String, E> fpSolution : solutions) {
//				MSolution solution = BioSynthUtils.fpSolutionToGenericSolution(fpSolution, S, T);
//				solution.setId(index);
//				solutionSet.addSolution(solution.getId(), solution);
//				index++;
//			}
//		}
//
//		return solutionSet;
//	}
	
	
	
//	@Override
//	public Map<Integer, MSolution> getSolutionMap() {
//		return this.solutionsMap;
//	}
//
//	@Override
//	public void setSolutionMap(Map<Integer, MSolution> map) {
//		this.solutionsMap = map;
//	}
}
