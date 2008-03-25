concern CacheMetrics
{
	superimposition
	{
		selectors
			countingClasses = { C | isClassWithName(C, 'textproc.TextMetrics') };
			countingMethods = { M | isMethodWithNameInList( M, ['countWords']), 
				isClassWithName(C, 'textproc.TextMetrics'), classHasMethod( C, M )};
		annotations
			countingClasses <- ApplyCaching;
			countingMethods <- CacheResult;
	}
}
