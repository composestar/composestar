package Composestar.Core.LAMA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Composestar.Core.CpsProgramRepository.PlatformRepresentation;
import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

public abstract class ProgramElement extends PlatformRepresentation implements SerializableRepositoryEntity
{

	/*
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

	private List annotations = new ArrayList();

	public void addAnnotation(Annotation annotation)
	{
		this.annotations.add(annotation);
	}

	public void removeAnnotation(Annotation annotation)
	{
		this.annotations.remove(annotation);
	}

	public List getAnnotations()
	{
		return this.annotations;
	}
}
