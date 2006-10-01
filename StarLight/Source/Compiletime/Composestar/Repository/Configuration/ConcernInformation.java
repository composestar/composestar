package Composestar.Repository.Configuration;

import java.io.File;

public class ConcernInformation {

	public ConcernInformation(String filename, String path)
	{
		this._filename = filename;
		this._path = path;
	}

	private int _timeStamp;

	/** @property */
	public int get_Timestamp()
	{
		return _timeStamp;
	}

	/** @property */
	public void set_Timestamp(int value)
	{
		_timeStamp = value;
	} 


	private String _path;

	/** @property */
	public String get_Path()
	{
		return _path;
	}

	/** @property */
	public void set_Path(String value)
	{
		_path = value;
	} 


	private String _filename;

	/** @property */
	public String get_Filename()
	{
		return _filename;
	}

	/** @property */
	public void set_Filename(String value)
	{
		_filename = value;
	} 

	
	/**
	 * @return Returns the fullpath, combining the pathname and the filename. 
	 */
	public String getFullFilename()
	{		
		if (!_path.endsWith(File.separator ))
		{
			return this.get_Path() + File.separator + this.get_Filename(); 
		}
		else
		{			
			return this.get_Path() + this.get_Filename();  
		}
	}
}
