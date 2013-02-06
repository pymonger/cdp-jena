package gov.nasa.jpl.cdp.jena;

import java.io.*;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.FileUtils;

public class IO {
	/* return Jena model from input file */
	public static Model getModel(String inputFile, String inputType)
	{
		InputStream ins = null;
    	String baseName = null;
    	
    	// create an empty model
        Model model = ModelFactory.createDefaultModel();
        
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
        File f = new File(inputFile);
        baseName = "file:///" + f.getAbsolutePath();
        baseName = baseName.replace('\\', '/');

        // read the input file
        model.read(in, baseName, inputType);
        //System.out.println("Read in input file of type(" + inputType +
        //		"): " + inputFile);
        
        return model;
	}
	
    /**
     * Return String of a file's contents.
     * @param file
     * @return String
     */
    public static String getStringFromFile(String file)
    {
        byte[] buffer = new byte[(int) new File(file).length()];
        BufferedInputStream f = null;
        try {
        	f = new BufferedInputStream(new FileInputStream(file));
        	f.read(buffer);
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
        	if (f != null) try { f.close(); } catch (IOException ignored) { }
        }
        return new String(buffer);
    }
}
