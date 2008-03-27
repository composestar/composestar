concern CacheMetrics
{
	/*superimposition
	{
		selectors
			countingClasses = { C | isClassWithName(C, 'textproc.TextMetrics') };
			countingMethods = { M | isMethodWithNameInList( M, ['countWords', 'countSentences']), 
				isClassWithName(C, 'textproc.TextMetrics'), classHasMethod( C, M )};
			invalidateMethods = { M | isMethodWithNameInList( M, ['bookUpdated', 'chapterUpdated',
				'paragraphUpdated']), isClassWithName(C, 'textproc.TextMetrics'), 
				classHasMethod( C, M )};
		annotations
			countingClasses <- ApplyCaching;
			countingMethods <- CacheResult;
			invalidateMethods <- InvalidateCache;
	}*/
}
