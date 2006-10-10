//*****************************************************************************
//     ____                              ___                __ __      
//    /\  _`\                           /\_ \              _\ \\ \__   
//    \ \ \L\ \ __  __   _ __   _____   \//\ \       __   /\__  _  _\  
//     \ \ ,__//\ \/\ \ /\`'__\/\ '__`\   \ \ \    /'__`\ \/_L\ \\ \L_ 
//      \ \ \/ \ \ \_\ \\ \ \/ \ \ \L\ \   \_\ \_ /\  __/   /\_   _  _\
//       \ \_\  \ \____/ \ \_\  \ \ ,__/   /\____\\ \____\  \/_/\_\\_\/
//        \/_/   \/___/   \/_/   \ \ \/    \/____/ \/____/     \/_//_/ 
//                                \ \_\                                
//                                 \/_/                                            
//                  Purple# - The smart way of programming games
#region //
// Copyright (c) 2002-2003 by 
//   Markus Wöß
//   Bunnz@Bunnz.com
//   http://www.bunnz.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
#endregion
//*****************************************************************************
using System;
using System.Reflection;
using System.Collections;
using System.ComponentModel;
using System.ComponentModel.Design;

namespace Purple.Collections
{
  //=================================================================
  /// <summary>
  /// A specialied collection converter that is used by the 
  /// <see cref="System.Windows.Forms.PropertyGrid"/> class.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public class CollectionConverter : System.ComponentModel.CollectionConverter {
    //---------------------------------------------------------------
    #region Internal calss
    //---------------------------------------------------------------
    class MyPropertyDescriptor : SimplePropertyDescriptor  {
      ICollection collection;
      int index;
      MethodInfo getMethod;
      MethodInfo setMethod;

      /// <summary>
      /// Creates a new instance of <see cref="MyPropertyDescriptor"/> which takes a
      /// collection and the index of the element in the collection.
      /// </summary>
      /// <remarks>
      /// <note type="note">The collection must implement an indexer, otherwise the 
      /// <see cref="ArgumentException"/> is thrown.</note>
      /// </remarks>
      /// <param name="collection">The collection to use.</param>
      /// <param name="index">The index of the property.</param>
      /// <param name="attributes">Gets the collection of attributes for this member.</param>
      /// <exception cref="ArgumentException">If the collection doesn't implement an indexer that supports a 
      /// getter method.</exception>
      public MyPropertyDescriptor(ICollection collection, int index, Attribute[] attributes) 
        : base(collection.GetType(), "empty" , typeof(object), attributes)  {
        this.collection = collection;
        this.index = index;       
        RetrieveIndexer();
      }

      private void RetrieveIndexer() {
        PropertyInfo info = collection.GetType().GetProperty("Item", new Type[] {typeof(int)});
        if (info == null)
          throw new ArgumentException("Collection doesn't implement an indexer!");
        setMethod = info.GetSetMethod(true);
        getMethod = info.GetGetMethod(true);
        if (getMethod == null)
          throw new ArgumentException("Indexer doesn't implement a getter method!");
      }

      /// <summary>
      /// Returns the string which should be displayed in the <see cref="System.Windows.Forms.PropertyGrid"/>.
      /// </summary>
      public override string DisplayName {
        get {
          return collection.GetType().Name + "["+index+"]";
          //return "[" + index + "]: " + PropertyType.Name;
        }
      }

      /// <summary>
      /// When overridden in a derived class, gets the current value of the property on a component.
      /// </summary>
      /// <param name="component">The component with the property for which to retrieve the value. </param>
      /// <returns>The value of a property for a given component.</returns>
      public override object GetValue( object component ) {
        return getMethod.Invoke(collection, new object[] {index});
      }

      /// <summary>
      /// When overridden in a derived class, sets the value of the component to a different value.
      /// </summary>
      /// <param name="component">The component with the property value that is to be set. </param>
      /// <param name="value">The new value.</param>
      public override void SetValue( object component, object value) {
        if (setMethod != null)
          setMethod.Invoke(collection, new object[]{index, value});
      }

      /// <summary>
      /// Gets a value indicating whether this property is read-only.
      /// </summary>
      public override bool IsReadOnly {
        get {
          return setMethod == null;
        }
      }

      /// <summary>
      /// A <see cref="Type"/> that represents the type of the property.
      /// </summary>
      public override Type PropertyType {
        get {
          return GetValue(null).GetType();
        }
      }      
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Variables and Properties
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// Creates a new instance of a collection converter.
    /// </summary>
    public CollectionConverter() {
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Returns a collection of properties for the type of array specified by the value parameter, 
    /// using the specified context and attributes.
    /// </summary>
    /// <param name="context">An ITypeDescriptorContext that provides a format context.</param>
    /// <param name="value">An Object that specifies the type of array for which to get properties.</param>
    /// <param name="attributes">An array of type Attribute that is used as a filter.</param>
    /// <returns>A PropertyDescriptorCollection with the properties that are exposed for this data type, 
    /// or a null reference (Nothing in Visual Basic) if there are no properties.</returns>
    public override PropertyDescriptorCollection GetProperties(ITypeDescriptorContext context, object value, Attribute[] attributes) {
      ICollection collection = (ICollection)value;
      PropertyDescriptor[] properties = new PropertyDescriptor[collection.Count];
      for( int i=0; i<collection.Count; i++)
        properties[i] = new MyPropertyDescriptor( collection, i, attributes);
      PropertyDescriptorCollection props = new PropertyDescriptorCollection(properties);
      return props;
    }

    /// <summary>
    /// Returns whether this object supports properties, using the specified context.
    /// </summary>
    /// <param name="context">An ITypeDescriptorContext that provides a format context. </param>
    /// <returns>True if GetProperties should be called to find the properties of this object; otherwise, false.</returns>
    public override bool GetPropertiesSupported(ITypeDescriptorContext context) {
      return true;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
	}
}
