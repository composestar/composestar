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

package Composestar.Java.FLIRT.Env;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.List;

import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.Meta.SourceInformation;
import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Java.FLIRT.ObjectManagerStorage;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;
import Composestar.Java.FLIRT.Interpreter.InterpreterMain;

/**
 * @author Michiel Hendriks
 */
public class ObjectManager extends MessageReceiver implements RTCpsObject
{
	private static final long serialVersionUID = 1L;

	/**
	 * The object this manager manages. It's a weak reference so the managers
	 * are not kept around longer than needed.
	 */
	protected WeakReference<Object> objectRef;

	/**
	 * The concern associated with this object. Used to delegate some calls to
	 */
	protected Concern concern;

	/**
	 * The filter modules imposed on this object in the desired ordering.
	 */
	protected List<RTFilterModule> filterModules;

	/**
	 * The inner object
	 */
	protected SimpleCpsObject innerObject;

	/**
	 * Create a new object manager
	 * 
	 * @param forObject
	 * @param crn
	 * @param fms
	 */
	public ObjectManager(Object forObject, Concern crn, List<RTFilterModule> fms)
	{
		super();
		objectRef = new WeakReference<Object>(forObject);
		concern = crn;
		filterModules = fms;
		innerObject = new SimpleCpsObject(forObject, crn.getTypeReference());
	}

	/**
	 * @return Return the instance of his object manager
	 */
	public Object getObject()
	{
		Object res = objectRef.get();
		if (res == null)
		{
			cleanup();
		}
		return res;
	}

	/**
	 * @return The inner object
	 */
	public RTCpsObject getInnerObject()
	{
		return innerObject;
	}

	/**
	 * @return The filter modules for this object
	 */
	public List<RTFilterModule> getFilterModules()
	{
		return filterModules;
	}

	/**
	 * Remove all references so garbage collection can take care of the rest.
	 */
	public void cleanup()
	{
		for (RTFilterModule fm : filterModules)
		{
			fm.cleanup();
		}
		filterModules.clear();
		innerObject = null;
		ObjectManagerStorage.removeObjectManager(this);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.TypeSystem.CpsObject#isInnerObject()
	 */
	public boolean isInnerObject()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.TypeSystem.CpsObject#isSelfObject()
	 */
	public boolean isSelfObject()
	{
		// this is irrelevant anyway
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.TypeSystem.CpsTypeProgramElement#
	 * getTypeReference()
	 */
	public TypeReference getTypeReference()
	{
		if (concern != null)
		{
			return concern.getTypeReference();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.TypeSystem.CpsProgramElement#
	 * getProgramElement()
	 */
	public ProgramElement getProgramElement()
	{
		if (concern != null)
		{
			return concern.getTypeReference().getReference();
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
		if (other instanceof CpsObject)
		{
			// objects must match instance
			return other == this;
		}
		// TODO
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.RepositoryEntity#getOwner()
	 */
	public RepositoryEntity getOwner()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.RepositoryEntity#getSourceInformation()
	 */
	public SourceInformation getSourceInformation()
	{
		if (concern != null)
		{
			return concern.getSourceInformation();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.RepositoryEntity#setOwner(Composestar
	 * .Core.CpsRepository2.RepositoryEntity)
	 */
	public RepositoryEntity setOwner(RepositoryEntity newOwner)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.RepositoryEntity#setSourceInformation
	 * (Composestar.Core.CpsRepository2.Meta.SourceInformation)
	 */
	public void setSourceInformation(SourceInformation srcInfo)
	{
	// nop
	}

	private void writeObject(ObjectOutputStream out) throws IOException
	{}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Java.FLIRT.Env.MessageReceiver#receiveMessage(Composestar
	 * .Java.FLIRT.Env.RTMessage)
	 */
	@Override
	protected Object receiveMessage(RTMessage msg) throws Throwable
	{
		FilterExecutionContext context = new FilterExecutionContext(this, msg);
		InterpreterMain.interpret(context);
		Object result = msg.getReturnValue();
		synchronized (context)
		{
			// notify a possible waiting interpreter
			context.freeContext();
		}
		return result;
	}
}
