//*****************************************************************************
//     ____                              ___                __ __      
//    /\  _`\                           /\_ \              _\ \\ \__   
//    \ \ \L\ \ __  __   _ __   _____   \//\ \       __   /\__  _  _\  
//     \ \ ,__//\ \/\ \ /\`'__\/\ '__`\   \ \ \    /'__`\ \/_L\ \\ \L_ 
//      \ \ \/ \ \ \_\ \\ \ \/ \ \ \L\ \   \_\ \_ /\  __/   /\_   _  _\
//       \ \_\  \ \____/ \ \_\  \ \ ,__/   /\____\\ \____\  \/_/\_\\_\/
//        \/_/   \/___/   \/_/   \ \ \/    \/____/ \/____/     \/_//_/ 
//                                \ \_\                                
//                                 \/_/                                            
//                  Purple# - The smart way of programming games
#region //
// Copyright (c) 2002-2003 by 
//   Markus Wöß
//   Bunnz@Bunnz.com
//   http://www.bunnz.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
#endregion
//*****************************************************************************
using System;
using System.Diagnostics;
using System.Windows.Forms;
using System.Text;

namespace Purple.Logging {
  //=================================================================
  /// <summary>
  /// a listener logging into a treeView 
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  /// </remarks>
  //=================================================================
  public class TreeViewListener : ILogListener {
    private delegate void AddNodeDelegate(TreeNodeCollection node, TreeNode newNode);
    private delegate void UpdateDelegate();
    private delegate void RemoveDelegate(TreeNode node);
    private delegate void AddMessageDelegate(TreeNode node, string message);

    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    private TreeView treeView;
    private TextBoxBase textBox;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// minimum severity of the log message to get logged
    /// </summary>
    public LogLevel Level { 
      get {
        return logLevel;
      }
      set {
        logLevel = value;
      }
    }
    private LogLevel logLevel = LogLevel.Spam;

    /// <summary>
    /// flag which indicates if time is added to logEntries
    /// </summary>
    public bool LogTime {
      get {
        return logTime;
      }
      set {
        logTime = value;
      }
    }
    bool logTime = true;

    /// <summary>
    /// flat indicating if the current assembly should be logged
    /// </summary>
    public bool LogAssembly {
      get {
        return logAssembly;
      }
      set {
        logAssembly = value;
      }
    }
    bool logAssembly = false;

    /// <summary>
    /// indicates if there should be a node for Warning, Info, Errors, Spam
    /// </summary>
    public bool LogLevelNode {
      get {
        return logLevelNode;
      }
      set {
        logLevelNode = value;
      }
    }
    bool logLevelNode = false;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates an instance of a tree view listener
    /// </summary>
    public TreeViewListener(TreeView view, TextBoxBase textBox) {
      this.treeView = view;
      this.textBox = textBox;

      this.treeView.AfterSelect += new System.Windows.Forms.TreeViewEventHandler(this.treeView1_AfterSelect);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    private StringBuilder CreateLine(LogLevel level, string message, string category) {
      StringBuilder builder = new StringBuilder(128);
      if (!logLevelNode)
        builder.AppendFormat( "{0,7}", '[' + level.ToString() + ']' );
			
      if (logTime)
        builder.AppendFormat( "{0,10}", System.DateTime.Now.ToLongTimeString());

      if (logAssembly)
        builder.AppendFormat( "{0,12}", System.AppDomain.CurrentDomain.FriendlyName);

      builder.AppendFormat( "{0, -50}", message );
      return builder;
    }

    private void AddMessage(TreeNode node, string message) {
      string text = "";
      if (node.Tag != null)
        text = (string)node.Tag;
      text += message;
      node.Tag = text;
    }

    private TreeNode GetNodeForCategory(string partialPath, TreeNodeCollection nodes) {
      for (int i=0; i<nodes.Count; i++) {
        TreeNode node = nodes[i];
        if (node.Text == partialPath)
          return node;
      }
      TreeNode newNode = CreateCategoryNode( partialPath );

      // manipulation of treeView data can't be done in the same thread => Invoke
      // invoke can't be called when handle ist created ;-/
      if (treeView.IsHandleCreated)
        treeView.Invoke( new AddNodeDelegate(AddNode), new object[]{nodes, newNode});
      else
        AddNode(nodes, newNode);
      return newNode;
    }

    private void AddNode(TreeNodeCollection nodes, TreeNode newNode) {
      nodes.Add(newNode);
    }

    private TreeNode GetNodeForCategory(string category) {	
      TreeNode currentNode = null;
      TreeNodeCollection nodes = treeView.Nodes;
      string partialPath = category;
      int startIndex = 0;
      do {
        int end = category.IndexOf('.', startIndex);
        if (end != -1)
          partialPath = category.Substring(startIndex, category.IndexOf('.', startIndex)-startIndex);
        else
          partialPath = category.Substring(startIndex);

        currentNode = GetNodeForCategory(partialPath, nodes);
        nodes = currentNode.Nodes;

        startIndex = end + 1;
      } while (startIndex != 0);	
      return currentNode;
    }

    private TreeNode CreateMessageNode(string message) {
      return new TreeNode(message);
    }

    private TreeNode CreateCategoryNode(string category) {
      return new TreeNode(category);
    }

    /// <summary>
    /// write a simple message for a given category to the treeview
    /// </summary>
    /// <param name="level">log level of message</param>
    /// <param name="message">message to write</param>
    /// <param name="category">category to use</param>
    public void Log(LogLevel level, string message, string category) {
      TreeNode node = GetNodeForCategory(category);
      string str = CreateLine(level, message, category).ToString() + System.Environment.NewLine;

      // manipulation of treeView data can't be done in the same thread => Invoke
      // invoke can't be called when handle ist created ;-/
      if (treeView.IsHandleCreated)
        treeView.Invoke( new AddMessageDelegate(AddMessage), new object[]{node, str});
      else
        AddMessage(node, str);

      // manipulation of treeView data can't be done in the same thread => Invoke
      // invoke can't be called when handle ist created ;-/
      if (treeView.IsHandleCreated)
        treeView.Invoke( new UpdateDelegate(Update));
      else
        Update();
    }

    /// <summary>
    /// update treeView
    /// </summary>
    private void Update() {
      treeView.Update();
      if (treeView.SelectedNode != null) {
        string text = (string)treeView.SelectedNode.Tag;
        textBox.Text = text;
      }
    }

    private void treeView1_AfterSelect(object sender, System.Windows.Forms.TreeViewEventArgs e) {
      string text = (string)e.Node.Tag;
      textBox.Text = text;
    }

    /// <summary>
    /// removes a certain category
    /// </summary>
    /// <param name="category">category to remove</param>
    public void Remove(string category) {
      TreeNode node = this.GetNodeForCategory(category);

      // manipulation of treeView data can't be done in the same thread => Invoke
      // invoke can't be called when handle ist created ;-/
      if (treeView.IsHandleCreated)
        treeView.Invoke( new RemoveDelegate(RemoveNode), new object[]{node});
      else
        RemoveNode(node);
    }

    private void RemoveNode(TreeNode node) {
      node.Remove();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
