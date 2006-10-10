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
using System.Text;
using System.Collections;

using Purple.IO;

namespace Purple.Graphics.Effect
{
  //=================================================================
  /// <summary>
  /// The preprocessor for effect files.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================
	public class PreProcessor
	{
    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    SortedList included = new SortedList();

    /// <summary>
    /// The fileSystem to use for the preprocessor.
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

    /// <summary>
    /// The fileSystem that is used for system includes.
    /// </summary>
    public IFileSystem SysFileSystem {
      get {
        return sysFileSystem;
      }
      set {
        sysFileSystem = value;
      }
    }
    IFileSystem sysFileSystem = new Purple.IO.Folder( Purple.IO.ResourceFileSystem.Instance, "Purple/Graphics/Effect/Library");
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new preprocessor object.
    /// </summary>
		public PreProcessor()
		{
		}
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Processes a certain file.
    /// </summary>
    /// <param name="fileName">Name of file to process.</param>
    /// <returns>The output stream.</returns>
    public Stream Process(string fileName) {
      using (Stream stream = fileSystem.Open(fileName)) {
        return Process(stream, fileSystem.GetFullPath(fileName));
      }
    }

    /// <summary>
    /// Processes a certain stream.
    /// </summary>
    /// <param name="input">The stream containing the input data.</param>
    /// <param name="fileName">The fileName of the stream or null - for debugging purposes.</param>
    /// <returns>The output stream.</returns>
    private Stream Process(Stream input, string fileName) {
      // reset stuff
      included.Clear();
      // read original code
      StreamReader reader = new StreamReader(input);
      StringBuilder builder = new StringBuilder();
      ProcessFile( reader, builder, fileName );
      reader.Close();
      string code = builder.ToString();
      return Purple.Tools.StringHelper.ToStream(code);      
    }

    private void ProcessFile( StreamReader reader, StringBuilder builder, string fileName ) {
      AddLine(builder, 0, fileName);
      int lineCount = 0;
      while (reader.Peek() != -1) {
        lineCount++;
        string line = reader.ReadLine();
        int preStart = line.IndexOf('#');
        if (preStart != -1) {
          if (line.IndexOf("#include") != -1) {
            Include(builder, line, lineCount, fileName);
          } else if (line.IndexOf("#define ") != -1) {
            Define(builder, line, lineCount, fileName);
          } else if (line.IndexOf("#undef ") != -1) {
            Purple.Log.Spam("Not supported so far!");
            Keep(builder, line);
          } else if (line.IndexOf("#if ") != -1) {
            Purple.Log.Spam("Not supported so far!");
            Keep(builder, line);
          } else if (line.IndexOf("#else ") != -1) {
            Purple.Log.Spam("Not supported so far!");
            Keep(builder, line);
          } else if (line.IndexOf("#elif ") != -1) {
            Purple.Log.Spam("Not supported so far!");
            Keep(builder, line);
          } else if (line.IndexOf("#endif ") != -1) {
            Purple.Log.Spam("Not supported so far!");
            Keep(builder, line);
          } else if (line.IndexOf("#ifdef ") != -1) {
            Purple.Log.Spam("Not supported so far!");
            Keep(builder, line);
          } else if (line.IndexOf("#ifndef ") != -1) {
            Purple.Log.Spam("Not supported so far!");
            Keep(builder, line);
          }
        } else {
          Keep(builder, line);
        }        
      }
    }

    private void Define(StringBuilder builder, string line, int lineCount, string fileName) {
      //builder.Append("// ");
      Keep(builder, line);
    }

    private void Include(StringBuilder builder, string line, int lineCount, string fileName) {
      bool sysInclude = false;
      // get the file name
      int indexStart = line.IndexOf('"');
      int indexEnd = 0;
      if (indexStart != -1)
        indexEnd = line.IndexOf('"', indexStart+1);
      else {
        indexStart = line.IndexOf('<');
        indexEnd = line.IndexOf('>', indexStart+1);
        sysInclude = true;
      }
      string fileName2 = line.Substring(indexStart+1, indexEnd-indexStart-1);
      string fullPath2;   

      // try sys include
      IFileSystem fs;
      string fp;
      if (sysInclude && sysFileSystem.Exists(fileName2)) {
        fullPath2 = sysFileSystem.GetFullPath(fileName2);
        fs = sysFileSystem;
        fp = fileName2;
      }
      else {
        fullPath2 = Purple.IO.Path.Merge(Purple.IO.Path.GetFolder(fileName), fileName2); 
        fs = fileSystem;
        fp = fullPath2;
      }

      if (!included.Contains(fullPath2)) {
        included.Add( fullPath2, fullPath2 );
        Stream stream = fs.Open( fp );
        StreamReader reader2 = new StreamReader( stream );
        ProcessFile(reader2, builder, fullPath2);
        AddLine(builder, lineCount, fileName);
        reader2.Close();
      }
    }

    private void Keep(StringBuilder builder, string line) {
      builder.Append(line);
      builder.Append(Environment.NewLine);
    }

    private void AddLine(StringBuilder builder, int lineCount, string fileName) {
      builder.Append("#line ");
      builder.Append(lineCount+1);
      builder.Append(' ');
      builder.Append('"');
      builder.Append(fileName);
      builder.Append('"');
      builder.Append(System.Environment.NewLine);
    }

    /// <summary>
    /// Processes a certain stream.
    /// </summary>
    /// <param name="input">The stream containing the input data.</param>
    /// <returns>The output stream.</returns>
    public Stream Process(Stream input) {
      // get name of file
      string fileName = null;
      if (input is FileStream)
        fileName = (input as FileStream).Name;
      return Process(input, fileName);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
