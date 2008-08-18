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

package Composestar.Core.CpsRepository2.SuperImposition;

import java.util.Collection;

import Composestar.Core.CpsRepository2.CpsConcern;
import Composestar.Core.CpsRepository2.QualifiedRepositoryEntity;

/**
 * This interface encapsulates all elements of the superimposition block in the
 * CPS language. A CpsConcern can have a superimposition instance assigned.
 * 
 * @author Michiel Hendriks
 * @see CpsConcern#getSuperImposition()
 * @see CpsConcern#setSuperImposition(SuperImposition)
 */
public interface SuperImposition extends QualifiedRepositoryEntity
{
	/**
	 * The name of all SuperImposition instances. This entity does not really
	 * have a name, but an identifier is required so that named elements within
	 * the superimposition section can have a fully qualified name which does
	 * not obstruct the names of other concern elements.
	 */
	public static final String NAME = "@SI";

	/**
	 * Add a new selector to this superimposition instance. Selectors should
	 * have unique names in the superimposition block. Therefore a selector will
	 * only be added when its name is unique. Both the names of Selectors and
	 * Conditions are considered in the unqiueness test. When the selector is
	 * added setOwner(this) is called on the newly added selector.
	 * 
	 * @param newSel The selector to add.
	 * @return True when the selector was added or false when it was not added.
	 * @throws NullPointerException Thrown when the provided selector is null
	 * @see #addCondition(Condition)
	 */
	boolean addSelector(Selector newSel) throws NullPointerException;

	/**
	 * Remove the provided selector
	 * 
	 * @param sel The selector to remove
	 * @return True if the selector was removed, or false when this selector was
	 *         not present.
	 * @throws NullPointerException Thrown when the selector was null.
	 * @see #removeSelector(String)
	 */
	boolean removeSelector(Selector sel) throws NullPointerException;

	/**
	 * Remove a selector by its name.
	 * 
	 * @param name The name of the selector to remove
	 * @return The removed selector or null when no selector with the given name
	 *         was found.
	 * @see #removeSelector(Selector)
	 */
	Selector removeSelector(String name);

	/**
	 * Get a selector by its name (not the fully qualified name).
	 * 
	 * @param name The name of the selector
	 * @return the selector instance with the given name or null when no such
	 *         selector exists
	 * @see #getSelectors()
	 */
	Selector getSelector(String name);

	/**
	 * @return All registered selectors. If no selectors were defined an empty
	 *         set will be returned. The returned collection is real only.
	 * @see #getSelector(String)
	 */
	Collection<Selector> getSelectors();

	/**
	 * Add a filter module binding. setOwner(this) is called on the filter
	 * module binding after is had been added.
	 * 
	 * @param fmb The filter module binding to add
	 * @throws NullPointerException Thrown when the provided filter module
	 *             binding is null
	 */
	void addFilterModuleBinding(FilterModuleBinding fmb) throws NullPointerException;

	/**
	 * Remove a previously added filter module binding
	 * 
	 * @param fmb The filter module binding to remove
	 * @return True when the filter module binding was removed
	 * @throws NullPointerException Thrown when the provided filter module
	 *             binding is null
	 */
	boolean removeFilterModuleBinding(FilterModuleBinding fmb) throws NullPointerException;

	/**
	 * @return The set of filter module bindings. An empty set is returned when
	 *         there are no filter module bindings. The returned collection is
	 *         real only.
	 */
	Collection<FilterModuleBinding> getFilterModuleBindings();

	/**
	 * Add a annotation binding to this instances. After the binding had been
	 * added setOwner(this) is called.
	 * 
	 * @param ab The annotation binding to add
	 * @throws NullPointerException Thrown when the annotation binding is null
	 */
	void addAnnotationBinding(AnnotationBinding ab) throws NullPointerException;

	/**
	 * Removes an annotation binding.
	 * 
	 * @param ab The annotation binding to remove
	 * @return True when the binding was removed or false when it was not
	 *         present
	 * @throws NullPointerException Thrown when the binding instance is null
	 */
	boolean removeAnnotationBinding(AnnotationBinding ab) throws NullPointerException;

	/**
	 * @return The set of annotation bindings, or an empty set when there are no
	 *         annotation bindings. The returned collection is real only.
	 */
	Collection<AnnotationBinding> getAnnotationBindings();

	/**
	 * Add a new filter module constraint to this annotation. Filter module
	 * constraints have a global effect. They are kept in the superimposition
	 * instance for administration purposes. And to resolve possible
	 * associations with superimposition conditions. After the constraint has
	 * been added setOwner(this) is called on the constaint instance.
	 * 
	 * @param fmc The constraint to add
	 * @throws NullPointerException Thrown when the constraint is null
	 */
	void addFilterModuleConstraint(FilterModuleConstraint fmc) throws NullPointerException;

	/**
	 * Remove a filter module constraint.
	 * 
	 * @param fmc The constaint to remove
	 * @return True when the constraint was removed, or false when it was not
	 *         found and thus not removed.
	 * @throws NullPointerException Thrown when the constraint is null
	 */
	boolean removeFilterModuleConstraint(FilterModuleConstraint fmc) throws NullPointerException;

	/**
	 * @return All registered filter module constraints in the superimposition
	 *         instance. Returns an empty set when no constraints where
	 *         registered. The returned collection is real only.
	 */
	Collection<FilterModuleConstraint> getFilterModuleConstraints();

	/**
	 * Register a condition to this superimposition instance. Conditions should
	 * have unique names within the superimposition instance. Both the names of
	 * Selectors and Conditions are considered in the unqiueness test. After the
	 * condition is added setOwner(this) is called on the condition.
	 * 
	 * @param cond The condition to add.
	 * @return True when the condition was added or false when there was a
	 *         naming collision.
	 * @throws NullPointerException Thrown when the provided condition is null
	 * @see #addSelector(Selector)
	 */
	boolean addCondition(Condition cond) throws NullPointerException;

	/**
	 * Remove a given condition from this superimposition. Removing a condition
	 * does not remove associations with the condition from other entities.
	 * 
	 * @param cond The condition to remove
	 * @return True when the condition was removed or false when the condition
	 *         was not found.
	 * @throws NullPointerException Thrown when the provided condition is null
	 */
	boolean removeCondition(Condition cond) throws NullPointerException;

	/**
	 * Remove a condition by it's name.
	 * 
	 * @param name The name of the condition to remove
	 * @return The condition that was removed or null when no condition was
	 *         removed.
	 */
	Condition removeCondition(String name);

	/**
	 * @param name The name of the condition to return.
	 * @return The condition with the given name, returns null when no such
	 *         condition exists.
	 */
	Condition getCondition(String name);

	/**
	 * @return The set of registered conditions, or an empty set when no
	 *         conditions where registered. The returned collection is real
	 *         only.
	 */
	Collection<Condition> getConditions();
}
