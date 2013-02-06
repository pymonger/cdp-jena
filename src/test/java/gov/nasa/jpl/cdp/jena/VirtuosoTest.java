package gov.nasa.jpl.cdp.jena;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.rules.TemporaryFolder;
import org.junit.Rule;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class VirtuosoTest extends TestCase {
	// virtuoso configuration
	String url = "jdbc:virtuoso://localhost:1111/charset=UTF-8/log_enable=2";
	String username = "dba";
	String password = "dba";
	
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public VirtuosoTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( VirtuosoTest.class );
    }
    
    /**
     * Test population of Virtuoso-backed Jena model.
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
    			"/virtuoso.assertion").getFile();
        String assertTtl = IO.getStringFromFile(assertFile);
        
    	//save rdf files in different formats
    	Virtuoso.saveRDF(inputFileNameRDF, "RDF/XML", url, username, password, true);
    	Virtuoso.saveRDF(inputFileNameN3, "N3", url, username, password);
    	Virtuoso.saveRDF(inputFileNameTurtle, "TURTLE", url, username, password);
    	Model model = Virtuoso.getModel(url, username, password);
    	ByteArrayOutputStream ttl = new ByteArrayOutputStream();
    	model.write(ttl, "TURTLE");
    	
    	FileOutputStream f;
		try {
			f = new FileOutputStream("/tmp/virtuoso.assertion");
			model.write(f, "TURTLE");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	model.removeAll();
    	model.close();
    	
    	//System.out.println("assertTtl:" + assertTtl);
    	//System.out.println("ttl:" + ttl.toString());
    	//assertEquals( ttl.toString(), assertTtl );
    }
    
    /**
     * Test population of Virtuoso-backed Jena inferred model
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
    			"/artifacts-virtuoso.assertion").getFile();
        String assertTtl = IO.getStringFromFile(assertFile);
        
        //save inferred model
        Model model = Virtuoso.indexRDF(inputFileNameTurtle, "TURTLE", 
        		url, username, password, true, inputRulesFile);
    	ByteArrayOutputStream ttl = new ByteArrayOutputStream();
    	model.write(ttl, "TURTLE");
		/*
		FileOutputStream f;
    	try {
			f = new FileOutputStream("/tmp/artifacts-virtuoso.assertion");
			model.write(f, "TURTLE");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
    	model.removeAll();
    	model.close();
    	
    	//System.out.println("assertTtl:" + assertTtl);
    	//System.out.println("ttl:" + ttl.toString());
    	//assertEquals( ttl.toString(), assertTtl );
    }
}
