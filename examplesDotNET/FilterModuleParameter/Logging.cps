concern logging in FilterModuleParameter{  //fmp #1
	filtermodule log(?logfunction){
		internals
			logger : ?logfunction;
		inputfilters
			m : Meta ={[*.*] logger.log}
	}
}