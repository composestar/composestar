package Composestar.Java.TYM.SignatureTransformer;

import Composestar.Core.CpsProgramRepository.Concern;

import java.lang.reflect.*;

class ClassWrapper{
	
	private Class theClass;
	private Concern concern;
	private byte[] bytecode;
	
	public ClassWrapper(Class aClass, Concern concern, byte[] bytecode){
		theClass = aClass;
		this.concern = concern;
		this.bytecode = bytecode;
	}
	
	public void setByteCode(byte[] bytecode){
		this.bytecode = bytecode;
	}
	
	public Class getClazz(){
		return theClass;
	}
	
	public Concern getConcern(){
		return concern;
	}
	
	public byte[] getByteCode(){
		return bytecode;
	}
}
