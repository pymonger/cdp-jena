package gov.nasa.jpl.cdp.jena;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.Iterator;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

public class Classification {
	
	public static InfModel getClassifiedModel(BufferedReader input, String inputType,
			BufferedReader schema, String schemaType, String reasonerType) {
		
		// create owl schema model
		Model s = ModelFactory.createDefaultModel();
        s.read(schema, null, schemaType);
        
        // create data model
        Model data = ModelFactory.createDefaultModel();
        data.read(input, null, inputType);
        
        // create inference model using the specified reasoner
        Reasoner reasoner;
		if (reasonerType.contentEquals("Pellet"))
        	reasoner  = PelletReasonerFactory.theInstance().create();
		else if (reasonerType.contentEquals("OWLMini"))
			reasoner = ReasonerRegistry.getOWLMiniReasoner();
		else if (reasonerType.contentEquals("OWLMicro"))
			reasoner = ReasonerRegistry.getOWLMicroReasoner();
		else if (reasonerType.contentEquals("OWL"))
			reasoner = ReasonerRegistry.getOWLReasoner();
		else if (reasonerType.contentEquals("Transitive"))
			reasoner = ReasonerRegistry.getTransitiveReasoner();
		else if (reasonerType.contentEquals("RDFSSimple"))
			reasoner = ReasonerRegistry.getRDFSSimpleReasoner();
        else
        	reasoner = ReasonerRegistry.getRDFSReasoner();
        reasoner = reasoner.bindSchema(s);
        InfModel infmodel = ModelFactory.createInfModel(reasoner, data);
        //infmodel.setNsPrefixes(data);
        
        // check validity
        ValidityReport validity = infmodel.validate();
        if (validity.isValid()) {
        }else {
        	System.err.println("Conflict found validating InfModel:");
        	for (Iterator i = validity.getReports(); i.hasNext(); ) {
        		System.err.println(" - " + i.next());
        	}
        }
        
        return infmodel;
        /*
        // write inferred model
    	ByteArrayOutputStream infStr = new ByteArrayOutputStream();
    	infmodel.write(infStr, "TURTLE");
    	
    	// write triples from inferred model
		String classified_triples = infStr.toString();
        
	    // close
		infmodel.close();
		data.close();
		s.close();
        
        return classified_triples;
        */
	}
	
	public static InfModel getClassifiedModel(BufferedReader input, String inputType,
			BufferedReader schema, String schemaType) {
		return getClassifiedModel(input, inputType, schema, schemaType, "RDFS");
	}
}
