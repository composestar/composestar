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

namespace Purple.Tools {
  //=================================================================
  /// <summary>
  /// Properties are used to store configurations which are used by
  /// other classes
  /// PropertySets can contain other PropertySets and arbitrary objects
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  /// </remarks>
  //=================================================================
  public class PropertySet {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    string name;
    IDictionary properties = new Hashtable();
    IDictionary objects = new Hashtable();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// collection of properties    
    /// </summary>
    /// <param name="name">
    /// name of PropertySet leaf in hierarchy (e.g. Resolution) => Root.DirectX.Resolution
    /// </param>
    public PropertySet(string name) {			
      this.name = name;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //--------------------------------------------------------------- 
    /// <summary>
    /// to test if there are any new PropertySet branches or objects
    /// </summary>
    /// <returns>true if there no new branches and objects</returns>
    public bool IsEmpty() {
      return (objects.Count == 0) && (properties.Count == 0);
    }
  
    /// <summary>
    /// test if PropertySet contains a certain entry (object or PropertySet)
    /// </summary>
    /// <param name="key">key name of entry</param>
    /// <returns>true if entry is contained</returns>
    public bool Contains(string key) {
      // use PURL for easier extraction of parts
      PURL url = new PURL(key);
      // test if url is complex
      if (url.IsComplex()) {
        // test first part
        string first = url.GetFirstPart();
        if (!properties.Contains(first))
          return false;
        // if ok test rest of url
        PropertySet child = (PropertySet) properties[first];
        return child.Contains(url.GetWithoutFirstPart());
      }
      // test if key is contained in properties or objects
      return properties.Contains(key) || objects.Contains(key);
    }   

    /// <summary>
    /// remove a certain entry
    /// </summary>
    /// <param name="key">key name of entry</param>
    public void Remove(string key) {
      // use PURL for easier extraction of parts
      PURL url = new PURL(key);
      // test if url is complex
      if (url.IsComplex()) {
        // test first part of url
        string first = url.GetFirstPart();
        if (!properties.Contains(first))
          return;
        // if ok advance to rest of url
        PropertySet child = (PropertySet) properties[first];
        child.Remove(url.GetWithoutFirstPart());
        // if branch is empty => delete 
        if (child.IsEmpty())
          properties.Remove(first);
        return;
      }
      // remove
      if (objects.Contains(key))
        objects.Remove(key);
      if (properties.Contains(key))
        properties.Remove(key);      
    }

    /// <summary>
    /// tests if entry is a new PropertySet object
    /// </summary>
    /// <param name="key">key name of entry</param>
    /// <returns>true if it is a property object</returns>
    public bool IsPropertySet(string key) {
      // use PURL for easier extraction of parts
      PURL url = new PURL(key);
      // test if url is complex
      if (url.IsComplex()) {
        // test first part of url
        string first = url.GetFirstPart();
        if (!properties.Contains(first))
          return false;
        // if ok advance to rest of url
        PropertySet child = (PropertySet) properties[first];
        return child.IsPropertySet(url.GetWithoutFirstPart());
      }
      return properties.Contains(key);      
    }

    /// <summary>
    /// tests if entry is an arbitrary object
    /// </summary>
    /// <param name="key">key name of entry</param>
    /// <returns>true if it is an arbitrary object</returns>
    public bool IsObject(string key) {
      // use PURL for easier extraction of parts
      PURL url = new PURL(key);
      // test if url is complex
      if (url.IsComplex()) {
        // test first part of url
        string first = url.GetFirstPart();
        if (!properties.Contains(first))
          return false;
        // if ok advance to rest of url
        PropertySet child = (PropertySet) properties[first];
        return child.IsObject(url.GetWithoutFirstPart());
      }
      return objects.Contains(key);      
    }

    /// <summary>
    /// get the entry specified by the key
    /// </summary>
    /// <param name="key">key name of entry</param>
    /// <returns>entry specified by key</returns>
    public object Get(string key) {
      // use PURL for easier extraction of parts
      PURL url = new PURL(key);
      // test if url is complex
      if (url.IsComplex()) {
        // test first part of url
        string first = url.GetFirstPart();
        if (!properties.Contains(first))
          return null;
        // if ok advance to rest of url
        PropertySet child = (PropertySet) properties[first];
        return child.Get(url.GetWithoutFirstPart());        
      }
      if (objects.Contains(key))
        return objects[key];
      if (properties.Contains(key))
        return properties[key];
      return null;
    }

    /// <summary>
    /// set the entry specififed by the key
    /// </summary>
    /// <param name="key">key name of entry</param>
    /// <param name="obj">entry to set</param>
    public void Set(string key, object obj) {
      // use PURL for easier extraction of parts
      PURL url = new PURL(key);
      // test if url is complex
      if (url.IsComplex()) {
        // test first part of url
        string first = url.GetFirstPart();
        if (!properties.Contains(first))
          properties.Add(first, new PropertySet(first));
        // if ok advance to rest of url
        PropertySet child = (PropertySet) properties[first];
        child.Set(url.GetWithoutFirstPart(), obj);        
        return;
      }
      if (obj is PropertySet)
        properties[key] = obj;
      else
        objects[key] = obj;        
    }

    /// <summary>
    /// indexer for setting and getting an entry with a certain key
    /// </summary>
    public object this[string key] {
      get {
        return Get(key);        
      }
      set {
        Set(key, value);        
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------    
  }
}
