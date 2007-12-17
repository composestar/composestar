package Composestar.Java.TYM.SignatureTransformer;

import Composestar.Core.CpsProgramRepository.Concern;

/**
 * Helper Class. It contains a <code>class</code> instance, a
 * <code>Concern</code> instance (to retrieve the changed signature) and the
 * bytecode of the transformed class.
 */
class ClassWrapper
{

	private Class<?> theClass;

	private Concern concern;

	private byte[] bytecode;

	/**
	 * Constructor
	 * 
	 * @param aClass - a class instance.
	 * @param concern - the concern representing the class.
	 * @param bytecode - the bytecode of the class.
	 */
	public ClassWrapper(Class<?> aClass, Concern concern, byte[] bytecode)
	{
		theClass = aClass;
		this.concern = concern;
		this.bytecode = bytecode;
	}

	/**
	 * Sets the bytecode.
	 * 
	 * @param bytecode - the bytecode.
	 */
	public void setByteCode(byte[] bytecode)
	{
		this.bytecode = bytecode;
	}

	/**
	 * Returns the class object.
	 */
	public Class<?> getClazz()
	{
		return theClass;
	}

	/**
	 * Returns the concern object.
	 */
	public Concern getConcern()
	{
		return concern;
	}

	/**
	 * Returns the bytecode.
	 */
	public byte[] getByteCode()
	{
		return bytecode;
	}
}
