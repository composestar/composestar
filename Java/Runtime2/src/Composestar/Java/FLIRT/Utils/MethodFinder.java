/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2005-2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */
package Composestar.Java.FLIRT.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Finds methods and constructors that can be invoked reflectively. Attempts to
 * address some of the limitations of the JDK's Class.getMethod() and
 * Class.getConstructor(), and other JDK reflective facilities. This code was
 * adapted from code provided by Paul Hosler in an article for Application
 * Development Trends: http://www.adtmag.com/java/article.aspx?id=4337
 */
public final class MethodFinder
{
	/**
	 * The target class to look for methods and constructors in.
	 */
	private final Class<?> clazz;

	/**
	 * Mapping from method name to the Methods in the target class with that
	 * name.
	 */
	private final Map<String, List<Method>> methodMap = new HashMap<String, List<Method>>();

	/**
	 * List of the Constructors in the target class.
	 */
	private final List<Constructor<?>> ctorList = new ArrayList<Constructor<?>>();

	/**
	 * Mapping from a Constructor or Method object to the Class objects
	 * representing its formal parameters.
	 */
	private final Map<Member, Class<?>[]> paramMap = new HashMap<Member, Class<?>[]>();

	/**
	 * @param clazz Class in which we will look for methods and constructors
	 * @exception IllegalArgumentException if clazz is null, or represents a
	 *                primitive, or represents an array type
	 */
	public MethodFinder(Class<?> clazz)
	{
		if (clazz == null)
		{
			throw new IllegalArgumentException("null Class parameter");
		}

		if (clazz.isPrimitive())
		{
			throw new IllegalArgumentException("primitive Class parameter");
		}

		if (clazz.isArray())
		{
			throw new IllegalArgumentException("array Class parameter");
		}

		this.clazz = clazz;

		loadMethods();
		loadConstructors();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		else if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		else
		{
			MethodFinder other = (MethodFinder) o;
			return clazz.equals(other.clazz);
		}
	}

	/**
	 * Returns the most specific public constructor in my target class that
	 * accepts the number and type of parameters in the given Class array in a
	 * reflective invocation.
	 * <p>
	 * A null value or Void.TYPE in parameterTypes matches a corresponding
	 * Object or array reference in a constructor's formal parameter list, but
	 * not a primitive formal parameter.
	 * 
	 * @param parameterTypes array representing the number and types of
	 *            parameters to look for in the constructor's signature. A null
	 *            array is treated as a zero-length array.
	 * @return Constructor object satisfying the conditions
	 * @exception NoSuchMethodException if no constructors match the criteria,
	 *                or if the reflective call is ambiguous based on the
	 *                parameter types
	 */
	public Constructor<?> findConstructor(Class<?>[] parameterTypes) throws NoSuchMethodException
	{
		if (parameterTypes == null)
		{
			parameterTypes = new Class[0];
		}

		return (Constructor<?>) findMemberIn(ctorList, parameterTypes);
	}

	/**
	 * Basis of findConstructor() and findMethod(). The member list fed to this
	 * method will be either all Constructor objects or all Method objects.
	 */
	private Member findMemberIn(List<? extends Member> memberList, Class<?>[] parameterTypes)
			throws NoSuchMethodException
	{
		List<Member> matchingMembers = new ArrayList<Member>();

		for (Member member : memberList)
		{
			Class<?>[] methodParamTypes = paramMap.get(member);

			if (Arrays.equals(methodParamTypes, parameterTypes))
			{
				return member;
			}

			// TODO: check this to be valid !!!!!!!
			if (ClassUtilities.compatibleClasses(methodParamTypes, parameterTypes))
			{
				matchingMembers.add(member);
			}
		}

		if (matchingMembers.isEmpty())
		{
			throw new NoSuchMethodException("no member in " + clazz.getName() + " matching given args");
		}

		if (matchingMembers.size() == 1)
		{
			return matchingMembers.get(0);
		}

		return findMostSpecificMemberIn(matchingMembers);
	}

	/**
	 * Returns the most specific public method in my target class that has the
	 * given name and accepts the number and type of parameters in the given
	 * Class array in a reflective invocation.
	 * <p>
	 * A null value or Void.TYPE in parameterTypes will match a corresponding
	 * Object or array reference in a method's formal parameter list, but not a
	 * primitive formal parameter.
	 * 
	 * @param methodName name of the method to search for
	 * @param parameterTypes array representing the number and types of
	 *            parameters to look for in the method's signature. A null array
	 *            is treated as a zero-length array.
	 * @return Method object satisfying the conditions
	 * @exception NoSuchMethodException if no methods match the criteria, or if
	 *                the reflective call is ambiguous based on the parameter
	 *                types, or if methodName is null
	 */
	public Method findMethod(String methodName, Class<?>[] parameterTypes) throws NoSuchMethodException
	{
		List<Method> methodList = methodMap.get(methodName);

		if (methodList == null)
		{
			throw new NoSuchMethodException("no method named " + clazz.getName() + "." + methodName);
		}

		if (parameterTypes == null)
		{
			parameterTypes = new Class[0];
		}

		return (Method) findMemberIn(methodList, parameterTypes);
	}

	/**
	 * @param a List of Members (either all Constructors or all Methods)
	 * @return the most specific of all Members in the list
	 * @exception NoSuchMethodException if there is an ambiguity as to which is
	 *                most specific
	 */
	private Member findMostSpecificMemberIn(List<? extends Member> memberList) throws NoSuchMethodException
	{
		List<Member> mostSpecificMembers = new ArrayList<Member>();

		for (Object aMemberList : memberList)
		{
			Member member = (Member) aMemberList;

			if (mostSpecificMembers.isEmpty())
			{
				// First guy in is the most specific so far.
				mostSpecificMembers.add(member);
			}
			else
			{
				boolean moreSpecific = true;
				boolean lessSpecific = false;

				// Is member more specific than everyone in the most-specific
				// set?
				for (Object mostSpecificMember : mostSpecificMembers)
				{
					Member moreSpecificMember = (Member) mostSpecificMember;

					if (!memberIsMoreSpecific(member, moreSpecificMember))
					{
						/*
						 * Can't be more specific than the whole set. Bail out,
						 * and mark whether member is less specific than the
						 * member under consideration. If it is less specific,
						 * it need not be added to the ambiguity set. This is no
						 * guarantee of not getting added to the ambiguity
						 * set...we're just not clever enough yet to make that
						 * assessment.
						 */

						moreSpecific = false;
						lessSpecific = memberIsMoreSpecific(moreSpecificMember, member);
						break;
					}
				}

				if (moreSpecific)
				{
					// Member is the most specific now.
					mostSpecificMembers.clear();
					mostSpecificMembers.add(member);
				}
				else if (!lessSpecific)
				{
					// Add to ambiguity set if mutually unspecific.
					mostSpecificMembers.add(member);
				}
			}
		}

		if (mostSpecificMembers.size() > 1)
		{
			throw new NoSuchMethodException("...");
		}

		return mostSpecificMembers.get(0);
	}

	/**
	 * @param args an Object array
	 * @return an array of Class objects representing the classes of the objects
	 *         in the given Object array. If args is null, a zero-length Class
	 *         array is returned. If an element in args is null, then Void.TYPE
	 *         is the corresponding Class in the return array.
	 */
	public static Class<?>[] getParameterTypesFrom(Object[] args)
	{
		Class<?>[] argTypes = null;

		if (args != null)
		{
			argTypes = new Class[args.length];

			for (int i = 0; i < args.length; ++i)
			{
				argTypes[i] = args[i] == null ? Void.TYPE : args[i].getClass();
			}
		}
		else
		{
			argTypes = new Class[0];
		}

		return argTypes;
	}

	/**
	 * @param classNames String array of fully qualified names (FQNs) of classes
	 *            or primitives. Represent an array type by using its JVM type
	 *            descriptor, with dots instead of slashes (e.g. represent the
	 *            type int[] with "[I", and Object[][] with
	 *            "[[Ljava.lang.Object;").
	 * @return an array of Class objects representing the classes or primitives
	 *         named by the FQNs in the given String array. If the String array
	 *         is null, a zero-length Class array is returned. If an element in
	 *         classNames is null, the empty string, "void", or "null", then
	 *         Void.TYPE is the corresponding Class in the return array. If any
	 *         classes require loading because of this operation, the loading is
	 *         done by the ClassLoader that loaded this class. Such classes are
	 *         not initialized, however.
	 * @exception ClassNotFoundException if any of the FQNs name an unknown
	 *                class
	 */
	public static Class<?>[] getParameterTypesFrom(String[] classNames) throws ClassNotFoundException
	{
		return getParameterTypesFrom(classNames, MethodFinder.class.getClassLoader());
	}

	/**
	 * @param classNames String array of fully qualified names (FQNs) of classes
	 *            or primitives. Represent an array type by using its JVM type
	 *            descriptor, with dots instead of slashes (e.g. represent the
	 *            type int[] with "[I", and Object[][] with
	 *            "[[Ljava.lang.Object;").
	 * @param loader a ClassLoader
	 * @return an array of Class objects representing the classes or primitives
	 *         named by the FQNs in the given String array. If the String array
	 *         is null, a zero-length Class array is returned. If an element in
	 *         classNames is null, the empty string, "void", or "null", then
	 *         Void.TYPE is the corresponding Class in the return array. If any
	 *         classes require loading because of this operation, the loading is
	 *         done by the given ClassLoader. Such classes are not initialized,
	 *         however.
	 * @exception ClassNotFoundException if any of the FQNs name an unknown
	 *                class
	 */
	public static Class<?>[] getParameterTypesFrom(String[] classNames, ClassLoader loader)
			throws ClassNotFoundException
	{
		Class<?>[] types = null;

		if (classNames != null)
		{
			types = new Class[classNames.length];

			for (int i = 0; i < classNames.length; ++i)
			{
				types[i] = ClassUtilities.classForNameOrPrimitive(classNames[i], loader);
			}
		}
		else
		{
			types = new Class[0];
		}

		return types;
	}

	@Override
	public int hashCode()
	{
		return clazz.hashCode();
	}

	/**
	 * Loads up the data structures for my target class's constructors.
	 */
	private void loadConstructors()
	{
		Constructor<?>[] ctors = clazz.getConstructors();

		for (Constructor<?> ctor : ctors)
		{
			ctorList.add(ctor);
			paramMap.put(ctor, ctor.getParameterTypes());
		}
	}

	/**
	 * Loads up the data structures for my target class's methods.
	 */
	private void loadMethods()
	{

		Method[] methods = clazz.getMethods();

		for (Method m : methods)
		{
			String methodName = m.getName();
			Class<?>[] paramTypes = m.getParameterTypes();

			List<Method> list = methodMap.get(methodName);

			if (list == null)
			{
				list = new ArrayList<Method>();
				methodMap.put(methodName, list);
			}

			/*
			 * if (! ClassUtilities.classIsAccessible(clazz)) m =
			 * ClassUtilities.getAccessibleMethodFrom(clazz, methodName,
			 * paramTypes );
			 */

			list.add(m);
			paramMap.put(m, paramTypes);
		}

		methods = clazz.getDeclaredMethods();
		for (Method m : methods)
		{
			String methodName = m.getName();
			Class<?>[] paramTypes = m.getParameterTypes();

			List<Method> list = methodMap.get(methodName);

			if (list == null)
			{
				list = new ArrayList<Method>();
				methodMap.put(methodName, list);
			}

			/*
			 * if (! ClassUtilities.classIsAccessible(clazz)) m =
			 * ClassUtilities.getAccessibleMethodFrom(clazz, methodName,
			 * paramTypes );
			 */

			list.add(m);
			paramMap.put(m, paramTypes);
		}
	}

	/**
	 * @param first a Member
	 * @param second a Member
	 * @return true if the first Member is more specific than the second, false
	 *         otherwise. Specificity is determined according to the procedure
	 *         in the Java Language Specification, section 15.12.2.
	 */
	private boolean memberIsMoreSpecific(Member first, Member second)
	{
		Class<?>[] firstParamTypes = paramMap.get(first);
		Class<?>[] secondParamTypes = paramMap.get(second);

		return ClassUtilities.compatibleClasses(secondParamTypes, firstParamTypes);
	}
}
