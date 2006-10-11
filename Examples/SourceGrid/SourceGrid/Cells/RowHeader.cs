using System;
using System.Drawing;

namespace SourceGrid.Cells.Virtual
{
	/// <summary>
	/// A cell that rappresent a header of a table, with 3D effect. This cell override IsSelectable to false. Default use VisualModels.VisualModelHeader.Style1
	/// </summary>
	public abstract class RowHeader : CellVirtual
	{
		/// <summary>
		/// Constructor
		/// </summary>
		public RowHeader()
		{
			View = Views.RowHeader.Default;
			AddController(Controllers.Unselectable.Default);
			AddController(Controllers.MouseInvalidate.Default);
			ResizeEnabled = true;
			RowFocusEnabled = true;
			RowSelectorEnabled = true;
		}

		/// <summary>
		/// Gets or sets if enable the resize of the height, using a Resizable controller. Default is true.
		/// </summary>
		public bool ResizeEnabled
		{
			get{return FindController(typeof(Controllers.Resizable)) == Controllers.Resizable.ResizeHeight;}
			set
			{
				if (value)
					AddController(Controllers.Resizable.ResizeHeight);
				else
					RemoveController(Controllers.Resizable.ResizeHeight);
			}
		}

		/// <summary>
		/// Gets or sets if enable the focus on the row when clicking this header, using a RowFocus controller. Default is true.
		/// </summary>
		public bool RowFocusEnabled
		{
			get{return FindController(typeof(Controllers.RowFocus)) == Controllers.RowFocus.Default;}
			set
			{
				if (value)
					AddController(Controllers.RowFocus.Default);
				else
					RemoveController(Controllers.RowFocus.Default);
			}
		}

		/// <summary>
		/// Gets or sets if enable the selection on the row when clicking this header, using a RowSelector controller. Default is true.
		/// </summary>
		public bool RowSelectorEnabled
		{
			get{return FindController(typeof(Controllers.RowSelector)) == Controllers.RowSelector.Default;}
			set
			{
				if (value)
					AddController(Controllers.RowSelector.Default);
				else
					RemoveController(Controllers.RowSelector.Default);
			}
		}
	}
}

namespace SourceGrid.Cells
{
	/// <summary>
	/// A cell that rappresent a header of a table, with 3D effect. This cell override IsSelectable to false. Default use VisualModels.VisualModelHeader.Style1
	/// </summary>
	public class RowHeader : Cell
	{
		/// <summary>
		/// Constructor
		/// </summary>
		public RowHeader():this(null)
		{
		}
		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="cellValue"></param>
		public RowHeader(object cellValue):base(cellValue)
		{
			View = Views.RowHeader.Default;
			AddController(Controllers.Unselectable.Default);
			AddController(Controllers.MouseInvalidate.Default);
			ResizeEnabled = true;
			RowFocusEnabled = true;
			RowSelectorEnabled = true;
		}

		/// <summary>
		/// Gets or sets if enable the resize of the height, using a Resizable controller. Default is true.
		/// </summary>
		public bool ResizeEnabled
		{
			get{return FindController(typeof(Controllers.Resizable)) == Controllers.Resizable.ResizeHeight;}
			set
			{
				if (value)
					AddController(Controllers.Resizable.ResizeHeight);
				else
					RemoveController(Controllers.Resizable.ResizeHeight);
			}
		}

		/// <summary>
		/// Gets or sets if enable the focus on the column when clicking this header, using a RowFocus controller. Default is true.
		/// </summary>
		public bool RowFocusEnabled
		{
			get{return FindController(typeof(Controllers.RowFocus)) == Controllers.RowFocus.Default;}
			set
			{
				if (value)
					AddController(Controllers.RowFocus.Default);
				else
					RemoveController(Controllers.RowFocus.Default);
			}
		}

		/// <summary>
		/// Gets or sets if enable the selection on the row when clicking this header, using a RowSelector controller. Default is true.
		/// </summary>
		public bool RowSelectorEnabled
		{
			get{return FindController(typeof(Controllers.RowSelector)) == Controllers.RowSelector.Default;}
			set
			{
				if (value)
					AddController(Controllers.RowSelector.Default);
				else
					RemoveController(Controllers.RowSelector.Default);
			}
		}
	}

}