using System;

namespace SourceGrid.Cells.Models
{
	/// <summary>
	/// A container for the model classes. THe only required model is the Value model, that you can assign using the ValueModel property.
	/// </summary>
	public class ModelContainer
	{
		/// <summary>
		/// Constructor
		/// </summary>
		public ModelContainer()
		{
		}

		private ModelList m_ModelList = null;
		/// <summary>
		/// A collection of elements of type IModel
		/// </summary>
		public class ModelList : DevAge.Collections.ObjectByTypeCollection
		{
			#region CollectionBase implementation
			/// <summary>
			/// Initializes a new empty instance of the ModelContainer class.
			/// </summary>
			public ModelList()
			{
				// empty
			}

			/// <summary>
			/// Initializes a new instance of the ModelContainer class, containing elements
			/// copied from an array.
			/// </summary>
			/// <param name="items">
			/// The array whose elements are to be added to the new ModelContainer.
			/// </param>
			public ModelList(IModel[] items)
			{
				this.AddRange(items);
			}

			/// <summary>
			/// Initializes a new instance of the ModelContainer class, containing elements
			/// copied from another instance of ModelContainer
			/// </summary>
			/// <param name="items">
			/// The ModelContainer whose elements are to be added to the new ModelContainer.
			/// </param>
			public ModelList(ModelList items)
			{
				this.AddRange(items);
			}

			/// <summary>
			/// Adds the elements of an array to the end of this ModelContainer.
			/// </summary>
			/// <param name="items">
			/// The array whose elements are to be added to the end of this ModelContainer.
			/// </param>
			public virtual void AddRange(IModel[] items)
			{
				foreach (IModel item in items)
				{
					Add(item);
				}
			}

			/// <summary>
			/// Adds the elements of another ModelContainer to the end of this ModelContainer.
			/// </summary>
			/// <param name="items">
			/// The ModelContainer whose elements are to be added to the end of this ModelContainer.
			/// </param>
			public virtual void AddRange(ModelList items)
			{
				foreach (IModel item in items)
				{
					Add(item);
				}
			}

			/// <summary>
			/// Adds an instance of type IModel to the end of this ModelContainer.
			/// </summary>
			/// <param name="value">
			/// The IModel to be added to the end of this ModelContainer.
			/// </param>
			public virtual void Add(IModel value)
			{
				Insert(Count, value);
			}

			/// <summary>
			/// Determines whether a specfic IModel value is in this ModelContainer.
			/// </summary>
			/// <param name="value">
			/// The IModel value to locate in this ModelContainer.
			/// </param>
			/// <returns>
			/// true if value is found in this ModelContainer;
			/// false otherwise.
			/// </returns>
			public virtual bool Contains(IModel value)
			{
				return this.List.Contains(value);
			}

			/// <summary>
			/// Return the zero-based index of the first occurrence of a specific value
			/// in this ModelContainer
			/// </summary>
			/// <param name="value">
			/// The IModel value to locate in the ModelContainer.
			/// </param>
			/// <returns>
			/// The zero-based index of the first occurrence of the _ELEMENT value if found;
			/// -1 otherwise.
			/// </returns>
			public virtual int IndexOf(IModel value)
			{
				return this.List.IndexOf(value);
			}

			/// <summary>
			/// Inserts an element into the ModelContainer at the specified index
			/// </summary>
			/// <param name="index">
			/// The index at which the IModel is to be inserted.
			/// </param>
			/// <param name="value">
			/// The IModel to insert.
			/// </param>
			public virtual void Insert(int index, IModel value)
			{
				if (Contains(value))
					return;

				this.List.Insert(index, value);
			}

			/// <summary>
			/// Gets or sets the IModel at the given index in this ModelContainer.
			/// </summary>
			public virtual IModel this[int index]
			{
				get
				{
					return (IModel) this.List[index];
				}
				set
				{
					this.List[index] = value;
				}
			}

			/// <summary>
			/// Removes the first occurrence of a specific IModel from this ModelContainer.
			/// </summary>
			/// <param name="value">
			/// The IModel value to remove from this ModelContainer.
			/// </param>
			public virtual void Remove(IModel value)
			{
				this.List.Remove(value);
			}

			/// <summary>
			/// Type-specific enumeration class, used by ModelContainer.GetEnumerator.
			/// </summary>
			public class Enumerator: System.Collections.IEnumerator
			{
				private System.Collections.IEnumerator wrapped;

				public Enumerator(ModelList collection)
				{
					this.wrapped = ((System.Collections.CollectionBase)collection).GetEnumerator();
				}

				public IModel Current
				{
					get
					{
						return (IModel) (this.wrapped.Current);
					}
				}

				object System.Collections.IEnumerator.Current
				{
					get
					{
						return (IModel) (this.wrapped.Current);
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
			/// Returns an enumerator that can iterate through the elements of this ModelContainer.
			/// </summary>
			/// <returns>
			/// An object that implements System.Collections.IEnumerator.
			/// </returns>        
			public new virtual ModelList.Enumerator GetEnumerator()
			{
				return new ModelList.Enumerator(this);
			}

			#endregion

			public IModel FindModel(Type modelType)
			{
				object model = base.GetObjectByType(modelType);
				if (model != null)
					return (IModel)model;
				else
					return null;
			}
		}


		/// <summary>
		/// Returns null if not exist
		/// </summary>
		/// <param name="modelType"></param>
		/// <returns></returns>
		public virtual IModel FindModel(Type modelType)
		{
			if (typeof(IValueModel).IsAssignableFrom(modelType))
				return m_ValueModel;
			else
			{
				if (m_ModelList == null)
					m_ModelList = new ModelList();

				return m_ModelList.FindModel(modelType);
			}
		}


		public virtual void AddModel(IModel model)
		{
			Type type = model.GetType();

			if (typeof(IValueModel).IsAssignableFrom(type))
				m_ValueModel = (IValueModel)model;
			else
			{
				if (m_ModelList == null)
					m_ModelList = new ModelList();

				m_ModelList.Add(model);
			}
		}

		public virtual void RemoveModel(IModel model)
		{
			if (object.ReferenceEquals(model, m_ValueModel))
				m_ValueModel = null;
			else if (m_ModelList != null)
				m_ModelList.Remove(model);
		}

		private IValueModel m_ValueModel;
		public virtual IValueModel ValueModel
		{
			get{return m_ValueModel;}
			set{m_ValueModel = value;}
		}
	}
}
