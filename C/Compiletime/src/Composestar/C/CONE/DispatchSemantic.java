package Composestar.C.CONE;

public class DispatchSemantic extends Semantic{
	
	private String type="Dispatch";
	private String advice="";
	private boolean afterAdvice=false;
	private boolean beforeAdvice=true;
	private boolean redirectMessage=true;
	private boolean needsHeaderFiles=false;
	
	
	public DispatchSemantic(){	   	
	}
	
	public String getBeforeAdvice(){
		//System.out.println(filter.name + "Dispatch filter semantics"+((SubstitutionPart)(filter.getFilterElement(elementNumber).getMatchingPattern(0).getSubstitutionParts().elementAt(0))).getSelector().getName());
		String substitutionFunction= substitutionFunction();
		String reifiedMessage=reifiedMessageCode();
		String conditionCode= conditionCode();
		if(conditionCode.equals(""))
			advice= "return "+substitutionFunction+ "("+this.parameters()+");\n";
		else advice= "if("+ disableOperatorCode()+conditionCode()+") { return "+substitutionFunction+ "("+this.parameters()+");}\n";
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
