using System;
using System.ComponentModel;
using System.Collections;
using System.Windows.Forms;

namespace SourceGrid
{
	public class CellCancelEventArgs : CellContextEventArgs
	{
		public CellCancelEventArgs(CellContext pCellContext):base(pCellContext)
		{
		}

		private bool m_bCancel = false;
		public bool Cancel
		{
			get{return m_bCancel;}
			set{m_bCancel = value;}
		}
	}

	public delegate void CellCancelEventHandler(object sender, CellCancelEventArgs e);

	public class ValidatingCellEventArgs : CellCancelEventArgs
	{
		private object m_NewValue;
		public ValidatingCellEventArgs(CellContext pCellContext, object p_NewValue):base(pCellContext)
		{
			m_NewValue = p_NewValue;
		}
		public object NewValue
		{
			get{return m_NewValue;}
			set{m_NewValue = value;}
		}
	}
	public delegate void ValidatingCellEventHandler(object sender, ValidatingCellEventArgs e);
	
	public class ScrollPositionChangedEventArgs : EventArgs
	{
		private int m_NewValue;
		private int m_OldValue;
		public int NewValue
		{
			get{return m_NewValue;}
		}
		public int OldValue
		{
			get{return m_OldValue;}
		}
		public int Delta
		{
			get{return m_OldValue-m_NewValue;}
		}

		public ScrollPositionChangedEventArgs(int p_NewValue, int p_OldValue)
		{
			m_NewValue = p_NewValue;
			m_OldValue = p_OldValue;
		}
	}
	public delegate void ScrollPositionChangedEventHandler(object sender, ScrollPositionChangedEventArgs e);

	/// <summary>
	/// EventArgs used by the FocusRowEnter
	/// </summary>
	public class RowEventArgs : EventArgs
	{
		private int m_Row;
		/// <summary>
		/// Row
		/// </summary>
		public int Row
		{
			get{return m_Row;}
			set{m_Row = value;}
		}

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="pRow"></param>
		public RowEventArgs(int pRow)
		{
			m_Row = pRow;
		}
	}

	/// <summary>
	/// EventHandler used by the FocusRowEnter
	/// </summary>
	public delegate void RowEventHandler(object sender, RowEventArgs e);

	/// <summary>
	/// EventArgs used by the FocusRowLeaving
	/// </summary>
	public class RowCancelEventArgs : RowEventArgs
	{
		private bool m_Cancel = false;
		/// <summary>
		/// Row
		/// </summary>
		public bool Cancel
		{
			get{return m_Cancel;}
			set{m_Cancel = value;}
		}

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="pRow"></param>
		public RowCancelEventArgs(int pRow):base(pRow)
		{
		}
	}

	/// <summary>
	/// EventHandler used by the FocusRowLeaving
	/// </summary>
	public delegate void RowCancelEventHandler(object sender, RowCancelEventArgs e);

	/// <summary>
	/// EventArgs used by the FocusColumnEnter
	/// </summary>
	public class ColumnEventArgs : EventArgs
	{
		private int m_Column;
		/// <summary>
		/// Column
		/// </summary>
		public int Column
		{
			get{return m_Column;}
			set{m_Column = value;}
		}

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="pColumn"></param>
		public ColumnEventArgs(int pColumn)
		{
			m_Column = pColumn;
		}
	}

	/// <summary>
	/// EventHandled used by the FocusColumnEnter
	/// </summary>
	public delegate void ColumnEventHandler(object sender, ColumnEventArgs e);

	/// <summary>
	/// EventArgs used by the FocusColumnLeaving
	/// </summary>
	public class ColumnCancelEventArgs : ColumnEventArgs
	{
		private bool m_Cancel;
		/// <summary>
		/// Column
		/// </summary>
		public bool Cancel
		{
			get{return m_Cancel;}
			set{m_Cancel = value;}
		}

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="pColumn"></param>
		public ColumnCancelEventArgs(int pColumn):base(pColumn)
		{
		}
	}

	/// <summary>
	/// EventHandled used by the FocusColumnLeave and FocusColumnEnter
	/// </summary>
	public delegate void ColumnCancelEventHandler(object sender, ColumnCancelEventArgs e);

//
//	public class CellArrayEventArgs : EventArgs
//	{
//		private Cell[] m_Cell;
//		public CellArrayEventArgs(Cell[] p_Cell)
//		{
//			m_Cell = p_Cell;
//		}
//
//		public Cell[] Cells
//		{
//			get{return m_Cell;}
//			set{m_Cell = value;}
//		}
//
//	}

	public class RowInfoEventArgs : EventArgs
	{
		private RowInfo m_RowInfo;
		public RowInfoEventArgs(RowInfo p_RowInfo)
		{
			m_RowInfo = p_RowInfo;
		}

		public RowInfo Row
		{
			get{return m_RowInfo;}
		}
	}

	public delegate void RowInfoEventHandler(object sender, RowInfoEventArgs e);

	public class ColumnInfoEventArgs : EventArgs
	{
		private ColumnInfo m_ColumnInfo;
		public ColumnInfoEventArgs(ColumnInfo p_ColumnInfo)
		{
			m_ColumnInfo = p_ColumnInfo;
		}

		public ColumnInfo Column
		{
			get{return m_ColumnInfo;}
		}
	}

	public delegate void ColumnInfoEventHandler(object sender, ColumnInfoEventArgs e);

	public class IndexRangeEventArgs : EventArgs
	{
		private int m_iStartIndex;
		private int m_iCount;

		public IndexRangeEventArgs(int p_iStartIndex, int p_iCount)
		{
			m_iStartIndex = p_iStartIndex;
			m_iCount = p_iCount;
		}

		public int StartIndex
		{
			get{return m_iStartIndex;}
		}

		public int Count
		{
			get{return m_iCount;}
		}
	}

	public delegate void IndexRangeEventHandler(object sender, IndexRangeEventArgs e);

	public class CellContextEventArgs : EventArgs
	{
		private CellContext mCellContext;
		public CellContextEventArgs(CellContext pCellContext)
		{
			mCellContext = pCellContext;
		}

		public CellContext CellContext
		{
			get{return mCellContext;}
		}
	}

	public delegate void CellContextEventHandler(object sender, CellContextEventArgs e);

	public class ContextMenuEventArgs
	{
		private MenuCollection m_ContextMenu;
		public ContextMenuEventArgs(MenuCollection p_ContextMenu)
		{
			m_ContextMenu = p_ContextMenu;
		}

		public MenuCollection ContextMenu
		{
			get{return m_ContextMenu;}
			set{m_ContextMenu = value;}
		}
	}

	public delegate void ContextMenuEventHandler(object sender, ContextMenuEventArgs e);

	public class GridPaintEventArgs : EventArgs
	{
		private System.Windows.Forms.PaintEventArgs mPaintEventArgs;
		private System.Drawing.Rectangle mPanelDrawRectangle;
		private GridSubPanel mPanel;
		public GridPaintEventArgs(System.Windows.Forms.PaintEventArgs pPaintEventArgs, GridSubPanel pPanel, System.Drawing.Rectangle pPanelDrawRectangle)
		{
			mPaintEventArgs = pPaintEventArgs;
			mPanel = pPanel;
			mPanelDrawRectangle = pPanelDrawRectangle;
		}

		public System.Windows.Forms.PaintEventArgs PaintEventArgs
		{
			get{return mPaintEventArgs;}
			set{mPaintEventArgs = value;}
		}

		public GridSubPanel Panel
		{
			get{return mPanel;}
			set{mPanel = value;}
		}

		public System.Drawing.Rectangle PanelDrawRectangle
		{
			get{return mPanelDrawRectangle;}
			set{mPanelDrawRectangle = value;}
		}
	}

	public delegate void GridPaintEventHandler(object sender, GridPaintEventArgs e);

	public class RangeEventArgs : EventArgs
	{
		private Range m_GridRange;
		public RangeEventArgs(Range p_GridRange)
		{
			m_GridRange = p_GridRange;
		}

		public Range Range
		{
			get{return m_GridRange;}
		}
	}

	public delegate void RangeEventHandler(object sender, RangeEventArgs e);

	public class RangeCancelEventArgs : RangeEventArgs
	{
		public RangeCancelEventArgs(Range p_GridRange):base(p_GridRange)
		{
		}

		private bool m_Cancel = false;
		public bool Cancel
		{
			get{return m_Cancel;}
			set{m_Cancel = value;}
		}
	}
	public delegate void RangeCancelEventHandler(object sender, RangeCancelEventArgs e);

	public class RangeRegionEventArgs : EventArgs
	{
		private RangeRegion m_GridRangeRegion;
		public RangeRegionEventArgs(RangeRegion p_GridRangeRegion)
		{
			m_GridRangeRegion = p_GridRangeRegion;
		}

		public RangeRegion RangeRegion
		{
			get{return m_GridRangeRegion;}
		}
	}

	public delegate void RangeRegionEventHandler(object sender, RangeRegionEventArgs e);

	public class RangeRegionChangingEventArgs : EventArgs
	{
		private RangeRegion mRegionToExclude;
		private RangeRegion mRegionToInclude;
		private RangeRegion mCurrentRegion;
		public RangeRegionChangingEventArgs(RangeRegion pCurrentRegion, RangeRegion pRangeToExclude, RangeRegion pRangeToInclude )
		{
			mRegionToExclude = pRangeToExclude;
			mCurrentRegion = pCurrentRegion;
			mRegionToInclude = pRangeToInclude;
		}

		public RangeRegion CurrentRegion
		{
			get{return mCurrentRegion;}
		}
		public RangeRegion RegionToInclude
		{
			get{return mRegionToInclude;}
		}
		public RangeRegion RegionToExclude
		{
			get{return mRegionToExclude;}
		}
	}

	public delegate void RangeRegionChangingEventHandler(object sender, RangeRegionChangingEventArgs e);

	public class RangeRegionCancelEventArgs : RangeRegionEventArgs
	{
		public RangeRegionCancelEventArgs(RangeRegion p_GridRangeRegion):base(p_GridRangeRegion)
		{
		}

		private bool m_Cancel = false;
		public bool Cancel
		{
			get{return m_Cancel;}
			set{m_Cancel = value;}
		}
	}
	public delegate void RangeRegionCancelEventHandler(object sender, RangeRegionCancelEventArgs e);

	public class SelectionChangeEventArgs : EventArgs
	{
		public SelectionChangeEventArgs(SelectionChangeEventType p_Type, Range p_Range)
		{
			m_Type = p_Type;
			m_Range = p_Range;
		}

		private Range m_Range;
		private SelectionChangeEventType m_Type;

		public Range Range
		{
			get{return m_Range;}
		}

		public SelectionChangeEventType EventType
		{
			get{return m_Type;}
		}
	}

	public delegate void SelectionChangeEventHandler(object sender, SelectionChangeEventArgs e);

	public class ExceptionEventArgs : EventArgs
	{
		public ExceptionEventArgs(Exception p_Exception)
		{
			m_Exception = p_Exception;
		}

		private Exception m_Exception;

		public Exception Exception
		{
			get{return m_Exception;}
		}

		private bool mHandled = false;
		public bool Handled
		{
			get{return mHandled;}
			set{mHandled = value;}
		}
	}

	public delegate void ExceptionEventHandler(object sender, ExceptionEventArgs e);

	public class SortRangeRowsEventArgs : EventArgs
	{
		private Range m_Range;
		private int mKeyColumn;
		private bool m_bAscending;
		private IComparer m_CellComparer;

		public SortRangeRowsEventArgs(Range p_Range,
            int keyColumn, 
			bool p_bAscending,
			IComparer p_CellComparer)
		{
			m_Range = p_Range;
            mKeyColumn = keyColumn;
			m_bAscending = p_bAscending;
			m_CellComparer = p_CellComparer;
		}

		public Range Range
		{
			get{return m_Range;}
		}
		public int KeyColumn
		{
            get { return mKeyColumn; }
		}
		public bool Ascending
		{
			get{return m_bAscending;}
		}
		public IComparer CellComparer
		{
			get{return m_CellComparer;}
		}
	}

	public delegate void SortRangeRowsEventHandler(object sender, SortRangeRowsEventArgs e);


	#region Grid and Selection Events
	public delegate void GridMouseEventHandler(GridVirtual sender, MouseEventArgs e);
	public delegate void GridEventHandler(GridVirtual sender, EventArgs e);
	public delegate void GridDragEventHandler(GridVirtual sender, DragEventArgs e);
	public delegate void GridGiveFeedbackEventHandler(GridVirtual sender, GiveFeedbackEventArgs e);
	public delegate void GridKeyEventHandler(GridVirtual sender, KeyEventArgs e);
	public delegate void GridKeyPressEventHandler(GridVirtual sender, KeyPressEventArgs e);

	/// <summary>
	/// Cell Lost Focus event arguments with the old position and the new position. Extends PositionCancelEventArgs.
	/// </summary>
	public class ChangeActivePositionEventArgs : System.ComponentModel.CancelEventArgs
	{
		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="pOldFocusPosition"></param>
		/// <param name="pNewFocusPosition">If Empty there isn't a cell that will receive the focus.</param>
		public ChangeActivePositionEventArgs(Position pOldFocusPosition, Position pNewFocusPosition):base(false)
		{
			mOldFocusPosition = pOldFocusPosition;
			mNewFocusPosition = pNewFocusPosition;
		}
		private Position mOldFocusPosition;
		/// <summary>
		/// Position that had the focus
		/// </summary>
		public Position OldFocusPosition
		{
			get{return mOldFocusPosition;}
		}

		private Position mNewFocusPosition;
		/// <summary>
		/// Position that will receive the focus. If Empty there isn't a cell that will receive the focus.
		/// </summary>
		public Position NewFocusPosition
		{
			get{return mNewFocusPosition;}
		}
	}
	public delegate void ChangeActivePositionEventHandler(Selection sender, ChangeActivePositionEventArgs e);
	#endregion

}
