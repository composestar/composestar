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
using System.Collections;

namespace Purple.Scripting {
  //=================================================================
  /// <summary>
  /// The variables collection, that contains all "public" variables created 
  /// during execution of scripts.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>
  /// Public variables are those starting with an uppercase letter.
  /// </remarks>
  //=================================================================
  public class Variables : IEnumerable {
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    IDictionary dictionary = new Hashtable();
    IList variables = new ArrayList();
    string baseName = "";

    /// <summary>
    /// Get variable by name.
    /// </summary>
    public object this[string name] {
      get {
        if (baseName != "")
          return dictionary[baseName + "." + name];
        else
          return dictionary[name];
      }
    }

    /// <summary>
    /// Returns the value for a certain index.
    /// </summary>
    public object this[int index] {
      get {
        return ((DictionaryEntry)variables[index]).Value;
      }
    }

    /// <summary>
    /// Length of the collection.
    /// </summary>
    public int Length {
      get {
        return dictionary.Count;
      }
    }

    /// <summary>
    /// Returns all keys of the collection.
    /// </summary>
    public string[] Keys {
      get {
        ICollection col = dictionary.Keys;
        string[] strings = new string[col.Count];
        col.CopyTo(strings, 0);
        return strings;
      }
    }

    /// <summary>
    /// Returns all values of the collection.
    /// </summary>
    public object[] Values {
      get {
        return (string[])dictionary.Values;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of the <see cref="Variables"/> object.
    /// </summary>
    internal Variables() {
    }

    /// <summary>
    /// Creates a new instance of the <see cref="Variables"/> object.
    /// </summary>
    /// <remarks>
    /// The object uses the passed dictionary, and appends the passed namespaceName to 
    /// all variable queries. That way the variables can be stored once, but reused 
    /// for several namespaces.
    /// </remarks>
    /// <param name="dictionary">The dictionary to use.</param>
    /// <param name="variables">The variables in an ArrayList.</param>
    /// <param name="namespaceName">The name of the base namespace.</param>
    private Variables(IDictionary dictionary, IList variables, string namespaceName) {
      this.dictionary = dictionary;
      this.variables = variables;
      this.baseName = namespaceName;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Returns the DictionaryEntry for a certain index.
    /// </summary>
    /// <param name="index">The index to return variable for.</param>
    /// <returns>The DictionaryEntry for a certain index.</returns>
    public DictionaryEntry GetByIndex(int index) {
      return (DictionaryEntry)variables[index];
    }

    /// <summary>
    /// Adds a certain number of new objects into a given namespace name.
    /// </summary>
    /// <param name="namespaceName">Name of the namespace. (Use "" for the global namespace.</param>
    /// <param name="elements">Collection of objects.</param>
    internal void Add(string namespaceName, IDictionary elements) {
      foreach( DictionaryEntry entry in elements) {
       
        // just export uppercase "public" variables
        string key = (string)entry.Key;
        if (char.IsUpper(key, 0)) {
          if (namespaceName != "")
            key = namespaceName + "." + key;
          dictionary.Add( key, entry.Value);
          variables.Add( new DictionaryEntry( key, entry.Value ) );
        }
      }
    }

    /// <summary>
    /// Return the <see cref="Variables"/> collection for a certain namespace.
    /// </summary>
    /// <param name="namespaceName">Name of the namespace.</param>
    /// <returns></returns>
    public Variables GetNamespace(string namespaceName) {
      return new Variables(dictionary, variables, namespaceName);
    }

    /// <summary>
    /// Returns true if the collection contains a variable with the given name.
    /// </summary>
    /// <param name="name">Name of variable.</param>
    /// <returns>True if the collection contains a variable with the given name.</returns>
    public bool Contains(string name) {
      if (baseName != "")
        return dictionary.Contains(baseName + "." + name);
      return dictionary.Contains(name);
    }

    /// <summary>
    /// Returns the enumerator object.
    /// </summary>
    /// <returns>The enumerator object.</returns>
    public IEnumerator GetEnumerator() {
      return new VariablesEnumerator(this.baseName, variables.GetEnumerator());      
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }

  //=================================================================
  /// <summary>
  /// The special enumerator object for enumerating <see cref="Variables"/>
  ///  objects.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>
  /// </remarks>
  //=================================================================
  internal class VariablesEnumerator : IDictionaryEnumerator {
    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    string baseNamespace;
    IEnumerator enumerator;
    
    /// <summary>
    /// Creates a new enumerator object.
    /// </summary>
    /// <param name="baseNamespace">The base namespace.</param>
    /// <param name="enumerator">The enumerator object to wrap.</param>
    public VariablesEnumerator(string baseNamespace, IEnumerator enumerator) {
      this.baseNamespace = baseNamespace;
      this.enumerator = enumerator;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #region IDictionaryEnumerator Members
    //---------------------------------------------------------------
    /// <summary>
    /// When implemented by a class, gets the key of the current dictionary entry.
    /// </summary>
    public object Key {
      get {
        string str = (string)Entry.Key;
        if (baseNamespace != "")
          str = str.Replace(baseNamespace + ".", "");
        return str;
      }
    }

    /// <summary>
    /// When implemented by a class, gets the value of the current dictionary entry.
    /// </summary>
    public object Value {
      get {
        return Entry.Value;
      }
    }

    /// <summary>
    /// When implemented by a class, gets both the key and the value of the current dictionary entry.
    /// </summary>
    public DictionaryEntry Entry {
      get {
        return (DictionaryEntry)enumerator.Current;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region IEnumerator Members
    //---------------------------------------------------------------
    /// <summary>
    /// Sets the enumerator to its initial position, which is before the first element in the collection.
    /// </summary>
    public void Reset() {
      enumerator.Reset();
    }

    /// <summary>
    /// Gets the current element in the collection.
    /// </summary>
    public object Current {
      get {
        return Entry;  
      }
    }

    /// <summary>
    /// Advances the enumerator to the next element of the collection.
    /// </summary>
    /// <returns>
    /// true if the enumerator was successfully advanced to the next element; 
    /// false if the enumerator has passed the end of the collection.
    /// </returns>
    public bool MoveNext() {
      if (baseNamespace == "")
        return enumerator.MoveNext();
      do {
        if (!enumerator.MoveNext())
          return false;
      } while (((string)Key).IndexOf(baseNamespace) == -1);
      return true;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
