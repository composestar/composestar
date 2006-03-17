
/**
 * Summary description for EntryPoint.
 */
public class EntryPoint
{
	private String fileName = "";
	public static final int UNDEFINED =-1;
	private int lineNumber;

	public EntryPoint(String fileName, int lineNumber)
	{
		this.fileName = fileName;
		this.lineNumber = lineNumber;
	}

	public EntryPoint(String fileName)
	{
		this.fileName = fileName;
		this.lineNumber = UNDEFINED;
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
