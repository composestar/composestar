/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
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

package Composestar.Core.CpsRepository2Impl.TypeSystem;

import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2.TypeSystem.CpsProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsTypeProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;

/**
 * Implementation of the CpsTypeProgramElement interface
 * 
 * @author Michiel Hendriks
 */
public class CpsTypeProgramElementImpl extends AbstractRepositoryEntity implements CpsTypeProgramElement
{
	private static final long serialVersionUID = 3460818029506014118L;

	/**
	 * Reference to a type
	 */
	protected TypeReference typeReference;

	/**
	 * Create a CpsTypeProgramElement using a type as base
	 * 
	 * @param type The type
	 * @throws NullPointerException Thrown when the type is null
	 */
	public CpsTypeProgramElementImpl(Type type) throws NullPointerException
	{
		this(new SelfTypeReference(type));
	}

	/**
	 * Create a new type program element using a type reference. This creates a
	 * type self reference for the given type.
	 * 
	 * @param ref The type reference
	 * @throws NullPointerException Thrown when the reference is null
	 */
	public CpsTypeProgramElementImpl(TypeReference ref) throws NullPointerException
	{
		super();
		if (ref == null)
		{
			throw new NullPointerException("reference is null");
		}
		typeReference = ref;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.TypeSystem.CpsTypeProgramElement#
	 * getTypeReference()
	 */
	public TypeReference getTypeReference()
	{
		return typeReference;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.TypeSystem.CpsProgramElement#
	 * getProgramElement()
	 */
	public ProgramElement getProgramElement()
	{
		if (typeReference != null)
		{
			return typeReference.getReference();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsVariable#compatible(Composestar
	 * .Core.CpsRepository2.TypeSystem.CpsVariable)
	 */
	public boolean compatible(CpsVariable other) throws UnsupportedOperationException
	{
		if (!(other instanceof CpsProgramElement))
		{
			return false;
		}
		if (other instanceof CpsTypeProgramElement)
		{
			if (typeReference == null)
			{
				return ((CpsTypeProgramElement) other).getTypeReference() == null;
			}
			if (((CpsTypeProgramElement) other).getTypeReference() == null)
			{
				return false;
			}
			return typeReference.getReferenceId().equals(
					((CpsTypeProgramElement) other).getTypeReference().getReferenceId());
		}
		CpsProgramElement o = (CpsProgramElement) other;
		if (getProgramElement() == null)
		{
			return o.getProgramElement() == null;
		}
		return getProgramElement().equals(o.getProgramElement());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		if (typeReference != null)
		{
			if (typeReference.getReference() != null)
			{
				return typeReference.getReference().toString();
			}
			return typeReference.getReferenceId();
		}
		return super.toString();
	}

	/**
	 * A type self reference
	 * 
	 * @author Michiel Hendriks
	 */
	public static class SelfTypeReference implements TypeReference
	{
		private static final long serialVersionUID = -2814252282053191394L;

		/**
		 * The refered to type
		 */
		protected Type type;

		/**
		 * @param refForType
		 * @throws NullPointerException Thrown when the type is null
		 */
		public SelfTypeReference(Type refForType) throws NullPointerException
		{
			super();
			if (refForType == null)
			{
				throw new NullPointerException("type is null");
			}
			type = refForType;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#dereference()
		 */
		public void dereference() throws UnsupportedOperationException
		{
			throw new UnsupportedOperationException();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#getReference()
		 */
		public Type getReference()
		{
			return type;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#getReferenceId()
		 */
		public String getReferenceId()
		{
			return type.getFullName();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#isResolved()
		 */
		public boolean isResolved()
		{
			return true;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#isSelfReference
		 * ()
		 */
		public boolean isSelfReference()
		{
			return true;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.CpsRepository2.References.Reference#setReference
		 * (java.lang.Object)
		 */
		public void setReference(Type element) throws UnsupportedOperationException
		{
			throw new UnsupportedOperationException();
		}

	}
}
