#ifndef __COMPOSESTAR_H
#define __COMPOSESTAR_H

int CSTAR_is_inner_call(int methodId)
{
  return 0;
}

void CSTAR_set_inner_call(int methodId)
{
}

void CSTAR_reset_inner_call(int methodId)
{
}

typedef struct __join_point_context {
  // sender? 

  char *startSelector;

  // current target
  char *currentSelector;
  
  // subst target
  char *substSelector;

  void **argv;
  unsigned int argc;
  
  void *returnValue;
} JoinPointContext;

#endif
