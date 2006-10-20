concern Logging {
 
  filtermodule logModule {
    internals
		logClass: MyApp.logging.Logger;
    inputfilters
      meta: Meta = { [*.*] logClass.log }
  }
  
  superimposition {
    selectors
		logged = { Class | isClassWithName(Conn, 'MyApp.net.Connection'), inherits(Conn, Class) }; 

    filtermodules
      logged <- logModule;
  }
}