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
using Purple.Tools;

namespace Purple.Testing {

	//=================================================================
	/// <summary>
	/// struct filled with PerformanceTestData for every tested method
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	//=================================================================  
	public struct PerformanceTestData {
		/// <summary>
		/// standard constructor
		/// </summary>
		/// <param name="name">sets name of testCase</param>
		public PerformanceTestData(string name) {
			TestName = name;
			SingleTime = 0;
			OverallTime = 0;
			TestNum = 0;
		}

		/// <summary>name of test (methodName)</summary>
		public string TestName;
		/// <summary>first test (has to be jitted most time)</summary>
		[System.CLSCompliant(false)]
		public uint SingleTime;
		/// <summary>overall time for testing testNum times (without singleTime)</summary>
		[System.CLSCompliant(false)]
		public uint OverallTime;
		/// <summary>number of iterations</summary>
		[System.CLSCompliant(false)]
		public uint TestNum;		
	}

	/// <summary>
	/// Delegate to inform application about performanceTest results
	/// </summary>  
	public delegate void PerformanceTestMethodCallback(PerformanceTestData data);

	//=================================================================
	/// <summary>
	/// class responsible for doing the actual performance testing by executing
	/// the PerformanceTestCases and sending info to TestOutput classes
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	/// </remarks>
	/// TODO: Refactoring! Code duplication in Tester
	//=================================================================
	public class PerformanceTester : TesterBase {

		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------
		int iterations = 1000;

		/// <summary>
		/// number of iterations for every testMethod
		/// </summary>
		public int Iterations {
			get {
				return iterations;
			}
			set {
				iterations = value;
			}			
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Events
		//---------------------------------------------------------------
		/// <summary>
		/// event which is raised in case of every tested Method
		/// </summary>    
		public event PerformanceTestMethodCallback testMethod;
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------
		/// <summary>
		/// standard constructor
		/// </summary>
		public PerformanceTester() 
		{			
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------   
		/// <summary>
		/// runs one single test		
		/// RunTest is not suited for very short methods, cause the overhead of invoking
		/// a method over the reflection mechanisme is too high
		/// </summary>
		/// <param name="tc">TestCase</param>
		/// <param name="m">Method to test</param>
		protected override void RunTest(TestCaseBase tc, MethodInfo m) {

			PerformanceTestCase testCase = (PerformanceTestCase) tc;
			Counter counter = Counter.Instance;
			PerformanceTestData td = new PerformanceTestData();  
			td.TestName = m.Name;  

			// single invoke
			ulong startCount = counter.Count;					
			m.Invoke(testCase, null);
			ulong endCount = counter.Count;
			td.SingleTime = (uint) Counter.GetElapsed(startCount, endCount);

			// multiple invoke
			startCount = counter.Count;
			for (uint i=0; i<iterations; i++)					
				m.Invoke(testCase, null);					
			endCount = counter.Count;
			td.OverallTime = (uint) Counter.GetElapsed(startCount, endCount);
			td.TestNum = (uint)iterations;

			// raise event and pass testData
			if (testMethod != null)
				testMethod(td);
		}

		/// <summary>
		/// runs one single test without using the reflection mechanisme
		/// The overhead is very low and therefore better suited for small methods	
		/// However this method doesn't reports results to connected objects like PerformanceTester			
		/// </summary>
		/// <param name="testMethod">method (delegate) to test</param>
		/// <returns>test results</returns>
		public PerformanceTestData RunTest(TestCallback testMethod) {
			
			Counter counter = Counter.Instance;
			PerformanceTestData td = new PerformanceTestData();  
			td.TestName = testMethod.Method.Name;  

			// single invoke
			ulong startCount = counter.Count;					
			testMethod();
			ulong endCount = counter.Count;
			td.SingleTime = (uint) Counter.GetElapsed(startCount, endCount);

			// multiple invoke
			startCount = counter.Count;
			for (uint i=0; i<iterations; i++)					
				testMethod();
			endCount = counter.Count;
			td.OverallTime = (uint) Counter.GetElapsed(startCount, endCount);
			td.TestNum = (uint)iterations;

			return td;
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}
