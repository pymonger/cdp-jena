package gov.nasa.jpl.cdp.jena;

import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

public class Rules {

	public static InfModel getInferenceModel(Model model, Reasoner reasoner) {
		
		// create inference model
        reasoner.setDerivationLogging(true);  // log derivation to print out later
        InfModel inf = ModelFactory.createInfModel(reasoner, model);
        inf.setNsPrefixes(model);
        
        // check validity
        ValidityReport validity = inf.validate();
        if (validity.isValid()) {
        }else {
        	System.err.println("Conflict found validating InfModel:");
        	for (Iterator i = validity.getReports(); i.hasNext(); ) {
        		System.err.println(" - " + i.next());
        	}
        }
        
        return inf;
	}
	
	public static InfModel getInferenceModel(Model model, String rulesFile) {
		
		// create reasoner and inference model from it
        Reasoner reasoner = new GenericRuleReasoner(
        		Rule.rulesFromURL(rulesFile)
        );
        return getInferenceModel(model, reasoner);
	}
}
