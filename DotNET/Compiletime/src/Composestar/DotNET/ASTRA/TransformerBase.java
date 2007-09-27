package Composestar.DotNET.ASTRA;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import Composestar.Utils.Logging.CPSLogger;

/**
 * Base class for assembly modifiers. It provides the means necessary to read
 * from and write to the asm fille.
 */
abstract class TransformerBase
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ASTRA.MODULE_NAME);

	private BufferedReader in;

	private BufferedWriter out;

	private TransformerBase parent;

	protected TransformerBase(TransformerBase parent)
	{
		this.in = parent.in;
		this.out = parent.out;
		this.parent = parent;
	}

	protected TransformerBase(BufferedReader in, BufferedWriter out)
	{
		this.in = in;
		this.out = out;
		this.parent = null;
	}

	/**
	 * Sets the buffer to use as input for the transformation.
	 * 
	 * @param in
	 */
	protected void setIn(BufferedReader in)
	{
		this.in = in;
	}

	/**
	 * Opens a file to write to.
	 * 
	 * @param fileName
	 */
	protected void openOut(File fileName) throws ModifierException
	{
		try
		{
			out = new BufferedWriter(new FileWriter(fileName));
		}
		catch (IOException e)
		{
			throw new ModifierException("IO error while modifying assembly: " + e.getMessage());
		}
	}

	/**
	 * Closes the file reader.
	 */
	protected void closeOut() throws ModifierException
	{
		try
		{
			out.close();
			out = null;
		}
		catch (IOException e)
		{
			throw new ModifierException("Unable to close reader: " + e.getMessage());
		}
	}

	/**
	 * Gets one line from the input buffer.
	 * 
	 * @throws ModifierException if there are no more lines (or not*).
	 */
	public String getLine() throws ModifierException
	{
		if (parent != null)
		{
			return parent.getLine();
		}

		try
		{
			String line;
			do
			{
				if ((line = in.readLine()) == null)
				{
					return null; // *throw
					// new
					// ModifierException(
					// "TransformerBase::readLine()
					// unexpected
					// end of
					// file." );
				}

				if ("".equals(line.trim()))
				{
					line = null;
				}

			} while (line == null);

			return line;
		}
		catch (IOException e)
		{
			throw new ModifierException("IO error while trying to access reader: " + e.getMessage());
		}
	}

	/**
	 * Discards one line of input from the input buffer.
	 */
	public void eatLine() throws ModifierException
	{
		getLine();
	}

	/**
	 * Outputs the next line from the intput buffer to the output file.
	 */
	public void printLine() throws ModifierException
	{
		write(getLine());
	}

	/**
	 * Outputs or discards a whole section from { to } including inner levels.
	 * 
	 * @param eat
	 */
	public void transformSection(boolean eat) throws ModifierException
	{
		// if (!line.matches("^\\s*\\{"))
		// throw new ModifierException( "TransformerBase::readLine() Section
		// must start with {" );

		int level = 0;
		do
		{
			String line = getLine();
			if (line.matches("^\\s*\\{"))
			{
				level++;
			}
			else if (line.matches("^\\s*}.*"))
			{
				level--;
			}
			if (!eat)
			{
				write(line);
			}
		} while (level > 0);
	}

	/**
	 * Writes the specified string to the output stream and terminates the line.
	 * 
	 * @param str
	 */
	public void write(String str) throws ModifierException
	{
		try
		{
			out.write(str);
			out.newLine();
		}
		catch (IOException e)
		{
			throw new ModifierException("Unable to write string: " + e.getMessage());
		}
	}

	/**
	 * Writes the specified string to the output stream.
	 * 
	 * @param str
	 */
	public void writenn(String str) throws ModifierException
	{
		try
		{
			out.write(str);
		}
		catch (IOException e)
		{
			throw new ModifierException("Unable to write string: " + e.getMessage());
		}
	}

	/**
	 * Entry hook. Must be implemented by concrete subclasses.
	 */
	abstract void run() throws ModifierException;
}
