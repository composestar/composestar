package Composestar.Java.CONE;

import Composestar.Core.CONE.CONE;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CommonResources;

public class JavaCONE extends CONE{

	public void run(CommonResources resources) throws ModuleException {
		//Generate repository file
		JavaRepositorySerializer rs = new JavaRepositorySerializer(); 
		rs.run(resources);
	}
}
