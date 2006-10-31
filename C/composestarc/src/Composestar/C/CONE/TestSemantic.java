/*
 * Created on 21-okt-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package Composestar.C.CONE;

/**
 * @author johan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestSemantic extends Semantic{
	private String type="Test";
	private String advice="";
	private boolean afterAdvice=true;
	private boolean beforeAdvice=true;
	private boolean redirectMessage=true;
	private boolean needsHeaderFiles=true;
	
	
	public String getBeforeAdvice(){
		String substitutionFunction= substitutionFunction();
		String reifiedMessage=reifiedMessageCode();
		String conditionCode= conditionCode();
		if(conditionCode.equals(""))
			advice= reifiedMessage +" "+substitutionFunction+ "(&msg);\n";
		else advice= reifiedMessage +" if("+ disableOperatorCode()+conditionCode()+") {"+substitutionFunction+ "(&msg);}\n";
		return advice;
	}
	
	public String getAfterAdvice(){
		if(methodAnnotation()!= null){
			String annotationvalue=annotationValue(methodAnnotation());
			System.out.println("MethodAnnotation: "+annotationValue(methodAnnotation()));
			advice="printf(\"methodAnnotation: type=" + annotationType(methodAnnotation())+" value=" +annotationvalue+"\");"; 
		}
		if(fileAnnotation()!=null){
			advice+="printf(\"FileAnnotation: type=" + annotationType(fileAnnotation())+" value=" +annotationValue(fileAnnotation())+"\");";
		}
		for(int i=0; i<numberOfParametersMF();i++)
		{
			if(parameterAnnotation(i)!=null){
				advice+="printf(\"parameterAnnotationNumber:"+i+": type=" + annotationType(parameterAnnotation(i))+" value=" +annotationValue(parameterAnnotation(i))+"\");";	
			}
		}
		return advice;
	}
	
	public boolean afterAdvice(){
		return afterAdvice;
	}
	
	public boolean beforeAdvice(){
		return beforeAdvice;
	} 	
	
	public boolean redirectMessage(){
		return redirectMessage;
	}
	
	public String headerFile(){
		return "";
	}
	
	public boolean needsHeaderFiles(){
		return needsHeaderFiles;
	}
	
	public String getType(){
		return type;
	}
}
