package Composestar.Repository.Configuration;

public class ConcernInformation {

	public ConcernInformation(String filename, String path)
	{
		this._filename = filename;
		this._path = path;
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

}
