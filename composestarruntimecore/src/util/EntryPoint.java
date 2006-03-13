
/**
 * Summary description for EntryPoint.
 */
public class EntryPoint
{
	private String fileName = "";
	private int lineNumber = 0;

	public EntryPoint(String fileName, int lineNumber)
	{
		this.fileName = fileName;
		this.lineNumber = lineNumber;
	}

	public String getFileName()
	{
		return fileName;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}
}
