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
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;

namespace Purple.Testing
{
	/// <summary>
	/// Summary description for TestForm.
	/// </summary>
  public class TestForm : System.Windows.Forms.Form {
    //---------------------------------------------------------------
    #region Logic Variables
    //---------------------------------------------------------------        
    private Tester tester = new Tester();
    private int currentTestCase = 0;
    private int currentTestMethod = 0;
    private int succeededNum = 0;
    private int failedNum = 0;    
    private int totalTestCases = 0;
    private int totalTestMethods = 0;
    DateTime startTime;
    int time = 0;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
        
    //---------------------------------------------------------------
    #region Form Variables
    //---------------------------------------------------------------        
    private System.Windows.Forms.Label label1;
    private System.Windows.Forms.Button button1;
    private System.Windows.Forms.Button button2;
    private System.Windows.Forms.Label label2;
    private System.Windows.Forms.Label label3;
    private System.Windows.Forms.Label label4;
    private System.Windows.Forms.Label label5;
    private System.Windows.Forms.Label label6;
    private System.Windows.Forms.TextBox txtOutput;
    private System.Windows.Forms.TextBox txtTime;
    private System.Windows.Forms.TextBox txtFailed;
    private System.Windows.Forms.TextBox txtSucceeded;
    private System.Windows.Forms.ProgressBar pbTestCases;
    private System.Windows.Forms.ProgressBar pbTestMethods;
    private System.Windows.Forms.TextBox txtTestCases;
    private System.Windows.Forms.TextBox txtTestMethods;
    private System.ComponentModel.Container components = null;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------        
    
    //---------------------------------------------------------------
    #region Construction
    //---------------------------------------------------------------        
    /// <summary>
    ///  standard constructor
    /// </summary>
    public TestForm() {
      //
      // Required for Windows Form Designer support
      //
      InitializeComponent();

      // add events      
      tester.beginTestCase += new TestCaseCallback( beginTestCase );
      tester.finishedTestCase += new TestCaseCallback( endTestCase );
      tester.testMethod += new TestMethodCallback( testMethod );     
    }

    /// <summary>
    /// Clean up any resources being used.
    /// </summary>
    protected override void Dispose( bool disposing ) {
      if( disposing ) {
        if(components != null) {
          components.Dispose();
        }
      }
      base.Dispose( disposing );
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------        

		#region Windows Form Designer generated code
    /// <summary>
    /// Required method for Designer support - do not modify
    /// the contents of this method with the code editor.
    /// </summary>
    private void InitializeComponent() {
      this.label1 = new System.Windows.Forms.Label();
      this.txtOutput = new System.Windows.Forms.TextBox();
      this.button1 = new System.Windows.Forms.Button();
      this.button2 = new System.Windows.Forms.Button();
      this.label2 = new System.Windows.Forms.Label();
      this.pbTestCases = new System.Windows.Forms.ProgressBar();
      this.label3 = new System.Windows.Forms.Label();
      this.pbTestMethods = new System.Windows.Forms.ProgressBar();
      this.label4 = new System.Windows.Forms.Label();
      this.txtTime = new System.Windows.Forms.TextBox();
      this.label5 = new System.Windows.Forms.Label();
      this.txtFailed = new System.Windows.Forms.TextBox();
      this.txtTestCases = new System.Windows.Forms.TextBox();
      this.txtTestMethods = new System.Windows.Forms.TextBox();
      this.txtSucceeded = new System.Windows.Forms.TextBox();
      this.label6 = new System.Windows.Forms.Label();
      this.SuspendLayout();
      // 
      // label1
      // 
      this.label1.Anchor = ((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right);
      this.label1.Font = new System.Drawing.Font("Arial Black", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.label1.Location = new System.Drawing.Point(16, 16);
      this.label1.Name = "label1";
      this.label1.Size = new System.Drawing.Size(552, 24);
      this.label1.TabIndex = 0;
      this.label1.Text = "PurpleSharp Unit Testing";
      // 
      // txtOutput
      // 
      this.txtOutput.Anchor = (((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
        | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right);
      this.txtOutput.Location = new System.Drawing.Point(8, 56);
      this.txtOutput.Multiline = true;
      this.txtOutput.Name = "txtOutput";
      this.txtOutput.ScrollBars = System.Windows.Forms.ScrollBars.Both;
      this.txtOutput.Size = new System.Drawing.Size(456, 280);
      this.txtOutput.TabIndex = 1;
      this.txtOutput.Text = "";
      this.txtOutput.WordWrap = false;
      // 
      // button1
      // 
      this.button1.Anchor = (System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right);
      this.button1.Location = new System.Drawing.Point(488, 56);
      this.button1.Name = "button1";
      this.button1.TabIndex = 2;
      this.button1.Text = "Run";
      this.button1.Click += new System.EventHandler(this.button1_Click);
      // 
      // button2
      // 
      this.button2.Anchor = (System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right);
      this.button2.Location = new System.Drawing.Point(488, 312);
      this.button2.Name = "button2";
      this.button2.TabIndex = 3;
      this.button2.Text = "Quit";
      this.button2.Click += new System.EventHandler(this.button2_Click);
      // 
      // label2
      // 
      this.label2.Anchor = (System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left);
      this.label2.Location = new System.Drawing.Point(16, 352);
      this.label2.Name = "label2";
      this.label2.Size = new System.Drawing.Size(96, 16);
      this.label2.TabIndex = 4;
      this.label2.Text = "Overal Progress:";
      // 
      // pbTestCases
      // 
      this.pbTestCases.Anchor = ((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right);
      this.pbTestCases.Location = new System.Drawing.Point(16, 368);
      this.pbTestCases.Name = "pbTestCases";
      this.pbTestCases.Size = new System.Drawing.Size(456, 23);
      this.pbTestCases.TabIndex = 5;
      // 
      // label3
      // 
      this.label3.Anchor = (System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left);
      this.label3.Location = new System.Drawing.Point(16, 400);
      this.label3.Name = "label3";
      this.label3.Size = new System.Drawing.Size(112, 16);
      this.label3.TabIndex = 6;
      this.label3.Text = "TestCase Progress:";
      // 
      // pbTestMethods
      // 
      this.pbTestMethods.Anchor = ((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right);
      this.pbTestMethods.Location = new System.Drawing.Point(16, 416);
      this.pbTestMethods.Name = "pbTestMethods";
      this.pbTestMethods.Size = new System.Drawing.Size(456, 24);
      this.pbTestMethods.TabIndex = 7;
      // 
      // label4
      // 
      this.label4.Anchor = System.Windows.Forms.AnchorStyles.Right;
      this.label4.Location = new System.Drawing.Point(488, 220);
      this.label4.Name = "label4";
      this.label4.Size = new System.Drawing.Size(64, 16);
      this.label4.TabIndex = 8;
      this.label4.Text = "Time:";
      // 
      // txtTime
      // 
      this.txtTime.Anchor = System.Windows.Forms.AnchorStyles.Right;
      this.txtTime.Font = new System.Drawing.Font("Microsoft Sans Serif", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.txtTime.Location = new System.Drawing.Point(488, 236);
      this.txtTime.Multiline = true;
      this.txtTime.Name = "txtTime";
      this.txtTime.ReadOnly = true;
      this.txtTime.Size = new System.Drawing.Size(80, 24);
      this.txtTime.TabIndex = 9;
      this.txtTime.Text = "";
      this.txtTime.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
      // 
      // label5
      // 
      this.label5.Anchor = System.Windows.Forms.AnchorStyles.Right;
      this.label5.Location = new System.Drawing.Point(488, 172);
      this.label5.Name = "label5";
      this.label5.Size = new System.Drawing.Size(64, 16);
      this.label5.TabIndex = 10;
      this.label5.Text = "Failed:";
      // 
      // txtFailed
      // 
      this.txtFailed.Anchor = System.Windows.Forms.AnchorStyles.Right;
      this.txtFailed.Font = new System.Drawing.Font("Microsoft Sans Serif", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.txtFailed.Location = new System.Drawing.Point(488, 188);
      this.txtFailed.Multiline = true;
      this.txtFailed.Name = "txtFailed";
      this.txtFailed.ReadOnly = true;
      this.txtFailed.Size = new System.Drawing.Size(80, 24);
      this.txtFailed.TabIndex = 11;
      this.txtFailed.Text = "";
      this.txtFailed.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
      // 
      // txtTestCases
      // 
      this.txtTestCases.Anchor = (System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right);
      this.txtTestCases.Font = new System.Drawing.Font("Microsoft Sans Serif", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.txtTestCases.Location = new System.Drawing.Point(488, 368);
      this.txtTestCases.Multiline = true;
      this.txtTestCases.Name = "txtTestCases";
      this.txtTestCases.ReadOnly = true;
      this.txtTestCases.Size = new System.Drawing.Size(80, 24);
      this.txtTestCases.TabIndex = 12;
      this.txtTestCases.Text = "";
      this.txtTestCases.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
      // 
      // txtTestMethods
      // 
      this.txtTestMethods.Anchor = (System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right);
      this.txtTestMethods.Font = new System.Drawing.Font("Microsoft Sans Serif", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.txtTestMethods.Location = new System.Drawing.Point(488, 416);
      this.txtTestMethods.Multiline = true;
      this.txtTestMethods.Name = "txtTestMethods";
      this.txtTestMethods.ReadOnly = true;
      this.txtTestMethods.Size = new System.Drawing.Size(80, 24);
      this.txtTestMethods.TabIndex = 13;
      this.txtTestMethods.Text = "";
      this.txtTestMethods.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
      // 
      // txtSucceeded
      // 
      this.txtSucceeded.Anchor = System.Windows.Forms.AnchorStyles.Right;
      this.txtSucceeded.Font = new System.Drawing.Font("Microsoft Sans Serif", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.txtSucceeded.Location = new System.Drawing.Point(488, 140);
      this.txtSucceeded.Multiline = true;
      this.txtSucceeded.Name = "txtSucceeded";
      this.txtSucceeded.ReadOnly = true;
      this.txtSucceeded.Size = new System.Drawing.Size(80, 24);
      this.txtSucceeded.TabIndex = 15;
      this.txtSucceeded.Text = "";
      this.txtSucceeded.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
      // 
      // label6
      // 
      this.label6.Anchor = System.Windows.Forms.AnchorStyles.Right;
      this.label6.Location = new System.Drawing.Point(488, 124);
      this.label6.Name = "label6";
      this.label6.Size = new System.Drawing.Size(64, 16);
      this.label6.TabIndex = 14;
      this.label6.Text = "Succeeded:";
      // 
      // TestForm
      // 
      this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
      this.ClientSize = new System.Drawing.Size(584, 454);
      this.Controls.AddRange(new System.Windows.Forms.Control[] {
                                                                  this.txtSucceeded,
                                                                  this.label6,
                                                                  this.txtTestMethods,
                                                                  this.txtTestCases,
                                                                  this.txtFailed,
                                                                  this.label5,
                                                                  this.txtTime,
                                                                  this.label4,
                                                                  this.pbTestMethods,
                                                                  this.label3,
                                                                  this.pbTestCases,
                                                                  this.label2,
                                                                  this.button2,
                                                                  this.button1,
                                                                  this.txtOutput,
                                                                  this.label1});
      this.MinimumSize = new System.Drawing.Size(480, 432);
      this.Name = "TestForm";
      this.Text = "TestForm";
      this.Activated += new System.EventHandler(this.TestForm_Activated);
      this.ResumeLayout(false);

    }
		#endregion

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------        
    private void button2_Click(object sender, System.EventArgs e) {
      this.Close();
    }

    private void button1_Click(object sender, System.EventArgs e) {
      Run();      
    }

    /// <summary>
    /// Tester object assigned to TestForm
    /// </summary>
    public Tester Tester {
      set {
        // remove events
        if (tester != null) {
          tester.beginTestCase -= new TestCaseCallback( beginTestCase );
          tester.finishedTestCase -= new TestCaseCallback( endTestCase );
          tester.testMethod -= new TestMethodCallback( testMethod );
        }
        // assign new tester
        tester = value;

        // add events
        tester.beginTestCase += new TestCaseCallback( beginTestCase );
        tester.finishedTestCase += new TestCaseCallback( endTestCase );
        tester.testMethod += new TestMethodCallback( testMethod );
      }
      get {               
        return tester;
      }
    }

    private void beginTestCase( TestCaseData tcd ) {
      txtOutput.AppendText(tcd.TestCaseName + Environment.NewLine);
      txtOutput.AppendText("======================" + Environment.NewLine);
      totalTestCases = tcd.TestCaseNum;
      totalTestMethods = tcd.MethodNum;
      currentTestMethod = 0;
      UpdateItems();
    }

    private void endTestCase( TestCaseData tcd ) {      
      currentTestCase++;
      txtOutput.AppendText("=====================" + Environment.NewLine + Environment.NewLine);
      UpdateItems();
    }

    private void testMethod( TestData td ) {      
      currentTestMethod++;
      txtOutput.AppendText("Testing: " + td.MethodName + " ... ");
      if (td.Successful) {
        succeededNum++;
        txtOutput.AppendText("Successful!" + Environment.NewLine);
      }
      else {
        failedNum++;
        txtOutput.AppendText("Failed!" + Environment.NewLine);        
        txtOutput.AppendText("   " + td.errorMessage + Environment.NewLine);              
      }
      TimeSpan ts = DateTime.Now - startTime;
      time = (int)ts.TotalMilliseconds;      
      
      UpdateItems();
    }
    

    /// <summary>
    /// starts the testing process
    /// </summary>
    public void Run() {
      if (tester == null)
        txtOutput.Text = "No Tester assigned!";
      else {        
        currentTestCase = 0;
        currentTestMethod = 0;
        succeededNum = 0;
        failedNum = 0;    
        totalTestCases = 0;
        totalTestMethods = 0;
        txtOutput.Clear();

        startTime = DateTime.Now;
        tester.Run();
      }
    }

    private void UpdateItems() {

      txtTestCases.Text = currentTestCase + "/" + totalTestCases;
      txtTestMethods.Text = currentTestMethod + "/" + totalTestMethods;
      txtSucceeded.Text = succeededNum.ToString();
      txtFailed.Text = failedNum.ToString();
      txtTime.Text = time + " ms";

      pbTestCases.Maximum = totalTestCases;
      pbTestCases.Value = currentTestCase;
      pbTestMethods.Maximum = totalTestMethods;
      pbTestMethods.Value = currentTestMethod;        
    }

    private void TestForm_Activated(object sender, System.EventArgs e) {      
      totalTestCases = tester.TestCases.Count;      
      if (totalTestCases != 0)              
        totalTestMethods = TestCase.CalcMethodsToTest((Type)tester.TestCases[0]).Count;
      UpdateItems();
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------        
	}
}
