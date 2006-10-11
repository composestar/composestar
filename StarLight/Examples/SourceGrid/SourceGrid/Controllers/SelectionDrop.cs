using System;
using System.Windows.Forms;

namespace SourceGrid.Controllers
{
	/// <summary>
	/// Summary description for SelectionDrop.
	/// </summary>
	public class SelectionDrop : GridBase
	{
		/// <summary>
		/// Default implementation.
		/// </summary>
		public readonly static SelectionDrop Default = new SelectionDrop();

		public SelectionDrop()
		{
		}

		private RangeData mRangeData = null;
		private HighlightedRange mHighlightedRange = null;

		private void ResetTempData()
		{
			mRangeData = null;

			if (mHighlightedRange != null)
			{
				mHighlightedRange.Range = Range.Empty;
				if (mHighlightedRange.Grid.HighlightedRanges.Contains(mHighlightedRange))
					mHighlightedRange.Grid.HighlightedRanges.Remove(mHighlightedRange);
				mHighlightedRange = null;
			}
		}

		protected override void OnAttach(GridVirtual grid)
		{
			grid.DragEnter += new GridDragEventHandler(grid_DragEnter);
			grid.DragLeave += new GridEventHandler(grid_DragLeave);
			grid.DragDrop += new GridDragEventHandler(grid_DragDrop);
			grid.DragOver += new GridDragEventHandler(grid_DragOver);
		}

		protected override void OnDetach(GridVirtual grid)
		{
			grid.DragEnter -= new GridDragEventHandler(grid_DragEnter);
			grid.DragLeave -= new GridEventHandler(grid_DragLeave);
			grid.DragDrop -= new GridDragEventHandler(grid_DragDrop);
		}

		protected virtual void grid_DragEnter(GridVirtual sender, DragEventArgs e)
		{
		}

		protected virtual void grid_DragLeave(GridVirtual sender, EventArgs e)
		{
			ResetTempData();
		}

		protected virtual void grid_DragDrop(GridVirtual sender, DragEventArgs e)
		{
			if (mRangeData != null)
			{
                Range destination = mHighlightedRange.Range;

                //Solo se il range sorgente è diverso dal range di destinazione
                if (mRangeData.SourceGrid != sender || mRangeData.SourceRange != destination)
                {
                    mRangeData.WriteData(sender, destination);
                    sender.Selection.Focus(destination.Start);
                    sender.Selection.Add(destination);
                }
			}

			ResetTempData();
		}

		private void grid_DragOver(GridVirtual sender, DragEventArgs e)
		{
			if (e.Data.GetDataPresent(typeof(RangeData)))
			{
				ResetTempData();

				mRangeData = (RangeData)e.Data.GetData(typeof(RangeData));
				if (mRangeData.CutMode == CutMode.None)
					e.Effect = DragDropEffects.Copy;
				else
					e.Effect = DragDropEffects.Move;

				Position mousePos = sender.PositionAtPoint(sender.PointToClient( new System.Drawing.Point(e.X, e.Y) ));

				if (mousePos.IsEmpty() == false)
				{
					Range rngDest = mRangeData.FindDestinationRange(sender, mousePos);
					if (rngDest.IsEmpty() == false)
					{
						mHighlightedRange = new HighlightedRange(sender);
						mHighlightedRange.Range = rngDest;
						mHighlightedRange.Border = mHighlightedRange.Border.SetDashStyle(System.Drawing.Drawing2D.DashStyle.DashDotDot);
						sender.HighlightedRanges.Add(mHighlightedRange);
					}
				}
			}
			else
			{
				e.Effect = DragDropEffects.None;
				ResetTempData();
			}
		}
	}
}
