/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.RepositoryImplementation;

//
// !! Compose* Runtime Warning !!
//
// This class is referenced in the Compose* Runtime for .NET 1.1
// Do not use Java features added after Java 2.0
//

/**
 * This is a RepositoryEntity that is context-sensitive in the sense that it
 * contains a reference to its encapsulating parent. This parent is typically
 * set during instantiation (i.e. through the constructor)
 */
public class ContextRepositoryEntity extends RepositoryEntity
{
	private static final long serialVersionUID = 9219062801100391060L;

	/**
	 * If this flag is set the entity is part of the default inner dispatch
	 * filter.
	 */
	public static final long FLAG_DEFAULT_FILTER = 0x1L;

	/**
	 * Special flags used by the Compose* compiler
	 */
	private long cpsFlags;

	/**
	 * owner of this entity
	 */
	private Object parent;

	public ContextRepositoryEntity()
	{
		super();
	}

	/**
	 * @param parent
	 */
	public ContextRepositoryEntity(RepositoryEntity parent)
	{
		setParent(parent);
	}

	/**
	 * @return java.lang.Object
	 */
	public Object getParent()
	{
		return parent;
	}

	/**
	 * @param parentValue
	 */
	public void setParent(Object parentValue)
	{
		this.parent = parentValue;
		this.updateRepositoryReference();
	}

	/**
	 * Return the ancestor that matches the given class, or null if no such
	 * ancestor
	 * 
	 * @param reqClass
	 * @return
	 */
	public Object getAncestorOfClass(Class reqClass)
	{
		Object o = getParent();
		while (o instanceof ContextRepositoryEntity)
		{
			if (o.getClass().equals(reqClass))
			{
				return o;
			}
			o = ((ContextRepositoryEntity) o).getParent();
		}
		return null;
	}

	public Object clone() throws CloneNotSupportedException
	{
		ContextRepositoryEntity newObject;

		newObject = (ContextRepositoryEntity) super.clone();

		// At this point, the newObject shares all data with the object
		// running clone. If you want newObject to have its own
		// copy of data, you must clone this data yourself.

		return newObject;
	}

	/**
	 * Returns the string representation of this element as source code. The
	 * result should be semantical identical to the original code, it does not
	 * have to be the same as the original since the syntactic sugar has been
	 * applied. Returns null when this element can not be translated to a source
	 * code representation.
	 */
	public String asSourceCode()
	{
		return null;
	}

	public void setFlag(long flag)
	{
		cpsFlags |= flag;
	}

	public void unsetFlag(long flag)
	{
		cpsFlags &= ~flag;
	}

	public long getFlags()
	{
		return cpsFlags;
	}
}
