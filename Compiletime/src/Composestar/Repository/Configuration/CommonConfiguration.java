package Composestar.Repository.Configuration;

public class CommonConfiguration {
	private int _compiletimeDebugLevel = 0;
	

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
}
