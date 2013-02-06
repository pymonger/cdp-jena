package gov.nasa.jpl.cdp.jena;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.VCARD;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class ARQTest extends TestCase {
	
	static public final String NL = System.getProperty("line.separator") ; 
	
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ARQTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ARQTest.class );
    }
    
    public void testSelect() {
    	
    	// get string to use for assertion
    	String assertFile = this.getClass().getResource(
    			"/arq1.assertion").getFile();
        String assertStr = IO.getStringFromFile(assertFile);
        
    	// build select query
        String prolog = "PREFIX rdf: <"+RDF.getURI()+">" + NL +
        	            "PREFIX vcard: <"+VCARD.getURI()+">";
        String sparql = prolog + NL +
            "SELECT ?family ?fn WHERE " + 
            "{?x vcard:FN ?fn . " +
            "?x vcard:N ?y . " +
            "?y vcard:Family ?family}" ; 
        
    	//get model
    	String file = this.getClass().getResource("/vc-db-1.ttl").getFile();
    	Model model = IO.getModel(file, "TURTLE");
    	ByteArrayOutputStream outputStr = new ByteArrayOutputStream();
    	
    	ArrayList<QuerySolution> qss = ARQ.select(model, sparql);
    	
    	try {
			outputStr.write("Full names: Family Name\n".getBytes());
			outputStr.write("=======================\n".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        for (int i=0; i < qss.size(); i++)
        {
            QuerySolution rb = qss.get(i);
            
            // Get title - variable names do not include the '?' (or '$')
            RDFNode family = rb.get("family");
            RDFNode fn = rb.get("fn");
            
            // Check the type of the result value
            if ( family.isLiteral()  && fn.isLiteral())
            {
                Literal familyStr = (Literal)family;
                Literal fnStr = (Literal)fn;
                try {
					outputStr.write((fnStr + ": "+familyStr+"\n").getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
        
        assertEquals( outputStr.toString(), assertStr );
    }

    public void testOutputSelectResults() {
        
    	// build select query
        String prolog = "PREFIX rdf: <"+RDF.getURI()+">" + NL +
        	            "PREFIX vcard: <"+VCARD.getURI()+">";
        String sparql = prolog + NL +
            "SELECT ?family ?fn WHERE " + 
            "{?x vcard:FN ?fn . " +
            "?x vcard:N ?y . " +
            "?y vcard:Family ?family}" ; 
        
    	//get model
    	String file = this.getClass().getResource("/vc-db-1.ttl").getFile();
    	Model model = IO.getModel(file, "TURTLE");
    	
    	QueryExecution qexec = ARQ.getQueryExecution(model, sparql);
    	
    	try {
        	ResultSet rs = qexec.execSelect();
        	ARQ.printQueryResults(rs);
        }
        finally
        {
            // QueryExecution objects should be closed to free any system resources 
            qexec.close();
        }
    }
}
