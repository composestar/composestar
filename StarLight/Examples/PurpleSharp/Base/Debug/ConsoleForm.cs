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
using System.IO;
using Purple.Scripting;

namespace Purple.Debug
{
	/// <summary>
	/// Summary description for Console.
	/// </summary>
	public class ConsoleForm : System.Windows.Forms.Form, IContainsTraceListener
	{

    // Logic
    TextBoxListener listener;
    IScriptingHost scriptingHost;
    private string loadedFile;

    // Designer
    private System.Windows.Forms.Label label1;
    private System.Windows.Forms.Label label2;
    private System.Windows.Forms.Button btnExecute;
    private System.Windows.Forms.Button btnLoad;
    private System.Windows.Forms.TextBox textConsoleInput;
    private System.Windows.Forms.TextBox textDebugOutput;
    private System.Windows.Forms.MainMenu mnuMain;
    private System.Windows.Forms.MenuItem mnuFile;
    private System.Windows.Forms.MenuItem mnuConsole;
    private System.Windows.Forms.MenuItem mnuHelp;
    private System.Windows.Forms.MenuItem mnuSaveAs;
    private System.Windows.Forms.MenuItem mnuSend;
    private System.Windows.Forms.MenuItem menuItem6;
    private System.Windows.Forms.MenuItem mnuExit;
    private System.Windows.Forms.MenuItem mnuLoadScript;
    private System.Windows.Forms.MenuItem mnuSaveScript;
    private System.Windows.Forms.MenuItem mnuSaveAsScript;
    private System.Windows.Forms.MenuItem mnuExecuteScript;
    private System.Windows.Forms.MenuItem mnuInfo;
    private System.Windows.Forms.Button btnTemplate;
    private System.Windows.Forms.TextBox textLines;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

    /// <summary>
    /// property for setting and retrieving the used scripting Host
    /// </summary>
    public IScriptingHost ScriptingHost {
      set {
        scriptingHost = value;
      }
      get {
        return scriptingHost;
      }
    }
    /// <summary>
    /// 
    /// </summary>
		public ConsoleForm()
		{			
			InitializeComponent();
      listener = new TextBoxListener(textDebugOutput);
		}

		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		protected override void Dispose( bool disposing )
		{
			if( disposing )
			{
				if(components != null)
				{
					components.Dispose();
				}
			}
			base.Dispose( disposing );
		}

    /// <summary>
    /// returns TraceListener to send debug output to
    /// </summary>
    /// <returns>TraceListener object</returns>
    public System.Diagnostics.TraceListener GetTraceListener() {
      return listener;
    }

		#region Windows Form Designer generated code
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
      this.textConsoleInput = new System.Windows.Forms.TextBox();
      this.label1 = new System.Windows.Forms.Label();
      this.textDebugOutput = new System.Windows.Forms.TextBox();
      this.label2 = new System.Windows.Forms.Label();
      this.btnExecute = new System.Windows.Forms.Button();
      this.btnLoad = new System.Windows.Forms.Button();
      this.mnuMain = new System.Windows.Forms.MainMenu();
      this.mnuFile = new System.Windows.Forms.MenuItem();
      this.mnuSaveAs = new System.Windows.Forms.MenuItem();
      this.mnuSend = new System.Windows.Forms.MenuItem();
      this.menuItem6 = new System.Windows.Forms.MenuItem();
      this.mnuExit = new System.Windows.Forms.MenuItem();
      this.mnuConsole = new System.Windows.Forms.MenuItem();
      this.mnuLoadScript = new System.Windows.Forms.MenuItem();
      this.mnuSaveScript = new System.Windows.Forms.MenuItem();
      this.mnuSaveAsScript = new System.Windows.Forms.MenuItem();
      this.mnuExecuteScript = new System.Windows.Forms.MenuItem();
      this.mnuHelp = new System.Windows.Forms.MenuItem();
      this.mnuInfo = new System.Windows.Forms.MenuItem();
      this.btnTemplate = new System.Windows.Forms.Button();
      this.textLines = new System.Windows.Forms.TextBox();
      this.SuspendLayout();
      // 
      // textConsoleInput
      // 
      this.textConsoleInput.Anchor = ((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right);
      this.textConsoleInput.Location = new System.Drawing.Point(24, 258);
      this.textConsoleInput.Multiline = true;
      this.textConsoleInput.Name = "textConsoleInput";
      this.textConsoleInput.ScrollBars = System.Windows.Forms.ScrollBars.Both;
      this.textConsoleInput.Size = new System.Drawing.Size(729, 80);
      this.textConsoleInput.TabIndex = 1;
      this.textConsoleInput.Text = "";
      // 
      // label1
      // 
      this.label1.Dock = System.Windows.Forms.DockStyle.Top;
      this.label1.Font = new System.Drawing.Font("Arial", 9.75F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.label1.Name = "label1";
      this.label1.Size = new System.Drawing.Size(752, 16);
      this.label1.TabIndex = 1;
      this.label1.Text = "Debug Output:";
      // 
      // textDebugOutput
      // 
      this.textDebugOutput.Anchor = (((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
        | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right);
      this.textDebugOutput.Location = new System.Drawing.Point(0, 16);
      this.textDebugOutput.Multiline = true;
      this.textDebugOutput.Name = "textDebugOutput";
      this.textDebugOutput.ReadOnly = true;
      this.textDebugOutput.ScrollBars = System.Windows.Forms.ScrollBars.Both;
      this.textDebugOutput.Size = new System.Drawing.Size(752, 218);
      this.textDebugOutput.TabIndex = 2;
      this.textDebugOutput.Text = "";
      // 
      // label2
      // 
      this.label2.Anchor = ((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right);
      this.label2.Font = new System.Drawing.Font("Arial", 9.75F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.label2.Location = new System.Drawing.Point(0, 235);
      this.label2.Name = "label2";
      this.label2.Size = new System.Drawing.Size(560, 24);
      this.label2.TabIndex = 3;
      this.label2.Text = "Console Input";
      // 
      // btnExecute
      // 
      this.btnExecute.Anchor = (System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right);
      this.btnExecute.Location = new System.Drawing.Point(688, 235);
      this.btnExecute.Name = "btnExecute";
      this.btnExecute.Size = new System.Drawing.Size(64, 24);
      this.btnExecute.TabIndex = 4;
      this.btnExecute.Text = "Execute";
      this.btnExecute.Click += new System.EventHandler(this.btnExecute_Click);
      // 
      // btnLoad
      // 
      this.btnLoad.Anchor = (System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right);
      this.btnLoad.Location = new System.Drawing.Point(624, 235);
      this.btnLoad.Name = "btnLoad";
      this.btnLoad.Size = new System.Drawing.Size(64, 24);
      this.btnLoad.TabIndex = 5;
      this.btnLoad.Text = "Load";
      this.btnLoad.Click += new System.EventHandler(this.btnLoad_Click);
      // 
      // mnuMain
      // 
      this.mnuMain.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                                                                            this.mnuFile,
                                                                            this.mnuConsole,
                                                                            this.mnuHelp});
      // 
      // mnuFile
      // 
      this.mnuFile.Index = 0;
      this.mnuFile.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                                                                            this.mnuSaveAs,
                                                                            this.mnuSend,
                                                                            this.menuItem6,
                                                                            this.mnuExit});
      this.mnuFile.Text = "File";
      // 
      // mnuSaveAs
      // 
      this.mnuSaveAs.Index = 0;
      this.mnuSaveAs.Text = "SaveAs";
      // 
      // mnuSend
      // 
      this.mnuSend.Index = 1;
      this.mnuSend.Text = "Send";
      // 
      // menuItem6
      // 
      this.menuItem6.Index = 2;
      this.menuItem6.Text = "-";
      // 
      // mnuExit
      // 
      this.mnuExit.Index = 3;
      this.mnuExit.Text = "Exit";
      // 
      // mnuConsole
      // 
      this.mnuConsole.Index = 1;
      this.mnuConsole.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                                                                               this.mnuLoadScript,
                                                                               this.mnuSaveScript,
                                                                               this.mnuSaveAsScript,
                                                                               this.mnuExecuteScript});
      this.mnuConsole.Text = "Console";
      // 
      // mnuLoadScript
      // 
      this.mnuLoadScript.Index = 0;
      this.mnuLoadScript.Text = "Load Script";
      this.mnuLoadScript.Click += new System.EventHandler(this.mnuLoadScript_Click);
      // 
      // mnuSaveScript
      // 
      this.mnuSaveScript.Index = 1;
      this.mnuSaveScript.Text = "Save Script";
      this.mnuSaveScript.Click += new System.EventHandler(this.mnuSaveScript_Click);
      // 
      // mnuSaveAsScript
      // 
      this.mnuSaveAsScript.Index = 2;
      this.mnuSaveAsScript.Text = "SaveAs Script";
      this.mnuSaveAsScript.Click += new System.EventHandler(this.mnuSaveAsScript_Click);
      // 
      // mnuExecuteScript
      // 
      this.mnuExecuteScript.Index = 3;
      this.mnuExecuteScript.Text = "Execute Script";
      this.mnuExecuteScript.Click += new System.EventHandler(this.mnuExecuteScript_Click);
      // 
      // mnuHelp
      // 
      this.mnuHelp.Index = 2;
      this.mnuHelp.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                                                                            this.mnuInfo});
      this.mnuHelp.Text = "Help";
      // 
      // mnuInfo
      // 
      this.mnuInfo.Index = 0;
      this.mnuInfo.Text = "Info";
      // 
      // btnTemplate
      // 
      this.btnTemplate.Anchor = (System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right);
      this.btnTemplate.Location = new System.Drawing.Point(560, 235);
      this.btnTemplate.Name = "btnTemplate";
      this.btnTemplate.Size = new System.Drawing.Size(64, 24);
      this.btnTemplate.TabIndex = 6;
      this.btnTemplate.Text = "Template";
      this.btnTemplate.Click += new System.EventHandler(this.btnTemplate_Click);
      // 
      // textLines
      // 
      this.textLines.Anchor = (System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left);
      this.textLines.Location = new System.Drawing.Point(0, 258);
      this.textLines.Multiline = true;
      this.textLines.Name = "textLines";
      this.textLines.ReadOnly = true;
      this.textLines.Size = new System.Drawing.Size(24, 80);
      this.textLines.TabIndex = 7;
      this.textLines.Text = "1\r\n2\r\n3\r\n4\r\n999";
      // 
      // ConsoleForm
      // 
      this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
      this.ClientSize = new System.Drawing.Size(752, 337);
      this.Controls.AddRange(new System.Windows.Forms.Control[] {
                                                                  this.textLines,
                                                                  this.btnTemplate,
                                                                  this.btnLoad,
                                                                  this.btnExecute,
                                                                  this.label2,
                                                                  this.textDebugOutput,
                                                                  this.label1,
                                                                  this.textConsoleInput});
      this.Menu = this.mnuMain;
      this.MinimumSize = new System.Drawing.Size(300, 200);
      this.Name = "ConsoleForm";
      this.Text = "Console";
      this.ResumeLayout(false);

    }
		#endregion

    private void btnExecute_Click(object sender, System.EventArgs e) {
      DoExecute();      
    }

    private void DoExecute() {
      if (scriptingHost == null)
        textDebugOutput.AppendText( "No ScriptingHost assigned!" );
      else {        
        try {
          scriptingHost.Source = textConsoleInput.Text;
          scriptingHost.Execute( );
        } catch (Exception ex) {
          textDebugOutput.AppendText( Purple.Tools.StringHelper.GuiNewLines(ex.ToString()) );          
        }
      }
    }

    private void btnLoad_Click(object sender, System.EventArgs e) {      
      DoLoad();      
    }

    /// <summary>
    /// opens the OpenFileDialog and loads the chosen file
    /// </summary>
    private void DoLoad() {
      if (scriptingHost == null)
        textDebugOutput.AppendText( "No ScriptingHost assigned!" ); 
      else {
        OpenFileDialog fd = new OpenFileDialog();
        fd.Filter = "Script files (*.script)|*.script|All files (*.*)|*.*" ;
        fd.FilterIndex = 1 ;
        fd.RestoreDirectory = true ;
        fd.DefaultExt = "script";        

        Stream fileStream = null;
        if(fd.ShowDialog() == DialogResult.OK) {
          if((fileStream = fd.OpenFile())!= null) {
            loadedFile = fd.FileName;
            scriptingHost.Load( fileStream );           
            textConsoleInput.Text = scriptingHost.Source; 
            fileStream.Close();          
          }
        }
      }
    }

    private void btnTemplate_Click(object sender, System.EventArgs e) {
      if (scriptingHost == null) {
        textDebugOutput.AppendText( "No ScriptingHost assigned!" );
        return;
      }
      ScriptingHostTemplateForm shtForm = new ScriptingHostTemplateForm(scriptingHost);      
      shtForm.ShowDialog(this);  
    }

    private void mnuLoadScript_Click(object sender, System.EventArgs e) {              
      DoLoad();
    }

    private void mnuExecuteScript_Click(object sender, System.EventArgs e) {
      DoExecute();
    }

    private void mnuSaveScript_Click(object sender, System.EventArgs e) {
      if (scriptingHost == null) {
        textDebugOutput.AppendText( "No ScriptingHost assigned!" );
        return;
      }          
      if (loadedFile == null)
        DoSaveAs();
      else {
        Stream fileStream = new FileStream( loadedFile, FileMode.Create);        
        scriptingHost.Source = textConsoleInput.Text;          
        scriptingHost.Save(fileStream);                    
        fileStream.Close();  
      }
    }

    private void DoSaveAs() {
      if (scriptingHost == null) {
        textDebugOutput.AppendText( "No ScriptingHost assigned!" );
        return;
      }

      SaveFileDialog fd = new SaveFileDialog();      
      fd.Filter = "Script files (*.script)|*.script|All files (*.*)|*.*" ;
      fd.FilterIndex = 1 ;
      fd.RestoreDirectory = true ;
      fd.DefaultExt = "script";
      fd.FileName = loadedFile;

      Stream fileStream = null;
      if(fd.ShowDialog() == DialogResult.OK) {        
        if((fileStream = fd.OpenFile())!= null) {
          loadedFile = fd.FileName;
          scriptingHost.Source = textConsoleInput.Text;          
          scriptingHost.Save(fileStream);                    
          fileStream.Close();          
        }
      }
    }

    private void mnuSaveAsScript_Click(object sender, System.EventArgs e) {
      DoSaveAs();      
    }
	}
}
