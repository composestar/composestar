import Composestar.C.CONE.Semantic;
/**
 * @author johan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Timing extends Semantic{
	
	private String type="Timing";
	private String advice="";
	private boolean afterAdvice=true;
	private boolean beforeAdvice=true;
	private boolean redirectMessage=false;
	private boolean needsHeaderFiles=true;
	
	public String getBeforeAdvice(){
		String advice="";
		advice+= "time_t time1;";
		advice+= "char * function_name = \"" + this.matchingFunction() +"\";";
		advice+= "(void)time(&time1);";
		advice+= "printf(\"\\n Timing Concern:%s \\n\", function_name);";
		advice+="TIMING_in(function_name, time1);";
		return advice;
	}
	
	public String getAfterAdvice(){
		String advice= "time_t time2;";
		advice+="(void)time(&time2);";
		advice+= "printf(\"\\n Timing Concern:"+this.matchingFunction()+" \\n\");";
		advice+="TIMING_out(\""+this.matchingFunction()+"\", time2);";
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
		return "Timing.h";
	}
	
	public boolean needsHeaderFiles(){
		return needsHeaderFiles;
	}
	
	public String getType(){
		return type;
	}

}
