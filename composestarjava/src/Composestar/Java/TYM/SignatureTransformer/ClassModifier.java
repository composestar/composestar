package Composestar.Java.TYM.SignatureTransformer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javassist.*;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Java.LAMA.JavaMethodInfo;
import Composestar.Utils.Debug;

public class ClassModifier {

	private static ClassModifier Instance = null;
	private ClassPool classpool;
	private ArrayList pathList;
	
	public ClassModifier(){
		classpool = ClassPool.getDefault();
		pathList = new ArrayList();
	}
	
	public static ClassModifier instance()
	{
		if (Instance == null) 
		{
			Instance = new ClassModifier();
		}
		return (Instance);
	} 
	
	public void addMethods(List methods, CtClass ct) throws Exception{
		
		Iterator it = methods.iterator();
		while(it.hasNext()) {
			JavaMethodInfo m = (JavaMethodInfo)it.next();
			int modifiers = m.theMethod.getModifiers();
			CtClass returnClass = null;
			if(m.ReturnTypeString!=""){
				returnClass = findClass(m.ReturnTypeString);
			}
			String methodName = m.theMethod.getName();
			CtClass[] parameters = new CtClass[m.Parameters.size()];
			if( m.Parameters.size() > 0 ) {
				Class[] params = m.theMethod.getParameterTypes();
				for (int i = 0; i < params.length; i++) {
					String name = params[i].getName();
					CtClass clazz = findClass(name);	
					parameters[i] = clazz;
				}
			}
			CtClass[] exceptions = new CtClass[0];
			CtMethod newMethod = CtNewMethod.make(modifiers,returnClass,methodName,parameters,exceptions,null,ct);
			Debug.out(Debug.MODE_INFORMATION,"SITRA","method "+newMethod.getName()+ " added to dummy class "+ct.getName());
			ct.addMethod(newMethod);
		}
		
	}
	
	public void deleteMethods(List methods, CtClass ct) throws Exception {
		Iterator it = methods.iterator();
		while(it.hasNext()) {
			JavaMethodInfo m = (JavaMethodInfo)it.next();
			CtClass[] parameters = new CtClass[m.Parameters.size()];
			if( m.Parameters.size() > 0 ) {
				Class[] params = m.theMethod.getParameterTypes();
				for (int i = 0; i < params.length; i++) {
					String name = params[i].getClass().getName();
					CtClass clazz = findClass(name);	
					parameters[i] = clazz;
				}
			}
			CtMethod method = ct.getDeclaredMethod(m.name(),parameters);
			ct.removeMethod(method);
		}
	}

	public CtClass findClass(String classname) throws Exception {
		CtClass ct = classpool.get(classname);
		return ct;
	}
	
	public void modifyClass(ClassWrapper c, String classpath) throws Exception {
		
		if(!pathList.contains(classpath)) {
			//insert classpath
			classpool.insertClassPath(classpath);
		}
		
		//load class
		CtClass ct = classpool.get(c.getClazz().getName());
			
		//make adjustments
		Concern concern = c.getConcern();
		Signature signature = concern.getSignature();
	    if( signature != null )
	    {
	       	List methods = signature.getMethods(MethodWrapper.ADDED);
	       	if(methods.size() > 0) {
	       		addMethods(methods,ct);
	       	}
	        	
	       	methods = signature.getMethods(MethodWrapper.REMOVED);
	       	if(methods.size() > 0) {
	       		deleteMethods(methods,ct);
	       	}
	    }
	    
	   byte[] bytecode = ct.toBytecode();
	   c.setByteCode(bytecode);
	}
}
