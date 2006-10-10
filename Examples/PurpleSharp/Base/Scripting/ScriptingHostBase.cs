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
using System.IO;
using System.Xml;
using Purple.Serialization;

namespace Purple.Scripting {
  //=================================================================
  /// <summary>
  /// abstract base class to simplify inplementation of concrete
  /// scripting hosts 
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>
  /// </remarks>
  //=================================================================
  public abstract class ScriptingHostBase : IScriptingHost {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------  
    IScriptingEngine scriptingEngine;
    IList references = new ArrayList();
    IList namespaces = new ArrayList();
    string templateSource = "";

    /// <summary>
    /// language 
    /// </summary>
    protected string language;
    /// <summary>
    /// name of script
    /// </summary>
    protected string name;
    string source;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------  

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------  
    /// <summary>
    /// scriptingEngine to use to "interpret" code
    /// </summary>
    public IScriptingEngine ScriptingEngine {
      get {
        return scriptingEngine;
      }
      set {
        scriptingEngine = value;
      }
    }

    /// <summary>
    /// to add assembly references like: System.DLL, ...
    /// </summary>
    public IList References { 
      get {              
        return references;
      }
			set {
				references.Clear();
				foreach (string refer in value)
					references.Add(refer);
			}
    }

    /// <summary>
    /// to add namespaces like: System.IO, Purple.Graphics....
    /// </summary>
    public IList Namespaces { 
      get {
        return namespaces;
      }
			set {
				namespaces.Clear();
				foreach (string ns in value) 
					namespaces.Add( ns );
			}
    }

    /// <summary>
    /// source which is used as template (predefined variables, ...)
    /// </summary>
    public string TemplateSource {
      get {
        return templateSource;
      }
      set {
        templateSource = value;
      }
    }

    /// <summary>
    /// actual source to execute
    /// </summary>
    public string Source {
      get {
        return source;
      }
      set {
        source = value;
      } 
    }
    //---------------------------------------------------------------
    #endregion
    //--------------------------------------------------------------- 
    
    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------  
    /// <summary>
    /// executing script
    /// </summary>    
    /// <exception cref="Purple.Scripting.ScriptingException">if source can't be executed</exception>
    public void Execute() {      
      IScript script = Create();
      script.Execute();
    }


    /// <summary>
    /// processes next element of the xml script file
    /// </summary>
    /// <param name="reader">reader filled with next element</param>
    /// <returns>false if processing failed</returns>
    protected bool ProcessElement(XmlReader reader) {
      switch(reader.Name) {
        case "Script":
          break;
        case "References":  
          references.Clear();
          break;
        case "Reference":
          string reference = reader.GetAttribute("file");          
          references.Add(reference);
          break;
        case "Namespaces":   
          namespaces.Clear();
          break;
        case "Namespace":
          string ns = reader.GetAttribute("name");          
          namespaces.Add(ns);
          break;
        case "Template":
          templateSource = reader.ReadElementString();
          break;
        case "Source":
          source = reader.ReadElementString();          
          break;
      }
      return true;
    }
    
    /// <summary>
    /// loads script from a PurpleSharp script file
    /// </summary>
    /// <param name="stream">name of script file</param>
    /// <returns>true if loading was successful</returns>   
    public bool Load(Stream stream) {
			Scriptlet scriptlet = (Scriptlet)XmlSerializeCodec.Load(stream);
			return LoadScriptlet(scriptlet);      
    }
  
    /// <summary>
    /// saves data into a PurpleSharp script file
    /// </summary>
    /// <param name="stream">to save data to</param>    
    public void Save(Stream stream) {
			Scriptlet scriptlet = CreateScriptlet();
			XmlSerializeCodec.Save(scriptlet, stream );
    }


		/// <summary>
		/// loads a scriptlet
		/// </summary>
		/// <param name="scriptlet">scriptlet to take data from</param>
		/// <returns>true if succeeded</returns>
		public bool LoadScriptlet(Scriptlet scriptlet) {
			if (!language.Equals(scriptlet.Language))
				return false;
			Namespaces = scriptlet.Namespaces;
			References = scriptlet.References;
			Source = scriptlet.Source;
			TemplateSource = scriptlet.TemplateSource;
			return true;
		}

		/// <summary>
		/// creates an scriptlet from the current state of the scripting host
		/// </summary>
		/// <returns>scriptlet</returns>
		public Scriptlet CreateScriptlet() {
			Scriptlet sl = new Scriptlet();
			sl.Language = language;
			sl.Namespaces = (ArrayList)Namespaces;
			sl.References = (ArrayList)References;
			sl.TemplateSource = TemplateSource;
			sl.Source = Source;
			return sl;
		}

    /// <summary>
    /// creates a script
    /// </summary>    
    /// <exception cref="Purple.Scripting.ScriptingException">if IScript object can't be created</exception>
    public abstract IScript Create();

   

    /// <summary>
    /// returns the source combined with the template and references
    /// </summary>    
    /// <returns>combined source</returns>
    public abstract string GetCombinedSource();
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------  
  }
}
