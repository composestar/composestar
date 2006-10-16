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
	[Sample("SourceGrid - Advanced features", 40, "SubGrid and scrolling features")]
	public class frmSample40 : System.Windows.Forms.Form
	{
		private SourceGrid.Grid grid1;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public frmSample40()
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
			this.grid1.Size = new System.Drawing.Size(548, 159);
			this.grid1.SpecialKeys = SourceGrid.GridSpecialKeys.Default;
			this.grid1.StyleGrid = null;
			this.grid1.TabIndex = 0;
			// 
			// frmSample40
			// 
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.ClientSize = new System.Drawing.Size(564, 174);
			this.Controls.Add(this.grid1);
			this.Name = "frmSample40";
			this.Text = "SubGrid and scrolling features";
			this.ResumeLayout(false);

		}
		#endregion

		private SourceGrid.Cells.Editors.EditorBase currencyFormat = new SourceGrid.Cells.Editors.EditorBase(typeof(double));
		private SourceGrid.Cells.Editors.EditorBase dateFormat = new SourceGrid.Cells.Editors.EditorBase(typeof(DateTime));
		private SourceGrid.Cells.Views.Cell currencyView = new SourceGrid.Cells.Views.Cell();
		protected override void OnLoad(EventArgs e)
		{
			base.OnLoad (e);

			currencyFormat.TypeConverter = new DevAge.ComponentModel.Converter.CurrencyTypeConverter(typeof(double));
			currencyView.TextAlignment = DevAge.Drawing.ContentAlignment.MiddleRight;
			dateFormat.TypeConverter = new DevAge.ComponentModel.Converter.DateTimeTypeConverter("MMM yyyy");

			grid1.Redim(8, 21);
			grid1.FixedColumns = 1;
			grid1.FixedRows = 1;
			
			grid1[0, 0] = new SourceGrid.Cells.Header();

			//row headers
			grid1[1, 0] = new SourceGrid.Cells.RowHeader("Revenue");
			grid1[2, 0] = new SourceGrid.Cells.RowHeader("Employees Cost");
			grid1[3, 0] = new SourceGrid.Cells.RowHeader("Tax");
			grid1[4, 0] = new SourceGrid.Cells.RowHeader("Totals");
			grid1[4, 0].RowSpan = 4;

			Random rnd = new Random();
			for (int i = 0; i < 20; i++)
				AddDate(i+1, DateTime.Now.AddMonths(i), 2000 * rnd.NextDouble(), 1000 * rnd.NextDouble(), 500 * rnd.NextDouble());

			//Totals
			grid1[4, 1] = new CellTotal("Revenue: ");
			grid1[4, 2] = new CellTotal(mTotRevenue.ToString("C"));
			grid1[5, 1] = new CellTotal("Employees Cost: ");
			grid1[5, 2] = new CellTotal(mTotEmployeesCost.ToString("C"));
			grid1[6, 1] = new CellTotal("Tax: ");
			grid1[6, 2] = new CellTotal(mTotTax.ToString("C"));
			grid1[7, 1] = new CellTotal("Total:");
			grid1[7, 2] = new CellTotal((mTotRevenue - (mTotEmployeesCost + mTotTax)).ToString("C"));

			grid1.Columns[1].AutoSizeMode = SourceGrid.AutoSizeMode.None;
			grid1.Columns[1].Width = 100;
			grid1.Columns[2].AutoSizeMode = SourceGrid.AutoSizeMode.None;
			grid1.Columns[2].Width = 100;
			grid1.Rows[4].AutoSizeMode = SourceGrid.AutoSizeMode.None;
			grid1.Rows[5].AutoSizeMode = SourceGrid.AutoSizeMode.None;
			grid1.Rows[6].AutoSizeMode = SourceGrid.AutoSizeMode.None;
			grid1.Rows[7].AutoSizeMode = SourceGrid.AutoSizeMode.None;
            grid1.AutoSizeCells();
		}

		private double mTotRevenue = 0;
		private double mTotEmployeesCost = 0;
		private double mTotTax = 0;
		private void AddDate(int col, DateTime date, double revenue, double employeesCost, double tax)
		{
			SourceGrid.Cells.ColumnHeader header = new SourceGrid.Cells.ColumnHeader(date);
			header.AutomaticSortEnabled = false;
			header.Editor = dateFormat;
			grid1[0, col] = header;

			grid1[1, col] = new SourceGrid.Cells.Cell(revenue, currencyFormat);
			grid1[1, col].View = currencyView;
			grid1[2, col] = new SourceGrid.Cells.Cell(employeesCost, currencyFormat);
			grid1[2, col].View = currencyView;
			grid1[3, col] = new SourceGrid.Cells.Cell(tax, currencyFormat);
			grid1[3, col].View = currencyView;

			mTotTax += tax;
			mTotRevenue += revenue;
			mTotEmployeesCost += employeesCost;
		}

		private class CellTotal : SourceGrid.Cells.CellControl
		{
			public CellTotal(object val):base(new SourceGrid.Grid(),
										SourceGrid.LinkedControlScrollMode.ScrollVertical, 
										false)
			{
				Control.Redim(1, 1);
				Control.Resize += new EventHandler(Control_Resize);
				Control.Selection.FocusStyle = SourceGrid.FocusStyle.RemoveFocusCellOnLeave | SourceGrid.FocusStyle.RemoveSelectionOnLeave;

				SourceGrid.Cells.Views.Cell totalView = new SourceGrid.Cells.Views.Cell();
				totalView.Font = new Font(Control.Font, FontStyle.Underline);
				Control[0, 0] = new SourceGrid.Cells.Cell(val);
				Control[0, 0].View = totalView;
			}

			public new SourceGrid.Grid Control
			{
				get{return (SourceGrid.Grid)base.Control;}
			}

			private void Control_Resize(object sender, EventArgs e)
			{
				Control.Rows[0].Height = Control.Height;
				Control.Columns[0].Width = Control.Width;
			}	
		}
	}
}
