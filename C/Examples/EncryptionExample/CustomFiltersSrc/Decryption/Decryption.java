import Composestar.C.CONE.Semantic;

/**
 * @author johan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Decryption extends Semantic{
	private String type="Decryption";
	private String advice="";
	private boolean afterAdvice=false;
	private boolean beforeAdvice=true;
	private boolean redirectMessage=false;
	private boolean needsHeaderFiles=false;
	
	/**Does not use parameter functions yet**/
	public String getBeforeAdvice(){
		String advice="";
		advice+= "char* x;";
		advice+= "x = "+ this.parameterName(0)+";";
		advice+= "printf(\"Decrypt message with filter:"+this.parameterName(0)+" \n\");";
		advice+= "while(*x != \'\\0\'){";
		advice+= "*x -= 20;";
		advice+= "x++;"; 
		advice+= "}";
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
