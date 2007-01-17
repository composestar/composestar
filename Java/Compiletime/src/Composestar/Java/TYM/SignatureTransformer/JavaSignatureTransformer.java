package Composestar.Java.TYM.SignatureTransformer;

import java.util.ArrayList;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.TYM.SignatureTransformer.SignatureTransformer;

/**
 * Starting point for the signature transformer module. This module transforms
 * the signatures of the compiled dummies.
 */
public class JavaSignatureTransformer implements SignatureTransformer
{

	/**
	 * Constructor.
	 */
	public JavaSignatureTransformer()
	{

	}

	/**
	 * Module run method.
	 * <p>
	 * The dummies are replaced in a jarfile after they have been compiled.
	 * Therefore a <code>JarTransformer</code> is called.
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		boolean signaturesmodified = (Boolean) (resources.getResource("signaturesmodified"));
		if (signaturesmodified) // process only if signatures are modified
		{
			// Iterate over dummy jarfiles
			ArrayList dummies = (ArrayList) Configuration.instance().getProjects().getCompiledDummies();
			for (Object dummy : dummies)
			{
				String name = (String) dummy;
				JarTransformer transformer = new JarTransformer(name);
				transformer.run();
			}
		}
	}
}
