package Composestar.Java.TYM.SignatureTransformer;

import java.io.File;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.TYM.SignatureTransformer.SignatureTransformer;
import Composestar.Java.COMP.CStarJavaCompiler;

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
		Boolean signaturesmodified = resources.get("signaturesmodified");
		if (signaturesmodified != null && signaturesmodified.booleanValue()) // process
																				// only
																				// if
																				// signatures
																				// are
																				// modified
		{
			File dummies = (File) resources.get(CStarJavaCompiler.DUMMY_JAR);
			JarTransformer transformer = new JarTransformer(dummies);
			transformer.run(resources.repository());
		}
	}
}
