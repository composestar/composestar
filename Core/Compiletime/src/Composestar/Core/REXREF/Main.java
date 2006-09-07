package Composestar.Core.REXREF;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;


/**
 * Resolves references to objects in the repository
 */
public class Main implements CTCommonModule {
    public static boolean debug = true; //display debugging information?

    /**
     * Constructor
     */
    public Main() {
    }

    /**
     * Function called by Master
     * @param resources Common resources supplied by Master
     */
    public void run(CommonResources resources) throws ModuleException {
      DoResolve dr = new DoResolve();
      dr.go(DataStore.instance());
    }
}
