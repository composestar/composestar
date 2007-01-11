using System;
using System.Collections.Generic;

namespace Composestar.StarLight.CoreServices
{
	public interface ISet<T> : ICollection<T>
	{
	}

	public class Set<T> : ISet<T>
	{
		private IDictionary<T, bool> _dict;

		public Set()
		{
			_dict = new Dictionary<T, bool>();
		}

		public Set(IEnumerable<T> collection)
		: base()
		{
			foreach (T item in collection)
				_dict[item] = true;
		}

		#region ICollection<T> Members

		public void Add(T item)
		{
			_dict[item] = true;
		}

		public bool Remove(T item)
		{
			return _dict.Remove(item);
		}

		public bool Contains(T item)
		{
			return _dict.ContainsKey(item);
		}

		public void Clear()
		{
			_dict.Clear();
		}

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

		public int Count
		{
			get { return _dict.Count; }
		}

		public bool IsReadOnly
		{
			get { return false; }
		}

		#endregion

		#region IEnumerable<T> Members

		public IEnumerator<T> GetEnumerator()
		{
			return _dict.Keys.GetEnumerator();
		}

		#endregion

		#region IEnumerable Members

		System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
		{
			return GetEnumerator();
		}

		#endregion
	}
}
