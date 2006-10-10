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
using System.Reflection;
using System.Collections;

namespace Purple.Scripting.Resource
{
  //=================================================================
  /// <summary>
  /// a scope object for resource scripts
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  /// </remarks>
  //=================================================================
  internal class ResourceScope {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the <see cref="ParserValue"/> objects in form of an <see cref="IDictionary"/>.
    /// </summary>
    public IDictionary ParserValues {
      get {
        return variables;
      }
    }
    SortedList variables = new SortedList();

    /// <summary>
    /// Returns the variables in form of an <see cref="IDictionary"/>.
    /// </summary>
    public IDictionary Variables {
      get {
        SortedList list = new SortedList();
        foreach( DictionaryEntry entry in ParserValues)
          list.Add(entry.Key, (entry.Value as ParserValue).Value);
        return list;
      }
    }

    /// <summary>
    /// The object that is linked with the curren scope.
    /// </summary>
    public ParserValue LinkedObject {
      get {
        return linkedObject;
      }
      set {
        linkedObject = value;
        Set("", linkedObject );
        Set("this", linkedObject );
      }
    }
    ParserValue linkedObject = ParserValue.Empty;

    /// <summary>
    /// returns the parent scope
    /// </summary>
    public ResourceScope Parent {
      get {
        return parent;
      }
    }
    ResourceScope parent = null;

    /// <summary>
    /// Returns the whole name of the scope (e.g. Zombie.Gfx.Gui).
    /// </summary>
    public string Name {
      get {
        ResourceScope current = this;
        string retName = "";
        if (current.name == "")
          retName = LinkedObject.Type.Name;
        else
          retName = current.name;

        while (current.Parent != null) {
          current = current.Parent;
          if (current.name != "")
            retName = retName.Insert(0, current.name + ".");
        }

        return retName;
      }
    }
    string name = "";
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// creates a new scope object
    /// </summary>
    public ResourceScope(string name) {
      this.name = name;
    }

    /// <summary>
    /// Creates a new child scope object.
    /// </summary>
    /// <param name="parent">The parent scope.</param>
    /// <param name="name">The namespace name.</param>
    public ResourceScope(ResourceScope parent, string name){
      this.name = name;
      this.parent = parent;
    }

    /// <summary>
    /// creates a new scope object
    /// </summary>
    /// <param name="parent">parent scope</param>
    /// <param name="linkedObject">the object that is linked to the scope (may be null)</param>
    public ResourceScope(ResourceScope parent, ParserValue linkedObject) {
      this.parent = parent;
      this.LinkedObject = linkedObject;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// returns the object for a given name or path
    /// </summary>
    /// <param name="name">name or path for object</param>
    /// <returns>the object for a given name or path</returns>
    public ParserValue Get(string name) {
      ParserValue obj;
      if( !GetVariable( name, out obj) ) {
        ResourceScope current = this;
        while (current.parent != null && !current.parent.GetVariable( name, out obj))
          current = current.parent;
      }
      return obj;
    }

    /// <summary>
    /// sets a certain value
    /// </summary>
    /// <param name="name">name of variable</param>
    /// <param name="value">value to set</param>
    /// <returns>true if successful</returns>
    public bool Set(string name, ParserValue value) {

      if (linkedObject.IsNull) {
        variables[name] = value;
      }
      else {    
        if (!ResourceSemantics.SetField(linkedObject, name, value))
          variables[name] = value;
      }
      return true;
    }

    /// <summary>
    /// returns the variable for a certain name
    /// </summary>
    /// <param name="name">name of variables</param>
    /// <param name="variable">the returned variable</param>
    /// <returns>true if variable was found</returns>
    private bool GetVariable(string name, out ParserValue variable) {
      variable = ParserValue.Empty;
      if (linkedObject.IsNotNull) {
        if (ResourceSemantics.GetField(linkedObject, name, out variable))
          return true;
      }
      if (variables.ContainsKey(name)) {
        variable = (ParserValue)variables[name]; 
        return true;
      }
      return false;
    }

    private bool ContainsVariable(string name) {
      if (linkedObject.IsNotNull) {
        if (ResourceSemantics.ContainsField(linkedObject, name))
          return true;
      }
      return variables.ContainsKey(name);
    }

    /// <summary>
    /// test if a certain variables is within the scope
    /// </summary>
    /// <param name="name">name of variable</param>
    /// <returns>true if variable is contained by scope</returns>
    public bool Contains(string name) {
      ResourceScope current = this;
      while (current != null) {
        if (current.ContainsVariable(name))
          return true;
        current = current.Parent;
      }
      return false;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
