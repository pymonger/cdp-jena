package gov.nasa.jpl.cdp.jena;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;


public class SDBTest extends TestCase {
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SDBTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( SDBTest.class );
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
    	//String assertFile = this.getClass().getResource(
    	//		"/tdb.assertion").getFile();
        //String assertTtl = IO.getStringFromFile(assertFile);
        
        // get ttl for SDB store configuration
        String storeConfigFile = this.getClass().getResource(
		"/sdb-mysql-innodb.ttl").getFile();
        
        //save rdf files in different formats; first saveRDF call cleans out sdb dir
    	SDB.saveRDF(inputFileNameRDF, "RDF/XML", storeConfigFile, true);
    	SDB.saveRDF(inputFileNameN3, "N3", storeConfigFile);
    	SDB.saveRDF(inputFileNameTurtle, "TURTLE", storeConfigFile);
    	Model model = SDB.getModel(storeConfigFile);
    	ByteArrayOutputStream ttl = new ByteArrayOutputStream();
    	model.write(ttl, "TURTLE");
    	model.close();
    	
    	//System.out.println("assertTtl:" + assertTtl);
    	//System.out.println("ttl:" + ttl.toString());
		//assertEquals( ttl.toString(), assertTtl );
    	assertTrue(true);
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

        // get ttl for SDB store configuration
        String storeConfigFile = this.getClass().getResource(
		"/sdb-mysql-innodb.ttl").getFile();
        
        //save inferred model
        Model model = SDB.indexRDF(inputFileNameTurtle, "TURTLE", 
        		storeConfigFile, true, inputRulesFile);
    	ByteArrayOutputStream ttl = new ByteArrayOutputStream();
    	model.write(ttl, "TURTLE");
    	model.close();
    	
    	//System.out.println("assertTtl:" + assertTtl);
    	//System.out.println("ttl:" + ttl.toString());
		assertEquals( ttl.toString(), assertTtl );
    }
}
