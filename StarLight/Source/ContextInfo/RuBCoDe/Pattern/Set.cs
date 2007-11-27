using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.RuBCoDe.Pattern
{
    /// <summary>
    /// A list with unique elements
    /// </summary>
    /// <typeparam name="T"></typeparam>
    public class Set<T> : IList<T>
    {
        private List<T> innerList;

        /// <summary>
        /// 
        /// </summary>
        public Set()
        {
            innerList = new List<T>();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="collection"></param>
        public Set(IEnumerable<T> collection)
        {
            innerList = new List<T>();
            AddRange(collection);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="capacity"></param>
        public Set(int capacity)
        {
            innerList = new List<T>(capacity);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="item"></param>
        public void Add(T item)
        {
            if (!innerList.Contains(item))
            {
                innerList.Add(item);
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="collection"></param>
        public void AddRange(IEnumerable<T> collection)
        {
            foreach (T e in collection)
            {
                Add(e);
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="item"></param>
        /// <returns></returns>
        public int IndexOf(T item)
        {
            return innerList.IndexOf(item);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="index"></param>
        /// <param name="item"></param>
        public void Insert(int index, T item)
        {
            throw new Exception("The method or operation is not implemented.");
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="index"></param>
        public void RemoveAt(int index)
        {
            innerList.RemoveAt(index);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="index"></param>
        /// <returns></returns>
        public T this[int index]
        {
            get
            {
                return innerList[index];
            }
            set
            {
                throw new Exception("The method or operation is not implemented.");
            }
        }

        /// <summary>
        /// 
        /// </summary>
        public void Clear()
        {
            innerList.Clear();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="item"></param>
        /// <returns></returns>
        public bool Contains(T item)
        {
            return innerList.Contains(item);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="array"></param>
        /// <param name="arrayIndex"></param>
        public void CopyTo(T[] array, int arrayIndex)
        {
            innerList.CopyTo(array, arrayIndex);
        }

        /// <summary>
        /// 
        /// </summary>
        public int Count
        {
            get { return innerList.Count; }
        }

        /// <summary>
        /// 
        /// </summary>
        public bool IsReadOnly
        {
            get { return false; }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="item"></param>
        /// <returns></returns>
        public bool Remove(T item)
        {
            return innerList.Remove(item);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public IEnumerator<T> GetEnumerator()
        {
            return innerList.GetEnumerator();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return innerList.GetEnumerator();
        }

    }
}
