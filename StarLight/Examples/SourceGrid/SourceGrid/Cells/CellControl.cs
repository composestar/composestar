using System;
using System.Windows.Forms;
using System.Reflection;
using System.Drawing;

namespace SourceGrid.Cells
{
	/// <summary>
	/// The CellControl class is used to create a cell with a Windows Forms Control inside.
	/// The CellControl class requires a new Windows Forms control for each cell. Unfortunately Winwods Forms control requires a lot of system resources and with many cells this can cause system fault or out of memory conditions.
	/// Basically I suggest to use CellControl with no more than 50 cells. Another problem with the CellControl class is that it is not integrated well with the rest of the grid (control borders, cell navigation, ...)
	/// </summary>
	public class CellControl : SourceGrid.Cells.Cell
	{
		private Control mControl;
		/// <summary>
		/// Constructor.
		/// </summary>
		/// <param name="control">Control to insert inside the grid</param>
		public CellControl(Control control):base(null)
		{
			mControl = control;
		}

		/// <summary>
		/// Constructor.
		/// </summary>
		/// <param name="control">Control to insert inside the grid</param>
		/// <param name="scrollMode"></param>
		/// <param name="useCellBorder"></param>
		public CellControl(Control control, LinkedControlScrollMode scrollMode, bool useCellBorder):this(control)
		{
			mControl = control;
			mScrollMode = scrollMode;
			mUseCellBorder = useCellBorder;
		}

		/// <summary>
		/// Gets the control associated with this cell.
		/// </summary>
		public Control Control
		{
			get{return mControl;}
		}

		private LinkedControlScrollMode mScrollMode = LinkedControlScrollMode.BasedOnPosition;
		private bool mUseCellBorder = true;

		private Guid mControlGuid = Guid.NewGuid();
		protected override void OnAddToGrid(EventArgs e)
		{
			base.OnAddToGrid (e);

			Grid.ScrollablePanel.ControlsRepository.Add(mControlGuid, mControl);

			LinkedControlValue linkedValue = new LinkedControlValue(Range.Start);
			linkedValue.ScrollMode = mScrollMode;
			linkedValue.UseCellBorder = mUseCellBorder;
			Grid.LinkedControls.Add(mControl, linkedValue);

			Grid.ArrangeLinkedControls();
		}

		protected override void OnRemoveToGrid(EventArgs e)
		{
			base.OnRemoveToGrid (e);

			Grid.LinkedControls.Remove(mControl);

			Grid.ScrollablePanel.ControlsRepository.Remove(mControlGuid);
		}
	}
}