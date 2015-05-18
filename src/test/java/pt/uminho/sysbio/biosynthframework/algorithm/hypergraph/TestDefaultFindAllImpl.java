package pt.uminho.sysbio.biosynthframework.algorithm.hypergraph;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperEdge;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperGraph;


public class TestDefaultFindAllImpl {

  public static final String FIG1_D[][] = {
	    {"v17"}, {"v1"},                     //R1
	    {"v4", "v9", "v11", "v14"}, {"v17"}, //R2
	    {"v5", "v7", "v20"}, {"v14"},        //R3
	    {"v2", "v7", "v16"}, {"v14"},        //R4
	    {"v4", "v6", "v9", "v15"}, {"v14"},  //R5
	    {"v19"}, {"v16"},                    //R6
	    {"v10"}, {"v16"},                    //R7
	    {"v5", "v7", "v18"}, {"v15"},        //R8
	    {"v2", "v7", "v13"}, {"v15"},        //R9
	    {"v3", "v8", "v9", "v12"}, {"v16"},  //R10
	    {"v4", "v6", "v9", "v13"}, {"v16"},  //R11
    };
  public static final String FIG1_R[] = { "R1", "R2", "R3", "R4", "R5", 
                                        	"R6", "R7", "R8", "R9", "R10", "R11"};
  public static final String FIG1_S[] = { "v2", "v3", "v4",  "v5",  "v6",  "v7", 
                                        	"v8", "v9", "v10", "v11", "v12", "v13"};

	public static DiHyperGraph<String, String> FIG1() {
		DiHyperGraph<String, String> hpg = new DiHyperGraph<String, String>();
		int i = 0;
		for ( String n : FIG1_R) {
			DiHyperEdge<String, String> e = new DiHyperEdge<String, String>( FIG1_D[i], FIG1_D[i + 1], n);
			i += 2;
			hpg.addEdge( e);
		}
		hpg.setName("FIG 1 Carbonell et al. 2012");
		return hpg;
	}
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() {
  	FindAll<String> findAll = new DefaultFindAllImpl<>();
  	Minimize<String> minimize = new PartitionMinimizeImpl<>();
  	minimize.setFindAllKernel(findAll);
  	String[] T = {"v1"};
  	String[] S = FIG1_S;
  	DefaultFindPathImpl<String> findPath = new DefaultFindPathImpl<String>(FIG1(), T, S);
  	findPath.setFindAllKernel(findAll);
  	findPath.setMinimizeKernel(minimize);
  	findPath.solve();

  	System.out.println(findPath.getSolutions_());
  	
    assertEquals(4, findPath.getSolutionsNumber());
  }

}
