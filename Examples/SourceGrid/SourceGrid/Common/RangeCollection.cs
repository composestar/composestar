using System;

namespace SourceGrid
{
	/// <summary>
	/// A collection of elements of type Range
	/// </summary>
	public class RangeCollection: System.Collections.CollectionBase
	{
		/// <summary>
		/// Initializes a new empty instance of the RangeCollection class.
		/// </summary>
		public RangeCollection()
		{
			// empty
		}

		/// <summary>
		/// Initializes a new instance of the RangeCollection class, containing elements
		/// copied from an array.
		/// </summary>
		/// <param name="items">
		/// The array whose elements are to be added to the new RangeCollection.
		/// </param>
		public RangeCollection(Range[] items)
		{
			this.AddRange(items);
		}

		/// <summary>
		/// Initializes a new instance of the RangeCollection class, containing elements
		/// copied from another instance of RangeCollection
		/// </summary>
		/// <param name="items">
		/// The RangeCollection whose elements are to be added to the new RangeCollection.
		/// </param>
		public RangeCollection(RangeCollection items)
		{
			this.AddRange(items);
		}

		/// <summary>
		/// Adds the elements of an array to the end of this RangeCollection.
		/// </summary>
		/// <param name="items">
		/// The array whose elements are to be added to the end of this RangeCollection.
		/// </param>
		public virtual void AddRange(Range[] items)
		{
			foreach (Range item in items)
			{
				this.List.Add(item);
			}
		}

		/// <summary>
		/// Adds the elements of another RangeCollection to the end of this RangeCollection.
		/// </summary>
		/// <param name="items">
		/// The RangeCollection whose elements are to be added to the end of this RangeCollection.
		/// </param>
		public virtual void AddRange(RangeCollection items)
		{
			foreach (Range item in items)
			{
				this.List.Add(item);
			}
		}

		/// <summary>
		/// Adds an instance of type Range to the end of this RangeCollection.
		/// </summary>
		/// <param name="value">
		/// The Range to be added to the end of this RangeCollection.
		/// </param>
		public virtual void Add(Range value)
		{
			this.List.Add(value);
		}

		/// <summary>
		/// Determines whether a specfic Range value is in this RangeCollection.
		/// </summary>
		/// <param name="value">
		/// The Range value to locate in this RangeCollection.
		/// </param>
		/// <returns>
		/// true if value is found in this RangeCollection;
		/// false otherwise.
		/// </returns>
		public virtual bool Contains(Range value)
		{
			return this.List.Contains(value);
		}

		/// <summary>
		/// Return the zero-based index of the first occurrence of a specific value
		/// in this RangeCollection
		/// </summary>
		/// <param name="value">
		/// The Range value to locate in the RangeCollection.
		/// </param>
		/// <returns>
		/// The zero-based index of the first occurrence of the _ELEMENT value if found;
		/// -1 otherwise.
		/// </returns>
		public virtual int IndexOf(Range value)
		{
			return this.List.IndexOf(value);
		}

		/// <summary>
		/// Inserts an element into the RangeCollection at the specified index
		/// </summary>
		/// <param name="index">
		/// The index at which the Range is to be inserted.
		/// </param>
		/// <param name="value">
		/// The Range to insert.
		/// </param>
		public virtual void Insert(int index, Range value)
		{
			this.List.Insert(index, value);
		}

		/// <summary>
		/// Gets or sets the Range at the given index in this RangeCollection.
		/// </summary>
		public virtual Range this[int index]
		{
			get
			{
				return (Range) this.List[index];
			}
			set
			{
				this.List[index] = value;
			}
		}

		/// <summary>
		/// Removes the first occurrence of a specific Range from this RangeCollection.
		/// </summary>
		/// <param name="value">
		/// The Range value to remove from this RangeCollection.
		/// </param>
		public virtual void Remove(Range value)
		{
			this.List.Remove(value);
		}

		/// <summary>
		/// Type-specific enumeration class, used by RangeCollection.GetEnumerator.
		/// </summary>
		public class Enumerator: System.Collections.IEnumerator
		{
			private System.Collections.IEnumerator wrapped;

			public Enumerator(RangeCollection collection)
			{
				this.wrapped = ((System.Collections.CollectionBase)collection).GetEnumerator();
			}

			public Range Current
			{
				get
				{
					return (Range) (this.wrapped.Current);
				}
			}

			object System.Collections.IEnumerator.Current
			{
				get
				{
					return (Range) (this.wrapped.Current);
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
		/// Returns an enumerator that can iterate through the elements of this RangeCollection.
		/// </summary>
		/// <returns>
		/// An object that implements System.Collections.IEnumerator.
		/// </returns>        
		public new virtual RangeCollection.Enumerator GetEnumerator()
		{
			return new RangeCollection.Enumerator(this);
		}
	}

}
