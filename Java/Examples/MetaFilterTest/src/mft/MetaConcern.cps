concern MetaConcern
{
	filtermodule MetaFM
	{
		internals
			mt : mft.MetaTarget;
			dlg: mft.SlowExec;
		inputfilters
			doMeta : Meta(target=mt) = ( selector == {'doNothing','doProceed',
				'doReply','doReply2','doResume','doRespond','doRespond2'} );
			slowDown : Dispatch = ( selector == 'doRespond' )
				{ target = dlg; selector = 'beSlow'; }
	}
	
	superimposition
	{
		selectors
			sub = { C | isClassWithName(C, 'mft.Subject') };
		filtermodules
			sub <- MetaFM;
	}
}