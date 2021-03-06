using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;

namespace WindowsFormsSample
{
	/// <summary>
	/// Summary description for frmSample17.
	/// </summary>
	[Sample("SourceGrid - Standard features", 17, "Selection")]
	public class frmSample17 : System.Windows.Forms.Form
	{
		private SourceGrid.Grid grid1;
		private System.Windows.Forms.GroupBox groupBox1;
		private System.Windows.Forms.RadioButton rdSelectionModeCell;
		private System.Windows.Forms.RadioButton rdSelectionModeRow;
		private System.Windows.Forms.RadioButton rdSelectionModeColumn;
		private System.Windows.Forms.CheckBox chkEnableMultiSelection;
		private DevAge.Windows.Forms.ColorPicker cPickSelBackColor;
		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.Label label3;
		private DevAge.Windows.Forms.ColorPicker cPckBorderColor;
		private System.Windows.Forms.TabControl tabControl1;
		private System.Windows.Forms.TabPage tabPage1;
		private System.Windows.Forms.TabPage tabPage2;
		private System.Windows.Forms.TrackBar trackSelectionAlpha;
		private System.Windows.Forms.Label label2;
		private System.Windows.Forms.Label label4;
		private System.Windows.Forms.TrackBar trackBorderWidth;
		private System.Windows.Forms.TabPage tabPage3;
		private System.Windows.Forms.GroupBox groupBox2;
		private System.Windows.Forms.RadioButton rdNone;
		private System.Windows.Forms.RadioButton rdFocusCell;
		private System.Windows.Forms.Label label5;
		private System.Windows.Forms.Label label6;
		private System.Windows.Forms.TrackBar trackFocusBackColorTrans;
		private DevAge.Windows.Forms.ColorPicker cPickFocusBackColor;
		private System.Windows.Forms.TabPage tabPage4;
		private System.Windows.Forms.CheckBox chkDrawOnlyInitializedCells;
		private System.Windows.Forms.CheckBox chkDrawOverCells;
		private DevAge.Windows.Forms.DevAgeComboBox cbDashStyle;
		private System.Windows.Forms.Label label7;
		private System.Windows.Forms.RadioButton rdUniqueRange;
		private System.Windows.Forms.RadioButton rdFocusRange;
		private System.Windows.Forms.RadioButton rdAuto;
		private System.Windows.Forms.CheckBox chkTabStop;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;

		public frmSample17()
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
			this.groupBox1 = new System.Windows.Forms.GroupBox();
			this.rdSelectionModeColumn = new System.Windows.Forms.RadioButton();
			this.rdSelectionModeRow = new System.Windows.Forms.RadioButton();
			this.rdSelectionModeCell = new System.Windows.Forms.RadioButton();
			this.chkEnableMultiSelection = new System.Windows.Forms.CheckBox();
			this.cPickSelBackColor = new DevAge.Windows.Forms.ColorPicker();
			this.label1 = new System.Windows.Forms.Label();
			this.label3 = new System.Windows.Forms.Label();
			this.cPckBorderColor = new DevAge.Windows.Forms.ColorPicker();
			this.tabControl1 = new System.Windows.Forms.TabControl();
			this.tabPage1 = new System.Windows.Forms.TabPage();
			this.tabPage2 = new System.Windows.Forms.TabPage();
			this.chkDrawOverCells = new System.Windows.Forms.CheckBox();
			this.chkDrawOnlyInitializedCells = new System.Windows.Forms.CheckBox();
			this.label2 = new System.Windows.Forms.Label();
			this.trackSelectionAlpha = new System.Windows.Forms.TrackBar();
			this.tabPage4 = new System.Windows.Forms.TabPage();
			this.label6 = new System.Windows.Forms.Label();
			this.trackFocusBackColorTrans = new System.Windows.Forms.TrackBar();
			this.cPickFocusBackColor = new DevAge.Windows.Forms.ColorPicker();
			this.label5 = new System.Windows.Forms.Label();
			this.tabPage3 = new System.Windows.Forms.TabPage();
			this.label7 = new System.Windows.Forms.Label();
            this.cbDashStyle = new DevAge.Windows.Forms.DevAgeComboBox();
			this.groupBox2 = new System.Windows.Forms.GroupBox();
			this.rdAuto = new System.Windows.Forms.RadioButton();
			this.rdFocusCell = new System.Windows.Forms.RadioButton();
			this.rdUniqueRange = new System.Windows.Forms.RadioButton();
			this.rdFocusRange = new System.Windows.Forms.RadioButton();
			this.rdNone = new System.Windows.Forms.RadioButton();
			this.trackBorderWidth = new System.Windows.Forms.TrackBar();
			this.label4 = new System.Windows.Forms.Label();
			this.chkTabStop = new System.Windows.Forms.CheckBox();
			this.groupBox1.SuspendLayout();
			this.tabControl1.SuspendLayout();
			this.tabPage1.SuspendLayout();
			this.tabPage2.SuspendLayout();
			((System.ComponentModel.ISupportInitialize)(this.trackSelectionAlpha)).BeginInit();
			this.tabPage4.SuspendLayout();
			((System.ComponentModel.ISupportInitialize)(this.trackFocusBackColorTrans)).BeginInit();
			this.tabPage3.SuspendLayout();
			this.groupBox2.SuspendLayout();
			((System.ComponentModel.ISupportInitialize)(this.trackBorderWidth)).BeginInit();
			this.SuspendLayout();
			// 
			// grid1
			// 
			this.grid1.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
				| System.Windows.Forms.AnchorStyles.Left) 
				| System.Windows.Forms.AnchorStyles.Right)));
			this.grid1.AutoStretchColumnsToFitWidth = false;
			this.grid1.AutoStretchRowsToFitHeight = false;
			this.grid1.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
			this.grid1.CustomSort = false;
			this.grid1.GridToolTipActive = true;
			this.grid1.Location = new System.Drawing.Point(4, 184);
			this.grid1.Name = "grid1";
			this.grid1.OverrideCommonCmdKey = true;
			this.grid1.Size = new System.Drawing.Size(432, 180);
			this.grid1.SpecialKeys = SourceGrid.GridSpecialKeys.Default;
			this.grid1.StyleGrid = null;
			this.grid1.TabIndex = 0;
			// 
			// groupBox1
			// 
			this.groupBox1.Controls.Add(this.rdSelectionModeColumn);
			this.groupBox1.Controls.Add(this.rdSelectionModeRow);
			this.groupBox1.Controls.Add(this.rdSelectionModeCell);
			this.groupBox1.Location = new System.Drawing.Point(20, 8);
			this.groupBox1.Name = "groupBox1";
			this.groupBox1.Size = new System.Drawing.Size(200, 92);
			this.groupBox1.TabIndex = 1;
			this.groupBox1.TabStop = false;
			this.groupBox1.Text = "Grid.Selection.SelectionMode";
			// 
			// rdSelectionModeColumn
			// 
			this.rdSelectionModeColumn.Location = new System.Drawing.Point(8, 68);
			this.rdSelectionModeColumn.Name = "rdSelectionModeColumn";
			this.rdSelectionModeColumn.Size = new System.Drawing.Size(188, 20);
			this.rdSelectionModeColumn.TabIndex = 2;
			this.rdSelectionModeColumn.Text = "Column";
			this.rdSelectionModeColumn.CheckedChanged += new System.EventHandler(this.Check_Change);
			// 
			// rdSelectionModeRow
			// 
			this.rdSelectionModeRow.Location = new System.Drawing.Point(8, 44);
			this.rdSelectionModeRow.Name = "rdSelectionModeRow";
			this.rdSelectionModeRow.Size = new System.Drawing.Size(188, 20);
			this.rdSelectionModeRow.TabIndex = 1;
			this.rdSelectionModeRow.Text = "Row";
			this.rdSelectionModeRow.CheckedChanged += new System.EventHandler(this.Check_Change);
			// 
			// rdSelectionModeCell
			// 
			this.rdSelectionModeCell.Checked = true;
			this.rdSelectionModeCell.Location = new System.Drawing.Point(8, 20);
			this.rdSelectionModeCell.Name = "rdSelectionModeCell";
			this.rdSelectionModeCell.Size = new System.Drawing.Size(188, 20);
			this.rdSelectionModeCell.TabIndex = 0;
			this.rdSelectionModeCell.TabStop = true;
			this.rdSelectionModeCell.Text = "Cell";
			this.rdSelectionModeCell.CheckedChanged += new System.EventHandler(this.Check_Change);
			// 
			// chkEnableMultiSelection
			// 
			this.chkEnableMultiSelection.Checked = true;
			this.chkEnableMultiSelection.CheckState = System.Windows.Forms.CheckState.Checked;
			this.chkEnableMultiSelection.Location = new System.Drawing.Point(24, 112);
			this.chkEnableMultiSelection.Name = "chkEnableMultiSelection";
			this.chkEnableMultiSelection.Size = new System.Drawing.Size(216, 24);
			this.chkEnableMultiSelection.TabIndex = 2;
			this.chkEnableMultiSelection.Text = "Grid.Selection.EnableMultiSelection";
			this.chkEnableMultiSelection.CheckedChanged += new System.EventHandler(this.chkEnableMultiSelection_CheckedChanged);
			// 
			// cPickSelBackColor
			// 
			this.cPickSelBackColor.Location = new System.Drawing.Point(152, 4);
			this.cPickSelBackColor.Name = "cPickSelBackColor";
			this.cPickSelBackColor.SelectedColor = System.Drawing.Color.Black;
			this.cPickSelBackColor.Size = new System.Drawing.Size(176, 24);
			this.cPickSelBackColor.TabIndex = 3;
			// 
			// label1
			// 
			this.label1.Location = new System.Drawing.Point(4, 8);
			this.label1.Name = "label1";
			this.label1.Size = new System.Drawing.Size(108, 20);
			this.label1.TabIndex = 4;
			this.label1.Text = "Selection BackColor";
			// 
			// label3
			// 
			this.label3.Location = new System.Drawing.Point(152, 12);
			this.label3.Name = "label3";
			this.label3.Size = new System.Drawing.Size(68, 20);
			this.label3.TabIndex = 8;
			this.label3.Text = "Border Color";
			// 
			// cPckBorderColor
			// 
			this.cPckBorderColor.Location = new System.Drawing.Point(224, 8);
			this.cPckBorderColor.Name = "cPckBorderColor";
			this.cPckBorderColor.SelectedColor = System.Drawing.Color.Black;
			this.cPckBorderColor.Size = new System.Drawing.Size(164, 24);
			this.cPckBorderColor.TabIndex = 7;
			// 
			// tabControl1
			// 
			this.tabControl1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
				| System.Windows.Forms.AnchorStyles.Right)));
			this.tabControl1.Controls.Add(this.tabPage1);
			this.tabControl1.Controls.Add(this.tabPage2);
			this.tabControl1.Controls.Add(this.tabPage4);
			this.tabControl1.Controls.Add(this.tabPage3);
			this.tabControl1.Location = new System.Drawing.Point(4, 8);
			this.tabControl1.Name = "tabControl1";
			this.tabControl1.SelectedIndex = 0;
			this.tabControl1.Size = new System.Drawing.Size(432, 172);
			this.tabControl1.TabIndex = 9;
			// 
			// tabPage1
			// 
			this.tabPage1.Controls.Add(this.groupBox1);
			this.tabPage1.Controls.Add(this.chkEnableMultiSelection);
			this.tabPage1.Location = new System.Drawing.Point(4, 22);
			this.tabPage1.Name = "tabPage1";
			this.tabPage1.Size = new System.Drawing.Size(424, 146);
			this.tabPage1.TabIndex = 0;
			this.tabPage1.Text = "SelectionMode";
			// 
			// tabPage2
			// 
			this.tabPage2.Controls.Add(this.chkDrawOverCells);
			this.tabPage2.Controls.Add(this.chkDrawOnlyInitializedCells);
			this.tabPage2.Controls.Add(this.label2);
			this.tabPage2.Controls.Add(this.trackSelectionAlpha);
			this.tabPage2.Controls.Add(this.cPickSelBackColor);
			this.tabPage2.Controls.Add(this.label1);
			this.tabPage2.Location = new System.Drawing.Point(4, 22);
			this.tabPage2.Name = "tabPage2";
			this.tabPage2.Size = new System.Drawing.Size(424, 146);
			this.tabPage2.TabIndex = 1;
			this.tabPage2.Text = "Selection Visual Style";
			// 
			// chkDrawOverCells
			// 
			this.chkDrawOverCells.Location = new System.Drawing.Point(4, 108);
			this.chkDrawOverCells.Name = "chkDrawOverCells";
			this.chkDrawOverCells.Size = new System.Drawing.Size(416, 28);
			this.chkDrawOverCells.TabIndex = 12;
			this.chkDrawOverCells.Text = "Draw selection over cells (Selection.MaskStyle = DrawSeletionOverCells)";
			this.chkDrawOverCells.CheckedChanged += new System.EventHandler(this.chkDrawStyleChanged_CheckedChanged);
			// 
			// chkDrawOnlyInitializedCells
			// 
			this.chkDrawOnlyInitializedCells.Location = new System.Drawing.Point(4, 84);
			this.chkDrawOnlyInitializedCells.Name = "chkDrawOnlyInitializedCells";
			this.chkDrawOnlyInitializedCells.Size = new System.Drawing.Size(416, 28);
			this.chkDrawOnlyInitializedCells.TabIndex = 11;
			this.chkDrawOnlyInitializedCells.Text = "Draw only initialized cells (Selection.MaskStyle = DrawOnlyInitializedCells)";
			this.chkDrawOnlyInitializedCells.CheckedChanged += new System.EventHandler(this.chkDrawStyleChanged_CheckedChanged);
			// 
			// label2
			// 
			this.label2.Location = new System.Drawing.Point(4, 40);
			this.label2.Name = "label2";
			this.label2.Size = new System.Drawing.Size(128, 20);
			this.label2.TabIndex = 10;
			this.label2.Text = "Selection Trasparency:";
			// 
			// trackSelectionAlpha
			// 
			this.trackSelectionAlpha.Location = new System.Drawing.Point(152, 32);
			this.trackSelectionAlpha.Maximum = 255;
			this.trackSelectionAlpha.Name = "trackSelectionAlpha";
			this.trackSelectionAlpha.Size = new System.Drawing.Size(176, 45);
			this.trackSelectionAlpha.TabIndex = 9;
			this.trackSelectionAlpha.TickFrequency = 10;
			this.trackSelectionAlpha.TickStyle = System.Windows.Forms.TickStyle.Both;
			// 
			// tabPage4
			// 
			this.tabPage4.Controls.Add(this.chkTabStop);
			this.tabPage4.Controls.Add(this.label6);
			this.tabPage4.Controls.Add(this.trackFocusBackColorTrans);
			this.tabPage4.Controls.Add(this.cPickFocusBackColor);
			this.tabPage4.Controls.Add(this.label5);
			this.tabPage4.Location = new System.Drawing.Point(4, 22);
			this.tabPage4.Name = "tabPage4";
			this.tabPage4.Size = new System.Drawing.Size(424, 146);
			this.tabPage4.TabIndex = 3;
			this.tabPage4.Text = "Focus Visual Style";
			// 
			// label6
			// 
			this.label6.Location = new System.Drawing.Point(4, 12);
			this.label6.Name = "label6";
			this.label6.Size = new System.Drawing.Size(108, 20);
			this.label6.TabIndex = 12;
			this.label6.Text = "Focus BackColor";
			// 
			// trackFocusBackColorTrans
			// 
			this.trackFocusBackColorTrans.Location = new System.Drawing.Point(152, 32);
			this.trackFocusBackColorTrans.Maximum = 255;
			this.trackFocusBackColorTrans.Name = "trackFocusBackColorTrans";
			this.trackFocusBackColorTrans.Size = new System.Drawing.Size(176, 45);
			this.trackFocusBackColorTrans.TabIndex = 13;
			this.trackFocusBackColorTrans.TickFrequency = 10;
			this.trackFocusBackColorTrans.TickStyle = System.Windows.Forms.TickStyle.Both;
			// 
			// cPickFocusBackColor
			// 
			this.cPickFocusBackColor.Location = new System.Drawing.Point(152, 8);
			this.cPickFocusBackColor.Name = "cPickFocusBackColor";
			this.cPickFocusBackColor.SelectedColor = System.Drawing.Color.Black;
			this.cPickFocusBackColor.Size = new System.Drawing.Size(176, 24);
			this.cPickFocusBackColor.TabIndex = 11;
			// 
			// label5
			// 
			this.label5.Location = new System.Drawing.Point(4, 44);
			this.label5.Name = "label5";
			this.label5.Size = new System.Drawing.Size(128, 20);
			this.label5.TabIndex = 14;
			this.label5.Text = "Focus Trasparency:";
			// 
			// tabPage3
			// 
			this.tabPage3.Controls.Add(this.label7);
			this.tabPage3.Controls.Add(this.cbDashStyle);
			this.tabPage3.Controls.Add(this.groupBox2);
			this.tabPage3.Controls.Add(this.cPckBorderColor);
			this.tabPage3.Controls.Add(this.label3);
			this.tabPage3.Controls.Add(this.trackBorderWidth);
			this.tabPage3.Controls.Add(this.label4);
			this.tabPage3.Location = new System.Drawing.Point(4, 22);
			this.tabPage3.Name = "tabPage3";
			this.tabPage3.Size = new System.Drawing.Size(424, 146);
			this.tabPage3.TabIndex = 2;
			this.tabPage3.Text = "Selection Border";
			// 
			// label7
			// 
			this.label7.Location = new System.Drawing.Point(148, 92);
			this.label7.Name = "label7";
			this.label7.Size = new System.Drawing.Size(76, 20);
			this.label7.TabIndex = 15;
			this.label7.Text = "Dash Style";
			// 
			// cbDashStyle
			// 
			this.cbDashStyle.Location = new System.Drawing.Point(228, 92);
			this.cbDashStyle.Name = "cbDashStyle";
			this.cbDashStyle.Size = new System.Drawing.Size(164, 20);
			this.cbDashStyle.TabIndex = 14;
			this.cbDashStyle.TextChanged += new System.EventHandler(this.cbDashStyle_ValueChanged);
			// 
			// groupBox2
			// 
			this.groupBox2.Controls.Add(this.rdAuto);
			this.groupBox2.Controls.Add(this.rdFocusCell);
			this.groupBox2.Controls.Add(this.rdUniqueRange);
			this.groupBox2.Controls.Add(this.rdFocusRange);
			this.groupBox2.Controls.Add(this.rdNone);
			this.groupBox2.Location = new System.Drawing.Point(4, 4);
			this.groupBox2.Name = "groupBox2";
			this.groupBox2.Size = new System.Drawing.Size(140, 140);
			this.groupBox2.TabIndex = 13;
			this.groupBox2.TabStop = false;
			this.groupBox2.Text = "Selection.BorderMode";
			// 
			// rdAuto
			// 
			this.rdAuto.Checked = true;
			this.rdAuto.Location = new System.Drawing.Point(12, 112);
			this.rdAuto.Name = "rdAuto";
			this.rdAuto.TabIndex = 4;
			this.rdAuto.TabStop = true;
			this.rdAuto.Text = "Auto";
			this.rdAuto.CheckedChanged += new System.EventHandler(this.ChangeSelectionBorderMode);
			// 
			// rdFocusCell
			// 
			this.rdFocusCell.Location = new System.Drawing.Point(12, 64);
			this.rdFocusCell.Name = "rdFocusCell";
			this.rdFocusCell.TabIndex = 3;
			this.rdFocusCell.Text = "FocusCell";
			this.rdFocusCell.CheckedChanged += new System.EventHandler(this.ChangeSelectionBorderMode);
			// 
			// rdUniqueRange
			// 
			this.rdUniqueRange.Location = new System.Drawing.Point(12, 40);
			this.rdUniqueRange.Name = "rdUniqueRange";
			this.rdUniqueRange.TabIndex = 2;
			this.rdUniqueRange.Text = "UniqueRange";
			this.rdUniqueRange.CheckedChanged += new System.EventHandler(this.ChangeSelectionBorderMode);
			// 
			// rdFocusRange
			// 
			this.rdFocusRange.Location = new System.Drawing.Point(12, 88);
			this.rdFocusRange.Name = "rdFocusRange";
			this.rdFocusRange.TabIndex = 1;
			this.rdFocusRange.Text = "FocusRange";
			this.rdFocusRange.CheckedChanged += new System.EventHandler(this.ChangeSelectionBorderMode);
			// 
			// rdNone
			// 
			this.rdNone.Location = new System.Drawing.Point(12, 16);
			this.rdNone.Name = "rdNone";
			this.rdNone.TabIndex = 0;
			this.rdNone.Text = "None";
			this.rdNone.CheckedChanged += new System.EventHandler(this.ChangeSelectionBorderMode);
			// 
			// trackBorderWidth
			// 
			this.trackBorderWidth.Location = new System.Drawing.Point(228, 44);
			this.trackBorderWidth.Maximum = 20;
			this.trackBorderWidth.Name = "trackBorderWidth";
			this.trackBorderWidth.Size = new System.Drawing.Size(96, 45);
			this.trackBorderWidth.TabIndex = 11;
			// 
			// label4
			// 
			this.label4.Location = new System.Drawing.Point(148, 52);
			this.label4.Name = "label4";
			this.label4.Size = new System.Drawing.Size(76, 20);
			this.label4.TabIndex = 12;
			this.label4.Text = "Border Width:";
			// 
			// chkTabStop
			// 
			this.chkTabStop.Checked = true;
			this.chkTabStop.CheckState = System.Windows.Forms.CheckState.Checked;
			this.chkTabStop.Location = new System.Drawing.Point(8, 88);
			this.chkTabStop.Name = "chkTabStop";
			this.chkTabStop.TabIndex = 15;
			this.chkTabStop.Text = "Grid.TabStop";
			this.chkTabStop.CheckedChanged += new System.EventHandler(this.chkTabStop_CheckedChanged);
			// 
			// frmSample17
			// 
			this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
			this.ClientSize = new System.Drawing.Size(440, 371);
			this.Controls.Add(this.tabControl1);
			this.Controls.Add(this.grid1);
			this.Name = "frmSample17";
			this.Text = "Selection Style";
			this.Load += new System.EventHandler(this.frmSample17_Load);
			this.groupBox1.ResumeLayout(false);
			this.tabControl1.ResumeLayout(false);
			this.tabPage1.ResumeLayout(false);
			this.tabPage2.ResumeLayout(false);
			((System.ComponentModel.ISupportInitialize)(this.trackSelectionAlpha)).EndInit();
			this.tabPage4.ResumeLayout(false);
			((System.ComponentModel.ISupportInitialize)(this.trackFocusBackColorTrans)).EndInit();
			this.tabPage3.ResumeLayout(false);
			this.groupBox2.ResumeLayout(false);
			((System.ComponentModel.ISupportInitialize)(this.trackBorderWidth)).EndInit();
			this.ResumeLayout(false);

		}
		#endregion

		private void frmSample17_Load(object sender, System.EventArgs e)
		{
			grid1.Redim(40,40);

			grid1[0,0] = new SourceGrid.Cells.Header(null);
			for (int c = 1; c < grid1.ColumnsCount; c++)
			{
				SourceGrid.Cells.ColumnHeader l_Header = new SourceGrid.Cells.ColumnHeader("Header " + c.ToString());
				l_Header.AutomaticSortEnabled = false;
				l_Header.ColumnSelectorEnabled = true;
				l_Header.ColumnFocusEnabled = true;
				grid1[0,c] = l_Header;
			}

			Random rnd = new Random();
			for (int r = 1; r < grid1.RowsCount; r++)
			{
				grid1[r,0] = new SourceGrid.Cells.RowHeader("Header " + r.ToString());
				for (int c = 1; c < grid1.ColumnsCount; c++)
				{
					if (rnd.NextDouble() > 0.20)
					{
						grid1[r,c] = new SourceGrid.Cells.Cell(r*c, typeof(int));
					}
					else
						grid1[r,c] = null;
				}
			}

			cPickSelBackColor.SelectedColor = Color.FromArgb(grid1.Selection.BackColor.R, grid1.Selection.BackColor.G, grid1.Selection.BackColor.B);
			cPckBorderColor.SelectedColor = grid1.Selection.Border.Top.Color;
			trackSelectionAlpha.Value = (int)grid1.Selection.BackColor.A;
			trackBorderWidth.Value = grid1.Selection.Border.Top.Width;

			cPickFocusBackColor.SelectedColor = Color.FromArgb(grid1.Selection.FocusBackColor.R, grid1.Selection.FocusBackColor.G, grid1.Selection.FocusBackColor.B);
			trackFocusBackColorTrans.Value = grid1.Selection.FocusBackColor.A;


			this.cPickSelBackColor.SelectedColorChanged += new System.EventHandler(this.cPickSelBackColor_SelectedColorChanged);
			this.cPckBorderColor.SelectedColorChanged += new System.EventHandler(this.cPckBorderColor_SelectedColorChanged);
			this.trackSelectionAlpha.ValueChanged += new System.EventHandler(this.trackSelectionAlpha_ValueChanged);
			this.trackBorderWidth.ValueChanged += new System.EventHandler(this.trackBorderWidth_ValueChanged);
			this.trackFocusBackColorTrans.ValueChanged += new System.EventHandler(this.trackFocusBackColorTrans_ValueChanged);
			this.cPickFocusBackColor.SelectedColorChanged += new System.EventHandler(this.cPickFocusBackColor_SelectedColorChanged);

			cbDashStyle.Validator = new DevAge.ComponentModel.Validator.ValidatorTypeConverter(typeof(System.Drawing.Drawing2D.DashStyle));
			cbDashStyle.Value = grid1.Selection.Border.Top.DashStyle;
		}

		private void Check_Change(object sender, System.EventArgs e)
		{
			if (rdSelectionModeCell.Checked)
				grid1.Selection.SelectionMode = SourceGrid.GridSelectionMode.Cell;
			else if (rdSelectionModeRow.Checked)
				grid1.Selection.SelectionMode = SourceGrid.GridSelectionMode.Row;
			else if (rdSelectionModeColumn.Checked)
				grid1.Selection.SelectionMode = SourceGrid.GridSelectionMode.Column;
		}

		private void chkEnableMultiSelection_CheckedChanged(object sender, System.EventArgs e)
		{
			if (chkEnableMultiSelection.Checked)
				grid1.Selection.EnableMultiSelection = true;
			else
				grid1.Selection.EnableMultiSelection = false;
		}

		private void cPickSelBackColor_SelectedColorChanged(object sender, System.EventArgs e)
		{
			grid1.Selection.BackColor = Color.FromArgb(trackSelectionAlpha.Value, cPickSelBackColor.SelectedColor);
		}

		private void cPckBorderColor_SelectedColorChanged(object sender, System.EventArgs e)
		{
			DevAge.Drawing.RectangleBorder border = grid1.Selection.Border;
			border.SetColor(cPckBorderColor.SelectedColor);
			grid1.Selection.Border = border;
		}

		private void trackSelectionAlpha_ValueChanged(object sender, System.EventArgs e)
		{
			grid1.Selection.BackColor = Color.FromArgb(trackSelectionAlpha.Value, cPickSelBackColor.SelectedColor);
		}

		private void trackBorderWidth_ValueChanged(object sender, System.EventArgs e)
		{
			DevAge.Drawing.RectangleBorder b = grid1.Selection.Border;
			b.SetWidth(trackBorderWidth.Value);
			grid1.Selection.Border = b;
		}

		private void ChangeSelectionBorderMode(object sender, System.EventArgs e)
		{
			if (rdFocusCell.Checked)
				grid1.Selection.BorderMode = SourceGrid.SelectionBorderMode.FocusCell;
			else if (rdFocusRange.Checked)
				grid1.Selection.BorderMode = SourceGrid.SelectionBorderMode.FocusRange;
			else if (rdNone.Checked)
				grid1.Selection.BorderMode = SourceGrid.SelectionBorderMode.None;
			else if (rdUniqueRange.Checked)
				grid1.Selection.BorderMode = SourceGrid.SelectionBorderMode.UniqueRange;
			else if (rdAuto.Checked)
				grid1.Selection.BorderMode = SourceGrid.SelectionBorderMode.Auto;
		}

		private void trackFocusBackColorTrans_ValueChanged(object sender, System.EventArgs e)
		{
			grid1.Selection.FocusBackColor = Color.FromArgb(trackFocusBackColorTrans.Value, cPickFocusBackColor.SelectedColor);
		}

		private void cPickFocusBackColor_SelectedColorChanged(object sender, System.EventArgs e)
		{
			grid1.Selection.FocusBackColor = Color.FromArgb(trackFocusBackColorTrans.Value, cPickFocusBackColor.SelectedColor);
		}

		private void chkDrawStyleChanged_CheckedChanged(object sender, System.EventArgs e)
		{
			grid1.Selection.MaskStyle = SourceGrid.SelectionMaskStyle.None;
			if (chkDrawOnlyInitializedCells.Checked)
			{
				grid1.Selection.MaskStyle |= SourceGrid.SelectionMaskStyle.DrawOnlyInitializedCells;
			}
			if (chkDrawOverCells.Checked)
			{
				grid1.Selection.MaskStyle |= SourceGrid.SelectionMaskStyle.DrawSeletionOverCells;
			}
		}

		private void cbDashStyle_ValueChanged(object sender, System.EventArgs e)
		{
			grid1.Selection.Border = grid1.Selection.Border.SetDashStyle((System.Drawing.Drawing2D.DashStyle)cbDashStyle.Value);
		}

		private void chkTabStop_CheckedChanged(object sender, System.EventArgs e)
		{
			grid1.TabStop = chkTabStop.Checked;
		}
	}
}
