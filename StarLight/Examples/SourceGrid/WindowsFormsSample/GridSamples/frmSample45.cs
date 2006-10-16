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
    [Sample("SourceGrid - Advanced features", 45, "Grid navigation, tab, arrows and other controls")]
    public class frmSample45 : System.Windows.Forms.Form
    {
        private SourceGrid.Grid grid1;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.TextBox textBox1;
        private System.Windows.Forms.CheckBox chkArrows;
        private System.Windows.Forms.CheckBox chkTab;
        private System.Windows.Forms.CheckBox chkAutomaticFocus;
        private System.Windows.Forms.CheckBox chkEnter;
        private System.Windows.Forms.CheckBox chkEscape;
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.Container components = null;

        public frmSample45()
        {
            //
            // Required for Windows Form Designer support
            //
            InitializeComponent();
        }

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                if (components != null)
                {
                    components.Dispose();
                }
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code
        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.grid1 = new SourceGrid.Grid();
            this.label1 = new System.Windows.Forms.Label();
            this.textBox1 = new System.Windows.Forms.TextBox();
            this.chkArrows = new System.Windows.Forms.CheckBox();
            this.chkTab = new System.Windows.Forms.CheckBox();
            this.chkAutomaticFocus = new System.Windows.Forms.CheckBox();
            this.chkEnter = new System.Windows.Forms.CheckBox();
            this.chkEscape = new System.Windows.Forms.CheckBox();
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
            this.grid1.Size = new System.Drawing.Size(256, 228);
            this.grid1.SpecialKeys = SourceGrid.GridSpecialKeys.Default;
            this.grid1.StyleGrid = null;
            this.grid1.TabIndex = 0;
            // 
            // label1
            // 
            this.label1.Location = new System.Drawing.Point(276, 8);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(100, 32);
            this.label1.TabIndex = 1;
            this.label1.Text = "Control to test tab navigation";
            // 
            // textBox1
            // 
            this.textBox1.Location = new System.Drawing.Point(276, 40);
            this.textBox1.Name = "textBox1";
            this.textBox1.Size = new System.Drawing.Size(96, 20);
            this.textBox1.TabIndex = 2;
            this.textBox1.Text = "Hello !";
            // 
            // chkArrows
            // 
            this.chkArrows.Checked = true;
            this.chkArrows.CheckState = System.Windows.Forms.CheckState.Checked;
            this.chkArrows.Location = new System.Drawing.Point(268, 68);
            this.chkArrows.Name = "chkArrows";
            this.chkArrows.Size = new System.Drawing.Size(104, 16);
            this.chkArrows.TabIndex = 3;
            this.chkArrows.Text = "Enable Arrows";
            this.chkArrows.CheckedChanged += new System.EventHandler(this.Checkbox_CheckedChanged);
            // 
            // chkTab
            // 
            this.chkTab.Checked = true;
            this.chkTab.CheckState = System.Windows.Forms.CheckState.Checked;
            this.chkTab.Location = new System.Drawing.Point(268, 88);
            this.chkTab.Name = "chkTab";
            this.chkTab.Size = new System.Drawing.Size(104, 16);
            this.chkTab.TabIndex = 4;
            this.chkTab.Text = "Enable Tab";
            this.chkTab.CheckedChanged += new System.EventHandler(this.Checkbox_CheckedChanged);
            // 
            // chkAutomaticFocus
            // 
            this.chkAutomaticFocus.Checked = true;
            this.chkAutomaticFocus.CheckState = System.Windows.Forms.CheckState.Checked;
            this.chkAutomaticFocus.Location = new System.Drawing.Point(268, 160);
            this.chkAutomaticFocus.Name = "chkAutomaticFocus";
            this.chkAutomaticFocus.Size = new System.Drawing.Size(104, 16);
            this.chkAutomaticFocus.TabIndex = 7;
            this.chkAutomaticFocus.Text = "Auto Focus Cell";
            this.chkAutomaticFocus.CheckedChanged += new System.EventHandler(this.Checkbox_CheckedChanged);
            // 
            // chkEnter
            // 
            this.chkEnter.Checked = true;
            this.chkEnter.CheckState = System.Windows.Forms.CheckState.Checked;
            this.chkEnter.Location = new System.Drawing.Point(268, 108);
            this.chkEnter.Name = "chkEnter";
            this.chkEnter.Size = new System.Drawing.Size(104, 16);
            this.chkEnter.TabIndex = 5;
            this.chkEnter.Text = "Enable Enter";
            this.chkEnter.CheckedChanged += new System.EventHandler(this.Checkbox_CheckedChanged);
            // 
            // chkEscape
            // 
            this.chkEscape.Checked = true;
            this.chkEscape.CheckState = System.Windows.Forms.CheckState.Checked;
            this.chkEscape.Location = new System.Drawing.Point(268, 128);
            this.chkEscape.Name = "chkEscape";
            this.chkEscape.Size = new System.Drawing.Size(104, 16);
            this.chkEscape.TabIndex = 6;
            this.chkEscape.Text = "Enable Escape";
            this.chkEscape.CheckedChanged += new System.EventHandler(this.Checkbox_CheckedChanged);
            // 
            // frmSample45
            // 
            this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
            this.ClientSize = new System.Drawing.Size(380, 245);
            this.Controls.Add(this.chkEscape);
            this.Controls.Add(this.chkEnter);
            this.Controls.Add(this.chkAutomaticFocus);
            this.Controls.Add(this.chkTab);
            this.Controls.Add(this.chkArrows);
            this.Controls.Add(this.textBox1);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.grid1);
            this.Name = "frmSample45";
            this.Text = "Grid navigation";
            this.Load += new System.EventHandler(this.frmSample45_Load);
            this.ResumeLayout(false);

        }
        #endregion

        private void frmSample45_Load(object sender, System.EventArgs e)
        {
            grid1.BorderStyle = BorderStyle.FixedSingle;

            grid1.ColumnsCount = 3;
            grid1.FixedRows = 1;
            grid1.Rows.Insert(0);
            grid1[0, 0] = new SourceGrid.Cells.ColumnHeader("String");
            grid1[0, 1] = new SourceGrid.Cells.ColumnHeader("DateTime");
            grid1[0, 2] = new SourceGrid.Cells.ColumnHeader("CheckBox");
            for (int r = 1; r < 10; r++)
            {
                grid1.Rows.Insert(r);
                grid1[r, 0] = new SourceGrid.Cells.Cell("Hello " + r.ToString(), typeof(string));
                grid1[r, 1] = new SourceGrid.Cells.Cell(DateTime.Today, typeof(DateTime));
                grid1[r, 2] = new SourceGrid.Cells.CheckBox(null, true);
            }

            grid1.AutoSizeCells();

            grid1.Selection.FocusStyle = grid1.Selection.FocusStyle | SourceGrid.FocusStyle.FocusFirstCellOnEnter;
            grid1.Selection.FocusStyle = grid1.Selection.FocusStyle | SourceGrid.FocusStyle.RemoveFocusCellOnLeave;
        }

        private void Checkbox_CheckedChanged(object sender, System.EventArgs e)
        {
            SourceGrid.GridSpecialKeys specialKeys = SourceGrid.GridSpecialKeys.None;

            if (chkArrows.Checked)
                specialKeys = specialKeys | SourceGrid.GridSpecialKeys.Arrows;
            if (chkEnter.Checked)
                specialKeys = specialKeys | SourceGrid.GridSpecialKeys.Enter;
            if (chkEscape.Checked)
                specialKeys = specialKeys | SourceGrid.GridSpecialKeys.Escape;
            if (chkTab.Checked)
                specialKeys = specialKeys | SourceGrid.GridSpecialKeys.Tab;

            grid1.SpecialKeys = specialKeys;

            SourceGrid.FocusStyle focusStyle = SourceGrid.FocusStyle.None;

            if (chkAutomaticFocus.Checked)
            {
                focusStyle = focusStyle | SourceGrid.FocusStyle.FocusFirstCellOnEnter;
                focusStyle = focusStyle | SourceGrid.FocusStyle.RemoveFocusCellOnLeave;
            }

            grid1.Selection.FocusStyle = focusStyle;
        }

    }
}
