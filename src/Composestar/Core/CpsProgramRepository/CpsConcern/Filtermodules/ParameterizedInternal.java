package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;

public class ParameterizedInternal extends Internal {
	public String parameter;
	public ParameterizedInternalAST pin_ast;
	
	public ParameterizedInternal() {
	    super();
	  }
	
	public ParameterizedInternal(ParameterizedInternalAST par_ast) {
	    super();
	    pin_ast = par_ast;
	    name = pin_ast.getName();
	    parameter = pin_ast.getParameter();
	  }
	
	public void setParameter(String aParameter){
		parameter = aParameter;
	}
	
	public String getParameter(){
		return parameter;
	}

	/**
	 * gets the type from the parent fm
	 */
	public ConcernReference getType() {
		return type;
	}
	
	public void setType(ConcernReference ref){
		type = ref;
	}
}
