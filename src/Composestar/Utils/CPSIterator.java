package Composestar.Utils;

import java.util.Iterator;
import java.util.Vector;

/**
 * This class is implemented only for backward migration purposes to jdk 1.1.4.
 * 
 * @author Gurcan Gulesir
 */
public class CPSIterator implements Iterator
{
	private Vector v;

	private int index;

	/**
	 * @roseuid 40ED13D002BE
	 */
	public CPSIterator()
	{
		this.v = new Vector();
		this.index = 0;
	}

	/**
	 * @param v
	 * @roseuid 40ADF325008E
	 */
	public CPSIterator(Vector inv)
	{
		v = inv;
		index = 0;
	}

	/**
	 * @return boolean
	 * @roseuid 40ADF32500A3
	 */
	public boolean hasNext()
	{
		return index < v.size();
	}

	/**
	 * @return java.lang.Object
	 * @roseuid 40ADF32500AC
	 */
	public Object next()
	{
		Object o = v.elementAt(index);
		index++;
		return o;
	}

	/**
	 * @throws java.lang.UnsupportedOperationException
	 * @roseuid 40ADF32500C0
	 */
	public void remove() throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("remove method of a CPSIterator must not be called.");
	}
}
