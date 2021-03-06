using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;

namespace WindowsFormsSample
{
	/// <summary>
	/// Summary description for frmSample4.
	/// </summary>
	[Sample("SourceGrid - Generic Samples", 4, "Real Grid performance")]
	public class frmSample4 : System.Windows.Forms.Form
	{
		private System.Windows.Forms.Button btLoad;
		private SourceGrid.Grid grid;
		private System.Windows.Forms.Label label2;
		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.TextBox txtCols;
		private System.Windows.Forms.TextBox txtRows;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public frmSample4()
		{
			//
			// Required for Windows Form Designer support
			//
			InitializeComponent();
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
			this.grid = new SourceGrid.Grid();
			this.btLoad = new System.Windows.Forms.Button();
			this.label2 = new System.Windows.Forms.Label();
			this.label1 = new System.Windows.Forms.Label();
			this.txtCols = new System.Windows.Forms.TextBox();
			this.txtRows = new System.Windows.Forms.TextBox();
			this.SuspendLayout();
			// 
			// grid
			// 
			this.grid.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
				| System.Windows.Forms.AnchorStyles.Left) 
				| System.Windows.Forms.AnchorStyles.Right)));
			this.grid.AutoStretchColumnsToFitWidth = false;
			this.grid.AutoStretchRowsToFitHeight = false;
			this.grid.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
			this.grid.CustomSort = false;
			this.grid.GridToolTipActive = true;
			this.grid.Location = new System.Drawing.Point(4, 32);
			this.grid.Name = "grid";
			this.grid.OverrideCommonCmdKey = true;
			this.grid.Size = new System.Drawing.Size(508, 404);
			this.grid.SpecialKeys = SourceGrid.GridSpecialKeys.Default;
			this.grid.StyleGrid = null;
			this.grid.TabIndex = 3;
			// 
			// btLoad
			// 
			this.btLoad.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.btLoad.Location = new System.Drawing.Point(292, 4);
			this.btLoad.Name = "btLoad";
			this.btLoad.TabIndex = 2;
			this.btLoad.Text = "Load";
			this.btLoad.Click += new System.EventHandler(this.btLoad_Click);
			// 
			// label2
			// 
			this.label2.Location = new System.Drawing.Point(4, 0);
			this.label2.Name = "label2";
			this.label2.Size = new System.Drawing.Size(56, 23);
			this.label2.TabIndex = 12;
			this.label2.Text = "Rows:";
			this.label2.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// label1
			// 
			this.label1.Location = new System.Drawing.Point(152, 0);
			this.label1.Name = "label1";
			this.label1.Size = new System.Drawing.Size(56, 23);
			this.label1.TabIndex = 11;
			this.label1.Text = "Columns:";
			this.label1.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// txtCols
			// 
			this.txtCols.Location = new System.Drawing.Point(216, 4);
			this.txtCols.Name = "txtCols";
			this.txtCols.Size = new System.Drawing.Size(72, 20);
			this.txtCols.TabIndex = 1;
			this.txtCols.Text = "200";
			// 
			// txtRows
			// 
			this.txtRows.Location = new System.Drawing.Point(64, 4);
			this.txtRows.Name = "txtRows";
			this.txtRows.Size = new System.Drawing.Size(72, 20);
			this.txtRows.TabIndex = 0;
			this.txtRows.Text = "200";
			// 
			// frmSample4
			// 
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.ClientSize = new System.Drawing.Size(516, 439);
			this.Controls.Add(this.label2);
			this.Controls.Add(this.label1);
			this.Controls.Add(this.txtCols);
			this.Controls.Add(this.txtRows);
			this.Controls.Add(this.btLoad);
			this.Controls.Add(this.grid);
			this.Name = "frmSample4";
			this.Text = "Grid Performance";
			this.ResumeLayout(false);

		}
		#endregion

		private void btLoad_Click(object sender, System.EventArgs e)
		{
			//Visual properties shared between all the cells
			SourceGrid.Cells.Views.Cell view = new SourceGrid.Cells.Views.Cell();
			view.BackColor = Color.Snow;

			//Editor (IDataModel) shared between all the cells
			SourceGrid.Cells.Editors.TextBox editor = new SourceGrid.Cells.Editors.TextBox(typeof(string));

			grid.Redim(int.Parse(txtRows.Text), int.Parse(txtCols.Text));

			for (int r = 0; r < grid.RowsCount; r++)
				for (int c = 0; c < grid.ColumnsCount; c++)
				{
					grid[r, c] = new SourceGrid.Cells.Cell(r.ToString() + "," + c.ToString());
					grid[r, c].Editor = editor;
					grid[r, c].View = view;
				}
		}
	}
}
