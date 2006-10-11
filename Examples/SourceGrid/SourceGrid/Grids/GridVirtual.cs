using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Windows.Forms;

namespace SourceGrid
{
	/// <summary>
	/// A abstract Grid control to support large virtual data. You must override: GetCell, CreateRowsObject, CreateColumnsObject
	/// </summary>
	[System.ComponentModel.ToolboxItem(true)]
	public abstract class GridVirtual : CustomScrollControl
	{
		#region Constructor
		/// <summary>
		/// Grid constructor
		/// </summary>
		public GridVirtual()
		{
            SetStyle(ControlStyles.Selectable, false);

			m_Rows = CreateRowsObject();
			m_Columns = CreateColumnsObject();
			m_Selection = new Selection(this);

			//Create the Controller list for the Cells
			m_Controller = new SourceGrid.Cells.Controllers.ControllerContainer();
			m_Controller.AddController(Cells.Controllers.Cell.Default);

			//Grid Controllers
			mGridController = new DevAge.ComponentModel.Controller.ControllerContainer(this);
			mGridController.AddController(Controllers.Grid.Default);
			mGridController.AddController(Controllers.MouseSelection.Default);

			ArrangePanelsLocation();//For default location
		}

		/// <summary>
		/// Abstract method used to create the rows object.
		/// </summary>
		protected abstract RowsBase CreateRowsObject();

		/// <summary>
		/// Abstract method used to create the columns object.
		/// </summary>
		protected abstract ColumnsBase CreateColumnsObject();

		/// <summary>
		/// Create the main docked controls used for scrolling and for the fixed rows and fixed columns.
		/// </summary>
		protected override void CreateDockControls()
		{
			m_PanelDockTop = new Panel();
            m_TopPanel = new GridSubPanel(this, GridSubPanelType.Top);
            m_LeftPanel = new GridSubPanel(this, GridSubPanelType.Left);
            m_TopLeftPanel = new GridSubPanel(this, GridSubPanelType.TopLeft);
            m_ScrollablePanel = new GridSubPanel(this, GridSubPanelType.Scrollable);
            m_HiddenFocusPanel = new GridSubPanelHidden(this);
            m_PanelDockTop.SuspendLayout();

			m_HiddenFocusPanel.TabIndex = 0;

			Controls.Add(m_HiddenFocusPanel);
			m_HiddenFocusPanel.Location = new Point(0,0);
			m_HiddenFocusPanel.Size = new Size(1, 1);

			m_PanelDockTop.Controls.Add(m_TopPanel);
			m_PanelDockTop.Controls.Add(m_TopLeftPanel);
			m_PanelDockTop.Dock = System.Windows.Forms.DockStyle.Top;

			m_TopLeftPanel.Dock = System.Windows.Forms.DockStyle.Left;

			m_TopPanel.Dock = System.Windows.Forms.DockStyle.Fill;

			m_LeftPanel.Dock = System.Windows.Forms.DockStyle.Left;

			m_ScrollablePanel.Dock = System.Windows.Forms.DockStyle.Fill;

			Controls.Add(m_LeftPanel);
			Controls.Add(m_PanelDockTop);
			Controls.Add(m_ScrollablePanel);

			//hide panel
			m_HiddenFocusPanel.SendToBack();

			m_PanelDockTop.ResumeLayout(false);

			base.CreateDockControls ();
		}

		#endregion

		#region Dispose
		/// <summary> 
		/// Clean up any resources being used.
		/// </summary>
		protected override void Dispose( bool disposing )
		{
			if( disposing )
			{
			}
			base.Dispose( disposing );
		}
		#endregion

		#region Default Cell Width/Height
		private int mDefaultHeight = 20;
		/// <summary>
		/// Indicates the default height of new row
		/// </summary>
		[DefaultValue(20)]
		public int DefaultHeight
		{
			get{return mDefaultHeight;}
			set{mDefaultHeight = value;}
		}

		private int mDefaultWidth = 50;
		/// <summary>
		/// Indicates the default width of new column
		/// </summary>
		[DefaultValue(50)]
		public int DefaultWidth
		{
			get{return mDefaultWidth;}
			set{mDefaultWidth = value;}
		}

		private int mMinimumHeight = 0;
		/// <summary>
		/// Indicates the minimum height of the rows
		/// </summary>
		[DefaultValue(0)]
		public int MinimumHeight
		{
			get{return mMinimumHeight;}
			set{mMinimumHeight = value;}
		}

		private int mMinimumWidth = 0;
		/// <summary>
		/// Indicates the minimum width of the columns
		/// </summary>
		[DefaultValue(0)]
		public int MinimumWidth
		{
			get{return mMinimumWidth;}
			set{mMinimumWidth = value;}
		}
		#endregion

		#region AutoSize
		/// <summary>
		/// Auto size the columns and the rows speified
		/// </summary>
		/// <param name="p_RangeToAutoSize"></param>
		public virtual void AutoSizeCells(Range p_RangeToAutoSize)
		{
			SuspendLayout();
			if (p_RangeToAutoSize.IsEmpty() == false)
			{
				Rows.SuspendLayout();
				Columns.SuspendLayout();
				try
				{
					for (int c = p_RangeToAutoSize.End.Column; c >= p_RangeToAutoSize.Start.Column ; c--)
						Columns.AutoSizeColumn(c,false, p_RangeToAutoSize.Start.Row, p_RangeToAutoSize.End.Row);
					for (int r = p_RangeToAutoSize.End.Row; r >= p_RangeToAutoSize.Start.Row ; r--)
						Rows.AutoSizeRow(r, false, p_RangeToAutoSize.Start.Column, p_RangeToAutoSize.End.Column);
				}
				finally
				{
					//aggiorno top e left
					Rows.ResumeLayout();
					Columns.ResumeLayout();
				}

				//Call this method after calculated Bottom and Right
				if (AutoStretchColumnsToFitWidth)
					Columns.StretchToFit();
				if (AutoStretchRowsToFitHeight)
					Rows.StretchToFit();
			}
			ResumeLayout(false);
		}

		/// <summary>
		/// Auto size all the columns and all the rows with the required width and height
		/// </summary>
		public virtual void AutoSizeCells()
		{
			AutoSizeCells(CompleteRange);
		}

		private bool m_bAutoStretchColumnsToFitWidth = false;
		/// <summary>
		/// True to auto stretch the columns width to always fit the available space, also when the contents of the cell is smaller.
		/// False to leave the original width of the columns
		/// </summary>
		[DefaultValue(false)]
		public bool AutoStretchColumnsToFitWidth
		{
			get{return m_bAutoStretchColumnsToFitWidth;}
			set{m_bAutoStretchColumnsToFitWidth = value;}
		}
		private bool m_bAutoStretchRowsToFitHeight = false;
		/// <summary>
		/// True to auto stretch the rows height to always fit the available space, also when the contents of the cell is smaller.
		/// False to leave the original height of the rows
		/// </summary>
		[DefaultValue(false)]
		public bool AutoStretchRowsToFitHeight
		{
			get{return m_bAutoStretchRowsToFitHeight;}
			set{m_bAutoStretchRowsToFitHeight = value;}
		}
		#endregion

		#region CheckPositions
		/// <summary>
		/// Check if the positions saved are still valid, for example if all the selected cells are still valid positions, if not the selection are removed without calling any other methods.
		/// </summary>
		public virtual void CheckPositions()
		{
			Range complete = CompleteRange;

			if (m_MouseCellPosition.IsEmpty() == false &&
				CompleteRange.Contains(m_MouseCellPosition) == false)
				m_MouseCellPosition = Position.Empty;

			if (m_MouseDownPosition.IsEmpty() == false &&
				CompleteRange.Contains(m_MouseDownPosition) == false)
				m_MouseDownPosition = Position.Empty;

			if (mDragCellPosition.IsEmpty() == false &&
				CompleteRange.Contains(mDragCellPosition) == false)
				mDragCellPosition = Position.Empty;

            //If the selection contains some invalid cells reset the selection state.
			RangeRegion completeRegion = new RangeRegion(complete);
			if ( 
				(Selection.ActivePosition.IsEmpty() == false && complete.Contains(Selection.ActivePosition) == false) ||
				(Selection.IsEmpty() == false && completeRegion.Contains(Selection) == false) 
				)
				Selection.Reset();
		}
		#endregion

        #region Position methods
        /// <summary>
        /// Returns the height and width of a specified Position (Row, Column)
        /// </summary>
        /// <param name="position"></param>
        /// <returns></returns>
        public virtual Size PositionToSize(Position position)
        {
            if (position.IsEmpty())
                return Size.Empty;

            int width = Columns.GetWidth(position.Column);
            int height = Rows.GetHeight(position.Row);

            return new Size(width, height);
        }

        /// <summary>
        /// Get the rectangle of the cell respect to the client area visible, the grid DisplayRectangle.
        /// Returns Rectangle.Empty if the Position is empty or if is not valid.
        /// </summary>
        /// <param name="position"></param>
        /// <returns></returns>
        public Rectangle PositionToRectangle(Position position)
        {
            int? row = GetFirstVisibleScrollableRow();

            int? col = GetFirstVisibleScrollableColumn();

            return PositionToRectangleRelative(row, col, position);
        }

        public virtual Rectangle PositionToRectangleRelative(int? relativeRow, int? relativeCol, Position position)
        {
            if (position.IsEmpty())
                return Rectangle.Empty;

            int x = Columns.GetLeft(relativeCol, position.Column);
            int y = Rows.GetTop(relativeRow, position.Row);

            return new Rectangle(new Point(x, y), PositionToSize(position));
        }

        /// <summary>
        /// Returns the cell at the specified grid view relative point (the point must be relative to the grid display region), SearchInFixedCells = true. Return Position.Empty if no valid cells are found.
        /// </summary>
        /// <param name="point">Point relative to the DisplayRectangle area.</param>
        /// <returns></returns>
        public virtual Position PositionAtPoint(Point point)
        {
            int? firstRow = GetFirstVisibleScrollableRow();
            if (firstRow == null)
                return Position.Empty;

            int? firstCol = GetFirstVisibleScrollableColumn();
            if (firstCol == null)
                return Position.Empty;


            int? row = Rows.RowAtPoint(firstRow.Value, point.Y);
            if (row == null)
                return Position.Empty;

            int? col = Columns.ColumnAtPoint(firstCol.Value, point.X);
            if (col == null)
                return Position.Empty;

            return new Position(row.Value, col.Value);
        }

        #endregion

        #region Range methods

        public Size RangeToSize(Range range)
        {
            if (range.IsEmpty())
                return Size.Empty;

            int width = 0;
            for (int c = range.Start.Column; c <= range.End.Column; c++)
                width += Columns.GetWidth(c);

            int height = 0;
            for (int r = range.Start.Row; r <= range.End.Row; r++)
                height += Rows.GetHeight(r);

            return new Size(width, height);
        }

        /// <summary>
        /// Returns the relative rectangle to the current scrollable area of the specified Range.
        /// Returns a Rectangle.Empty if the Range is not valid. 
        /// Calculates the relative position based on the Range.End property.
        /// </summary>
        /// <param name="range"></param>
        /// <returns></returns>
        public Rectangle RangeToRectangle(Range range)
        {
            int? row = GetFirstVisibleScrollableRow();

            int? col = GetFirstVisibleScrollableColumn();

            return RangeToRectangleRelative(row, col, range);
        }

        public Rectangle RangeToRectangleRelative(int? relativeRow, int? relativeCol, Range range)
        {
            if (range.IsEmpty())
                return Rectangle.Empty;

            int x = Columns.GetLeft(relativeCol, range.Start.Column);
            int y = Rows.GetTop(relativeRow, range.Start.Row);

            //Rectangle end = PositionToRectangle(range.End);
            //if (end.IsEmpty)
            //    return Rectangle.Empty;

            //return new Rectangle(start.X, start.Y, end.Right - start.Left, end.Bottom - start.Top);

            Size size = RangeToSize(range);
            return new Rectangle(new Point(x, y), size);
        }

        /// <summary>
        /// Get the visible range of cells.
        /// </summary>
        /// <param name="displayRect"></param>
        /// <returns></returns>
        public Range RangeAtRectangle(Rectangle displayRect)
        {
            //E' difficile implementare questo metodo perchè bisogna tener conto delle righe e colonne fixed.
            //TODO per ora approssimo che il range sia formato dal primo e l'ultimo elemento della collection ma potrebbe non essere vero, come si può fare
            //forse come nella vecchia versione si potrebbe prendere la parte di celle che risiedono in un unico panel, prendendo come punto di partenza il punto finale dell rettangolo

            int? firstVisibleRow = GetFirstVisibleScrollableRow();
            int? firstVisibleCol = GetFirstVisibleScrollableColumn();
            List<int> rows = Rows.RowsInsideRegion(firstVisibleRow, displayRect.Y, displayRect.Height);
            List<int> columns = Columns.ColumnsInsideRegion(firstVisibleCol, displayRect.X, displayRect.Width);

            return new Range(rows[0], columns[0], rows[rows.Count - 1], columns[columns.Count - 1]);
        }

        #endregion

        #region Cells and scrols methods

        /// <summary>
        /// Returns the first visible scrollable column.
        /// Return null if there isn't a visible column.
        /// </summary>
        /// <returns></returns>
        public int? GetFirstVisibleScrollableColumn()
        {
            int firstVisible = CustomScrollPosition.X + FixedColumns;

            if (firstVisible >= Columns.Count)
                return null;
            else
                return firstVisible;
        }

        /// <summary>
        /// Returns the first visible scrollable row.
        /// Return null if there isn't a visible row.
        /// </summary>
        /// <returns></returns>
        public int? GetFirstVisibleScrollableRow()
        {
            int firstVisible = CustomScrollPosition.Y + FixedRows;

            if (firstVisible >= Rows.Count)
                return null;
            else
                return firstVisible;
        }

        public List<int> GetVisibleRows()
        {
            Rectangle displayRectangle = DisplayRectangle;

            int? firstRow = GetFirstVisibleScrollableRow();
            return Rows.RowsInsideRegion(firstRow, displayRectangle.Y, displayRectangle.Height, false);
        }

        public List<int> GetVisibleColumns()
        {
            Rectangle displayRectangle = DisplayRectangle;

            int? firstCol = GetFirstVisibleScrollableColumn();
            return Columns.ColumnsInsideRegion(firstCol, displayRectangle.X, displayRectangle.Width, false);
        }

        /// <summary>
        /// Returns the logical scroll size (usually Rows and Columns) for the specified display area.
        /// </summary>
        /// <param name="displayRectangle"></param>
        /// <returns></returns>
        protected override Size GetVisibleScrollArea(Rectangle displayRectangle)
        {
            List<int> rows = GetVisibleRows();
            List<int> columns = GetVisibleColumns();

            return new Size(columns.Count, rows.Count);
        }

		/// <summary>
		/// Indicates if the specified cell is visible.
		/// </summary>
		/// <param name="p_Position"></param>
		/// <returns></returns>
		public bool IsCellVisible(Position p_Position)
		{
			Point l_ScrollPosition;
			return !(GetScrollPositionToShowCell(p_Position, out l_ScrollPosition));
		}

        /// <summary>
        /// Indicates if the specified range is visible
        /// </summary>
        /// <param name="range"></param>
        /// <returns></returns>
        public bool IsRangeVisible(Range range)
        {
            List<int> rows = GetVisibleRows();
            List<int> columns = GetVisibleColumns();

            if (rows.Count == 0 || columns.Count == 0)
                return false;

            //All the fixed rows are considered to be visible
            bool isRowVisible = false;
            if (range.Start.Row < FixedRows)
                isRowVisible = true;
            else if (range.Start.Row < rows[rows.Count - 1] &&
                        range.End.Row > rows[0])
                isRowVisible = true;

            bool isColVisible = false;
            if (range.Start.Column < FixedColumns)
                isColVisible = true;
            else if (range.Start.Column < columns[columns.Count - 1] &&
                        range.End.Column > columns[0])
                isColVisible = true;

            return isColVisible && isRowVisible;
        }

		/// <summary>
		/// Return the scroll position that must be set to show a specific cell.
		/// </summary>
		/// <param name="p_Position"></param>
		/// <param name="p_NewScrollPosition"></param>
		/// <returns>Return false if the cell is already visible, return true is the cell is not currently visible.</returns>
		protected virtual bool GetScrollPositionToShowCell(Position position, out Point newScrollPosition)
		{
            Rectangle displayRectangle = DisplayRectangle;

            List<int> rows = GetVisibleRows();
            List<int> columns = GetVisibleColumns();

            if (rows.Contains(position.Row) && columns.Contains(position.Column))
            {
                newScrollPosition = CustomScrollPosition;
                return false;
            }
            else
            {
                CellPositionType posType = GetPositionType(position);
                bool isFixedTop = false;
                if (posType == CellPositionType.FixedTop || posType == CellPositionType.FixedTopLeft)
                    isFixedTop = true;
                bool isFixedLeft = false;
                if (posType == CellPositionType.FixedLeft || posType == CellPositionType.FixedTopLeft)
                    isFixedLeft = true;

                int x;
                if (isFixedLeft)
                    x = 0;
                else
                    x = position.Column - FixedColumns;

                int y;
                if (isFixedTop)
                    y = 0;
                else
                    y = position.Row - FixedRows;

                newScrollPosition = new Point(x, y);
                return true;
            }
		}

        public Range GetVisibleRange()
        {
            return RangeAtRectangle(DisplayRectangle);
        }

		/// <summary>
		/// Scroll the view to show the cell passed
		/// </summary>
		/// <param name="p_Position"></param>
		/// <returns>Returns true if the Cell passed was already visible, otherwise false</returns>
		public bool ShowCell(Position p_Position)
		{
			Point l_newCustomScrollPosition;
			if (GetScrollPositionToShowCell(p_Position, out l_newCustomScrollPosition))
			{
				CustomScrollPosition = l_newCustomScrollPosition;
				//il problema di refresh si verifica solo in caso di FixedRows e ColumnsCount maggiori di 0
				if (FixedRows > 0 || FixedColumns > 0)
					InvalidateCells();

				return false;
			}
			return true;
		}


		/// <summary>
		/// Force a cell to redraw. If Redraw is set to false this function has no effects
		/// </summary>
		/// <param name="p_Position"></param>
		public virtual void InvalidateCell(Position p_Position)
		{
            if (p_Position.IsEmpty() == false && CompleteRange.Contains(p_Position))
            {
                GridSubPanel panel = PanelAtPosition(p_Position);
                if (panel != null && IsRangeVisible(new Range(p_Position)))
                {
                    Rectangle gridRectangle = PositionToRectangle(p_Position);
                    panel.Invalidate(panel.RectangleGridToPanel(gridRectangle), true);
                }
            }
		}

		/// <summary>
		/// Force a range of cells to redraw. If Redraw is set to false this function has no effects
		/// </summary>
		/// <param name="p_Range"></param>
		public void InvalidateRange(Range p_Range)
		{
            p_Range = Range.Intersect(p_Range, CompleteRange); //to ensure the range is valid
            if (p_Range.IsEmpty() == false)
            {
                if (IsRangeVisible(p_Range))
                {
                    Rectangle gridRectangle = RangeToRectangle(p_Range);
                    Invalidate(gridRectangle, true);
                }
            }
		}
		#endregion

		#region Focus

#if !MINI
		/// <summary>
		/// Raises the System.Windows.Forms.Control.Leave event.  
		/// </summary>
		/// <param name="e"></param>
		protected override void OnLeave(EventArgs e)
		{
			base.OnLeave (e);

			if ( (Selection.FocusStyle & FocusStyle.RemoveFocusCellOnLeave) == FocusStyle.RemoveFocusCellOnLeave)
			{
				Selection.Focus(Position.Empty);
			}

			if ( (Selection.FocusStyle & FocusStyle.RemoveSelectionOnLeave) == FocusStyle.RemoveSelectionOnLeave)
			{
				Selection.Clear( new Range( Selection.ActivePosition ) );
			}		
		}
#else
		/// <summary>
		/// Raises the System.Windows.Forms.Control.Leave event.  
		/// </summary>
		/// <param name="e"></param>
		protected override void OnLostFocus(EventArgs e)
		{
			base.OnLostFocus (e);

			if ( (Selection.FocusStyle & FocusStyle.RemoveFocusCellOnLeave) == FocusStyle.RemoveFocusCellOnLeave)
			{
				Selection.Focus(Position.Empty);
			}

			if ( (Selection.FocusStyle & FocusStyle.RemoveSelectionOnLeave) == FocusStyle.RemoveSelectionOnLeave)
			{
				Selection.Clear(Selection.ActivePosition);
			}		
		}
#endif
		#endregion

		#region Row/Column Span
		/// <summary>
		/// This method converts a Position to the real start position of cell. This is usefull when RowSpan or ColumnSPan is greater than 1.
		/// For example suppose to have at grid[0,0] a cell with ColumnSpan equal to 2. If you call this method with the position 0,0 returns 0,0 and if you call this method with 0,1 return again 0,0.
		/// Get the real position for the specified position. For example when p_Position is a merged cell this method returns the starting position of the merged cells.
		/// Usually this method returns the same cell specified as parameter. This method is used for processing arrow keys, to find a valid cell when the focus is in a merged cell.
		/// For this class returns always p_Position.
		/// </summary>
		/// <param name="p_Position"></param>
		/// <returns></returns>
		public Position PositionToStartPosition(Position p_Position)
		{
			return PositionToCellRange(p_Position).Start;
		}

		/// <summary>
		/// This method converts a Position to the real range of the cell. This is usefull when RowSpan or ColumnSpan is greater than 1.
		/// For example suppose to have at grid[0,0] a cell with ColumnSpan equal to 2. If you call this method with the position 0,0 returns 0,0-0,1 and if you call this method with 0,1 return again 0,0-0,1.
		/// </summary>
		/// <param name="pPosition"></param>
		/// <returns></returns>
		public virtual Range PositionToCellRange(Position pPosition)
		{
			return new Range(pPosition);
		}
		#endregion


		#region Drag Fields
		/// <summary>
		/// Indica la cella che ha subito l'ultimo evento di DragEnter
		/// </summary>
		private Position mDragCellPosition = Position.Empty;

		/// <summary>
		/// The last cell that has received a DragEnter event.
		/// </summary>
		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public Position DragCellPosition
		{
			get{return mDragCellPosition;}
		}

		/// <summary>
		/// Fired when the cell in the drag events change. For internal use only.
		/// </summary>
		/// <param name="cell"></param>
		/// <param name="pDragEventArgs"></param>
		public virtual void ChangeDragCell(CellContext cell, DragEventArgs pDragEventArgs)
		{
			if (cell.Position != mDragCellPosition)
			{
				if (mDragCellPosition.IsEmpty() == false)
					Controller.OnDragLeave(new CellContext(this, mDragCellPosition, GetCell(mDragCellPosition)), pDragEventArgs);
				
				if (cell.Position.IsEmpty() == false)
					Controller.OnDragEnter(cell, pDragEventArgs);

				mDragCellPosition = cell.Position;
			}
		}
		#endregion


		#region Selection
#if !MINI
		//non supportato nel compact framework per l'assenza dei metodi OnMouseEnter e OnMouseLeave

		/// <summary>
		/// indica l'ultima cella su cui il mouse è stato spostato 
		/// serve per la gestione dell'evento Cell.MouseLeave e MouseEnter
		/// </summary>
		protected Position m_MouseCellPosition = Position.Empty;

		/// <summary>
		/// The cell position currently under the mouse cursor (row, col). If you MouseDown on a cell this cell is the MouseCellPosition until an MouseUp is fired
		/// </summary>
		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public Position MouseCellPosition
		{
			get{return m_MouseCellPosition;}
		}

		/// <summary>
		/// Fired when the cell under the mouse change. For internal use only.
		/// </summary>
		/// <param name="p_Cell"></param>
		public virtual void ChangeMouseCell(Position p_Cell)
		{
			if (m_MouseCellPosition != p_Cell)
			{
				if (m_MouseCellPosition.IsEmpty() == false &&
					m_MouseCellPosition != m_MouseDownPosition) //se la cella che sta perdento il mouse è anche quella che ha ricevuto un eventuale evento di MouseDown non scateno il MouseLeave (che invece verrà scatenato dopo il MouseUp)
				{
					Controller.OnMouseLeave(new CellContext(this, m_MouseCellPosition), EventArgs.Empty);
				}

				m_MouseCellPosition = p_Cell;
				if (m_MouseCellPosition.IsEmpty() == false)
				{
					Controller.OnMouseEnter(new CellContext(this, m_MouseCellPosition), EventArgs.Empty);
				}
			}
		}
#endif

		/// <summary>
		/// Change the cell currently under the mouse
		/// </summary>
		/// <param name="p_MouseDownCell"></param>
		/// <param name="p_MouseCell"></param>
		public virtual void ChangeMouseDownCell(Position p_MouseDownCell, Position p_MouseCell)
		{
			m_MouseDownPosition = p_MouseDownCell;
#if !MINI
			ChangeMouseCell(p_MouseCell);
#endif
		}

		/// <summary>
		/// Fired when the selection eith the mouse is finished
		/// </summary>
		/// <param name="e"></param>
		protected virtual void OnMouseSelectionFinish(RangeEventArgs e)
		{
			m_OldMouseSelectionRange = Range.Empty;
		}

		/// <summary>
		/// Returns the cells that are selected with the mouse. Range.Empty if no cells are selected. Consider that this method returns valid cells only during the mouse down operations, when release the mouse the cells are selected and you can read them using Grid.Selection object.
		/// </summary>
		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public virtual Range MouseSelectionRange
		{
			get{return m_MouseSelectionRange;}
		}

		/// <summary>
		/// Fired when the mouse selection must be canceled. See also MouseSelectionRange.
		/// </summary>
		/// <param name="e"></param>
		protected virtual void OnUndoMouseSelection(RangeEventArgs e)
		{
			Selection.Remove(e.Range);
		}

		/// <summary>
		/// Fired when the mouse selection is succesfully finished. See also MouseSelectionRange.
		/// </summary>
		/// <param name="e"></param>
		protected virtual void OnApplyMouseSelection(RangeEventArgs e)
		{
			Selection.Add(e.Range);
		}

		/// <summary>
		/// Fired when the mouse selection change. See also MouseSelectionRange.
		/// </summary>
		/// <param name="e"></param>
		protected virtual void OnMouseSelectionChange(EventArgs e)
		{
			Range l_MouseRange = MouseSelectionRange;

			OnUndoMouseSelection(new RangeEventArgs(m_OldMouseSelectionRange));

			OnApplyMouseSelection(new RangeEventArgs(l_MouseRange));

			m_OldMouseSelectionRange = l_MouseRange;
		}

		/// <summary>
		/// Fired when the mouse selection finish. See also MouseSelectionRange.
		/// </summary>
		public void MouseSelectionFinish()
		{
			if (m_MouseSelectionRange != Range.Empty)
				OnMouseSelectionFinish(new RangeEventArgs(m_OldMouseSelectionRange));

			m_MouseSelectionRange = Range.Empty;
		}

		/// <summary>
		/// Fired when the corner of the mouse selection change. For internal use only.
		/// </summary>
		/// <param name="p_Corner"></param>
		public virtual void ChangeMouseSelectionCorner(Position p_Corner)
		{
			Range newMouseSelection = new Range(Selection.ActivePosition, p_Corner);

			bool l_bChange = false;
			if (m_MouseSelectionRange != newMouseSelection)
			{
				m_MouseSelectionRange = newMouseSelection;
				l_bChange = true;
			}

			if (l_bChange)
				OnMouseSelectionChange(EventArgs.Empty);
		}

		private Range m_MouseSelectionRange = Range.Empty;
		private Range m_OldMouseSelectionRange = Range.Empty;

		private Selection m_Selection;
		/// <summary>
		/// Gets the selected cells
		/// </summary>
		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public Selection Selection
		{
			get{return m_Selection;}
		}
		#endregion

		#region Mouse Properties

		/// <summary>
		/// Represents the cell that receive the mouse down event
		/// </summary>
		protected Position m_MouseDownPosition = Position.Empty; 

		/// <summary>
		/// Represents the cell that have received the MouseDown event. You can use this cell for contextmenu logic. Can be null.
		/// </summary>
		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public Position MouseDownPosition
		{
			get{return m_MouseDownPosition;}
		}


		#endregion

		#region Special Keys

		private GridSpecialKeys m_GridSpecialKeys = GridSpecialKeys.Default;

		/// <summary>
		/// Special keys that the grid can handle. You can change this enum to block or allow some special keys function. For example to disable Ctrl+C Copy operation remove from this enum the GridSpecialKeys.Ctrl_C.
		/// </summary>
        [DefaultValue(GridSpecialKeys.Default)]
		public GridSpecialKeys SpecialKeys
		{
			get{return m_GridSpecialKeys;}
			set{m_GridSpecialKeys = value;}
		}

		private bool mAcceptsInputChar = true;
		/// <summary>
		/// True accepts input char when the grid has the focus. Used for example to start the edit operation without processing the char.
		/// If you set this property to false when the character is sended to the windows forms handler and can be used for example to execute directly some access key (shortcut keys).
		/// Default is true.
		/// </summary>
		[DefaultValue(true)]
		public bool AcceptsInputChar
		{
			get{return mAcceptsInputChar;}
			set{mAcceptsInputChar = value;}
		}

		/// <summary>
		/// Process Delete, Ctrl+C, Ctrl+V, Up, Down, Left, Right, Tab keys 
		/// </summary>
		/// <param name="e"></param>
		public virtual void ProcessSpecialGridKey(KeyEventArgs e)
		{
			if (e.Handled)
				return;

			bool enableArrows,enableTab,enablePageDownUp;
			enableArrows = enableTab = enablePageDownUp = false;

			if ( (SpecialKeys & GridSpecialKeys.Arrows) == GridSpecialKeys.Arrows)
				enableArrows = true;
			if ( (SpecialKeys & GridSpecialKeys.PageDownUp) == GridSpecialKeys.PageDownUp)
				enablePageDownUp = true;
			if ( (SpecialKeys & GridSpecialKeys.Tab) == GridSpecialKeys.Tab)
				enableTab = true;

			bool enableEscape = false;
			if ( (SpecialKeys & GridSpecialKeys.Escape) == GridSpecialKeys.Escape)
				enableEscape = true;
			bool enableEnter = false;
			if ( (SpecialKeys & GridSpecialKeys.Enter) == GridSpecialKeys.Enter)
				enableEnter = true;

			#region Processing keys
			//Escape
			if (e.KeyCode == Keys.Escape && enableEscape)
			{
				CellContext focusCellContext = new CellContext(this, Selection.ActivePosition);
				if (focusCellContext.Cell != null && focusCellContext.IsEditing())
				{
					if (focusCellContext.EndEdit(true))
						e.Handled = true;
				}
			}

			//Enter
			if (e.KeyCode == Keys.Enter && enableEnter)
			{
				CellContext focusCellContext = new CellContext(this, Selection.ActivePosition);
				if (focusCellContext.Cell != null && focusCellContext.IsEditing())
				{
					focusCellContext.EndEdit(false);

					e.Handled = true;
				}
			}

			//Tab
			if (e.KeyCode == Keys.Tab && enableTab)
			{
				CellContext focusCellContext = new CellContext(this, Selection.ActivePosition);
				if (focusCellContext.Cell != null && focusCellContext.IsEditing())
				{
					//se l'editing non riesce considero il tasto processato 
					// altrimenti no, in questo modo il tab ha effetto anche per lo spostamento
					if (focusCellContext.EndEdit(false) == false)
					{
						e.Handled = true;
						return;
					}
				}
			}
			#endregion

			#region Navigate keys: arrows, tab and PgDown/Up
            if (e.KeyCode == Keys.Down && enableArrows)
            {
                Selection.MoveActiveCell(1, 0);
                e.Handled = true;
            }
            else if (e.KeyCode == Keys.Up && enableArrows)
            {
                Selection.MoveActiveCell(-1, 0);
                e.Handled = true;
            }
            else if (e.KeyCode == Keys.Right && enableArrows)
            {
                Selection.MoveActiveCell(0, 1);
                e.Handled = true;
            }
            else if (e.KeyCode == Keys.Left && enableArrows)
            {
                Selection.MoveActiveCell(0, -1);
                e.Handled = true;
            }
            else if (e.KeyCode == Keys.Tab && enableTab)
            {
                if (e.Modifiers == Keys.Shift) // backward
                {
                    Selection.MoveActiveCell(0, -1, -1, int.MaxValue);
                    e.Handled = true;
                }
                else //forward
                {
                    Selection.MoveActiveCell(0, 1, 1, int.MinValue);
                    e.Handled = true;
                }
            }
            else if ( (e.KeyCode == Keys.PageUp || e.KeyCode == Keys.PageDown)
                   && enablePageDownUp)
            {
                Point focusPoint = PositionToRectangle(Selection.ActivePosition).Location;
                focusPoint.Offset(1, 1); //in modo da entrare nella cella

                if (e.KeyCode == Keys.PageDown)
                    CustomScrollPageDown();
                else if (e.KeyCode == Keys.PageUp)
                    CustomScrollPageUp();

                Position newPosition = PositionAtPoint(focusPoint);
                if (Selection.CanReceiveFocus(newPosition))
                    Selection.Focus(newPosition);

                e.Handled = true;
            }

            //if (Selection.ActivePosition.IsEmpty())
            //{
            //    if ((e.KeyCode == Keys.Down && enableArrows) ||
            //        (e.KeyCode == Keys.Up && enableArrows) ||
            //        (e.KeyCode == Keys.Right && enableArrows) ||
            //        (e.KeyCode == Keys.Left && enableArrows) ||
            //        (e.KeyCode == Keys.Tab && enableTab) ||
            //        ((e.KeyCode == Keys.PageUp || e.KeyCode == Keys.PageDown)
            //        && enablePageDownUp))
            //    {
            //        Selection.FocusFirstCell(true);
            //        e.Handled = true;
            //    }
            //}
            //else
            //{
            //    Cells.ICellVirtual focusCell = GetCell(Selection.ActivePosition);
            //    if (focusCell != null)
            //    {
            //        Cells.ICellVirtual tmp = null;
            //        Position newPosition = Position.Empty;
            //        if (e.KeyCode == Keys.Down && enableArrows)
            //        {
            //            int tmpRow = Selection.ActivePosition.Row;
            //            tmpRow++;
            //            while (tmp == null && tmpRow < Rows.Count)
            //            {
            //                newPosition = new Position(tmpRow, Selection.ActivePosition.Column);
            //                //verifico che la posizione di partenza non coincida con quella di focus, altrimenti significa che ci stiamo spostando sulla stessa cella perchè usa un RowSpan/ColSpan
            //                if (PositionToStartPosition(newPosition) == Selection.ActivePosition)
            //                    tmp = null;
            //                else
            //                {
            //                    tmp = GetCell(newPosition);
            //                    if (tmp != null && Controller.CanReceiveFocus(new CellContext(this, newPosition, tmp), EventArgs.Empty) == false)
            //                        tmp = null;
            //                }

            //                tmpRow++;
            //            }
            //        }
            //        else if (e.KeyCode == Keys.Up && enableArrows)
            //        {
            //            int tmpRow = Selection.ActivePosition.Row;
            //            tmpRow--;
            //            while (tmp == null && tmpRow >= 0)
            //            {
            //                newPosition = new Position(tmpRow, Selection.ActivePosition.Column);
            //                //verifico che la posizione di partenza non coincida con quella di focus, altrimenti significa che ci stiamo spostando sulla stessa cella perchè usa un RowSpan/ColSpan
            //                if (PositionToStartPosition(newPosition) == Selection.ActivePosition)
            //                    tmp = null;
            //                else
            //                {
            //                    tmp = GetCell(newPosition);
            //                    if (tmp != null && Controller.CanReceiveFocus(new CellContext(this, newPosition, tmp), EventArgs.Empty) == false)
            //                        tmp = null;
            //                }

            //                tmpRow--;
            //            }
            //        }
            //        else if (e.KeyCode == Keys.Right && enableArrows)
            //        {
            //            int tmpCol = Selection.ActivePosition.Column;
            //            tmpCol++;
            //            while (tmp == null && tmpCol < Columns.Count)
            //            {
            //                newPosition = new Position(Selection.ActivePosition.Row, tmpCol);
            //                //verifico che la posizione di partenza non coincida con quella di focus, altrimenti significa che ci stiamo spostando sulla stessa cella perchè usa un RowSpan/ColSpan
            //                if (PositionToStartPosition(newPosition) == Selection.ActivePosition)
            //                    tmp = null;
            //                else
            //                {
            //                    tmp = GetCell(newPosition);
            //                    if (tmp != null && Controller.CanReceiveFocus(new CellContext(this, newPosition, tmp), EventArgs.Empty) == false)
            //                        tmp = null;
            //                }

            //                tmpCol++;
            //            }
            //        }
            //        else if (e.KeyCode == Keys.Left && enableArrows)
            //        {
            //            int tmpCol = Selection.ActivePosition.Column;
            //            tmpCol--;
            //            while (tmp == null && tmpCol >= 0)
            //            {
            //                newPosition = new Position(Selection.ActivePosition.Row, tmpCol);
            //                //verifico che la posizione di partenza non coincida con quella di focus, altrimenti significa che ci stiamo spostando sulla stessa cella perchè usa un RowSpan/ColSpan
            //                if (PositionToStartPosition(newPosition) == Selection.ActivePosition)
            //                    tmp = null;
            //                else
            //                {
            //                    tmp = GetCell(newPosition);
            //                    if (tmp != null && Controller.CanReceiveFocus(new CellContext(this, newPosition, tmp), EventArgs.Empty) == false)
            //                        tmp = null;
            //                }
							
            //                tmpCol--;
            //            }
            //        }
            //        else if (e.KeyCode == Keys.Tab && enableTab)//se è premuto tab e non ho trovato nessuna cella provo a muovermi sulla riga in basso e partendo nuovamente dall'inizio ricerco una cella valida
            //        {
            //            int tmpRow = Selection.ActivePosition.Row;
            //            int tmpCol = Selection.ActivePosition.Column;
            //            //indietro
            //            if (e.Modifiers == Keys.Shift)
            //            {
            //                tmpCol--;
            //                while (tmp == null && tmpRow >= 0)
            //                {
            //                    while (tmp == null && tmpCol >= 0)
            //                    {
            //                        newPosition = new Position(tmpRow,tmpCol);
            //                        //verifico che la posizione di partenza non coincida con quella di focus, altrimenti significa che ci stiamo spostando sulla stessa cella perchè usa un RowSpan/ColSpan
            //                        if (PositionToStartPosition(newPosition) == Selection.ActivePosition)
            //                            tmp = null;
            //                        else
            //                        {
            //                            tmp = GetCell(newPosition);
            //                            if (tmp != null && Controller.CanReceiveFocus(new CellContext(this, newPosition, tmp), EventArgs.Empty) == false)
            //                                tmp = null;
            //                        }

            //                        tmpCol--;
            //                    }

            //                    tmpRow--;
            //                    tmpCol = Columns.Count - 1;
            //                }
            //            }
            //            else //avanti
            //            {
            //                tmpCol++;
            //                while (tmp == null && tmpRow < Rows.Count)
            //                {
            //                    while (tmp == null && tmpCol < Columns.Count)
            //                    {
            //                        newPosition = new Position(tmpRow,tmpCol);
            //                        //verifico che la posizione di partenza non coincida con quella di focus, altrimenti significa che ci stiamo spostando sulla stessa cella perchè usa un RowSpan/ColSpan
            //                        if (PositionToStartPosition(newPosition) == Selection.ActivePosition)
            //                            tmp = null;
            //                        else
            //                        {
            //                            tmp = GetCell(newPosition);
            //                            if (tmp != null && Controller.CanReceiveFocus(new CellContext(this, newPosition, tmp), EventArgs.Empty) == false)
            //                                tmp = null;
            //                        }

            //                        tmpCol++;
            //                    }

            //                    tmpRow++;
            //                    tmpCol = 0;
            //                }
            //            }
            //        }
            //        else if ( (e.KeyCode == Keys.PageUp || e.KeyCode == Keys.PageDown) 
            //            && enablePageDownUp)
            //        {
            //            Point focusPoint = PositionToDisplayRect(Selection.ActivePosition).Location;
            //            focusPoint.Offset(1,1); //in modo da entrare nella cella

            //            if (e.KeyCode == Keys.PageDown)
            //                CustomScrollPageDown();
            //            else if (e.KeyCode == Keys.PageUp)
            //                CustomScrollPageUp();

            //            newPosition = PositionAtPoint(focusPoint,false);
            //            tmp = GetCell(newPosition);
            //            if (tmp != null && Controller.CanReceiveFocus(new CellContext(this, newPosition, tmp), EventArgs.Empty)==false)
            //                tmp = null;
            //        }

            //        if (tmp != null && newPosition.IsEmpty() == false)
            //        {
            //            Selection.Focus(newPosition);
            //            e.Handled = true;
            //        }
            //    }
            //}
			#endregion
		}


		#endregion

		#region Grid Events (Click, MouseDown, MouseMove, ...)

		//N.B. Gli argomenti degli eventi di Paint non sono convertiti rispetto alle coordinate relative della griglia
		// mentre gli argomenti degli altri eventi (ad esempio MouseDown, MouseMove, ...) sono convertiti rispetto alle coordinate della GridContainer nei vari pannelli GridSubPanel)

		#region Paint Events
		/// <summary>
		/// Fired when draw Left Panel
		/// </summary>
		/// <param name="e"></param>
		public virtual void OnTopLeftPanelPaint(DevAge.Drawing.GraphicsCache graphics)
		{
			PanelPaint(TopLeftPanel, graphics);
			PanelsUpdate();
		}

		/// <summary>
		/// Fired when draw Left Panel
		/// </summary>
		/// <param name="e"></param>
        public virtual void OnLeftPanelPaint(DevAge.Drawing.GraphicsCache graphics)
		{
			PanelPaint(LeftPanel, graphics);
			PanelsUpdate();
		}

		/// <summary>
		/// Fired when draw Top Panel
		/// </summary>
		/// <param name="e"></param>
        public virtual void OnTopPanelPaint(DevAge.Drawing.GraphicsCache graphics)
		{
			PanelPaint(TopPanel, graphics);
			PanelsUpdate();
		}

		/// <summary>
		/// Fired when draw scrollable panel
		/// </summary>
		/// <param name="e"></param>
        public virtual void OnScrollablePanelPaint(DevAge.Drawing.GraphicsCache graphics)
		{
			PanelPaint(ScrollablePanel, graphics);
			PanelsUpdate();
		}

		/// <summary>
		/// Used to draw (if invalidated) all the panels in the same event.
		/// </summary>
		private void PanelsUpdate()
		{
			ScrollablePanel.Update();
			TopPanel.Update();
			LeftPanel.Update();
			TopLeftPanel.Update();
		}

#if MINI
//		//BACK BUFFER COMPACT FRAMEWORK: gestione Back Buffer per il Compact Framework
//		private Graphics m_BackBufferGraphics;
//		private Bitmap m_BackBufferBitmap;

		//per evitare lo sfarfallio nel compact framework
		//per sostituire SetStyle UserDraw
		protected override void OnPaintBackground(PaintEventArgs e)
		{
			//base.OnPaintBackground (e);
		}

		protected override void OnPaint(PaintEventArgs e)
		{
			//base.OnPaint (e);
		}
#endif

		/// <summary>
		/// Draw the specified region of cells in PaintEventArgs to the GridSubPanel specified
		/// </summary>
		/// <param name="p_Panel"></param>
		/// <param name="e"></param>
        protected virtual void PanelPaint(GridSubPanel p_Panel, DevAge.Drawing.GraphicsCache graphics)
        {
            Rectangle gridRect = p_Panel.RectanglePanelToGrid(graphics.ClipRectangle);

            int? firstVisibleRow = GetFirstVisibleScrollableRow();
            int? firstVisibleCol = GetFirstVisibleScrollableColumn();
            List<int> rows = Rows.RowsInsideRegion(firstVisibleRow, gridRect.Y, gridRect.Height);
            List<int> columns = Columns.ColumnsInsideRegion(firstVisibleCol, gridRect.X, gridRect.Width);

            //Calculate separately left, width, top and height to don't calls always PositionToRectangle
            foreach (int r in rows)
            {
                int top = Rows.GetTop(firstVisibleRow, r);
                int height = Rows.GetHeight(r);

                foreach (int c in columns)
                {
                    int left = Columns.GetLeft(firstVisibleCol, c);
                    int width = Columns.GetWidth(c);

                    Cells.ICellVirtual cell = GetCell(r, c);
                    if (cell != null)
                    {
                        CellContext cellContext = new CellContext(this, new Position(r, c), cell);
                        Rectangle drawRect = new Rectangle(left, top, width, height);
                        drawRect = p_Panel.RectangleGridToPanel(drawRect);

                        PaintCell(cellContext, p_Panel, graphics, drawRect);
                    }
                }
            }

            if (rows.Count > 0 && columns.Count > 0)
            {
                Range range = RangeAtRectangle(gridRect);

                //Draw selection
                m_Selection.DrawSelectionMask(p_Panel, graphics, range);

                //Draw highlighted ranges
                for (int i = 0; i < mHighlightedRanges.Count; i++)
                    mHighlightedRanges[i].DrawHighlight(p_Panel, graphics, range);
            }
        }

		/// <summary>
		/// Draw the specified Cell
		/// </summary>
		/// <param name="cellContext"></param>
		/// <param name="p_Panel"></param>
		/// <param name="e"></param>
		/// <param name="p_PanelDrawRectangle"></param>
		protected virtual void PaintCell(CellContext cellContext, 
                                            GridSubPanel p_Panel, 
                                            DevAge.Drawing.GraphicsCache graphics, 
                                            Rectangle p_PanelDrawRectangle)
		{
			if ( p_PanelDrawRectangle.Width > 0 && p_PanelDrawRectangle.Height > 0 &&
				(cellContext.Cell.Editor == null || cellContext.Cell.Editor.EnableCellDrawOnEdit || 
                cellContext.IsEditing() == false) )
				cellContext.Cell.View.DrawCell(cellContext, graphics, p_PanelDrawRectangle);
		}

		#endregion

		#region MouseEvents
		[Browsable(true)]
		public new event GridMouseEventHandler MouseDown;
		/// <summary>
		/// Fired when receiving a MouseDown event from one of the panels. Use the MouseDown event to add custom code.
		/// </summary>
		/// <param name="e"></param>
		public virtual void OnGridMouseDown(MouseEventArgs e)
		{
			if (MouseDown!=null)
				MouseDown(this, e);
		}
		[Browsable(true)]
		public new event GridMouseEventHandler MouseUp;
		/// <summary>
		/// Fired when receiving a MouseUp event from one of the panels. Use the MouseUp event to add custom code.
		/// </summary>
		/// <param name="e"></param>
		public virtual void OnGridMouseUp(MouseEventArgs e)
		{
			if (MouseUp!=null)
				MouseUp(this, e);
		}
		[Browsable(true)]
		public new event GridMouseEventHandler MouseMove;
		/// <summary>
		/// Fired when receiving a MouseMove event from one of the panels. Use the MouseMove event to add custom code.
		/// </summary>
		/// <param name="e"></param>
		public virtual void OnGridMouseMove(MouseEventArgs e)
		{
			if (MouseMove!=null)
				MouseMove(this, e);
		}

		[Browsable(true)]
		public new event GridMouseEventHandler MouseWheel;
		/// <summary>
		/// Fired when receiving a MouseWheel event from one of the panels. Use the MouseWheel event to add custom code.
		/// </summary>
		/// <param name="e"></param>
		public virtual void OnGridMouseWheel(MouseEventArgs e)
		{
			if (MouseWheel!=null)
				MouseWheel(this, e);
		}

		[Browsable(true)]
		public new event GridEventHandler MouseLeave;
		/// <summary>
		/// Fired when receiving a MouseLeave event from one of the panels. Use the MouseLeave event to add custom code.
		/// </summary>
		/// <param name="e"></param>
		public virtual void OnPanelMouseLeave(EventArgs e)
		{
			if (MouseLeave!=null)
				MouseLeave(this, e);
		}

		[Browsable(true)]
		public new event GridEventHandler MouseEnter;
		/// <summary>
		/// Fired when receiving a MouseEnter event from one of the panels. Use the MouseEnter event to add custom code.
		/// </summary>
		/// <param name="e"></param>
		public virtual void OnPanelMouseEnter(EventArgs e)
		{
			if (MouseEnter!=null)
				MouseEnter(this, e);
		}

		[Browsable(true)]
		public new event GridEventHandler MouseHover;
		/// <summary>
		/// Fired when receiving a MouseHover event from one of the panels. Use the MouseHover event to add custom code.
		/// </summary>
		/// <param name="e"></param>
		public virtual void OnGridMouseHover(EventArgs e)
		{
			if (MouseHover!=null)
				MouseHover(this, e);
		}
		#endregion

#if !MINI
		#region Drag Events
		[Browsable(true)]
		public new event GridDragEventHandler DragDrop;
		public virtual void OnGridDragDrop(DragEventArgs e)
		{
			if (DragDrop!=null)
				DragDrop(this, e);
		}
		[Browsable(true)]
		public new event GridDragEventHandler DragEnter;
		public virtual void OnGridDragEnter(DragEventArgs e)
		{
			if (DragEnter!=null)
				DragEnter(this, e);
		}
		[Browsable(true)]
		public new event GridEventHandler DragLeave;
		public virtual void OnGridDragLeave(EventArgs e)
		{
			if (DragLeave!=null)
				DragLeave(this, e);
		}
		[Browsable(true)]
		public new event GridDragEventHandler DragOver;
		public virtual void OnGridDragOver(DragEventArgs e)
		{
			if (DragOver!=null)
				DragOver(this, e);
		}

		[Browsable(true)]
		public new event GridGiveFeedbackEventHandler GiveFeedback;
		public virtual void OnGridGiveFeedback(GiveFeedbackEventArgs e)
		{
			if (GiveFeedback!=null)
				GiveFeedback(this, e);
		}
		#endregion
#endif

		#region ClickEvents
		[Browsable(true)]
		public new event GridEventHandler Click;
		public virtual void OnGridClick(EventArgs e)
		{
			if (Click!=null)
				Click(this, e);
		}
		[Browsable(true)]
		public new event GridEventHandler DoubleClick;
		public virtual void OnGridDoubleClick(EventArgs e)
		{
			if (DoubleClick!=null)
				DoubleClick(this, e);
		}
		#endregion

		#region Keys
		[Browsable(true)]
		public new event GridKeyEventHandler KeyDown;
		public virtual void OnGridKeyDown(KeyEventArgs e)
		{
			if (KeyDown!=null)
				KeyDown(this, e);
		}
		[Browsable(true)]
		public new event GridKeyEventHandler KeyUp;
		public virtual void OnGridKeyUp(KeyEventArgs e)
		{
			if (KeyUp!=null)
				KeyUp(this, e);
		}
		[Browsable(true)]
		public new event GridKeyPressEventHandler KeyPress;
		public virtual void OnGridKeyPress(KeyPressEventArgs e)
		{
			if (KeyPress!=null)
				KeyPress(this, e);
		}
		#endregion

		#endregion

		#region Controls linked
		private LinkedControlsList m_LinkedControls = new LinkedControlsList();

		/// <summary>
		/// List of controls that are linked to a specific cell position. For example is used for editors controls. Key=Control, Value=Position. The controls are automatically removed from the list when they are removed from the Grid.Controls collection
		/// </summary>
		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public LinkedControlsList LinkedControls
		{
			get{return m_LinkedControls;}
		}

		/// <summary>
		/// OnHScrollPositionChanged
		/// </summary>
		/// <param name="e"></param>
		protected override void OnHScrollPositionChanged(ScrollPositionChangedEventArgs e)
		{
			base.OnHScrollPositionChanged (e);

			ArrangeLinkedControls();
		}

		/// <summary>
		/// OnVScrollPositionChanged
		/// </summary>
		/// <param name="e"></param>
		protected override void OnVScrollPositionChanged(ScrollPositionChangedEventArgs e)
		{
			base.OnVScrollPositionChanged (e);

			ArrangeLinkedControls();
		}

		/// <summary>
		/// Refresh the linked controls bounds
		/// </summary>
		public virtual void ArrangeLinkedControls()
		{
			SuspendLayout();
			foreach (DictionaryEntry e in m_LinkedControls)
			{
				LinkedControlValue linkedValue = (LinkedControlValue)e.Value;
				Control control = (Control)e.Key;
				Cells.ICellVirtual cell = GetCell(linkedValue.Position);

				GridSubPanel panel = PanelAtPosition(linkedValue.Position);
				if (panel == null)
					throw new SourceGridException("Invalid position, panel not found");


				Rectangle rect = panel.RectangleGridToPanel(PositionToRectangle(linkedValue.Position));

				if (cell != null && linkedValue.UseCellBorder)
                    rect = cell.View.Border.RemoveBorderFromRectangle(rect);

                control.Bounds = rect;
			}
			ResumeLayout(false);
		}

#if !MINI
		/// <summary>
		/// Fired when you remove a linked control from the grid.
		/// </summary>
		/// <param name="e"></param>
		protected override void OnControlRemoved(ControlEventArgs e)
		{
			base.OnControlRemoved (e);

			if (LinkedControls.ContainsKey(e.Control))
				LinkedControls.Remove(e.Control);
		}
#endif

		#endregion

		#region Layout
		/// <summary>
		/// Recalculate the scrollbar position and value based on the current cells, scroll client area, linked controls and more. If redraw == false this method has not effect. This method is called when you put Redraw = true;
		/// </summary>
		private void PerformStretch()
		{
			if (AutoStretchColumnsToFitWidth || AutoStretchRowsToFitHeight)
			{
				if (AutoStretchColumnsToFitWidth && AutoStretchRowsToFitHeight)
				{
					Rows.AutoSize(false);
					Columns.AutoSize(false);
					Columns.StretchToFit();
					Rows.StretchToFit();
				}
				else if (AutoStretchColumnsToFitWidth)
				{
					Columns.AutoSize(true);
					Columns.StretchToFit();
				}
				else if (AutoStretchRowsToFitHeight)
				{
					Rows.AutoSize(true);
					Rows.StretchToFit();
				}
			}
		}

		protected override void OnResize(EventArgs e)
		{
			base.OnResize (e);

			SuspendLayout();
            SetScrollArea();
			PerformStretch();
			ResumeLayout(true);
		}


		/// <summary>
		/// Force to recalculate scrollbars and panels location. Used usually after changing width and height of the columns / rows, or after adding or removing rows and columns.
		/// </summary>
		public virtual void OnCellsAreaChanged()
		{
			SuspendLayout();
			ArrangePanelsLocation();
            SetScrollArea();
			CheckPositions();
			ArrangeLinkedControls();
			ResumeLayout(true);
			InvalidateCells();
		}

		/// <summary>
		/// Invalidate all the cells.
		/// </summary>
		public virtual void InvalidateCells()
		{
			InvalidateScrollableArea();
		}

        private void SetScrollArea()
        {
            int vertPage = Math.Max(Rows.Count / 10, 1);
            int horPage = Math.Max(Columns.Count / 10, 1);

            Size scrollArea = new Size(Columns.Count, Rows.Count);

            LoadScrollArea(scrollArea, vertPage, horPage);
                
        }
		#endregion

		#region Sort Range
		/// <summary>
		/// Sort a range of the grid
		/// </summary>
		/// <param name="p_RangeToSort">Range to sort</param>
        /// <param name="keyColumn">Index of the column relative to the grid to use as sort keys, must be between start and end col of the range</param>
		/// <param name="p_bAsc">Ascending true, Descending false</param>
		/// <param name="p_CellComparer">CellComparer, if null the default comparer will be used</param>
		public void SortRangeRows(IRangeLoader p_RangeToSort,
            int keyColumn, 
			bool p_bAsc,
			IComparer p_CellComparer)
		{
			Range l_Range = p_RangeToSort.GetRange(this);
            SortRangeRows(l_Range, keyColumn, p_bAsc, p_CellComparer);
		}

		/// <summary>
		/// Sort a range of the grid.
		/// </summary>
		/// <param name="p_Range"></param>
        /// <param name="keyColumn">Index of the column relative to the grid to use as sort keys, must be between start and end col</param>
		/// <param name="p_bAscending">Ascending true, Descending false</param>
		/// <param name="p_CellComparer">CellComparer, if null the default ValueCellComparer comparer will be used</param>
		public void SortRangeRows(Range p_Range,
            int keyColumn, 
			bool p_bAscending,
			IComparer p_CellComparer)
		{
            SortRangeRowsEventArgs eventArgs = new SortRangeRowsEventArgs(p_Range, keyColumn, p_bAscending, p_CellComparer);

			if (SortingRangeRows!=null)
				SortingRangeRows(this, eventArgs);

			OnSortingRangeRows(eventArgs);

			if (SortedRangeRows!=null)
				SortedRangeRows(this, eventArgs);

			OnSortedRangeRows(eventArgs);
		}

		/// <summary>
		/// Fired when calling SortRangeRows method
		/// </summary>
		[Browsable(true)]
		public event SortRangeRowsEventHandler SortingRangeRows;

		/// <summary>
		/// Fired after calling SortRangeRows method
		/// </summary>
		[Browsable(true)]
		public event SortRangeRowsEventHandler SortedRangeRows;

		/// <summary>
		/// Fired when calling SortRangeRows method
		/// </summary>
		/// <param name="e"></param>
		protected virtual void OnSortingRangeRows(SortRangeRowsEventArgs e)
		{
		}
		/// <summary>
		/// Fired after calling SortRangeRows method
		/// </summary>
		/// <param name="e"></param>
		protected virtual void OnSortedRangeRows(SortRangeRowsEventArgs e)
		{
		}
		#endregion

		#region ProcessCmdKey
		/// <summary>
		/// Processes a command key. 
		/// </summary>
		/// <param name="msg"></param>
		/// <param name="keyData"></param>
		/// <returns></returns>
		protected override bool ProcessCmdKey(
			ref Message msg,
			Keys keyData
			)
		{
			if ( (keyData == Keys.Enter || 
				keyData == Keys.Escape || 
				keyData == Keys.Tab ||
				keyData == (Keys.Tab | Keys.Shift ) ) &&
				OverrideCommonCmdKey )
			{
				KeyEventArgs args = new KeyEventArgs(keyData);
				OnGridKeyDown(args);
				if (args.Handled)
					return true;
				else
					return base.ProcessCmdKey(ref msg,keyData);
			}
			else
				return base.ProcessCmdKey(ref msg,keyData);
		}

		private bool m_OverrideCommonCmdKey = true;
		/// <summary>
		/// True to override with the ProcessCmdKey the common Command Key: Enter, Escape, Tab
		/// </summary>
		[DefaultValue(true)]
		public bool OverrideCommonCmdKey
		{
			get{return m_OverrideCommonCmdKey;}
			set{m_OverrideCommonCmdKey = value;}
		}

		#endregion

		#region GetCell methods

		/// <summary>
		/// Return the Cell at the specified Row and Col position. Abstract, must be implemented in the derived class.
		/// </summary>
		/// <param name="p_iRow"></param>
		/// <param name="p_iCol"></param>
		/// <returns></returns>
		public abstract Cells.ICellVirtual GetCell(int p_iRow, int p_iCol);

		/// <summary>
		/// Return the Cell at the specified Row and Col position. This method is called for sort operations and for Move operations. If position is Empty return null. This method calls GetCell(int p_iRow, int p_iCol)
		/// </summary>
		/// <param name="p_Position"></param>
		/// <returns></returns>
		public Cells.ICellVirtual GetCell(Position p_Position)
		{
			if (p_Position.IsEmpty())
				return null;
			else
				return GetCell(p_Position.Row, p_Position.Column);
		}

		/// <summary>
		/// Returns all the cells at specified row position
		/// </summary>
		/// <param name="p_RowIndex"></param>
		/// <returns></returns>
		public virtual Cells.ICellVirtual[] GetCellsAtRow(int p_RowIndex)
		{
			Cells.ICellVirtual[] l_Cells = new Cells.ICellVirtual[Columns.Count];
			for (int c = 0; c < Columns.Count; c++)
				l_Cells[c] = GetCell(p_RowIndex, c);

			return l_Cells;
		}

		/// <summary>
		/// Returns all the cells at specified column position
		/// </summary>
		/// <param name="p_ColumnIndex"></param>
		/// <returns></returns>
		public virtual Cells.ICellVirtual[] GetCellsAtColumn(int p_ColumnIndex)
		{
			Cells.ICellVirtual[] l_Cells = new Cells.ICellVirtual[Rows.Count];
			for (int r = 0; r < Rows.Count; r++)
				l_Cells[r] = GetCell(r, p_ColumnIndex);

			return l_Cells;
		}

		#endregion

		#region Panels
		/// <summary>
		/// This panel contains the TopLeft and the Top panel. Is used for focking purpose.
		/// </summary>
		private Panel m_PanelDockTop;
		/// <summary>
		/// Questo è un pannello nascosto per gestire il focus della cella. Gli editor adesso vengono inseriti nei panelli a seconda della posizione delle celle e quindi per poter rimuovere il focus dalla cella bisogna spostare il focus su un controllo parallelo che non sia parent dell'editor.
		/// </summary>
        private GridSubPanelHidden m_HiddenFocusPanel;
		private GridSubPanel m_LeftPanel;
		private GridSubPanel m_TopPanel;
		private GridSubPanel m_TopLeftPanel;
		private GridSubPanel m_ScrollablePanel;

		/// <summary>
		/// Gets the not scrollable left panel (For RowHeader)
		/// </summary>
		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public GridSubPanel LeftPanel
		{
			get{return m_LeftPanel;}
		}
		/// <summary>
		/// Gets the not scrollable top panel (For ColHeader)
		/// </summary>
		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public GridSubPanel TopPanel
		{
			get{return m_TopPanel;}
		}
		/// <summary>
		/// Gets the not scrollable top+left panel (For Row or Col Header)
		/// </summary>
		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public GridSubPanel TopLeftPanel
		{
			get{return m_TopLeftPanel;}
		}
		/// <summary>
		/// Gets the scrollable panel for normal scrollable cells
		/// </summary>
		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public GridSubPanel ScrollablePanel
		{
			get{return m_ScrollablePanel;}
		}

		/// <summary>
		/// Gets the hidden panel for internal use only. I use this panel to catch mouse and keyboard events.
		/// </summary>
        public GridSubPanelHidden HiddenFocusPanel
		{
			get{return m_HiddenFocusPanel;}
		}

		/// <summary>
		/// Recalculate panel position
		/// </summary>
		private void ArrangePanelsLocation()
		{
			int fixedY = 0;
            if (Rows.Count >= FixedRows && FixedRows > 0)
            {
                fixedY = FixedRows;
                m_PanelDockTop.Height = Rows.GetBottom(0, fixedY - 1);
            }
            else
            {
                m_PanelDockTop.Height = 0;
            }

			int fixedX = 0;
            if (Columns.Count >= FixedColumns && FixedColumns > 0)
            {
                fixedX = FixedColumns;
                m_LeftPanel.Width = Columns.GetRight(0, fixedX - 1);
                m_TopLeftPanel.Width = Columns.GetRight(0, fixedX - 1);
            }
            else
            {
                m_LeftPanel.Width = 0;
                m_TopLeftPanel.Width = 0;
            }
		}

		/// <summary>
		/// Get the panels that contains the specified cells position. Returns null if the position is not valid
		/// </summary>
		/// <param name="p_CellPosition"></param>
		/// <returns></returns>
		public GridSubPanel PanelAtPosition(Position p_CellPosition)
		{
			if (p_CellPosition.IsEmpty() == false)
			{
				CellPositionType l_Type = GetPositionType(p_CellPosition);
				if (l_Type == CellPositionType.FixedTopLeft)
					return TopLeftPanel;
				else if (l_Type == CellPositionType.FixedLeft)
					return LeftPanel;
				else if (l_Type == CellPositionType.FixedTop)
					return TopPanel;
				else if (l_Type == CellPositionType.Scrollable)
					return ScrollablePanel;
				else
					return null;
			}
			else
				return null;
		}


		/// <summary>
		/// Set the focus on the control that contains the cells. Consider that the grid control contains a series of panels, so to set the focus on a cell you must first set the focus on the panels. This method set the Focus on the right panel.
		/// </summary>
		/// <param name="causesValidation"></param>
		public bool SetFocusOnCells(bool causesValidation)
		{
            if (HiddenFocusPanel.ContainsFocus) //check if the cells already have the focus. This is important because the Focus fire some events also when the control already have the focus.
                return true;

			try
			{
				HiddenFocusPanel.CausesValidation = causesValidation;

				return HiddenFocusPanel.Focus();
			}
			finally
			{
				HiddenFocusPanel.CausesValidation = true;
			}
		}

#if !MINI
		/// <summary>
		/// Returns true if the cells have the focus. See also SetFocusOnCells
		/// </summary>
		/// <returns></returns>
		public bool CellsContainsFocus
		{
			get{return HiddenFocusPanel.ContainsFocus;}
		}
#endif

		/// <summary>
		/// Invalidate the scrollable area
		/// </summary>
		protected override void InvalidateScrollableArea()
		{
			m_ScrollablePanel.Invalidate(true);
			m_TopLeftPanel.Invalidate(true);
			m_LeftPanel.Invalidate(true);
			m_TopPanel.Invalidate(true);
		}
		#endregion

		#region Rows, Columns
		private int m_FixedRows = 0;
		/// <summary>
		/// Gets or Sets how many rows are not scrollable
		/// </summary>
		[DefaultValue(0)]
		public virtual int FixedRows
		{
			get{return m_FixedRows;}
			set
			{
				if (m_FixedRows != value)
				{
					m_FixedRows = value;
					OnCellsAreaChanged();
				}
			}
		}
		private int m_FixedCols = 0;
		/// <summary>
		/// Gets or Sets how many cols are not scrollable
		/// </summary>
		[DefaultValue(0)]
		public virtual int FixedColumns
		{
			get{return m_FixedCols;}
			set
			{
				if (m_FixedCols !=  value)
				{
					m_FixedCols = value;
					OnCellsAreaChanged();
				}
			}
		}

		private RowsBase m_Rows;

		/// <summary>
		/// RowsCount informations
		/// </summary>
		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public RowsBase Rows
		{
			get{return m_Rows;}
		}

		private ColumnsBase m_Columns;

		/// <summary>
		/// Columns informations
		/// </summary>
		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public ColumnsBase Columns
		{
			get{return m_Columns;}
		}

		/// <summary>
		/// Returns the type of a cell position
		/// </summary>
		/// <param name="position"></param>
		/// <returns></returns>
		public CellPositionType GetPositionType(Position position)
		{
			if (position.IsEmpty())
				return CellPositionType.Empty;
			else if (position.Row < FixedRows && position.Column < FixedColumns)
				return CellPositionType.FixedTopLeft;
			else if (position.Row < FixedRows)
				return CellPositionType.FixedTop;
			else if (position.Column < FixedColumns)
				return CellPositionType.FixedLeft;
			else
				return CellPositionType.Scrollable;
		}

		/// <summary>
		/// Returns the type of the panel fr a given position
		/// </summary>
		/// <param name="position"></param>
		/// <returns></returns>
		public GridSubPanelType GetSubPanelType(Position position)
		{
			if (position.IsEmpty())
				return GridSubPanelType.Scrollable;
			else if (position.Row < FixedRows && position.Column < FixedColumns)
				return GridSubPanelType.TopLeft;
			else if (position.Row < FixedRows)
				return GridSubPanelType.Top;
			else if (position.Column < FixedColumns)
				return GridSubPanelType.Left;
			else
				return GridSubPanelType.Scrollable;
		}

		/// <summary>
		/// Returns a Range that represents the complete cells of the grid
		/// </summary>
		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public Range CompleteRange
		{
			get
			{
				if (Rows.Count > 0 && Columns.Count > 0)
					return new Range(0, 0, Rows.Count - 1, Columns.Count - 1);
				else
					return Range.Empty;
			}
		}
		#endregion

		#region ToolTip and Cursor
		/// <summary>
		/// True to activate the tooltiptext
		/// </summary>
        [DefaultValue(true)]
		public bool GridToolTipActive
		{
			get{return ScrollablePanel.ToolTipActive;}
			set{ScrollablePanel.ToolTipActive = value;LeftPanel.ToolTipActive=value;TopPanel.ToolTipActive=value;TopLeftPanel.ToolTipActive=value;}
		}

		/// <summary>
		/// Cursor for the container of the cells. This property is used when you set a cursor to a specified cell.
		/// </summary>
		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public Cursor GridCursor
		{
			get{return ScrollablePanel.Cursor;}
			set{ScrollablePanel.Cursor = value;LeftPanel.Cursor=value;TopPanel.Cursor=value;TopLeftPanel.Cursor=value;}
		}

		/// <summary>
		/// ToolTip text of the container of the cells. This property is used when you set a tooltip to a specified cell.
		/// </summary>
		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public string GridToolTipText
		{
			get{return ScrollablePanel.ToolTipText;}
			set{ScrollablePanel.ToolTipText = value;LeftPanel.ToolTipText=value;TopPanel.ToolTipText=value;TopLeftPanel.ToolTipText=value;}
		}
		#endregion

		#region Events Wheel
		//questi eventi non sono gestiti a livello di Panel perchè devono fare riferimento all'intero controllo

		/// <summary>
		/// Fired when a user scroll with the mouse wheel
		/// </summary>
		/// <param name="e"></param>
		protected override void OnMouseWheel(MouseEventArgs e)
		{
			base.OnMouseWheel (e);
			OnGridMouseWheel(e);
		}

		#endregion

		#region Controllers

		private DevAge.ComponentModel.Controller.ControllerContainer mGridController;
		public DevAge.ComponentModel.Controller.ControllerContainer GridController
		{
			get{return mGridController;}
		}

		private Cells.Controllers.ControllerContainer m_Controller;
		[Browsable(false),DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
		public Cells.Controllers.ControllerContainer Controller
		{
			get{return m_Controller;}
		}
		#endregion

		#region Styles
		private Styles.StyleGrid m_StyleGrid;
		public Styles.StyleGrid StyleGrid
		{
			get{return m_StyleGrid;}
			set{m_StyleGrid = value;}
		}
		#endregion

		#region Range Highlight
		private HighlightedRangeCollection mHighlightedRanges = new HighlightedRangeCollection();

		public HighlightedRangeCollection HighlightedRanges
		{
			get{return mHighlightedRanges;}
		}
		#endregion

		#region Exception
		/// <summary>
		/// Event fired when an exception is throw in some method that require a notification to the user.
		/// </summary>
		public event ExceptionEventHandler UserException;

		/// <summary>
		/// Event fired when an exception is throw in some method that require a notification to the user.
		/// If not handled by the user (Handled property = false) a MessageBox is used to display the exception.
		/// </summary>
		/// <param name="e"></param>
		public virtual void OnUserException(ExceptionEventArgs e)
		{
#if DEBUG
			System.Diagnostics.Debug.WriteLine("Exception on editing cell: " + e.Exception.ToString());
#endif

			if (UserException!=null)
				UserException(this, e);

			if (e.Handled == false)
			{
				DevAge.Windows.Forms.ErrorDialog.Show(this, e.Exception, "Error");
				e.Handled = true;
			}
		}
		#endregion

		#region Right To Left Support
		//With Visual Studio 2005 probably I can use the new RightToLeftLayout and TextRenderer class
		// se also
//		http://www.microsoft.com/middleeast/msdn/visualstudio2005.aspx
//		http://www.microsoft.com/middleeast/msdn/mirror.aspx
//      http://msdn2.microsoft.com/en-us/library/fh376txk.aspx
//      http://msdn2.microsoft.com/en-us/library/7d3337xw.aspx
//      "How to: Create Mirrored Windows Forms and Controls"  http://msdn2.microsoft.com/en-us/library/xwbz5ws0.aspx

        const int WS_EX_LAYOUTRTL = 0x400000;
        const int WS_EX_NOINHERITLAYOUT = 0x100000;
        private bool m_Mirrored = false;

        [Description("Change the right-to-left layout."), DefaultValue(false),
        Localizable(true), Category("Appearance"), Browsable(true)]
        public bool Mirrored
        {
            get
            {
                return m_Mirrored;
            }
            set
            {
                if (m_Mirrored != value)
                {
                    m_Mirrored = value;
                    base.OnRightToLeftChanged(EventArgs.Empty);
                }
            }
        }
        protected override CreateParams CreateParams
        {
            get
            {
                CreateParams CP;
                CP = base.CreateParams;
                if (Mirrored)
                    CP.ExStyle = CP.ExStyle | WS_EX_LAYOUTRTL;
                return CP;
            }
        }

        /// <summary>
        /// Hide the RightToLeft property and returns always No.
        /// </summary>
        [Browsable(false)]
        [DefaultValue(RightToLeft.No)]
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public override RightToLeft RightToLeft
        {
            get { return RightToLeft.No; }
            set { }
        }
		#endregion
	}
}