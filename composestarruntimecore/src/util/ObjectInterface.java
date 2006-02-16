package Composestar.RuntimeCore.Utils;

/**
 * Summary description for ObjectInterface.
 */
public abstract class ObjectInterface
{
	protected static final String INTERNAL_STING_VALUE = "Realvalue";

	public static ObjectInterface getInstance()
		{
		if(instance == null)
		{
			Debug.out(Debug.MODE_ERROR,"ObjectInterface","Object Interface instance called without platform instanciation");
		}
		return instance;
	}

	protected static ObjectInterface instance = null;

	public abstract String[] getFields(Object object);

	public abstract Object getFieldValue(Object object,String name);
}
