package gov.nasa.jpl.cdp.jena;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Derivation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class RulesTest extends TestCase {
	
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public RulesTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( RulesTest.class );
    }
    
    public void testGetInferenceModel() {
    	
    	//get model
    	String file = this.getClass().getResource("/manipon-family.ttl").getFile();
    	Model model = IO.getModel(file, "TURTLE");
    	
    	//rules file
    	String rulesFile = this.getClass().getResource("/family.rules").getFile();
    	
    	// get inference model
    	InfModel inf = Rules.getInferenceModel(model, rulesFile);
    	
    	// print out derivations
    	ByteArrayOutputStream outputStr = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(outputStr);
        for (StmtIterator i = inf.listStatements(); i.hasNext(); ) {
            Statement s = i.nextStatement();
            try {
				outputStr.write(("Statement is " + s + "\n").getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            for (Iterator id = inf.getDerivation(s); id.hasNext(); ) {
                Derivation deriv = (Derivation) id.next();
                deriv.printTrace(out, true);
            }
        }
        out.flush();
        //System.out.print(outputStr.toString());
    }
}
