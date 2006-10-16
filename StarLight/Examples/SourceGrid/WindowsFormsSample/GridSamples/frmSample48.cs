using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace WindowsFormsSample.GridSamples
{
    [Sample("SourceGrid - Basic concepts", 48, "Real Grid basic concepts 2")]
    public partial class frmSample48 : Form
    {
        public frmSample48()
        {
            InitializeComponent();
        }

        protected override void OnLoad(EventArgs e)
        {
            base.OnLoad(e);

            grid1.BorderStyle = BorderStyle.FixedSingle;

            grid1.ColumnsCount = 3;
            grid1.FixedRows = 1;
            grid1.Rows.Insert(0);

            SourceGrid.Cells.Views.ColumnHeader boldHeader = new SourceGrid.Cells.Views.ColumnHeader();
            boldHeader.Font = new Font(grid1.Font, FontStyle.Bold | FontStyle.Underline);

            SourceGrid.Cells.Views.Cell yellowView = new SourceGrid.Cells.Views.Cell();
            yellowView.BackColor = Color.Yellow;
            SourceGrid.Cells.Views.CheckBox yellowViewCheck = new SourceGrid.Cells.Views.CheckBox();
            yellowViewCheck.BackColor = Color.Yellow;


            grid1[0, 0] = new SourceGrid.Cells.ColumnHeader("String");
            grid1[0, 0].View = boldHeader;
            
            grid1[0, 1] = new SourceGrid.Cells.ColumnHeader("DateTime");
            grid1[0, 1].View = boldHeader;

            grid1[0, 2] = new SourceGrid.Cells.ColumnHeader("CheckBox");
            grid1[0, 2].View = boldHeader;
            for (int r = 1; r < 10; r++)
            {
                grid1.Rows.Insert(r);

                grid1[r, 0] = new SourceGrid.Cells.Cell("Hello " + r.ToString(), typeof(string));
                grid1[r, 0].View = yellowView;

                grid1[r, 1] = new SourceGrid.Cells.Cell(DateTime.Today, typeof(DateTime));
                grid1[r, 1].View = yellowView;

                grid1[r, 2] = new SourceGrid.Cells.CheckBox(null, true);
                grid1[r, 2].View = yellowViewCheck;
            }

            grid1.AutoSizeCells();
        }
    }
}