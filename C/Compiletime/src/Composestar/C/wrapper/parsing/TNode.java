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
 * $Id: TNode.java,v 1.1 2006/09/04 08:12:15 johantewinkel Exp $
 */
package Composestar.C.wrapper.parsing;

import antlr.collections.AST;
import antlr.CommonAST;
import antlr.Token;
import java.lang.reflect.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import Composestar.C.wrapper.parsing.CToken;


public class TNode extends CommonAST
{
  public final static String TOKEN_NUMBER = "tokenNumber";
  public final static String SCOPE_NAME = "scopeName";
  public final static String IS_SOURCE = "source";
  
  public static final long serialVersionUID = 5324853;
  
  public boolean INTRODUCED = false;
  public boolean HEADER =false;

  protected int ttype;
  protected String text;
  protected int lineNum = 0;
  //  these four shits do not work
  protected TNode defNode;
  protected TNode up;
  protected TNode left;
  protected boolean marker = false;
  //
  protected Hashtable attributes = null;
  static String tokenVocabulary;  
  
  private TNode parent = null;
  private TNode previousNode = null;
  private int oldComment = -1;
  private String comment="";

  public void setParent(TNode parent)
  {
    this.parent = parent;
  }

    public TNode getParent()
    {
        return parent;
    }

    public void setPreviousNode(TNode previousNode)
    {
        this.previousNode = previousNode;
    }

    public TNode getPreviousNode()
    {
        return previousNode;
    }

    public int getOldComment()
    {
        return oldComment;
    }

    public void setOldComment(int oldComment)
    {
        this.oldComment = oldComment;
    }

  /** Set the token vocabulary to a tokentypes class
      generated by antlr.
  */
  public static void setTokenVocabulary(String s) {
    tokenVocabulary = s;
  }
  public String getComment()
  {
      return comment;
  }

  public void addComment(String comment)
  {
      if(this.comment.indexOf(comment)==-1)
    	  this.comment += comment+"\n";
  }
    
public void initialize(Token token) {
        CToken tok = (CToken) token;
        setText(tok.getText());
        setType(tok.getType());
        setLineNum(tok.getLine());
        setAttribute("source", tok.getSource());
        setAttribute("tokenNumber", new Integer(tok.getTokenNumber()));
}       
public void initialize(AST tr) {
        TNode t = (TNode) tr;
        setText(t.getText());
        setType(t.getType());
        setLineNum(t.getLineNum());
        setDefNode(t.getDefNode());
        this.attributes = t.getAttributesTable();
}       


  /** Get the token type for this node */
  public int getType() { return ttype; }
  
  /** Set the token type for this node */
  public void setType(int ttype_) { 
    ttype = ttype_; 
  }
  
  /** Get the marker value for this node.
   This member is a general-use marker.
   */
  public boolean getMarker() { return marker; }
  
  /** Set the marker value for this node.
   This property is a general-use boolean marker.
   */
  public void setMarker(boolean marker_)
  {
    marker = marker_; 
  }

  /** get the hashtable that holds attribute values.
   */  
  public Hashtable getAttributesTable()
  {
    if(attributes == null)
      attributes = new Hashtable(7);
    return attributes;
  }

  /** set an attribute in the attribute table.
   */
  public void setAttribute(String attrName, Object value)
  {
    if(attributes == null)
      attributes = new Hashtable(7);
    attributes.put(attrName,value);
  }

  /** lookup the attribute name in the attribute table.
    If the value does not exist, it returns null.
    */
  public Object getAttribute(String attrName)
  {
    if(attributes == null)
      return null;
    else
      return attributes.get(attrName);
  }

  /** Get the line number for this node.
   If the line number is 0, search for a non-zero line num among children */
  public int getLineNum() {
    if(lineNum != 0)
      return lineNum; 
    else
      if(down == null)
        return lineNum; 
      else
        return ((TNode)down).getLocalLineNum();
  }
  
  public int getLocalLineNum() { 
    if(lineNum != 0)
      return lineNum; 
    else
      if(down == null)
        if(right == null)
          return lineNum; 
        else
          return ((TNode)right).getLocalLineNum();
      else
        return ((TNode)down).getLocalLineNum();
  }
  
  /** Set the line number for this node */
  public void setLineNum(int lineNum_) { 
    lineNum = lineNum_; 
  }
  
  /** Get the token text for this node */
  public String getText() { return text; }
  
  /** Set the token text for this node */
  public void setText(String text_) { 
    text = text_; 
  }
  
  /** return the last child of this node, or null if there is none */
  public TNode getLastChild() {
    TNode down = (TNode)getFirstChild();
    if(down != null)
      return down.getLastSibling(); 
    else 
      return null;
  }

  /** return the last sibling of this node, which is 
      this if the next sibling is null */
  public TNode getLastSibling() {
    TNode next = (TNode)getNextSibling();
    if(next != null)
      return next.getLastSibling();
    else
      return this;
  }

  /** return the first sibling of this node, which is 
      this if the prev sibling is null */
  public TNode getFirstSibling()
  {
    TNode prev = (TNode)left;
    if(prev != null)
      return prev.getFirstSibling();
    else
    {
        System.out.println("pizdec again!!!");
      return this;
    }
  }

  /** return the parent node of this node */
//  public TNode getParent() {
//    return (TNode)getFirstSibling().up;
//  }


  /** add the new node as a new sibling, inserting it ahead of any
    existing next sibling.  This method maintains double-linking.
    if node is null, nothing happens.  If the node has siblings, 
    then they are added in as well.
    */
  public void addSibling(AST node) {
    if(node == null) return;
    TNode next = (TNode)right;
    right = (TNode)node;
    ((TNode)node).left = this;
    TNode nodeLastSib = ((TNode)node).getLastSibling();
    nodeLastSib.right = next;
    if(next != null)
      next.left = nodeLastSib;
  }

 
  /** return the number of children of this node */
  public int numberOfChildren() {
    int count = 0;
    AST child = getFirstChild();
    while(child != null) {
      count++;
      child = child.getNextSibling();
    }
    return count;
  }


  /** remove this node from the tree, resetting sibling and parent
    pointers as necessary.  This method maintains double-linking */
  public void removeSelf()
  {
    TNode parent = (TNode)up;
    TNode prev = (TNode)left;
    TNode next = (TNode)right;
     
    if(parent != null) { 
      parent.down = next;
      if(next != null) {
        next.up = parent;
        next.left = prev;    // which should be null
      }
    } 
    else {      
     if(prev != null)
      prev.right = next;
     if(next != null)
       next.left = prev;
    }
  }


  /** return the def node for this node */
  public TNode getDefNode() {
      return defNode;
  }
  
  /** set the def node for this node */
  public void setDefNode(TNode n) {
    defNode = n;
  }


  /** return a deep copy of this node, and all sub nodes.
    New tree is doubleLinked, with no parent or siblings.
    Marker value is not copied!
    */
  public TNode deepCopy()
  {
    TNode copy = new TNode();
    copy.ttype = ttype;
    copy.text = text;
    copy.lineNum = lineNum;
    copy.defNode = defNode;
    copy.oldComment = oldComment;

      if(attributes != null)
        copy.attributes = (Hashtable)attributes.clone();
    if(down != null)
        copy.down = ((TNode)down).deepCopyWithRightSiblings();
    copy.doubleLink();
    return copy;
  }


  /** return a deep copy of this node, all sub nodes,
    and right siblings.
    New tree is doubleLinked, with no parent or left siblings.
    defNode is not copied  */
  public TNode deepCopyWithRightSiblings()
  {
    TNode copy = new TNode();
    copy.ttype = ttype;
    copy.text = text;
    copy.lineNum = lineNum;
    copy.defNode = defNode;
      copy.oldComment = oldComment;
    if(attributes != null)
      copy.attributes = (Hashtable)attributes.clone();
    if(down != null)
      copy.down = ((TNode)down).deepCopyWithRightSiblings();
    if(right != null)
      copy.right = ((TNode)right).deepCopyWithRightSiblings();
    copy.doubleLink();
    return copy;
  }


  /** return a short string representation of the node */
  public String toString() {
    StringBuffer str = new StringBuffer( getNameForType(getType()) +
           "[" + getText() + ", " + "]");

     if(this.getLineNum() != 0) 
       str.append(" line:" + (this.getLineNum() ) );

     Enumeration keys = (this.getAttributesTable().keys());
     while (keys.hasMoreElements()) {
       String key = (String) keys.nextElement();
       str.append(" " + key + ":" + (this.getAttribute(key)));
     }

    return str.toString();
  }


  /** print given tree to System.out */
  public static void printTree(AST t)
  {
       if (t == null) return;
       printASTNode(t,0);
       System.out.print("\n");
  }

    public static void printTreeToFile(AST t, String filename) throws FileNotFoundException
    {
       if (t == null) return;
       PrintStream out = new PrintStream(new FileOutputStream(filename));
       printASTNodeToFile(t , 0, out);
       out.print("\n");
       out.flush();
  }




  /** protected method that does the work of printing */
  protected static void printASTNode(AST t, int indent) {
     AST child1, next;
     child1 = t.getFirstChild();         

    System.out.print("\n");
     for(int i = 0; i < indent; i++) 
       System.out.print("   ");

     if(child1 != null) 
        System.out.print("(");

     String s = t.getText();
     if(s != null && s.length() > 0) {
       System.out.print("type " + t.getType() + "  " +getNameForType(t.getType()));
       System.out.print(": \"" + s + "\"");
     }  
     else
        System.out.print("type " + t.getType() + "  " +getNameForType(t.getType()));
       //System.out.print(getNameForType(t.getType()));
     if(((TNode)t).getLineNum() != 0) 
       System.out.print(" line:" + ((TNode)t).getLineNum()  + " chi#" + t.getNumberOfChildren() + " ");

     Enumeration keys = ((TNode)t).getAttributesTable().keys();
     while (keys.hasMoreElements()) {
       String key = (String) keys.nextElement();
       System.out.print(" " + key + ":" + ((TNode)t).getAttribute(key));
     }
     TNode def = ((TNode)t).getDefNode();
     if(def != null)
       System.out.print("[" + getNameForType(def.getType()) + "]");


     if(child1 != null) {
        printASTNode(child1,indent + 1);

        System.out.print("\n");
        for(int i = 0; i < indent; i++) 
           System.out.print("   ");
        System.out.print(")");
     }

     next = t.getNextSibling();
     if(next != null) {
        printASTNode(next,indent);
     }
  }

    private static void printASTNodeToFile(AST t, int indent, PrintStream out)
    {
     AST child1, next;
     child1 = t.getFirstChild();

    out.print("\n");
     for(int i = 0; i < indent; i++)
       out.print("   ");

     if(child1 != null)
        out.print("(");

     String s = t.getText();
     if(s != null && s.length() > 0)
     {
       out.print("type " + t.getType() + "  " +getNameForType(t.getType()));
       out.print(": \"" + s + "\"");
     }
     else
        out.print("type " + t.getType() + "  " +getNameForType(t.getType()));
     if(((TNode)t).getLineNum() != 0)
       out.print(" line:" + ((TNode)t).getLineNum()  + " chi#" + t.getNumberOfChildren() + " ");

     Enumeration keys = ((TNode)t).getAttributesTable().keys();
     while (keys.hasMoreElements()) {
       String key = (String) keys.nextElement();
       out.print(" " + key + ":" + ((TNode)t).getAttribute(key));
     }
     TNode def = ((TNode)t).getDefNode();
     if(def != null)
       out.print("[" + getNameForType(def.getType()) + "]");


     if(child1 != null)
     {
        printASTNodeToFile(child1,indent + 1, out);

        out.print("\n");
        for(int i = 0; i < indent; i++)
           out.print("   ");
        out.print(")");
     }

     next = t.getNextSibling();
     if(next != null)
     {
        printASTNodeToFile(next,indent, out);
     }
  }

  public static String getNameForType(int t)
  {
    try{
      Class c = Class.forName(tokenVocabulary);
      Field[] fields = c.getDeclaredFields();
      if(t-2 < fields.length)
        return fields[t-2].getName();
    } catch (Exception e) { System.out.println(e); }
    return "unfoundtype: " + t;
  }


  /** set up reverse links between this node and its first
     child and its first sibling, and link those as well */
  public void doubleLink()
  {
    TNode right = (TNode)getNextSibling();
    if(right != null) {
      right.left = this;
      right.doubleLink();
    }
    TNode down = (TNode)getFirstChild();
    if(down != null)
    {
      down.up = this;
      down.doubleLink();
    }
  }

  /** find first parent of the given type,
    return null on failure */
  public TNode parentOfType(int type) {
    if(up == null) {
      if(left == null)
        return null;
      else
        return left.parentOfType(type);
    }
    if(up.getType() == type)
      return up;
    return up.parentOfType(type);
  }

  /** find the first child of the node 
    of the given type, return null on failure */
  public TNode firstChildOfType(int type) {
    TNode down = (TNode)getFirstChild();
    if(down == null) 
      return null;
    if(down.getType() == type)
      return down;
    return down.firstSiblingOfType(type);
  }
  
  public int numberOfChildsOfType(int type){
	  int nocot=0;
	  TNode down = (TNode)getFirstChild();
	    if(down == null) 
	      return 0;
	    if(down.getType() == type)
	      nocot++;
	    return down.numberOfSiblingsOfType(type,nocot);
  }
  public int numberOfSiblingsOfType(int type, int nocot) {
	    TNode right = (TNode)getNextSibling();
	    if(right == null) 
	      return nocot;
	    if(right.getType() == type)
	      nocot++;
	    return right.numberOfSiblingsOfType(type, nocot);
  }

  /** find the first sibling of the node 
    of the given type, return null on failure */
  public TNode firstSiblingOfType(int type) {
    TNode right = (TNode)getNextSibling();
    if(right == null) 
      return null;
    if(right.getType() == type)
      return right;
    return right.firstSiblingOfType(type);
  }

    public void setTokenNumber(int i)
    {
        if(this.getAttribute(TNode.TOKEN_NUMBER) != null)
            this.setAttribute(TNode.TOKEN_NUMBER, new Integer(i));
    }

    public int getTokenNumber()
    {
        if(this.getAttribute(TNode.TOKEN_NUMBER) != null)
            return ((Integer)this.getAttribute(TNode.TOKEN_NUMBER)).intValue();
        return -1;
    }

    public boolean isSource()
    {
        if(this.getAttribute(TNode.IS_SOURCE) != null)
            return true;
        return false;
    }

    public String getScope()
    {
        if(this.getAttribute(TNode.SCOPE_NAME) != null)
            return (String)this.getAttribute(TNode.SCOPE_NAME);    
        return "";
    }

    public void printKnoledge()
    {
        printOneNode(this);
        if(parent != null)
        {
            System.out.print("My parent is :");
            printOneNode(parent);
        }
        else if(previousNode != null)
        {
            System.out.print(" I do not know  my parent, but Previous sibling : ");
            printOneNode(previousNode);
        }
        else
            System.out.println(" nothing");
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
}
