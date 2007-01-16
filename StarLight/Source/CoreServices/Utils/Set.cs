using System;
using System.Collections.Generic;

namespace Composestar.StarLight.CoreServices
{
	/// <summary>
	/// A collection that contains no duplicate elements.
	/// </summary>
	/// <typeparam name="T">The type of the elements in the set.</typeparam>
	public interface ISet<T> : ICollection<T>
	{
	}

	/// <summary>
	/// Implements the ISet interface, backed by a dictionary.
	/// </summary>
	/// <typeparam name="T">The type of the elements in the set.</typeparam>
	public class Set<T> : ISet<T>
	{
		private IDictionary<T, bool> _dict;

		/// <summary>
		/// Creates a new set with no elements.
		/// </summary>
		public Set()
		{
			_dict = new Dictionary<T, bool>();
		}

		/// <summary>
		/// Creates a new set containing the elements of the specified collection.
		/// </summary>
		/// <param name="collection">The collection whose elements are to be placed into this set.</param>
		public Set(IEnumerable<T> collection)
		: base()
		{
			foreach (T item in collection)
				_dict[item] = true;
		}

		#region ICollection<T> Members

		/// <summary>
		/// Adds the specified element to this set if it is not already present.
		/// </summary>
		/// <param name="item">The element to be added to this set.</param>
		public void Add(T item)
		{
			_dict[item] = true;
		}

		/// <summary>
		/// Removes the specified element from this set if it is present.
		/// </summary>
		/// <param name="item">Element to be removed from this set.</param>
		/// <returns>true if the set contained the specified element.</returns>
		public bool Remove(T item)
		{
			return _dict.Remove(item);
		}

		/// <summary>
		/// Indicates whether this set contains the specified element.
		/// </summary>
		/// <param name="item">Element whose presence in this set is to be tested.</param>
		/// <returns>true if this set contains the specified element.</returns>
		public bool Contains(T item)
		{
			return _dict.ContainsKey(item);
		}

		/// <summary>
		/// Removes all elements from this set.
		/// </summary>
		public void Clear()
		{
			_dict.Clear();
		}

		/// <summary>
		/// Copies the elements of this set to an array, starting at a particular index. 
		/// </summary>
		/// <param name="array">The array to copy to.</param>
		/// <param name="startIndex">The index at which copying begins.</param>
		public void CopyTo(T[] array, int startIndex)
		{
			if (array == null)
				throw new NullReferenceException("array");

			if (startIndex < 0 || array.Length - startIndex < _dict.Count)
				throw new ArgumentOutOfRangeException("startIndex");

			int index = startIndex;
			foreach (T item in _dict.Keys)
				array[index++] = item;
		}
		
		/// <summary>
		/// Sets the number of elements in this set.
		/// </summary>
		public int Count
		{
			get { return _dict.Count; }
		}

		/// <summary>
		/// Indicates whether this set is read-only
		/// </summary>
		public bool IsReadOnly
		{
			get { return false; }
		}

		#endregion

		#region IEnumerable<T> Members

		/// <summary>
		/// Returns an enumerator that iterates through this set. 
		/// </summary>
		/// <returns>An enumerator over the elements in this set.</returns>
		public IEnumerator<T> GetEnumerator()
		{
			return _dict.Keys.GetEnumerator();
		}

		#endregion

		#region IEnumerable Members

		/// <summary>
		/// Returns an enumerator that iterates through this set. 
		/// </summary>
		/// <returns>An enumerator over the elements in this set.</returns>
		System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
		{
			return GetEnumerator();
		}

		#endregion
	}
}
