package Composestar.C.CONE;

public class MetaSemantic extends Semantic {
	
	private String type="Meta";
	private String advice="";
	private boolean afterAdvice=true;
	private boolean beforeAdvice=true;
	private boolean redirectMessage=true;
	private boolean needsHeaderFiles=false;
	
	
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
