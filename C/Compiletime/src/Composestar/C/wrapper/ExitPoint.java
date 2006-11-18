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
 * $Id: ExitPoint.java,v 1.1 2006/09/01 15:31:21 johantewinkel Exp $
 */
package Composestar.C.wrapper;

import Composestar.C.wrapper.parsing.TNode;

/**
 * Created by IntelliJ IDEA.
 * User: ByelasH
 * Date: 14-jan-2005
 * Time: 11:03:09
 * To change this template use File | Settings | File Templates.
 */
public class ExitPoint
{
    public final static int RETURN = 0;
    public final static int END = 1;
    public final static int EXIT = 2;
    public final static int IF_RETURN = 3;

    private String valueID = null;
    private TNode node = null;
    private int exitType;

    private boolean simpleVarReturn = false;

    public boolean isSimpleVarReturn()
    {
        return simpleVarReturn;
    }

    public ExitPoint(String valueID, TNode node, int exitType)
    {
        this.valueID = valueID;
        this.node = node;
        this.exitType = exitType;

        simpleVarReturn = true;
    }

    public ExitPoint(TNode node, int exitType)
    {
        this.node = node;
        this.exitType = exitType;
    }

    public String getValueID()
    {
        return valueID;
    }

    public TNode getNode()
    {
        return node;
    }

    public void setNode(TNode node)
    {
        this.node = node;
    }

    public int getExitType()
    {
        return exitType;
    }
}
