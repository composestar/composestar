using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;

namespace WindowsFormsSample
{
	/// <summary>
	/// Summary description for frmSampleGrid1.
	/// </summary>
	[Sample("SourceGrid - Standard features", 26, "Use of Controllers: custom cursors, click event and context menu")]
	public class frmSample26 : System.Windows.Forms.Form
	{
		private SourceGrid.Grid grid1;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public frmSample26()
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
				if (components != null) 
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
			this.SuspendLayout();
			// 
			// grid1
			// 
			this.grid1.GridToolTipActive = true;
			this.grid1.Location = new System.Drawing.Point(8, 16);
			this.grid1.Name = "grid1";
			this.grid1.Size = new System.Drawing.Size(280, 240);
			this.grid1.SpecialKeys = SourceGrid.GridSpecialKeys.Default;
			this.grid1.StyleGrid = null;
			this.grid1.TabIndex = 0;
			// 
			// Form1
			// 
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.ClientSize = new System.Drawing.Size(292, 266);
			this.Controls.Add(this.grid1);
			this.Name = "Form1";
			this.Text = "Form1";
			this.Load += new System.EventHandler(this.Form1_Load);
			this.ResumeLayout(false);

		}
		#endregion

		private void Form1_Load(object sender, System.EventArgs e)
		{
			grid1.Redim(10, 3);

			CellClickEvent clickController = new CellClickEvent();
			PopupMenu menuController = new PopupMenu();
			CellCursor cursorController = new CellCursor();

			for (int r = 0; r < grid1.Rows.Count; r++)
			{
				if (r == 0)
				{
					grid1[r, 0] = new SourceGrid.Cells.ColumnHeader("Click event");
					grid1[r, 1] = new SourceGrid.Cells.ColumnHeader("Custom Cursor");
					grid1[r, 2] = new SourceGrid.Cells.ColumnHeader("ContextMenu");
				}
				else
				{
					grid1[r, 0] = new SourceGrid.Cells.Cell("Value " + r.ToString(), typeof(string));
					grid1[r, 1] = new SourceGrid.Cells.Cell(DateTime.Now, typeof(DateTime));
					grid1[r, 2] = new SourceGrid.Cells.Cell("Right click");
				}

				grid1[r, 0].AddController(clickController);
				grid1[r, 1].AddController(cursorController);
				grid1[r, 2].AddController(menuController);
			}

            grid1.AutoSizeCells();
		}
	}

	public class CellCursor : SourceGrid.Cells.Controllers.MouseCursor
	{
		public CellCursor():base(Cursors.Cross, true)
		{
		}
	}

	public class PopupMenu : SourceGrid.Cells.Controllers.ControllerBase
	{
		ContextMenu menu = new ContextMenu();
		public PopupMenu()
		{
			menu.MenuItems.Add("Menu 1");
			menu.MenuItems.Add("Menu 2");
		}

		public override void OnMouseUp(SourceGrid.CellContext sender, MouseEventArgs e)
		{
			base.OnMouseUp (sender, e);

			if (e.Button == MouseButtons.Right)
				menu.Show(sender.Grid, new Point(e.X, e.Y));
		}
	}

	public class CellClickEvent : SourceGrid.Cells.Controllers.ControllerBase
	{
		public override void OnClick(SourceGrid.CellContext sender, EventArgs e)
		{
			base.OnClick (sender, e);

			MessageBox.Show(sender.Grid, sender.GetDisplayText());
		}
	}
}
