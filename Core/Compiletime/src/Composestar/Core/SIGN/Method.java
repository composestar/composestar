
package Composestar.Core.SIGN;

import java.util.List;


import Composestar.Core.LAMA.*;

public class Method 
{
	public MethodInfo methodInfo;
	
	public Method (MethodInfo dnmi)
	{
		setMethodInfo(dnmi);
	}
	
	public String getName()
	{
		return getMethodInfo().name();
	}
	
	public List getArguments()
	{
		return getMethodInfo().getParameters();
	}
	
	public String getReturnType ()
	{
		return getMethodInfo().ReturnTypeString;
	}
	
	public String getHashKey()
	{
		String key = getMethodInfo().name() + '%';
		List parameter = getMethodInfo().getParameters();
		for (int i = 0; i < parameter.size(); i++)
			key += ((ParameterInfo) parameter.get(i)).ParameterTypeString + '%';
		
		key += getMethodInfo().returnType();
		
		return key;
	}
	
	public MethodInfo getReference()
	{
		return getMethodInfo();
	}

    public MethodInfo getMethodInfo() {
        return methodInfo;
    }

    public void setMethodInfo(MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
    }
}
