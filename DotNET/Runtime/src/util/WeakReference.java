package java.lang.ref;

public class WeakReference
{
	private System.WeakReference ref;

	public WeakReference(Object referent)
	{
		ref = new System.WeakReference(referent);
	}

	public Object get()
	{
		if (ref.get_IsAlive())
		{
			return ref.get_Target();
		}
		return null;
	}
}
