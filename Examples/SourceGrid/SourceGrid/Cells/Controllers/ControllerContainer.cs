using System;

namespace SourceGrid.Cells.Controllers
{
	public class ControllerContainer : IController
	{
		private ControllerList m_ControllerList = null;
		/// <summary>
		/// A collection of elements of type IController. Only one instance of the same controller is allowed.
		/// </summary>
		public class ControllerList : DevAge.Collections.ObjectByTypeCollection
		{
			#region CollectionBase implementation
			/// <summary>
			/// Initializes a new empty instance of the ControllerContainer class.
			/// </summary>
			public ControllerList()
			{
				// empty
			}

			/// <summary>
			/// Initializes a new instance of the ControllerContainer class, containing elements
			/// copied from an array.
			/// </summary>
			/// <param name="items">
			/// The array whose elements are to be added to the new ControllerContainer.
			/// </param>
			public ControllerList(IController[] items)
			{
				this.AddRange(items);
			}

			/// <summary>
			/// Initializes a new instance of the ControllerContainer class, containing elements
			/// copied from another instance of ControllerContainer
			/// </summary>
			/// <param name="items">
			/// The ControllerContainer whose elements are to be added to the new ControllerContainer.
			/// </param>
			public ControllerList(ControllerList items)
			{
				this.AddRange(items);
			}

			/// <summary>
			/// Adds the elements of an array to the end of this ControllerContainer.
			/// </summary>
			/// <param name="items">
			/// The array whose elements are to be added to the end of this ControllerContainer.
			/// </param>
			public virtual void AddRange(IController[] items)
			{
				foreach (IController item in items)
				{
					Add(item);
				}
			}

			/// <summary>
			/// Adds the elements of another ControllerContainer to the end of this ControllerContainer.
			/// </summary>
			/// <param name="items">
			/// The ControllerContainer whose elements are to be added to the end of this ControllerContainer.
			/// </param>
			public virtual void AddRange(ControllerList items)
			{
				foreach (IController item in items)
				{
					Add(item);
				}
			}

			/// <summary>
			/// Adds an instance of type IController to the end of this ControllerContainer. Check if the element already exists.
			/// </summary>
			/// <param name="value">
			/// The IController to be added to the end of this ControllerContainer.
			/// </param>
			public virtual void Add(IController value)
			{
				Insert(Count, value);
			}

			/// <summary>
			/// Determines whether a specfic IController value is in this ControllerContainer.
			/// </summary>
			/// <param name="value">
			/// The IController value to locate in this ControllerContainer.
			/// </param>
			/// <returns>
			/// true if value is found in this ControllerContainer;
			/// false otherwise.
			/// </returns>
			public virtual bool Contains(IController value)
			{
				return this.List.Contains(value);
			}

			/// <summary>
			/// Return the zero-based index of the first occurrence of a specific value
			/// in this ControllerContainer
			/// </summary>
			/// <param name="value">
			/// The IController value to locate in the ControllerContainer.
			/// </param>
			/// <returns>
			/// The zero-based index of the first occurrence of the _ELEMENT value if found;
			/// -1 otherwise.
			/// </returns>
			public virtual int IndexOf(IController value)
			{
				return this.List.IndexOf(value);
			}

			/// <summary>
			/// Inserts an element into the ControllerContainer at the specified index. Check if the element already exists.
			/// </summary>
			/// <param name="index">
			/// The index at which the IController is to be inserted.
			/// </param>
			/// <param name="value">
			/// The IController to insert.
			/// </param>
			public virtual void Insert(int index, IController value)
			{
				if (Contains(value))
					return;

				this.List.Insert(index, value);
			}

			/// <summary>
			/// Gets or sets the IController at the given index in this ControllerContainer.
			/// </summary>
			public virtual IController this[int index]
			{
				get
				{
					return (IController) this.List[index];
				}
				set
				{
					this.List[index] = value;
				}
			}

			/// <summary>
			/// Removes the first occurrence of a specific IController from this ControllerContainer.
			/// </summary>
			/// <param name="value">
			/// The IController value to remove from this ControllerContainer.
			/// </param>
			public virtual void Remove(IController value)
			{
				this.List.Remove(value);
			}

			/// <summary>
			/// Type-specific enumeration class, used by ControllerContainer.GetEnumerator.
			/// </summary>
			public class Enumerator: System.Collections.IEnumerator
			{
				private System.Collections.IEnumerator wrapped;

				public Enumerator(ControllerList collection)
				{
					this.wrapped = ((System.Collections.CollectionBase)collection).GetEnumerator();
				}

				public IController Current
				{
					get
					{
						return (IController) (this.wrapped.Current);
					}
				}

				object System.Collections.IEnumerator.Current
				{
					get
					{
						return (IController) (this.wrapped.Current);
					}
				}

				public bool MoveNext()
				{
					return this.wrapped.MoveNext();
				}

				public void Reset()
				{
					this.wrapped.Reset();
				}
			}

			/// <summary>
			/// Returns an enumerator that can iterate through the elements of this ControllerContainer.
			/// </summary>
			/// <returns>
			/// An object that implements System.Collections.IEnumerator.
			/// </returns>        
			public new virtual ControllerList.Enumerator GetEnumerator()
			{
				return new ControllerList.Enumerator(this);
			}

			#endregion

			public IController FindController(Type modelType)
			{
				object model = base.GetObjectByType(modelType);
				if (model != null)
					return (IController)model;
				else
					return null;
			}
		}


		/// <summary>
		/// Returns null if not exist
		/// </summary>
		/// <param name="modelType"></param>
		/// <returns></returns>
		public virtual IController FindController(Type modelType)
		{
			if (m_ControllerList == null)
				m_ControllerList = new ControllerList();

			return m_ControllerList.FindController(modelType);
		}


		public virtual void AddController(IController model)
		{
			Type type = model.GetType();

			if (m_ControllerList == null)
				m_ControllerList = new ControllerList();

			m_ControllerList.Add(model);
		}

		public virtual void RemoveController(IController model)
		{
			if (m_ControllerList != null)
				m_ControllerList.Remove(model);
		}

		#region IController Members
		public void OnContextMenuPopUp(CellContext sender, ContextMenuEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnContextMenuPopUp(sender, e);
		}

		public void OnMouseDown(CellContext sender, System.Windows.Forms.MouseEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnMouseDown(sender, e);
		}

		public void OnMouseUp(CellContext sender, System.Windows.Forms.MouseEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnMouseUp(sender, e);
		}

		public void OnMouseMove(CellContext sender, System.Windows.Forms.MouseEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnMouseMove(sender, e);
		}

		public void OnMouseEnter(CellContext sender, EventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnMouseEnter(sender, e);
		}

		public void OnMouseLeave(CellContext sender, EventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnMouseLeave(sender, e);
		}

		public void OnKeyUp(CellContext sender, System.Windows.Forms.KeyEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnKeyUp(sender, e);
		}

		public void OnKeyDown(CellContext sender, System.Windows.Forms.KeyEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnKeyDown(sender, e);
		}

		public void OnKeyPress(CellContext sender, System.Windows.Forms.KeyPressEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnKeyPress(sender, e);
		}

		public void OnDoubleClick(CellContext sender, EventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnDoubleClick(sender, e);
		}

		public void OnClick(CellContext sender, EventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnClick(sender, e);
		}

		public void OnFocusLeaving(CellContext sender, System.ComponentModel.CancelEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnFocusLeaving(sender, e);
		}

		public void OnFocusLeft(CellContext sender, EventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnFocusLeft(sender, e);
		}

		public void OnFocusEntering(CellContext sender, System.ComponentModel.CancelEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnFocusEntering(sender, e);
		}

		public void OnFocusEntered(CellContext sender, EventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnFocusEntered(sender, e);
		}

		/// <summary>
		/// Fired before the value of the cell is changed.
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		public void OnValueChanging(CellContext sender, DevAge.ComponentModel.ValueEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnValueChanging(sender, e);
		}

		/// <summary>
		/// Fired after the value of the cell is changed.
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		public void OnValueChanged(CellContext sender, EventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnValueChanged(sender, e);
		}

		/// <summary>
		/// Fired when the StartEdit is called and before the cell start the edit operation. You can set the Cancel = true to stop editing.
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		public void OnEditStarting(CellContext sender, System.ComponentModel.CancelEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnEditStarting(sender, e);
		}
		/// <summary>
		/// Fired when the StartEdit is sucesfully called.
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		public void OnEditStarted(CellContext sender, EventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnEditStarted(sender, e);
		}
		/// <summary>
		/// Fired when the EndEdit is called. You can read the Cancel property to determine if the edit is completed. If you change the cancel property there is no effect.
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		public void OnEditEnded(CellContext sender, EventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnEditEnded(sender, e);
		}

		public bool CanReceiveFocus(CellContext sender, EventArgs e)
		{
			foreach (IController controller in m_ControllerList)
			{
				if (controller.CanReceiveFocus(sender, e) == false)
					return false;
			}

			return true;
		}

		public void OnSelectionAdding(CellContext sender, RangeRegionChangingEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnSelectionAdding(sender, e);
		}

		public void OnSelectionAdded(CellContext sender, RangeRegionEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnSelectionAdded(sender, e);
		}

		public void OnSelectionRemoving(CellContext sender, RangeRegionChangingEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnSelectionRemoving(sender, e);
		}

		public void OnSelectionRemoved(CellContext sender, RangeRegionEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnSelectionRemoved(sender, e);
		}

		public void OnDragDrop(CellContext sender, System.Windows.Forms.DragEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnDragDrop(sender, e);
		}

		public void OnDragEnter(CellContext sender, System.Windows.Forms.DragEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnDragEnter(sender, e);
		}

		public void OnDragLeave(CellContext sender, EventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnDragLeave(sender, e);
		}

		public void OnDragOver(CellContext sender, System.Windows.Forms.DragEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnDragOver(sender, e);
		}

		public void OnGiveFeedback(CellContext sender, System.Windows.Forms.GiveFeedbackEventArgs e)
		{
			foreach (IController controller in m_ControllerList)
				controller.OnGiveFeedback(sender, e);
		}
		#endregion
	}
}
