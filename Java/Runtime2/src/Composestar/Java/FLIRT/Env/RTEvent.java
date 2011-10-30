/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2011 University of Twente.
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

import java.util.HashMap;
import java.util.Map;

import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.TypeSystem.CpsLiteral;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2.TypeSystem.CpsValue;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsLiteralImpl;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsValueImpl;

import physdl.runtime.PhysicalModelInstance;

/**
 * @author arjan
 */
public class RTEvent
{
	private PhysicalModelInstance pmi;

	/**
	 * Stores all properties
	 */
	protected Map<String, CpsVariable> properties = new HashMap<String, CpsVariable>();

	// private CpsLiteral variable;
	//
	// private CpsLiteral eventType;
	//
	// private CpsValue value;
	//
	// private CpsValue result;
	//
	// private CpsLiteral resultIdentifier;

	private Map<String, CpsValue> values;

	// private CpsValue margin;
	//
	// private CpsLiteral enforceResult;

	private final static String VARIABLE_NAME = "variableName";

	private final static String EVENT_TYPE = "eventType";

	private final static String VALUE = "value";

	private final static String RESULT = "resultValue";

	private final static String RESULT_ID = "resultId";

	private final static String VALUES = "values";

	private final static String MARGIN = "margin";

	private final static String ENFORCE_RESULT = "enforceResult";

	public final static String REQUEST_EVENT_TYPE = "Request";

	public final static String UPDATE_EVENT_TYPE = "Update";

	public final static String CHECK_UPDATE_EVENT_TYPE = "CheckUpdate";

	public final static String CHANGE_EVENT_TYPE = "Change";

	public final static String INCONSISTENCY_EVENT_TYPE = "Inconsistency";

	public RTEvent()
	{
		super();

	}

	public CpsVariable getProperty(String name)
	{
		return properties.get(name);
	}

	public CpsVariable getProperty(String name, String key)
	{
		if (key == null && !VALUES.equals(name))
		{
			return properties.get(name);
		}
		else
		{
			return values.get(key);
		}
	}

	public void setProperty(String name, CpsVariable value) throws NullPointerException, IllegalArgumentException
	{
		properties.put(name, value);
	}

	public PhysicalModelInstance getPMI()
	{
		return pmi;
	}

	public void setPMI(PhysicalModelInstance pmi)
	{
		this.pmi = pmi;
	}

	public CpsLiteral getVariable()
	{
		return (CpsLiteral) properties.get(VARIABLE_NAME);
	}

	public void setVariable(CpsLiteral variable)
	{
		properties.put(VARIABLE_NAME, variable);
	}
	
	public void setVariable(String variable)
	{
		setVariable(new CpsLiteralImpl(variable));
	}

	public CpsLiteral getEventType()
	{
		return (CpsLiteral) properties.get(EVENT_TYPE);
	}

	public void setEventType(CpsLiteral eventType)
	{
		properties.put(EVENT_TYPE, eventType);
	}
	
	public void setEventType(String eventType)
	{
		CpsLiteral literal = new CpsLiteralImpl(eventType);
		setEventType(literal);
	}

	public CpsValue getValue()
	{
		return (CpsValue) properties.get(VALUE);
	}

	public void setValue(CpsValue value)
	{
		properties.put(VALUE, value);
	}
	
	public void setValue(double value)
	{
		setValue(new CpsValueImpl(value));
	}

	public CpsValue getResult()
	{
		return (CpsValue) properties.get(RESULT);
	}

	public void setResult(CpsValue result)
	{
		properties.put(RESULT, result);
	}

	public CpsLiteral getResultIdentifier()
	{
		return (CpsLiteral) properties.get(RESULT_ID);
	}

	public void setResultIdentifier(CpsLiteral resultIdentifier)
	{
		properties.put(RESULT_ID, resultIdentifier);
	}
	
	public void setResultIdentifier(String resultIdentifier){
		setResultIdentifier(new CpsLiteralImpl(resultIdentifier));
	}

	public Map<String, CpsValue> getValues()
	{
		return values;
	}

	public void setValues(Map<String, CpsValue> values)
	{
		this.values = values;
	}

	public CpsValue getMargin()
	{
		return (CpsValue) properties.get(MARGIN);
	}

	public void setMargin(CpsValue margin)
	{
		properties.put(MARGIN, margin);
	}

	public CpsLiteral getEnforceResult()
	{
		return (CpsLiteral) properties.get(ENFORCE_RESULT);
	}

	public void setEnforceResult(CpsLiteral enforceResult)
	{
		properties.put(ENFORCE_RESULT, enforceResult);
	}

}
