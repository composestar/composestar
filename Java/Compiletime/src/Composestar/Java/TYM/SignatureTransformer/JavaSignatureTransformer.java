package Composestar.Java.TYM.SignatureTransformer;

import java.util.ArrayList;
import java.util.Iterator;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.TYM.SignatureTransformer.SignatureTransformer;

public class JavaSignatureTransformer implements SignatureTransformer {

	public JavaSignatureTransformer(){
		
	}
	
	public void run(CommonResources resources) throws ModuleException {
		
		boolean signaturesmodified = ((Boolean)(resources.getResource("signaturesmodified"))).booleanValue();
		if( signaturesmodified ) { //process only if signatures are modified
			
			//Iterate over dummy jarfiles
			ArrayList dummies = (ArrayList)Configuration.instance().getProjects().getCompiledDummies();
			Iterator dumIt = dummies.iterator();
			while(dumIt.hasNext()) 
			{
				String name = (String)dumIt.next();
				JarTransformer transformer = new JarTransformer(name);
				transformer.run();
			}
		}//end signaturesmodified
	}
}
