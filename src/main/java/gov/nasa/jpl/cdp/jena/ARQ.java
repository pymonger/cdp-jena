package gov.nasa.jpl.cdp.jena;

import java.io.OutputStream;
import java.util.ArrayList;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;

public class ARQ {
	
	public static QueryExecution getQueryExecution(Model model, String sparql) {
		Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
        return QueryExecutionFactory.create(query, model);
    }
	
	public static ArrayList<QuerySolution> select(Model model, String sparql) {
		// build ArrayList of QuerySolution objects because we need to close
		// QueryExecution explicitly
		ArrayList<QuerySolution> qss = new ArrayList<QuerySolution>();
        QueryExecution qexec = getQueryExecution(model, sparql);
        try {
        	ResultSet rs = qexec.execSelect();
            for ( ; rs.hasNext(); ) qss.add(rs.nextSolution());
        }
        finally
        {
            // QueryExecution objects should be closed to free any system resources 
            qexec.close();
        }
        return qss;
    }
	
	public static void printQueryResults(OutputStream stream, ResultSet rs) {
		ResultSetFormatter.outputAsJSON(stream, rs);
	}
	
	public static void printQueryResults(ResultSet rs) {
		printQueryResults(System.out, rs);
	}
}
