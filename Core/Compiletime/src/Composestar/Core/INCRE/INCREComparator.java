package Composestar.Core.INCRE;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import Composestar.Core.Exception.ModuleException;
import Composestar.Utils.Debug;

public class INCREComparator
{
	protected HashMap myFields = new HashMap();

	protected HashMap comparisons = new HashMap();

	protected String module;

	protected int compare;

	public INCREComparator(String inModule)
	{
		module = inModule;
	}

	public int getCompare()
	{
		return compare;
	}

	public void clearComparisons()
	{
		comparisons.clear();
	}

	public int duplicates;

	/*
	 * Compares two objects returns true if equals and false otherwise
	 */
	public boolean compare(Object a, Object b) throws ModuleException
	{
		// Keep track of the number of comparisons made
		compare++;

		// special cases: one or both objects are null
		if (a == null && b == null)
		{
			return true;
		}
		else if (a == null || b == null)
		{
			return false;
		}

		// first check the types of both objects
		if (a.getClass().getName().equals(b.getClass().getName()))
		{
			if (a.getClass().equals(String.class))
			{
				// easy case
				return a.equals(b);
			}
			else if (a.getClass().equals(Integer.class))
			{
				// easy case
				return a.equals(b);
			}
			else if (a.getClass().equals(Boolean.class))
			{
				// easy case
				return a.equals(b);
			}
			else if (a instanceof AbstractList)
			{
				// compare abstract list
				return compareAbstractLists((AbstractList) a, (AbstractList) b);
			}
			else if (a instanceof HashSet)
			{
				// compare HashSets
				return compareAbstractSets((HashSet) a, (HashSet) b);
			}
			else
			{
				if (hasComparableObjects(a))
				{
					// compare all INCRE fields
					if (!compareINCREfields(a, b))
					{
						return false;
					}
				}
				else
				{
					// iterate over all public fields
					Enumeration enumFields = getFields(a.getClass()).elements();
					while (enumFields.hasMoreElements())
					{
						Field field = (Field) enumFields.nextElement();

						try
						{
							// only public fields are compared
							if (!compare(field.get(a), field.get(b)))
							{
								return false;
							}
						}
						catch (Exception excep)
						{
							throw new ModuleException("MyComparator error: " + excep.getMessage(), "INCRE");
						}
					}
				}
			}

			return true;
		}

		return false;
	}

	/**
	 * @param AbstractList a1
	 * @param AbstractList a2
	 * @return
	 * @param a1
	 * @param a2
	 */
	public boolean compareAbstractLists(AbstractList a1, AbstractList a2) throws ModuleException
	{
		if (a1.size() != a2.size()) // compare sizes first
		{
			return false;
		}
		else
		{// compare all objects in the ArrayList
			for (int i = 0; i < a1.size(); i++)
			{
				Object obj1 = a1.get(i);
				Object obj2 = a2.get(i);
				if (!compare(obj1, obj2))
				{
					// no match, return false
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * @param HashSet s1
	 * @param HashSet s2
	 * @return
	 * @param s2
	 * @param s1
	 */
	public boolean compareAbstractSets(AbstractSet s1, AbstractSet s2) throws ModuleException
	{
		if (s1.size() != s2.size()) // compare sizes first
		{
			return false;
		}
		else
		{// compare all objects in the HashSet

			Iterator iter1 = s1.iterator();
			Iterator iter2 = s2.iterator();
			while (iter1.hasNext())
			{
				Object obj1 = iter1.next();
				Object obj2 = iter2.next();
				if (!compare(obj1, obj2))
				{
					// no match, return false
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * @param Class
	 * @return
	 * @param c
	 */
	public Vector getFields(Class c)
	{
		if (myFields.containsKey(c))
		{
			return (Vector) myFields.get(c);
		}

		Vector fields = new Vector();
		Stack stack = new Stack();
		Class myClass = c;
		// while( !myClass.equals(Object.class) )
		// {
		stack.push(myClass);
		// myClass = myClass.getSuperclass();
		// }

		while (!stack.empty())
		{
			myClass = (Class) stack.pop();
			Field[] declaredFields = myClass.getDeclaredFields();
			for (Field declaredField : declaredFields)
			{
				// can only check public fields
				if (Modifier.isPublic(declaredField.getModifiers()))
				{
					Field f = declaredField;
					// IMPORTANT: skip repositoryKey due to different hashcodes
					if (!f.getName().equals("repositoryKey"))
					{
						fields.add(f);
					}
				}
			}
		}

		myFields.put(c, fields);
		return fields;
	}

	/**
	 * Adds the result of a comparison to the map
	 * 
	 * @param id The id of the object compared
	 * @param result The result of the comparison
	 * @return
	 */
	public void addComparison(String id, boolean result)
	{
		if (id != null)
		{
			Boolean b = result;
			comparisons.put(id, b);
		}
		else
		{
			Debug.out(Debug.MODE_DEBUG, "INCRE::Comparator", "Key of comparison is null!");
		}
	}

	/**
	 * Check whether an object has been compared before
	 * 
	 * @param id The id of the object
	 * @return true if object is in map, false if not
	 */
	public boolean comparisonMade(String id)
	{
		return comparisons.containsKey(id);
	}

	/*
	 * Get the result of an earlier comparison @param id The id of the object
	 * compared @return boolean The result of the comparison
	 */
	public boolean getComparison(String id)
	{
		return (Boolean) comparisons.get(id);
	}

	public boolean hasComparableObjects(Object obj)
	{
		String fullname = obj.getClass().getName();
		INCRE incre = INCRE.instance();
		INCREModule m = incre.getConfigManager().getModuleByID(this.module);

		return m.hasComparableObjects(fullname);
	}

	/**
	 * Compares all 'INCRE fields' of two objects The fields are acquired from
	 * the Module and were extracted from the incre configuration file
	 * 
	 * @param b
	 * @param a
	 */
	public boolean compareINCREfields(Object a, Object b) throws ModuleException
	{
		try
		{
			INCRE incre = INCRE.instance();
			String fullname = a.getClass().getName();
			INCREModule m = incre.getConfigManager().getModuleByID(this.module);
			ArrayList compObjects = m.getComparableObjects(fullname);
			boolean equal;
			String key = null;

			for (Object obj : compObjects)
			{
				Object fielda = null;
				Object fieldb = null;

				if (obj instanceof FieldNode)
				{
					FieldNode fieldnode = (FieldNode) obj;
					key = fieldnode.getUniqueID(a) + b.hashCode();
					fielda = fieldnode.visit(a);
					fieldb = fieldnode.visit(b);
				}
				else if (obj instanceof MethodNode)
				{
					MethodNode methodnode = (MethodNode) obj;
					key = methodnode.getUniqueID(a) + b.hashCode();
					fielda = methodnode.visit(a);
					fieldb = methodnode.visit(b);
				}
				else if (obj instanceof Path)
				{
					Path path = (Path) obj;
					fielda = path.follow(a);
					fieldb = path.follow(b);
				}

				if (key != null && comparisonMade(key))
				{
					// already made comparison before
					duplicates++;
					equal = getComparison(key);
				}
				else
				{
					if (key != null)// temporarily true to avoid infinite loops
					{
						addComparison(key, true);
					}

					equal = compare(fielda, fieldb);

					if (key != null)// store result of comparison
					{
						addComparison(key, equal);
					}
				}

				if (!equal) // stop comparison by returning false
				{
					return false;
				}
			}
		}
		catch (Exception e)
		{
			throw new ModuleException("MyComparator error: " + e.toString(), "INCRE");
		}

		return true;
	}
}