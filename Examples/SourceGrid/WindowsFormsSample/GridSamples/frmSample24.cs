using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;

namespace WindowsFormsSample.GridSamples
{
	/// <summary>
	/// Summary description for frmSample24.
	/// </summary>
	[Sample("SourceGrid - Standard features", 24, "Clipboard, Delete and Drag and Drop")]
	public class frmSample24 : System.Windows.Forms.Form
	{
		private SourceGrid.Grid grid1;
		private SourceGrid.Grid grid2;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public frmSample24()
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
			this.grid1 = new SourceGrid.Grid();
			this.grid2 = new SourceGrid.Grid();
			this.SuspendLayout();
			// 
			// grid1
			// 
			this.grid1.AutoStretchColumnsToFitWidth = false;
			this.grid1.AutoStretchRowsToFitHeight = false;
			this.grid1.CustomSort = false;
			this.grid1.GridToolTipActive = true;
			this.grid1.Location = new System.Drawing.Point(4, 8);
			this.grid1.Name = "grid1";
			this.grid1.OverrideCommonCmdKey = true;
			this.grid1.Size = new System.Drawing.Size(256, 228);
			this.grid1.SpecialKeys = SourceGrid.GridSpecialKeys.Default;
			this.grid1.StyleGrid = null;
			this.grid1.TabIndex = 0;
			// 
			// grid2
			// 
			this.grid2.AutoStretchColumnsToFitWidth = false;
			this.grid2.AutoStretchRowsToFitHeight = false;
			this.grid2.CustomSort = false;
			this.grid2.GridToolTipActive = true;
			this.grid2.Location = new System.Drawing.Point(276, 8);
			this.grid2.Name = "grid2";
			this.grid2.OverrideCommonCmdKey = true;
			this.grid2.Size = new System.Drawing.Size(256, 228);
			this.grid2.SpecialKeys = SourceGrid.GridSpecialKeys.Default;
			this.grid2.StyleGrid = null;
			this.grid2.TabIndex = 1;
			// 
			// frmSample24
			// 
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.ClientSize = new System.Drawing.Size(536, 241);
			this.Controls.Add(this.grid2);
			this.Controls.Add(this.grid1);
			this.Name = "frmSample24";
			this.Text = "Drag and Drop";
			this.ResumeLayout(false);

		}
		#endregion

		protected override void OnLoad(EventArgs e)
		{
			base.OnLoad (e);

			grid1.Redim(10, 10);
			grid2.Redim(10, 10);
			Random rnd = new Random();

			for (int r = 0; r < grid1.RowsCount; r++)
				for (int c = 0; c < grid1.ColumnsCount; c++)
					grid1[r, c] = new SourceGrid.Cells.Cell(rnd.NextDouble(), typeof(double));

			for (int r = 0; r < grid2.RowsCount; r++)
				for (int c = 0; c < grid2.ColumnsCount; c++)
					grid2[r, c] = new SourceGrid.Cells.Cell(rnd.NextDouble(), typeof(double));


			grid1.GridController.AddController(SourceGrid.Controllers.SelectionDrag.Cut);
			grid2.GridController.AddController(SourceGrid.Controllers.SelectionDrag.Cut);

			grid1.GridController.AddController(SourceGrid.Controllers.SelectionDrop.Default);
			grid2.GridController.AddController(SourceGrid.Controllers.SelectionDrop.Default);

			grid1.GridController.AddController(SourceGrid.Controllers.SelectionDelete.Default);
			grid2.GridController.AddController(SourceGrid.Controllers.SelectionDelete.Default);

			grid1.GridController.AddController(SourceGrid.Controllers.SelectionClipboard.CopyCut);
			grid2.GridController.AddController(SourceGrid.Controllers.SelectionClipboard.CopyCut);
		}

	}
}
