package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id$
 * 
 **/

import java.util.LinkedList;

public class Column implements Cloneable
{
	private LinkedList colList = new LinkedList();

	public int length = 0;

	public Column()
	{}

	public Column(int size)
	{
		this(size, false);
	}

	public Column(int size, boolean setBoolean)
	{
		addElements(size, setBoolean);
	}

	public Column(boolean[] array)
	{
		addElements(array);
	}

	public Column(int size, int trueBits, int multFalse, int multPadding)
	{
		length = size;
		colList.add(new boolean[size]);
		setPattern(trueBits, multFalse, multPadding);
	}

	public void addElements(int size, boolean setBoolean)
	{
		length += size;

		boolean[] col = new boolean[size];
		for (int i = 0; i < size; i++)
			col[i] = setBoolean;
		colList.add(col);
	}

	public void addElements(boolean[] array)
	{
		length += array.length;

		boolean[] col = new boolean[array.length];
		System.arraycopy(array, 0, col, 0, array.length);
		colList.add(col);
	}

	public void addElements(Column b)
	{
		length += b.length;
		boolean[] col = new boolean[b.length];
		for (int i = 0; i < b.length; i++)
			col[i] = b.getValue(i);
		colList.add(col);
	}

	public void addElements(boolean[] array, int overrideRow)
	{
		length += array.length - 1;

		boolean[] col = new boolean[array.length - 1];
		System.arraycopy(array, 1, col, 0, array.length);
		colList.add(col);

		setValue(overrideRow, array[0]);
	}

	public void addElements(int size, int trueBits, int multFalse, int multPadding)
	{
		// TODO
		length += size;
		colList.add(new boolean[size]);
		setPattern(trueBits, multFalse, multPadding);
	}

	public void setFalse()
	{
		setAllValues(false);
	}

	public void setFalse(int row)
	{
		setValue(row, false);
	}

	public void setTrue()
	{
		setAllValues(true);
	}

	public void setTrue(int row)
	{
		setValue(row, true);
	}

	public void setAllValues(boolean value)
	{
		setAllValues(value, 0);
	}

	public void setAllValues(boolean value, int offset)
	{
		int table = getTable(offset);
		int index = getTableIndex(offset);

		// Finish this table.
		boolean[] col = (boolean[]) colList.get(table);
		for (int k = index; k < col.length; k++)
			col[k] = value;

		// and the rest.
		for (int i = table + 1; i < colList.size(); i++)
		{
			col = (boolean[]) colList.get(i);
			for (int k = 0; k < col.length; k++)
				col[k] = value;
		}
	}

	public void setValue(int row, boolean value)
	{
		int table = getTable(row);
		int index = getTableIndex(row);

		boolean[] col = (boolean[]) colList.get(table);
		col[index] = value;
	}

	public boolean getValue(int row)
	{
		int table = getTable(row);
		int index = getTableIndex(row);

		boolean[] col = (boolean[]) colList.get(table);
		return col[index];
	}

	/**
	 * Fill the internal column with a certain pattern of bits. trueBits: The
	 * block size, the number of true bits that occur after each other. Example
	 * trueBits 2: [1 1 0 0 0 0 1 1 0 0 ] multFalse: Multiplication of the, true
	 * bits, block size that are false and occur after each other. Example
	 * multFalse 3: [ 1 1 0 0 0 0 0 0 1 1 ] multPadding: Multiplication of the
	 * true bits that starts the pattern. Example trueBits 2, multFalse 1,
	 * multPadding 2: [ 0 0 0 0 1 1 0 0 1 1 0 0]
	 * 
	 * @param multFalse
	 * @param offset
	 * @param multPadding
	 * @param trueBits
	 */

	public void setPattern(int trueBits, int multFalse, int multPadding, int offset)
	{
		setAllValues(false, offset);
		int padding = multPadding * trueBits;
		int columnLength = length - padding;
		int totalBits = (multFalse + 1) * trueBits;

		for (int i = offset; i < columnLength; i++)
		{
			if (((i - offset) % totalBits) < trueBits)
			{
				// TODO inefficient
				setValue(i + padding, true);
			}
		}
	}

	public void setPattern(int trueBits, int multFalse, int multPadding)
	{
		setPattern(trueBits, multFalse, multPadding, 0);
	}

	public String toString()
	{
		String str = "[";
		for (int k = 0; k < colList.size(); k++)
		{
			boolean[] col = (boolean[]) colList.get(k);

			for (int i = 0; i < col.length; i++)
				str += (col[i] ? " 1" : " 0");
		}

		str += " ]";
		return str;
	}

	public Object clone() throws CloneNotSupportedException
	{
		try
		{
			Column column = (Column) super.clone();
			column.colList = new LinkedList();

			for (int table = 0; table < colList.size(); table++)
			{
				boolean[] col = (boolean[]) colList.get(table);
				boolean[] coltmp = new boolean[col.length];

				System.arraycopy(col, 0, coltmp, 0, col.length);
				column.colList.add(coltmp);
			}

			return column;
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}

	// /////////////////////// PRIVATE STUFF ///////////////////////////////

	private int getTable(int row)
	{
		int totalLength = 0;
		for (int i = 0; i < colList.size(); i++)
		{
			totalLength += ((boolean[]) colList.get(i)).length;
			if (totalLength > row) return i;
		}

		return -1;
	}

	private int getTableIndex(int row)
	{
		int totalLength = 0;
		for (int i = 0; i < getTable(row); i++)
		{
			totalLength += ((boolean[]) colList.get(i)).length;
		}
		return row - totalLength;
	}

}
