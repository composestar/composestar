using System;
using System.Drawing;
using System.Collections;
using System.Collections.Generic;
using System.Windows.Forms;
using System.ComponentModel;

namespace SourceGrid
{
	/// <summary>
	/// Abstract base class for manage rows informations.
	/// </summary>
	public abstract class RowsBase
	{
		private GridVirtual mGrid;

		public RowsBase(GridVirtual grid)
		{
			mGrid = grid;
		}

		public GridVirtual Grid
		{
			get{return mGrid;}
		}

		#region Abstract methods
		public abstract int Count
		{
			get;
		}
	
		/// <summary>
		/// Gets the height of the specified row.
		/// </summary>
		/// <param name="row"></param>
		/// <returns></returns>
		public abstract int GetHeight(int row);
		/// <summary>
		/// Sets the height of the specified row.
		/// </summary>
		/// <param name="row"></param>
		/// <param name="height"></param>
		public abstract void SetHeight(int row, int height);

        public abstract AutoSizeMode GetAutoSizeMode(int row);
		#endregion

        /// <summary>
        /// Gets the rows index inside the specified display area.
        /// </summary>
        /// <param name="relativeRow"></param>
        /// <param name="y"></param>
        /// <param name="height"></param>
        /// <returns></returns>
        public List<int> RowsInsideRegion(int? relativeRow, int y, int height)
        {
            return RowsInsideRegion(relativeRow, y, height, true);
        }

        /// <summary>
        /// Gets the rows index inside the specified display area.
        /// The list returned is ordered by the index.
        /// </summary>
        /// <param name="relativeRow"></param>
        /// <param name="y"></param>
        /// <param name="height"></param>
        /// <param name="returnsPartial">True to returns also partial rows</param>
        /// <returns></returns>
        public List<int> RowsInsideRegion(int? relativeRow, int y, int height, bool returnsPartial)
        {
            int bottom = y + height;

            List<int> list = new List<int>();
            int current = 0;

            //Add the fixed rows
            // Loop until the currentHeight is smaller then the requested displayRect
            for (int fr = 0; fr < Grid.FixedRows && fr < Count; fr++)
            {
                int topDisplay = GetTop(relativeRow, fr);
                int bottomDisplay = topDisplay + GetHeight(fr);

                //If the row is inside the view
                if (bottom >= topDisplay && y < bottomDisplay &&
                    (returnsPartial || (bottomDisplay <= bottom && topDisplay >= y ) ))
                {
                    list.Add(fr);
                    current += GetHeight(fr);
                }

                if (topDisplay > bottom)
                    break;
            }

            if (relativeRow != null)
            {
                //Add the standard rows
                for (int r = relativeRow.Value; r < Count; r++)
                {
                    int topDisplay = GetTop(relativeRow, r);
                    int bottomDisplay = topDisplay + GetHeight(r);

                    //If the row is inside the view
                    if (bottom >= topDisplay && y < bottomDisplay &&
                         (returnsPartial || (bottomDisplay <= bottom && topDisplay >= y)))
                    {
                        list.Add(r);
                        current += GetHeight(r);
                    }

                    if (topDisplay > bottom)
                        break;
                }
            }

            return list;
        }

        /// <summary>
        /// Calculate the Row that have the Top value smaller or equal than the point p_Y, or -1 if not found found.
        /// </summary>
        /// <param name="relativeRow"></param>
        /// <param name="y">Y Coordinate to search for a row</param>
        /// <returns></returns>
        public int? RowAtPoint(int relativeRow, int y)
        {
            List<int> list = RowsInsideRegion(relativeRow, y, 1);
            if (list.Count == 0)
                return null;
            else
                return list[0];
        }

		public void AutoSizeRow(int row)
		{
			AutoSizeRow(row, true, 0, Grid.Columns.Count - 1);
		}
		public void AutoSizeRow(int row, bool useColumnWidth, int StartCol, int EndCol)
		{
            if (( GetAutoSizeMode(row) & AutoSizeMode.EnableAutoSize) == AutoSizeMode.EnableAutoSize)
    			SetHeight(row, MeasureRowHeight(row, useColumnWidth, StartCol, EndCol) );
		}

		/// <summary>
		/// Measures the current row when drawn with the specified cells.
		/// </summary>
		/// <param name="row"></param>
		/// <param name="useColumnWidth">True to fix the column width when calculating the required height of the row.</param>
		/// <param name="StartCol">Start column to measure</param>
		/// <param name="EndCol">End column to measure</param>
		/// <returns>Returns the required height</returns>
		public int MeasureRowHeight(int row, bool useColumnWidth, int StartCol, int EndCol)
		{
			int min = Grid.MinimumHeight;

            if ((GetAutoSizeMode(row) & AutoSizeMode.MinimumSize) == AutoSizeMode.MinimumSize)
                return min;

			for (int c = StartCol; c <= EndCol; c++)
			{
				Cells.ICellVirtual cell = Grid.GetCell(row, c);
				if (cell != null)
				{
					Size maxLayout = Size.Empty;
					if (useColumnWidth)
						maxLayout.Width = Grid.Columns.GetWidth(c);

					CellContext cellContext = new CellContext(Grid, new Position(row, c), cell);
					Size l_size = cellContext.Measure(maxLayout);
					if (l_size.Height > min)
						min = l_size.Height;
				}
			}
			return min;
		}


		public void AutoSize(bool useColumnWidth)
		{
			AutoSize(useColumnWidth, 0, Grid.Columns.Count - 1);
		}

		/// <summary>
		/// Auto size all the rows with the max required height of all cells.
		/// </summary>
		/// <param name="useColumnWidth">True to fix the column width when calculating the required height of the row.</param>
		/// <param name="StartCol">Start column to measure</param>
		/// <param name="EndCol">End column to measure</param>
		public void AutoSize(bool useColumnWidth, int StartCol, int EndCol)
		{
			SuspendLayout();
			for (int i = 0; i < Count; i++)
				AutoSizeRow(i, useColumnWidth, StartCol, EndCol);
			ResumeLayout();
		}

		/// <summary>
		/// stretch the rows height to always fit the available space when the contents of the cell is smaller.
		/// </summary>
		public virtual void StretchToFit()
		{
			SuspendLayout();

            Rectangle displayRect = Grid.DisplayRectangle;

            int? firstVisible = Grid.GetFirstVisibleScrollableRow();

            if (Count > 0 && displayRect.Height > 0 && firstVisible != null)
            {
                List<int> visibleIndex = RowsInsideRegion(firstVisible.Value, displayRect.Y, displayRect.Height);

                //Continue only if the rows are all visible, otherwise this method cannot shirnk the rows
                if (visibleIndex.Count >= Count)
                {
                    int? current = GetBottom(firstVisible.Value, Count - 1);
                    if (current != null && displayRect.Height > current.Value)
                    {
                        //Calculate the columns to stretch
                        int countToStretch = 0;
                        for (int i = 0; i < Count; i++)
                        {
                            if ((GetAutoSizeMode(i) & AutoSizeMode.EnableStretch) == AutoSizeMode.EnableStretch)
                                countToStretch++;
                        }

                        int deltaPerRow = (displayRect.Height - current.Value) / countToStretch;
                        for (int i = 0; i < Count; i++)
                        {
                            if ((GetAutoSizeMode(i) & AutoSizeMode.EnableStretch) == AutoSizeMode.EnableStretch)
                                SetHeight(i, GetHeight(i) + deltaPerRow);
                        }
                    }
                }
            }

			ResumeLayout();
		}

		public Range GetRange(int row)
		{
			return new Range(row, 0, row, Grid.Columns.Count-1);
		}

		#region Layout
		private int mSuspendedCount = 0;
		public void SuspendLayout()
		{
			mSuspendedCount++;
		}
		public void ResumeLayout()
		{
			if (mSuspendedCount > 0)
				mSuspendedCount--;

			PerformLayout();
		}
		public void PerformLayout()
		{
			if (mSuspendedCount == 0)
				OnLayout();
		}
		protected virtual void OnLayout()
		{
			Grid.OnCellsAreaChanged();
		}
		#endregion


		/// <summary>
		/// Fired when the numbers of rows changed.
		/// </summary>
		public void RowsChanged()
		{
			PerformLayout();
        }

        #region Relative methods
        /// <summary>
        /// Gets the row top position.
        /// The Top is relative to the specified start position.
        /// Calculate the top using also the FixedRows if present.
        /// </summary>
        /// <param name="relativeRow">This is the row from wich you want to calculate the top. 
        /// Use 0 if you want to calculate an absolute top value or a specified row to calculate the top from that row.
        /// Use null if there isn't a visible row. In this case the start is considered to be at the end of the row.</param>
        /// <param name="row"></param>
        /// <returns></returns>
        public int GetTop(int? relativeRow, int row)
        {
            int actualFixedRows = Math.Min(Grid.FixedRows, Count);

            int top = 0;

            //Calculate fixed left cells
            for (int i = 0; i < actualFixedRows; i++)
            {
                if (i == row)
                    return top;

                top += GetHeight(i);
            }


            if (relativeRow == null)
                relativeRow = Count;

            if (relativeRow == row)
                return top;
            else if (relativeRow < row)
            {
                for (int i = relativeRow.Value; i < Count; i++)
                {
                    if (i == row)
                        return top;

                    top += GetHeight(i);
                }
            }
            else if (relativeRow > row)
            {
                for (int i = relativeRow.Value - 1; i >= 0; i--)
                {
                    top -= GetHeight(i);

                    if (i == row)
                        return top;
                }
            }

            throw new IndexOutOfRangeException();
        }

        /// <summary>
        /// Gets the row bottom position. GetTop + GetHeight.
        /// </summary>
        /// <param name="relativeRow">This is the row from wich you want to calculate the top. Use 0 if you want to calculate an absolute top value or a specified row to calculate the top from that row.</param>
        /// <param name="row"></param>
        /// <returns></returns>
        public int GetBottom(int? relativeRow, int row)
        {
            int top = GetTop(relativeRow, row);
            return top + GetHeight(row);
        }
        #endregion
    }


	/// <summary>
	/// This class implements a RowsBase class using always the same Height for all rows. Using this class you must only implement the Count method.
	/// </summary>
	public abstract class RowsSimpleBase : RowsBase
	{
		public RowsSimpleBase(GridVirtual grid):base(grid)
		{
			mRowHeight = grid.DefaultHeight;
		}

		private int mRowHeight;
		public int RowHeight
		{
			get{return mRowHeight;}
			set
			{
				if (mRowHeight != value)
				{
					mRowHeight = value;
					PerformLayout();
				}
			}
		}

		public override int GetHeight(int row)
		{
			return RowHeight;
		}
		public override void SetHeight(int row, int height)
		{
			RowHeight = height;
		}
	}

	/// <summary>
	/// Row Information
	/// </summary>
	public class RowInfo
	{
		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="p_Grid"></param>
		public RowInfo(GridVirtual p_Grid)
		{
			m_Grid = p_Grid;
			m_Height = Grid.DefaultHeight;
		}

		private int m_Height;
		/// <summary>
		/// Height of the current row
		/// </summary>
		public int Height
		{
			get{return m_Height;}
			set
			{
				if (value < 0)
					value = 0;

				if (m_Height != value)
				{
					m_Height = value;
					((RowInfoCollection)m_Grid.Rows).OnRowHeightChanged(new RowInfoEventArgs(this));
				}
			}
		}

		//private int m_Index;
		/// <summary>
		/// Index of the current row
		/// </summary>
		public int Index
		{
			get{return ((RowInfoCollection)Grid.Rows).IndexOf(this);}
		}

		private GridVirtual m_Grid;
		/// <summary>
		/// Attached Grid
		/// </summary>
		[Browsable(false)]
		public GridVirtual Grid
		{
			get{return m_Grid;}
		}


		/// <summary>
		/// Gets or sets the cells at the specified row
		/// </summary>
		[Browsable(false)]
		public Cells.ICellVirtual[] Cells
		{
			get
			{
				if (m_Grid == null)
					throw new SourceGridException("Invalid Grid object");
			
				return m_Grid.GetCellsAtRow(Index);
			}
			set
			{
				if (m_Grid == null)
					throw new SourceGridException("Invalid Grid object");

				if (m_Grid is Grid == false)
					throw new SourceGridException("This method is valid only for the Grid class");

				((Grid)m_Grid).SetCellsAtRow(Index, value);
			}
		}

		public Range Range
		{
			get
			{
				if (m_Grid == null)
					throw new SourceGridException("Invalid Grid object");

				return new Range(Index, 0, Index, Grid.Columns.Count - 1);
			}
		}
		private object m_Tag;
		/// <summary>
		/// A property that the user can use to insert custom informations associated to a specific row
		/// </summary>
		[Browsable(false)]
		public object Tag
		{
			get{return m_Tag;}
			set{m_Tag = value;}
		}

		private AutoSizeMode m_AutoSizeMode = AutoSizeMode.Default;
		/// <summary>
		/// Flags for autosize and stretch
		/// </summary>
		public AutoSizeMode AutoSizeMode
		{
			get{return m_AutoSizeMode;}
			set{m_AutoSizeMode = value;}
		}
	}

	#region RowInfoCollection
	/// <summary>
	/// Collection of RowInfo
	/// </summary>
	public abstract class RowInfoCollection : RowsBase, ICollection
	{
		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="grid"></param>
		public RowInfoCollection(GridVirtual grid):base(grid)
		{
		}

		private ArrayList m_List = new ArrayList();

		/// <summary>
		/// Returns true if the range passed is valid
		/// </summary>
		/// <param name="p_StartIndex"></param>
		/// <param name="p_Count"></param>
		/// <returns></returns>
		public bool IsValidRange(int p_StartIndex, int p_Count)
		{
			if (p_StartIndex < Count && p_StartIndex >= 0 &&
				p_Count > 0 && (p_StartIndex+p_Count) <= Count)
				return true;
			else
				return false;
		}

		/// <summary>
		/// Returns true if the range passed is valid for insert method
		/// </summary>
		/// <param name="p_StartIndex"></param>
		/// <param name="p_Count"></param>
		/// <returns></returns>
		public bool IsValidRangeForInsert(int p_StartIndex, int p_Count)
		{
			if (p_StartIndex <= Count && p_StartIndex >= 0 &&
				p_Count > 0)
				return true;
			else
				return false;
		}


		#region Insert/Remove Methods

		/// <summary>
		/// Insert the specified number of rows at the specified position
		/// </summary>
		/// <param name="p_StartIndex"></param>
		/// <param name="rows"></param>
		protected void InsertRange(int p_StartIndex, RowInfo[] rows)
		{
			if (IsValidRangeForInsert(p_StartIndex, rows.Length) == false)
				throw new SourceGridException("Invalid index");

			for (int r = 0; r < rows.Length; r++)
			{
				m_List.Insert(p_StartIndex+r, rows[r]);
			}

			PerformLayout();

			OnRowsAdded(new IndexRangeEventArgs(p_StartIndex, rows.Length));
		}

		/// <summary>
		/// Remove a row at the speicifed position
		/// </summary>
		/// <param name="p_Index"></param>
		public void Remove(int p_Index)
		{
			RemoveRange(p_Index, 1);
		}
		/// <summary>
		/// Remove the RowInfo at the specified positions
		/// </summary>
		/// <param name="p_StartIndex"></param>
		/// <param name="p_Count"></param>
		public void RemoveRange(int p_StartIndex, int p_Count)
		{
			if (IsValidRange(p_StartIndex, p_Count)==false)
				throw new SourceGridException("Invalid index");

			IndexRangeEventArgs eventArgs = new IndexRangeEventArgs(p_StartIndex, p_Count);
			OnRowsRemoving(eventArgs);

			m_List.RemoveRange(p_StartIndex, p_Count);

			OnRowsRemoved(eventArgs);

			PerformLayout();
		}

		#endregion

		/// <summary>
		/// Move a row from one position to another position
		/// </summary>
		/// <param name="p_CurrentRowPosition"></param>
		/// <param name="p_NewRowPosition"></param>
		public void Move(int p_CurrentRowPosition, int p_NewRowPosition)
		{
			if (p_CurrentRowPosition == p_NewRowPosition)
				return;

			if (p_CurrentRowPosition < p_NewRowPosition)
			{
				for (int r = p_CurrentRowPosition; r < p_NewRowPosition; r++)
				{
					Swap(r, r + 1);
				}
			}
			else
			{
				for (int r = p_CurrentRowPosition; r > p_NewRowPosition; r--)
				{
					Swap(r, r - 1);
				}
			}
		}

		/// <summary>
		/// Change the position of row 1 with row 2.
		/// </summary>
		/// <param name="p_RowIndex1"></param>
		/// <param name="p_RowIndex2"></param>
		public void Swap(int p_RowIndex1, int p_RowIndex2)
		{
			if (p_RowIndex1 == p_RowIndex2)
				return;

			RowInfo l_Row1 = this[p_RowIndex1];
			Cells.ICellVirtual[] l_Cells1 = l_Row1.Cells;
			RowInfo l_Row2 = this[p_RowIndex2];
			Cells.ICellVirtual[] l_Cells2 = l_Row2.Cells;

			m_List[p_RowIndex1] = l_Row2;
			m_List[p_RowIndex2] = l_Row1;

			l_Row1.Cells = null;
			l_Row2.Cells = null;

			l_Row1.Cells = l_Cells1;
			l_Row2.Cells = l_Cells2;

			PerformLayout();
		}

		/// <summary>
		/// Fired when the number of rows change
		/// </summary>
		public event IndexRangeEventHandler RowsAdded;

		/// <summary>
		/// Fired when the number of rows change
		/// </summary>
		/// <param name="e"></param>
		protected virtual void OnRowsAdded(IndexRangeEventArgs e)
		{
			if (RowsAdded!=null)
				RowsAdded(this, e);

			RowsChanged();
		}

		/// <summary>
		/// Fired when some rows are removed
		/// </summary>
		public event IndexRangeEventHandler RowsRemoved;

		/// <summary>
		/// Fired when some rows are removed
		/// </summary>
		/// <param name="e"></param>
		protected virtual void OnRowsRemoved(IndexRangeEventArgs e)
		{
			if (RowsRemoved!=null)
				RowsRemoved(this, e);

			RowsChanged();
		}

		/// <summary>
		/// Fired before some rows are removed
		/// </summary>
		public event IndexRangeEventHandler RowsRemoving;

		/// <summary>
		/// Fired before some rows are removed
		/// </summary>
		/// <param name="e"></param>
		protected virtual void OnRowsRemoving(IndexRangeEventArgs e)
		{
			if (RowsRemoving!=null)
				RowsRemoving(this, e);

			//Grid.OnRowsRemoving(e);
		}

		/// <summary>
		/// Indexer. Returns a RowInfo at the specified position
		/// </summary>
		public RowInfo this[int p]
		{
			get{return (RowInfo)m_List[p];}
		}

		protected override void OnLayout()
		{
			base.OnLayout ();
		}

		/// <summary>
		/// Fired when the user change the Height property of one of the Row
		/// </summary>
		public event RowInfoEventHandler RowHeightChanged;

		/// <summary>
		/// Execute the RowHeightChanged event
		/// </summary>
		/// <param name="e"></param>
		public void OnRowHeightChanged(RowInfoEventArgs e)
		{
			PerformLayout();

			if (RowHeightChanged!=null)
				RowHeightChanged(this, e);
		}


		public int IndexOf(RowInfo p_Info)
		{
			return m_List.IndexOf(p_Info);
		}

		/// <summary>
        /// Auto size the rows calculating the required size only on the columns currently visible
        /// </summary>
		public void AutoSizeView()
		{
            int? relative = Grid.GetFirstVisibleScrollableColumn();
            if (relative != null)
            {
                List<int> list = Grid.Columns.ColumnsInsideRegion(relative.Value, Grid.DisplayRectangle.X, Grid.DisplayRectangle.Width);
                if (list.Count > 0)
                {
                    AutoSize(false, list[0], list[list.Count - 1]);
                }
            }
		}

        /// <summary>
		/// Remove all the columns
		/// </summary>
		public void Clear()
		{
			if (Count > 0)
				RemoveRange(0, Count);
		}

		#region RowsBase
		public override int GetHeight(int row)
		{
			return this[row].Height;
		}
		public override void SetHeight(int row, int height)
		{
			this[row].Height = height;
		}
        public override AutoSizeMode GetAutoSizeMode(int row)
        {
            return this[row].AutoSizeMode;
        }
		#endregion

		#region ICollection
		public virtual void CopyTo ( System.Array array , System.Int32 index )
		{
			m_List.CopyTo(array,index);
		}
		public override int Count
		{
			get{return m_List.Count;}
		}

		public bool IsSynchronized
		{
			get{return m_List.IsSynchronized;}
		}
		public object SyncRoot
		{
			get{return m_List.SyncRoot;}
		}
		public virtual System.Collections.IEnumerator GetEnumerator (  )
		{
			return m_List.GetEnumerator();
		}
		#endregion
	}

	#endregion

}
