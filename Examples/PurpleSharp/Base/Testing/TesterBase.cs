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
	/// struct filled with TestData for every tested TestCase
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.6</para>  
	/// </remarks>
	//=================================================================  
	public struct TestCaseData {
		/// <summary>
		/// name of TestCase class
		/// </summary>
		public string TestCaseName;

		/// <summary>
		/// number of TestCases to test
		/// </summary>
		public int    TestCaseNum;

		/// <summary>
		/// number of methods to test
		/// </summary>
		public int    MethodNum;
	}
	
	/// <summary>
	/// Delegate to inform apllication if testing new TestCase
	/// </summary>
	public delegate void TestCaseCallback(TestCaseData data);

	//=================================================================
	/// <summary>
	/// abstract base class for executing TestCases
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================
	public abstract class TesterBase {
		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------    
		/// <summary>
		/// TestCases to test
		/// </summary>
		protected ArrayList testsToDo = new ArrayList();

		/// <summary>
		/// get collection of TestCases
		/// </summary>
		public IList TestCases {
			get {
				return testsToDo;
			}
		}
		//---------------------------------------------------------------
    #endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Events
		//---------------------------------------------------------------
		/// <summary>
		/// start of execution of next TestCase
		/// </summary>
		public event TestCaseCallback beginTestCase;

		/// <summary>
		/// finished execution of TestCase
		/// </summary>
		public event TestCaseCallback finishedTestCase;
		//---------------------------------------------------------------
    #endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------

		/// <summary>
		/// add some testCases
		/// </summary>
		/// <param name="testCases">names of testCases</param>
		public void Add(string[] testCases) {
			foreach(string tc in testCases)
				Add(tc);
		}

		/// <summary>
		/// add another testCase class or an .exe or .dll assembly
		/// </summary>
		/// <param name="testCase">name of TestCase class</param>
		/// <exception cref="TestException">if testCase is invalid or assembly can't be loaded</exception>
		public void Add(string testCase) {      
			// test if it's a DLL or an EXE file
			if (testCase.ToLower().IndexOf(".dll") != -1 || testCase.ToLower().IndexOf(".exe") != -1) {
				try {
					Add(Assembly.LoadFrom(testCase));                                    
				} catch {
					throw new TestException("Couldn't load Assembly: " + testCase);
				}
			}
			else {
				// get Type by Name
				Type type = Type.GetType(testCase);
				if (type == null)
					throw new TestException("Couldn't find Type: " + testCase);
				Add(type);
			}
		}

		/// <summary>
		/// adds all testCases from an Assembly
		/// </summary>
		/// <param name="assembly">to load TestCases from</param>    
		public void Add(Assembly assembly) {      
			Type[] types = assembly.GetTypes();
			foreach(Type t in types)
				if (t.BaseType == typeof(Purple.Testing.TestCase))
					Add(t);              
		}

		/// <summary>
		/// adds another testCase class
		/// </summary>
		/// <param name="testCase">type of TestCase</param>
		public void Add(Type testCase) {
			testsToDo.Add(testCase);
		}

		/// <summary>
		/// executes TestCases added before
		/// </summary>
		public void Run() {
			Run((Type[]) testsToDo.ToArray(typeof(Type)));
		}    

		/// <summary>
		/// executes the PerformanceTestCases
		/// </summary>
		/// <param name="testCases">list of performanceTestCases to test</param>
		public void Run(Type[] testCases) {
			
			TestCaseData tcd = new TestCaseData();  			
			tcd.TestCaseNum = testCases.Length;

			// for every test case
			foreach (Type tct in testCases) {   
     
				// get instance of Type
				TestCaseBase tc = (TestCaseBase)Activator.CreateInstance(tct);
				if (tc == null)
					throw new TestException("Couldn't create instance of Type: " + tct.Name);

				tcd.TestCaseName = tct.Name;
				tcd.MethodNum  = tc.GetMethodsToTest().Count;
				if (beginTestCase != null)
					beginTestCase(tcd);
        
				// test every method
				foreach (MethodInfo m in tc.GetMethodsToTest())
					RunTest(tc, m);									

				if (finishedTestCase != null)
					finishedTestCase(tcd);
			}
		}

		/// <summary>
		/// runs one single test
		/// </summary>
		/// <param name="tc">TestCase</param>
		/// <param name="m">Method to test</param>
		protected abstract void RunTest(TestCaseBase tc, MethodInfo m);
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}
