concern CacheMetrics
{
	filtermodule ResourceConflict(??m)
	{
		inputfilters
			alwaysinval : Invalidate = ( selector == ??m )
	}

	superimposition
	{
		selectors
			countingClasses = { C | isClassWithName(C, 'textproc.TextMetrics') };
			countingMethods = { M | isMethodWithNameInList( M, ['countWords', 'countSentences']), 
				isClassWithName(C, 'textproc.TextMetrics'), classHasMethod( C, M )};
			invalidateMethods = { M | isMethodWithNameInList( M, ['bookUpdated', 'chapterUpdated',
				'paragraphUpdated']), isClassWithName(C, 'textproc.TextMetrics'), 
				classHasMethod( C, M )};
		// Uncomment this to cause a resource conflict
		/*
		filtermodules
			countingClasses <- ResourceConflict(countingMethods);
		*/
		annotations
			countingClasses <- ApplyCaching;
			countingMethods <- CacheResult;
			invalidateMethods <- InvalidateCache;
		constraints
			// make sure the conflict is caused
			pre(Memoization.caching_advice,ResourceConflict);
	}
}
