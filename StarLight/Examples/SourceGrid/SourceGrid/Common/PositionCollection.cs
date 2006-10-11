using System;

namespace SourceGrid
{
	/// <summary>
	/// A collection of elements of type Position
	/// </summary>
	public class PositionCollection : System.Collections.CollectionBase
	{
		/// <summary>
		/// Initializes a new empty instance of the PositionCollection class.
		/// </summary>
		public PositionCollection()
		{
			// empty
		}

		/// <summary>
		/// Initializes a new instance of the PositionCollection class, containing elements
		/// copied from an array.
		/// </summary>
		/// <param name="items">
		/// The array whose elements are to be added to the new PositionCollection.
		/// </param>
		public PositionCollection(Position[] items)
		{
			this.AddRange(items);
		}

		/// <summary>
		/// Initializes a new instance of the PositionCollection class, containing elements
		/// copied from another instance of PositionCollection
		/// </summary>
		/// <param name="items">
		/// The PositionCollection whose elements are to be added to the new PositionCollection.
		/// </param>
		public PositionCollection(PositionCollection items)
		{
			this.AddRange(items);
		}

		/// <summary>
		/// Adds the elements of an array to the end of this PositionCollection.
		/// </summary>
		/// <param name="items">
		/// The array whose elements are to be added to the end of this PositionCollection.
		/// </param>
		public virtual void AddRange(Position[] items)
		{
			foreach (Position item in items)
			{
				this.List.Add(item);
			}
		}

		/// <summary>
		/// Adds the elements of another PositionCollection to the end of this PositionCollection.
		/// </summary>
		/// <param name="items">
		/// The PositionCollection whose elements are to be added to the end of this PositionCollection.
		/// </param>
		public virtual void AddRange(PositionCollection items)
		{
			foreach (Position item in items)
			{
				this.List.Add(item);
			}
		}

		/// <summary>
		/// Adds an instance of type Position to the end of this PositionCollection.
		/// </summary>
		/// <param name="value">
		/// The Position to be added to the end of this PositionCollection.
		/// </param>
		public virtual void Add(Position value)
		{
			this.List.Add(value);
		}

		/// <summary>
		/// Determines whether a specfic Position value is in this PositionCollection.
		/// </summary>
		/// <param name="value">
		/// The Position value to locate in this PositionCollection.
		/// </param>
		/// <returns>
		/// true if value is found in this PositionCollection;
		/// false otherwise.
		/// </returns>
		public virtual bool Contains(Position value)
		{
			return this.List.Contains(value);
		}

		/// <summary>
		/// Return the zero-based index of the first occurrence of a specific value
		/// in this PositionCollection
		/// </summary>
		/// <param name="value">
		/// The Position value to locate in the PositionCollection.
		/// </param>
		/// <returns>
		/// The zero-based index of the first occurrence of the _ELEMENT value if found;
		/// -1 otherwise.
		/// </returns>
		public virtual int IndexOf(Position value)
		{
			return this.List.IndexOf(value);
		}

		/// <summary>
		/// Inserts an element into the PositionCollection at the specified index
		/// </summary>
		/// <param name="index">
		/// The index at which the Position is to be inserted.
		/// </param>
		/// <param name="value">
		/// The Position to insert.
		/// </param>
		public virtual void Insert(int index, Position value)
		{
			this.List.Insert(index, value);
		}

		/// <summary>
		/// Gets or sets the Position at the given index in this PositionCollection.
		/// </summary>
		public virtual Position this[int index]
		{
			get
			{
				return (Position) this.List[index];
			}
			set
			{
				this.List[index] = value;
			}
		}

		/// <summary>
		/// Removes the first occurrence of a specific Position from this PositionCollection.
		/// </summary>
		/// <param name="value">
		/// The Position value to remove from this PositionCollection.
		/// </param>
		public virtual void Remove(Position value)
		{
			this.List.Remove(value);
		}

		/// <summary>
		/// Type-specific enumeration class, used by PositionCollection.GetEnumerator.
		/// </summary>
		public class Enumerator: System.Collections.IEnumerator
		{
			private System.Collections.IEnumerator wrapped;

			public Enumerator(PositionCollection collection)
			{
				this.wrapped = ((System.Collections.CollectionBase)collection).GetEnumerator();
			}

			public Position Current
			{
				get
				{
					return (Position) (this.wrapped.Current);
				}
			}

			object System.Collections.IEnumerator.Current
			{
				get
				{
					return (Position) (this.wrapped.Current);
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
		/// Returns an enumerator that can iterate through the elements of this PositionCollection.
		/// </summary>
		/// <returns>
		/// An object that implements System.Collections.IEnumerator.
		/// </returns>        
		public new virtual PositionCollection.Enumerator GetEnumerator()
		{
			return new PositionCollection.Enumerator(this);
		}
	}

}
