package gov.nasa.jpl.cdp.jena;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileUtils;


/**
 * Main
 *
 */
public class App 
{
	public static String version = 
		App.class.getPackage().getImplementationVersion();
	
	public static void printSubcommandHelp()
	{
		System.err.println("usage: cdp_jena <subcommand>");
		System.err.println("version: " + version + "\n");
		System.err.println("Specify a subcommand:");
		System.err.println("  save");
		System.err.println("  dump");
		System.err.println("  rules");
		System.err.println("  query");
		System.err.println("  clean");
		System.err.println("  classify");
		System.exit(1);
	}
	
	public static void printOptionsHelp(Options options,
			String command, String message, Integer exitVal)
	{
		if (!message.equals("")) System.err.println(message);
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("cdp_jena " + command, options);
		System.err.println("version: " + version);
		System.exit(exitVal);
	}
	
	public static void printOptionsHelp(Options options,
			String command, ParseException e, Integer exitVal)
	{
		e.printStackTrace();
		printOptionsHelp(options, command, "", exitVal);
	}
	
    public static void main( String[] args )
    {
    	// make sure there is at least one option/argument
    	if (args.length == 0) printSubcommandHelp();
    	
    	// possible commands
    	Hashtable<String,String> commands = new Hashtable<String,String>();
    	commands.put("save", args[0]);
    	commands.put("dump", args[0]);
    	commands.put("rules", args[0]);
    	commands.put("query", args[0]);
    	commands.put("clean", args[0]);
    	commands.put("classify", args[0]);
    	
    	// check that one of the possible commands
    	if (!commands.containsKey(args[0])) {
    		printSubcommandHelp();
    	}
    	
    	// get command
    	String command = args[0];
    	
    	// copy rest of args/opts for option processing
    	String[] restArgs = new String[args.length-1];
    	System.arraycopy(args,1,restArgs,0,args.length-1);
    	
    	// parse opts by subcommand
    	CommandLineParser parser = new PosixParser();
    	Options options = new Options();
    	CommandLine line = null;
    	
    	// save subcommand (to model)
    	if (command.compareTo("save") == 0) {
    		Option inputFile = OptionBuilder.withLongOpt("input-file")
    			.withDescription("Input RDF file")
    			.hasArg()
    			.withArgName("input RDF file")
    			.create('i');
    		Option inputType = OptionBuilder.withLongOpt("input-type")
				.withDescription("Input RDF type")
				.hasArg()
				.withArgName("N3|RDF/XML|N-TRIPLE|TURTLE")
				.create('t');
    		Option tdbDir = OptionBuilder.withLongOpt("tdb-directory")
				.withDescription("TDB directory")
				.hasArg()
				.withArgName("directory")
				.create('d');
    		Option storeConfig = OptionBuilder.withLongOpt("store-config-file")
				.withDescription("Store configuration file")
				.hasArg()
				.withArgName("file")
				.create('s');
    		options.addOption("c", "clean", false, "Clean backend before saving");
        	options.addOption("h", "help", false, "Show help");
    		options.addOption(inputFile);
    		options.addOption(inputType);
    		options.addOption(tdbDir);
    		options.addOption(storeConfig);
        	
        	try {
    			line = parser.parse(options, restArgs);
    		} catch (ParseException e) {
    			// TODO Auto-generated catch block
    			printOptionsHelp(options, command, e, 1);
    		}
            
            // print help
        	if (line.hasOption("h")) {
        		printOptionsHelp(options, command, "", 0);
        	}
        	
        	// check options are set
        	if (!line.hasOption("i")) {
        		printOptionsHelp(options, command, 
        				"Error: need to specify -i/--input-file.", 1);
        	}
        	if (!line.hasOption("t")) {
        		// Using TURTLE as default
        		//printOptionsHelp(options, command, 
        		//		"Error: need to specify -t/--input-type.", 1);
        	}else {
        		if (!(line.getOptionValue("t").equals("RDF/XML") ||
        			  line.getOptionValue("t").equals("N3") ||
        			  line.getOptionValue("t").equals("N-TRIPLE") ||
        			  line.getOptionValue("t").equals("TURTLE")))
        			printOptionsHelp(options, command, 
            				"Error: Unknown input-type specified: "
        					+ line.getOptionValue("t"), 1);
        	}
        	if ((line.hasOption("d") && line.hasOption("s")) ||
	    		(!line.hasOption("d") && !line.hasOption("s"))) {
	    		printOptionsHelp(options, command, 
	    				"Error: Specify either TDB (-d/--tdb-directory) "
	    				+ "or SDB (-s/--store-config-file) backend.", 1);
	    	}
        	
        	// check if clean out
        	boolean clean = line.hasOption("c");
        	
        	// save RDF to TDB-backed model
        	if (line.hasOption("d")) {
        		TDB.saveRDF(line.getOptionValue("i"), 
        				line.getOptionValue("t", "TURTLE"),
        				line.getOptionValue("d"), clean);
        		
        	// save RDF to SDB-backed model
        	}else {
        		SDB.saveRDF(line.getOptionValue("i"), 
        				line.getOptionValue("t", "TURLTLE"),
            			line.getOptionValue("s"), clean);
        	}

	    // dump subcommand
    	}else if (command.compareTo("dump") == 0) {
			Option outputType = OptionBuilder.withLongOpt("output-type")
				.withDescription("Output RDF type")
				.hasArg()
				.withArgName("N3|RDF/XML|N-TRIPLE|TURTLE")
				.create('t');
			Option storeConfig = OptionBuilder.withLongOpt("store-config-file")
				.withDescription("Store configuration file")
				.hasArg()
				.withArgName("file")
				.create('s');
			Option tdbDir = OptionBuilder.withLongOpt("tdb-directory")
				.withDescription("TDB directory")
				.hasArg()
				.withArgName("directory")
				.create('d');
	    	options.addOption("h", "help", false, "Show help");
			options.addOption(outputType);
			options.addOption(storeConfig);
			options.addOption(tdbDir);
	    	
	    	try {
				line = parser.parse(options, restArgs);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				printOptionsHelp(options, command, e, 1);
			}
	        
	        // print help
	    	if (line.hasOption("h")) {
	    		printOptionsHelp(options, command, "", 0);
	    	}
	    	
	    	// check options are set
	    	if (!line.hasOption("t")) {
	    		// Using TURTLE as default
        		//printOptionsHelp(options, command, 
        		//		"Error: need to specify -t/--output-type.", 1);
        	}else {
        		if (!(line.getOptionValue("t").equals("RDF/XML") ||
        			  line.getOptionValue("t").equals("N3") ||
        			  line.getOptionValue("t").equals("N-TRIPLE") ||
        			  line.getOptionValue("t").equals("TURTLE")))
        			printOptionsHelp(options, command, 
            				"Error: Unknown input-type specified: "
        					+ line.getOptionValue("t"), 1);
        	}
	    	if ((line.hasOption("d") && line.hasOption("s")) ||
	    		(!line.hasOption("d") && !line.hasOption("s"))) {
	    		printOptionsHelp(options, command, 
	    				"Error: Specify either TDB (-d/--tdb-directory) "
	    				+ "or SDB (-s/--store-config-file) backend.", 1);
	    	}
	    	
	    	// get model
	    	Model model = null;
	    	if (line.hasOption("d"))
	    		model = TDB.getModel(line.getOptionValue("d"));
	    	else
	    		model = SDB.getModel(line.getOptionValue("s"));
	    	
	    	// write model
	    	model.write(System.out, line.getOptionValue("t", "TURTLE"));
	    	
	    	// close
	    	model.close();
	    	
	    // rules subcommand
    	}else if (command.compareTo("rules") == 0) {
    		Option outputType = OptionBuilder.withLongOpt("output-type")
				.withDescription("Output RDF type")
				.hasArg()
				.withArgName("N3|RDF/XML|N-TRIPLE|TURTLE")
			.create('t');
    		Option inputFile = OptionBuilder.withLongOpt("rules-file")
				.withDescription("Rules RDF file")
				.hasArg()
				.withArgName("Rules RDF file")
				.create('r');
			Option storeConfig = OptionBuilder.withLongOpt("store-config-file")
				.withDescription("Store configuration file")
				.hasArg()
				.withArgName("file")
				.create('s');
			Option tdbDir = OptionBuilder.withLongOpt("tdb-directory")
				.withDescription("TDB directory")
				.hasArg()
				.withArgName("directory")
				.create('d');
	    	options.addOption("h", "help", false, "Show help");
	    	options.addOption(outputType);
			options.addOption(inputFile);
			options.addOption(storeConfig);
			options.addOption(tdbDir);
	    	
	    	try {
				line = parser.parse(options, restArgs);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				printOptionsHelp(options, command, e, 1);
			}
	        
	        // print help
	    	if (line.hasOption("h")) {
	    		printOptionsHelp(options, command, "", 0);
	    	}
	    	
	    	// check options are set
	    	if (!line.hasOption("t")) {
	    		// Using TURTLE as default
        		//printOptionsHelp(options, command, 
        		//		"Error: need to specify -t/--output-type.", 1);
        	}else {
        		if (!(line.getOptionValue("t").equals("RDF/XML") ||
        			  line.getOptionValue("t").equals("N3") ||
        			  line.getOptionValue("t").equals("N-TRIPLE") ||
        			  line.getOptionValue("t").equals("TURTLE")))
        			printOptionsHelp(options, command, 
            				"Error: Unknown input-type specified: "
        					+ line.getOptionValue("t"), 1);
        	}
	    	if (!line.hasOption("r")) {
	    		printOptionsHelp(options, command, 
	    				"Error: need to specify -r/--rules-file.", 1);
	    	}
	    	if ((line.hasOption("d") && line.hasOption("s")) ||
	    		(!line.hasOption("d") && !line.hasOption("s"))) {
	    		printOptionsHelp(options, command, 
	    				"Error: Specify either TDB (-d/--tdb-directory) "
	    				+ "or SDB (-s/--store-config-file) backend.", 1);
	    	}
	    	
	    	// get model
	    	Model model = null;
	    	if (line.hasOption("d"))
	    		model = TDB.getModel(line.getOptionValue("d"));
	    	else
	    		model = SDB.getModel(line.getOptionValue("s"));
	    	
	    	// apply rules (get inference model)
	    	InfModel inf = Rules.getInferenceModel(model,
	    			line.getOptionValue("r"));
	    	
	    	// write inferred model
	    	inf.write(System.out, line.getOptionValue("t", "TURTLE"));
	    	
	    	// close
	    	inf.close();
	    	model.close();
	    
	    // arq subcommand
    	}else if (command.compareTo("query") == 0) {
    		Option inputFile = OptionBuilder.withLongOpt("sparql-file")
				.withDescription("SPARQL file")
				.hasArg()
				.withArgName("SPARQL file")
				.create('a');
			Option storeConfig = OptionBuilder.withLongOpt("store-config-file")
				.withDescription("Store configuration file")
				.hasArg()
				.withArgName("file")
				.create('s');
			Option tdbDir = OptionBuilder.withLongOpt("tdb-directory")
				.withDescription("TDB directory")
				.hasArg()
				.withArgName("directory")
				.create('d');
	    	options.addOption("h", "help", false, "Show help");
			options.addOption(inputFile);
			options.addOption(storeConfig);
			options.addOption(tdbDir);
	    	
	    	try {
				line = parser.parse(options, restArgs);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				printOptionsHelp(options, command, e, 1);
			}
	        
	        // print help
	    	if (line.hasOption("h")) {
	    		printOptionsHelp(options, command, "", 0);
	    	}
	    	
	    	// check options are set
	    	if (!line.hasOption("a")) {
	    		printOptionsHelp(options, command, 
	    				"Error: need to specify -a/--sparql-file.", 1);
	    	}
	    	if ((line.hasOption("d") && line.hasOption("s")) ||
	    		(!line.hasOption("d") && !line.hasOption("s"))) {
	    		printOptionsHelp(options, command, 
	    				"Error: Specify either TDB (-d/--tdb-directory) "
	    				+ "or SDB (-s/--store-config-file) backend.", 1);
	    	}
	    	
	    	// get model
	    	Model model = null;
	    	if (line.hasOption("d"))
	    		model = TDB.getModel(line.getOptionValue("d"));
	    	else
	    		model = SDB.getModel(line.getOptionValue("s"));
	    	
	    	// run sparql query
	    	String sparql = IO.getStringFromFile(line.getOptionValue("a"));
	    	QueryExecution qexec = ARQ.getQueryExecution(model, sparql);
	    	try {
	        	ResultSet rs = qexec.execSelect();
	        	ARQ.printQueryResults(rs);
	        }
	        finally
	        {
	            // QueryExecution objects should be closed to free any 
	        	// system resources 
	            qexec.close();
	            model.close();
	        }
	    
	    // clean subcommand
    	}else if (command.compareTo("clean") == 0) {
			Option storeConfig = OptionBuilder.withLongOpt("store-config-file")
				.withDescription("Store configuration file")
				.hasArg()
				.withArgName("file")
				.create('s');
			Option tdbDir = OptionBuilder.withLongOpt("tdb-directory")
				.withDescription("TDB directory")
				.hasArg()
				.withArgName("directory")
				.create('d');
	    	options.addOption("h", "help", false, "Show help");
			options.addOption(storeConfig);
			options.addOption(tdbDir);
	    	
	    	try {
				line = parser.parse(options, restArgs);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				printOptionsHelp(options, command, e, 1);
			}
	        
	        // print help
	    	if (line.hasOption("h")) {
	    		printOptionsHelp(options, command, "", 0);
	    	}
	    	
	    	// check options are set
	    	if ((line.hasOption("d") && line.hasOption("s")) ||
	    		(!line.hasOption("d") && !line.hasOption("s"))) {
	    		printOptionsHelp(options, command, 
	    				"Error: Specify either TDB (-d/--tdb-directory) "
	    				+ "or SDB (-s/--store-config-file) backend.", 1);
	    	}
	    	
	    	// get model
	    	Model model = null;
	    	if (line.hasOption("d"))
	    		model = TDB.getModel(line.getOptionValue("d"));
	    	else
	    		model = SDB.getModel(line.getOptionValue("s"));
	    	
	    	// remove all statements from model
	    	model.removeAll();
	    	
	    	// close
	    	model.close();
	    	
    	// rules subcommand
    	}else if (command.compareTo("classify") == 0) {
			Option inputFile = OptionBuilder.withLongOpt("input-file")
    			.withDescription("Input RDF file")
    			.hasArg()
    			.withArgName("input RDF file")
    			.create('i');
    		Option inputType = OptionBuilder.withLongOpt("input-type")
				.withDescription("Input RDF type")
				.hasArg()
				.withArgName("N3|RDF/XML|N-TRIPLE|TURTLE")
				.create('n');
			Option outputType = OptionBuilder.withLongOpt("output-type")
				.withDescription("Output RDF type")
				.hasArg()
				.withArgName("N3|RDF/XML|N-TRIPLE|TURTLE")
			.create('t');
			Option schemaFile = OptionBuilder.withLongOpt("schema-file")
				.withDescription("RDF schema file")
				.hasArg()
				.withArgName("RDF schema file")
				.create('s');
			Option reasonerType = OptionBuilder.withLongOpt("reasoner-type")
					.withDescription("Reasoner type")
					.hasArg()
					.withArgName("Pellet|OWL|OWLMini|OWLMicro|Transitive|RDFSSimple|RDFS")
				.create('r');
			
	    	options.addOption("h", "help", false, "Show help");
	    	options.addOption(inputType);
			options.addOption(inputFile);
			options.addOption(outputType);
			options.addOption(schemaFile);
			options.addOption(reasonerType);
	    	
	    	try {
				line = parser.parse(options, restArgs);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				printOptionsHelp(options, command, e, 1);
			}
	        
	        // print help
	    	if (line.hasOption("h")) {
	    		printOptionsHelp(options, command, "", 0);
	    	}
	    	
	    	// check options are set
	    	if (!line.hasOption("t")) {
	    		// Using TURTLE as default
	    		//printOptionsHelp(options, command, 
	    		//		"Error: need to specify -t/--output-type.", 1);
	    	}else {
	    		if (!(line.getOptionValue("t").equals("RDF/XML") ||
	    			  line.getOptionValue("t").equals("N3") ||
	    			  line.getOptionValue("t").equals("N-TRIPLE") ||
	    			  line.getOptionValue("t").equals("TURTLE")))
	    			printOptionsHelp(options, command, 
	        				"Error: Unknown output-type specified: "
	    					+ line.getOptionValue("t"), 1);
	    	}
	    	if (!line.hasOption("n")) {
	    		// Using TURTLE as default
	    		//printOptionsHelp(options, command, 
	    		//		"Error: need to specify -t/--output-type.", 1);
	    	}else {
	    		if (!(line.getOptionValue("n").equals("RDF/XML") ||
	    			  line.getOptionValue("n").equals("N3") ||
	    			  line.getOptionValue("n").equals("N-TRIPLE") ||
	    			  line.getOptionValue("n").equals("TURTLE")))
	    			printOptionsHelp(options, command, 
	        				"Error: Unknown input-type specified: "
	    					+ line.getOptionValue("n"), 1);
	    	}
	    	if (!line.hasOption("s")) {
	    		printOptionsHelp(options, command, 
	    				"Error: need to specify -s/--schema-file.", 1);
	    	}
	    	if (!line.hasOption("r")) {
	    		// Using TURTLE as default
	    		//printOptionsHelp(options, command, 
	    		//		"Error: need to specify -t/--output-type.", 1);
	    		//Pellet|OWL|OWLMini|OWLMicro|Transitive|RDFSSimple|RDFS
	    	}else {
	    		if (!(line.getOptionValue("r").equals("Pellet") ||
	    			  line.getOptionValue("r").equals("OWL") ||
	    			  line.getOptionValue("r").equals("OWLMini") ||
	    			  line.getOptionValue("r").equals("OWLMicro") ||
	    			  line.getOptionValue("r").equals("Transitive") ||
	    			  line.getOptionValue("r").equals("RDFSSimple") ||
	    			  line.getOptionValue("r").equals("RDFS")))
	    			printOptionsHelp(options, command, 
	        				"Error: Unknown reasoner-type specified: "
	    					+ line.getOptionValue("r"), 1);
	    	}
	    	
	    	// get triples
	    	InputStream ins = null;
	        try {
	        	ins = new FileInputStream(line.getOptionValue("i"));
	        }catch (FileNotFoundException noEx)
	        {
	        	System.err.println("File not found: " + line.getOptionValue("i"));
	        	System.exit(2);
	        }
	        BufferedReader triples = FileUtils.asBufferedUTF8(ins);
	        
	        // get schema
	        InputStream insch = null;
	        try {
	        	insch = new FileInputStream(line.getOptionValue("s"));
	        }catch (FileNotFoundException noEx)
	        {
	        	System.err.println("File not found: " + line.getOptionValue("s"));
	        	System.exit(2);
	        }
	        BufferedReader schema = FileUtils.asBufferedUTF8(insch);
	    	
	    	// get classified
	    	InfModel clsmodel = Classification.getClassifiedModel(triples, 
	    			line.getOptionValue("n", "TURTLE"), schema, "RDF/XML",
	    			line.getOptionValue("r", "RDFS"));
	    	
	    	// write model
	    	clsmodel.write(System.out, line.getOptionValue("t", "TURTLE"));
	    	
	    	// close
	    	clsmodel.close();
		}
    }
}
