package ComposestarEclipsePlugin.Core.Utils;

public class Timer {

	private long startTime;
	private long stopTime;
	private long elapsedTime;
	
	public void start()
	{
		this.startTime = System.currentTimeMillis();
	}

	public void stop()
	{
		this.stopTime = System.currentTimeMillis();
		this.elapsedTime = this.stopTime - this.startTime;	
	}

	public long getElapsed()
	{
		return this.elapsedTime;
	}

}
