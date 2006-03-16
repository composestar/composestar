package Composestar.Core.CKRET;

import java.io.Serializable;

public class Conflict implements Serializable
{
	private String resource = "";
	private String sequence = "";
	private String msg = "";
	private String expr = "";
	
	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getResource() {
		return resource;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public String getSequence() {
		return sequence;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getMsg() {
		return msg;
	}
	public void setExpr(String expr) {
		this.expr = expr;
	}
	public String getExpr() {
		return expr;
	}
}
