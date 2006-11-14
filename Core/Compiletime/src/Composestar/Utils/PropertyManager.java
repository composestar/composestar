package Composestar.Utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Properties;

import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * @deprecated
 */
public class PropertyManager implements Cloneable, Serializable
{
	static final long serialVersionUID = 4343139487235166396L;

	private String filename;

	private DataStore ds;

	/**
	 * Creates a new PropertyManager with the given filename.
	 * 
	 * @param inds DataStore The repository
	 * @param infilename String The name of the file
	 * @roseuid 404DCD010103
	 */
	public PropertyManager(DataStore inds, String infilename)
	{
		ds = inds;
		filename = infilename;
	}

	/**
	 * Creates a new PropertyManager with the default filename.
	 * 
	 * @param inds DataStore The repository
	 * @roseuid 404DCD0100D4
	 */
	public PropertyManager(DataStore inds)
	{
		ds = inds;
	}

	/**
	 * Sets the filename to the given filename.
	 * 
	 * @param infilename String The name of the file
	 * @roseuid 404DCD010151
	 */
	public void setFilename(String infilename)
	{
		filename = infilename;
	}

	/**
	 * Gets the filename.
	 * 
	 * @return String The name of the file
	 * @roseuid 404DCD01019F
	 */
	public String getFilename()
	{
		return filename;
	}

	/**
	 * Loads the properties from the file, and puts them in the Repository under
	 * name "config".
	 * 
	 * @roseuid 404DCD0101CE
	 */
	public void load()
	{
		load("config", filename);
	}

	/**
	 * Loads the properties from the given filename.
	 * 
	 * @param keyname String The name of the key used to store this in the
	 *            repository.
	 * @param infilename String The file to load from.
	 * @param keyname
	 * @roseuid 404DCD0101DD
	 */
	public void load(String keyname, String infilename)
	{
		try
		{
			Properties props = new Properties();
			props.load(new FileInputStream(infilename));
			ds.addObject(keyname, props);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Stores the properties in the file.
	 * 
	 * @param properties Properties The properties to store.
	 * @param header String The header of file to store the info.
	 * @roseuid 404DCD01023B
	 */
	public void store(Properties properties, String header)
	{
		store(properties, this.filename, header);
	}

	/**
	 * Stores the properties in the given filename.
	 * 
	 * @param properties Properties The properties to store.
	 * @param infilename String The file to store the info.
	 * @param header String The header of file to store to.
	 * @roseuid 404DCD010299
	 */
	public void store(Properties properties, String infilename, String header)
	{
		try
		{
			properties.store(new FileOutputStream(infilename), header);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
