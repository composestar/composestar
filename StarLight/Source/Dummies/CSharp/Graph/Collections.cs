using System;
using System.Collections;

namespace DDW.CSharp.Dom
{
	#region CompileUnitCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="CompileUnit"/> objects.
	/// </summary>
	[Serializable]
	public class CompileUnitCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		public override GraphTypes GraphType{get{return GraphTypes.CompileUnitCollection;}}
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="CompileUnitCollection"/>.
		/// </summary>
		public interface ICompileUnitCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			CompileUnit Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;

		#region Implementation (data)
		private CompileUnit[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>CompileUnitCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>CompileUnitCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static CompileUnitCollection Synchronized(CompileUnitCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncCompileUnitCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>CompileUnitCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>CompileUnitCollection</c> wrapper that is read-only.
		/// </returns>
		public static CompileUnitCollection ReadOnly(CompileUnitCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyCompileUnitCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>CompileUnitCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public CompileUnitCollection()
		{
			m_array = new CompileUnit[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>CompileUnitCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>CompileUnitCollection</c> is initially capable of storing.
		///	</param>
		public CompileUnitCollection(int capacity)
		{
			m_array = new CompileUnit[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>CompileUnitCollection</c> class
		///		that contains elements copied from the specified <c>CompileUnitCollection</c>.
		/// </summary>
		/// <param name="c">The <c>CompileUnitCollection</c> whose elements are copied to the new collection.</param>
		public CompileUnitCollection(CompileUnitCollection c)
		{
			m_array = new CompileUnit[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>CompileUnitCollection</c> class
		///		that contains elements copied from the specified <see cref="CompileUnit"/> array.
		/// </summary>
		/// <param name="a">The <see cref="CompileUnit"/> array whose elements are copied to the new list.</param>
		public CompileUnitCollection(CompileUnit[] a)
		{
			m_array = new CompileUnit[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected CompileUnitCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>CompileUnitCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>CompileUnitCollection</c> to a one-dimensional
		///		<see cref="CompileUnit"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="CompileUnit"/> array to copy to.</param>
		public virtual void CopyTo(CompileUnit[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>CompileUnitCollection</c> to a one-dimensional
		///		<see cref="CompileUnit"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="CompileUnit"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(CompileUnit[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="CompileUnit"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CompileUnitCollection.Count"/>.</para>
		/// </exception>
		public virtual CompileUnit this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="CompileUnit"/> to the end of the <c>CompileUnitCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="CompileUnit"/> to be added to the end of the <c>CompileUnitCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(CompileUnit item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>CompileUnitCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new CompileUnit[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="CompileUnitCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			CompileUnitCollection newColl = new CompileUnitCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="CompileUnit"/> is in the <c>CompileUnitCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="CompileUnit"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>CompileUnitCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(CompileUnit item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="CompileUnit"/>
		///		in the <c>CompileUnitCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="CompileUnit"/> to locate in the <c>CompileUnitCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>CompileUnitCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(CompileUnit item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>CompileUnitCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="CompileUnit"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CompileUnitCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, CompileUnit item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="CompileUnit"/> from the <c>CompileUnitCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="CompileUnit"/> to remove from the <c>CompileUnitCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="CompileUnit"/> was not found in the <c>CompileUnitCollection</c>.
		/// </exception>
		public virtual void Remove(CompileUnit item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>CompileUnitCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CompileUnitCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			CompileUnit[] temp = new CompileUnit[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>CompileUnitCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>CompileUnitCollection</c>.</returns>
		public virtual ICompileUnitCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>CompileUnitCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						CompileUnit[] temp = new CompileUnit[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new CompileUnit[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>CompileUnitCollection</c> to the current <c>CompileUnitCollection</c>.
		/// </summary>
		/// <param name="x">The <c>CompileUnitCollection</c> whose elements should be added to the end of the current <c>CompileUnitCollection</c>.</param>
		/// <returns>The new <see cref="CompileUnitCollection.Count"/> of the <c>CompileUnitCollection</c>.</returns>
		public virtual int AddRange(CompileUnitCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="CompileUnit"/> array to the current <c>CompileUnitCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="CompileUnit"/> array whose elements should be added to the end of the <c>CompileUnitCollection</c>.</param>
		/// <returns>The new <see cref="CompileUnitCollection.Count"/> of the <c>CompileUnitCollection</c>.</returns>
		public virtual int AddRange(CompileUnit[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CompileUnitCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CompileUnitCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((CompileUnit[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (CompileUnit)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((CompileUnit)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((CompileUnit)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((CompileUnit)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (CompileUnit)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((CompileUnit)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="CompileUnitCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, ICompileUnitCollectionEnumerator
		{
			#region Implementation (data)
			
			private CompileUnitCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(CompileUnitCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public CompileUnit Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncCompileUnitCollection : CompileUnitCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private CompileUnitCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncCompileUnitCollection(CompileUnitCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(CompileUnit[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(CompileUnit[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override CompileUnit this[int i]
			{
				get
				{
					CompileUnit thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(CompileUnit x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(CompileUnit x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(CompileUnit x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, CompileUnit x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(CompileUnit x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override ICompileUnitCollectionEnumerator GetEnumerator()
			{
				ICompileUnitCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(CompileUnitCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(CompileUnit[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyCompileUnitCollection : CompileUnitCollection
		{
			#region Implementation (data)
			private CompileUnitCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyCompileUnitCollection(CompileUnitCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(CompileUnit[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(CompileUnit[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override CompileUnit this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(CompileUnit x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(CompileUnit x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(CompileUnit x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, CompileUnit x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(CompileUnit x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override ICompileUnitCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(CompileUnitCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(CompileUnit[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion

		#region Text
		public override string Text
		{
			get
			{
				return "Compile Units";
			}
		}
		#endregion

	}
	#endregion
	#region ImportCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="Import"/> objects.
	/// </summary>
	[Serializable]
	public class ImportCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		public override GraphTypes GraphType{get{return GraphTypes.ImportCollection;}}
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="ImportCollection"/>.
		/// </summary>
		public interface IImportCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			Import Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;

		#region Implementation (data)
		private Import[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>ImportCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>ImportCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static ImportCollection Synchronized(ImportCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncImportCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>ImportCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>ImportCollection</c> wrapper that is read-only.
		/// </returns>
		public static ImportCollection ReadOnly(ImportCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyImportCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>ImportCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public ImportCollection()
		{
			m_array = new Import[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>ImportCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>ImportCollection</c> is initially capable of storing.
		///	</param>
		public ImportCollection(int capacity)
		{
			m_array = new Import[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>ImportCollection</c> class
		///		that contains elements copied from the specified <c>ImportCollection</c>.
		/// </summary>
		/// <param name="c">The <c>ImportCollection</c> whose elements are copied to the new collection.</param>
		public ImportCollection(ImportCollection c)
		{
			m_array = new Import[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>ImportCollection</c> class
		///		that contains elements copied from the specified <see cref="Import"/> array.
		/// </summary>
		/// <param name="a">The <see cref="Import"/> array whose elements are copied to the new list.</param>
		public ImportCollection(Import[] a)
		{
			m_array = new Import[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected ImportCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>ImportCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>ImportCollection</c> to a one-dimensional
		///		<see cref="Import"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="Import"/> array to copy to.</param>
		public virtual void CopyTo(Import[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>ImportCollection</c> to a one-dimensional
		///		<see cref="Import"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="Import"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(Import[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="Import"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ImportCollection.Count"/>.</para>
		/// </exception>
		public virtual Import this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="Import"/> to the end of the <c>ImportCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Import"/> to be added to the end of the <c>ImportCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(Import item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>ImportCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new Import[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="ImportCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			ImportCollection newColl = new ImportCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="Import"/> is in the <c>ImportCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Import"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>ImportCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(Import item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="Import"/>
		///		in the <c>ImportCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Import"/> to locate in the <c>ImportCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>ImportCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(Import item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>ImportCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="Import"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ImportCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, Import item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="Import"/> from the <c>ImportCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Import"/> to remove from the <c>ImportCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="Import"/> was not found in the <c>ImportCollection</c>.
		/// </exception>
		public virtual void Remove(Import item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>ImportCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ImportCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			Import[] temp = new Import[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>ImportCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>ImportCollection</c>.</returns>
		public virtual IImportCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>ImportCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						Import[] temp = new Import[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new Import[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>ImportCollection</c> to the current <c>ImportCollection</c>.
		/// </summary>
		/// <param name="x">The <c>ImportCollection</c> whose elements should be added to the end of the current <c>ImportCollection</c>.</param>
		/// <returns>The new <see cref="ImportCollection.Count"/> of the <c>ImportCollection</c>.</returns>
		public virtual int AddRange(ImportCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="Import"/> array to the current <c>ImportCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="Import"/> array whose elements should be added to the end of the <c>ImportCollection</c>.</param>
		/// <returns>The new <see cref="ImportCollection.Count"/> of the <c>ImportCollection</c>.</returns>
		public virtual int AddRange(Import[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ImportCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ImportCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((Import[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (Import)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((Import)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((Import)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((Import)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (Import)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((Import)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="ImportCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, IImportCollectionEnumerator
		{
			#region Implementation (data)
			
			private ImportCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(ImportCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public Import Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncImportCollection : ImportCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private ImportCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncImportCollection(ImportCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(Import[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(Import[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override Import this[int i]
			{
				get
				{
					Import thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(Import x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(Import x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(Import x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, Import x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(Import x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override IImportCollectionEnumerator GetEnumerator()
			{
				IImportCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(ImportCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(Import[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyImportCollection : ImportCollection
		{
			#region Implementation (data)
			private ImportCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyImportCollection(ImportCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(Import[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(Import[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override Import this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(Import x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(Import x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(Import x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, Import x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(Import x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override IImportCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(ImportCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(Import[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion

		#region Text
		public override string Text
		{
			get
			{
				return "Imports";
			}
		}
		#endregion

	}
	#endregion
	#region NamespaceDeclCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="NamespaceDecl"/> objects.
	/// </summary>
	[Serializable]
	public class NamespaceDeclCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		public override GraphTypes GraphType{get{return GraphTypes.NamespaceDeclCollection;}}
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="NamespaceDeclCollection"/>.
		/// </summary>
		public interface INamespaceDeclCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			NamespaceDecl Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;

		#region Implementation (data)
		private NamespaceDecl[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>NamespaceDeclCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>NamespaceDeclCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static NamespaceDeclCollection Synchronized(NamespaceDeclCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncNamespaceDeclCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>NamespaceDeclCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>NamespaceDeclCollection</c> wrapper that is read-only.
		/// </returns>
		public static NamespaceDeclCollection ReadOnly(NamespaceDeclCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyNamespaceDeclCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>NamespaceDeclCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public NamespaceDeclCollection()
		{
			m_array = new NamespaceDecl[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>NamespaceDeclCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>NamespaceDeclCollection</c> is initially capable of storing.
		///	</param>
		public NamespaceDeclCollection(int capacity)
		{
			m_array = new NamespaceDecl[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>NamespaceDeclCollection</c> class
		///		that contains elements copied from the specified <c>NamespaceDeclCollection</c>.
		/// </summary>
		/// <param name="c">The <c>NamespaceDeclCollection</c> whose elements are copied to the new collection.</param>
		public NamespaceDeclCollection(NamespaceDeclCollection c)
		{
			m_array = new NamespaceDecl[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>NamespaceDeclCollection</c> class
		///		that contains elements copied from the specified <see cref="NamespaceDecl"/> array.
		/// </summary>
		/// <param name="a">The <see cref="NamespaceDecl"/> array whose elements are copied to the new list.</param>
		public NamespaceDeclCollection(NamespaceDecl[] a)
		{
			m_array = new NamespaceDecl[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected NamespaceDeclCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>NamespaceDeclCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>NamespaceDeclCollection</c> to a one-dimensional
		///		<see cref="NamespaceDecl"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="NamespaceDecl"/> array to copy to.</param>
		public virtual void CopyTo(NamespaceDecl[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>NamespaceDeclCollection</c> to a one-dimensional
		///		<see cref="NamespaceDecl"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="NamespaceDecl"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(NamespaceDecl[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="NamespaceDecl"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="NamespaceDeclCollection.Count"/>.</para>
		/// </exception>
		public virtual NamespaceDecl this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="NamespaceDecl"/> to the end of the <c>NamespaceDeclCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="NamespaceDecl"/> to be added to the end of the <c>NamespaceDeclCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(NamespaceDecl item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>NamespaceDeclCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new NamespaceDecl[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="NamespaceDeclCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			NamespaceDeclCollection newColl = new NamespaceDeclCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="NamespaceDecl"/> is in the <c>NamespaceDeclCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="NamespaceDecl"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>NamespaceDeclCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(NamespaceDecl item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="NamespaceDecl"/>
		///		in the <c>NamespaceDeclCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="NamespaceDecl"/> to locate in the <c>NamespaceDeclCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>NamespaceDeclCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(NamespaceDecl item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>NamespaceDeclCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="NamespaceDecl"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="NamespaceDeclCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, NamespaceDecl item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="NamespaceDecl"/> from the <c>NamespaceDeclCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="NamespaceDecl"/> to remove from the <c>NamespaceDeclCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="NamespaceDecl"/> was not found in the <c>NamespaceDeclCollection</c>.
		/// </exception>
		public virtual void Remove(NamespaceDecl item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>NamespaceDeclCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="NamespaceDeclCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			NamespaceDecl[] temp = new NamespaceDecl[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>NamespaceDeclCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>NamespaceDeclCollection</c>.</returns>
		public virtual INamespaceDeclCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>NamespaceDeclCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						NamespaceDecl[] temp = new NamespaceDecl[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new NamespaceDecl[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>NamespaceDeclCollection</c> to the current <c>NamespaceDeclCollection</c>.
		/// </summary>
		/// <param name="x">The <c>NamespaceDeclCollection</c> whose elements should be added to the end of the current <c>NamespaceDeclCollection</c>.</param>
		/// <returns>The new <see cref="NamespaceDeclCollection.Count"/> of the <c>NamespaceDeclCollection</c>.</returns>
		public virtual int AddRange(NamespaceDeclCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="NamespaceDecl"/> array to the current <c>NamespaceDeclCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="NamespaceDecl"/> array whose elements should be added to the end of the <c>NamespaceDeclCollection</c>.</param>
		/// <returns>The new <see cref="NamespaceDeclCollection.Count"/> of the <c>NamespaceDeclCollection</c>.</returns>
		public virtual int AddRange(NamespaceDecl[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="NamespaceDeclCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="NamespaceDeclCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((NamespaceDecl[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (NamespaceDecl)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((NamespaceDecl)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((NamespaceDecl)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((NamespaceDecl)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (NamespaceDecl)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((NamespaceDecl)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="NamespaceDeclCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, INamespaceDeclCollectionEnumerator
		{
			#region Implementation (data)
			
			private NamespaceDeclCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(NamespaceDeclCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public NamespaceDecl Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncNamespaceDeclCollection : NamespaceDeclCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private NamespaceDeclCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncNamespaceDeclCollection(NamespaceDeclCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(NamespaceDecl[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(NamespaceDecl[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override NamespaceDecl this[int i]
			{
				get
				{
					NamespaceDecl thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(NamespaceDecl x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(NamespaceDecl x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(NamespaceDecl x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, NamespaceDecl x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(NamespaceDecl x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override INamespaceDeclCollectionEnumerator GetEnumerator()
			{
				INamespaceDeclCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(NamespaceDeclCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(NamespaceDecl[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyNamespaceDeclCollection : NamespaceDeclCollection
		{
			#region Implementation (data)
			private NamespaceDeclCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyNamespaceDeclCollection(NamespaceDeclCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(NamespaceDecl[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(NamespaceDecl[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override NamespaceDecl this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(NamespaceDecl x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(NamespaceDecl x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(NamespaceDecl x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, NamespaceDecl x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(NamespaceDecl x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override INamespaceDeclCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(NamespaceDeclCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(NamespaceDecl[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion

		#region Text
		public override string Text
		{
			get
			{
				return "Namespaces";
			}
		}
		#endregion

	}
	#endregion
	#region TypeDeclCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="TypeDecl"/> objects.
	/// </summary>
	[Serializable]
	public class TypeDeclCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		public override GraphTypes GraphType{get{return GraphTypes.TypeDeclCollection;}}
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="TypeDeclCollection"/>.
		/// </summary>
		public interface ITypeDeclCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			TypeDecl Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;

		#region Implementation (data)
		private TypeDecl[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>TypeDeclCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>TypeDeclCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static TypeDeclCollection Synchronized(TypeDeclCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncTypeDeclCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>TypeDeclCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>TypeDeclCollection</c> wrapper that is read-only.
		/// </returns>
		public static TypeDeclCollection ReadOnly(TypeDeclCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyTypeDeclCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>TypeDeclCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public TypeDeclCollection()
		{
			m_array = new TypeDecl[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>TypeDeclCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>TypeDeclCollection</c> is initially capable of storing.
		///	</param>
		public TypeDeclCollection(int capacity)
		{
			m_array = new TypeDecl[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>TypeDeclCollection</c> class
		///		that contains elements copied from the specified <c>TypeDeclCollection</c>.
		/// </summary>
		/// <param name="c">The <c>TypeDeclCollection</c> whose elements are copied to the new collection.</param>
		public TypeDeclCollection(TypeDeclCollection c)
		{
			m_array = new TypeDecl[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>TypeDeclCollection</c> class
		///		that contains elements copied from the specified <see cref="TypeDecl"/> array.
		/// </summary>
		/// <param name="a">The <see cref="TypeDecl"/> array whose elements are copied to the new list.</param>
		public TypeDeclCollection(TypeDecl[] a)
		{
			m_array = new TypeDecl[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected TypeDeclCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>TypeDeclCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>TypeDeclCollection</c> to a one-dimensional
		///		<see cref="TypeDecl"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="TypeDecl"/> array to copy to.</param>
		public virtual void CopyTo(TypeDecl[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>TypeDeclCollection</c> to a one-dimensional
		///		<see cref="TypeDecl"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="TypeDecl"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(TypeDecl[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="TypeDecl"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="TypeDeclCollection.Count"/>.</para>
		/// </exception>
		public virtual TypeDecl this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="TypeDecl"/> to the end of the <c>TypeDeclCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="TypeDecl"/> to be added to the end of the <c>TypeDeclCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(TypeDecl item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>TypeDeclCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new TypeDecl[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="TypeDeclCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			TypeDeclCollection newColl = new TypeDeclCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="TypeDecl"/> is in the <c>TypeDeclCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="TypeDecl"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>TypeDeclCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(TypeDecl item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="TypeDecl"/>
		///		in the <c>TypeDeclCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="TypeDecl"/> to locate in the <c>TypeDeclCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>TypeDeclCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(TypeDecl item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>TypeDeclCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="TypeDecl"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="TypeDeclCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, TypeDecl item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="TypeDecl"/> from the <c>TypeDeclCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="TypeDecl"/> to remove from the <c>TypeDeclCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="TypeDecl"/> was not found in the <c>TypeDeclCollection</c>.
		/// </exception>
		public virtual void Remove(TypeDecl item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>TypeDeclCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="TypeDeclCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			TypeDecl[] temp = new TypeDecl[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>TypeDeclCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>TypeDeclCollection</c>.</returns>
		public virtual ITypeDeclCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>TypeDeclCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						TypeDecl[] temp = new TypeDecl[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new TypeDecl[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>TypeDeclCollection</c> to the current <c>TypeDeclCollection</c>.
		/// </summary>
		/// <param name="x">The <c>TypeDeclCollection</c> whose elements should be added to the end of the current <c>TypeDeclCollection</c>.</param>
		/// <returns>The new <see cref="TypeDeclCollection.Count"/> of the <c>TypeDeclCollection</c>.</returns>
		public virtual int AddRange(TypeDeclCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="TypeDecl"/> array to the current <c>TypeDeclCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="TypeDecl"/> array whose elements should be added to the end of the <c>TypeDeclCollection</c>.</param>
		/// <returns>The new <see cref="TypeDeclCollection.Count"/> of the <c>TypeDeclCollection</c>.</returns>
		public virtual int AddRange(TypeDecl[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="TypeDeclCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="TypeDeclCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((TypeDecl[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (TypeDecl)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((TypeDecl)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((TypeDecl)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((TypeDecl)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (TypeDecl)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((TypeDecl)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="TypeDeclCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, ITypeDeclCollectionEnumerator
		{
			#region Implementation (data)
			
			private TypeDeclCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(TypeDeclCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public TypeDecl Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncTypeDeclCollection : TypeDeclCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private TypeDeclCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncTypeDeclCollection(TypeDeclCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(TypeDecl[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(TypeDecl[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override TypeDecl this[int i]
			{
				get
				{
					TypeDecl thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(TypeDecl x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(TypeDecl x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(TypeDecl x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, TypeDecl x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(TypeDecl x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override ITypeDeclCollectionEnumerator GetEnumerator()
			{
				ITypeDeclCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(TypeDeclCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(TypeDecl[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyTypeDeclCollection : TypeDeclCollection
		{
			#region Implementation (data)
			private TypeDeclCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyTypeDeclCollection(TypeDeclCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(TypeDecl[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(TypeDecl[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override TypeDecl this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(TypeDecl x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(TypeDecl x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(TypeDecl x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, TypeDecl x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(TypeDecl x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override ITypeDeclCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(TypeDeclCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(TypeDecl[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion

		#region Text
		public override string Text
		{
			get
			{
				return "TypeDeclarations";
			}
		}
		#endregion

	}
	#endregion
	#region MemberDeclCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="MemberDecl"/> objects.
	/// </summary>
	[Serializable]
	public class MemberDeclCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		public override GraphTypes GraphType{get{return GraphTypes.MemberDeclCollection;}}
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="MemberDeclCollection"/>.
		/// </summary>
		public interface IMemberDeclCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			MemberDecl Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;

		#region Implementation (data)
		private MemberDecl[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>MemberDeclCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>MemberDeclCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static MemberDeclCollection Synchronized(MemberDeclCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncMemberDeclCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>MemberDeclCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>MemberDeclCollection</c> wrapper that is read-only.
		/// </returns>
		public static MemberDeclCollection ReadOnly(MemberDeclCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyMemberDeclCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>MemberDeclCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public MemberDeclCollection()
		{
			m_array = new MemberDecl[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>MemberDeclCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>MemberDeclCollection</c> is initially capable of storing.
		///	</param>
		public MemberDeclCollection(int capacity)
		{
			m_array = new MemberDecl[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>MemberDeclCollection</c> class
		///		that contains elements copied from the specified <c>MemberDeclCollection</c>.
		/// </summary>
		/// <param name="c">The <c>MemberDeclCollection</c> whose elements are copied to the new collection.</param>
		public MemberDeclCollection(MemberDeclCollection c)
		{
			m_array = new MemberDecl[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>MemberDeclCollection</c> class
		///		that contains elements copied from the specified <see cref="MemberDecl"/> array.
		/// </summary>
		/// <param name="a">The <see cref="MemberDecl"/> array whose elements are copied to the new list.</param>
		public MemberDeclCollection(MemberDecl[] a)
		{
			m_array = new MemberDecl[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected MemberDeclCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>MemberDeclCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>MemberDeclCollection</c> to a one-dimensional
		///		<see cref="MemberDecl"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="MemberDecl"/> array to copy to.</param>
		public virtual void CopyTo(MemberDecl[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>MemberDeclCollection</c> to a one-dimensional
		///		<see cref="MemberDecl"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="MemberDecl"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(MemberDecl[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="MemberDecl"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="MemberDeclCollection.Count"/>.</para>
		/// </exception>
		public virtual MemberDecl this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="MemberDecl"/> to the end of the <c>MemberDeclCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="MemberDecl"/> to be added to the end of the <c>MemberDeclCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(MemberDecl item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>MemberDeclCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new MemberDecl[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="MemberDeclCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			MemberDeclCollection newColl = new MemberDeclCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="MemberDecl"/> is in the <c>MemberDeclCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="MemberDecl"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>MemberDeclCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(MemberDecl item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="MemberDecl"/>
		///		in the <c>MemberDeclCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="MemberDecl"/> to locate in the <c>MemberDeclCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>MemberDeclCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(MemberDecl item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>MemberDeclCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="MemberDecl"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="MemberDeclCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, MemberDecl item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="MemberDecl"/> from the <c>MemberDeclCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="MemberDecl"/> to remove from the <c>MemberDeclCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="MemberDecl"/> was not found in the <c>MemberDeclCollection</c>.
		/// </exception>
		public virtual void Remove(MemberDecl item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>MemberDeclCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="MemberDeclCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			MemberDecl[] temp = new MemberDecl[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>MemberDeclCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>MemberDeclCollection</c>.</returns>
		public virtual IMemberDeclCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>MemberDeclCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						MemberDecl[] temp = new MemberDecl[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new MemberDecl[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>MemberDeclCollection</c> to the current <c>MemberDeclCollection</c>.
		/// </summary>
		/// <param name="x">The <c>MemberDeclCollection</c> whose elements should be added to the end of the current <c>MemberDeclCollection</c>.</param>
		/// <returns>The new <see cref="MemberDeclCollection.Count"/> of the <c>MemberDeclCollection</c>.</returns>
		public virtual int AddRange(MemberDeclCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="MemberDecl"/> array to the current <c>MemberDeclCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="MemberDecl"/> array whose elements should be added to the end of the <c>MemberDeclCollection</c>.</param>
		/// <returns>The new <see cref="MemberDeclCollection.Count"/> of the <c>MemberDeclCollection</c>.</returns>
		public virtual int AddRange(MemberDecl[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="MemberDeclCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="MemberDeclCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((MemberDecl[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (MemberDecl)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((MemberDecl)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((MemberDecl)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((MemberDecl)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (MemberDecl)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((MemberDecl)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="MemberDeclCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, IMemberDeclCollectionEnumerator
		{
			#region Implementation (data)
			
			private MemberDeclCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(MemberDeclCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public MemberDecl Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncMemberDeclCollection : MemberDeclCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private MemberDeclCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncMemberDeclCollection(MemberDeclCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(MemberDecl[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(MemberDecl[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override MemberDecl this[int i]
			{
				get
				{
					MemberDecl thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(MemberDecl x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(MemberDecl x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(MemberDecl x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, MemberDecl x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(MemberDecl x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override IMemberDeclCollectionEnumerator GetEnumerator()
			{
				IMemberDeclCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(MemberDeclCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(MemberDecl[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyMemberDeclCollection : MemberDeclCollection
		{
			#region Implementation (data)
			private MemberDeclCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyMemberDeclCollection(MemberDeclCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(MemberDecl[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(MemberDecl[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override MemberDecl this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(MemberDecl x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(MemberDecl x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(MemberDecl x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, MemberDecl x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(MemberDecl x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override IMemberDeclCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(MemberDeclCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(MemberDecl[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion

		#region Text
		public override string Text
		{
			get
			{
				return "Members";
			}
		}
		#endregion

	}
	#endregion
	#region TypeRefCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="TypeRef"/> objects.
	/// </summary>
	[Serializable]
	public class TypeRefCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		public override GraphTypes GraphType{get{return GraphTypes.TypeRefCollection;}}
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="TypeRefCollection"/>.
		/// </summary>
		public interface ITypeRefCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			TypeRef Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;

		#region Implementation (data)
		private TypeRef[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>TypeRefCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>TypeRefCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static TypeRefCollection Synchronized(TypeRefCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncTypeRefCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>TypeRefCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>TypeRefCollection</c> wrapper that is read-only.
		/// </returns>
		public static TypeRefCollection ReadOnly(TypeRefCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyTypeRefCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>TypeRefCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public TypeRefCollection()
		{
			m_array = new TypeRef[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>TypeRefCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>TypeRefCollection</c> is initially capable of storing.
		///	</param>
		public TypeRefCollection(int capacity)
		{
			m_array = new TypeRef[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>TypeRefCollection</c> class
		///		that contains elements copied from the specified <c>TypeRefCollection</c>.
		/// </summary>
		/// <param name="c">The <c>TypeRefCollection</c> whose elements are copied to the new collection.</param>
		public TypeRefCollection(TypeRefCollection c)
		{
			m_array = new TypeRef[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>TypeRefCollection</c> class
		///		that contains elements copied from the specified <see cref="TypeRef"/> array.
		/// </summary>
		/// <param name="a">The <see cref="TypeRef"/> array whose elements are copied to the new list.</param>
		public TypeRefCollection(TypeRef[] a)
		{
			m_array = new TypeRef[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected TypeRefCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>TypeRefCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>TypeRefCollection</c> to a one-dimensional
		///		<see cref="TypeRef"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="TypeRef"/> array to copy to.</param>
		public virtual void CopyTo(TypeRef[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>TypeRefCollection</c> to a one-dimensional
		///		<see cref="TypeRef"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="TypeRef"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(TypeRef[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="TypeRef"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="TypeRefCollection.Count"/>.</para>
		/// </exception>
		public virtual TypeRef this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="TypeRef"/> to the end of the <c>TypeRefCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="TypeRef"/> to be added to the end of the <c>TypeRefCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(TypeRef item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>TypeRefCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new TypeRef[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="TypeRefCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			TypeRefCollection newColl = new TypeRefCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="TypeRef"/> is in the <c>TypeRefCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="TypeRef"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>TypeRefCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(TypeRef item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="TypeRef"/>
		///		in the <c>TypeRefCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="TypeRef"/> to locate in the <c>TypeRefCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>TypeRefCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(TypeRef item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>TypeRefCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="TypeRef"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="TypeRefCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, TypeRef item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="TypeRef"/> from the <c>TypeRefCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="TypeRef"/> to remove from the <c>TypeRefCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="TypeRef"/> was not found in the <c>TypeRefCollection</c>.
		/// </exception>
		public virtual void Remove(TypeRef item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>TypeRefCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="TypeRefCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			TypeRef[] temp = new TypeRef[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>TypeRefCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>TypeRefCollection</c>.</returns>
		public virtual ITypeRefCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>TypeRefCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						TypeRef[] temp = new TypeRef[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new TypeRef[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>TypeRefCollection</c> to the current <c>TypeRefCollection</c>.
		/// </summary>
		/// <param name="x">The <c>TypeRefCollection</c> whose elements should be added to the end of the current <c>TypeRefCollection</c>.</param>
		/// <returns>The new <see cref="TypeRefCollection.Count"/> of the <c>TypeRefCollection</c>.</returns>
		public virtual int AddRange(TypeRefCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="TypeRef"/> array to the current <c>TypeRefCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="TypeRef"/> array whose elements should be added to the end of the <c>TypeRefCollection</c>.</param>
		/// <returns>The new <see cref="TypeRefCollection.Count"/> of the <c>TypeRefCollection</c>.</returns>
		public virtual int AddRange(TypeRef[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="TypeRefCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="TypeRefCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((TypeRef[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (TypeRef)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((TypeRef)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((TypeRef)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((TypeRef)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (TypeRef)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((TypeRef)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="TypeRefCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, ITypeRefCollectionEnumerator
		{
			#region Implementation (data)
			
			private TypeRefCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(TypeRefCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public TypeRef Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncTypeRefCollection : TypeRefCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private TypeRefCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncTypeRefCollection(TypeRefCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(TypeRef[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(TypeRef[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override TypeRef this[int i]
			{
				get
				{
					TypeRef thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(TypeRef x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(TypeRef x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(TypeRef x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, TypeRef x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(TypeRef x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override ITypeRefCollectionEnumerator GetEnumerator()
			{
				ITypeRefCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(TypeRefCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(TypeRef[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyTypeRefCollection : TypeRefCollection
		{
			#region Implementation (data)
			private TypeRefCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyTypeRefCollection(TypeRefCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(TypeRef[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(TypeRef[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override TypeRef this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(TypeRef x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(TypeRef x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(TypeRef x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, TypeRef x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(TypeRef x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override ITypeRefCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(TypeRefCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(TypeRef[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion

		#region Text
		public override string Text
		{
			get
			{
				return "Type References";
			}
		}
		#endregion

	}
	#endregion	
	#region ParamCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="Param"/> objects.
	/// </summary>
	[Serializable]
	public class ParamCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		public override GraphTypes GraphType{get{return GraphTypes.ParamCollection;}}
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="ParamCollection"/>.
		/// </summary>
		public interface IParamCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			Param Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;

		#region Implementation (data)
		private Param[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>ParamCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>ParamCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static ParamCollection Synchronized(ParamCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncParamCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>ParamCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>ParamCollection</c> wrapper that is read-only.
		/// </returns>
		public static ParamCollection ReadOnly(ParamCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyParamCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>ParamCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public ParamCollection()
		{
			m_array = new Param[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>ParamCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>ParamCollection</c> is initially capable of storing.
		///	</param>
		public ParamCollection(int capacity)
		{
			m_array = new Param[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>ParamCollection</c> class
		///		that contains elements copied from the specified <c>ParamCollection</c>.
		/// </summary>
		/// <param name="c">The <c>ParamCollection</c> whose elements are copied to the new collection.</param>
		public ParamCollection(ParamCollection c)
		{
			m_array = new Param[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>ParamCollection</c> class
		///		that contains elements copied from the specified <see cref="Param"/> array.
		/// </summary>
		/// <param name="a">The <see cref="Param"/> array whose elements are copied to the new list.</param>
		public ParamCollection(Param[] a)
		{
			m_array = new Param[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected ParamCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>ParamCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>ParamCollection</c> to a one-dimensional
		///		<see cref="Param"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="Param"/> array to copy to.</param>
		public virtual void CopyTo(Param[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>ParamCollection</c> to a one-dimensional
		///		<see cref="Param"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="Param"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(Param[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="Param"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ParamCollection.Count"/>.</para>
		/// </exception>
		public virtual Param this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="Param"/> to the end of the <c>ParamCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Param"/> to be added to the end of the <c>ParamCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(Param item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>ParamCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new Param[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="ParamCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			ParamCollection newColl = new ParamCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="Param"/> is in the <c>ParamCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Param"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>ParamCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(Param item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="Param"/>
		///		in the <c>ParamCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Param"/> to locate in the <c>ParamCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>ParamCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(Param item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>ParamCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="Param"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ParamCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, Param item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="Param"/> from the <c>ParamCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Param"/> to remove from the <c>ParamCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="Param"/> was not found in the <c>ParamCollection</c>.
		/// </exception>
		public virtual void Remove(Param item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>ParamCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ParamCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			Param[] temp = new Param[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>ParamCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>ParamCollection</c>.</returns>
		public virtual IParamCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>ParamCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						Param[] temp = new Param[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new Param[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>ParamCollection</c> to the current <c>ParamCollection</c>.
		/// </summary>
		/// <param name="x">The <c>ParamCollection</c> whose elements should be added to the end of the current <c>ParamCollection</c>.</param>
		/// <returns>The new <see cref="ParamCollection.Count"/> of the <c>ParamCollection</c>.</returns>
		public virtual int AddRange(ParamCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="Param"/> array to the current <c>ParamCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="Param"/> array whose elements should be added to the end of the <c>ParamCollection</c>.</param>
		/// <returns>The new <see cref="ParamCollection.Count"/> of the <c>ParamCollection</c>.</returns>
		public virtual int AddRange(Param[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ParamCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ParamCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((Param[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (Param)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((Param)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((Param)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((Param)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (Param)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((Param)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="ParamCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, IParamCollectionEnumerator
		{
			#region Implementation (data)
			
			private ParamCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(ParamCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public Param Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncParamCollection : ParamCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private ParamCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncParamCollection(ParamCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(Param[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(Param[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override Param this[int i]
			{
				get
				{
					Param thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(Param x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(Param x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(Param x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, Param x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(Param x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override IParamCollectionEnumerator GetEnumerator()
			{
				IParamCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(ParamCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(Param[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyParamCollection : ParamCollection
		{
			#region Implementation (data)
			private ParamCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyParamCollection(ParamCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(Param[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(Param[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override Param this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(Param x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(Param x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(Param x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, Param x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(Param x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override IParamCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(ParamCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(Param[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion
		
		#region Text
		public override string Text
		{
			get
			{
				return "Paramaters";
			}
		}
		#endregion
	}
	#endregion
	#region ParamDeclCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="ParamDecl"/> objects.
	/// </summary>
	[Serializable]
	public class ParamDeclCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		public override GraphTypes GraphType{get{return GraphTypes.ParamDeclCollection;}}
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="ParamDeclCollection"/>.
		/// </summary>
		public interface IParamDeclCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			ParamDecl Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;

		#region Implementation (data)
		private ParamDecl[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>ParamDeclCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>ParamDeclCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static ParamDeclCollection Synchronized(ParamDeclCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncParamDeclCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>ParamDeclCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>ParamDeclCollection</c> wrapper that is read-only.
		/// </returns>
		public static ParamDeclCollection ReadOnly(ParamDeclCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyParamDeclCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>ParamDeclCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public ParamDeclCollection()
		{
			m_array = new ParamDecl[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>ParamDeclCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>ParamDeclCollection</c> is initially capable of storing.
		///	</param>
		public ParamDeclCollection(int capacity)
		{
			m_array = new ParamDecl[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>ParamDeclCollection</c> class
		///		that contains elements copied from the specified <c>ParamDeclCollection</c>.
		/// </summary>
		/// <param name="c">The <c>ParamDeclCollection</c> whose elements are copied to the new collection.</param>
		public ParamDeclCollection(ParamDeclCollection c)
		{
			m_array = new ParamDecl[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>ParamDeclCollection</c> class
		///		that contains elements copied from the specified <see cref="ParamDecl"/> array.
		/// </summary>
		/// <param name="a">The <see cref="ParamDecl"/> array whose elements are copied to the new list.</param>
		public ParamDeclCollection(ParamDecl[] a)
		{
			m_array = new ParamDecl[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected ParamDeclCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>ParamDeclCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>ParamDeclCollection</c> to a one-dimensional
		///		<see cref="ParamDecl"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="ParamDecl"/> array to copy to.</param>
		public virtual void CopyTo(ParamDecl[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>ParamDeclCollection</c> to a one-dimensional
		///		<see cref="ParamDecl"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="ParamDecl"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(ParamDecl[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="ParamDecl"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ParamDeclCollection.Count"/>.</para>
		/// </exception>
		public virtual ParamDecl this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="ParamDecl"/> to the end of the <c>ParamDeclCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="ParamDecl"/> to be added to the end of the <c>ParamDeclCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(ParamDecl item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>ParamDeclCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new ParamDecl[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="ParamDeclCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			ParamDeclCollection newColl = new ParamDeclCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="ParamDecl"/> is in the <c>ParamDeclCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="ParamDecl"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>ParamDeclCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(ParamDecl item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="ParamDecl"/>
		///		in the <c>ParamDeclCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="ParamDecl"/> to locate in the <c>ParamDeclCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>ParamDeclCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(ParamDecl item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>ParamDeclCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="ParamDecl"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ParamDeclCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, ParamDecl item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="ParamDecl"/> from the <c>ParamDeclCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="ParamDecl"/> to remove from the <c>ParamDeclCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="ParamDecl"/> was not found in the <c>ParamDeclCollection</c>.
		/// </exception>
		public virtual void Remove(ParamDecl item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>ParamDeclCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ParamDeclCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			ParamDecl[] temp = new ParamDecl[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>ParamDeclCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>ParamDeclCollection</c>.</returns>
		public virtual IParamDeclCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>ParamDeclCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						ParamDecl[] temp = new ParamDecl[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new ParamDecl[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>ParamDeclCollection</c> to the current <c>ParamDeclCollection</c>.
		/// </summary>
		/// <param name="x">The <c>ParamDeclCollection</c> whose elements should be added to the end of the current <c>ParamDeclCollection</c>.</param>
		/// <returns>The new <see cref="ParamDeclCollection.Count"/> of the <c>ParamDeclCollection</c>.</returns>
		public virtual int AddRange(ParamDeclCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="ParamDecl"/> array to the current <c>ParamDeclCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="ParamDecl"/> array whose elements should be added to the end of the <c>ParamDeclCollection</c>.</param>
		/// <returns>The new <see cref="ParamDeclCollection.Count"/> of the <c>ParamDeclCollection</c>.</returns>
		public virtual int AddRange(ParamDecl[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ParamDeclCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ParamDeclCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((ParamDecl[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (ParamDecl)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((ParamDecl)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((ParamDecl)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((ParamDecl)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (ParamDecl)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((ParamDecl)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="ParamDeclCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, IParamDeclCollectionEnumerator
		{
			#region Implementation (data)
			
			private ParamDeclCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(ParamDeclCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public ParamDecl Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncParamDeclCollection : ParamDeclCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private ParamDeclCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncParamDeclCollection(ParamDeclCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(ParamDecl[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(ParamDecl[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override ParamDecl this[int i]
			{
				get
				{
					ParamDecl thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(ParamDecl x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(ParamDecl x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(ParamDecl x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, ParamDecl x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(ParamDecl x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override IParamDeclCollectionEnumerator GetEnumerator()
			{
				IParamDeclCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(ParamDeclCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(ParamDecl[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyParamDeclCollection : ParamDeclCollection
		{
			#region Implementation (data)
			private ParamDeclCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyParamDeclCollection(ParamDeclCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(ParamDecl[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(ParamDecl[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override ParamDecl this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(ParamDecl x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(ParamDecl x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(ParamDecl x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, ParamDecl x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(ParamDecl x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override IParamDeclCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(ParamDeclCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(ParamDecl[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion
		#region Text
		public override string Text
		{
			get
			{
				return "Paramater Declarations";
			}
		}
		#endregion
	}
	#endregion
	#region StatementCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="Statement"/> objects.
	/// </summary>
	[Serializable]
	public class StatementCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		public override GraphTypes GraphType{get{return GraphTypes.StatementCollection;}}
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="StatementCollection"/>.
		/// </summary>
		public interface IStatementCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			Statement Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;

		#region Implementation (data)
		private Statement[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>StatementCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>StatementCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static StatementCollection Synchronized(StatementCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncStatementCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>StatementCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>StatementCollection</c> wrapper that is read-only.
		/// </returns>
		public static StatementCollection ReadOnly(StatementCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyStatementCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>StatementCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public StatementCollection()
		{
			m_array = new Statement[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>StatementCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>StatementCollection</c> is initially capable of storing.
		///	</param>
		public StatementCollection(int capacity)
		{
			m_array = new Statement[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>StatementCollection</c> class
		///		that contains elements copied from the specified <c>StatementCollection</c>.
		/// </summary>
		/// <param name="c">The <c>StatementCollection</c> whose elements are copied to the new collection.</param>
		public StatementCollection(StatementCollection c)
		{
			m_array = new Statement[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>StatementCollection</c> class
		///		that contains elements copied from the specified <see cref="Statement"/> array.
		/// </summary>
		/// <param name="a">The <see cref="Statement"/> array whose elements are copied to the new list.</param>
		public StatementCollection(Statement[] a)
		{
			m_array = new Statement[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected StatementCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>StatementCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>StatementCollection</c> to a one-dimensional
		///		<see cref="Statement"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="Statement"/> array to copy to.</param>
		public virtual void CopyTo(Statement[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>StatementCollection</c> to a one-dimensional
		///		<see cref="Statement"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="Statement"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(Statement[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="Statement"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="StatementCollection.Count"/>.</para>
		/// </exception>
		public virtual Statement this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="Statement"/> to the end of the <c>StatementCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Statement"/> to be added to the end of the <c>StatementCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(Statement item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>StatementCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new Statement[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="StatementCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			StatementCollection newColl = new StatementCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="Statement"/> is in the <c>StatementCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Statement"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>StatementCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(Statement item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="Statement"/>
		///		in the <c>StatementCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Statement"/> to locate in the <c>StatementCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>StatementCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(Statement item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>StatementCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="Statement"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="StatementCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, Statement item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="Statement"/> from the <c>StatementCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Statement"/> to remove from the <c>StatementCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="Statement"/> was not found in the <c>StatementCollection</c>.
		/// </exception>
		public virtual void Remove(Statement item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>StatementCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="StatementCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			Statement[] temp = new Statement[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>StatementCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>StatementCollection</c>.</returns>
		public virtual IStatementCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>StatementCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						Statement[] temp = new Statement[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new Statement[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>StatementCollection</c> to the current <c>StatementCollection</c>.
		/// </summary>
		/// <param name="x">The <c>StatementCollection</c> whose elements should be added to the end of the current <c>StatementCollection</c>.</param>
		/// <returns>The new <see cref="StatementCollection.Count"/> of the <c>StatementCollection</c>.</returns>
		public virtual int AddRange(StatementCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="Statement"/> array to the current <c>StatementCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="Statement"/> array whose elements should be added to the end of the <c>StatementCollection</c>.</param>
		/// <returns>The new <see cref="StatementCollection.Count"/> of the <c>StatementCollection</c>.</returns>
		public virtual int AddRange(Statement[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="StatementCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="StatementCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((Statement[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (Statement)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((Statement)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((Statement)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((Statement)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (Statement)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((Statement)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="StatementCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, IStatementCollectionEnumerator
		{
			#region Implementation (data)
			
			private StatementCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(StatementCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public Statement Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncStatementCollection : StatementCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private StatementCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncStatementCollection(StatementCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(Statement[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(Statement[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override Statement this[int i]
			{
				get
				{
					Statement thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(Statement x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(Statement x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(Statement x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, Statement x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(Statement x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override IStatementCollectionEnumerator GetEnumerator()
			{
				IStatementCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(StatementCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(Statement[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyStatementCollection : StatementCollection
		{
			#region Implementation (data)
			private StatementCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyStatementCollection(StatementCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(Statement[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(Statement[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override Statement this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(Statement x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(Statement x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(Statement x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, Statement x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(Statement x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override IStatementCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(StatementCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(Statement[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion

		#region Text
		public override string Text
		{
			get
			{
				return "Statements";
			}
		}
		#endregion

	}
	#endregion
	#region CommentStmtCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="CommentStmt"/> objects.
	/// </summary>
	[Serializable]
	public class CommentStmtCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		public override GraphTypes GraphType{get{return GraphTypes.CommentStmtCollection;}}
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="CommentStmtCollection"/>.
		/// </summary>
		public interface ICommentStmtCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			CommentStmt Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;

		#region Implementation (data)
		private CommentStmt[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>CommentStmtCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>CommentStmtCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static CommentStmtCollection Synchronized(CommentStmtCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncCommentStmtCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>CommentStmtCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>CommentStmtCollection</c> wrapper that is read-only.
		/// </returns>
		public static CommentStmtCollection ReadOnly(CommentStmtCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyCommentStmtCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>CommentStmtCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public CommentStmtCollection()
		{
			m_array = new CommentStmt[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>CommentStmtCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>CommentStmtCollection</c> is initially capable of storing.
		///	</param>
		public CommentStmtCollection(int capacity)
		{
			m_array = new CommentStmt[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>CommentStmtCollection</c> class
		///		that contains elements copied from the specified <c>CommentStmtCollection</c>.
		/// </summary>
		/// <param name="c">The <c>CommentStmtCollection</c> whose elements are copied to the new collection.</param>
		public CommentStmtCollection(CommentStmtCollection c)
		{
			m_array = new CommentStmt[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>CommentStmtCollection</c> class
		///		that contains elements copied from the specified <see cref="CommentStmt"/> array.
		/// </summary>
		/// <param name="a">The <see cref="CommentStmt"/> array whose elements are copied to the new list.</param>
		public CommentStmtCollection(CommentStmt[] a)
		{
			m_array = new CommentStmt[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected CommentStmtCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>CommentStmtCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>CommentStmtCollection</c> to a one-dimensional
		///		<see cref="CommentStmt"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="CommentStmt"/> array to copy to.</param>
		public virtual void CopyTo(CommentStmt[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>CommentStmtCollection</c> to a one-dimensional
		///		<see cref="CommentStmt"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="CommentStmt"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(CommentStmt[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="CommentStmt"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CommentStmtCollection.Count"/>.</para>
		/// </exception>
		public virtual CommentStmt this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="CommentStmt"/> to the end of the <c>CommentStmtCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="CommentStmt"/> to be added to the end of the <c>CommentStmtCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(CommentStmt item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>CommentStmtCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new CommentStmt[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="CommentStmtCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			CommentStmtCollection newColl = new CommentStmtCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="CommentStmt"/> is in the <c>CommentStmtCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="CommentStmt"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>CommentStmtCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(CommentStmt item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="CommentStmt"/>
		///		in the <c>CommentStmtCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="CommentStmt"/> to locate in the <c>CommentStmtCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>CommentStmtCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(CommentStmt item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>CommentStmtCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="CommentStmt"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CommentStmtCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, CommentStmt item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="CommentStmt"/> from the <c>CommentStmtCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="CommentStmt"/> to remove from the <c>CommentStmtCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="CommentStmt"/> was not found in the <c>CommentStmtCollection</c>.
		/// </exception>
		public virtual void Remove(CommentStmt item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>CommentStmtCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CommentStmtCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			CommentStmt[] temp = new CommentStmt[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>CommentStmtCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>CommentStmtCollection</c>.</returns>
		public virtual ICommentStmtCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>CommentStmtCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						CommentStmt[] temp = new CommentStmt[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new CommentStmt[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>CommentStmtCollection</c> to the current <c>CommentStmtCollection</c>.
		/// </summary>
		/// <param name="x">The <c>CommentStmtCollection</c> whose elements should be added to the end of the current <c>CommentStmtCollection</c>.</param>
		/// <returns>The new <see cref="CommentStmtCollection.Count"/> of the <c>CommentStmtCollection</c>.</returns>
		public virtual int AddRange(CommentStmtCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="CommentStmt"/> array to the current <c>CommentStmtCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="CommentStmt"/> array whose elements should be added to the end of the <c>CommentStmtCollection</c>.</param>
		/// <returns>The new <see cref="CommentStmtCollection.Count"/> of the <c>CommentStmtCollection</c>.</returns>
		public virtual int AddRange(CommentStmt[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CommentStmtCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CommentStmtCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((CommentStmt[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (CommentStmt)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((CommentStmt)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((CommentStmt)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((CommentStmt)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (CommentStmt)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((CommentStmt)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="CommentStmtCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, ICommentStmtCollectionEnumerator
		{
			#region Implementation (data)
			
			private CommentStmtCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(CommentStmtCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public CommentStmt Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncCommentStmtCollection : CommentStmtCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private CommentStmtCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncCommentStmtCollection(CommentStmtCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(CommentStmt[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(CommentStmt[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override CommentStmt this[int i]
			{
				get
				{
					CommentStmt thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(CommentStmt x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(CommentStmt x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(CommentStmt x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, CommentStmt x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(CommentStmt x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override ICommentStmtCollectionEnumerator GetEnumerator()
			{
				ICommentStmtCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(CommentStmtCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(CommentStmt[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyCommentStmtCollection : CommentStmtCollection
		{
			#region Implementation (data)
			private CommentStmtCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyCommentStmtCollection(CommentStmtCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(CommentStmt[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(CommentStmt[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override CommentStmt this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(CommentStmt x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(CommentStmt x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(CommentStmt x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, CommentStmt x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(CommentStmt x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override ICommentStmtCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(CommentStmtCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(CommentStmt[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion

		#region Text
		public override string Text
		{
			get
			{
				return "Comments";
			}
		}
		#endregion

	}
	#endregion
	#region CatchCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="Catch"/> objects.
	/// </summary>
	[Serializable]
	public class CatchCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		public override GraphTypes GraphType{get{return GraphTypes.CatchCollection;}}
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="CatchCollection"/>.
		/// </summary>
		public interface ICatchCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			Catch Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;

		#region Implementation (data)
		private Catch[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>CatchCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>CatchCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static CatchCollection Synchronized(CatchCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncCatchCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>CatchCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>CatchCollection</c> wrapper that is read-only.
		/// </returns>
		public static CatchCollection ReadOnly(CatchCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyCatchCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>CatchCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public CatchCollection()
		{
			m_array = new Catch[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>CatchCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>CatchCollection</c> is initially capable of storing.
		///	</param>
		public CatchCollection(int capacity)
		{
			m_array = new Catch[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>CatchCollection</c> class
		///		that contains elements copied from the specified <c>CatchCollection</c>.
		/// </summary>
		/// <param name="c">The <c>CatchCollection</c> whose elements are copied to the new collection.</param>
		public CatchCollection(CatchCollection c)
		{
			m_array = new Catch[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>CatchCollection</c> class
		///		that contains elements copied from the specified <see cref="Catch"/> array.
		/// </summary>
		/// <param name="a">The <see cref="Catch"/> array whose elements are copied to the new list.</param>
		public CatchCollection(Catch[] a)
		{
			m_array = new Catch[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected CatchCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>CatchCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>CatchCollection</c> to a one-dimensional
		///		<see cref="Catch"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="Catch"/> array to copy to.</param>
		public virtual void CopyTo(Catch[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>CatchCollection</c> to a one-dimensional
		///		<see cref="Catch"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="Catch"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(Catch[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="Catch"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CatchCollection.Count"/>.</para>
		/// </exception>
		public virtual Catch this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="Catch"/> to the end of the <c>CatchCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Catch"/> to be added to the end of the <c>CatchCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(Catch item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>CatchCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new Catch[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="CatchCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			CatchCollection newColl = new CatchCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="Catch"/> is in the <c>CatchCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Catch"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>CatchCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(Catch item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="Catch"/>
		///		in the <c>CatchCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Catch"/> to locate in the <c>CatchCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>CatchCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(Catch item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>CatchCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="Catch"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CatchCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, Catch item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="Catch"/> from the <c>CatchCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Catch"/> to remove from the <c>CatchCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="Catch"/> was not found in the <c>CatchCollection</c>.
		/// </exception>
		public virtual void Remove(Catch item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>CatchCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CatchCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			Catch[] temp = new Catch[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>CatchCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>CatchCollection</c>.</returns>
		public virtual ICatchCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>CatchCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						Catch[] temp = new Catch[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new Catch[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>CatchCollection</c> to the current <c>CatchCollection</c>.
		/// </summary>
		/// <param name="x">The <c>CatchCollection</c> whose elements should be added to the end of the current <c>CatchCollection</c>.</param>
		/// <returns>The new <see cref="CatchCollection.Count"/> of the <c>CatchCollection</c>.</returns>
		public virtual int AddRange(CatchCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="Catch"/> array to the current <c>CatchCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="Catch"/> array whose elements should be added to the end of the <c>CatchCollection</c>.</param>
		/// <returns>The new <see cref="CatchCollection.Count"/> of the <c>CatchCollection</c>.</returns>
		public virtual int AddRange(Catch[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CatchCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CatchCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((Catch[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (Catch)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((Catch)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((Catch)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((Catch)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (Catch)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((Catch)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="CatchCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, ICatchCollectionEnumerator
		{
			#region Implementation (data)
			
			private CatchCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(CatchCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public Catch Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncCatchCollection : CatchCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private CatchCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncCatchCollection(CatchCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(Catch[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(Catch[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override Catch this[int i]
			{
				get
				{
					Catch thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(Catch x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(Catch x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(Catch x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, Catch x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(Catch x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override ICatchCollectionEnumerator GetEnumerator()
			{
				ICatchCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(CatchCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(Catch[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyCatchCollection : CatchCollection
		{
			#region Implementation (data)
			private CatchCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyCatchCollection(CatchCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(Catch[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(Catch[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override Catch this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(Catch x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(Catch x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(Catch x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, Catch x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(Catch x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override ICatchCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(CatchCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(Catch[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion

		#region Text
		public override string Text
		{
			get
			{
				return "Catch Clauses";
			}
		}
		#endregion

	}
	#endregion
	#region ExpressionCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="Expression"/> objects.
	/// </summary>
	[Serializable]
	public class ExpressionCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		public override GraphTypes GraphType{get{return GraphTypes.ExpressionCollection;}}
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="ExpressionCollection"/>.
		/// </summary>
		public interface IExpressionCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			Expression Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;

		#region Implementation (data)
		private Expression[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>ExpressionCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>ExpressionCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static ExpressionCollection Synchronized(ExpressionCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncExpressionCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>ExpressionCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>ExpressionCollection</c> wrapper that is read-only.
		/// </returns>
		public static ExpressionCollection ReadOnly(ExpressionCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyExpressionCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>ExpressionCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public ExpressionCollection()
		{
			m_array = new Expression[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>ExpressionCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>ExpressionCollection</c> is initially capable of storing.
		///	</param>
		public ExpressionCollection(int capacity)
		{
			m_array = new Expression[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>ExpressionCollection</c> class
		///		that contains elements copied from the specified <c>ExpressionCollection</c>.
		/// </summary>
		/// <param name="c">The <c>ExpressionCollection</c> whose elements are copied to the new collection.</param>
		public ExpressionCollection(ExpressionCollection c)
		{
			m_array = new Expression[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>ExpressionCollection</c> class
		///		that contains elements copied from the specified <see cref="Expression"/> array.
		/// </summary>
		/// <param name="a">The <see cref="Expression"/> array whose elements are copied to the new list.</param>
		public ExpressionCollection(Expression[] a)
		{
			m_array = new Expression[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected ExpressionCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>ExpressionCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>ExpressionCollection</c> to a one-dimensional
		///		<see cref="Expression"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="Expression"/> array to copy to.</param>
		public virtual void CopyTo(Expression[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>ExpressionCollection</c> to a one-dimensional
		///		<see cref="Expression"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="Expression"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(Expression[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="Expression"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ExpressionCollection.Count"/>.</para>
		/// </exception>
		public virtual Expression this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="Expression"/> to the end of the <c>ExpressionCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Expression"/> to be added to the end of the <c>ExpressionCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(Expression item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>ExpressionCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new Expression[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="ExpressionCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			ExpressionCollection newColl = new ExpressionCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="Expression"/> is in the <c>ExpressionCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Expression"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>ExpressionCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(Expression item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="Expression"/>
		///		in the <c>ExpressionCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Expression"/> to locate in the <c>ExpressionCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>ExpressionCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(Expression item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>ExpressionCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="Expression"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ExpressionCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, Expression item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="Expression"/> from the <c>ExpressionCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Expression"/> to remove from the <c>ExpressionCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="Expression"/> was not found in the <c>ExpressionCollection</c>.
		/// </exception>
		public virtual void Remove(Expression item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>ExpressionCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ExpressionCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			Expression[] temp = new Expression[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>ExpressionCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>ExpressionCollection</c>.</returns>
		public virtual IExpressionCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>ExpressionCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						Expression[] temp = new Expression[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new Expression[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>ExpressionCollection</c> to the current <c>ExpressionCollection</c>.
		/// </summary>
		/// <param name="x">The <c>ExpressionCollection</c> whose elements should be added to the end of the current <c>ExpressionCollection</c>.</param>
		/// <returns>The new <see cref="ExpressionCollection.Count"/> of the <c>ExpressionCollection</c>.</returns>
		public virtual int AddRange(ExpressionCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="Expression"/> array to the current <c>ExpressionCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="Expression"/> array whose elements should be added to the end of the <c>ExpressionCollection</c>.</param>
		/// <returns>The new <see cref="ExpressionCollection.Count"/> of the <c>ExpressionCollection</c>.</returns>
		public virtual int AddRange(Expression[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ExpressionCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="ExpressionCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((Expression[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (Expression)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((Expression)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((Expression)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((Expression)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (Expression)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((Expression)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="ExpressionCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, IExpressionCollectionEnumerator
		{
			#region Implementation (data)
			
			private ExpressionCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(ExpressionCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public Expression Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncExpressionCollection : ExpressionCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private ExpressionCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncExpressionCollection(ExpressionCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(Expression[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(Expression[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override Expression this[int i]
			{
				get
				{
					Expression thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(Expression x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(Expression x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(Expression x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, Expression x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(Expression x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override IExpressionCollectionEnumerator GetEnumerator()
			{
				IExpressionCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(ExpressionCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(Expression[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyExpressionCollection : ExpressionCollection
		{
			#region Implementation (data)
			private ExpressionCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyExpressionCollection(ExpressionCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(Expression[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(Expression[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override Expression this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(Expression x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(Expression x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(Expression x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, Expression x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(Expression x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override IExpressionCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(ExpressionCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(Expression[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion

		#region Text
		public override string Text
		{
			get
			{
				return "Expressions";
			}
		}
		#endregion

	}
	#endregion
	#region CustomAttributeCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="CustomAttribute"/> objects.
	/// </summary>
	[Serializable]
	public class CustomAttributeCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		public override GraphTypes GraphType{get{return GraphTypes.CustomAttributeCollection;}}
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="CustomAttributeCollection"/>.
		/// </summary>
		public interface ICustomAttributeCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			CustomAttribute Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;

		#region Implementation (data)
		private CustomAttribute[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>CustomAttributeCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>CustomAttributeCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static CustomAttributeCollection Synchronized(CustomAttributeCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncCustomAttributeCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>CustomAttributeCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>CustomAttributeCollection</c> wrapper that is read-only.
		/// </returns>
		public static CustomAttributeCollection ReadOnly(CustomAttributeCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyCustomAttributeCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>CustomAttributeCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public CustomAttributeCollection()
		{
			m_array = new CustomAttribute[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>CustomAttributeCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>CustomAttributeCollection</c> is initially capable of storing.
		///	</param>
		public CustomAttributeCollection(int capacity)
		{
			m_array = new CustomAttribute[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>CustomAttributeCollection</c> class
		///		that contains elements copied from the specified <c>CustomAttributeCollection</c>.
		/// </summary>
		/// <param name="c">The <c>CustomAttributeCollection</c> whose elements are copied to the new collection.</param>
		public CustomAttributeCollection(CustomAttributeCollection c)
		{
			m_array = new CustomAttribute[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>CustomAttributeCollection</c> class
		///		that contains elements copied from the specified <see cref="CustomAttribute"/> array.
		/// </summary>
		/// <param name="a">The <see cref="CustomAttribute"/> array whose elements are copied to the new list.</param>
		public CustomAttributeCollection(CustomAttribute[] a)
		{
			m_array = new CustomAttribute[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected CustomAttributeCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>CustomAttributeCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>CustomAttributeCollection</c> to a one-dimensional
		///		<see cref="CustomAttribute"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="CustomAttribute"/> array to copy to.</param>
		public virtual void CopyTo(CustomAttribute[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>CustomAttributeCollection</c> to a one-dimensional
		///		<see cref="CustomAttribute"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="CustomAttribute"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(CustomAttribute[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="CustomAttribute"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CustomAttributeCollection.Count"/>.</para>
		/// </exception>
		public virtual CustomAttribute this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="CustomAttribute"/> to the end of the <c>CustomAttributeCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="CustomAttribute"/> to be added to the end of the <c>CustomAttributeCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(CustomAttribute item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>CustomAttributeCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new CustomAttribute[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="CustomAttributeCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			CustomAttributeCollection newColl = new CustomAttributeCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="CustomAttribute"/> is in the <c>CustomAttributeCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="CustomAttribute"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>CustomAttributeCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(CustomAttribute item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="CustomAttribute"/>
		///		in the <c>CustomAttributeCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="CustomAttribute"/> to locate in the <c>CustomAttributeCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>CustomAttributeCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(CustomAttribute item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>CustomAttributeCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="CustomAttribute"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CustomAttributeCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, CustomAttribute item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="CustomAttribute"/> from the <c>CustomAttributeCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="CustomAttribute"/> to remove from the <c>CustomAttributeCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="CustomAttribute"/> was not found in the <c>CustomAttributeCollection</c>.
		/// </exception>
		public virtual void Remove(CustomAttribute item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>CustomAttributeCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CustomAttributeCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			CustomAttribute[] temp = new CustomAttribute[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>CustomAttributeCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>CustomAttributeCollection</c>.</returns>
		public virtual ICustomAttributeCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>CustomAttributeCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						CustomAttribute[] temp = new CustomAttribute[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new CustomAttribute[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>CustomAttributeCollection</c> to the current <c>CustomAttributeCollection</c>.
		/// </summary>
		/// <param name="x">The <c>CustomAttributeCollection</c> whose elements should be added to the end of the current <c>CustomAttributeCollection</c>.</param>
		/// <returns>The new <see cref="CustomAttributeCollection.Count"/> of the <c>CustomAttributeCollection</c>.</returns>
		public virtual int AddRange(CustomAttributeCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="CustomAttribute"/> array to the current <c>CustomAttributeCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="CustomAttribute"/> array whose elements should be added to the end of the <c>CustomAttributeCollection</c>.</param>
		/// <returns>The new <see cref="CustomAttributeCollection.Count"/> of the <c>CustomAttributeCollection</c>.</returns>
		public virtual int AddRange(CustomAttribute[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CustomAttributeCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CustomAttributeCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((CustomAttribute[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (CustomAttribute)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((CustomAttribute)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((CustomAttribute)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((CustomAttribute)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (CustomAttribute)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((CustomAttribute)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="CustomAttributeCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, ICustomAttributeCollectionEnumerator
		{
			#region Implementation (data)
			
			private CustomAttributeCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(CustomAttributeCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public CustomAttribute Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncCustomAttributeCollection : CustomAttributeCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private CustomAttributeCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncCustomAttributeCollection(CustomAttributeCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(CustomAttribute[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(CustomAttribute[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override CustomAttribute this[int i]
			{
				get
				{
					CustomAttribute thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(CustomAttribute x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(CustomAttribute x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(CustomAttribute x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, CustomAttribute x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(CustomAttribute x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override ICustomAttributeCollectionEnumerator GetEnumerator()
			{
				ICustomAttributeCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(CustomAttributeCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(CustomAttribute[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyCustomAttributeCollection : CustomAttributeCollection
		{
			#region Implementation (data)
			private CustomAttributeCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyCustomAttributeCollection(CustomAttributeCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(CustomAttribute[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(CustomAttribute[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override CustomAttribute this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(CustomAttribute x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(CustomAttribute x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(CustomAttribute x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, CustomAttribute x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(CustomAttribute x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override ICustomAttributeCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(CustomAttributeCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(CustomAttribute[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion

		#region Text
		public override string Text
		{
			get
			{
				return "CustomAttributes";
			}
		}
		#endregion

	}
	#endregion
	#region CaseCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="Case"/> objects.
	/// </summary>
	[Serializable]
	public class CaseCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		public override GraphTypes GraphType{get{return GraphTypes.CaseCollection;}}
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="CaseCollection"/>.
		/// </summary>
		public interface ICaseCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			Case Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;

		#region Implementation (data)
		private Case[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>CaseCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>CaseCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static CaseCollection Synchronized(CaseCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncCaseCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>CaseCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>CaseCollection</c> wrapper that is read-only.
		/// </returns>
		public static CaseCollection ReadOnly(CaseCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyCaseCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>CaseCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public CaseCollection()
		{
			m_array = new Case[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>CaseCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>CaseCollection</c> is initially capable of storing.
		///	</param>
		public CaseCollection(int capacity)
		{
			m_array = new Case[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>CaseCollection</c> class
		///		that contains elements copied from the specified <c>CaseCollection</c>.
		/// </summary>
		/// <param name="c">The <c>CaseCollection</c> whose elements are copied to the new collection.</param>
		public CaseCollection(CaseCollection c)
		{
			m_array = new Case[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>CaseCollection</c> class
		///		that contains elements copied from the specified <see cref="Case"/> array.
		/// </summary>
		/// <param name="a">The <see cref="Case"/> array whose elements are copied to the new list.</param>
		public CaseCollection(Case[] a)
		{
			m_array = new Case[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected CaseCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>CaseCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>CaseCollection</c> to a one-dimensional
		///		<see cref="Case"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="Case"/> array to copy to.</param>
		public virtual void CopyTo(Case[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>CaseCollection</c> to a one-dimensional
		///		<see cref="Case"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="Case"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(Case[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="Case"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CaseCollection.Count"/>.</para>
		/// </exception>
		public virtual Case this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="Case"/> to the end of the <c>CaseCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Case"/> to be added to the end of the <c>CaseCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(Case item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>CaseCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new Case[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="CaseCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			CaseCollection newColl = new CaseCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="Case"/> is in the <c>CaseCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Case"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>CaseCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(Case item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="Case"/>
		///		in the <c>CaseCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Case"/> to locate in the <c>CaseCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>CaseCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(Case item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>CaseCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="Case"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CaseCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, Case item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="Case"/> from the <c>CaseCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Case"/> to remove from the <c>CaseCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="Case"/> was not found in the <c>CaseCollection</c>.
		/// </exception>
		public virtual void Remove(Case item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>CaseCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CaseCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			Case[] temp = new Case[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>CaseCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>CaseCollection</c>.</returns>
		public virtual ICaseCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>CaseCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						Case[] temp = new Case[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new Case[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>CaseCollection</c> to the current <c>CaseCollection</c>.
		/// </summary>
		/// <param name="x">The <c>CaseCollection</c> whose elements should be added to the end of the current <c>CaseCollection</c>.</param>
		/// <returns>The new <see cref="CaseCollection.Count"/> of the <c>CaseCollection</c>.</returns>
		public virtual int AddRange(CaseCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="Case"/> array to the current <c>CaseCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="Case"/> array whose elements should be added to the end of the <c>CaseCollection</c>.</param>
		/// <returns>The new <see cref="CaseCollection.Count"/> of the <c>CaseCollection</c>.</returns>
		public virtual int AddRange(Case[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CaseCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="CaseCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((Case[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (Case)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((Case)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((Case)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((Case)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (Case)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((Case)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="CaseCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, ICaseCollectionEnumerator
		{
			#region Implementation (data)
			
			private CaseCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(CaseCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public Case Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncCaseCollection : CaseCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private CaseCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncCaseCollection(CaseCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(Case[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(Case[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override Case this[int i]
			{
				get
				{
					Case thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(Case x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(Case x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(Case x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, Case x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(Case x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override ICaseCollectionEnumerator GetEnumerator()
			{
				ICaseCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(CaseCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(Case[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyCaseCollection : CaseCollection
		{
			#region Implementation (data)
			private CaseCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyCaseCollection(CaseCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(Case[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(Case[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override Case this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(Case x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(Case x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(Case x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, Case x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(Case x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override ICaseCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(CaseCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(Case[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion

		#region Text
		public override string Text
		{
			get
			{
				return "Cases";
			}
		}
		#endregion
	}
	#endregion

	#region AssemblyReferenceCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="AssemblyReference"/> objects.
	/// </summary>
	[Serializable]
	public class AssemblyReferenceCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="AssemblyReferenceCollection"/>.
		/// </summary>
		public interface IAssemblyReferenceCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			AssemblyReference Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;
		public override GraphTypes GraphType{get{return GraphTypes.AssemblyReferenceCollection;}}

		#region Implementation (data)
		private AssemblyReference[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>AssemblyReferenceCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>AssemblyReferenceCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static AssemblyReferenceCollection Synchronized(AssemblyReferenceCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncAssemblyReferenceCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>AssemblyReferenceCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>AssemblyReferenceCollection</c> wrapper that is read-only.
		/// </returns>
		public static AssemblyReferenceCollection ReadOnly(AssemblyReferenceCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyAssemblyReferenceCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>AssemblyReferenceCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public AssemblyReferenceCollection()
		{
			m_array = new AssemblyReference[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>AssemblyReferenceCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>AssemblyReferenceCollection</c> is initially capable of storing.
		///	</param>
		public AssemblyReferenceCollection(int capacity)
		{
			m_array = new AssemblyReference[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>AssemblyReferenceCollection</c> class
		///		that contains elements copied from the specified <c>AssemblyReferenceCollection</c>.
		/// </summary>
		/// <param name="c">The <c>AssemblyReferenceCollection</c> whose elements are copied to the new collection.</param>
		public AssemblyReferenceCollection(AssemblyReferenceCollection c)
		{
			m_array = new AssemblyReference[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>AssemblyReferenceCollection</c> class
		///		that contains elements copied from the specified <see cref="AssemblyReference"/> array.
		/// </summary>
		/// <param name="a">The <see cref="AssemblyReference"/> array whose elements are copied to the new list.</param>
		public AssemblyReferenceCollection(AssemblyReference[] a)
		{
			m_array = new AssemblyReference[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected AssemblyReferenceCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>AssemblyReferenceCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>AssemblyReferenceCollection</c> to a one-dimensional
		///		<see cref="AssemblyReference"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="AssemblyReference"/> array to copy to.</param>
		public virtual void CopyTo(AssemblyReference[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>AssemblyReferenceCollection</c> to a one-dimensional
		///		<see cref="AssemblyReference"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="AssemblyReference"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(AssemblyReference[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="AssemblyReference"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="AssemblyReferenceCollection.Count"/>.</para>
		/// </exception>
		public virtual AssemblyReference this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="AssemblyReference"/> to the end of the <c>AssemblyReferenceCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="AssemblyReference"/> to be added to the end of the <c>AssemblyReferenceCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(AssemblyReference item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>AssemblyReferenceCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new AssemblyReference[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="AssemblyReferenceCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			AssemblyReferenceCollection newColl = new AssemblyReferenceCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="AssemblyReference"/> is in the <c>AssemblyReferenceCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="AssemblyReference"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>AssemblyReferenceCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(AssemblyReference item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="AssemblyReference"/>
		///		in the <c>AssemblyReferenceCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="AssemblyReference"/> to locate in the <c>AssemblyReferenceCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>AssemblyReferenceCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(AssemblyReference item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>AssemblyReferenceCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="AssemblyReference"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="AssemblyReferenceCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, AssemblyReference item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="AssemblyReference"/> from the <c>AssemblyReferenceCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="AssemblyReference"/> to remove from the <c>AssemblyReferenceCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="AssemblyReference"/> was not found in the <c>AssemblyReferenceCollection</c>.
		/// </exception>
		public virtual void Remove(AssemblyReference item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>AssemblyReferenceCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="AssemblyReferenceCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			AssemblyReference[] temp = new AssemblyReference[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>AssemblyReferenceCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>AssemblyReferenceCollection</c>.</returns>
		public virtual IAssemblyReferenceCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>AssemblyReferenceCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						AssemblyReference[] temp = new AssemblyReference[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new AssemblyReference[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>AssemblyReferenceCollection</c> to the current <c>AssemblyReferenceCollection</c>.
		/// </summary>
		/// <param name="x">The <c>AssemblyReferenceCollection</c> whose elements should be added to the end of the current <c>AssemblyReferenceCollection</c>.</param>
		/// <returns>The new <see cref="AssemblyReferenceCollection.Count"/> of the <c>AssemblyReferenceCollection</c>.</returns>
		public virtual int AddRange(AssemblyReferenceCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="AssemblyReference"/> array to the current <c>AssemblyReferenceCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="AssemblyReference"/> array whose elements should be added to the end of the <c>AssemblyReferenceCollection</c>.</param>
		/// <returns>The new <see cref="AssemblyReferenceCollection.Count"/> of the <c>AssemblyReferenceCollection</c>.</returns>
		public virtual int AddRange(AssemblyReference[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="AssemblyReferenceCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="AssemblyReferenceCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((AssemblyReference[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (AssemblyReference)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((AssemblyReference)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((AssemblyReference)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((AssemblyReference)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (AssemblyReference)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((AssemblyReference)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="AssemblyReferenceCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, IAssemblyReferenceCollectionEnumerator
		{
			#region Implementation (data)
			
			private AssemblyReferenceCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(AssemblyReferenceCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public AssemblyReference Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncAssemblyReferenceCollection : AssemblyReferenceCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private AssemblyReferenceCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncAssemblyReferenceCollection(AssemblyReferenceCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(AssemblyReference[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(AssemblyReference[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override AssemblyReference this[int i]
			{
				get
				{
					AssemblyReference thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(AssemblyReference x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(AssemblyReference x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(AssemblyReference x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, AssemblyReference x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(AssemblyReference x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override IAssemblyReferenceCollectionEnumerator GetEnumerator()
			{
				IAssemblyReferenceCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(AssemblyReferenceCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(AssemblyReference[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyAssemblyReferenceCollection : AssemblyReferenceCollection
		{
			#region Implementation (data)
			private AssemblyReferenceCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyAssemblyReferenceCollection(AssemblyReferenceCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(AssemblyReference[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(AssemblyReference[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override AssemblyReference this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(AssemblyReference x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(AssemblyReference x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(AssemblyReference x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, AssemblyReference x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(AssemblyReference x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override IAssemblyReferenceCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(AssemblyReferenceCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(AssemblyReference[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion

		#region Text
		public override string Text
		{
			get
			{
				return "Assembly References";
			}
		}
		#endregion

	}
	#endregion
	#region RankSpecifierCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="RankSpecifier"/> objects.
	/// </summary>
	[Serializable]
	public class RankSpecifierCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="RankSpecifierCollection"/>.
		/// </summary>
		public interface IRankCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			RankSpecifier Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;
		public override GraphTypes GraphType{get{return GraphTypes.RankSpecifierCollection;}}

		#region Implementation (data)
		private RankSpecifier[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>RankSpecifierCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>RankSpecifierCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static RankSpecifierCollection Synchronized(RankSpecifierCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncRankCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>RankSpecifierCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>RankSpecifierCollection</c> wrapper that is read-only.
		/// </returns>
		public static RankSpecifierCollection ReadOnly(RankSpecifierCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyRankCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>RankSpecifierCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public RankSpecifierCollection()
		{
			m_array = new RankSpecifier[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>RankSpecifierCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>RankSpecifierCollection</c> is initially capable of storing.
		///	</param>
		public RankSpecifierCollection(int capacity)
		{
			m_array = new RankSpecifier[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>RankSpecifierCollection</c> class
		///		that contains elements copied from the specified <c>RankSpecifierCollection</c>.
		/// </summary>
		/// <param name="c">The <c>RankSpecifierCollection</c> whose elements are copied to the new collection.</param>
		public RankSpecifierCollection(RankSpecifierCollection c)
		{
			m_array = new RankSpecifier[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>RankSpecifierCollection</c> class
		///		that contains elements copied from the specified <see cref="RankSpecifier"/> array.
		/// </summary>
		/// <param name="a">The <see cref="RankSpecifier"/> array whose elements are copied to the new list.</param>
		public RankSpecifierCollection(RankSpecifier[] a)
		{
			m_array = new RankSpecifier[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected RankSpecifierCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>RankSpecifierCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>RankSpecifierCollection</c> to a one-dimensional
		///		<see cref="RankSpecifier"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="RankSpecifier"/> array to copy to.</param>
		public virtual void CopyTo(RankSpecifier[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>RankSpecifierCollection</c> to a one-dimensional
		///		<see cref="RankSpecifier"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="RankSpecifier"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(RankSpecifier[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="RankSpecifier"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="RankSpecifierCollection.Count"/>.</para>
		/// </exception>
		public virtual RankSpecifier this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="RankSpecifier"/> to the end of the <c>RankSpecifierCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="RankSpecifier"/> to be added to the end of the <c>RankSpecifierCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(RankSpecifier item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>RankSpecifierCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new RankSpecifier[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="RankSpecifierCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			RankSpecifierCollection newColl = new RankSpecifierCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="RankSpecifier"/> is in the <c>RankSpecifierCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="RankSpecifier"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>RankSpecifierCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(RankSpecifier item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="RankSpecifier"/>
		///		in the <c>RankSpecifierCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="RankSpecifier"/> to locate in the <c>RankSpecifierCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>RankSpecifierCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(RankSpecifier item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>RankSpecifierCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="RankSpecifier"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="RankSpecifierCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, RankSpecifier item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="RankSpecifier"/> from the <c>RankSpecifierCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="RankSpecifier"/> to remove from the <c>RankSpecifierCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="RankSpecifier"/> was not found in the <c>RankSpecifierCollection</c>.
		/// </exception>
		public virtual void Remove(RankSpecifier item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>RankSpecifierCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="RankSpecifierCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			RankSpecifier[] temp = new RankSpecifier[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>RankSpecifierCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>RankSpecifierCollection</c>.</returns>
		public virtual IRankCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>RankSpecifierCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						RankSpecifier[] temp = new RankSpecifier[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new RankSpecifier[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>RankSpecifierCollection</c> to the current <c>RankSpecifierCollection</c>.
		/// </summary>
		/// <param name="x">The <c>RankSpecifierCollection</c> whose elements should be added to the end of the current <c>RankSpecifierCollection</c>.</param>
		/// <returns>The new <see cref="RankSpecifierCollection.Count"/> of the <c>RankSpecifierCollection</c>.</returns>
		public virtual int AddRange(RankSpecifierCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="RankSpecifier"/> array to the current <c>RankSpecifierCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="RankSpecifier"/> array whose elements should be added to the end of the <c>RankSpecifierCollection</c>.</param>
		/// <returns>The new <see cref="RankSpecifierCollection.Count"/> of the <c>RankSpecifierCollection</c>.</returns>
		public virtual int AddRange(RankSpecifier[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="RankSpecifierCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="RankSpecifierCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((RankSpecifier[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (RankSpecifier)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((RankSpecifier)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((RankSpecifier)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((RankSpecifier)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (RankSpecifier)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((RankSpecifier)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="RankSpecifierCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, IRankCollectionEnumerator
		{
			#region Implementation (data)
			
			private RankSpecifierCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(RankSpecifierCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public RankSpecifier Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncRankCollection : RankSpecifierCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private RankSpecifierCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncRankCollection(RankSpecifierCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(RankSpecifier[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(RankSpecifier[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override RankSpecifier this[int i]
			{
				get
				{
					RankSpecifier thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(RankSpecifier x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(RankSpecifier x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(RankSpecifier x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, RankSpecifier x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(RankSpecifier x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override IRankCollectionEnumerator GetEnumerator()
			{
				IRankCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(RankSpecifierCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(RankSpecifier[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyRankCollection : RankSpecifierCollection
		{
			#region Implementation (data)
			private RankSpecifierCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyRankCollection(RankSpecifierCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(RankSpecifier[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(RankSpecifier[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override RankSpecifier this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(RankSpecifier x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(RankSpecifier x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(RankSpecifier x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, RankSpecifier x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(RankSpecifier x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override IRankCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(RankSpecifierCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(RankSpecifier[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion

		#region Text
		public override string Text
		{
			get
			{
				return "Ranks";
			}
		}
		#endregion

	}
	#endregion
	
	#region DeclaratorCollection  
	/// <summary>
	///		A strongly-typed collection of <see cref="Declarator"/> objects.
	/// </summary>
	[Serializable]
	public class DeclaratorCollection : CSharpGraph, ICollection, IList, IEnumerable, ICloneable
	{
		#region Interfaces
		/// <summary>
		///		Supports type-safe iteration over a <see cref="DeclaratorCollection"/>.
		/// </summary>
		public interface IDeclaratorCollectionEnumerator
		{
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			Declarator Current {get;}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			bool MoveNext();

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			void Reset();
		}
		#endregion

		private const int DEFAULT_CAPACITY = 16;
		public override GraphTypes GraphType{get{return GraphTypes.Declarator;}}


		#region Implementation (data)
		private Declarator[] m_array;
		private int m_count = 0;
		[NonSerialized]
		private int m_version = 0;
		#endregion
	
		#region Static Wrappers
		/// <summary>
		///		Creates a synchronized (thread-safe) wrapper for a 
		///     <c>DeclaratorCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>DeclaratorCollection</c> wrapper that is synchronized (thread-safe).
		/// </returns>
		public static DeclaratorCollection Synchronized(DeclaratorCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new SyncDeclaratorCollection(list);
		}
		
		/// <summary>
		///		Creates a read-only wrapper for a 
		///     <c>DeclaratorCollection</c> instance.
		/// </summary>
		/// <returns>
		///     An <c>DeclaratorCollection</c> wrapper that is read-only.
		/// </returns>
		public static DeclaratorCollection ReadOnly(DeclaratorCollection list)
		{
			if(list==null)
				throw new ArgumentNullException("list");
			return new ReadOnlyDeclaratorCollection(list);
		}
		#endregion

		#region Construction
		/// <summary>
		///		Initializes a new instance of the <c>DeclaratorCollection</c> class
		///		that is empty and has the default initial capacity.
		/// </summary>
		public DeclaratorCollection()
		{
			m_array = new Declarator[DEFAULT_CAPACITY];
		}
		
		/// <summary>
		///		Initializes a new instance of the <c>DeclaratorCollection</c> class
		///		that has the specified initial capacity.
		/// </summary>
		/// <param name="capacity">
		///		The number of elements that the new <c>DeclaratorCollection</c> is initially capable of storing.
		///	</param>
		public DeclaratorCollection(int capacity)
		{
			m_array = new Declarator[capacity];
		}

		/// <summary>
		///		Initializes a new instance of the <c>DeclaratorCollection</c> class
		///		that contains elements copied from the specified <c>DeclaratorCollection</c>.
		/// </summary>
		/// <param name="c">The <c>DeclaratorCollection</c> whose elements are copied to the new collection.</param>
		public DeclaratorCollection(DeclaratorCollection c)
		{
			m_array = new Declarator[c.Count];
			AddRange(c);
		}

		/// <summary>
		///		Initializes a new instance of the <c>DeclaratorCollection</c> class
		///		that contains elements copied from the specified <see cref="Declarator"/> array.
		/// </summary>
		/// <param name="a">The <see cref="Declarator"/> array whose elements are copied to the new list.</param>
		public DeclaratorCollection(Declarator[] a)
		{
			m_array = new Declarator[a.Length];
			AddRange(a);
		}
		
		protected enum Tag 
		{
			Default
		}

		protected DeclaratorCollection(Tag t)
		{
			m_array = null;
		}
		#endregion
		
		#region Operations (type-safe ICollection)
		/// <summary>
		///		Gets the number of elements actually contained in the <c>DeclaratorCollection</c>.
		/// </summary>
		public virtual int Count
		{
			get { return m_count; }
		}

		/// <summary>
		///		Copies the entire <c>DeclaratorCollection</c> to a one-dimensional
		///		<see cref="Declarator"/> array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="Declarator"/> array to copy to.</param>
		public virtual void CopyTo(Declarator[] array)
		{
			this.CopyTo(array, 0);
		}

		/// <summary>
		///		Copies the entire <c>DeclaratorCollection</c> to a one-dimensional
		///		<see cref="Declarator"/> array, starting at the specified index of the target array.
		/// </summary>
		/// <param name="array">The one-dimensional <see cref="Declarator"/> array to copy to.</param>
		/// <param name="start">The zero-based index in <paramref name="array"/> at which copying begins.</param>
		public virtual void CopyTo(Declarator[] array, int start)
		{
			if (m_count > array.GetUpperBound(0) + 1 - start)
				throw new System.ArgumentException("Destination array was not long enough.");
			
			Array.Copy(m_array, 0, array, start, m_count); 
		}

		/// <summary>
		///		Gets a value indicating whether access to the collection is synchronized (thread-safe).
		/// </summary>
		/// <returns>true if access to the ICollection is synchronized (thread-safe); otherwise, false.</returns>
		public virtual bool IsSynchronized
		{
			get { return m_array.IsSynchronized; }
		}

		/// <summary>
		///		Gets an object that can be used to synchronize access to the collection.
		/// </summary>
		public virtual object SyncRoot
		{
			get { return m_array.SyncRoot; }
		}
		#endregion
		
		#region Operations (type-safe IList)
		/// <summary>
		///		Gets or sets the <see cref="Declarator"/> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index of the element to get or set.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="DeclaratorCollection.Count"/>.</para>
		/// </exception>
		public virtual Declarator this[int index]
		{
			get
			{
				ValidateIndex(index); // throws
				return m_array[index]; 
			}
			set
			{
				ValidateIndex(index); // throws
				++m_version; 
				m_array[index] = value; 
			}
		}

		/// <summary>
		///		Adds a <see cref="Declarator"/> to the end of the <c>DeclaratorCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Declarator"/> to be added to the end of the <c>DeclaratorCollection</c>.</param>
		/// <returns>The index at which the value has been added.</returns>
		public virtual int Add(Declarator item)
		{
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			m_array[m_count] = item;
			m_version++;

			return m_count++;
		}
		
		/// <summary>
		///		Removes all elements from the <c>DeclaratorCollection</c>.
		/// </summary>
		public virtual void Clear()
		{
			++m_version;
			m_array = new Declarator[DEFAULT_CAPACITY];
			m_count = 0;
		}
		
		/// <summary>
		///		Creates a shallow copy of the <see cref="DeclaratorCollection"/>.
		/// </summary>
		public virtual object Clone()
		{
			DeclaratorCollection newColl = new DeclaratorCollection(m_count);
			Array.Copy(m_array, 0, newColl.m_array, 0, m_count);
			newColl.m_count = m_count;
			newColl.m_version = m_version;

			return newColl;
		}

		/// <summary>
		///		Determines whether a given <see cref="Declarator"/> is in the <c>DeclaratorCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Declarator"/> to check for.</param>
		/// <returns><c>true</c> if <paramref name="item"/> is found in the <c>DeclaratorCollection</c>; otherwise, <c>false</c>.</returns>
		public virtual bool Contains(Declarator item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return true;
			return false;
		}

		/// <summary>
		///		Returns the zero-based index of the first occurrence of a <see cref="Declarator"/>
		///		in the <c>DeclaratorCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Declarator"/> to locate in the <c>DeclaratorCollection</c>.</param>
		/// <returns>
		///		The zero-based index of the first occurrence of <paramref name="item"/> 
		///		in the entire <c>DeclaratorCollection</c>, if found; otherwise, -1.
		///	</returns>
		public virtual int IndexOf(Declarator item)
		{
			for (int i=0; i != m_count; ++i)
				if (m_array[i].Equals(item))
					return i;
			return -1;
		}

		/// <summary>
		///		Inserts an element into the <c>DeclaratorCollection</c> at the specified index.
		/// </summary>
		/// <param name="index">The zero-based index at which <paramref name="item"/> should be inserted.</param>
		/// <param name="item">The <see cref="Declarator"/> to insert.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="DeclaratorCollection.Count"/>.</para>
		/// </exception>
		public virtual void Insert(int index, Declarator item)
		{
			ValidateIndex(index, true); // throws
			
			if (m_count == m_array.Length)
				EnsureCapacity(m_count + 1);

			if (index < m_count)
			{
				Array.Copy(m_array, index, m_array, index + 1, m_count - index);
			}

			m_array[index] = item;
			m_count++;
			m_version++;
		}

		/// <summary>
		///		Removes the first occurrence of a specific <see cref="Declarator"/> from the <c>DeclaratorCollection</c>.
		/// </summary>
		/// <param name="item">The <see cref="Declarator"/> to remove from the <c>DeclaratorCollection</c>.</param>
		/// <exception cref="ArgumentException">
		///		The specified <see cref="Declarator"/> was not found in the <c>DeclaratorCollection</c>.
		/// </exception>
		public virtual void Remove(Declarator item)
		{		   
			int i = IndexOf(item);
			if (i < 0)
				throw new System.ArgumentException("Cannot remove the specified item because it was not found in the specified Collection.");
			
			++m_version;
			RemoveAt(i);
		}

		/// <summary>
		///		Removes the element at the specified index of the <c>DeclaratorCollection</c>.
		/// </summary>
		/// <param name="index">The zero-based index of the element to remove.</param>
		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="DeclaratorCollection.Count"/>.</para>
		/// </exception>
		public virtual void RemoveAt(int index)
		{
			ValidateIndex(index); // throws
			
			m_count--;

			if (index < m_count)
			{
				Array.Copy(m_array, index + 1, m_array, index, m_count - index);
			}
			
			// We can't set the deleted entry equal to null, because it might be a value type.
			// Instead, we'll create an empty single-element array of the right type and copy it 
			// over the entry we want to erase.
			Declarator[] temp = new Declarator[1];
			Array.Copy(temp, 0, m_array, m_count, 1);
			m_version++;
		}

		/// <summary>
		///		Gets a value indicating whether the collection has a fixed size.
		/// </summary>
		/// <value>true if the collection has a fixed size; otherwise, false. The default is false</value>
		public virtual bool IsFixedSize
		{
			get { return false; }
		}

		/// <summary>
		///		gets a value indicating whether the <B>IList</B> is read-only.
		/// </summary>
		/// <value>true if the collection is read-only; otherwise, false. The default is false</value>
		public virtual bool IsReadOnly
		{
			get { return false; }
		}
		#endregion

		#region Operations (type-safe IEnumerable)
		
		/// <summary>
		///		Returns an enumerator that can iterate through the <c>DeclaratorCollection</c>.
		/// </summary>
		/// <returns>An <see cref="Enumerator"/> for the entire <c>DeclaratorCollection</c>.</returns>
		public virtual IDeclaratorCollectionEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}
		#endregion

		#region Public helpers (just to mimic some nice features of ArrayList)
		
		/// <summary>
		///		Gets or sets the number of elements the <c>DeclaratorCollection</c> can contain.
		/// </summary>
		public virtual int Capacity
		{
			get { return m_array.Length; }
			
			set
			{
				if (value < m_count)
					value = m_count;

				if (value != m_array.Length)
				{
					if (value > 0)
					{
						Declarator[] temp = new Declarator[value];
						Array.Copy(m_array, temp, m_count);
						m_array = temp;
					}
					else
					{
						m_array = new Declarator[DEFAULT_CAPACITY];
					}
				}
			}
		}

		/// <summary>
		///		Adds the elements of another <c>DeclaratorCollection</c> to the current <c>DeclaratorCollection</c>.
		/// </summary>
		/// <param name="x">The <c>DeclaratorCollection</c> whose elements should be added to the end of the current <c>DeclaratorCollection</c>.</param>
		/// <returns>The new <see cref="DeclaratorCollection.Count"/> of the <c>DeclaratorCollection</c>.</returns>
		public virtual int AddRange(DeclaratorCollection x)
		{
			if (m_count + x.Count >= m_array.Length)
				EnsureCapacity(m_count + x.Count);
			
			Array.Copy(x.m_array, 0, m_array, m_count, x.Count);
			m_count += x.Count;
			m_version++;

			return m_count;
		}

		/// <summary>
		///		Adds the elements of a <see cref="Declarator"/> array to the current <c>DeclaratorCollection</c>.
		/// </summary>
		/// <param name="x">The <see cref="Declarator"/> array whose elements should be added to the end of the <c>DeclaratorCollection</c>.</param>
		/// <returns>The new <see cref="DeclaratorCollection.Count"/> of the <c>DeclaratorCollection</c>.</returns>
		public virtual int AddRange(Declarator[] x)
		{
			if (m_count + x.Length >= m_array.Length)
				EnsureCapacity(m_count + x.Length);

			Array.Copy(x, 0, m_array, m_count, x.Length);
			m_count += x.Length;
			m_version++;

			return m_count;
		}
		
		/// <summary>
		///		Sets the capacity to the actual number of elements.
		/// </summary>
		public virtual void TrimToSize()
		{
			this.Capacity = m_count;
		}

		#endregion

		#region Implementation (helpers)

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="DeclaratorCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i)
		{
			ValidateIndex(i, false);
		}

		/// <exception cref="ArgumentOutOfRangeException">
		///		<para><paramref name="index"/> is less than zero</para>
		///		<para>-or-</para>
		///		<para><paramref name="index"/> is equal to or greater than <see cref="DeclaratorCollection.Count"/>.</para>
		/// </exception>
		private void ValidateIndex(int i, bool allowEqualEnd)
		{
			int max = (allowEqualEnd)?(m_count):(m_count-1);
			if (i < 0 || i > max)
				throw new System.ArgumentOutOfRangeException("Index was out of range.  Must be non-negative and less than the size of the collection.", (object)i, "Specified argument was out of the range of valid values.");
		}

		private void EnsureCapacity(int min)
		{
			int newCapacity = ((m_array.Length == 0) ? DEFAULT_CAPACITY : m_array.Length * 2);
			if (newCapacity < min)
				newCapacity = min;

			this.Capacity = newCapacity;
		}

		#endregion
		
		#region Implementation (ICollection)

		void ICollection.CopyTo(Array array, int start)
		{
			this.CopyTo((Declarator[])array, start);
		}

		#endregion

		#region Implementation (IList)

		object IList.this[int i]
		{
			get { return (object)this[i]; }
			set { this[i] = (Declarator)value; }
		}

		int IList.Add(object x)
		{
			return this.Add((Declarator)x);
		}

		bool IList.Contains(object x)
		{
			return this.Contains((Declarator)x);
		}

		int IList.IndexOf(object x)
		{
			return this.IndexOf((Declarator)x);
		}

		void IList.Insert(int pos, object x)
		{
			this.Insert(pos, (Declarator)x);
		}

		void IList.Remove(object x)
		{
			this.Remove((Declarator)x);
		}

		void IList.RemoveAt(int pos)
		{
			this.RemoveAt(pos);
		}

		#endregion

		#region Implementation (IEnumerable)

		IEnumerator IEnumerable.GetEnumerator()
		{
			return (IEnumerator)(this.GetEnumerator());
		}

		#endregion

		#region Nested enumerator class
		/// <summary>
		///		Supports simple iteration over a <see cref="DeclaratorCollection"/>.
		/// </summary>
		private class Enumerator : IEnumerator, IDeclaratorCollectionEnumerator
		{
			#region Implementation (data)
			
			private DeclaratorCollection m_collection;
			private int m_index;
			private int m_version;
			
			#endregion
		
			#region Construction
			
			/// <summary>
			///		Initializes a new instance of the <c>Enumerator</c> class.
			/// </summary>
			/// <param name="tc"></param>
			internal Enumerator(DeclaratorCollection tc)
			{
				m_collection = tc;
				m_index = -1;
				m_version = tc.m_version;
			}
			
			#endregion
	
			#region Operations (type-safe IEnumerator)
			
			/// <summary>
			///		Gets the current element in the collection.
			/// </summary>
			public Declarator Current
			{
				get { return m_collection[m_index]; }
			}

			/// <summary>
			///		Advances the enumerator to the next element in the collection.
			/// </summary>
			/// <exception cref="InvalidOperationException">
			///		The collection was modified after the enumerator was created.
			/// </exception>
			/// <returns>
			///		<c>true</c> if the enumerator was successfully advanced to the next element; 
			///		<c>false</c> if the enumerator has passed the end of the collection.
			/// </returns>
			public bool MoveNext()
			{
				if (m_version != m_collection.m_version)
					throw new System.InvalidOperationException("Collection was modified; enumeration operation may not execute.");

				++m_index;
				return (m_index < m_collection.Count) ? true : false;
			}

			/// <summary>
			///		Sets the enumerator to its initial position, before the first element in the collection.
			/// </summary>
			public void Reset()
			{
				m_index = -1;
			}
			#endregion
	
			#region Implementation (IEnumerator)
			
			object IEnumerator.Current
			{
				get { return (object)(this.Current); }
			}
			
			#endregion
		}
		#endregion
		
		#region Nested Syncronized Wrapper class
		[Serializable]
			private class SyncDeclaratorCollection : DeclaratorCollection, System.Runtime.Serialization.IDeserializationCallback
		{
			#region Implementation (data)
			private const int timeout = 0; // infinite
			private DeclaratorCollection collection;
			[NonSerialized]
			private System.Threading.ReaderWriterLock rwLock;
			#endregion

			#region Construction
			internal SyncDeclaratorCollection(DeclaratorCollection list) : base(Tag.Default)
			{
				rwLock = new System.Threading.ReaderWriterLock();
				collection = list;
			}
			#endregion
			
			#region IDeserializationCallback Members
			void System.Runtime.Serialization.IDeserializationCallback.OnDeserialization(object sender)
			{
				rwLock = new System.Threading.ReaderWriterLock();
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(Declarator[] array)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}

			public override void CopyTo(Declarator[] array, int start)
			{
				rwLock.AcquireReaderLock(timeout);

				try
				{
					collection.CopyTo(array, start);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}
			}
			
			public override int Count
			{
				get
				{
					int count = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						count = collection.Count;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return count;
				}
			}

			public override bool IsSynchronized
			{
				get { return true; }
			}

			public override object SyncRoot
			{
				get { return collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override Declarator this[int i]
			{
				get
				{
					Declarator thisItem;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						thisItem = collection[i];
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}
					
					return thisItem;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection[i] = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int Add(Declarator x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.Add(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			
			public override void Clear()
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Clear();
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override bool Contains(Declarator x)
			{
				bool result = false;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.Contains(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override int IndexOf(Declarator x)
			{
				int result = 0;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					result = collection.IndexOf(x);
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return result;
			}

			public override void Insert(int pos, Declarator x)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Insert(pos,x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void Remove(Declarator x)
			{           
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.Remove(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}

			public override void RemoveAt(int pos)
			{
				rwLock.AcquireWriterLock(timeout);

				try
				{
					collection.RemoveAt(pos);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}
			}
			
			public override bool IsFixedSize
			{
				get { return collection.IsFixedSize; }
			}

			public override bool IsReadOnly
			{
				get { return collection.IsReadOnly; }
			}
			#endregion

			#region Type-safe IEnumerable
			public override IDeclaratorCollectionEnumerator GetEnumerator()
			{
				IDeclaratorCollectionEnumerator enumerator = null;
				rwLock.AcquireReaderLock(timeout);

				try
				{
					enumerator = collection.GetEnumerator();
				}
				finally
				{
					rwLock.ReleaseReaderLock();
				}

				return enumerator;
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get
				{
					int result = 0;
					rwLock.AcquireReaderLock(timeout);

					try
					{
						result = collection.Capacity;
					}
					finally
					{
						rwLock.ReleaseReaderLock();
					}

					return result;
				}
				
				set
				{
					rwLock.AcquireWriterLock(timeout);

					try
					{
						collection.Capacity = value;
					}
					finally
					{
						rwLock.ReleaseWriterLock();
					}
				}
			}

			public override int AddRange(DeclaratorCollection x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}

			public override int AddRange(Declarator[] x)
			{
				int result = 0;
				rwLock.AcquireWriterLock(timeout);

				try
				{
					result = collection.AddRange(x);
				}
				finally
				{
					rwLock.ReleaseWriterLock();
				}

				return result;
			}
			#endregion
		}
		#endregion

		#region Nested Read Only Wrapper class
		private class ReadOnlyDeclaratorCollection : DeclaratorCollection
		{
			#region Implementation (data)
			private DeclaratorCollection m_collection;
			#endregion

			#region Construction
			internal ReadOnlyDeclaratorCollection(DeclaratorCollection list) : base(Tag.Default)
			{
				m_collection = list;
			}
			#endregion
			
			#region Type-safe ICollection
			public override void CopyTo(Declarator[] array)
			{
				m_collection.CopyTo(array);
			}

			public override void CopyTo(Declarator[] array, int start)
			{
				m_collection.CopyTo(array,start);
			}
			public override int Count
			{
				get { return m_collection.Count; }
			}

			public override bool IsSynchronized
			{
				get { return m_collection.IsSynchronized; }
			}

			public override object SyncRoot
			{
				get { return this.m_collection.SyncRoot; }
			}
			#endregion
			
			#region Type-safe IList
			public override Declarator this[int i]
			{
				get { return m_collection[i]; }
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int Add(Declarator x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override void Clear()
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override bool Contains(Declarator x)
			{
				return m_collection.Contains(x);
			}

			public override int IndexOf(Declarator x)
			{
				return m_collection.IndexOf(x);
			}

			public override void Insert(int pos, Declarator x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void Remove(Declarator x)
			{           
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override void RemoveAt(int pos)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			
			public override bool IsFixedSize
			{
				get {return true;}
			}

			public override bool IsReadOnly
			{
				get {return true;}
			}
			#endregion

			#region Type-safe IEnumerable
			public override IDeclaratorCollectionEnumerator GetEnumerator()
			{
				return m_collection.GetEnumerator();
			}
			#endregion

			#region Public Helpers
			// (just to mimic some nice features of ArrayList)
			public override int Capacity
			{
				get { return m_collection.Capacity; }
				
				set { throw new NotSupportedException("This is a Read Only Collection and can not be modified"); }
			}

			public override int AddRange(DeclaratorCollection x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}

			public override int AddRange(Declarator[] x)
			{
				throw new NotSupportedException("This is a Read Only Collection and can not be modified");
			}
			#endregion
		}
		#endregion
		#region Text
		public override string Text
		{
			get
			{
				return "Name-InitExpr Pairs";
			}
		}
		#endregion
	}
	#endregion

}
