
#ifndef _CSTAR_CACHING_H_
#define _CSTAR_CACHING_H_

int CSTAR_is_cached(int methodid, int arg0);
void CSTAR_get_cache(int methodid, int arg0, void* dest);
void CSTAR_set_cache(int methodid, int arg0, int value);
void CSTAR_clear_cache(int methodid, int arg0);

#endif
