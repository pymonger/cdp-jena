package gov.nasa.jpl.cdp.jena;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.junit.rules.TemporaryFolder;
import org.junit.Rule;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TDBTest extends TestCase {
	//TDB directory
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TDBTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( TDBTest.class );
    }
    
    /**
     * Test population of TDB-backed Jena model.
     */
    public void testSaveRDF()
    {
    	// input RDF in RDF/XML, N3, and TURTLE format
    	String inputFileNameRDF = this.getClass().getResource(
    			"/vc-db-1.rdf").getFile();
    	String inputFileNameN3 = this.getClass().getResource(
    			"/vc-db-1.n3").getFile();
    	String inputFileNameTurtle = this.getClass().getResource(
    			"/vc-db-1.ttl").getFile();
    	
    	// get ttl string to use for assertion from ttl file
    	String assertFile = this.getClass().getResource(
    			"/tdb.assertion").getFile();
        String assertTtl = IO.getStringFromFile(assertFile);

    	// get temporary directory for tdb
        File tdbDir = folder.newFolder("/tmp/tdbDir");
        //System.out.println("tdbDir:" + tdbDir.getAbsolutePath());
        
        //save rdf files in different formats; first saveRDF call cleans out tdb dir
    	TDB.saveRDF(inputFileNameRDF, "RDF/XML", tdbDir.getAbsolutePath(), 
    			true);
    	TDB.saveRDF(inputFileNameN3, "N3", tdbDir.getAbsolutePath());
    	TDB.saveRDF(inputFileNameTurtle, "TURTLE", tdbDir.getAbsolutePath());
    	Model model = TDB.getModel(tdbDir.getAbsolutePath());
    	ByteArrayOutputStream ttl = new ByteArrayOutputStream();
    	model.write(ttl, "TURTLE");
    	model.close();
    	
    	//remove tdbDir
    	try {
			org.apache.commons.io.FileUtils.deleteDirectory(
					new File(tdbDir.getAbsolutePath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//System.out.println("assertTtl:" + assertTtl);
    	//System.out.println("ttl:" + ttl.toString());
		assertEquals( ttl.toString(), assertTtl );
    }
    
    /**
     * Test population of TDB-backed Jena inferred model
     */
    public void testSaveRDFInferred()
    {
    	// input turtle
    	String inputFileNameTurtle = this.getClass().getResource(
    			"/artifacts.ttl").getFile();
    	String inputRulesFile = this.getClass().getResource(
    			"/artifacts.rules").getFile();
    	
    	// get ttl string to use for assertion from ttl file
    	String assertFile = this.getClass().getResource(
    			"/artifacts.assertion").getFile();
        String assertTtl = IO.getStringFromFile(assertFile);

    	// get temporary directory for tdb
        File tdbDir = folder.newFolder("/tmp/tdbDirInferred");
        //System.out.println("tdbDir:" + tdbDir.getAbsolutePath());
        
        //save inferred model
        Model model = TDB.indexRDF(inputFileNameTurtle, "TURTLE", 
        		tdbDir.getAbsolutePath(), true, inputRulesFile);
    	ByteArrayOutputStream ttl = new ByteArrayOutputStream();
    	model.write(ttl, "TURTLE");
    	model.close();
    	
    	//remove tdbDir
    	try {
			org.apache.commons.io.FileUtils.deleteDirectory(
					new File(tdbDir.getAbsolutePath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//System.out.println("assertTtl:" + assertTtl);
    	//System.out.println("ttl:" + ttl.toString());
		assertEquals( ttl.toString(), assertTtl );
    }
}
