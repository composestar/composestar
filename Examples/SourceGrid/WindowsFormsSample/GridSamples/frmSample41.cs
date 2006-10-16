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
	[Sample("SourceGrid - Extensions", 41, "Advanced Data Binding - different Views, images and tooltip")]
	public class frmSample41 : System.Windows.Forms.Form
	{
		private SourceGrid.DataGrid dataGrid;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public frmSample41()
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
			this.dataGrid.Location = new System.Drawing.Point(4, 4);
			this.dataGrid.Name = "dataGrid";
			this.dataGrid.Size = new System.Drawing.Size(492, 231);
			this.dataGrid.SpecialKeys = SourceGrid.GridSpecialKeys.Default;
			this.dataGrid.StyleGrid = null;
			this.dataGrid.TabIndex = 15;
			// 
			// frmSample41
			// 
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.ClientSize = new System.Drawing.Size(500, 242);
			this.Controls.Add(this.dataGrid);
			this.Name = "frmSample41";
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
			System.IO.Stream stream = System.Reflection.Assembly.GetExecutingAssembly().GetManifestResourceStream("WindowsFormsSample.GridSamples.SampleData2.xml");
			ds.ReadXml(stream);
			mView = ds.Tables[0].DefaultView;
			mView.AllowDelete = false;
			mView.AllowNew = false;

			dataGrid.FixedColumns = 1;

			//Create default columns
			CreateColumns(dataGrid.Columns, mView.Table);

			dataGrid.DataSource = mView;

            dataGrid.AutoSizeCells();
		}

		private static void CreateColumns(SourceGrid.DataGridColumns columns, DataTable sourceTable)
		{
			SourceGrid.Cells.Editors.TextBoxNumeric numericEditor = new SourceGrid.Cells.Editors.TextBoxNumeric(typeof(decimal));
			numericEditor.TypeConverter = new DevAge.ComponentModel.Converter.NumberTypeConverter(typeof(decimal), "N");
			numericEditor.AllowNull = true;  //the database value can be null (System.DbNull)
			SourceGrid.Cells.Editors.TextBox stringEditor = new SourceGrid.Cells.Editors.TextBox(typeof(string));
			SourceGrid.Cells.Editors.ComboBox externalIdEditor = new SourceGrid.Cells.Editors.ComboBox(typeof(int));
			externalIdEditor.StandardValues = new int[]{1, 2, 3, 4};
			externalIdEditor.StandardValuesExclusive = true;
			DevAge.ComponentModel.Validator.ValueMapping mapping = new DevAge.ComponentModel.Validator.ValueMapping();
			mapping.ValueList = new int[]{1, 2, 3, 4};
			mapping.SpecialType = typeof(string);
			mapping.SpecialList = new string[]{"Reference 1", "Reference 2", "Reference 3", "Reference 4"};
			mapping.ThrowErrorIfNotFound = false;
			mapping.BindValidator(externalIdEditor);


			DataColumn dataColumn;
			SourceGrid.DataGridColumn gridColumn;
			SourceGrid.Cells.ICellVirtual dataCell;

			//Create columns

			dataColumn = sourceTable.Columns["Selected"];
			dataCell = new SourceGrid.Cells.DataGrid.CheckBox(dataColumn);
			gridColumn = new MyDataGridColumn(columns.Grid, dataColumn, dataColumn.Caption, dataCell);
			columns.Insert(columns.Count, gridColumn);

			dataColumn = sourceTable.Columns["Country"];
			dataCell = new SourceGrid.Cells.DataGrid.Cell(dataColumn);
			dataCell.Editor = stringEditor;
			gridColumn = new MyDataGridColumn(columns.Grid, dataColumn, dataColumn.Caption, dataCell);
			columns.Insert(columns.Count, gridColumn);

			dataColumn = sourceTable.Columns["Uniform"];
			dataCell = new SourceGrid.Cells.DataGrid.Image(dataColumn);
			gridColumn = new MyDataGridColumn(columns.Grid, dataColumn, dataColumn.Caption, dataCell);
			columns.Insert(columns.Count, gridColumn);

			dataColumn = sourceTable.Columns["Population"];
			dataCell = new SourceGrid.Cells.DataGrid.Cell(dataColumn);
			dataCell.Editor = numericEditor;
			gridColumn = new MyDataGridColumn(columns.Grid, dataColumn, dataColumn.Caption, dataCell);
			columns.Insert(columns.Count, gridColumn);

			dataColumn = sourceTable.Columns["Surface"];
			dataCell = new SourceGrid.Cells.DataGrid.Cell(dataColumn);
			dataCell.Editor = numericEditor;
			gridColumn = new MyDataGridColumn(columns.Grid, dataColumn, dataColumn.Caption, dataCell);
			columns.Insert(columns.Count, gridColumn);

			dataColumn = sourceTable.Columns["ExternalID"];
			dataCell = new SourceGrid.Cells.DataGrid.Cell(dataColumn);
			dataCell.Editor = externalIdEditor;
			gridColumn = new MyDataGridColumn(columns.Grid, dataColumn, dataColumn.Caption, dataCell);
			columns.Insert(columns.Count, gridColumn);

			gridColumn = new MyDataGridColumnStar(columns.Grid, "Star");
			columns.Insert(columns.Count, gridColumn);
		}

		private class MyDataGridColumn : SourceGrid.DataGridColumn
		{
			protected SourceGrid.Cells.ICellVirtual dataCellSelected;
			protected SourceGrid.Cells.ICellVirtual dataCellSelected_OFF;

			public MyDataGridColumn(SourceGrid.DataGrid grid, System.Data.DataColumn dataColumn, string caption, SourceGrid.Cells.ICellVirtual dataCell):base(grid)
			{
				HeaderCell = new SourceGrid.DataGridColumnHeader(caption);
				DataColumn = dataColumn;
				DataCell = null;

				if (dataCell != null)
				{
                    //Add a ToolTip
                    dataCell.AddController(SourceGrid.Cells.Controllers.ToolTipText.Default);
                    dataCell.Model.AddModel(MyToolTipModel.Default);

					dataCellSelected_OFF = dataCell;
					dataCellSelected = dataCell.Copy();
					dataCellSelected.View = (SourceGrid.Cells.Views.IView)dataCell.View.Clone();
					dataCellSelected.View.ForeColor = Color.DarkGreen;
					dataCellSelected.View.Font = new Font(grid.Font, FontStyle.Bold);
				}
			}

			public override SourceGrid.Cells.ICellVirtual GetDataCell(int gridRow)
			{
				System.Data.DataRowView row = Grid.Rows.IndexToDataSourceRow(gridRow);
				if (row["Selected"] is bool && (bool)row["Selected"] == true)
					return dataCellSelected;
				else
					return dataCellSelected_OFF;
			}		
		}

		private class MyDataGridColumnStar : MyDataGridColumn
		{
			public MyDataGridColumnStar(SourceGrid.DataGrid grid, string caption):base(grid, null, caption, null)
			{
				HeaderCell = new SourceGrid.DataGridColumnHeader(caption);
				DataColumn = null;
				DataCell = null;

				dataCellSelected = new SourceGrid.Cells.Virtual.CellVirtual();
				dataCellSelected.Model.AddModel(new SourceGrid.Cells.Models.Image(Properties.Resources.Star.ToBitmap()));
				dataCellSelected_OFF = new SourceGrid.Cells.Virtual.CellVirtual();
                dataCellSelected_OFF.Model.AddModel(new SourceGrid.Cells.Models.Image(Properties.Resources.StarOff));
			}
		}

        private class MyToolTipModel : SourceGrid.Cells.Models.IToolTipText
        {
            public static readonly MyToolTipModel Default = new MyToolTipModel();

            public string GetToolTipText(SourceGrid.CellContext cellContext)
            {
                SourceGrid.DataGrid grid = (SourceGrid.DataGrid)cellContext.Grid;
                DataRowView row = grid.Rows.IndexToDataSourceRow(cellContext.Position.Row);
                if (row != null)
                {
                    if (bool.Equals(row["Selected"], true))
                        return "Row " + cellContext.Position.Row.ToString() + " is selected";
                    else
                        return "Row " + cellContext.Position.Row.ToString() + " is NOT selected";
                }
                else
                    return string.Empty;
            }
        }
	}
}