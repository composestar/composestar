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
using System.Web.Mail;

namespace Purple.Debug
{
	/// <summary>
	/// Summary description for ExceptionForm.
	/// </summary>
	public class ExceptionForm : System.Windows.Forms.Form
	{
    private System.Windows.Forms.Label label1;
    private System.Windows.Forms.TextBox textBox1;
    private System.Windows.Forms.Button btnContinue;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

    /// <summary>
    /// Creates a new ExceptionForm.
    /// </summary>
		public ExceptionForm()
		{
			//
			// Required for Windows Form Designer support
			//
			InitializeComponent();
		}

    /// <summary>
    /// Creates a new ExceptionForm.
    /// </summary>
    /// <param name="ex">The exception to visualize.</param>
    public ExceptionForm(Exception ex) : this() {
      this.textBox1.Text = ex.GetType().ToString() + " - " + ex.ToString();
    }

    /// <summary>
    /// Creates a new ExceptionForm.
    /// </summary>
    /// <param name="ex">The string to visualize.</param>
    public ExceptionForm(string ex) : this() {
      this.textBox1.Text = ex;
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
      this.textBox1 = new System.Windows.Forms.TextBox();
      this.btnContinue = new System.Windows.Forms.Button();
      this.SuspendLayout();
      // 
      // label1
      // 
      this.label1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right)));
      this.label1.BackColor = System.Drawing.Color.FromArgb(((System.Byte)(192)), ((System.Byte)(192)), ((System.Byte)(255)));
      this.label1.Font = new System.Drawing.Font("Arial Black", 20.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
      this.label1.Location = new System.Drawing.Point(8, 8);
      this.label1.Name = "label1";
      this.label1.Size = new System.Drawing.Size(656, 40);
      this.label1.TabIndex = 0;
      this.label1.Text = "Error Report";
      this.label1.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
      // 
      // textBox1
      // 
      this.textBox1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
        | System.Windows.Forms.AnchorStyles.Left) 
        | System.Windows.Forms.AnchorStyles.Right)));
      this.textBox1.BackColor = System.Drawing.Color.FromArgb(((System.Byte)(208)), ((System.Byte)(208)), ((System.Byte)(255)));
      this.textBox1.Location = new System.Drawing.Point(8, 56);
      this.textBox1.Multiline = true;
      this.textBox1.Name = "textBox1";
      this.textBox1.ReadOnly = true;
      this.textBox1.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
      this.textBox1.Size = new System.Drawing.Size(656, 184);
      this.textBox1.TabIndex = 1;
      this.textBox1.Text = "textBox1";
      // 
      // btnContinue
      // 
      this.btnContinue.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
      this.btnContinue.Location = new System.Drawing.Point(576, 248);
      this.btnContinue.Name = "btnContinue";
      this.btnContinue.Size = new System.Drawing.Size(88, 32);
      this.btnContinue.TabIndex = 3;
      this.btnContinue.Text = "Ok";
      this.btnContinue.Click += new System.EventHandler(this.btnContinue_Click);
      // 
      // ExceptionForm
      // 
      this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
      this.ClientSize = new System.Drawing.Size(672, 286);
      this.Controls.Add(this.btnContinue);
      this.Controls.Add(this.textBox1);
      this.Controls.Add(this.label1);
      this.Name = "ExceptionForm";
      this.Text = "ExceptionForm";
      this.ResumeLayout(false);

    }
		#endregion

    private void btnContinue_Click(object sender, System.EventArgs e) {
      this.DialogResult = DialogResult.OK;
      this.Close();    
    }

    private void btnBreak_Click(object sender, System.EventArgs e) {
      this.DialogResult = DialogResult.Abort;
      this.Close();
    }

    /// <summary>
    /// ExceptionHandler that outpus the exception in a form.
    /// </summary>
    /// <param name="ex">The exception to output.</param>
    public static void ExceptionHandler(Exception ex) {
      ExceptionForm form = new ExceptionForm(ex);
      form.ShowDialog();
      //return (form.ShowDialog() == DialogResult.OK);
    }
	}
}
