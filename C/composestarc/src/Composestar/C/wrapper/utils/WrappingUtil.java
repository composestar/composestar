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
 * $Id$
 */
package Composestar.C.wrapper.utils;

import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.TokenStreamException;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

import Composestar.C.wrapper.CWrapper;
import Composestar.C.wrapper.parsing.*;

/**
 * Created by IntelliJ IDEA.
 * User: ByelasH
 * Date: 9-dec-2004
 * Time: 11:58:40
 * To change this template use File | Settings | File Templates.
 */
public class WrappingUtil
{
    //com.ideals.weavec.wrapper.parsing lexer into AST (com.ideals.weavec.wrapper.parsing.TNode in our case)
    public TNode createTNode(GnuCLexer lexer)
    {
    	//CWrapper cwrapper = new CWrapper();
    	//wrapper.createWrappedAST(filename);
    	GnuCParser parser = new GnuCParser(lexer);
    	parser.activateSymbolCheck(false);
        parser.setASTNodeClass(TNode.class.getName());
        TNode.setTokenVocabulary("Composestar.C.wrapper.parsing.GnuCTokenTypes");
        try
        {
        	parser.translationUnit();
        }
        catch (RecognitionException e)
        {
            e.printStackTrace();
        }
        catch (TokenStreamException e)
        {
            e.printStackTrace();
        }
        //System.gc();
        AST ast = parser.getAST();
        TNode node = (TNode) ast;
        return node;
    }

    public TNode prepareNode(String inputStr)
    {
    	Reader input1 = new StringReader("void foo(){" +inputStr+"}");
    	System.out.println("Prepating node for: "+"void foo(){ " +inputStr+"}");
    	
    	GnuCLexer lexer1 = new GnuCLexer(input1);
        lexer1.setTokenObjectClass("Composestar.C.wrapper.parsing.CToken");
        
        TNode  node = createTNode(lexer1);
    	node = node.firstChildOfType(GnuCTokenTypes.NCompoundStatement);
        //node = node.firstChildOfType(109);
        node = (TNode) node.getFirstChild();
        
        node = node.deepCopy();
//        com.ideals.weavec.wrapper.parsing.TNode.printTree(node);
//        System.out.println("------------");
        
        preparingTokenNumbers(node);
        
        preparingRelationShips(node);
        
        flagNodes(node,true);

        return node;
    }
    
    public TNode prepareNodeForIntroduction(String inputStr)
    {
    	//inputStr="short x;"; 
    	Reader input1 = new StringReader("void foo(){" +inputStr+"}");
    	 
    	GnuCLexer lexer1 = new GnuCLexer(input1);
        lexer1.setTokenObjectClass("Composestar.C.wrapper.parsing.CToken");
        TNode  node = createTNode(lexer1);
        node = node.firstChildOfType(GnuCTokenTypes.NCompoundStatement);
        //node = node.firstChildOfType(109);
        node = (TNode) node.getFirstChild();
        node = node.deepCopy();
//        com.ideals.weavec.wrapper.parsing.TNode.printTree(node);
//        System.out.println("------------");
        preparingTokenNumbers(node);
        preparingRelationShips(node);
        
        flagNodes(node,true);
        
        return node;
    }
    
    public TNode prepareNodeForHeaderIntroduction(String inputStr)
    {
    	//inputStr="short x;"; 
    	//Reader input1 = new StringReader("void foo(){" +inputStr+"}");
    	Reader input1 = new StringReader( inputStr);
    	
    	GnuCLexer lexer1 = new GnuCLexer(input1);
        lexer1.setTokenObjectClass("Composestar.C.wrapper.parsing.CToken");
        TNode  node = createTNode(lexer1);
        node = node.firstChildOfType(GnuCTokenTypes.NCompoundStatement);
        //node = node.firstChildOfType(109);
        node = (TNode) node.getFirstChild();
        node = node.deepCopy();
//        com.ideals.weavec.wrapper.parsing.TNode.printTree(node);
//        System.out.println("------------");
        preparingTokenNumbers(node);
        preparingRelationShips(node);
        node.HEADER=true;
        flagNodes(node,true);
        
        return node;
    }
    
    public TNode[] prepareNodeForStructureIntroduction(String inputStr)
    {
    	Reader input1 = new StringReader("typedef struct {" +inputStr+"} hallo;");
    	//System.out.println("Prepating node for: "+"void foo(){ " +inputStr+"}");
        GnuCLexer lexer1 = new GnuCLexer(input1);
        lexer1.setTokenObjectClass("Composestar.C.wrapper.parsing.CToken");
        TNode  node = createTNode(lexer1);
        node = (TNode)node.getFirstChild();
        TNode node1 = (TNode)node.getNextSibling().getFirstChild().getNextSibling();
        
        node = (TNode)node1.getNextSibling();
        node = node.deepCopy();
        //TNode.printTree(node);
        
        //TNode.printTree(node);
        //node = node.firstChildOfType(109);
        node1 = node1.deepCopy();
        //TNode.printTree(node1);
//        com.ideals.weavec.wrapper.parsing.TNode.printTree(node);
//        System.out.println("------------");
        
        preparingTokenNumbers(node);
        preparingRelationShips(node);
        
        flagNodes(node,true);
        
        TNode[] nodes = new TNode[2];
        nodes[0] = node1;
        nodes[1] = node;
        
        return nodes;
    }

    public TNode prepareNodeWithBrackets(String inputStr)
    {
    	//System.out.println("Prepating node for: "+"void foo(){ " +inputStr+"}");
        Reader input1 = new StringReader("void foo(){" +inputStr+"}");
        GnuCLexer lexer1 = new GnuCLexer(input1);
        lexer1.setTokenObjectClass("Composestar.C.wrapper.parsing.CToken");
        TNode  node = createTNode(lexer1);
        
        node = node.firstChildOfType(GnuCTokenTypes.NCompoundStatement);
        node = node.deepCopy();
        removeFoo(node);
//        com.ideals.weavec.wrapper.parsing.TNode.printTree(node);
//        System.out.println("------------");
        preparingTokenNumbers(node);
        preparingRelationShips(node);
        
        flagNodes(node,true);
        
        return node;
    }
    
    public TNode prepareNodeWithSiblings(String code)
    {
    	//System.out.println("Prepating node for: "+"void foo(){ " +code+"}");
        Reader input1 = new StringReader("void foo(){" + code +"}");
        GnuCLexer lexer1 = new GnuCLexer(input1);
        lexer1.setTokenObjectClass("Composestar.C.wrapper.parsing.CToken");
        TNode  node = createTNode(lexer1);
        node = node.firstChildOfType(GnuCTokenTypes.NCompoundStatement);

        //com.ideals.weavec.wrapper.parsing.TNode.printTree(node);
        
        //node = (TNode) node.getFirstChild();
        node = node.deepCopyWithRightSiblings();
        removeFoo(node);
        // removing closing bracket
        //TNode closingBr = node.getLastSibling();
        //closingBr.removeSelf();
        //
        
        //com.ideals.weavec.wrapper.parsing.TNode.printTree(node);
        
        preparingTokenNumbers(node);
        preparingRelationShips(node);
        
        flagNodes(node,true);
        
        return node;
    }

    //making Token Numbers from zero
    private void preparingTokenNumbers(TNode node)
    {
        Vector vec = new Vector();
        fillVector(node, vec);
        int minNumber = 1000000;
        for (int i = 0; i < vec.size(); i++)
        {
            TNode tNode = (TNode) vec.elementAt(i);
            if(tNode.getTokenNumber() < minNumber && tNode.getTokenNumber() > 0)
                minNumber = tNode.getTokenNumber();
        }
        //System.out.println(" min number " + minNumber);
        if(minNumber > 1)
        {
            minNumber = minNumber - 1;
            for (int i = 0; i < vec.size(); i++)
            {
                TNode tNode = (TNode) vec.elementAt(i);
                if(tNode.getTokenNumber() != -1)
                    tNode.setTokenNumber(tNode.getTokenNumber() - minNumber);
            }
        }
    }

    private void fillVector(TNode node, Vector vec)
    {
        vec.addElement(node);
        if(node.getNumberOfChildren()>0)
        {
            fillVector((TNode) node.getFirstChild(), vec);
        }
        if(node.getNextSibling() != null)
        {
            fillVector((TNode) node.getNextSibling(), vec);
        }
    }
    
    private void flagNodes(TNode node, boolean introduced)
    {
    	//System.out.println("Flagging node["+introduced+"] "+node);
    	node.INTRODUCED = introduced;
        if(node.getNumberOfChildren()>0)
        {
        	flagNodes((TNode) node.getFirstChild(), introduced);
        }
        if(node.getNextSibling() != null)
        {
        	flagNodes((TNode) node.getNextSibling(), introduced);
        }
    }

    private void preparingRelationShips(TNode node)
    {
        if(node.getNumberOfChildren()>0)
        {
            ((TNode)node.getFirstChild()).setParent(node);
            preparingRelationShips((TNode) node.getFirstChild());
        }
        if(node.getNextSibling() != null)
        {
            ((TNode)node.getNextSibling()).setPreviousNode(node);
            preparingRelationShips((TNode) node.getNextSibling());
        }
    }

    private void removeFoo(TNode node)
    {
        //System.out.println(" !!!!!!!!!!!!!! ");
        Hashtable hashtable = ((TNode)node).getAttributesTable();
        hashtable.remove("scopeName");

    }

    public void printOneNode(TNode node)
    {
        System.out.print(" > " + node.getText() + "\t  type: " + node.getType() +
                        "  line: " + node.getLineNum() + "  children# " + node.getNumberOfChildren());

        Enumeration keys = ((TNode)node).getAttributesTable().keys();
        while (keys.hasMoreElements())
        {
            String key = (String) keys.nextElement();
            if(key.equalsIgnoreCase("tokenNumber"))
              System.out.print(" " + key + ":" + ((TNode)node).getAttribute(key));
            else if(key.equalsIgnoreCase("source"))
              System.out.print("  --isSource");
            else if(key.equalsIgnoreCase("scopeName"))
              System.out.print("   scope :" + ((TNode)node).getAttribute(key));
            else
              System.out.print("key::" + key + " == "+ ((TNode)node).getAttribute(key));
        }
        System.out.println("");

    }
}
