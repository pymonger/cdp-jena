package gov.nasa.jpl.cdp.jena;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Derivation;
import com.hp.hpl.jena.util.FileUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class ClassificationTest extends TestCase {
	
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ClassificationTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ClassificationTest.class );
    }
    
    public void testGetRDFSModel() {
    	
    	// get triples
    	InputStream ins = null;
        try {
        	ins = new FileInputStream(this.getClass().
        			getResource("/opmo_es_inferred.ttl").getFile());
        }catch (FileNotFoundException noEx)
        {
        	System.err.println("File not found: " + this.getClass().
        			getResource("/opmo_es_inferred.ttl").getFile());
        	System.exit(2);
        }
        BufferedReader triples = FileUtils.asBufferedUTF8(ins);
        
        // get schema
        InputStream insch = null;
        try {
        	insch = new FileInputStream(this.getClass().
        			getResource("/opmo_es.rdf").getFile());
        }catch (FileNotFoundException noEx)
        {
        	System.err.println("File not found: " + this.getClass().
        			getResource("/opmo_es.rdf").getFile());
        	System.exit(2);
        }
        BufferedReader schema = FileUtils.asBufferedUTF8(insch);
    	
        // get classified
    	InfModel clsmodel = Classification.getClassifiedModel(triples, "TURTLE", 
    			schema, "RDF/XML");
    	
    	// print out derivations
    	ByteArrayOutputStream outputStr = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(outputStr);
        for (StmtIterator i = clsmodel.listStatements(); i.hasNext(); ) {
            Statement s = i.nextStatement();
            try {
				outputStr.write(("Statement is " + s + "\n").getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            for (Iterator id = clsmodel.getDerivation(s); id.hasNext(); ) {
                Derivation deriv = (Derivation) id.next();
                deriv.printTrace(out, true);
            }
        }
        out.flush();
        //System.out.print(outputStr.toString());
        
        // close
    	clsmodel.close();
    }
    
    public void testGetPelletModel() {
    	
    	// get triples
    	InputStream ins = null;
        try {
        	ins = new FileInputStream(this.getClass().
        			getResource("/opmo_es_inferred.ttl").getFile());
        }catch (FileNotFoundException noEx)
        {
        	System.err.println("File not found: " + this.getClass().
        			getResource("/opmo_es_inferred.ttl").getFile());
        	System.exit(2);
        }
        BufferedReader triples = FileUtils.asBufferedUTF8(ins);
        
        // get schema
        InputStream insch = null;
        try {
        	insch = new FileInputStream(this.getClass().
        			getResource("/opmo_es.rdf").getFile());
        }catch (FileNotFoundException noEx)
        {
        	System.err.println("File not found: " + this.getClass().
        			getResource("/opmo_es.rdf").getFile());
        	System.exit(2);
        }
        BufferedReader schema = FileUtils.asBufferedUTF8(insch);
    	
        // get classified using pellet
    	InfModel clsmodel = Classification.getClassifiedModel(triples, "TURTLE", 
    			schema, "RDF/XML", "Pellet");
    	
    	/*
    	// print out derivations
    	ByteArrayOutputStream outputStr = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(outputStr);
        for (StmtIterator i = clsmodel.listStatements(); i.hasNext(); ) {
            Statement s = i.nextStatement();
            try {
				outputStr.write(("Statement is " + s + "\n").getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            for (Iterator id = clsmodel.getDerivation(s); id.hasNext(); ) {
                Derivation deriv = (Derivation) id.next();
                deriv.printTrace(out, true);
            }
        }
        out.flush();
        //System.out.print(outputStr.toString());
         */
        
        // close
    	clsmodel.close();
    }
}
