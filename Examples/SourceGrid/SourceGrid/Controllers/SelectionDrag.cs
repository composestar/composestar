using System;
using System.Windows.Forms;

namespace SourceGrid.Controllers
{
	/// <summary>
	/// Summary description for SelectionDrag.
	/// </summary>
	public class SelectionDrag : GridBase
	{
		/// <summary>
		/// Create a selection drag controller for cut operations. (CutMode.CutOnPaste)
		/// </summary>
		public readonly static SelectionDrag Cut = new SelectionDrag(CutMode.CutOnPaste);
		/// <summary>
		/// Create a selection drag controller for copy operations. (CutMode.None)
		/// </summary>
		public readonly static SelectionDrag Copy = new SelectionDrag(CutMode.None);

		public SelectionDrag()
		{
		}

		public SelectionDrag(CutMode cutMode)
		{
			mCutMode = cutMode;
		}

		private CutMode mCutMode = CutMode.None;
		public CutMode CutMode
		{
			get{return mCutMode;}
		}

		private DevAge.Windows.Forms.ControlCursor mDragCursor = new DevAge.Windows.Forms.ControlCursor(Cursors.SizeAll);

		protected override void OnAttach(GridVirtual grid)
		{
			grid.MouseMove += new GridMouseEventHandler(grid_MouseMove);
			grid.MouseLeave += new GridEventHandler(grid_MouseLeave);
			grid.MouseDown += new GridMouseEventHandler(grid_MouseDown);
		}

		protected override void OnDetach(GridVirtual grid)
		{
			grid.MouseMove -= new GridMouseEventHandler(grid_MouseMove);
			grid.MouseLeave -= new GridEventHandler(grid_MouseLeave);
			grid.MouseDown -= new GridMouseEventHandler(grid_MouseDown);
		}

		protected virtual void grid_MouseMove(GridVirtual sender, MouseEventArgs e)
		{
			if (sender.Selection.BorderRange.IsEmpty() == false)
			{
				int distance;
				DevAge.Drawing.RectanglePartType partType = sender.Selection.Border.PointToPartType( sender.Selection.GetDrawingRectangle(), 
																			new System.Drawing.Point( e.X, e.Y) , out distance);
				if ( partType == DevAge.Drawing.RectanglePartType.BottomBorder || 
					partType == DevAge.Drawing.RectanglePartType.TopBorder || 
					partType == DevAge.Drawing.RectanglePartType.RightBorder || 
					partType == DevAge.Drawing.RectanglePartType.LeftBorder)
					mDragCursor.ApplyCursor(sender);
				else
					mDragCursor.ResetCursor();
			}
		}

		protected virtual void grid_MouseLeave(GridVirtual sender, EventArgs e)
		{
			mDragCursor.ResetCursor();
		}

		protected virtual void grid_MouseDown(GridVirtual sender, MouseEventArgs e)
		{
			if (sender.Selection.BorderRange.IsEmpty() == false)
			{
				Position mousePos = sender.PositionAtPoint(new System.Drawing.Point(e.X, e.Y));

				if (mousePos.IsEmpty() == false)
				{
					int distance;
					DevAge.Drawing.RectanglePartType partType = sender.Selection.Border.PointToPartType( sender.Selection.GetDrawingRectangle() , 
						new System.Drawing.Point( e.X, e.Y) , out distance);
					if ( partType == DevAge.Drawing.RectanglePartType.BottomBorder || 
						partType == DevAge.Drawing.RectanglePartType.TopBorder || 
						partType == DevAge.Drawing.RectanglePartType.RightBorder || 
						partType == DevAge.Drawing.RectanglePartType.LeftBorder)
					{
						RangeData data = new RangeData();
						data.LoadData(sender, sender.Selection.BorderRange, mousePos, mCutMode);
						if (mCutMode == CutMode.None)
							sender.DoDragDrop(data, DragDropEffects.Copy);
						else
							sender.DoDragDrop(data, DragDropEffects.Move);
					}
				}
			}
		}
	}
}
