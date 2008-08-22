package Composestar.Core.CpsRepository2;

/**
 * Defines what join point context the method accepts as its first and only
 * argument. This will be used to look up a specific method signature and to
 * initialize a context.
 * 
 * @author Michiel Hendriks
 */
public enum JoinPointContextArgument
{
	/**
	 * The method does not need a join point context. A method with either zero
	 * or one argument is accepted. Preference goes to the zero argument method.
	 */
	UNUSED,
	/**
	 * The method uses the lightweight join point context. All information of
	 * the partial context is also included in the full context.
	 */
	PARTIAL,
	/**
	 * The method requires the full join point context.
	 */
	FULL,
	/**
	 * Absolutely none
	 */
	NONE
}
