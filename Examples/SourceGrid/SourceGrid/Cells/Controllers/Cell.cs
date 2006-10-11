using System;
using System.Windows.Forms;

namespace SourceGrid.Cells.Controllers
{
	/// <summary>
	/// Common behavior of the cell. This controller can be shared between multiple cells and is usually used as the default Grid.Controller. Removing this controller can cause unexpected behaviors.
	/// </summary>
	public class Cell : ControllerBase
	{
		/// <summary>
		/// The default behavior of a cell.
		/// </summary>
		public readonly static Cell Default = new Cell();

		/// <summary>
		/// 
		/// </summary>
		/// <param name="e"></param>
		public override void OnKeyDown (CellContext sender, KeyEventArgs e)
		{
			base.OnKeyDown(sender, e);

			if (e.KeyCode == Keys.F2 && 
				sender.Cell.Editor != null && ((sender.Cell.Editor.EditableMode & EditableMode.F2Key) == EditableMode.F2Key))
			{
				e.Handled = true;
				sender.StartEdit();
			}

			if (sender.Cell != null && sender.Cell.Controller != null && e.Handled == false)
				sender.Cell.Controller.OnKeyDown(sender, e);
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="e"></param>
		public override void OnKeyPress (CellContext sender, KeyPressEventArgs e)
		{
			base.OnKeyPress(sender, e);

			if ( sender.Cell.Editor != null &&  
				(sender.Cell.Editor.EditableMode & EditableMode.AnyKey) == EditableMode.AnyKey && 
				sender.IsEditing() == false &&
				char.IsControl( e.KeyChar ) == false )
			{
				e.Handled = true;
				sender.StartEdit();
				sender.Cell.Editor.SendCharToEditor(e.KeyChar);
			}

			if (sender.Cell != null && sender.Cell.Controller != null && e.Handled == false)
				sender.Cell.Controller.OnKeyPress(sender, e);
		}

#if !MINI
		/// <summary>
		/// 
		/// </summary>
		/// <param name="e"></param>
		public override void OnDoubleClick (CellContext sender, EventArgs e)
		{
			base.OnDoubleClick(sender, e);

			if ( sender.Cell.Editor != null && 
				(sender.Cell.Editor.EditableMode & EditableMode.DoubleClick) == EditableMode.DoubleClick &&
				sender.Grid.Selection.ActivePosition == sender.Position)
				sender.StartEdit();

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnDoubleClick(sender, e);
		}
#endif

		/// <summary>
		/// 
		/// </summary>
		/// <param name="e"></param>
		public override void OnClick (CellContext sender, EventArgs e)
		{
			base.OnClick(sender, e);

			if ( sender.Cell.Editor != null && 
				(sender.Cell.Editor.EditableMode & EditableMode.SingleClick) == EditableMode.SingleClick &&
				sender.Grid.Selection.ActivePosition == sender.Position)
				sender.StartEdit();

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnClick(sender, e);
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="e"></param>
		public override void OnFocusEntered(CellContext sender, EventArgs e)
		{
			base.OnFocusEntered(sender, e);

			//sposto la visuale su questa cella
			sender.Grid.ShowCell(sender.Position);

			//Getsione dell'edit sul focus, non lo metto all'interno della cella perchè un utente potrebbe chiamare direttamente il metodo SetFocusCell senza passare dalla cella
			if ( sender.Cell.Editor != null && (sender.Cell.Editor.EditableMode & EditableMode.Focus) == EditableMode.Focus)
				sender.StartEdit();

			if (sender.Grid!=null)
				sender.Grid.InvalidateCell(sender.Position);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnFocusEntered(sender, e);
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="e"></param>
		public override void OnFocusLeft(CellContext sender, EventArgs e)
		{
			base.OnFocusLeft (sender, e);

			if (sender.Grid!=null)
				sender.Grid.InvalidateCell(sender.Position);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnFocusLeft(sender, e);
		}


		/// <summary>
		/// Fired when the SetValue method is called.
		/// </summary>
		/// <param name="e"></param>
		public override void OnValueChanged(CellContext sender, EventArgs e)
		{
			base.OnValueChanged(sender, e);

			if (sender.Grid!=null)
				sender.Grid.InvalidateCell(sender.Position);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnValueChanged(sender, e);
		}

		/// <summary>
		/// Fired when editing is ended
		/// </summary>
		/// <param name="e"></param>
		public override void OnEditEnded(CellContext sender, EventArgs e)
		{
			base.OnEditEnded (sender, e);

			//Invalidate the selection to redraw the selection border
			sender.Grid.Selection.Invalidate();

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnEditEnded(sender, e);
		}

		public override bool CanReceiveFocus(CellContext sender, EventArgs e)
		{
			bool canReceiveFocus = base.CanReceiveFocus (sender, e);
			if (canReceiveFocus == false)
				return false;

			if (sender.Cell == null)
				return false;
			
			if (sender.Cell.Controller != null)
				return sender.Cell.Controller.CanReceiveFocus(sender, e);

			return true;
		}

		public override void OnContextMenuPopUp(CellContext sender, ContextMenuEventArgs e)
		{
			base.OnContextMenuPopUp (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnContextMenuPopUp(sender, e);
		}

		public override void OnEditStarting(CellContext sender, System.ComponentModel.CancelEventArgs e)
		{
			base.OnEditStarting (sender, e);

			//Invalidate the selection to redraw the selection border
			sender.Grid.Selection.Invalidate();

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnEditStarting(sender, e);
		}

        public override void OnEditStarted(CellContext sender, EventArgs e)
        {
            base.OnEditStarted(sender, e);

            if (sender.Cell != null && sender.Cell.Controller != null)
                sender.Cell.Controller.OnEditStarted(sender, e);
        }

        public override void OnValueChanging(CellContext sender, DevAge.ComponentModel.ValueEventArgs e)
        {
            base.OnValueChanging(sender, e);

            if (sender.Cell != null && sender.Cell.Controller != null)
                sender.Cell.Controller.OnValueChanging(sender, e);
        }

		public override void OnFocusEntering(CellContext sender, System.ComponentModel.CancelEventArgs e)
		{
			base.OnFocusEntering (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnFocusEntering(sender, e);
		}

		public override void OnFocusLeaving(CellContext sender, System.ComponentModel.CancelEventArgs e)
		{
			base.OnFocusLeaving (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnFocusLeaving(sender, e);
		}

		public override void OnKeyUp(CellContext sender, KeyEventArgs e)
		{
			base.OnKeyUp (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnKeyUp(sender, e);
		}

		public override void OnMouseDown(CellContext sender, MouseEventArgs e)
		{
			base.OnMouseDown (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnMouseDown(sender, e);
		}

		public override void OnMouseEnter(CellContext sender, EventArgs e)
		{
			base.OnMouseEnter (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnMouseEnter(sender, e);
		}

		public override void OnMouseLeave(CellContext sender, EventArgs e)
		{
			base.OnMouseLeave (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnMouseLeave(sender, e);
		}

		public override void OnMouseMove(CellContext sender, MouseEventArgs e)
		{
			base.OnMouseMove (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnMouseMove(sender, e);
		}

		public override void OnMouseUp(CellContext sender, MouseEventArgs e)
		{
			base.OnMouseUp (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnMouseUp(sender, e);
		}


		/// <summary>
		/// Fired before the cell is added to the selection.
		/// </summary>
		/// <param name="e"></param>
		public override void OnSelectionAdding(CellContext sender, RangeRegionChangingEventArgs e)
		{
			base.OnSelectionAdding (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnSelectionAdding(sender, e);
		}
		/// <summary>
		/// Fired after the cell is added to the selection.
		/// </summary>
		/// <param name="e"></param>
		public override void OnSelectionAdded(CellContext sender, RangeRegionEventArgs e)
		{
			base.OnSelectionAdded (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnSelectionAdded(sender, e);
		}
		/// <summary>
		/// Fired before the cell is removed to the selection.
		/// </summary>
		/// <param name="e"></param>
		public override void OnSelectionRemoving(CellContext sender, RangeRegionChangingEventArgs e)
		{
			base.OnSelectionRemoving (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnSelectionRemoving(sender, e);
		}
		/// <summary>
		/// Fired after the cell is removed to the selection.
		/// </summary>
		/// <param name="e"></param>
		public override void OnSelectionRemoved(CellContext sender, RangeRegionEventArgs e)
		{
			base.OnSelectionRemoved (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnSelectionRemoved(sender, e);
		}



		/// <summary>
		/// 
		/// </summary>
		/// <param name="e"></param>
		public override void OnDragDrop(CellContext sender, DragEventArgs e)
		{
			base.OnDragDrop (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnDragDrop(sender, e);
		}
		/// <summary>
		/// 
		/// </summary>
		/// <param name="e"></param>
		public override void OnDragEnter(CellContext sender, DragEventArgs e)
		{
			base.OnDragEnter (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnDragEnter(sender, e);
		}
		/// <summary>
		/// 
		/// </summary>
		/// <param name="e"></param>
		public override void OnDragLeave(CellContext sender, EventArgs e)
		{
			base.OnDragLeave (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnDragLeave(sender, e);
		}
		/// <summary>
		/// 
		/// </summary>
		/// <param name="e"></param>
		public override void OnDragOver(CellContext sender, DragEventArgs e)
		{
			base.OnDragOver (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnDragOver(sender, e);
		}
		/// <summary>
		/// 
		/// </summary>
		/// <param name="e"></param>
		public override void OnGiveFeedback(CellContext sender, GiveFeedbackEventArgs e)
		{
			base.OnGiveFeedback (sender, e);

			if (sender.Cell != null && sender.Cell.Controller != null)
				sender.Cell.Controller.OnGiveFeedback(sender, e);
		}
	}
}
