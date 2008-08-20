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

package Composestar.Core.CpsRepository2.FilterModules;

import java.util.Collection;
import java.util.List;

import Composestar.Core.CpsRepository2.QualifiedRepositoryEntity;
import Composestar.Core.CpsRepository2.FMParams.FMParameter;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiatable;
import Composestar.Core.CpsRepository2.References.FilterModuleReference;

/**
 * This interface defines the filter module. Starting from the filter module the
 * repository elements can be instantiated. Thus all child elements of a filter
 * module should implement the "Instantiatable" interface. A new instance of a
 * defined filter module is created when it has parameters and it is
 * superimposed. An instance is also created during in the runtime for every
 * concern that is instantiated.
 * 
 * @author Michiel Hendriks
 */
public interface FilterModule extends QualifiedRepositoryEntity, FilterModuleReference, Instantiatable<FilterModule>
{
	/**
	 * @return True if this filter module has filter module parameters.
	 */
	boolean hasParameters();

	/**
	 * Add a filter module parameter to the end of the parameter list. After a
	 * parameter is added, setOwner(this) is called on the parameter.
	 * 
	 * @param param The parameter to add.
	 * @return Returns true when the parameter was added, and false where there
	 *         already was a parameter defined with that name.
	 * @throws NullPointerException Thrown when the parameter is null.
	 */
	boolean addParameter(FMParameter param) throws NullPointerException;

	/**
	 * @return The list of filter module parameters. If this filter module has
	 *         no parameters an empty list is returned. This list is read-only.
	 */
	List<FMParameter> getParameters();

	/**
	 * Get a parameter by its name.
	 * 
	 * @param name The name of the requested parameter.
	 * @return The parameter with the requested name or null when no parameter
	 *         with that name exists.
	 */
	FMParameter getParameter(String name);

	/**
	 * Add a new variable to this filter module. The name of the variable must
	 * be unique within the filter module. After an variable has been added
	 * setOwner(this) is called on the internal. This method is used to add
	 * internals, externals and conditions to the filter module.
	 * 
	 * @param var The internal to add.
	 * @return True if the variable was added, or false when its name is not
	 *         unqiue.
	 * @throws NullPointerException Thrown when the variable is null.
	 */
	boolean addVariable(FilterModuleVariable var) throws NullPointerException;

	/**
	 * Retrieves an variable by a name.
	 * 
	 * @param name The name of the requested variable
	 * @return The variable with the given name, or null if no variable was
	 *         found.
	 * @see #getInternal(String)
	 * @see #getExternal(String)
	 * @see #getCondition(String)
	 */
	FilterModuleVariable getVariable(String name);

	/**
	 * @return A read-only list of all variables in this filter module.
	 *         Variables include the Internals, Externals, and Conditions. An
	 *         empty collection is returned when there are no variables in this
	 *         filter module.
	 */
	Collection<FilterModuleVariable> getVariables();

	/**
	 * Just like {@link #getVariable(String)}, except that it will only return
	 * an Internal.
	 * 
	 * @param name The name of the internal
	 * @return The internal instance or null when no internal with that name was
	 *         found.
	 * @see #getVariable(String)
	 */
	Internal getInternal(String name);

	/**
	 * Just like {@link #getVariable(String)}, except that it will only return
	 * an External.
	 * 
	 * @param name The name of the external
	 * @return The external instance or null when no internal with that name was
	 *         found.
	 * @see #getVariable(String)
	 */
	External getExternal(String name);

	/**
	 * Just like {@link #getVariable(String)}, except that it will only return
	 * an Condition.
	 * 
	 * @param name The name of the condition
	 * @return The condition instance or null when no internal with that name
	 *         was found.
	 * @see #getVariable(String)
	 */
	Condition getCondition(String name);

	/**
	 * Removes the given variable from the filter module. This does not unset
	 * the owner relation in the variable.
	 * 
	 * @param var The variable to remove.
	 * @return True if the variable was removed, or false if the variable did
	 *         not exist in this filter module.
	 * @throws NullPointerException Thrown when the variable is null.
	 */
	boolean removeVariable(FilterModuleVariable var) throws NullPointerException;

	/**
	 * Removes a variable from the filter module by its name.
	 * 
	 * @param name The name of the variable to remove.
	 * @return The removed variable, or null if no variable was removed.
	 */
	FilterModuleVariable removeVariable(String name);

	/**
	 * Sets the input filter expression. setOwner(this) is called after the
	 * expression is added.
	 * 
	 * @param expr The new input filter expression. If this value is null the
	 *            current expression will be unset.
	 */
	void setInputFilterExpression(FilterExpression expr);

	/**
	 * @return The current input filter expression. If this filter module has no
	 *         input filter expression null is returned.
	 */
	FilterExpression getInputFilterExpression();

	/**
	 * Sets the output filter expression. setOwner(this) is called after the
	 * expression is added.
	 * 
	 * @param expr The new output filter expression. If this value is null the
	 *            current expression will be unset.
	 */
	void setOutputFilterExpression(FilterExpression expr);

	/**
	 * @return The current output filter expression. If this filter module has
	 *         no output filter expression null is returned.
	 */
	FilterExpression getOutputFilterExpression();
}
