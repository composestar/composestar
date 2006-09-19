package Composestar.Repository.Configuration;

public class CommonConfiguration {
	private int _compiletimeDebugLevel = 0;

	private String _installFolder;

	/** @property */
	public String get_InstallFolder()
	{
		return _installFolder;
	}

	/** @property */
	public void set_InstallFolder(String value)
	{
		_installFolder = value;
	} 



	/** @property */
	public int get_CompiletimeDebugLevel()
	{
	    return _compiletimeDebugLevel;
	}

	/** @property */
	public void set_CompiletimeDebugLevel(int value)
	{
		if (value < 0 || value > 4) {
			_compiletimeDebugLevel = 3;
		}
		else {
			_compiletimeDebugLevel = value;
		}
	}

	private String _intermediateOutputPath;

	/** @property */
	public String get_IntermediateOutputPath()
	{
		return _intermediateOutputPath;
	}

	/** @property */
	public void set_IntermediateOutputPath(String value)
	{
		_intermediateOutputPath = value;
	} 

}
