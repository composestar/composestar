package Composestar.RuntimeCore.FLIRT;

import java.util.ArrayList;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.RuntimeCore.FLIRT.Interpreter.FilterModuleRuntime;
import Composestar.RuntimeCore.FLIRT.Interpreter.FilterRuntime;

public class FLIRT {
    //private DataStore datastore;
    
    /**
     * @roseuid 41173DE30394
     */
    public FLIRT() {
     
    }
    
    /**
     * @param obj
     * @roseuid 411734D50278
     * @deprecated
     */
    public void run(Object obj) {
		Filter filter = new Filter();
		FilterType type = new FilterType();
		type.setName("Error");
		filter.setFilterType(type);
		filter.setName("Pacman.Hallo");
		FilterRuntime fr = new FilterRuntime();
		fr.setReference(filter);
		ArrayList list = new ArrayList();
		list.add(fr);
		FilterModuleRuntime fmr = new FilterModuleRuntime(list,null);
		ObjectManager om = new ObjectManager(obj, null);
		om.addFilterModule(fmr);
		GlobalObjectManager.setObjectManagerFor(obj,om);
    }
    
    /**
     * @param args
     * @roseuid 40F27B3C02B6
     */
   /*
    public static void main(String[] args) {

		Debug.setMode(Debug.MODE_INFORMATION);
		Debug.out(Debug.MODE_INFORMATION,"FLIRT","Starting...");
		CommonResources cr = new CommonResources();
		FLIRT flirt = new FLIRT();
		MessageHandlingFacility.handleApplicationStart("repository.xml");
		Object[] arguments = {new String("Hello World")};
		flirt.run(cr);
		MessageHandlingFacility.handleVoidMethodCall(flirt,cr,"addResource",arguments);

    }
        */
}
