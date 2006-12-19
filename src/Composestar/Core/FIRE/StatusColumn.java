package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * StatusColumn.java 2282 2006-10-25 14:12:31Z arjanderoo $
 */

public class StatusColumn extends Column implements Cloneable
{
	private Column finished = null;

	public StatusColumn(int size)
	{
		this(size, false);
	}

	public StatusColumn(int size, boolean setBoolean)
	{
		finished = new Column(size, false);
		addElements(size, setBoolean);
	}

	public StatusColumn(boolean[] array)
	{
		finished = new Column(array.length, false);
		addElements(array);
	}

	public StatusColumn(int size, int trueBits, int multFalse, int multPadding)
	{
		super(size, trueBits, multFalse, multPadding);

		finished = new Column(size, false);
	}

	public void addElements(int size, boolean setBoolean)
	{
		super.addElements(size, setBoolean);
		finished.addElements(size, false);
	}

	public void addElements(boolean[] array)
	{
		super.addElements(array);
		finished.addElements(array.length, false);
	}

	public void addElements(boolean[] array, int overrideRow)
	{
		super.addElements(array, overrideRow);
		finished.addElements(array.length - 1, false);
	}

	public void addElements(int size, int trueBits, int multFalse, int multPadding)
	{
		super.addElements(size, trueBits, multFalse, multPadding);
		finished.addElements(size, false);
	}

	public Object clone() throws CloneNotSupportedException
	{
		StatusColumn statusColumn = (StatusColumn) super.clone();
		statusColumn.finished = (Column) finished.clone();
		return statusColumn;
	}

	public void finish(boolean value)
	{
		for (int i = 0; i < length; i++)
		{
			if (getValue(i) == value)
			{
				finished.setValue(i, true);
			}
		}
	}

	public void finish(int row)
	{
		finished.setValue(row, true);
	}

	public boolean isFinished(int row)
	{
		return finished.getValue(row);
	}

	public boolean isActive(int row)
	{
		return !isFinished(row) && getValue(row);
	}

}
