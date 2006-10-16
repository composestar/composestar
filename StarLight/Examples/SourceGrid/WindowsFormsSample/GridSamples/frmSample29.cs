using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Data;

namespace WindowsFormsSample
{
	/// <summary>
	/// Summary description for frmSample9.
	/// </summary>
	[Sample("SourceGrid - Extensions", 29, "Advanced Data Binding - DataGrid 1")]
	public class frmSample29 : System.Windows.Forms.Form
	{
		private SourceGrid.DataGrid dataGrid;
		private System.Windows.Forms.Button btLoadXml;
		private System.Windows.Forms.Button btSaveXml;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public frmSample29()
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
			this.dataGrid = new SourceGrid.DataGrid();
			this.btLoadXml = new System.Windows.Forms.Button();
			this.btSaveXml = new System.Windows.Forms.Button();
			this.SuspendLayout();
			// 
			// dataGrid
			// 
			this.dataGrid.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
				| System.Windows.Forms.AnchorStyles.Left) 
				| System.Windows.Forms.AnchorStyles.Right)));
			this.dataGrid.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
			this.dataGrid.DefaultWidth = 20;
			this.dataGrid.DeleteQuestionMessage = "Are you sure to delete all the selected rows?";
			this.dataGrid.FixedRows = 1;
			this.dataGrid.GridToolTipActive = true;
			this.dataGrid.Location = new System.Drawing.Point(4, 32);
			this.dataGrid.Name = "dataGrid";
			this.dataGrid.Size = new System.Drawing.Size(572, 424);
			this.dataGrid.SpecialKeys = SourceGrid.GridSpecialKeys.Default;
			this.dataGrid.StyleGrid = null;
			this.dataGrid.TabIndex = 15;
			// 
			// btLoadXml
			// 
			this.btLoadXml.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.btLoadXml.Location = new System.Drawing.Point(4, 4);
			this.btLoadXml.Name = "btLoadXml";
			this.btLoadXml.TabIndex = 16;
			this.btLoadXml.Text = "Load XML";
			this.btLoadXml.Click += new System.EventHandler(this.btLoadXml_Click);
			// 
			// btSaveXml
			// 
			this.btSaveXml.FlatStyle = System.Windows.Forms.FlatStyle.System;
			this.btSaveXml.Location = new System.Drawing.Point(92, 4);
			this.btSaveXml.Name = "btSaveXml";
			this.btSaveXml.TabIndex = 17;
			this.btSaveXml.Text = "Save XML";
			this.btSaveXml.Click += new System.EventHandler(this.btSaveXml_Click);
			// 
			// frmSample29
			// 
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.ClientSize = new System.Drawing.Size(580, 463);
			this.Controls.Add(this.btSaveXml);
			this.Controls.Add(this.btLoadXml);
			this.Controls.Add(this.dataGrid);
			this.Name = "frmSample29";
			this.Text = "Advanced DataGrid binding (XML DataSet)";
			this.Load += new System.EventHandler(this.frmSample29_Load);
			this.ResumeLayout(false);

		}
		#endregion

		private DataView mView;
		private void frmSample29_Load(object sender, System.EventArgs e)
		{
			//Read Data From xml
			DataSet ds = new DataSet();
			ds.ReadXml(System.Reflection.Assembly.GetExecutingAssembly().GetManifestResourceStream("WindowsFormsSample.GridSamples.SampleData2.xml"));
			mView = ds.Tables[0].DefaultView;


			dataGrid.FixedRows = 1;
			dataGrid.FixedColumns = 1;

			//Header row
			dataGrid.Columns.Insert(0, SourceGrid.DataGridColumn.CreateRowHeader(dataGrid));

			//Create default columns
			CreateColumns(dataGrid.Columns, mView.Table);

			dataGrid.DataSource = mView;

            dataGrid.AutoSizeCells();

            SourceGrid.DataGridColumnHeader header = (SourceGrid.DataGridColumnHeader)dataGrid.Columns[1].HeaderCell;
            header.ResizeEnabled = false;
		}

		private static void CreateColumns(SourceGrid.DataGridColumns columns, DataTable sourceTable)
		{
			SourceGrid.Cells.Editors.TextBoxNumeric numericEditor = new SourceGrid.Cells.Editors.TextBoxNumeric(typeof(decimal));
			numericEditor.TypeConverter = new DevAge.ComponentModel.Converter.NumberTypeConverter(typeof(decimal), "N");
			numericEditor.AllowNull = true;
			SourceGrid.Cells.Editors.TextBox stringEditor = new SourceGrid.Cells.Editors.TextBox(typeof(string));

			//Borders
			DevAge.Drawing.RectangleBorder border = new DevAge.Drawing.RectangleBorder(new DevAge.Drawing.Border(Color.ForestGreen), new DevAge.Drawing.Border(Color.ForestGreen));

			//Standard Views
			SourceGrid.Cells.Views.Link viewLink = new SourceGrid.Cells.Views.Link();
			viewLink.BackColor = Color.DarkSeaGreen;
			viewLink.Border = border;
			viewLink.ImageAlignment = DevAge.Drawing.ContentAlignment.MiddleCenter;
			viewLink.TextAlignment = DevAge.Drawing.ContentAlignment.MiddleCenter;
			SourceGrid.Cells.Views.Cell viewString = new SourceGrid.Cells.Views.Cell();
			viewString.BackColor = Color.DarkSeaGreen;
			viewString.Border = border;
			viewString.TextAlignment = DevAge.Drawing.ContentAlignment.MiddleLeft;
			SourceGrid.Cells.Views.Cell viewNumeric = new SourceGrid.Cells.Views.Cell();
			viewNumeric.BackColor = Color.DarkSeaGreen;
			viewNumeric.Border = border;
			viewNumeric.TextAlignment = DevAge.Drawing.ContentAlignment.MiddleRight;
			SourceGrid.Cells.Views.Cell viewImage = new SourceGrid.Cells.Views.Cell();
			viewImage.BackColor = Color.DarkSeaGreen;
			viewImage.Border = border;
			viewImage.ImageStretch = false;
			viewImage.ImageAlignment = DevAge.Drawing.ContentAlignment.MiddleCenter;

			Color alternateColor = Color.LightGray;

			DataColumn dataColumn;
			DataGridColumnAlternate gridColumn;
			SourceGrid.Cells.ICellVirtual dataCell;

			//Create columns

			dataCell = new SourceGrid.Cells.Link("");
			dataCell.AddController(new LinkClickDelete());
			dataCell.View = viewLink;
            ((SourceGrid.Cells.Link)dataCell).Image = Properties.Resources.trash.ToBitmap();
			gridColumn = new DataGridColumnAlternate(columns.Grid, null, "Delete", dataCell, alternateColor);
			columns.Insert(columns.Count, gridColumn);
			//Add an image to the header

			dataColumn = sourceTable.Columns["Flag"];
			dataCell = new SourceGrid.Cells.DataGrid.Image(dataColumn);
			dataCell.View = viewImage;
			gridColumn = new DataGridColumnAlternate(columns.Grid, dataColumn, dataColumn.Caption, dataCell, alternateColor);
			columns.Insert(columns.Count, gridColumn);

			dataColumn = sourceTable.Columns["Country"];
			dataCell = new SourceGrid.Cells.DataGrid.Cell(dataColumn);
			dataCell.View = viewString;
			dataCell.Editor = stringEditor;
			gridColumn = new DataGridColumnAlternate(columns.Grid, dataColumn, dataColumn.Caption, dataCell, alternateColor);
			columns.Insert(columns.Count, gridColumn);

			dataColumn = sourceTable.Columns["Uniform"];
			dataCell = new SourceGrid.Cells.DataGrid.Image(dataColumn);
			dataCell.View = viewImage;
			gridColumn = new DataGridColumnAlternate(columns.Grid, dataColumn, dataColumn.Caption, dataCell, alternateColor);
			columns.Insert(columns.Count, gridColumn);

			dataColumn = sourceTable.Columns["Capital"];
			dataCell = new SourceGrid.Cells.DataGrid.Cell(dataColumn);
			dataCell.View = viewString;
			dataCell.Editor = stringEditor;
			gridColumn = new DataGridColumnAlternate(columns.Grid, dataColumn, dataColumn.Caption, dataCell, alternateColor);
			columns.Insert(columns.Count, gridColumn);

			dataColumn = sourceTable.Columns["Population"];
			dataCell = new SourceGrid.Cells.DataGrid.Cell(dataColumn);
			dataCell.View = viewNumeric;
			dataCell.Editor = numericEditor;
			gridColumn = new DataGridColumnAlternate(columns.Grid, dataColumn, dataColumn.Caption, dataCell, alternateColor);
			columns.Insert(columns.Count, gridColumn);

			dataColumn = sourceTable.Columns["Surface"];
			dataCell = new SourceGrid.Cells.DataGrid.Cell(dataColumn);
			dataCell.View = viewNumeric;
			dataCell.Editor = numericEditor;
			gridColumn = new DataGridColumnAlternate(columns.Grid, dataColumn, dataColumn.Caption, dataCell, alternateColor);
			columns.Insert(columns.Count, gridColumn);

			dataColumn = sourceTable.Columns["Languages"];
			dataCell = new SourceGrid.Cells.DataGrid.Cell(dataColumn);
			dataCell.View = viewString;
			dataCell.Editor = stringEditor;
			gridColumn = new DataGridColumnAlternate(columns.Grid, dataColumn, dataColumn.Caption, dataCell, alternateColor);
			columns.Insert(columns.Count, gridColumn);

			dataColumn = sourceTable.Columns["Currency"];
			dataCell = new SourceGrid.Cells.DataGrid.Cell(dataColumn);
			dataCell.View = viewString;
			dataCell.Editor = stringEditor;
			gridColumn = new DataGridColumnAlternate(columns.Grid, dataColumn, dataColumn.Caption, dataCell, alternateColor);
			columns.Insert(columns.Count, gridColumn);

			dataColumn = sourceTable.Columns["Major Cities"];
			dataCell = new SourceGrid.Cells.DataGrid.Cell(dataColumn);
			dataCell.View = viewString;
			dataCell.Editor = stringEditor;
			gridColumn = new DataGridColumnAlternate(columns.Grid, dataColumn, dataColumn.Caption, dataCell, alternateColor);
			columns.Insert(columns.Count, gridColumn);

			dataColumn = sourceTable.Columns["National Holiday"];
			dataCell = new SourceGrid.Cells.DataGrid.Cell(dataColumn);
			dataCell.View = viewString;
			dataCell.Editor = stringEditor;
			gridColumn = new DataGridColumnAlternate(columns.Grid, dataColumn, dataColumn.Caption, dataCell, alternateColor);
			columns.Insert(columns.Count, gridColumn);

			dataColumn = sourceTable.Columns["Lowest point"];
			dataCell = new SourceGrid.Cells.DataGrid.Cell(dataColumn);
			dataCell.View = viewString;
			dataCell.Editor = stringEditor;
			gridColumn = new DataGridColumnAlternate(columns.Grid, dataColumn, dataColumn.Caption, dataCell, alternateColor);
			columns.Insert(columns.Count, gridColumn);

			dataColumn = sourceTable.Columns["Highest point"];
			dataCell = new SourceGrid.Cells.DataGrid.Cell(dataColumn);
			dataCell.View = viewString;
			dataCell.Editor = stringEditor;
			gridColumn = new DataGridColumnAlternate(columns.Grid, dataColumn, dataColumn.Caption, dataCell, alternateColor);
			columns.Insert(columns.Count, gridColumn);
		}

		private void btLoadXml_Click(object sender, System.EventArgs e)
		{
			using (OpenFileDialog dg = new OpenFileDialog())
			{
				if (dg.ShowDialog(this) == DialogResult.OK)
				{
					mView.Table.DataSet.Clear();
					mView.Table.DataSet.ReadXml(dg.FileName, XmlReadMode.IgnoreSchema);
				}
			}
		}

		private void btSaveXml_Click(object sender, System.EventArgs e)
		{
			using (SaveFileDialog dg = new SaveFileDialog())
			{
				if (dg.ShowDialog(this) == DialogResult.OK)
					mView.Table.DataSet.WriteXml(dg.FileName, XmlWriteMode.WriteSchema);
			}
		}


		private class DataGridColumnAlternate : SourceGrid.DataGridColumn
		{
			#region Static Views
			private static SourceGrid.Cells.Views.ColumnHeader viewColHeader;
			static DataGridColumnAlternate()
			{
				//Column header view
				viewColHeader = new SourceGrid.Cells.Views.ColumnHeader();
				viewColHeader.TextAlignment = DevAge.Drawing.ContentAlignment.MiddleCenter;
				viewColHeader.ImageAlignment = DevAge.Drawing.ContentAlignment.MiddleCenter;
			}
			#endregion

			public DataGridColumnAlternate(SourceGrid.DataGrid grid, 
				DataColumn dataColumn, 
				string caption,
				SourceGrid.Cells.ICellVirtual dataCell,
				Color alternateBackColor):base(grid)
			{
				DataColumn = dataColumn;
				
				HeaderCell = new SourceGrid.DataGridColumnHeader(caption);
				HeaderCell.View = viewColHeader;

				DataCell = dataCell;

				AlternateDataCell = dataCell.Copy();
				AlternateDataCell.View = (SourceGrid.Cells.Views.IView)DataCell.View.Clone();
				AlternateDataCell.View.BackColor = alternateBackColor;
			}

			private SourceGrid.Cells.ICellVirtual mAlternateDataCell;
			public SourceGrid.Cells.ICellVirtual AlternateDataCell
			{
				get{return mAlternateDataCell;}
				set{mAlternateDataCell = value;}
			}

			public override SourceGrid.Cells.ICellVirtual GetDataCell(int gridRow)
			{
				int reminder;
				Math.DivRem(gridRow, 2, out reminder);
				if (reminder == 0)
					return DataCell;
				else
					return AlternateDataCell;
			}
		}

		private class LinkClickDelete : SourceGrid.Cells.Controllers.ControllerBase
		{
			public override void OnClick(SourceGrid.CellContext sender, EventArgs e)
			{
				base.OnClick (sender, e);

				SourceGrid.DataGrid grid = (SourceGrid.DataGrid)sender.Grid;
				grid.DeleteSelectedRows();
			}
		}
	}
}