/*
 * Compose* / CwC Header file
 * 
 * $Id$
 */

#ifndef _COMPOSESTAR_H_
#define _COMPOSESTAR_H_

/**
 * Check if the inner call is set for the given method id
 */
extern int CSTAR_is_inner_call(int methodId);

/**
 * Sets the inner call flag for the given method id
 */
extern void CSTAR_set_inner_call(int methodId);

/**
 * Unsets the inner call flag for the given method id
 */
extern void CSTAR_reset_inner_call(int methodId);

/**
 * The JoinPointContext definition
 */
typedef struct __join_point_context {
	// sender? 

	/**
	 * The name of the starting selector (name of the function that was called).
	 */
	char *startSelector;

	// current target
	/**
	 * The current selector value, which could have been substituted
	 */
	char *currentSelector;

	// subst target
	/**
	 * The name of the selectors from the substitution
	 */
	char *substSelector;

	/**
	 * List with pointers to the argument values
	 */
	void **argv;
	
	/**
	 * Number of arguments passed to the method
	 */
	unsigned int argc;

	/**
	 * Pointer to the return value
	 */
	void *returnValue;
} JoinPointContext;

#endif
