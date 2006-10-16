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
	[Sample("SourceGrid - Generic Samples", 1, "Real Grid generic features")]
	public class frmSample1 : System.Windows.Forms.Form
	{
		private System.Windows.Forms.Button brAddRow;
		private System.Windows.Forms.Button btRemoveRow;
		private System.Windows.Forms.Panel panel1;
		private SourceGrid.Grid grid1;
		private System.Windows.Forms.Button btMoveColumn;
		private System.Windows.Forms.Button btMoveRow;
		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.Button btExportHTML;
		private System.Windows.Forms.CheckBox chkReadOnly;
		private System.Windows.Forms.CheckBox chkEditOnDoubleClick;
		private System.Windows.Forms.GroupBox groupBox1;
		private System.Windows.Forms.RadioButton rdNromalBorder;
		private System.Windows.Forms.RadioButton rdLine;
		private System.Windows.Forms.RadioButton rdNone;
		private System.Windows.Forms.TabControl tabControl1;
		private System.Windows.Forms.TabPage tabPage1;
		private System.Windows.Forms.TabPage tabPage2;
		private System.Windows.Forms.CheckBox chkThemedHeaders;
        private Button btExportCsv;
        private Button btExportImage;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public frmSample1()
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
            this.brAddRow = new System.Windows.Forms.Button();
            this.btRemoveRow = new System.Windows.Forms.Button();
            this.panel1 = new System.Windows.Forms.Panel();
            this.grid1 = new SourceGrid.Grid();
            this.btMoveColumn = new System.Windows.Forms.Button();
            this.btMoveRow = new System.Windows.Forms.Button();
            this.label1 = new System.Windows.Forms.Label();
            this.btExportHTML = new System.Windows.Forms.Button();
            this.chkReadOnly = new System.Windows.Forms.CheckBox();
            this.chkEditOnDoubleClick = new System.Windows.Forms.CheckBox();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.rdNone = new System.Windows.Forms.RadioButton();
            this.rdLine = new System.Windows.Forms.RadioButton();
            this.rdNromalBorder = new System.Windows.Forms.RadioButton();
            this.chkThemedHeaders = new System.Windows.Forms.CheckBox();
            this.tabControl1 = new System.Windows.Forms.TabControl();
            this.tabPage1 = new System.Windows.Forms.TabPage();
            this.tabPage2 = new System.Windows.Forms.TabPage();
            this.btExportCsv = new System.Windows.Forms.Button();
            this.btExportImage = new System.Windows.Forms.Button();
            this.panel1.SuspendLayout();
            this.groupBox1.SuspendLayout();
            this.tabControl1.SuspendLayout();
            this.tabPage1.SuspendLayout();
            this.tabPage2.SuspendLayout();
            this.SuspendLayout();
            // 
            // brAddRow
            // 
            this.brAddRow.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.brAddRow.Location = new System.Drawing.Point(8, 8);
            this.brAddRow.Name = "brAddRow";
            this.brAddRow.Size = new System.Drawing.Size(84, 23);
            this.brAddRow.TabIndex = 1;
            this.brAddRow.Text = "AddRow";
            this.brAddRow.Click += new System.EventHandler(this.brAddRow_Click);
            // 
            // btRemoveRow
            // 
            this.btRemoveRow.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.btRemoveRow.Location = new System.Drawing.Point(96, 8);
            this.btRemoveRow.Name = "btRemoveRow";
            this.btRemoveRow.Size = new System.Drawing.Size(84, 23);
            this.btRemoveRow.TabIndex = 2;
            this.btRemoveRow.Text = "RemoveRow";
            this.btRemoveRow.Click += new System.EventHandler(this.btRemoveRow_Click);
            // 
            // panel1
            // 
            this.panel1.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.panel1.Controls.Add(this.grid1);
            this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.panel1.Location = new System.Drawing.Point(0, 0);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(740, 278);
            this.panel1.TabIndex = 3;
            // 
            // grid1
            // 
            this.grid1.BackColor = System.Drawing.Color.White;
            this.grid1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.grid1.GridToolTipActive = true;
            this.grid1.Location = new System.Drawing.Point(0, 0);
            this.grid1.Name = "grid1";
            this.grid1.Size = new System.Drawing.Size(738, 276);
            this.grid1.SpecialKeys = ((SourceGrid.GridSpecialKeys)(((((((SourceGrid.GridSpecialKeys.Arrows | SourceGrid.GridSpecialKeys.Tab)
                        | SourceGrid.GridSpecialKeys.PageDownUp)
                        | SourceGrid.GridSpecialKeys.Enter)
                        | SourceGrid.GridSpecialKeys.Escape)
                        | SourceGrid.GridSpecialKeys.Control)
                        | SourceGrid.GridSpecialKeys.Shift)));
            this.grid1.StyleGrid = null;
            this.grid1.TabIndex = 1;
            // 
            // btMoveColumn
            // 
            this.btMoveColumn.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.btMoveColumn.Location = new System.Drawing.Point(300, 8);
            this.btMoveColumn.Name = "btMoveColumn";
            this.btMoveColumn.Size = new System.Drawing.Size(84, 23);
            this.btMoveColumn.TabIndex = 4;
            this.btMoveColumn.Text = "Move Column";
            this.btMoveColumn.Click += new System.EventHandler(this.btMoveColumn_Click);
            // 
            // btMoveRow
            // 
            this.btMoveRow.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.btMoveRow.Location = new System.Drawing.Point(208, 8);
            this.btMoveRow.Name = "btMoveRow";
            this.btMoveRow.Size = new System.Drawing.Size(84, 23);
            this.btMoveRow.TabIndex = 5;
            this.btMoveRow.Text = "Move Row";
            this.btMoveRow.Click += new System.EventHandler(this.btMoveRow_Click);
            // 
            // label1
            // 
            this.label1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.label1.Location = new System.Drawing.Point(8, 368);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(192, 23);
            this.label1.TabIndex = 6;
            this.label1.Text = "Click column header to sort the grid";
            // 
            // btExportHTML
            // 
            this.btExportHTML.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.btExportHTML.Location = new System.Drawing.Point(416, 8);
            this.btExportHTML.Name = "btExportHTML";
            this.btExportHTML.Size = new System.Drawing.Size(84, 23);
            this.btExportHTML.TabIndex = 7;
            this.btExportHTML.Text = "&Export HTML";
            this.btExportHTML.Click += new System.EventHandler(this.btExportHTML_Click);
            // 
            // chkReadOnly
            // 
            this.chkReadOnly.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.chkReadOnly.Location = new System.Drawing.Point(140, 64);
            this.chkReadOnly.Name = "chkReadOnly";
            this.chkReadOnly.Size = new System.Drawing.Size(128, 20);
            this.chkReadOnly.TabIndex = 8;
            this.chkReadOnly.Text = "Read Only Cells";
            this.chkReadOnly.CheckedChanged += new System.EventHandler(this.chkReadOnly_CheckedChanged);
            // 
            // chkEditOnDoubleClick
            // 
            this.chkEditOnDoubleClick.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.chkEditOnDoubleClick.Location = new System.Drawing.Point(140, 80);
            this.chkEditOnDoubleClick.Name = "chkEditOnDoubleClick";
            this.chkEditOnDoubleClick.Size = new System.Drawing.Size(128, 20);
            this.chkEditOnDoubleClick.TabIndex = 9;
            this.chkEditOnDoubleClick.Text = "Edit On Double Click";
            this.chkEditOnDoubleClick.CheckedChanged += new System.EventHandler(this.chkEditOnDoubleClick_CheckedChanged);
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.rdNone);
            this.groupBox1.Controls.Add(this.rdLine);
            this.groupBox1.Controls.Add(this.rdNromalBorder);
            this.groupBox1.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.groupBox1.Location = new System.Drawing.Point(12, 16);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(276, 36);
            this.groupBox1.TabIndex = 10;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Border Style";
            // 
            // rdNone
            // 
            this.rdNone.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.rdNone.Location = new System.Drawing.Point(188, 16);
            this.rdNone.Name = "rdNone";
            this.rdNone.Size = new System.Drawing.Size(68, 16);
            this.rdNone.TabIndex = 2;
            this.rdNone.Text = "None";
            this.rdNone.CheckedChanged += new System.EventHandler(this.rdNone_CheckedChanged);
            // 
            // rdLine
            // 
            this.rdLine.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.rdLine.Location = new System.Drawing.Point(96, 16);
            this.rdLine.Name = "rdLine";
            this.rdLine.Size = new System.Drawing.Size(68, 16);
            this.rdLine.TabIndex = 1;
            this.rdLine.Text = "Line";
            this.rdLine.CheckedChanged += new System.EventHandler(this.rdLine_CheckedChanged);
            // 
            // rdNromalBorder
            // 
            this.rdNromalBorder.Checked = true;
            this.rdNromalBorder.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.rdNromalBorder.Location = new System.Drawing.Point(12, 16);
            this.rdNromalBorder.Name = "rdNromalBorder";
            this.rdNromalBorder.Size = new System.Drawing.Size(80, 16);
            this.rdNromalBorder.TabIndex = 0;
            this.rdNromalBorder.TabStop = true;
            this.rdNromalBorder.Text = "Normal";
            this.rdNromalBorder.CheckedChanged += new System.EventHandler(this.rdNromalBorder_CheckedChanged);
            // 
            // chkThemedHeaders
            // 
            this.chkThemedHeaders.Checked = true;
            this.chkThemedHeaders.CheckState = System.Windows.Forms.CheckState.Checked;
            this.chkThemedHeaders.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.chkThemedHeaders.Location = new System.Drawing.Point(16, 64);
            this.chkThemedHeaders.Name = "chkThemedHeaders";
            this.chkThemedHeaders.Size = new System.Drawing.Size(120, 20);
            this.chkThemedHeaders.TabIndex = 11;
            this.chkThemedHeaders.Text = "Themed Headers";
            this.chkThemedHeaders.CheckedChanged += new System.EventHandler(this.chkThemedHeaders_CheckedChanged);
            // 
            // tabControl1
            // 
            this.tabControl1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.tabControl1.Controls.Add(this.tabPage1);
            this.tabControl1.Controls.Add(this.tabPage2);
            this.tabControl1.Location = new System.Drawing.Point(8, 48);
            this.tabControl1.Name = "tabControl1";
            this.tabControl1.SelectedIndex = 0;
            this.tabControl1.Size = new System.Drawing.Size(748, 304);
            this.tabControl1.TabIndex = 12;
            // 
            // tabPage1
            // 
            this.tabPage1.Controls.Add(this.panel1);
            this.tabPage1.Location = new System.Drawing.Point(4, 22);
            this.tabPage1.Name = "tabPage1";
            this.tabPage1.Size = new System.Drawing.Size(740, 278);
            this.tabPage1.TabIndex = 0;
            this.tabPage1.Text = "Grid";
            // 
            // tabPage2
            // 
            this.tabPage2.Controls.Add(this.groupBox1);
            this.tabPage2.Controls.Add(this.chkEditOnDoubleClick);
            this.tabPage2.Controls.Add(this.chkReadOnly);
            this.tabPage2.Controls.Add(this.chkThemedHeaders);
            this.tabPage2.Location = new System.Drawing.Point(4, 22);
            this.tabPage2.Name = "tabPage2";
            this.tabPage2.Size = new System.Drawing.Size(740, 278);
            this.tabPage2.TabIndex = 1;
            this.tabPage2.Text = "Style and Options";
            // 
            // btExportCsv
            // 
            this.btExportCsv.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.btExportCsv.Location = new System.Drawing.Point(506, 8);
            this.btExportCsv.Name = "btExportCsv";
            this.btExportCsv.Size = new System.Drawing.Size(84, 23);
            this.btExportCsv.TabIndex = 13;
            this.btExportCsv.Text = "Export CSV";
            this.btExportCsv.Click += new System.EventHandler(this.btExportCsv_Click);
            // 
            // btExportImage
            // 
            this.btExportImage.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.btExportImage.Location = new System.Drawing.Point(596, 8);
            this.btExportImage.Name = "btExportImage";
            this.btExportImage.Size = new System.Drawing.Size(84, 23);
            this.btExportImage.TabIndex = 14;
            this.btExportImage.Text = "Export Image";
            this.btExportImage.Click += new System.EventHandler(this.btExportImage_Click);
            // 
            // frmSample1
            // 
            this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
            this.ClientSize = new System.Drawing.Size(764, 395);
            this.Controls.Add(this.btExportImage);
            this.Controls.Add(this.btExportCsv);
            this.Controls.Add(this.tabControl1);
            this.Controls.Add(this.btExportHTML);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.btMoveRow);
            this.Controls.Add(this.btMoveColumn);
            this.Controls.Add(this.btRemoveRow);
            this.Controls.Add(this.brAddRow);
            this.Name = "frmSample1";
            this.Text = "Sample Grid 1";
            this.Load += new System.EventHandler(this.frmSampleGrid1_Load);
            this.panel1.ResumeLayout(false);
            this.groupBox1.ResumeLayout(false);
            this.tabControl1.ResumeLayout(false);
            this.tabPage1.ResumeLayout(false);
            this.tabPage2.ResumeLayout(false);
            this.ResumeLayout(false);

		}
		#endregion

		//Editors
		private SourceGrid.Cells.Editors.EditorBase mEditor_Id;
		private SourceGrid.Cells.Editors.EditorBase mEditor_Name;
		private SourceGrid.Cells.Editors.EditorBase mEditor_Address;
		private SourceGrid.Cells.Editors.EditorBase mEditor_City;
		private SourceGrid.Cells.Editors.EditorBase mEditor_BirthDay;
		private SourceGrid.Cells.Editors.EditorBase mEditor_Country;
		private SourceGrid.Cells.Editors.EditorBase mEditor_Price;

		//Views
		private SourceGrid.Cells.Views.Cell mView_Default;
		private SourceGrid.Cells.Views.Cell mView_Price;
		private SourceGrid.Cells.Views.Cell mView_CheckBox;
		private SourceGrid.Cells.Views.Cell mView_Link;
		private SourceGrid.Cells.Views.Header mView_RowHeader;
		private SourceGrid.Cells.Views.Header mView_ColumnHeader;
		private SourceGrid.Cells.Views.Header mView_Header;

		//Controllers
		private SourceGrid.Cells.Controllers.Button mController_Link;

		private void frmSampleGrid1_Load(object sender, System.EventArgs e)
		{
			string[] l_CountryList = new string[]{"Italy","France","Spain","UK","Argentina","Mexico", "Switzerland", "Brazil", "Germany","Portugal","Sweden","Austria"};

			grid1.RowsCount = 1;
			grid1.ColumnsCount = 10;
			grid1.FixedRows = 1;
			grid1.FixedColumns = 1;
			grid1.Selection.SelectionMode = SourceGrid.GridSelectionMode.Row;
			grid1.AutoStretchColumnsToFitWidth = true;
			grid1.Columns[0].AutoSizeMode = SourceGrid.AutoSizeMode.None;
			grid1.Columns[0].Width = 25;
			
			//Enable Drag and Drop
			grid1.GridController.AddController(SourceGrid.Controllers.SelectionDrag.Cut);
			grid1.GridController.AddController(SourceGrid.Controllers.SelectionDrop.Default);


			#region Create Grid Style, Views and Controllers
			//Views
			mView_Default = new SourceGrid.Cells.Views.Cell();

			mView_Price = new SourceGrid.Cells.Views.Cell();
			mView_Price.TextAlignment = DevAge.Drawing.ContentAlignment.MiddleRight;

			mView_CheckBox = new SourceGrid.Cells.Views.CheckBox();

			mView_Link = new SourceGrid.Cells.Views.Link();

			mView_RowHeader = new SourceGrid.Cells.Views.RowHeader();
			mView_ColumnHeader = new SourceGrid.Cells.Views.ColumnHeader();
			mView_Header = new SourceGrid.Cells.Views.Header();

			//Style
			SourceGrid.Styles.StyleGrid style = new SourceGrid.Styles.StyleGrid();
			style.StyleCells.Add(new SourceGrid.Styles.StyleCell(null, null, typeof(string), mView_Default));
			style.StyleCells.Add(new SourceGrid.Styles.StyleCell(null, null, typeof(DateTime), mView_Default));
			style.StyleCells.Add(new SourceGrid.Styles.StyleCell(null, null, typeof(int), mView_Default));
			style.StyleCells.Add(new SourceGrid.Styles.StyleCell(null, null, typeof(Boolean), mView_CheckBox));
			style.StyleCells.Add(new SourceGrid.Styles.StyleCell(null, null, typeof(double), mView_Price));
			style.StyleCells.Add(new SourceGrid.Styles.StyleCell(typeof(SourceGrid.Cells.Link), null, null, mView_Link));
			style.StyleCells.Add(new SourceGrid.Styles.StyleCell(typeof(SourceGrid.Cells.Header), null, null, mView_Header));
			style.StyleCells.Add(new SourceGrid.Styles.StyleCell(typeof(SourceGrid.Cells.ColumnHeader), null, null, mView_ColumnHeader));
			style.StyleCells.Add(new SourceGrid.Styles.StyleCell(typeof(SourceGrid.Cells.RowHeader), null, null, mView_RowHeader));
			grid1.StyleGrid = style;

			mController_Link = new SourceGrid.Cells.Controllers.Button();
			mController_Link.Executed += new EventHandler(mController_Link_Click);
			#endregion

			#region Create Header Row and Editor
			SourceGrid.Cells.Header l_00Header = new SourceGrid.Cells.Header(null);
			grid1[0,0] = l_00Header;

            mEditor_Id = SourceGrid.Cells.Editors.Factory.Create(typeof(int));
			mEditor_Id.EditableMode = SourceGrid.EditableMode.Focus|SourceGrid.EditableMode.AnyKey|SourceGrid.EditableMode.SingleClick;
			grid1[0,1] = new SourceGrid.Cells.ColumnHeader("ID (int)");

            mEditor_Name = SourceGrid.Cells.Editors.Factory.Create(typeof(string));
			mEditor_Name.EditableMode = SourceGrid.EditableMode.Focus|SourceGrid.EditableMode.AnyKey|SourceGrid.EditableMode.SingleClick;
			grid1[0,2] = new SourceGrid.Cells.ColumnHeader("NAME (string)");

            mEditor_Address = SourceGrid.Cells.Editors.Factory.Create(typeof(string));
			mEditor_Address.EditableMode = SourceGrid.EditableMode.Focus|SourceGrid.EditableMode.AnyKey|SourceGrid.EditableMode.SingleClick;
			grid1[0,3] = new SourceGrid.Cells.ColumnHeader("ADDRESS (string)");

            mEditor_City = SourceGrid.Cells.Editors.Factory.Create(typeof(string));
			mEditor_City.EditableMode = SourceGrid.EditableMode.Focus|SourceGrid.EditableMode.AnyKey|SourceGrid.EditableMode.SingleClick;
			grid1[0,4] = new SourceGrid.Cells.ColumnHeader("CITY (string)");

			mEditor_BirthDay = SourceGrid.Cells.Editors.Factory.Create(typeof(DateTime));
			mEditor_BirthDay.EditableMode = SourceGrid.EditableMode.Focus|SourceGrid.EditableMode.AnyKey|SourceGrid.EditableMode.SingleClick;
			grid1[0,5] = new SourceGrid.Cells.ColumnHeader("BIRTHDATE (DateTime)");

			mEditor_Country = new SourceGrid.Cells.Editors.ComboBox(typeof(string),l_CountryList,false);
			mEditor_Country.EditableMode = SourceGrid.EditableMode.Focus|SourceGrid.EditableMode.AnyKey|SourceGrid.EditableMode.SingleClick;
			grid1[0,6] = new SourceGrid.Cells.ColumnHeader("COUNTRY (string + combobox)");

			mEditor_Price = new SourceGrid.Cells.Editors.TextBoxCurrency(typeof(double));
			mEditor_Price.EditableMode = SourceGrid.EditableMode.Focus|SourceGrid.EditableMode.AnyKey|SourceGrid.EditableMode.SingleClick;
			grid1[0,7] = new SourceGrid.Cells.ColumnHeader("$ PRICE (double)");

			grid1[0,8] = new SourceGrid.Cells.ColumnHeader("Selected");

			grid1[0,9] = new SourceGrid.Cells.ColumnHeader("WebSite");
			#endregion

			//Read Data From xml
			System.IO.StreamReader reader = new System.IO.StreamReader(System.Reflection.Assembly.GetExecutingAssembly().GetManifestResourceStream("WindowsFormsSample.GridSamples.SampleData.xml"));
			System.Xml.XmlDocument xmlDoc = new System.Xml.XmlDocument();
			xmlDoc.LoadXml(reader.ReadToEnd());
			reader.Close();
			System.Xml.XmlNodeList rows = xmlDoc.SelectNodes("//row");
			grid1.RowsCount = rows.Count+1;
			int rowsCount = 1;
			foreach(System.Xml.XmlNode l_Node in rows)
			{
				#region Pupulate RowsCount
				grid1[rowsCount,0] = new SourceGrid.Cells.RowHeader(null);

				grid1[rowsCount,1] = new SourceGrid.Cells.Cell(rowsCount, mEditor_Id);

				grid1[rowsCount,2] = new SourceGrid.Cells.Cell(l_Node.Attributes["ContactName"].InnerText, mEditor_Name);

				grid1[rowsCount,3] = new SourceGrid.Cells.Cell(l_Node.Attributes["Address"].InnerText, mEditor_Address);

				grid1[rowsCount,4] = new SourceGrid.Cells.Cell(l_Node.Attributes["City"].InnerText, mEditor_City);

				grid1[rowsCount,5] = new SourceGrid.Cells.Cell(DateTime.Today, mEditor_BirthDay);

				grid1[rowsCount,6] = new SourceGrid.Cells.Cell(l_Node.Attributes["Country"].InnerText, mEditor_Country);

				grid1[rowsCount,7] = new SourceGrid.Cells.Cell(25.0, mEditor_Price);

				grid1[rowsCount,8] = new SourceGrid.Cells.CheckBox(null, false);

				grid1[rowsCount,9] = new SourceGrid.Cells.Link(l_Node.Attributes["website"].InnerText);
				grid1[rowsCount,9].AddController(mController_Link);
				#endregion

				rowsCount++;
			}

            grid1.AutoSizeCells();
		}

		private void brAddRow_Click(object sender, System.EventArgs e)
		{
			SourceGrid.Cells.Link link = new SourceGrid.Cells.Link("http://www.codeproject.com");
			link.AddController(mController_Link);

			grid1.Rows.Insert(grid1.RowsCount,
				new SourceGrid.Cells.RowHeader(null),
				new SourceGrid.Cells.Cell(grid1.RowsCount, mEditor_Id),
				new SourceGrid.Cells.Cell(mEditor_Name.DefaultValue, mEditor_Name),
				new SourceGrid.Cells.Cell(mEditor_Address.DefaultValue,mEditor_Address),
				new SourceGrid.Cells.Cell(mEditor_City.DefaultValue,mEditor_City),
				new SourceGrid.Cells.Cell(mEditor_BirthDay.DefaultValue, mEditor_BirthDay),
				new SourceGrid.Cells.Cell(mEditor_Country.DefaultValue, mEditor_Country),
				new SourceGrid.Cells.Cell(mEditor_Price.DefaultValue, mEditor_Price),
				new SourceGrid.Cells.CheckBox(null, false),
				link);

			grid1.Selection.FocusRow(grid1.RowsCount-1);
		}

		private void btRemoveRow_Click(object sender, System.EventArgs e)
		{
			int[] rowsIndex = grid1.Selection.GetRowsIndex();
			SourceGrid.RowInfo[] rows = new SourceGrid.RowInfo[rowsIndex.Length];
			for (int i = 0; i < rows.Length; i++)
				rows[i] = grid1.Rows[rowsIndex[i]];

			foreach (SourceGrid.RowInfo r in rows)
				grid1.Rows.Remove(r.Index);

			if (grid1.RowsCount > 1)
				grid1.Selection.FocusRow(1);
		}

		private void btMoveRow_Click(object sender, System.EventArgs e)
		{
			if (grid1.RowsCount>1)
				grid1.Rows.Move(grid1.RowsCount-1,1);
		}

		private void btMoveColumn_Click(object sender, System.EventArgs e)
		{
			brAddRow.Enabled = false; //disable the add button to prevent adding new row when column are out of position
			if (grid1.ColumnsCount>1)
				grid1.Columns.Move(grid1.ColumnsCount-1,1);
		}

		private void btExportHTML_Click(object sender, System.EventArgs e)
		{
			try
			{
				string l_Path = System.IO.Path.Combine(System.IO.Path.GetTempPath(), "tmpSourceGridExport.htm");

				using (System.IO.FileStream l_Stream = new System.IO.FileStream(l_Path,System.IO.FileMode.Create,System.IO.FileAccess.Write))
				{
					SourceGrid.Exporter.HTML html = new SourceGrid.Exporter.HTML(SourceGrid.Exporter.ExportHTMLMode.Default, System.IO.Path.GetTempPath(), "", l_Stream);
					html.Export(grid1);
					l_Stream.Close();
				}

				DevAge.Shell.Utilities.OpenFile(l_Path);
			}
			catch(Exception err)
			{
				DevAge.Windows.Forms.ErrorDialog.Show(this,err,"HTML Export Error");
			}
		}

		private void chkReadOnly_CheckedChanged(object sender, System.EventArgs e)
		{
			for (int r = 0; r < grid1.RowsCount; r++)
				for (int c = 0; c < grid1.ColumnsCount; c++)
				{
					if (grid1[r,c].Editor != null)
						grid1[r,c].Editor.EnableEdit = !chkReadOnly.Checked;
				}
		}

		private void mController_Link_Click(object sender, EventArgs e)
		{
			try
			{
				SourceGrid.CellContext context = (SourceGrid.CellContext)sender;
				DevAge.Shell.Utilities.OpenFile( ((SourceGrid.Cells.Link)context.Cell).Value.ToString());
			}
			catch(Exception)
			{
			}
		}

		private void chkEditOnDoubleClick_CheckedChanged(object sender, System.EventArgs e)
		{
			if (chkEditOnDoubleClick.Checked)
			{
				mEditor_Id.EditableMode = SourceGrid.EditableMode.AnyKey | SourceGrid.EditableMode.DoubleClick | SourceGrid.EditableMode.F2Key;
				mEditor_Name.EditableMode = SourceGrid.EditableMode.AnyKey | SourceGrid.EditableMode.DoubleClick | SourceGrid.EditableMode.F2Key;
				mEditor_Address.EditableMode = SourceGrid.EditableMode.AnyKey | SourceGrid.EditableMode.DoubleClick | SourceGrid.EditableMode.F2Key;
				mEditor_City.EditableMode = SourceGrid.EditableMode.AnyKey | SourceGrid.EditableMode.DoubleClick | SourceGrid.EditableMode.F2Key;
				mEditor_BirthDay.EditableMode = SourceGrid.EditableMode.AnyKey | SourceGrid.EditableMode.DoubleClick | SourceGrid.EditableMode.F2Key;
				mEditor_Country.EditableMode = SourceGrid.EditableMode.AnyKey | SourceGrid.EditableMode.DoubleClick | SourceGrid.EditableMode.F2Key;
				mEditor_Price.EditableMode = SourceGrid.EditableMode.AnyKey | SourceGrid.EditableMode.DoubleClick | SourceGrid.EditableMode.F2Key;
			}
			else
			{
				mEditor_Id.EditableMode = SourceGrid.EditableMode.Focus|SourceGrid.EditableMode.AnyKey|SourceGrid.EditableMode.SingleClick;
				mEditor_Name.EditableMode = SourceGrid.EditableMode.Focus|SourceGrid.EditableMode.AnyKey|SourceGrid.EditableMode.SingleClick;
				mEditor_Address.EditableMode = SourceGrid.EditableMode.Focus|SourceGrid.EditableMode.AnyKey|SourceGrid.EditableMode.SingleClick;
				mEditor_City.EditableMode = SourceGrid.EditableMode.Focus|SourceGrid.EditableMode.AnyKey|SourceGrid.EditableMode.SingleClick;
				mEditor_BirthDay.EditableMode = SourceGrid.EditableMode.Focus|SourceGrid.EditableMode.AnyKey|SourceGrid.EditableMode.SingleClick;
				mEditor_Country.EditableMode = SourceGrid.EditableMode.Focus|SourceGrid.EditableMode.AnyKey|SourceGrid.EditableMode.SingleClick;
				mEditor_Price.EditableMode = SourceGrid.EditableMode.Focus|SourceGrid.EditableMode.AnyKey|SourceGrid.EditableMode.SingleClick;
			}
		}

		private void rdNromalBorder_CheckedChanged(object sender, System.EventArgs e)
		{
			SetBorder();
		}

		private void rdLine_CheckedChanged(object sender, System.EventArgs e)
		{
			SetBorder();
		}
		private void rdNone_CheckedChanged(object sender, System.EventArgs e)
		{
			SetBorder();
		}
		private void SetBorder()
		{
			if (rdLine.Checked)
			{
				mView_Default.Border = new DevAge.Drawing.RectangleBorder(new DevAge.Drawing.Border(Color.Black,0), new DevAge.Drawing.Border(Color.Black,1));
				mView_CheckBox.Border = mView_Default.Border;
				mView_Price.Border = mView_Default.Border;
				mView_Link.Border = mView_Default.Border;
			}
			else if (rdNromalBorder.Checked)
			{
				mView_Default.Border = SourceGrid.Cells.Views.ViewBase.DefaultBorder;
				mView_CheckBox.Border = SourceGrid.Cells.Views.ViewBase.DefaultBorder;
				mView_Price.Border = SourceGrid.Cells.Views.ViewBase.DefaultBorder;
				mView_Link.Border = SourceGrid.Cells.Views.ViewBase.DefaultBorder;
			}
			else
			{
				mView_Default.Border = DevAge.Drawing.RectangleBorder.NoBorder;
				mView_CheckBox.Border = DevAge.Drawing.RectangleBorder.NoBorder;
				mView_Price.Border = DevAge.Drawing.RectangleBorder.NoBorder;
				mView_Link.Border = DevAge.Drawing.RectangleBorder.NoBorder;
			}
			grid1.InvalidateCells();
		}

		private void chkThemedHeaders_CheckedChanged(object sender, System.EventArgs e)
		{
            if (chkThemedHeaders.Checked)
            {
                mView_ColumnHeader.Background = new DevAge.Drawing.VisualElements.ColumnHeaderThemed();
                mView_RowHeader.Background = new DevAge.Drawing.VisualElements.RowHeaderThemed();
                mView_Header.Background = new DevAge.Drawing.VisualElements.HeaderThemed();
            }
            else
            {
                mView_ColumnHeader.Background = new DevAge.Drawing.VisualElements.ColumnHeader();
                mView_RowHeader.Background = new DevAge.Drawing.VisualElements.RowHeader();
                mView_Header.Background = new DevAge.Drawing.VisualElements.Header();
            }
		}

        private void btExportCsv_Click(object sender, EventArgs e)
        {
            try
            {
                string l_Path = System.IO.Path.Combine(System.IO.Path.GetTempPath(), "CsvFile.csv");

                using (System.IO.StreamWriter writer = new System.IO.StreamWriter(l_Path, false, System.Text.Encoding.Default))
                {
                    SourceGrid.Exporter.CSV csv = new SourceGrid.Exporter.CSV();
                    csv.Export(grid1, writer);
                    writer.Close();
                }

                DevAge.Shell.Utilities.OpenFile(l_Path);
            }
            catch (Exception err)
            {
                DevAge.Windows.Forms.ErrorDialog.Show(this, err, "CSV Export Error");
            }
        }

        private void btExportImage_Click(object sender, EventArgs e)
        {
            try
            {
                string l_Path = System.IO.Path.Combine(System.IO.Path.GetTempPath(), "ImageFile.bmp");

                SourceGrid.Exporter.Image image = new SourceGrid.Exporter.Image();

                using (System.Drawing.Bitmap bitmap = image.Export(grid1, grid1.CompleteRange))
                {
                    bitmap.Save(l_Path);
                }

                DevAge.Shell.Utilities.OpenFile(l_Path);
            }
            catch (Exception err)
            {
                DevAge.Windows.Forms.ErrorDialog.Show(this, err, "BITMAP Export Error");
            }
        }
	}
}
