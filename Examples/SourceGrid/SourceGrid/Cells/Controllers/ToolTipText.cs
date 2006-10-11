using System;

namespace SourceGrid.Cells.Controllers
{
	/// <summary>
	/// Allow to customize the tooltiptext of a cell. This class read the tooltiptext from the ICellToolTipText.GetToolTipText.  This behavior can be shared between multiple cells.
	/// </summary>
	public class ToolTipText : ControllerBase
	{
		/// <summary>
		/// Default tooltiptext
		/// </summary>
		public readonly static ToolTipText Default = new ToolTipText();

		#region IBehaviorModel Members
		public override void OnMouseEnter(CellContext sender, EventArgs e)
		{
			base.OnMouseEnter(sender, e);

			ApplyToolTipText(sender, e);
		}

		public override void OnMouseLeave(CellContext sender, EventArgs e)
		{
			base.OnMouseLeave(sender, e);

			ResetToolTipText(sender, e);
		}
		#endregion

		/// <summary>
		/// Change the cursor with the cursor of the cell
		/// </summary>
		/// <param name="e"></param>
		protected virtual void ApplyToolTipText(CellContext sender, EventArgs e)
		{
			Models.IToolTipText toolTip;
			if ( (toolTip = (Models.IToolTipText)sender.Cell.Model.FindModel(typeof(Models.IToolTipText))) != null)
			{
				string l_ToolTipText = toolTip.GetToolTipText(sender);
				if (l_ToolTipText != null && l_ToolTipText.Length > 0)
					sender.Grid.GridToolTipText = l_ToolTipText;
			}
		}

		/// <summary>
		/// Reset the original cursor
		/// </summary>
		/// <param name="e"></param>
		protected virtual void ResetToolTipText(CellContext sender, EventArgs e)
		{
			Models.IToolTipText toolTip;
			if ( (toolTip = (Models.IToolTipText)sender.Cell.Model.FindModel(typeof(Models.IToolTipText))) != null)
			{
				sender.Grid.GridToolTipText = null;
			}
		}
	}
}
