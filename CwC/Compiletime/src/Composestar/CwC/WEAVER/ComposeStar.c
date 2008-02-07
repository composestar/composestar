/*
 * Compose* / CwC Implementation of support methods
 * 
 * $Id$
 */

#include <stdlib.h>
#include <assert.h>
#include "Composestar.h"

static int *cstar_inner_register;
static int cstar_inner_register_size = 0;

#ifndef CSTAR_INNER_REGISTER_ALLOC_BY
#define CSTAR_INNER_REGISTER_ALLOC_BY 32
#endif

/**
 * Check if the inner call is set for the given method id
 */
int CSTAR_is_inner_call(int methodId) {
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
void CSTAR_set_inner_call(int methodId) {
	int i;
	for (i = 0; i < cstar_inner_register_size; i++) {
		if (cstar_inner_register[i] == -1) {
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
	// unset the other fields
	i++;
	while (i < cstar_inner_register_size) {
		cstar_inner_register[i] = -1;
		i++;
	}
}

/**
 * Unsets the inner call flag for the given method id
 */
void CSTAR_reset_inner_call(int methodId) {
	int i;
	for (i = 0; i < cstar_inner_register_size; i++) {
		if (cstar_inner_register[i] == methodId) {
			cstar_inner_register[i] = -1;
			return;
		}
	}
}
