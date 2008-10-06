package Composestar.Core.CpsRepository2.References;

import java.util.Collection;

import Composestar.Core.CpsRepository2.JoinPointContextArgument;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;

/**
 * The reference manager is responsible for creating and managing the various
 * reference types.
 * 
 * @author Michiel Hendriks
 */
public interface ReferenceManager
{
	/**
	 * Key for the reference manager in the common resources
	 */
	public static final String RESOURCE_KEY = "CpsRepository2.ReferenceManager";

	/**
	 * Get a type reference with the given reference id. It will create a new
	 * type reference when no previous reference existed.
	 * 
	 * @param refid The reference id.
	 * @return The type reference
	 * @throws IllegalArgumentException Thrown when the reference id is empty
	 * @throws NullPointerException Thrown when the reference id is null
	 */
	TypeReference getTypeReference(String refid) throws IllegalArgumentException, NullPointerException;

	/**
	 * @return Read-only list of all known type references.
	 */
	Collection<TypeReference> getTypeReferences();

	/**
	 * Get a MethodInfo reference using a reference id of a type.
	 * 
	 * @param refid The reference id of the method reference
	 * @param typeRef The reference id of the type.
	 * @param jpca The desired join point context argument of the method
	 * @return The method reference
	 * @throws NullPointerException Thrown when the type reference is null or
	 *             when the reference id is null
	 * @throws IllegalArgumentException Thrown when the reference id or type
	 *             reference is empty
	 */
	MethodReference getMethodReference(String refid, String typeRef, JoinPointContextArgument jpca)
			throws NullPointerException, IllegalArgumentException;

	/**
	 * Get a method reference using a type reference
	 * 
	 * @param refid The reference id of the method reference
	 * @param typeRef A type reference
	 * @param The desired join point context argument of the method
	 * @return The method reference
	 * @throws NullPointerException Thrown when the type reference is null or
	 *             when the reference id is null
	 * @throws IllegalArgumentException Thrown when the reference id is empty
	 */
	MethodReference getMethodReference(String refid, TypeReference typeRef, JoinPointContextArgument jpca)
			throws NullPointerException, IllegalArgumentException;

	/**
	 * @return Read-only list of all method references
	 */
	Collection<MethodReference> getMethodReferences();

	/**
	 * Get an instance method reference
	 * 
	 * @param refid The reference id of the method reference
	 * @param context A context
	 * @param The desired join point context argument of the method
	 * @return The method reference
	 * @throws NullPointerException Thrown when the context reference is null or
	 *             when the reference id is null
	 * @throws IllegalArgumentException Thrown when the reference id is null
	 */
	InstanceMethodReference getInstanceMethodReference(String refid, CpsObject context, JoinPointContextArgument jpca)
			throws NullPointerException, IllegalArgumentException;

	/**
	 * @return Read-only list of all method references
	 */
	Collection<InstanceMethodReference> getInstanceMethodReferences();

	/**
	 * Get a type reference with the given reference id. It will create a new
	 * type reference when no previous reference existed.
	 * 
	 * @param refid The reference id.
	 * @return The type reference
	 * @throws IllegalArgumentException Thrown when the reference id is empty
	 * @throws NullPointerException Thrown when the reference id is null
	 */
	FilterModuleReference getFilterModuleReference(String refid) throws NullPointerException, IllegalArgumentException;

	/**
	 * @return Read-only list of all known type references.
	 */
	Collection<FilterModuleReference> getFilterModuleReferences();

	/**
	 * Register a user for a given repository entity
	 * 
	 * @param ref
	 * @param entity
	 * @throws NullPointerException Thrown when the reference or entity are null
	 */
	void addReferenceUser(Reference<?> ref, RepositoryEntity entity, boolean isRequired) throws NullPointerException;

	/**
	 * Get all usage information for a given reference
	 * 
	 * @param ref
	 * @return A collection of reference usage classes. Returns an empty
	 *         collection when the reference is not known to be used.
	 */
	Collection<ReferenceUsage> getReferenceUsage(Reference<?> ref);
}
