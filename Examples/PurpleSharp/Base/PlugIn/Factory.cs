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
using System.IO;
using System.Collections;

using Purple.IO;
using Purple.Serialization;

namespace Purple.PlugIn {

  //=================================================================
  /// <summary>
  /// PlugIn Factory which loads PlugIns, creates objects and
  /// can be cofigurated over a xml file
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  ///   <para>Update: 0.5</para>
  /// </remarks>
  //=================================================================
  public class Factory : ISerializer, IFileSystemContainer {

    //---------------------------------------------------------------
    #region struct PlugInData
    //---------------------------------------------------------------
    struct PlugInData {
      /// <summary>
      /// name to identify plugIn
      /// </summary>
      public string Name;
      /// <summary>
      /// path to dll containing class
      /// </summary>
      public string DllPath;
      /// <summary>
      /// name of class to create
      /// </summary>
      public string ClassName;
      /// <summary>
      /// The array of arguments for the fileSystem.
      /// </summary>
      public object[] Arguments;
      /// <summary>
      /// create instance of PlugInData
      /// </summary>
      /// <param name="name">name to identify plugIn</param>
      /// <param name="dllPath">path to dll containing class</param>
      /// <param name="className">name of class to create</param>
      /// <param name="arguments">The arguments for creating the plugIn.</param>
      public PlugInData( string name, string dllPath, string className, object[] arguments) {
        Name = name;
        DllPath = dllPath;
        ClassName = className;
        this.Arguments = arguments;
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    Hashtable instances = new Hashtable();
    Hashtable registered = new Hashtable();

    /// <summary>
    /// returns the singleton instance of the factory
    /// </summary>
    static public Factory Instance {
      get {
        return instance;
      }
    }
    static Factory instance = new Factory();

    /// <summary>
    /// The contained <see cref="FileSystem"/>.
    /// </summary>
    public IFileSystem FileSystem { 
      get {
        return fileSystem;
      }
      set {
        fileSystem = value;
      }
    }
    IFileSystem fileSystem = RefFileSystem.Instance;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    private Factory() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// registeres a plugIn
    /// </summary>
    /// <param name="name">name to identify plugIn</param>
    /// <param name="dllPath">path of dll</param>
    /// <param name="className">name of class to create</param>
    public void Register( string name, string dllPath, string className) {
      Register(name, dllPath, className, null);
    }

    /// <summary>
    /// registeres a plugIn
    /// </summary>
    /// <param name="name">name to identify plugIn</param>
    /// <param name="dllPath">path of dll</param>
    /// <param name="className">name of class to create</param>
    /// <param name="arguments">Arguments to use for creating the plugIn instance.</param>
    public void Register( string name, string dllPath, string className, params object[] arguments) {
      if (!IsRegistered(name)) {
        registered.Add( name, new PlugInData( name, dllPath, className, arguments ) );
      }
    }

    /// <summary>
    /// test if a certain plugIn is registered
    /// </summary>
    /// <param name="name">name to identify plugIn</param>
    /// <returns>true if given name is already registered</returns>
    public bool IsRegistered( string name ) {
      return registered.Contains( name );
    }

    /// <summary>
    /// unregister a certain plugIn
    /// </summary>
    /// <param name="name">name to identify plugIn</param>
    public void Unregister( string name ) {
      registered.Remove( name );
    }

    /// <summary>
    /// configurates the Factory by the passed xml file
    /// </summary>
    /// <param name="fileName">filename of xml file</param>
    public void Load(string fileName) {
      using (Stream stream = fileSystem.Open(fileName)) {
        XmlSerializeCodec.Load(stream);
      }
    }

    /// <summary>
    /// save current configuration of Factory
    /// </summary>
    /// <param name="fileName"></param>
    public void Save(string fileName) {
      // Todo: fileSystem
      XmlSerializeCodec.Save(this, fileName);
    }

    /// <summary>
    /// returns the instance of the object given by a certain name
    /// </summary>
    /// <param name="name">string to identify object</param>
    /// <returns></returns>
    public object Get(string name) {
      if (!instances.Contains(name)) {
        if (IsRegistered(name)) {
          return Create(name);
        }
      }
      return null;
    }

    /// <summary>
    /// creates a certain instance
    /// </summary>
    /// <param name="name"></param>
    /// <returns></returns>
    public object Create(string name) {
      if (!IsRegistered(name))
        return false;
      PlugInData data = (PlugInData)registered[name];
      PlugIn plugIn = new PlugIn( data.DllPath );
      object instance = plugIn.GetInstanceOf( data.ClassName, data.Arguments );
      if (instance != null && !instances.Contains( name ))
        instances.Add(name, instance);
      return instance;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region FactorySerializer
    //---------------------------------------------------------------
    /// <summary>
    /// Serializes the given object.
    /// </summary>
    /// <param name="obj">The object to serialize.</param>
    /// <param name="stream">Stream to fill with data.</param>
    public void Serialize(object obj, SerializeStream stream) {
      ICollection collection = registered.Values;
      stream.Write("registered", collection.Count);
      foreach(PlugInData data in collection) {
        stream.EnterObject("PlugIn");
        stream.Write("Name", data.Name);
        stream.Write("DllPath", data.DllPath);
        stream.Write("ClassName", data.ClassName);
        stream.LeaveObject();
      }
    }
        
    /// <summary>
    /// Deserializes the given object.
    /// </summary>
    /// <param name="stream">Stream containing data.</param>
    /// <param name="type">The type of the object to create.</param>
    /// <returns>The deserialized object.</returns>
    public object Deserialize(Type type, SerializeStream stream) {
      int length = stream.ReadInt("registered");
      for (int i=0; i<length; i++) {
        stream.EnterObject(null);
        Register( stream.ReadString("Name"), stream.ReadString("DllPath"), stream.ReadString("ClassName"));
        stream.LeaveObject();
      }
      return this;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
