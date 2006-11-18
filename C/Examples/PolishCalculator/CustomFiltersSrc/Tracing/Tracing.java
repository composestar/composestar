import Composestar.C.CONE.Semantic;
/**
 * @author johan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Tracing extends Semantic{
	private String type="Tracing";
	private String advice="";
	private boolean afterAdvice=true;
	private boolean beforeAdvice=true;
	private boolean redirectMessage=false;
	private boolean needsHeaderFiles=false;
	
	public String getBeforeAdvice(){
		String advice="";
		advice+= "printf(\"\\n Tracing Concern:"+this.matchingFunction()+" \\n\");";
		advice+="TRACE_in(\""+this.matchingFunction()+"\");";
		return advice;
	}
	
	public String getAfterAdvice(){
		String advice="";
		advice+= "printf(\"\\n Tracing Concern:"+this.matchingFunction()+" \\n\");";
		advice+="TRACE_out(\""+this.matchingFunction()+"\");";
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
		return "Trace.h";
	}
	
	public boolean needsHeaderFiles(){
		return needsHeaderFiles;
	}
	
	public String getType(){
		return type;
	}
}
