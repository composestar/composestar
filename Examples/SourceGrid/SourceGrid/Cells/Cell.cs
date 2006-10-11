using System;
using System.Collections;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Windows.Forms;

namespace SourceGrid.Cells
{
	/// <summary>
	/// Represents a Cell in a grid, with Cell.Value support and row/col span. Support also ToolTipText, ContextMenu and Cursor
	/// </summary>
	public class Cell : Virtual.CellVirtual, ICell
	{
		#region Constructors

		/// <summary>
		/// Constructor
		/// </summary>
		public Cell():this(null)
		{
		}

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="cellValue"></param>
		public Cell(object cellValue)
		{
			Model = new SourceGrid.Cells.Models.ModelContainer();
			Model.ValueModel = new Models.ValueModel();

			Model.AddModel(new Models.ContextMenu());
			Model.AddModel(new Models.ToolTip());
			Model.AddModel(new Models.Image());

			AddController(Controllers.ContextMenu.Default);
			AddController(Controllers.ToolTipText.Default);

			Value = cellValue;
		}

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="cellValue"></param>
		/// <param name="pType"></param>
		public Cell(object cellValue, Type pType):this(cellValue)
		{
            Editor = Editors.Factory.Create(pType);
		}

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="cellValue"></param>
		/// <param name="pEditor"></param>
		public Cell(object cellValue, Editors.EditorBase pEditor):this(cellValue)
		{
			Editor = pEditor;
		}
		#endregion

		#region Cell Data (Value, DisplayText, Tag)

		//ATTENTION: is reccomanded that all the actions fired by the user interface does not modify this property
		// instead call the CellEditor.ChangeCellValue to preserve data consistence

		/// <summary>
		/// The string representation of the Cell.Value property (default Value.ToString())
		/// </summary>
		public virtual string DisplayText
		{
			get
			{
				return GetContext().GetDisplayText();
			}
		}

		/// <summary>
		/// Value of the cell 
		/// </summary>
		public virtual object Value
		{
			get{return Model.ValueModel.GetValue(GetContext());}
			set
			{
				Model.ValueModel.SetValue(GetContext(), value);
			}
		}

		/// <summary>
		/// Object to put additional info for this cell
		/// </summary>
		private object m_Tag = null;
		/// <summary>
		/// Object to put additional info for this cell
		/// </summary>
		public virtual object Tag
		{
			get{return m_Tag;}
			set{m_Tag = value;}
		}

		/// <summary>
		/// ToString method
		/// </summary>
		/// <returns></returns>
		public override string ToString()
		{
			return DisplayText;
		}
		#endregion

		#region LinkToGrid

		public CellContext GetContext()
		{
			return new CellContext(Grid, m_Range.Start, this);
		}

		private Grid m_Grid;
		/// <summary>
		/// The Grid object
		/// </summary>
		public Grid Grid
		{
			get{return m_Grid;}
		}
		/// <summary>
		/// Link the cell at the specified grid.
		/// REMARKS: To insert a cell in a grid use Grid.InsertCell, this methos is for internal use only
		/// </summary>
		/// <param name="p_grid"></param>
		/// <param name="p_Position"></param>
		public void BindToGrid(Grid p_grid, Position p_Position)
		{
			m_Range.MoveTo(p_Position);

			if (Model == null)
				throw new SourceGridException("Models not valid, Model property is null. You must assign the models before binding the cell to the grid.");
			if (Model.ValueModel == null)
				throw new SourceGridException("Models not valid, Model.ValueModel property is null. You must assign the value model before binding the cell to the grid.");

			m_Grid = p_grid;
			OnAddToGrid(EventArgs.Empty);

			RefreshSpanSearch();
		}
		/// <summary>
		/// Remove the link of the cell from the previous grid.
		/// REMARKS: To remove a cell from a grid use the grid.RemoveCell, this method is for internal use only
		/// </summary>
		public void UnBindToGrid()
		{
			m_Range.MoveTo(Position.Empty);
			if (m_Grid!=null) //tolgo la cella dalla griglia precedentemente selezionata
				OnRemoveToGrid(EventArgs.Empty);

			m_Grid = null;
		}

		/// <summary>
		/// Fired when the cell is added to a grid
		/// </summary>
		/// <param name="e"></param>
		protected virtual void OnAddToGrid(EventArgs e)
		{
			if (m_Grid.StyleGrid != null)
				m_Grid.StyleGrid.ApplyStyle(this);
		}
		/// <summary>
		/// Fired before a cell is removed from a grid
		/// </summary>
		/// <param name="e"></param>
		protected virtual void OnRemoveToGrid(EventArgs e)
		{
		}
		#endregion

		#region Range and Position
		private Range m_Range = Range.Empty; //la posizione è Range.Start

		/// <summary>
		/// Returns the current Row and Col position. If this cell is not attached to the grid returns Position.Empty. And the range occupied by the current cell.
		/// Returns the Range of the cells occupied by the current cell. If RowSpan and ColSpan = 1 then this method returns a single cell.
		/// </summary>
		public Range Range
		{
			get{return m_Range;}
		}

		/// <summary>
		/// Current Row
		/// </summary>
		public int Row
		{
			get{return m_Range.Start.Row;}
		}

		/// <summary>
		/// Current Column
		/// </summary>
		public int Column
		{
			get{return m_Range.Start.Column;}
		}
 
		/// <summary>
		/// Size occupied by the current cell
		/// </summary>
		public Size Size
		{
			get
			{
				if (m_Range.IsEmpty() || Grid == null)
					return Size.Empty;

				return Grid.RangeToRectangle(m_Range).Size;
			}
		}
		#endregion

		#region Row/Col Span

		private void RefreshSpanSearch()
		{
			Grid l_grid = (SourceGrid.Grid)Grid;
			l_grid.SetMaxSpanSearch(Math.Max(ColumnSpan, RowSpan)-1, false);
		}

		/// <summary>
		/// ColSpan for merge operation
		/// </summary>
		public int ColumnSpan
		{
			get{return m_Range.ColumnsCount;}
			set
			{
				m_Range.ColumnsCount = value;
				RefreshSpanSearch();
				Invalidate();
			}
		}
		/// <summary>
		/// RowSpan for merge operation
		/// </summary>
		public int RowSpan
		{
			get{return m_Range.RowsCount;}
			set
			{
				m_Range.RowsCount = value;
				RefreshSpanSearch();
				Invalidate();
			}
		}

		/// <summary>
		/// Returns true if the position specified is inside the current cell range (use Range.Contains)
		/// </summary>
		/// <param name="p_Position"></param>
		/// <returns></returns>
		public virtual bool ContainsPosition(Position p_Position)
		{
			return m_Range.Contains(p_Position);
		}
		#endregion

		#region CalculateRequiredSize
		/// <summary>
		/// If the cell is not linked to a grid the result is not accurate (Font can be null). Call InternalMeasure with RowSpan and ColSpan
		/// </summary>
		/// <param name="maxLayoutArea">SizeF structure that specifies the maximum layout area for the text. If width or height are zero the value is set to a default maximum value.</param>
		/// <returns></returns>
		public Size Measure(Size maxLayoutArea)
		{
			return GetContext().Measure(maxLayoutArea);
		}
		#endregion

		#region Selection

		/// <summary>
		/// Gets or Sets if the current cell is selected
		/// </summary>
		public bool Select
		{
			get
			{
				if (Grid!=null)
					return Grid.Selection.Contains( m_Range.Start );
				else
					return false;
			}
			set
			{
				if (Select != value && Grid != null)
				{
					if (value)
						Grid.Selection.Add( m_Range.Start );
					else
						Grid.Selection.Remove( m_Range.Start );
				}
			}
		}

		#endregion

		#region Focus
		/// <summary>
		/// True if the cell has the focus, is the active cell, see also Selection.ActivePosition.
		/// </summary>
		public bool IsActive
		{
			get
			{
				if (Grid != null)
					return Grid.Selection.ActivePosition == m_Range.Start;
				else
					return false;
			}
		}

		/// <summary>
		/// Give the focus at the cell
		/// </summary>
		/// <returns>Returns if the cell can receive the focus</returns>
		public bool Focus()
		{
			if (Grid!=null)
				return Grid.Selection.Focus(m_Range.Start);
			else
				return false;
		}
		
		/// <summary>
		/// Remove the focus from the cell
		/// </summary>
		/// <returns>Returns true if the cell can leave the focus otherwise false</returns>
		public bool LeaveFocus()
		{
			if (Grid!=null && IsActive)
				return Grid.Selection.Focus(Position.Empty);
			else
				return true;
		}

		#endregion

		#region Editing
		/// <summary>
		/// True if this cell is currently in edit state, otherwise false.
		/// </summary>
		public bool IsEditing()
		{
			return GetContext().IsEditing();
		}

		/// <summary>
		/// Start the edit operation with the current editor specified in the Model property. Using the current cell position.
		/// </summary>
		public void StartEdit()
		{
			GetContext().StartEdit();
		}
		#endregion

		#region Invalidate
		/// <summary>
		/// Invalidate this cell
		/// </summary>
		public void Invalidate()
		{
			GetContext().Invalidate();
		}
		#endregion

		#region ToolTipText
		private Models.ToolTip ToolTipModel
		{
			get{return (Models.ToolTip)Model.FindModel(typeof(Models.ToolTip));}
		}

		/// <summary>
		/// Gets or sets the tool tip text of the cell. Internally use the Models.ToolTip class.
		/// </summary>
		public string ToolTipText
		{
			get{return ToolTipModel.ToolTipText;}
			set{ToolTipModel.ToolTipText = value;}
		}
		#endregion

		#region Image
		private Models.Image ImageModel
		{
			get{return (Models.Image)Model.FindModel(typeof(Models.Image));}
		}

		/// <summary>
		/// Gets or sets the Image associeted with the Cell. Internally use a Models.Image class.
		/// </summary>
		public System.Drawing.Image Image
		{
			get{return ImageModel.ImageValue;}
			set{ImageModel.ImageValue = value;}
		}
		#endregion

		#region ICellContextMenu
		private Models.ContextMenu ContextMenuModel
		{
			get{return (Models.ContextMenu)Model.FindModel(typeof(Models.ContextMenu));}
		}

		/// <summary>
		/// Gets or sets the context menu collection associated with this cell. Internally use a Models.ContextMenu class.
		/// </summary>
		public MenuCollection MenuItems
		{
			get{return ContextMenuModel.MenuItems;}
			set{ContextMenuModel.MenuItems = value;}
		}

		#endregion
	}
}
