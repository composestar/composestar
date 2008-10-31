/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2006-2008 University of Twente.
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
package Composestar.Core.LAMA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class ProgramElement implements Serializable
{
	private static final long serialVersionUID = 7176893071089850442L;

	/**
	 * @return the name of this program element
	 */
	public abstract String getUnitName();

	/**
	 * @return the type of this program element (e.g. class, method etc.)
	 */
	public abstract String getUnitType();

	/**
	 * @param attribute A standard attribute, such as
	 *            private/public/protected/synchronized/etc.
	 * @return whether this program element has the specified attribute.
	 */
	public abstract boolean hasUnitAttribute(String attribute);

	/**
	 * @return collection of all (standard) attributes attached to this program
	 *         element.
	 */
	public abstract Collection getUnitAttributes();

	/*
	 * @param argumentName the name of the relation to other program element(s)
	 * (not the predicate name, but the name of the argument, e.g. ChildTypes)
	 * @returns UnitResult, containing one or more references to other
	 * ProgramElements
	 */
	public abstract UnitResult getUnitRelation(String argumentName);

	/** Stuff for annotations * */

	protected List<Annotation> annotations = new ArrayList<Annotation>();

	public void addAnnotation(Annotation annotation)
	{
		annotations.add(annotation);
	}

	public void removeAnnotation(Annotation annotation)
	{
		annotations.remove(annotation);
	}

	public List<Annotation> getAnnotations()
	{
		return Collections.unmodifiableList(annotations);
	}
}
