using System;
using System.Drawing;
using System.Windows.Forms;

namespace SourceGrid.Controllers
{
	/// <summary>
	/// A Grid controllers used to handle mouse selection and multi selection with Shift and Ctrl.
	/// </summary>
	public class MouseSelection : GridBase
	{
		public static MouseSelection Default = new MouseSelection();

		protected override void OnAttach(GridVirtual grid)
		{
			grid.MouseDown += new GridMouseEventHandler(grid_MouseDown);
			grid.MouseUp += new GridMouseEventHandler(grid_MouseUp);
			grid.MouseMove += new GridMouseEventHandler(grid_MouseMove);
			grid.MouseLeave += new GridEventHandler(grid_MouseLeave);
		}

		protected override void OnDetach(GridVirtual grid)
		{
			grid.MouseDown -= new GridMouseEventHandler(grid_MouseDown);
			grid.MouseUp -= new GridMouseEventHandler(grid_MouseUp);
			grid.MouseMove -= new GridMouseEventHandler(grid_MouseMove);
			grid.MouseLeave -= new GridEventHandler(grid_MouseLeave);
		}


		protected virtual void grid_MouseDown(GridVirtual sender, System.Windows.Forms.MouseEventArgs e)
		{
			//verifico che l'eventuale edit sia terminato altrimenti esco
			if (sender.Selection.ActivePosition.IsEmpty() == false)
			{
				//Se la cella coincide esco
				if (sender.MouseDownPosition == sender.Selection.ActivePosition)
					return;

				//Altrimenti provo a terminare l'edit
				CellContext focusCell = new CellContext(sender, sender.Selection.ActivePosition);
				if (focusCell.Cell != null && focusCell.IsEditing())
				{
					if (focusCell.EndEdit(false) == false)
						return;
				}
			}

			//scateno eventi di MouseDown e seleziono la cella
			if (sender.MouseDownPosition.IsEmpty() == false)
			{
				Cells.ICellVirtual cellMouseDown = sender.GetCell(sender.MouseDownPosition);
				if (cellMouseDown != null)
				{
					int distance;
					DevAge.Drawing.RectanglePartType partType = sender.Selection.Border.PointToPartType(sender.Selection.GetDrawingRectangle() , 
						new System.Drawing.Point( e.X, e.Y) , out distance);
					if (partType == DevAge.Drawing.RectanglePartType.ContentArea || 
						partType == DevAge.Drawing.RectanglePartType.None)
					{
						bool l_bShiftPress = ((Control.ModifierKeys & Keys.Shift) == Keys.Shift &&
							(sender.SpecialKeys & GridSpecialKeys.Shift) == GridSpecialKeys.Shift);
				
						if (l_bShiftPress == false || 
							sender.Selection.EnableMultiSelection == false || 
							sender.Selection.ActivePosition.IsEmpty() )
						{
							//Standard focus on the cell on MouseDown
							if (sender.Selection.Contains(sender.MouseDownPosition) == false || e.Button == MouseButtons.Left) //solo se non è stata ancora selezionata
								sender.Selection.Focus(sender.MouseDownPosition);
						}
						else //gestione speciale caso shift
						{
							sender.Selection.Clear();
							Range rangeToSelect = new Range(sender.Selection.ActivePosition, sender.MouseDownPosition);
							sender.Selection.Add(rangeToSelect);
						}
					}
				}
			}
		}

		protected virtual void grid_MouseUp(GridVirtual sender, System.Windows.Forms.MouseEventArgs e)
		{
			//questo è per assicurarsi che la selezione precedentemente fatta tramite mouse venga effettivamente deselezionata
			sender.MouseSelectionFinish();
		}

		protected virtual void grid_MouseMove(GridVirtual sender, System.Windows.Forms.MouseEventArgs e)
		{
			Position l_PointPosition = sender.PositionAtPoint(new Point(e.X, e.Y));
			Cells.ICellVirtual l_CellPosition = sender.GetCell(l_PointPosition);

			#region Mouse Multiselection
			if (e.Button == MouseButtons.Left && sender.Selection.EnableMultiSelection)
			{
				//Only if there is a FocusCell
				CellContext focusCellContext = new CellContext(sender, sender.Selection.ActivePosition);
				if (focusCellContext.Cell != null && focusCellContext.IsEditing() ==false)
				{
					Position l_SelCornerPos = l_PointPosition;
					Cells.ICellVirtual l_SelCorner = l_CellPosition;

					//If the current Focus Cell is a scrollable cell then search the current cell (under the mouse)only in scrollable cells
					// see PositionAtPoint with false parameter
					if (sender.GetPositionType(sender.Selection.ActivePosition) == CellPositionType.Scrollable)
					{
						l_SelCornerPos = sender.PositionAtPoint(new Point(e.X, e.Y));
						l_SelCorner = sender.GetCell(l_PointPosition);
					}

					if (l_SelCornerPos.IsEmpty() == false && l_SelCorner != null)
					{
						//Only if the user start the selection with a cell (m_MouseDownCell!=null)
						if (sender.MouseDownPosition.IsEmpty() == false && sender.Selection.Contains(sender.MouseDownPosition))
						{
							sender.ChangeMouseSelectionCorner(l_SelCornerPos);
							sender.ShowCell(l_SelCornerPos);
						}
					}
				}
			}
			#endregion
		}

		protected virtual void grid_MouseLeave(GridVirtual sender, EventArgs e)
		{
			//questo è per assicurarsi che la selezione del mouse venga effettivamente deselezionata
			sender.MouseSelectionFinish();
		}
	}
}
