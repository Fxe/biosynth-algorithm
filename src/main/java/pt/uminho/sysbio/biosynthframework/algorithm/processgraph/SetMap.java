package pt.uminho.sysbio.biosynthframework.algorithm.processgraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.pgraph.OperatingUnit;

public class SetMap {
	
	public static<T> Set<T> psi_minus( Set< OperatingUnit<T>> opunits) {
		Set<T> psi_m = new HashSet<T>();
		for ( OperatingUnit<T> op : opunits) {
			psi_m.addAll( op.getAlpha());
		}
		return psi_m;
	}
	
	public static<T> Set<T> psi_plus( Set< OperatingUnit<T>> opunits) {
		Set<T> psi_p = new HashSet<T>();
		for ( OperatingUnit<T> op : opunits) {
			psi_p.addAll( op.getBeta());
		}
		return psi_p;
	}
	
	public static<T> Set<T> psi( Set< OperatingUnit<T>> opunits) {
		Set<T> psi = new HashSet<T>();
		psi.addAll(SetMap.psi_minus( opunits));
		psi.addAll(SetMap.psi_plus( opunits));
		return psi;
	}
	
	public static<T> Set< OperatingUnit<T>> phi_minus( Set<T> materials, Set< OperatingUnit<T>> opunits) {
		Set< OperatingUnit<T>> phi_m = new HashSet<OperatingUnit<T>> ();
		for ( OperatingUnit<T> op : opunits) {
			Set<T> aux_ = new HashSet<> (materials);
			aux_.retainAll(op.getBeta());
			if ( aux_.size() > 0 ) {
				phi_m.add(op);
			}
		}
		return phi_m;
	}
	
	public static<T> Set< OperatingUnit<T>> phi_plus( Set<T> materials, Set< OperatingUnit<T>> opunits) {
		Set< OperatingUnit<T>> phi_p = new HashSet<OperatingUnit<T>> ();
		for ( OperatingUnit<T> op : opunits) {
			Set<T> aux_ = new HashSet<> (materials);
			aux_.retainAll(op.getAlpha());
			if ( aux_.size() > 0 ) {
				phi_p.add(op);
			}
		}
		return phi_p;
	}
	
	public static<T> Set< OperatingUnit<T>> phi( Set<T> materials, Set< OperatingUnit<T>> opunits) {
		Set< OperatingUnit<T>> phi = new HashSet<OperatingUnit<T>> ();
		phi.addAll( SetMap.phi_minus(materials, opunits));
		phi.addAll( SetMap.phi_plus(materials, opunits));

		return phi;
	}
	
	public static<T> Set< OperatingUnit<T>> phi_plus( T x, Set< OperatingUnit<T>> opunits) {
		Set< OperatingUnit<T>> phi_p = new HashSet<OperatingUnit<T>> ();
		for ( OperatingUnit<T> op : opunits) {
			if ( op.getAlpha().contains(x) ) {
				phi_p.add(op);
			}
		}
		return phi_p;
	}
	
	public static<T> Set< OperatingUnit<T>> phi_minus( T x, Set< OperatingUnit<T>> opunits) {
		Set< OperatingUnit<T>> phi_m = new HashSet<OperatingUnit<T>> ();
		for ( OperatingUnit<T> op : opunits) {
			if ( op.getBeta().contains(x) ) {
				phi_m.add(op);
			}
		}
		return phi_m;
	}

	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
	    Set<Set<T>> sets = new HashSet<Set<T>>();
	    if (originalSet.isEmpty()) {
	    	sets.add(new HashSet<T>());
	    	return sets;
	    }
	    List<T> list = new ArrayList<T>(originalSet);
	    T head = list.get(0);
	    Set<T> rest = new HashSet<T>(list.subList(1, list.size())); 
	    for (Set<T> set : powerSet(rest)) {
	    	Set<T> newSet = new HashSet<T>();
	    	newSet.add(head);
	    	newSet.addAll(set);
	    	sets.add(newSet);
	    	sets.add(set);
	    }		
	    return sets;
	}
	public static <T> Set<Set<T>> powerSet(Set<T> originalSet, int level) {
		Set<Set<T>> pw = powerSet(originalSet);
		Set<Set<T>> filter = new HashSet<> ();
		
		for (Set<T> subset : pw) {
			if (subset.size() != level) filter.add(subset);
		}
		
		pw.removeAll(filter);
		return pw;
	}

	public static<T> Set< Set<OperatingUnit<T> > > powerSet_( Set< OperatingUnit<T>> opunits, int level) {
		Set< Set<OperatingUnit<T>>> powerSet = new HashSet< Set<OperatingUnit<T>>> ();
		
		if ( level == 1) {
			//DO SINGLETONS
			for ( OperatingUnit<T> op : opunits) {
				HashSet<OperatingUnit<T>> singleton = new HashSet<OperatingUnit<T>>();
				singleton.add( op);
				powerSet.add( singleton);
			}
		}
		
		if ( level > 1) {
			ArrayList<OperatingUnit<T>> ls = new ArrayList< OperatingUnit<T>>(opunits.size());
			for ( OperatingUnit<T> op : opunits) {
				ls.add( op);
			}
			
			int start = level - 1;
			int end = level;
			
			int n = ls.size() - start;
			for ( int h = start; h < end; h++) {
				ArrayList< Set<OperatingUnit<T>>> heads = new ArrayList< Set<OperatingUnit<T>>> (n);
				//System.out.println(n + "keys");
				for ( int i = 0; i < n; i++) {
					HashSet<OperatingUnit<T>> combination = new HashSet<OperatingUnit<T>>();
					for ( int j = 0; j < h; j++) {
						combination.add( ls.get(i + j));
					}
					heads.add(i, combination);
					//System.out.println(key);
				}
				for (int i = 0; i < n; i++) {
					for ( int j = (h + i); j < ls.size(); j++) {
						//System.out.println( (heads[i] + ls[j]));
						HashSet<OperatingUnit<T>> combination = new HashSet<OperatingUnit<T>> ( heads.get(i));
						combination.add(ls.get(j));
						powerSet.add( combination);
					}
				}
				n--;
			}
		}
		
		return powerSet;
	}
	
	public static<T> Set< Set<OperatingUnit<T> > > powerSet_( Set< OperatingUnit<T>> opunits) {
		Set< Set<OperatingUnit<T>>> powerSet = new HashSet< Set<OperatingUnit<T>>> ();
		
		ArrayList<OperatingUnit<T>> ls = new ArrayList< OperatingUnit<T>>(opunits.size());
		for ( OperatingUnit<T> op : opunits) {
			ls.add( op);
		}
		
		for ( int i = 0; i < ls.size(); i++) {
			HashSet<OperatingUnit<T>> singleton = new HashSet<OperatingUnit<T>>();
			singleton.add( ls.get(i));
			powerSet.add( singleton);
		}
		
		int n = ls.size() - 1;
		for ( int h = 1; h < ls.size(); h++) {
			ArrayList< Set<OperatingUnit<T>>> heads = new ArrayList< Set<OperatingUnit<T>>> (n);
			//System.out.println(n + "keys");
			for ( int i = 0; i < n; i++) {
				HashSet<OperatingUnit<T>> combination = new HashSet<OperatingUnit<T>>();
				for ( int j = 0; j < h; j++) {
					combination.add( ls.get(i + j));
				}
				heads.add(i, combination);
				//System.out.println(key);
			}
			for (int i = 0; i < n; i++) {
				for ( int j = (h + i); j < ls.size(); j++) {
					//System.out.println( (heads[i] + ls[j]));
					HashSet<OperatingUnit<T>> combination = new HashSet<OperatingUnit<T>> ( heads.get(i));
					combination.add(ls.get(j));
					powerSet.add( combination);
				}
					
			}
			n--;
		}
		
		return powerSet;
	}
	
	public static void main(String[] args) {
		Set<OperatingUnit<String>> omg = new HashSet<> ();
		OperatingUnit<String> o1 = new OperatingUnit<>();
		o1.setID("K00001");
		OperatingUnit<String> o2 = new OperatingUnit<>();
		o2.setID("K00002");
		OperatingUnit<String> o3 = new OperatingUnit<>();
		o3.setID("K00003");
		OperatingUnit<String> o4 = new OperatingUnit<>();
		o4.setID("K00004");
		omg.add(o1);omg.add(o2);omg.add(o3);omg.add(o4);
		
		System.out.println(powerSet(omg, 2));
	}
}
