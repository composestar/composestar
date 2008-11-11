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
package Composestar.RuntimeJava.Interface;

import java.lang.reflect.Field;

import Composestar.RuntimeCore.Utils.ObjectInterface;

public class JavaObjectInterface extends ObjectInterface
{
	public JavaObjectInterface()
	{
		instance = this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.RuntimeCore.Utils.ObjectInterface#getFields(java.lang.Object)
	 */
	@Override
	public String[] getFields(Object object)
	{
		if (object == null)
		{
			return new String[0];
		}

		Class<?> type = object.getClass();
		Field[] fields = type.getFields();
		String[] result = new String[fields.length];
		for (int i = 0; i < result.length; i++)
		{
			result[i] = fields[i].getName();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.RuntimeCore.Utils.ObjectInterface#getFieldValue(java.lang
	 * .Object, java.lang.String)
	 */
	@Override
	public Object getFieldValue(Object object, String fieldName)
	{
		if (object == null)
		{
			return null;
		}
		Class<?> type = object.getClass();
		Object result = null;
		Field field = null;
		boolean accessible = true;
		try
		{
			field = type.getField(fieldName);
			accessible = field.isAccessible();
			if (!accessible)
			{
				field.setAccessible(true);
			}
			Class<?> fieldType = field.getType();

			if (fieldType.equals(int.class))
			{
				result = field.getInt(object);
			}
			else if (fieldType.equals(long.class))
			{
				result = field.getLong(object);
			}
			else if (fieldType.equals(char.class))
			{
				result = field.getChar(object);
			}
			else if (fieldType.equals(double.class))
			{
				result = field.getDouble(object);
			}
			else if (fieldType.equals(float.class))
			{
				result = field.getFloat(object);
			}
			else if (fieldType.equals(short.class))
			{
				result = field.getShort(object);
			}
			else if (fieldType.equals(byte.class))
			{
				result = field.getByte(object);
			}
			else if (fieldType.equals(boolean.class))
			{
				result = field.getBoolean(object);
			}
			else
			{
				result = field.get(object);
			}
		}
		catch (Exception e)
		{
			result = null;
		}
		finally
		{
			if (field != null)
			{
				field.setAccessible(accessible);
			}
		}
		return result;
	}
}
