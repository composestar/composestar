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
using System.Reflection;

namespace Purple.Testing {

  //=================================================================
  /// <summary>
  /// struct filled with "Unit-Test" TestData for every tested method
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================  
  public struct TestData {    
    /// <summary>
    /// name of tested method of TestCase with TestCaseName
    /// </summary>
    public string MethodName;

    /// <summary>
    /// was tested method successful?
    /// </summary>
    public bool   Successful;

    /// <summary>
    /// filled with info why method failed
    /// </summary>
    public string errorMessage;
  }

  /// <summary>
  /// Delegate to inform application if testMethod was successful or not
  /// </summary>  
  public delegate void TestMethodCallback(TestData data);

  //=================================================================
  /// <summary>
  /// class responsible for doing the actual testing by executing
  /// the TestCases and sending info to TestOutput classes
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  /// TODO: Refactoring! Code duplication in PerformanceTester
  //=================================================================
  public class Tester : TesterBase {   
    //---------------------------------------------------------------
    #region Events
    //---------------------------------------------------------------
    /// <summary>
    /// event which is raised in case of every tested Method
    /// </summary>    
    public event TestMethodCallback testMethod;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// standard constructor
    /// </summary>
    public Tester() {      
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------
		/// <summary>
		/// runs one single test
		/// </summary>
		/// <param name="tc">TestCase</param>
		/// <param name="m">Method to test</param>
		protected override void RunTest(TestCaseBase tc, MethodInfo m) {
			TestData td = new TestData();
			td.MethodName = m.Name;        
			td.Successful = true;                    

			try {            
				m.Invoke(tc, new object[] {});
			} catch (Exception e) {
				td.Successful = false;
				td.errorMessage = cleanMessage(e.InnerException.ToString());                     
			}

			// raise event and pass testData
			if (testMethod != null)
				testMethod(td);
		}

    /// <summary>
    /// removes lines like "at Purple.Testing.TestCase.Fail(String message) .."    
    /// from stack trace (cause exception is thrown in Purplesharp framework)
    /// </summary>
    /// <param name="message">stackTrace passed to method</param>
    /// <returns>filtered stack trace</returns>
    protected string cleanMessage(string message) {
      string[] lines = 
        new string[4]{ "   at Purple.Testing.TestCase.Fail(String message)",
                       "   at Purple.Testing.TestCase.Assert(Boolean condition, String message)",
                       "   at Purple.Testing.TestCase.Assert(Boolean condition)",
                       "   at Purple.Testing.TestCase.Fail()"};
      foreach(string l in lines)
        message = Purple.Tools.StringHelper.RemoveLine(message, l);
      return message;  
    }
   
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }
}
