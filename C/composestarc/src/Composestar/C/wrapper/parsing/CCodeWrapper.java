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
 * $Id: CCodeWrapper.java,v 1.1 2006/03/16 14:08:54 johantewinkel Exp $
 */
package Composestar.C.wrapper.parsing;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import Composestar.C.wrapper.ExitPoint;
import Composestar.C.wrapper.Function;
import Composestar.C.wrapper.FunctionFactory;
import Composestar.C.wrapper.parsing.*;

/**
 * Created by IntelliJ IDEA.
 * User: ByelasH
 * Date: 20-dec-2004
 * Time: 11:17:45
 * To change this template use File | Settings | File Templates.
 */
public class CCodeWrapper
{

    private static final int FOR_REMOVAL = -999;

    private GnuCLexer lexer = null;
    private GnuCParser parser = null;
    private GnuCEmitter emitter = null;
    private TNode node = null;
    private PreprocessorInfoChannel infoChannel = null;

    private Vector allNodes = new Vector();
    private Vector functions = new Vector();

    private FunctionFactory functionFactory = new FunctionFactory();

    public CCodeWrapper(String filename) throws FileNotFoundException
    {
        DataInputStream input = new DataInputStream(new FileInputStream(filename));
        initialization(input);
        System.out.println("... initialization done");
        fillAllNodes();
        createFunctions(filename);
    }

    private void fillAllNodes()
    {
        allNodes.addElement(node);
        if(node.getNumberOfChildren()>0)
        {
            fillVectorAll((TNode) node.getFirstChild(), allNodes);
            ((TNode)node.getFirstChild()).setParent(node);
        }
        if(node.getNextSibling() != null)
        {
            fillVectorAll((TNode) node.getNextSibling(), allNodes);
            ((TNode)node.getNextSibling()).setPreviousNode(node);
        }
    }

    private void fillVectorAll(TNode node, Vector vec)
    {
        vec.addElement(node);

        if(node.getNumberOfChildren()>0)
        {
            fillVectorAll((TNode) node.getFirstChild(), vec);
            ((TNode)node.getFirstChild()).setParent(node);
        }
        if(node.getNextSibling() != null)
        {
            fillVectorAll((TNode) node.getNextSibling(), vec);
            ((TNode)node.getNextSibling()).setPreviousNode(node);
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

    public void printAllNodes()
    {
        for (int i = 0; i < allNodes.size(); i++)
        {
            TNode node = (TNode) allNodes.elementAt(i);
            System.out.print(" > " + node.getText());
            System.out.print("\t  type: " + node.getType());
            System.out.print("\t  # : " + node.getTokenNumber());
            System.out.print("\t  line: " + node.getLineNum());
            System.out.print("\t  #chi: " + node.getNumberOfChildren());
            System.out.print("\t  scope: " + node.getScope());
            if(node.isSource()) System.out.print("\t  is Source: ");
            System.out.println("");
        }
    }

    private void initialization(DataInputStream input)
    {
        lexer = new GnuCLexer(input);
        lexer.setTokenObjectClass("Composestar.C.wrapper.parsing.CToken");
        lexer.initialize();
        infoChannel = lexer.getPreprocessorInfoChannel();
        System.out.println(" ... lexer done");
        parser = new GnuCParser(lexer);
        System.out.println("");
        emitter = new GnuCEmitter(infoChannel);
        parser.setASTNodeType(TNode.class.getName());
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
        System.gc();
        System.out.println(" ... Composestar.C.wrapper.parsing done");
        node = (TNode) parser.getAST();
        System.out.println(" ... node done");
    }

    public void emite(String filename) throws FileNotFoundException, RecognitionException
    {
        //emitter.setAdvancedOutput(filename);
        //emitter.setASTNodeType(com.ideals.weavec.wrapper.parsing.TNode.class.getName());
        emitter.translationUnit( node );
    }

    public void emite() throws RecognitionException
    {
        //emitter.setCurrentOutputToScreen();
        emitter.translationUnit( node );
    }

    public void emiteToFile(String filename) throws FileNotFoundException, RecognitionException
    {
        //emitter.setCurrentOutputToFile(filename);
    	    emitter.translationUnit( node );
    }

    public void printTreeElements()
    {
        System.out.print(" > " + node.getText() + "\t  type: " + node.getType() +
                "  line: " + node.getLineNum() + "  children# " + node.getNumberOfChildren());

        Enumeration keys = ((TNode)node).getAttributesTable().keys();
        while (keys.hasMoreElements())
        {
            String key = (String) keys.nextElement();
            if(key.equalsIgnoreCase("tokenNumber"))
                System.out.print(" " + key + ":" + ((TNode)node).getAttribute(key));
            if(key.equalsIgnoreCase("source"))
                System.out.print("  --isSource");
            if(key.equalsIgnoreCase("scopeName"))
                System.out.print("   scope :" + ((TNode)node).getAttribute(key));
        }
        System.out.println("");

        if(node.getNumberOfChildren()>0)
        {
            TNode firstChild = (TNode) node.getFirstChild();
            printNextElements(firstChild);
        }
        if(node.getNextSibling() != null)
        {
            TNode firstSibling = (TNode) node.getNextSibling();
            printNextElements(firstSibling);
        }
    }

    private void printNextElements(TNode node)
    {
       System.out.print(" > " + node.getText() + "\t  type: " + node.getType() +
                "  line: " + node.getLineNum() + "  children# " + node.getNumberOfChildren());

        Enumeration keys = ((TNode)node).getAttributesTable().keys();
        while (keys.hasMoreElements())
        {
            String key = (String) keys.nextElement();
            if(key.equalsIgnoreCase("tokenNumber"))
                System.out.print(" " + key + ":" + ((TNode)node).getAttribute(key));
            if(key.equalsIgnoreCase("source"))
                System.out.print("  --isSource");
            if(key.equalsIgnoreCase("scopeName"))
                System.out.print("   scope :" + ((TNode)node).getAttribute(key));
        }
        System.out.println("");

        if(node.getNumberOfChildren()>0)
        {
            TNode firstChild = (TNode) node.getFirstChild();
            printNextElements(firstChild);
        }
        if(node.getNextSibling() != null)
        {
            TNode firstSibling = (TNode) node.getNextSibling();
            printNextElements(firstSibling);
        }
    }

    public void printTree()
    {
        TNode.printTree(node);
    }

    public void printNodeDeep(TNode n)
    {
        TNode.printTree(n);
    }

    public void printPreprocessorInfo()
    {
        System.out.println(" max token number: " + infoChannel.getMaxTokenNumber());
        Hashtable lineLists =  infoChannel.getHashtableLine();
        for (int i = 0; i <= infoChannel.getMaxTokenNumber(); i++){
            Integer inti = new Integer(i);
            System.out.print(" # " + inti);
            if ( lineLists.containsKey( inti ) )
            {
                System.out.print(" ---- ");
                Vector tokenLineVector = (Vector) lineLists.get( inti );
                if ( tokenLineVector != null)
                {
                    Enumeration tokenLines = tokenLineVector.elements();
                    while ( tokenLines.hasMoreElements() )
                    {
                        System.out.println( "  " + inti + " " + tokenLines.nextElement());
                    }
                }
            }
            else
                System.out.println(" null ");
        }
        System.out.println("");
    }

    public void printXML() throws IOException
    {
        System.out.println("XML ---------------------");
        Writer out = new BufferedWriter(new OutputStreamWriter(System.out));
        node.xmlSerialize(out);
        out.flush();
        System.out.println("/end XML ----------------");
    }


    private void insertB(TNode bNode, TNode iNode)
    {
        int tokenNumber  = bNode.getTokenNumber();
        int lineNumber = bNode.getLineNum();

        Vector vecINode = new Vector();
        fillVector(iNode, vecINode);
        int insertionNumber = getMaxTokenNumber(vecINode);

        //System.out.println(" numbers: " + vecINode.size() + " & " + insertionNumber);

        increaseNumberNewNode(vecINode, tokenNumber, lineNumber+1);
        increaseNumberMainNode(insertionNumber, tokenNumber);
        increaseNumberInfoChannel(insertionNumber, tokenNumber);

        TNode firstChild = (TNode) bNode.getFirstChild();

        bNode.setFirstChild(iNode);
        iNode.setParent(bNode);

        if(bNode.getNumberOfChildren() > 0)
        {
            iNode.setNextSibling(firstChild);
            firstChild.setPreviousNode(iNode);
        }
        updateVector(vecINode);
    }

    private int getMaxTokenNumber(Vector vecINode)
    {
        int result = -1;
        for (int i = 0; i < vecINode.size(); i++)
        {
            TNode node =  (TNode) vecINode.elementAt(i);
            Integer n = (Integer)node.getAttribute(TNode.TOKEN_NUMBER);
            if(n != null)
            {
                int number = n.intValue();
                if(number > result)
                    result = number;
            }
        }
        return result;
    }

    private void updateVector(Vector vecINode)
    {
        for (int i = 0; i < vecINode.size(); i++)
        {
            TNode tnode =  (TNode) vecINode.elementAt(i);
            allNodes.addElement(tnode);
        }
    }

    private void increaseNumberNewNode(Vector vecINode, int tokenNumber, int lineNumber)
    {
        for (int i = 0; i < vecINode.size(); i++)
        {
            TNode tNode =  (TNode) vecINode.elementAt(i);
            tNode.setLineNum(lineNumber);
            int oldNumber = tNode.getTokenNumber();
            if(oldNumber != -1)
                tNode.setTokenNumber(oldNumber + tokenNumber);
        }
        // printElements(vecINode);
    }

    private void increaseNumberNewNode(TNode iNode, int tokenNumber, int lineNumber)
    {
      Vector vecINode = new Vector();
      fillVector(iNode, vecINode);

      for (int i = 0; i < vecINode.size(); i++)
      {
         TNode tNode =  (TNode) vecINode.elementAt(i);

              if(tNode.getLineNum()> 0)
                tNode.setLineNum(tNode.getLineNum() + lineNumber);
             else
                tNode.setLineNum(lineNumber);

         int oldNumber = tNode.getTokenNumber();
         if(oldNumber != -1)
         tNode.setTokenNumber(oldNumber + tokenNumber);
       }
            // printElements(vecINode);
    }


    private void printElements(Vector vec)
    {
        System.out.println(" elements:  -------------------------" + vec.size());
        for (int i = 0; i < vec.size(); i++)
        {
            TNode node = (TNode) vec.elementAt(i);
            System.out.print(" > " + node.getText() + "\t  type: " + node.getType() +
            "  line: " + node.getLineNum() + "  children# " + node.getNumberOfChildren());

            Enumeration keys = ((TNode)node).getAttributesTable().keys();
            while (keys.hasMoreElements())
            {
                String key = (String) keys.nextElement();
                if(key.equalsIgnoreCase(TNode.TOKEN_NUMBER))
                    System.out.print(" " + key + ":" + ((TNode)node).getAttribute(key));
                if(key.equalsIgnoreCase(TNode.IS_SOURCE))
                    System.out.print("  --isSource");
                if(key.equalsIgnoreCase(TNode.SCOPE_NAME))
                    System.out.print("   scope :" + ((TNode)node).getAttribute(key));
                else
                    System.out.print(" " + key);
            }
            System.out.println("");
        }
        System.out.println("x------------------------------------");
    }

    private void increaseNumberInfoChannel(int iNumber, int tokenNumber)
    {

        int maxTokenNumber = infoChannel.getMaxTokenNumber() + iNumber;
        infoChannel.setMaxTokenNumber(maxTokenNumber);

        Hashtable lineLists =  infoChannel.getHashtableLine();
        Hashtable newHashtable = (Hashtable) lineLists.clone();

        for (int i = 0; i <= infoChannel.getMaxTokenNumber(); i++)
        {
            Integer inti = new Integer(i);
            if ( lineLists.containsKey( inti ) )
            {
                Vector tokenLineVector = (Vector) lineLists.get( inti );
                if ( tokenLineVector != null && i > tokenNumber)
                {
                    newHashtable.remove(new Integer(i));
                    newHashtable.put(new Integer(i + iNumber), tokenLineVector);
                }
            }
        }
        infoChannel.setHashtable(newHashtable);
    }

    private void increaseNumberMainNode(int iNumber, int tNumber)
    {
        for (int i = 0; i < allNodes.size(); i++)
        {
            TNode tNode = (TNode) allNodes.elementAt(i);
            int curNumber = tNode.getTokenNumber();
            if (curNumber > tNumber)
            {
                tNode.setTokenNumber(curNumber + iNumber);
                tNode.setLineNum(tNode.getLineNum() + 1);
            }
        }
    }

    private void insertA(TNode bNode, TNode iNode)
    {
        int tokenNumber = -100;
        int lineNumber = -100;
        TNode nextSibling = (TNode) bNode.getNextSibling();

        tokenNumber  = nextSibling.getTokenNumber();
        lineNumber = nextSibling.getLineNum();

        if(tokenNumber == -1) // node without token number
        {
            tokenNumber = findMinTokenNumber(nextSibling.deepCopy());
        }

        System.out.println("token # : " + tokenNumber + "   line # " + lineNumber );

        Vector vecINode = new Vector();
        fillVector(iNode, vecINode);

        //int insertionNumber = vecINode.size();
        int insertionNumber = getMaxTokenNumber(vecINode);

        increaseNumberNewNode(vecINode, tokenNumber-1, lineNumber);
        increaseNumberMainNode(insertionNumber, tokenNumber-1);
        increaseNumberInfoChannel(insertionNumber, tokenNumber-1);

        bNode.setNextSibling(iNode);
        iNode.setPreviousNode(bNode);

        iNode.setNextSibling(nextSibling);
        nextSibling.setPreviousNode(iNode);

        updateVector(vecINode);
    }

    private int findMinTokenNumber(TNode n)
    {
       Vector v = new Vector();
       fillVector(n, v);
        int min = 2147483647;
        for (int i = 0; i < v.size(); i++)
        {
            int number = ((TNode) v.elementAt(i)).getTokenNumber();
            if(number > 0 && number < min)
                min = number;
        }
        return min;
    }

    private int findMaxTokenNumber(TNode nnn)
    {
        int result = -1;
        Vector v = new Vector();
        fillVector(nnn, v);

        for (int i = 0; i < v.size(); i++)
        {
            TNode node =  (TNode) v.elementAt(i);
            Integer n = (Integer)node.getAttribute(TNode.TOKEN_NUMBER);
            if(n != null)
            {
                int number = n.intValue();
                if(number > result)
                    result = number;
            }
        }
        return result;
    }

    private void createFunctions(String filename)
    {
        for (int i = 0; i < allNodes.size(); i++)
        {
            TNode tNode = (TNode) allNodes.elementAt(i);
            if(tNode.getType() == 112)
            {
                /** deleted by johan**/
            	//Function f = functionFactory.createFunction(tNode,filename);
                //functions.addElement(f);
            }
        }
    }

    public void printTreeToFile(String s) throws FileNotFoundException
    {
        TNode.printTreeToFile(node, s);
    }

    public GnuCLexer getLexer()
    {
        return lexer;
    }

    public CSymbolTable getCSymbolTable()
    {
        return parser.symbolTable;
    }


    public void printFunctions()
    {
        for (int i = 0; i < functions.size(); i++)
        {
            Function function = (Function) functions.elementAt(i);
            System.out.println(" " + function.getName());
        }
    }

    public void insertBeforeTest(TNode[] node3)
    {
        for (int i = 0; i < functions.size(); i++)
        {
            Function function = (Function) functions.elementAt(i);
            // !!! here the analysis where to insert node before node
            {
                insertBeforeFunExecutionBody(function, node3[i]);
            }

        }
    }

    private void insertBeforeFunExecutionBody(Function function, TNode node)
    {
        TNode node113 = function.getToInsertBeforeNode();
        TNode firstChild = (TNode) node113.getFirstChild();
        if(firstChild.getType() != 102)
            insertB(function.getToInsertBeforeNode(), node);
        else
        {
            while (((TNode) firstChild.getNextSibling()).getType() == 102)
                firstChild = (TNode) firstChild.getNextSibling();
                insertA(firstChild, node);
        }
    }

    public void insertAfterTest(TNode[] node4)
    {
        //for (int i = 0; i < functions.size(); i++)
        for (int i = 0; i < 1; i++)
        {
            Function function = (Function) functions.elementAt(i);
            weaveExitFunction(function, node4[i]);
        }
    }
    // todo working here
    private void weaveExitFunction(Function function, TNode iNode)
    {
        System.out.println("----------- function " + function.getName());
        for(int i = 0; i < function.getNumberExitPoints(); i++)
        {
            ExitPoint exitPoint = function.getExitPoint(i);
            TNode exitNode = exitPoint.getNode();
            //
//            System.out.print("exit node : ");
//            exitNode.printKnoledge();

            // case when we add just to the end of function (no return - no exit)
            if(exitPoint.getExitType() == ExitPoint.END)
            {
                prepareNodeEndCase(iNode, exitNode);
            }
            else
            {
                TNode copyExitNode = exitNode.deepCopy();
                prepareNodeReturnCase(iNode, copyExitNode);
                printNodeDeep(iNode);

                TNode prev = exitNode.getPreviousNode();
                TNode next = (TNode) exitNode.getNextSibling();

                if(prev != null)
                {
                    prev.setNextSibling(iNode);
                    iNode.setPreviousNode(prev);
                }
                else
                {
                    TNode parent = exitNode.getParent();
                    parent.setFirstChild(iNode);
                    iNode.setParent(parent);
                }

                if(next != null)
                    iNode.setNextSibling(next);


            }
        }

    }

    private void prepareNodeEndCase(TNode iNode, TNode exitNode)
    {

    }

    private void prepareNodeReturnCase(TNode iNode, TNode copyExitNode)
    {
        int min = findMinTokenNumber(copyExitNode);
        int max_old = findMaxTokenNumber(copyExitNode);
        int line = copyExitNode.getLineNum();

        int insertionNumber = iNode.firstChildOfType(8).getTokenNumber();

        int newLineNumber = findMaxLineNumber(iNode);

        increaseNumberNewNode(iNode, min, line);

        increaseNumberNewNode(copyExitNode, insertionNumber, newLineNumber + 1);

        //todo after insertion of return into compound statement
        // change token numbers of the rest of compound statement
        newLineNumber = findMaxLineNumber(copyExitNode);
        iNode.firstChildOfType(8).setTokenNumber(findMaxTokenNumber(copyExitNode) + 1);
        iNode.firstChildOfType(8).setLineNum(newLineNumber);// + 1
        //todo increase token number of all text
        //new line numbering is bad

        TNode lastNode = iNode.firstChildOfType(8);
        TNode prevNode = lastNode.getPreviousNode();
        prevNode.setNextSibling(copyExitNode);
        copyExitNode.setPreviousNode(prevNode);
        copyExitNode.setNextSibling(lastNode);
        lastNode.setPreviousNode(copyExitNode);
    }

    private int findMaxLineNumber(TNode iNode)
    {
        int result = -1;
        Vector v = new Vector();
        fillVector(iNode, v);

        for (int i = 0; i < v.size(); i++)
        {
            int number = ((TNode) v.elementAt(i)).getLineNum();
            if(number > result)
                result = number;
        }
        return result;
    }


    // just for testing - later remove
    public void testFunctions()
    {
        for (int i = 0; i < functions.size(); i++)
        {
            ((Function) functions.elementAt(i)).testFunction();
        }
    }

    //todo remove later -  only for testing
    private void printOneNode(TNode node)
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

    public void testEmite(String filename) throws RecognitionException, FileNotFoundException
    {
        TNode node1 = node.deepCopyWithRightSiblings();
        //emitter.setAdvancedOutput(filename);
        emitter.translationUnit(node);
    }

}
