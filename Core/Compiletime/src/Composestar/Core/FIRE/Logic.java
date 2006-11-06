package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * Logic.java 2032 2006-10-12 15:08:13Z reddog33hummer $
 */

class Logic
{
	/*
	 * LeftHand will be changed.
	 */
	public static void or(boolean[] lh, boolean[] rh)
	{
		for (int i = 0; i < lh.length; i++)
		{
			lh[i] |= rh[i];
		}
	}

	/*
	 * LeftHand will be changed. TODO: Not checked if they have the same length
	 */
	public static void or(Column lh, Column rh)
	{
		int smallestCol = Logic.smallestColumn(lh, rh);

		for (int i = 0; i < smallestCol; i++)
		{
			lh.setValue(i, lh.getValue(i) | rh.getValue(i));
		}
	}

	/*
	 * Changes the left hand side.
	 */
	public static void and(boolean[] lh, boolean[] rh)
	{
		for (int i = 0; i < lh.length; i++)
		{
			lh[i] &= rh[i];
		}
	}

	private static int smallestColumn(Column lh, Column rh)
	{
		if (lh.length < rh.length) return lh.length;
		return rh.length;
	}

	/**
	 * TODO: Review: Not checked if they have the same length
	 * 
	 * @param rh
	 * @param lh
	 */
	public static void and(Column lh, Column rh)
	{
		int smallestCol = Logic.smallestColumn(lh, rh);

		for (int i = 0; i < smallestCol; i++)
		{
			lh.setValue(i, lh.getValue(i) & rh.getValue(i));
		}
	}

	public static void xor(boolean[] lh, boolean[] rh)
	{
		for (int i = 0; i < lh.length; i++)
		{
			lh[i] ^= rh[i];
		}
	}

	public static void implies(boolean[] lh, boolean[] rh)
	{
		for (int i = 0; i < lh.length; i++)
		{
			lh[i] = !lh[i] || rh[i];
		}
	}

	public static void equivalence(boolean[] lh, boolean[] rh)
	{
		for (int i = 0; i < lh.length; i++)
		{
			lh[i] = (lh[i] && rh[i]) || (!lh[i] && !rh[i]);
		}
	}

	public static void not(boolean[] lh)
	{
		for (int i = 0; i < lh.length; i++)
		{
			lh[i] = !lh[i];
		}
	}

	public static void not(Column lh)
	{
		for (int i = 0; i < lh.length; i++)
		{
			lh.setValue(i, !lh.getValue(i));
		}
	}

	public static boolean allZero(Column lh)
	{
		for (int i = 0; i < lh.length; i++)
		{
			if (lh.getValue(i)) return false;
		}

		return true;
	}
}
