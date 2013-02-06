package gov.nasa.jpl.cdp.jena;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import virtuoso.jena.driver.VirtDataSource;
import virtuoso.jena.driver.VirtModel;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileUtils;

public class Virtuoso {
	public static VirtDataSource getDataSource(String url, String username, String password)
	{
		return new VirtDataSource(url, username, password);
	}
	
	public static Model getModel(String url, String username, String password)
	{
		return VirtModel.openDefaultModel(url, username, password);
	}
	
	/* return Jena model from BufferedReader */
	public static Model indexRDF(BufferedReader in, String inputType,
			String url, String username, String password, boolean clean)
	{    	
    	
    	// get model
        Model model = getModel(url, username, password);
        
        // clean model
    	if (clean == true) model.removeAll();

        // read the input into the model
        model.read(in, null, inputType);
        
		return model;
	}
	
	/* return Jena model from input file */
	public static Model indexRDF(String inputFile, String inputType,
			String url, String username, String password, boolean clean)
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

        return indexRDF(in, inputType, url, username, password, clean);
	}
	
	/* return Jena model from input file */
	public static Model indexRDF(String inputFile, String inputType,
			String url, String username, String password)
	{
		return indexRDF(inputFile, inputType, url, username, password, false);
	}
	
	public static void saveRDF(String inputFile, String inputType,
			String url, String username, String password, boolean clean)
	{
		Model model = indexRDF(inputFile, inputType, url, username, password, 
				clean);
		model.close();
	}
	
	public static void saveRDF(String inputFile, String inputType,
			String url, String username, String password)
	{
		Model model = indexRDF(inputFile, inputType, url, username, password);
		model.close();
	}
	
/* INFERENCE MODELS */
	
	/* index RDF, apply rules, and store the inferred model */
	/* return Jena model from BufferedReader */
	public static Model indexRDF(BufferedReader in, String inputType,
			String url, String username, String password, boolean clean, 
			String rulesFile)
	{    	
    	// get model
        Model model = indexRDF(in, inputType, url, username, password, clean);

        // get inference model
        InfModel inf_model = Rules.getInferenceModel(model, rulesFile);
        model.add(inf_model);
        
		return model;
	}
	
	public static Model indexRDF(String inputFile, String inputType,
			String url, String username, String password, boolean clean, 
			String rulesFile)
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
        return indexRDF(in, inputType, url, username, password, clean, 
        		rulesFile);
	}
	
	public static Model indexRDF(String inputFile, String inputType,
			String url, String username, String password, String rulesFile)
	{
		return indexRDF(inputFile, inputType, url, username, password, 
				false, rulesFile);
	}
	
	public static void saveRDF(String inputFile, String inputType,
			String url, String username, String password, boolean clean, 
			String rulesFile)
	{
		Model model = indexRDF(inputFile, inputType, url, username, password, 
				clean, rulesFile);
		model.close();
	}
	
	public static void saveRDF(String inputFile, String inputType,
			String url, String username, String password, String rulesFile)
	{
		Model model = indexRDF(inputFile, inputType, url, username, password, 
				rulesFile);
		model.close();
	}
}
