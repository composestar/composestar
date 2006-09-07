concern JukeboxFrameConcern
{
	filtermodule GUITriggers
	{
		externals
			jbframe : JukeboxFrame.JBFrame = JukeboxFrame.JBFrame.instance();
		conditions
			isStateChanged : jbframe.isStateChanged();
	    inputfilters
			meta : Meta = { isStateChanged => [*.*]jbframe.update }
	}

	superimposition
	{
		selectors
			player = { C | methodHasAnnotationWithName(M, 'Jukebox.StateChange'), classHasMethod(C, M) };
		filtermodules
			player <- GUITriggers;
	}
}