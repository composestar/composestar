using System;
using System.Windows.Forms;

namespace SourceGrid.Cells.Controllers
{
	/// <summary>
	/// Summary description for BehaviorModelCheckBox. This behavior can be shared between multiple cells.
	/// </summary>
	public class CheckBox : ControllerBase
	{
		/// <summary>
		/// Default behavior checkbox
		/// </summary>
		public readonly static CheckBox Default = new CheckBox();

		/// <summary>
		/// Constructor
		/// </summary>
		public CheckBox()
		{
		}

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="p_bAutoChangeValueOfSelectedCells">Indicates if this cells when checked or uncheck must change also the value of the selected cells of type CellCheckBox.</param>
		public CheckBox(bool p_bAutoChangeValueOfSelectedCells)
		{
			m_bAutoChangeValueOfSelectedCells = p_bAutoChangeValueOfSelectedCells;
		}

		#region IBehaviorModel Members
		public override void OnKeyPress(CellContext sender, KeyPressEventArgs e)
		{
			base.OnKeyPress(sender, e);

			if (e.KeyChar == ' ')
				UIChangeChecked(sender, e);
		}

		public override void OnClick(CellContext sender, EventArgs e)
		{
			base.OnClick(sender, e);

			UIChangeChecked(sender, e);
		}

		#endregion

		private bool m_bAutoChangeValueOfSelectedCells = false;
		/// <summary>
		/// Indicates if this cells when checked or uncheck must change also the value of the selected cells of type CellCheckBox. Default is false
		/// </summary>
		public bool AutoChangeValueOfSelectedCells
		{
			get{return m_bAutoChangeValueOfSelectedCells;}
		}

		/// <summary>
		/// Toggle the value of the current cell and if AutoChangeValueOfSelectedCells is true of all the selected cells.
		/// Simulate an edit operation.
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void UIChangeChecked(CellContext sender, EventArgs e)
		{
			Models.ICheckBox l_Check = (Models.ICheckBox)sender.Cell.Model.FindModel(typeof(Models.ICheckBox));;
			if (l_Check == null)
				throw new SourceGrid.SourceGridException("Models.ICheckBox not found");

			Models.CheckBoxStatus checkStatus = l_Check.GetCheckBoxStatus(sender);
			if (checkStatus.CheckEnable)
			{
				bool l_NewVal = !checkStatus.Checked;
				sender.StartEdit();
				try
				{
					l_Check.SetCheckedValue(sender, l_NewVal);
					sender.EndEdit(false);
				}
				catch(Exception)
				{
					sender.EndEdit(true);
					throw;
				}

				//change the status of all selected control
				if (AutoChangeValueOfSelectedCells)
				{
					foreach(Position pos in sender.Grid.Selection.GetCellsPositions())
					{
						Cells.ICellVirtual c = sender.Grid.GetCell(pos);
						Models.ICheckBox check;
						if (c != this && c != null && 
							 (check = (Models.ICheckBox)c.Model.FindModel(typeof(Models.ICheckBox))) != null )
						{
							CellContext context = new CellContext(sender.Grid, pos, c);
							context.StartEdit();
							try
							{
								check.SetCheckedValue(context, l_NewVal);
								context.EndEdit(false);
							}
							catch(Exception)
							{
								context.EndEdit(true);
								throw;
							}
						}
					}
				}
			}
		}

	}
}
