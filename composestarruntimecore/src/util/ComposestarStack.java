package Composestar.RuntimeCore.Utils;

import java.util.Stack;

public class ComposestarStack {
    private Stack inner;
    
    /**
     * @roseuid 404C4B6001EE
     */
    public ComposestarStack() {
		inner = new Stack();     
    }
    
    /**
     * @return java.lang.Object
     * @roseuid 4115FAD90297
     */
    public Object pop() {
     	return inner.pop();     
    }
    
    /**
     * @param obj
     * @roseuid 4115FADE00E5
     */
    public void push(Object obj) {
     	inner.push(obj);     
    }
    
    /**
     * @param s
     * @roseuid 4115FAF2010C
     */
    public void pushAll(ComposestarStack s) {
		ComposestarStack tmp = new ComposestarStack();
		for(;s.length()!=0;)
		{
			tmp.push(s.pop());
		}
		for(;tmp.length()!=0;)
		{
			inner.push(tmp.pop());
		}     
    }
    
    /**
     * @return int
     * @roseuid 4115FB0A0157
     */
    public int length() {
		return inner.size();     
    }
}
