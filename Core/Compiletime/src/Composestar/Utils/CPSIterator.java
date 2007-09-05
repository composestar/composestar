package Composestar.Utils;

import java.util.Iterator;
import java.util.Vector;

//
// !! Compose* Runtime Warning !!
//
// This class is referenced in the Compose* Runtime for .NET 1.1
// Do not use Java features added after Java 2.0
//

/**
 * This class is implemented only for backward migration purposes to jdk 1.1.4.
 * Used in the runtime!
 * 
 * @author Gurcan Gulesir
 * @deprecated
 */
public class CPSIterator implements Iterator
{
	private Vector v;

	private int index;

	public CPSIterator()
	{
		v = new Vector();
		index = 0;
	}

	public CPSIterator(Vector inv)
	{
		v = inv;
		index = 0;
	}

	public boolean hasNext()
	{
		return index < v.size();
	}

	public Object next()
	{
		Object o = v.elementAt(index);
		index++;
		return o;
	}

	public void remove() throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("remove method of a CPSIterator must not be called.");
	}
}
