using System;
using System.ComponentModel;
using System.Drawing;
using System.Windows.Forms;

namespace SourceGrid.Cells
{
	/// <summary>
	/// Represents a Cell to use with Grid control.
	/// </summary>
	public interface ICell : ICellVirtual
	{
		#region Value, DisplayText, ToolTipText

		/// <summary>
		/// Gets the string representation of the Cell.Value property (default Value.ToString())
		/// </summary>
		string DisplayText
		{
			get;
		}

		/// <summary>
		/// Gets or sets the value of the cell 
		/// </summary>
		object Value
		{
			get;
			set;
		}

		/// <summary>
		/// Gets or sets additional info for this cell
		/// </summary>
		object Tag
		{
			get;
			set;
		}

		/// <summary>
		/// Gets or sets the ToolTipText
		/// </summary>
		string ToolTipText
		{
			get;
			set;
		}

		/// <summary>
		/// Gets or set the image of the cell.
		/// </summary>
		System.Drawing.Image Image
		{
			get;
			set;
		}
		#endregion

		#region LinkToGrid
		/// <summary>
		/// The Grid object
		/// </summary>
		Grid Grid
		{
			get;
		}
		/// <summary>
		/// Link the cell at the specified grid.
		/// REMARKS: To insert a cell in a grid use Grid.InsertCell, this methos is for internal use only
		/// </summary>
		/// <param name="p_grid"></param>
		/// <param name="p_Position"></param>
		void BindToGrid(Grid p_grid, Position p_Position);

		/// <summary>
		/// Remove the link of the cell from the grid.
		/// </summary>
		void UnBindToGrid();
		#endregion

		#region Range and Position
		/// <summary>
		/// Returns the current Row and Col position. If this cell is not attached to the grid returns Position.Empty. And the range occupied by the current cell.
		/// Returns the Range of the cells occupied by the current cell. If RowSpan and ColSpan = 1 then this method returns a single cell.
		/// </summary>
		Range Range
		{
			get;
		}

		/// <summary>
		/// Current Row
		/// </summary>
		int Row
		{
			get;
		}

		/// <summary>
		/// Current Column
		/// </summary>
		int Column
		{
			get;
		}
		#endregion

		#region Row/Col Span
		/// <summary>
		/// ColSpan for merge operation, calculated using the current range.
		/// </summary>
		int ColumnSpan
		{
			get;
			set;
		}
		/// <summary>
		/// RowSpan for merge operation, calculated using the current range.
		/// </summary>
		int RowSpan
		{
			get;
			set;
		}

		/// <summary>
		/// Returns true if the position specified is inside the current cell range (use Range.Contains).
		/// </summary>
		/// <param name="p_Position"></param>
		/// <returns></returns>
		bool ContainsPosition(Position p_Position);
		#endregion

		#region Selection

		/// <summary>
		/// Gets or Sets if the current cell is selected
		/// </summary>
		bool Select
		{
			get;
			set;
		}

		#endregion

		#region Focus
		/// <summary>
		/// True if the has the focus
		/// </summary>
		bool IsActive
		{
			get;
		}

		/// <summary>
		/// Give the focus at the cell
		/// </summary>
		/// <returns>Returns if the cell can receive the focus</returns>
		bool Focus();
		
		/// <summary>
		/// Remove the focus from the cell
		/// </summary>
		/// <returns>Returns true if the cell can leave the focus otherwise false</returns>
		bool LeaveFocus();
		#endregion

		#region Editing
		/// <summary>
		/// True if this cell is currently in edit state, otherwise false.
		/// </summary>
		bool IsEditing();

		/// <summary>
		/// Start the edit operation with the current editor specified in the Model property. Using the current cell position.
		/// </summary>
		void StartEdit();
		#endregion

		#region Invalidate
		/// <summary>
		/// Invalidate this cell
		/// </summary>
		void Invalidate();
		#endregion

		#region Measure
		/// <summary>
		/// Measure the area required by the cell.
		/// </summary>
		/// <param name="maxLayoutArea">Maximum area that can be returned</param>
		/// <returns></returns>
		Size Measure(Size maxLayoutArea);
		#endregion
	}
}
