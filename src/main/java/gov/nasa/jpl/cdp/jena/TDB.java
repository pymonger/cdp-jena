package gov.nasa.jpl.cdp.jena;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;

public class TDB {
	
	public static Dataset getDataset(String directory)
	{
		//System.out.println("Writing out XML/RDF from TDB");
		//System.out.println("============================");
		// if directory doesn't exist, there is no failure; creates empty store
		Dataset ds = TDBFactory.createDataset(directory);
        //Model model = ds.getDefaultModel();
		//model.write(System.out, "RDF/XML");
		//model.close();
		return ds;
	}
	
	public static Model getModel(String directory)
	{
		Dataset ds = getDataset(directory);
		return ds.getDefaultModel();
	}
	
	/* return Jena model from BufferedReader */
	public static Model indexRDF(BufferedReader in, String inputType,
			String directory, boolean clean)
	{    	
    	// clean directory
    	if (clean == true) {
			try {
				org.apache.commons.io.FileUtils.deleteDirectory(new File(directory));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	// get model
        Model model = getModel(directory);

        // read the input into the model
        model.read(in, null, inputType);
        
		return model;
	}
	
	/* return Jena model from input file */
	public static Model indexRDF(String inputFile, String inputType,
			String directory, boolean clean)
	{
		InputStream ins = null;
    	//String baseName = null;
        
        // create InputStream (vs. using FileManger in the RDF/XML case)
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

        return indexRDF(in, inputType, directory, clean);
	}
	
	/* return Jena model from input file */
	public static Model indexRDF(String inputFile, String inputType,
			String directory)
	{
		return indexRDF(inputFile, inputType, directory, false);
	}
	
	public static void saveRDF(String inputFile, String inputType,
			String directory, boolean clean)
	{
		Model model = indexRDF(inputFile, inputType, directory, clean);
		model.close();
	}
	
	public static void saveRDF(String inputFile, String inputType,
			String directory)
	{
		Model model = indexRDF(inputFile, inputType, directory);
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
