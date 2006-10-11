using System;
using System.Data;

namespace SourceGrid.Cells.DataGrid
{
	public class Cell : SourceGrid.Cells.Virtual.CellVirtual
	{
		public Cell(DataColumn column)
		{
			Model.AddModel(new DataGridValueModel(column.Ordinal));
		}
	}

	public class CheckBox : SourceGrid.Cells.Virtual.CheckBox
	{
		public CheckBox(DataColumn column)
		{
			Model.AddModel(new DataGridValueModel(column.Ordinal));
		}
	}

	public class Image : SourceGrid.Cells.Virtual.Image
	{
		public Image(DataColumn column)
		{
			Model.AddModel(new DataGridValueModel(column.Ordinal));
		}
	}

	public class Link : SourceGrid.Cells.Virtual.Link
	{
		public Link(DataColumn column)
		{
			Model.AddModel(new DataGridValueModel(column.Ordinal));
		}
	}
}
