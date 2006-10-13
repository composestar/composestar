package Composestar.Java.TYM.TypeCollector;

import java.lang.reflect.Method;
import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Java.LAMA.*;
import Composestar.Java.TYM.TypeHarvester.ClassMap;
import Composestar.Utils.Debug;

public class AnnotationCollector implements CTCommonModule 
{

	public void run(CommonResources resources) throws ModuleException 
	{
		try {
			ClassMap classmap = ClassMap.instance();
			HashMap classes = classmap.map();
			Iterator classIt = classes.values().iterator();
			while( classIt.hasNext() ) 
			{
				Class c = (Class)classIt.next();
				try {
				fetchMethodAnnotations(c);
				}
				catch(Throwable t){Debug.out(Debug.MODE_DEBUG,"COLLECTOR","Error while fetching annotations from type: "+c.getName()+" --> "+t.getMessage());}
			}
		}
		catch(Exception e) {
			throw new ModuleException(e.getMessage(),"AnnotationCollector");
		}
	}
	
	public void fetchMethodAnnotations(Class c) throws Throwable
	{
					
		Method[] methods  = c.getMethods();
		for(int i = 0; i < methods.length; i++ ) 
		{
			Annotation[] annotations = methods[i].getAnnotations();
			for(int j = 0; j < annotations.length; j++ ) 
			{
				JavaAnnotation annot = new JavaAnnotation();
				Type annotType = getTypeLocation(annotations[j].annotationType().getName());
				if(annotType!=null) 
				{
					annot.register(annotType,getMethodLocation(getTypeLocation(c.getName()),methods[i].getName()));
				}
					
				//retrieving value
				String value = "";
				String annotStr = annotations[j].toString(); 
				//"@Composestar.Semantics(value=args.read)"
				value = annotStr.substring(annotStr.indexOf("=")+1 , annotStr.indexOf(")"));
				annot.setValue(value);
			}
		}
	}
	
	public MethodInfo getMethodLocation(Type type, String methodName) throws Throwable
	{
		if( type != null )
		{
			List methods = type.getMethods();
			Iterator i = methods.iterator();
			while(i.hasNext())
			{
				MethodInfo method = (MethodInfo) i.next();
				if( method.Name.equals(methodName))
					return method;
			}
		}
		return null;
	}
	
	public Type getTypeLocation(String typeName)
	{
		Concern c = (Concern) DataStore.instance().getObjectByID(typeName);
		if( c != null && c.getPlatformRepresentation() instanceof Type )
		{
			return (Type) c.getPlatformRepresentation();
		}
		return null;
	}
}