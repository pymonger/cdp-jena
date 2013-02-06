/**
 * 
 */
package gov.nasa.jpl.cdp.jena;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import com.hp.hpl.jena.rdf.model.Model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author gmanipon
 *
 */
public class IOTest extends TestCase {
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public IOTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( IOTest.class );
    }

    /**
     * Test Jena Model instantiation of RDF/XML
     */
    public void testGetModelFromRdfXml()
    {
    	// input RDF/XML file
    	String file = this.getClass().getResource("/vc-db-1.rdf").getFile();
    	
    	// get ttl string to use for assertion from ttl file
    	String assertFile = this.getClass().getResource(
		"/vc-db-1.rdf.assertion").getFile();
        String assertTtl = IO.getStringFromFile(assertFile);
        
    	// get model and output turtle
    	Model model = IO.getModel(file, "RDF/XML");
    	ByteArrayOutputStream ttl = new ByteArrayOutputStream();
    	model.write(ttl, "TURTLE");
    	
		assertEquals( ttl.toString(), assertTtl );
    }
}
