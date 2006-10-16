using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;

namespace WindowsFormsSample
{
	/// <summary>
	/// Summary description for frmSample14.
	/// </summary>
	[Sample("SourceGrid - Basic concepts", 28, "Real Grid with shared components")]
	public class frmSample28 : System.Windows.Forms.Form
	{
		private SourceGrid.Grid grid1;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public frmSample28()
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
			this.SuspendLayout();
			// 
			// grid1
			// 
			this.grid1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
				| System.Windows.Forms.AnchorStyles.Left) 
				| System.Windows.Forms.AnchorStyles.Right)));
			this.grid1.GridToolTipActive = true;
			this.grid1.Location = new System.Drawing.Point(8, 8);
			this.grid1.Name = "grid1";
			this.grid1.Size = new System.Drawing.Size(276, 256);
			this.grid1.SpecialKeys = SourceGrid.GridSpecialKeys.Default;
			this.grid1.StyleGrid = null;
			this.grid1.TabIndex = 0;
			// 
			// frmSample28
			// 
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.ClientSize = new System.Drawing.Size(292, 271);
			this.Controls.Add(this.grid1);
			this.Name = "frmSample28";
			this.Text = "Basic Grid";
			this.ResumeLayout(false);

		}
		#endregion

		protected override void OnLoad(EventArgs e)
		{
			base.OnLoad (e);

			//Border
			DevAge.Drawing.Border border = new DevAge.Drawing.Border(Color.DarkKhaki, 1);
			DevAge.Drawing.RectangleBorder cellBorder = new DevAge.Drawing.RectangleBorder(border, border);

			//Views Odd
			SourceGrid.Cells.Views.Cell viewOddNormal = new SourceGrid.Cells.Views.Cell();
			viewOddNormal.BackColor = Color.Khaki;
			viewOddNormal.Border = cellBorder;
			SourceGrid.Cells.Views.CheckBox viewOddCheckBox = new SourceGrid.Cells.Views.CheckBox();
			viewOddCheckBox.BackColor = Color.Khaki;
			viewOddCheckBox.Border = cellBorder;

			//Views Even
			SourceGrid.Cells.Views.Cell viewEvenNormal = new SourceGrid.Cells.Views.Cell(viewOddNormal);
			viewEvenNormal.BackColor = Color.DarkKhaki;
			SourceGrid.Cells.Views.CheckBox viewEvenCheckBox = new SourceGrid.Cells.Views.CheckBox(viewOddCheckBox);
			viewEvenCheckBox.BackColor = Color.DarkKhaki;

			//ColumnHeader view
			SourceGrid.Cells.Views.ColumnHeader viewColumnHeader = new SourceGrid.Cells.Views.ColumnHeader();
            viewColumnHeader.ForeColor = Color.White;
            viewColumnHeader.Font = new Font("Comic Sans MS", 10, FontStyle.Underline);

            DevAge.Drawing.VisualElements.ColumnHeader backgroundColHeader = new DevAge.Drawing.VisualElements.ColumnHeader();
            backgroundColHeader.BackColor = Color.Maroon;
            viewColumnHeader.Background = backgroundColHeader;

			//Editors
			SourceGrid.Cells.Editors.TextBox editorString = new SourceGrid.Cells.Editors.TextBox(typeof(string));
			SourceGrid.Cells.Editors.TextBoxUITypeEditor editorDateTime = new SourceGrid.Cells.Editors.TextBoxUITypeEditor(typeof(DateTime));


			//Create the grid
			grid1.BorderStyle = BorderStyle.FixedSingle;

			grid1.ColumnsCount = 3;
			grid1.FixedRows = 1;
			grid1.Rows.Insert(0);

			SourceGrid.Cells.ColumnHeader columnHeader;

			columnHeader = new SourceGrid.Cells.ColumnHeader("String");
			columnHeader.View = viewColumnHeader;
			columnHeader.AutomaticSortEnabled = false;
			grid1[0,0] = columnHeader;

			columnHeader = new SourceGrid.Cells.ColumnHeader("DateTime");
			columnHeader.View = viewColumnHeader;
			columnHeader.AutomaticSortEnabled = false;
			grid1[0,1] = columnHeader;

			columnHeader = new SourceGrid.Cells.ColumnHeader("CheckBox");
			columnHeader.View = viewColumnHeader;
			columnHeader.AutomaticSortEnabled = false;
			grid1[0,2] = columnHeader;

			for (int r = 1; r < 10; r++)
			{
				grid1.Rows.Insert(r);

				grid1[r,0] = new SourceGrid.Cells.Cell("Hello " + r.ToString());
				grid1[r,0].Editor = editorString;

				grid1[r,1] = new SourceGrid.Cells.Cell(DateTime.Today);
				grid1[r,1].Editor = editorDateTime;

				grid1[r,2] = new SourceGrid.Cells.CheckBox(null, true);

				if (Math.IEEERemainder(r, 2) == 0)
				{
					grid1[r,0].View = viewOddNormal;
					grid1[r,1].View = viewOddNormal;
					grid1[r,2].View = viewOddCheckBox;
				}
				else
				{
					grid1[r,0].View = viewEvenNormal;
					grid1[r,1].View = viewEvenNormal;
					grid1[r,2].View = viewEvenCheckBox;
				}
			}

            grid1.AutoSizeCells();
		}
	}
}