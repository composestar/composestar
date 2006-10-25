package Composestar.RuntimeJava.Interface;

import java.lang.reflect.*;

import Composestar.RuntimeCore.Utils.ObjectInterface;

public class JavaObjectInterface extends ObjectInterface
{

	public JavaObjectInterface()
	{
		instance = this;
	}

	public String[] getFields(Object object)
	{
		if (object == null)
		{
			return new String[0];
		}

		Class type = object.getClass();
		Field[] fields = type.getFields();
		String[] result = new String[fields.length];
		for (int i = 0; i < result.length; i++)
		{
			result[i] = fields[i].getName();
		}
		return result;
	}

	public Object getFieldValue(Object object, String fieldName)
	{
		if (object == null) return null;
		Class type = object.getClass();
		Object result = null;
		Field field = null;
		boolean accessible = true;
		try
		{
			field = type.getField(fieldName);
			accessible = field.isAccessible();
			if (!accessible) field.setAccessible(true);
			Class fieldType = field.getType();

			if (fieldType.equals(int.class))
			{
				result = new Integer(field.getInt(object));
			}
			else if (fieldType.equals(long.class))
			{
				result = new Long(field.getLong(object));
			}
			else if (fieldType.equals(char.class))
			{
				result = new Character(field.getChar(object));
			}
			else if (fieldType.equals(double.class))
			{
				result = new Double(field.getDouble(object));
			}
			else if (fieldType.equals(float.class))
			{
				result = new Float(field.getFloat(object));
			}
			else if (fieldType.equals(short.class))
			{
				result = new Short(field.getShort(object));
			}
			else if (fieldType.equals(byte.class))
			{
				result = new Byte(field.getByte(object));
			}
			else if (fieldType.equals(boolean.class))
			{
				result = new Boolean(field.getBoolean(object));
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
			if (field != null) field.setAccessible(accessible);
		}
		return result;
	}
}
