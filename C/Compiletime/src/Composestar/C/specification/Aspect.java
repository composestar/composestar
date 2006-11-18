/*
 * This file is part of WeaveC project [http://weavec.sf.net].
 * Copyright (C) 2005 University of Twente.
 *
 * Licensed under the BSD License.
 * [http://www.opensource.org/licenses/bsd-license.php]
 * 
 * Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions
   are met:
 * 1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University of Twente nor the names of its 
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY AUTHOR AND CONTRIBUTORS ``AS IS'' AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODSOR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * $Id: Aspect.java,v 1.1 2006/09/01 15:31:21 johantewinkel Exp $
 */
package Composestar.C.specification;

import java.util.*;

import Composestar.C.wrapper.utils.GeneralUtils;

public class Aspect
{
    private String id = null;
    private Hashtable pointcuts = new Hashtable();
    private Hashtable advices = new Hashtable();
    
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void addPointcut(Pointcut pointcut)
    {
        pointcuts.put(pointcut.getId(),pointcut);
    }

    public int getNumberOfPointcuts()
    {
        return pointcuts.size();
    }

    public Pointcut getPointcut(int i)
    {
    	Iterator it = pointcuts.values().iterator();
    	int j=0;
    	while(it.hasNext())
    	{
    		if(j == i) return (Pointcut)it.next();
    		else it.next();
    		j++;
    	}
    	return null;
    }
    
    public void addAdvice(Advice advice)
    {
    	this.advices.put(advice.getId(), advice);
    }
    
    public int getNumberOfAdvices()
    {
    	return advices.size();
    }
    
    public Advice getAdvice(int i)
    {
    	Iterator it = advices.values().iterator();
    	int j=0;
    	while(it.hasNext())
    	{
    		if(j == i) return (Advice)it.next();
    		else
    		{
    			it.next();
    			j++;
    		}
    	}
    	return null;
    }
    
    public String toString()
    {
    	String tmp = "";
    	tmp+="Aspect("+aspectIsSane()+"): "+id+"\n";
    	for(int i=0; i<this.getNumberOfPointcuts(); i++)
    	{
    		tmp+=this.getPointcut(i).toString();
    	}
    	for(int i=0; i<this.getNumberOfAdvices(); i++)
    	{
    		tmp+=this.getAdvice(i).toString();
    	}
    	return tmp;
    }
    
    public boolean aspectIsSane()
    {
    	for(int i=0; i<this.getNumberOfPointcuts(); i++)
    	{
    		Pointcut p = this.getPointcut(i);
    		for(int j=0; j<p.getNumberOfAdviceApplications(); j++)
    		{
    			AdviceApplication aa = p.getAdviceApplication(j);
    			if(this.advices.containsKey(aa.getId()))
    			{
    				return true;
    			}
    			else
    				return false;
    		}
    	}
    	return true;
    }
    
    public ArrayList getAllFiles()
    {
    	ArrayList list = new ArrayList();
    	for(int i=0; i<this.getNumberOfPointcuts(); i++)
    	{
    		Pointcut p = this.getPointcut(i);
    		for(int j=0; j<p.getNumberOfFunctions(); j++)
    		{
    			list.add(p.getFunctions(j).getFile()); 
    		}
    	}
    	return list;
    }
    
    public Hashtable getAdvices()
    {
    	return this.advices;
    }

	public boolean hasCall()
	{
		Iterator adviceit = this.advices.values().iterator();
		while(adviceit.hasNext())
		{
			Advice advice = (Advice)adviceit.next();
			if(advice.getType() == GeneralUtils.FUNCTION_CALL)
			{
				return true;
			}
		}
		return false;
	}
}
