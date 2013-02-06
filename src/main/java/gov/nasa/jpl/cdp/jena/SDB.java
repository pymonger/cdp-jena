package gov.nasa.jpl.cdp.jena;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.sql.SDBExceptionSQL;
import com.hp.hpl.jena.sdb.util.StoreUtils;
import com.hp.hpl.jena.util.FileUtils;

public class SDB {
	
	public static Dataset getDataset(String storeConfigFile)
	{
		//System.out.println("Writing out XML/RDF from SDB");
		//System.out.println("============================");
		// initialize store
    	Store store = SDBFactory.connectStore(storeConfigFile);
    	try {
			if (StoreUtils.isFormatted(store) == false)
				store.getTableFormatter().create();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	// return dataset
		return SDBFactory.connectDataset(store);
	}
	
	public static Model getModel(String storeConfigFile)
	{
		Dataset ds = getDataset(storeConfigFile);
		return ds.getDefaultModel();
	}
	
	public static Model indexRDF(BufferedReader in, String inputType,
			String storeConfigFile, boolean clean)
	{
    	// clean directory
    	if (clean == true) {
    		Store store = SDBFactory.connectStore(storeConfigFile);
    		try {
    			store.getTableFormatter().truncate();
    		}catch (SDBExceptionSQL e) {}
    		store.close();
    	}
    	
    	// get model
    	Model model = getModel(storeConfigFile);

        // read the input file
        model.read(in, null, inputType);
        
        return model;
	}
	
	public static Model indexRDF(String inputFile, String inputType,
			String storeConfigFile, boolean clean)
	{
		InputStream ins = null;
    	//String baseName = null;
    	
        // create InputStream
        try {
        	ins = new FileInputStream(inputFile);
        }catch (FileNotFoundException noEx)
        {
        	System.err.println("File not found: " + inputFile);
        	System.exit(2);
        }
        BufferedReader in = FileUtils.asBufferedUTF8(ins);
        //System.out.println("Got InputStream for " + inputFile);
        
        // get baseName
        //File f = new File(inputFile);
        //baseName = "file:///" + f.getAbsolutePath();
        //baseName = baseName.replace('\\', '/');

        return indexRDF(in, inputType, storeConfigFile, clean);
	}
	
	/* return Jena model from input file */
	public static Model indexRDF(String inputFile, String inputType,
			String storeConfigFile)
	{
		return indexRDF(inputFile, inputType, storeConfigFile, false);
	}
	
	public static void saveRDF(String inputFile, String inputType,
			String storeConfigFile, boolean clean)
	{
		Model model = indexRDF(inputFile, inputType, storeConfigFile, clean);
		model.close();
	}
	
	public static void saveRDF(String inputFile, String inputType,
			String storeConfigFile)
	{
		Model model = indexRDF(inputFile, inputType, storeConfigFile, false);
		model.close();
	}
	
	/* INFERENCE MODELS */
	
	/* index RDF, apply rules, and store the inferred model */
	/* return Jena model from BufferedReader */
	public static Model indexRDF(BufferedReader in, String inputType,
			String directory, boolean clean, String rulesFile)
	{    	
    	// get model
        Model model = indexRDF(in, inputType, directory, clean);

        // get inference model
        InfModel inf_model = Rules.getInferenceModel(model, rulesFile);
        model.add(inf_model);
        
		return model;
	}
	
	public static Model indexRDF(String inputFile, String inputType,
			String directory, boolean clean, String rulesFile)
	{
		InputStream ins = null;
        
        // create InputStream (vs. using FileManger in the RDF/XML case)
        try {
        	ins = new FileInputStream(inputFile);
        }catch (FileNotFoundException noEx)
        {
        	System.err.println("File not found: " + inputFile);
        	System.exit(2);
        }
        BufferedReader in = FileUtils.asBufferedUTF8(ins);
        
    	// return model w/ inference
        return indexRDF(in, inputType, directory, clean, rulesFile);
	}
	
	public static Model indexRDF(String inputFile, String inputType,
			String directory, String rulesFile)
	{
		return indexRDF(inputFile, inputType, directory, false, rulesFile);
	}
	
	public static void saveRDF(String inputFile, String inputType,
			String directory, boolean clean, String rulesFile)
	{
		Model model = indexRDF(inputFile, inputType, directory, clean, rulesFile);
		model.close();
	}
	
	public static void saveRDF(String inputFile, String inputType,
			String directory, String rulesFile)
	{
		Model model = indexRDF(inputFile, inputType, directory, rulesFile);
		model.close();
	}
}
