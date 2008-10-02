package Composestar.Utils.Logging;

public interface LocationProvider
{
	/**
	 * @return the source filename
	 */
	String getFilename();

	/**
	 * @return the line number
	 */
	int getLineNumber();

	/**
	 * @return the character position on the line
	 */
	int getLinePosition();
}
