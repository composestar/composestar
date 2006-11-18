import Composestar.C.CONE.Semantic;

/**
 * @author johan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Parametercheck extends Semantic{
	
	private String type="Parametercheck";
	private String advice="";
	private boolean afterAdvice=false;
	private boolean beforeAdvice=true;
	private boolean redirectMessage=false;
	private boolean needsHeaderFiles=false;
	
	public String getBeforeAdvice(){
		/***
		 if (op1 == NULL || op2 == NULL ){
		 	ERROR_LOG (" Input Parameter error in function : \%i",func_name );
		 		return ( INPUT_PARAMETER_ERROR );
		 		}
		
		**/
		String advice="";
		advice+= "printf(\"ParameterChecking Concern:"+this.matchingFunction()+" \");";
		
		for(int i=0; i<this.numberOfParametersMF(); i++){
			if(this.parameterAnnotation(i)!=null)
			{
				if(this.annotationType(parameterAnnotation(i)).equals("in"))
				{
					advice+= "if ("+this.parameterName(i)+ "== 0 ){";
					advice+= "printf(\"Input parameter in function = NULL function:"+this.matchingFunction()+" parameterName= "+this.parameterName(i)+" \\n\");";
					advice+= "}";
				}
				if(this.annotationType(parameterAnnotation(i)).equals("inout"))
				{
					advice+= "if ("+this.parameterName(i)+ "== 0 ){";
					advice+= "printf(\"InOut parameter in function = NULL function:"+this.matchingFunction()+" parameterName= "+this.parameterName(i)+" \\n\");";
					advice+= "}";
				}
				if(this.annotationType(parameterAnnotation(i)).equals("out"))
				{
					advice+= "if ("+this.parameterName(i)+ "== 0 ){";
					advice+= "printf(\"Out parameter in function = NULL function:"+this.matchingFunction()+" parameterName= "+this.parameterName(i)+" \\n\");";
					advice+= "}";
				}
			}
		}
		return advice;
	}
	
	public String getAfterAdvice(){
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
