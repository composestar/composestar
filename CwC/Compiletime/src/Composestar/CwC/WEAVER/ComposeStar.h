#ifndef _COMPOSESTAR_H_
#define _COMPOSESTAR_H_

/*
 * Compose* / CwC Header file
 * 
 * $Id$
 */

#ifndef _STDLIB_H_
#include <stdlib>
#endif

#ifndef _ASSERT_H_
#include <assert>
#endif

static int *cstar_inner_register;
static int cstar_inner_register_size = 0;

#ifndef CSTAR_INNER_REGISTER_ALLOC_BY
#define CSTAR_INNER_REGISTER_ALLOC_BY 32
#endif

/**
 * Check if the inner call is set for the given method id
 */
static int CSTAR_is_inner_call(int methodId) {
	int i;
	for (i = 0; i < cstar_inner_register_size; i++) {
		if (cstar_inner_register[i] == methodId)
			return 1;
	}
	return 0;
}

/**
 * Sets the inner call flag for the given method id
 */
static void CSTAR_set_inner_call(int methodId) {
	int i;
	for (i = 0; i < cstar_inner_register_size; i++) {
		if (cstar_inner_register[i] == 0) {
			cstar_inner_register[i] = methodId;
			return;
		}
	}
	if (cstar_inner_register_size == 0) {
		cstar_inner_register_size += CSTAR_INNER_REGISTER_ALLOC_BY;
		cstar_inner_register = malloc(cstar_inner_register_size * sizeof(int));
	} else {
		cstar_inner_register_size += CSTAR_INNER_REGISTER_ALLOC_BY;
		cstar_inner_register = realloc(cstar_inner_register,
				cstar_inner_register_size * sizeof(int));
	}
	assert(cstar_inner_register != NULL);
	cstar_inner_register[i] = methodId;
}

/**
 * Unsets the inner call flag for the given method id
 */
static void CSTAR_reset_inner_call(int methodId) {
	int i;
	for (i = 0; i < cstar_inner_register_size; i++) {
		if (cstar_inner_register[i] == methodId) {
			cstar_inner_register[i] = 0;
			return;
		}
	}
}

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
