using System;
using System.Windows.Forms;

namespace SourceGrid
{

	/// <summary>
	/// A dictionary with keys of type Control and values of type LinkedControlValue
	/// </summary>
	public class LinkedControlsList : System.Collections.DictionaryBase
	{
		/// <summary>
		/// Initializes a new empty instance of the ControlToPositionAssociation class
		/// </summary>
		public LinkedControlsList()
		{
			// empty
		}

		/// <summary>
		/// Gets or sets the Position associated with the given Control
		/// </summary>
		/// <param name="key">
		/// The Control whose value to get or set.
		/// </param>
		public virtual LinkedControlValue this[Control key]
		{
			get
			{
				return (LinkedControlValue) this.Dictionary[key];
			}
			set
			{
				this.Dictionary[key] = value;
			}
		}

		/// <summary>
		/// Adds an element with the specified key and value to this ControlToPositionAssociation.
		/// </summary>
		/// <param name="key">
		/// The Control key of the element to add.
		/// </param>
		/// <param name="value">
		/// The Position value of the element to add.
		/// </param>
		public virtual void Add(Control key, LinkedControlValue value)
		{
			this.Dictionary.Add(key, value);
		}

		/// <summary>
		/// Determines whether this ControlToPositionAssociation contains a specific key.
		/// </summary>
		/// <param name="key">
		/// The Control key to locate in this ControlToPositionAssociation.
		/// </param>
		/// <returns>
		/// true if this ControlToPositionAssociation contains an element with the specified key;
		/// otherwise, false.
		/// </returns>
		public virtual bool Contains(Control key)
		{
			return this.Dictionary.Contains(key);
		}

		/// <summary>
		/// Determines whether this ControlToPositionAssociation contains a specific key.
		/// </summary>
		/// <param name="key">
		/// The Control key to locate in this ControlToPositionAssociation.
		/// </param>
		/// <returns>
		/// true if this ControlToPositionAssociation contains an element with the specified key;
		/// otherwise, false.
		/// </returns>
		public virtual bool ContainsKey(Control key)
		{
			return this.Dictionary.Contains(key);
		}

		/// <summary>
		/// Determines whether this ControlToPositionAssociation contains a specific value.
		/// </summary>
		/// <param name="value">
		/// The Position value to locate in this ControlToPositionAssociation.
		/// </param>
		/// <returns>
		/// true if this ControlToPositionAssociation contains an element with the specified value;
		/// otherwise, false.
		/// </returns>
		public virtual bool ContainsValue(LinkedControlValue value)
		{
			foreach (LinkedControlValue item in this.Dictionary.Values)
			{
				if (item == value)
					return true;
			}
			return false;
		}

		/// <summary>
		/// Removes the element with the specified key from this ControlToPositionAssociation.
		/// </summary>
		/// <param name="key">
		/// The Control key of the element to remove.
		/// </param>
		public virtual void Remove(Control key)
		{
			this.Dictionary.Remove(key);
		}

		/// <summary>
		/// Gets a collection containing the keys in this ControlToPositionAssociation.
		/// </summary>
		public virtual System.Collections.ICollection Keys
		{
			get
			{
				return this.Dictionary.Keys;
			}
		}

		/// <summary>
		/// Gets a collection containing the values in this ControlToPositionAssociation.
		/// </summary>
		public virtual System.Collections.ICollection Values
		{
			get
			{
				return this.Dictionary.Values;
			}
		}
	}

	/// <summary>
	/// Determine the scrolling mode of the linked controls.
	/// </summary>
	public enum LinkedControlScrollMode
	{
		None = 0,
		ScrollVertical = 1,
		ScrollHorizontal = 2,
		ScrollBoth = 3,
		BasedOnPosition = 4
	}

	/// <summary>
	/// Linked control value
	/// </summary>
	public struct LinkedControlValue
	{
		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="position"></param>
		public LinkedControlValue(Position position)
		{
			mPosition = position;
			m_bUseCellBorder = true;
			mScrollMode = LinkedControlScrollMode.BasedOnPosition;
		}

		private Position mPosition;
		/// <summary>
		/// Gets or sets the position of the linked control.
		/// </summary>
		public Position Position
		{
			get{return mPosition;}
			set{mPosition = value;}
		}

		private bool m_bUseCellBorder;

		/// <summary>
		/// Gets or sets if show the cell border. True to insert the editor control inside the border of the cell, false to put the editor control over the entire cell. If you use true remember to set EnableCellDrawOnEdit == true.
		/// </summary>
		public bool UseCellBorder
		{
			get{return m_bUseCellBorder;}
			set{m_bUseCellBorder = value;}
		}

		private LinkedControlScrollMode mScrollMode;
		/// <summary>
		/// Gets or sets the scrolling mode of the control.
		/// </summary>
		public LinkedControlScrollMode ScrollMode
		{
			get{return mScrollMode;}
			set{mScrollMode = value;}
		}

		/// <summary>
		/// GetHashCode
		/// </summary>
		/// <returns></returns>
		public override int GetHashCode()
		{
			return Position.GetHashCode();
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="other"></param>
		/// <returns></returns>
		public bool Equals(LinkedControlValue other)
		{
			return (mPosition == other.mPosition && 
				m_bUseCellBorder == other.m_bUseCellBorder &&
				mScrollMode == other.mScrollMode);
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="obj"></param>
		/// <returns></returns>
		public override bool Equals(object obj)
		{
			return Equals((LinkedControlValue)obj);
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="Left"></param>
		/// <param name="Right"></param>
		/// <returns></returns>
		public static bool operator == (LinkedControlValue Left, LinkedControlValue Right)
		{
			return Left.Equals(Right);
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="Left"></param>
		/// <param name="Right"></param>
		/// <returns></returns>
		public static bool operator != (LinkedControlValue Left, LinkedControlValue Right)
		{
			return !Left.Equals(Right);
		}

		/// <summary>
		/// 
		/// </summary>
		/// <returns></returns>
		public override string ToString()
		{
			return Position.ToString();
		}	
	}
}
