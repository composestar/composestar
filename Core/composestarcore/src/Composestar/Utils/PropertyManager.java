package Composestar.Utils;

import java.util.*;
import java.io.*;
import Composestar.Core.RepositoryImplementation.DataStore;

public class PropertyManager extends Object implements Cloneable, Serializable {
    static final long serialVersionUID = 4343139487235166396L;
    private Properties props = null;
    private String filename;
    private DataStore ds;
    
    /**
     * Creates a new PropertyManager with the given filename.
     * @param ds DataStore The repository
     * @param  filename    String  The name of the file
     * @roseuid 404DCD010103
     */
    public PropertyManager(DataStore ds, String filename) {
    	this.ds = ds;
    	this.filename = filename;     
    }
    
    /**
     * Creates a new PropertyManager with the default filename.
     * @param ds DataStore The repository
     * @roseuid 404DCD0100D4
     */
    public PropertyManager(DataStore ds) {
    	this.ds = ds;     
    }
    
    /**
     * Sets the filename to the given filename.
     * @param  filename    String  The name of the file
     * @roseuid 404DCD010151
     */
    public void setFilename(String filename) {
    	this.filename = filename;     
    }
    
    /**
     * Gets the filename.
     * @return  String  The name of the file
     * @roseuid 404DCD01019F
     */
    public String getFilename() {
     	return this.filename;     
    }
    
    /**
     * Loads the properties from the file, and puts them in the Repository under name 
     * "config".
     * @roseuid 404DCD0101CE
     */
    public void load() {
    	this.load("config",this.filename);     
    }
    
    /**
     * Loads the properties from the given filename.
     * @param  keyname String  The name of the key used to store this in the
     * repository.
     * @param filename String  The file to load from.
     * @param keyname
     * @roseuid 404DCD0101DD
     */
    public void load(String keyname, String filename) {
    	try
		{
		    props = new Properties();
		    props.load(new FileInputStream(filename));
		    ds.addObject(keyname,props);
		}
		catch(Exception e) { e.printStackTrace(); }     
    }
    
    /**
     * Stores the properties in the file.
     * @param properties Properties The properties to store.
     * @param header String  The header of file to store the info.
     * @roseuid 404DCD01023B
     */
    public void store(Properties properties, String header) {
    	this.store(properties,this.filename,header);     
    }
    
    /**
     * Stores the properties in the given filename.
     * @param properties Properties The properties to store.
     * @param filename String  The file to store the info.
     * @param header String  The header of file to store to.
     * @roseuid 404DCD010299
     */
    public void store(Properties properties, String filename, String header) {
    	try
		{
		    properties.store(new FileOutputStream(filename), header);
		}
		catch(Exception e) { e.printStackTrace(); }     
    }
}
