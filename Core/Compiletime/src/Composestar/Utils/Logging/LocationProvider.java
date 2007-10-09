package Composestar.Utils.Logging;

//
// !! Compose* Runtime Warning !!
//
// This class is referenced in the Compose* Runtime for .NET 1.1
// Do not use Java features added after Java 2.0
//

public interface LocationProvider
{
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
