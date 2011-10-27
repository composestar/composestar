/*
 * Created on Feb 28, 2006
 *
 * Java implementation of the prolog language model.
 * 
 * @author roy_
 */
package Composestar.Java.LOLA.metamodel;

import Composestar.Core.LOLA.metamodel.InvalidModelException;
import Composestar.Core.LOLA.metamodel.LanguageModel;

/**
 * Java implementation of the prolog language model.
 */
public class JavaLanguageModel extends LanguageModel
{
	public JavaLanguageModel()
	{
		super();
	}

	/**
	 * Creates specifications for all language units that can occur in java, and
	 * the relations between them.
	 */
	@Override
	public void createMetaModel() throws InvalidModelException
	{
		mcNamespace = Composestar.Core.LAMA.LangNamespace.class;
		mcClass = Composestar.Java.LAMA.JavaType.class;
		mcInterface = Composestar.Java.LAMA.JavaType.class;
		mcType = Composestar.Java.LAMA.JavaType.class;
		mcMethod = Composestar.Java.LAMA.JavaMethodInfo.class;
		mcField = Composestar.Java.LAMA.JavaFieldInfo.class;
		mcParameter = Composestar.Java.LAMA.JavaParameterInfo.class;
		mcAnnotation = Composestar.Java.LAMA.JavaType.class;
		mcModel = Composestar.Java.LAMA.PhysicalModel.class;
		super.createMetaModel();
	}
}
