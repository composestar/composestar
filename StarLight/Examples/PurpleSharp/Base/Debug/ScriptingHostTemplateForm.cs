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
using Purple.Scripting;

namespace Purple.Debug
{
	/// <summary>
	/// Summary description for ScriptingHostTemplateForm.
	/// </summary>
	public class ScriptingHostTemplateForm : System.Windows.Forms.Form
	{
    // Logic
    IScriptingHost scriptingHost;
    // form stuff
    private System.Windows.Forms.Label label1;
    private System.Windows.Forms.TextBox textReferences;
    private System.Windows.Forms.Label label2;
    private System.Windows.Forms.TextBox textNamespaces;
    private System.Windows.Forms.Label label3;
    private System.Windows.Forms.TextBox textTemplate;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

    /// <summary>
    /// constructor filling textboxes with info from ScriptingHost
    /// </summary>
    /// <param name="sh">ScriptingHost to take info from</param>
    public ScriptingHostTemplateForm(IScriptingHost sh) : this() {
      SetScriptingHost(sh);
    }

    /// <summary>
    /// standard constructor
    /// </summary>
		public ScriptingHostTemplateForm()
		{			
			InitializeComponent();    
		}

    /// <summary>
    /// sets the scriptingHost from which textBoxes should take their content
    /// </summary>
    /// <param name="sh">scriptingHost to set</param>
    public void SetScriptingHost(IScriptingHost sh) {
      if (sh == null)
        return;
      scriptingHost = sh;
      
      foreach( string reference in scriptingHost.References)
        textReferences.Text += reference + Environment.NewLine;
        // strange AppendText bug!!!

      foreach( string ns in scriptingHost.Namespaces)
        textNamespaces.Text += ns + Environment.NewLine;

      textTemplate.Text = scriptingHost.TemplateSource;
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

		#region Windows Form Designer generated code
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
      this.label1 = new System.Windows.Forms.Label();
      this.textReferences = new System.Windows.Forms.TextBox();
      this.label2 = new System.Windows.Forms.Label();
      this.textNamespaces = new System.Windows.Forms.TextBox();
      this.label3 = new System.Windows.Forms.Label();
      this.textTemplate = new System.Windows.Forms.TextBox();
      this.SuspendLayout();
      // 
      // label1
      // 
      this.label1.Font = new System.Drawing.Font("Arial", 9.75F, System.Drawing.FontStyle.Bold);
      this.label1.Name = "label1";
      this.label1.Size = new System.Drawing.Size(192, 24);
      this.label1.TabIndex = 0;
      this.label1.Text = "References";
      // 
      // textReferences
      // 
      this.textReferences.Anchor = ((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right);
      this.textReferences.Location = new System.Drawing.Point(0, 16);
      this.textReferences.Multiline = true;
      this.textReferences.Name = "textReferences";
      this.textReferences.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
      this.textReferences.Size = new System.Drawing.Size(432, 80);
      this.textReferences.TabIndex = 1;
      this.textReferences.Text = "";
      // 
      // label2
      // 
      this.label2.Font = new System.Drawing.Font("Arial", 9.75F, System.Drawing.FontStyle.Bold);
      this.label2.Location = new System.Drawing.Point(0, 104);
      this.label2.Name = "label2";
      this.label2.Size = new System.Drawing.Size(192, 24);
      this.label2.TabIndex = 2;
      this.label2.Text = "Namespaces";
      // 
      // textNamespaces
      // 
      this.textNamespaces.Anchor = ((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right);
      this.textNamespaces.Location = new System.Drawing.Point(0, 120);
      this.textNamespaces.Multiline = true;
      this.textNamespaces.Name = "textNamespaces";
      this.textNamespaces.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
      this.textNamespaces.Size = new System.Drawing.Size(432, 88);
      this.textNamespaces.TabIndex = 3;
      this.textNamespaces.Text = "";
      // 
      // label3
      // 
      this.label3.Font = new System.Drawing.Font("Arial", 9.75F, System.Drawing.FontStyle.Bold);
      this.label3.Location = new System.Drawing.Point(0, 216);
      this.label3.Name = "label3";
      this.label3.Size = new System.Drawing.Size(192, 24);
      this.label3.TabIndex = 4;
      this.label3.Text = "TemplateSource";
      // 
      // textTemplate
      // 
      this.textTemplate.Anchor = (((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
        | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right);
      this.textTemplate.Location = new System.Drawing.Point(0, 232);
      this.textTemplate.Multiline = true;
      this.textTemplate.Name = "textTemplate";
      this.textTemplate.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
      this.textTemplate.Size = new System.Drawing.Size(432, 88);
      this.textTemplate.TabIndex = 5;
      this.textTemplate.Text = "";
      // 
      // ScriptingHostTemplateForm
      // 
      this.AccessibleRole = System.Windows.Forms.AccessibleRole.Dialog;
      this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
      this.ClientSize = new System.Drawing.Size(432, 318);
      this.Controls.AddRange(new System.Windows.Forms.Control[] {
                                                                  this.textTemplate,
                                                                  this.label3,
                                                                  this.textNamespaces,
                                                                  this.label2,
                                                                  this.textReferences,
                                                                  this.label1});
      this.MinimumSize = new System.Drawing.Size(256, 328);
      this.Name = "ScriptingHostTemplateForm";
      this.Text = "ScriptingHostTemplateForm";
      this.Closing += new System.ComponentModel.CancelEventHandler(this.ScriptingHostTemplateForm_Closing);
      this.Load += new System.EventHandler(this.ScriptingHostTemplateForm_Load);
      this.ResumeLayout(false);

    }
		#endregion

    private void ScriptingHostTemplateForm_Load(object sender, System.EventArgs e) {
    
    }

    private void ScriptingHostTemplateForm_Closing(object sender, System.ComponentModel.CancelEventArgs e) {
      scriptingHost.TemplateSource = textTemplate.Text;

      scriptingHost.References.Clear();
      foreach( string reference in textReferences.Lines)
        if (reference.Length != 0)
          scriptingHost.References.Add(reference);

      scriptingHost.Namespaces.Clear();
      foreach( string ns in textNamespaces.Lines)
        if (ns.Length != 0)
          scriptingHost.Namespaces.Add(ns);        
    }
	}
}
