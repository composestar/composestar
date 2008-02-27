/**
 * Implementation of functions used by the caching and invalidation filters.
 * !!! This is not a very good caching system. !!! It only supports functions
 * with 1 int argument and an int return value   
 *  
 * It should be part of the CachingFilters package, but file/object passing is
 * not yet implemented.  
 */ 

#include <stdlib.h>
#include "caching.h"

struct CacheEntry
{
	int methodid;
	int arg0;
	int value;
};

static struct CacheEntry *cache = NULL;
static unsigned int cacheSize = 0;

/**
 * Return not 0 when the value is cached
 */ 
int CSTAR_is_cached(int methodid, int arg0)
{
	unsigned int i;
	for (i = 0; i < cacheSize; i++)
	{
		if (cache[i].methodid == methodid && cache[i].arg0 == arg0)
		{
			return 1;
		}
	}
	return 0;
}

void CSTAR_get_cache(int methodid, int arg0, void* dest)
{
	unsigned int i;
	for (i = 0; i < cacheSize; i++)
	{
		if (cache[i].methodid == methodid && cache[i].arg0 == arg0)
		{
			*(int*)dest = cache[i].value;
			return;
		}
	}
}

void CSTAR_set_cache(int methodid, int arg0, int value)
{
	unsigned int i;
	for (i = 0; i < cacheSize; i++)
	{
		if (cache[i].methodid == methodid && cache[i].arg0 == arg0)
		{
			cache[i].value = value;
			return;
		}
	}
	for (i = 0; i < cacheSize; i++)
	{
		if (cache[i].methodid == -1)
		{
			cache[i].methodid = methodid;
			cache[i].arg0 = arg0;
			cache[i].value = value;
			return;
		}
	}
	if (cache == NULL)
	{
		cache = malloc(sizeof(struct CacheEntry));
	}
	else {
		cache = realloc(cache, (cacheSize+1) * sizeof(struct CacheEntry));
	}
	cache[cacheSize].methodid = methodid;
	cache[cacheSize].arg0 = arg0;
	cache[cacheSize].value = value;
	cacheSize++;
}

void CSTAR_clear_cache()
{
	free(cache);
	cache = NULL;
	cacheSize = 0;
}
