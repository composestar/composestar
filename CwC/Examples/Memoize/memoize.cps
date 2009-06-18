concern Memoization
{
  filtermodule caching_advice(??cached,??invalidators)
  {
    inputfilters
      caching      : Cache      = ( selector == ??cached );
      invalidation : Invalidate = ( selector == ??invalidators )
  }
  
  superimposition
  {
    selectors
      memoize = { C | classHasAnnotationWithName(C, 'ApplyCaching') };
      cacheresults = { M | classHasAnnotationWithName(C, 'ApplyCaching'), 
        classHasMethod(C, M), methodHasAnnotationWithName(M, 'CacheResult') };
      invalidators = { M | classHasAnnotationWithName(C, 'ApplyCaching'), 
        classHasMethod(C, M), methodHasAnnotationWithName(M, 'InvalidateCache') };
    filtermodules
      memoize <- caching_advice(cacheresults, invalidators);
  }
}
