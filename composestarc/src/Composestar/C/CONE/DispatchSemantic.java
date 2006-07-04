package Composestar.C.CONE;

import java.util.Iterator;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.C.LAMA.*;
import java.util.List;

public class DispatchSemantic extends Semantic{
	
	private Filter filter; 
	private int elementNumber;
	private Concern concern;
	private List parameters =null;
	private String typeString="";
	
	public DispatchSemantic(Filter f, int elementNumber, Concern c){	   
		this.filter=f;
		this.elementNumber=elementNumber;
		this.concern= c;
	}
	
	public String retrieveSemantics(){
		//System.out.println(filter.name + "Dispatch filter semantics"+((SubstitutionPart)(filter.getFilterElement(elementNumber).getMatchingPattern(0).getSubstitutionParts().elementAt(0))).getSelector().getName());
		String matchingFunction=((MatchingPart)(filter.getFilterElement(elementNumber).getMatchingPattern(0).getMatchingParts().elementAt(0))).getSelector().getName();
		String substitutionFunction=((SubstitutionPart)(filter.getFilterElement(elementNumber).getMatchingPattern(0).getSubstitutionParts().elementAt(0))).getSelector().getName();
		String parameters=setParameterCode(matchingFunction);
		String code ="";
		
		code=parameters+  substitutionFunction+ "(&msg);\n";
		return code;
	}
	
	private String setParameterCode(String functionName){
		CFile file= (CFile)concern.getPlatformRepresentation();
		String parameters= "";
		String code="";
		CParameterInfo parameter=null;
		CMethodInfo method =(CMethodInfo)file.getMethod(functionName);
		if(method!=null){
			code+="struct message msg;\n";
			code+="void* temp;\n";
			List params =method.getParameters();
			Iterator par =params.iterator();
			int numberOfParameters=0;
			if(par.hasNext()){
				parameter=(CParameterInfo)par.next();
				code+=setArgument(parameter,numberOfParameters);
				numberOfParameters++;
				typeString+=typeString(parameter.getParameterTypeString());
				parameters+=parameter.getParameterTypeString()+ " " + parameter.getUnitName();
			}
			while(par.hasNext()){
				parameter=(CParameterInfo)par.next();
				code+=setArgument(parameter,numberOfParameters);
				numberOfParameters++;
				typeString+=typeString(parameter.getParameterTypeString());
				parameters+=","+ parameter.getParameterTypeString()+ " " + parameter.getUnitName();
			}
			code+=setArgumentNumber(numberOfParameters);
			code+=setType(typeString);
			return code;
		}
		else{ 
			//System.out.println("No method with name "+functionName + "kan ook niet");
			return code;
		}
	}
	
	private String setArgumentNumber(int totalNumberOfParameters){
		return "msg.argumentNumber="+totalNumberOfParameters+";";	
	}
	
	private String setType(String typeString){
		return "msg.type=\""+typeString+"\";\n";
	}
	
	private String typeString(String typeString){
		String type="";
		if(typeString.equals("int")){
			type="i";
		}
		else if(typeString.equals("char*")){
			type="s";
		}
		else if(typeString.equals("double")){
			type="f";
		}
		else if(typeString.equals("void *")){
			type="p";
		}
		else if(typeString.equals("int *")){
			type="n";
		}
		else if(typeString.equals("char")){
			type="c";
		}
		else if(typeString.equals("long")){
			type="l";
		}
		else type="?";
		return type;
	}
	
	private String setArgument(CParameterInfo parameter, int parameterOffset){
		String parameterToMessage="";
		parameterToMessage+="temp=&"+parameter.getUnitName()+";\n";
		parameterToMessage+="msg.argument["+parameterOffset+"]=temp;\n";
		//parameter.getParameterTypeString()+ " " + parameter.getUnitName()
		return parameterToMessage;
	}
	
	
	public String getType(){
		return "Dispatch";
	}
}
