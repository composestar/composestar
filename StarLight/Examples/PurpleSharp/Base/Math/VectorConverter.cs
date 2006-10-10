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
using System.Globalization;
using System.ComponentModel;
using System.ComponentModel.Design.Serialization;
using System.Reflection;

namespace Purple.Math
{
  //=================================================================
  /// <summary>
  /// Converter, that converts a Vector2 object to a string and 
  /// vice versa.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public class Vector2Converter : ExpandableObjectConverter  {
    /// <summary>
    /// Converts the given value object to the specified type, using the specified context and culture information.
    /// </summary>
    /// <param name="context">An ITypeDescriptorContext that provides a format context.</param>
    /// <param name="culture">A CultureInfo object. If a null reference (Nothing in Visual Basic) is passed, the current culture is assumed.</param>
    /// <param name="value">The Object to convert.</param>
    /// <param name="destinationType"></param>
    /// <returns>The Type to convert the value parameter to.</returns>
    public override object ConvertTo(ITypeDescriptorContext context, 
      CultureInfo culture, object value, Type destinationType) {

      if (destinationType == typeof(string) && 
        value is Vector2) {
        Vector2 pt = (Vector2)value;
        return pt.X.ToString(culture) + "; " + pt.Y.ToString(culture);
      }
      return base.ConvertTo(context, culture, value, destinationType);      
    }

    /// <summary>
    /// Converts the given value to the type of this converter.
    /// </summary>
    /// <param name="context">An ITypeDescriptorContext that provides a format context. </param>
    /// <param name="culture">The CultureInfo to use as the current culture. </param>
    /// <param name="value">The Object to convert. </param>
    /// <returns>An Object that represents the converted value.</returns>
    public override object ConvertFrom(ITypeDescriptorContext context, CultureInfo culture, object value) {
      if (value.GetType() == typeof(string)) {
        try {
          string str = (string)value;
          string[] elements = str.Split(';');
          return new Vector2(float.Parse(elements[0], culture), float.Parse(elements[1], culture));
        } catch {
          throw new ArgumentException("Invalid value: " + value.ToString());
        }
      }
      return base.ConvertFrom (context, culture, value);
    }

    /// <summary>
    /// Returns whether this converter can convert an object of the given type to the type of this converter, 
    /// using the specified context.
    /// </summary>
    /// <param name="context">An ITypeDescriptorContext that provides a format context. </param>
    /// <param name="sourceType">A Type that represents the type you want to convert from. </param>
    /// <returns>True if this converter can perform the conversion; otherwise, false.</returns>
    public override bool CanConvertFrom(ITypeDescriptorContext context, Type sourceType) {
      if (sourceType == typeof(string))
        return true;
      return base.CanConvertFrom (context, sourceType);
    }
  }

  //=================================================================
  /// <summary>
  /// Converter, that converts a Vector3 object to a string and 
  /// vice versa.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public class Vector3Converter : ExpandableObjectConverter  {
    /// <summary>
    /// Converts the given value object to the specified type, using the specified context and culture information.
    /// </summary>
    /// <param name="context">An ITypeDescriptorContext that provides a format context.</param>
    /// <param name="culture">A CultureInfo object. If a null reference (Nothing in Visual Basic) is passed, the current culture is assumed.</param>
    /// <param name="value">The Object to convert.</param>
    /// <param name="destinationType"></param>
    /// <returns>The Type to convert the value parameter to.</returns>
    public override object ConvertTo(ITypeDescriptorContext context, 
      CultureInfo culture, object value, Type destinationType) {

      if (destinationType == typeof(string) && 
        value is Vector3) {
        Vector3 pt = (Vector3)value;
        return pt.X.ToString(culture) + "; " + pt.Y.ToString(culture)  + "; " + pt.Z.ToString(culture);
      }
      return base.ConvertTo(context, culture, value, destinationType);      
    }

    /// <summary>
    /// Converts the given value to the type of this converter.
    /// </summary>
    /// <param name="context">An ITypeDescriptorContext that provides a format context. </param>
    /// <param name="culture">The CultureInfo to use as the current culture. </param>
    /// <param name="value">The Object to convert. </param>
    /// <returns>An Object that represents the converted value.</returns>
    public override object ConvertFrom(ITypeDescriptorContext context, CultureInfo culture, object value) {
      if (value.GetType() == typeof(string)) {
        try {
          string str = (string)value;
          string[] elements = str.Split(';');
          return new Vector3(float.Parse(elements[0], culture), float.Parse(elements[1], culture),
            float.Parse(elements[2], culture));
        } catch {
          throw new ArgumentException("Invalid value: " + value.ToString());
        }
      }
      return base.ConvertFrom (context, culture, value);
    }

    /// <summary>
    /// Returns whether this converter can convert an object of the given type to the type of this converter, 
    /// using the specified context.
    /// </summary>
    /// <param name="context">An ITypeDescriptorContext that provides a format context. </param>
    /// <param name="sourceType">A Type that represents the type you want to convert from. </param>
    /// <returns>True if this converter can perform the conversion; otherwise, false.</returns>
    public override bool CanConvertFrom(ITypeDescriptorContext context, Type sourceType) {
      if (sourceType == typeof(string))
        return true;
      return base.CanConvertFrom (context, sourceType);
    }
  }

  //=================================================================
  /// <summary>
  /// Converter, that converts a Vector3 object to a string and 
  /// vice versa.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public class Vector4Converter : ExpandableObjectConverter  {
    /// <summary>
    /// Converts the given value object to the specified type, using the specified context and culture information.
    /// </summary>
    /// <param name="context">An ITypeDescriptorContext that provides a format context.</param>
    /// <param name="culture">A CultureInfo object. If a null reference (Nothing in Visual Basic) is passed, the current culture is assumed.</param>
    /// <param name="value">The Object to convert.</param>
    /// <param name="destinationType"></param>
    /// <returns>The Type to convert the value parameter to.</returns>
    public override object ConvertTo(ITypeDescriptorContext context, 
      CultureInfo culture, object value, Type destinationType) {

      if (destinationType == typeof(string) && 
        value is Vector4) {
        Vector4 pt = (Vector4)value;
        return pt.X.ToString(culture) + "; " + pt.Y.ToString(culture)  + "; " + 
          pt.Z.ToString(culture) + "; " + pt.W.ToString(culture);
      }
      return base.ConvertTo(context, culture, value, destinationType);      
    }

    /// <summary>
    /// Converts the given value to the type of this converter.
    /// </summary>
    /// <param name="context">An ITypeDescriptorContext that provides a format context. </param>
    /// <param name="culture">The CultureInfo to use as the current culture. </param>
    /// <param name="value">The Object to convert. </param>
    /// <returns>An Object that represents the converted value.</returns>
    public override object ConvertFrom(ITypeDescriptorContext context, CultureInfo culture, object value) {
      if (value.GetType() == typeof(string)) {
        try {
          string str = (string)value;
          string[] elements = str.Split(';');
          return new Vector4(float.Parse(elements[0], culture), float.Parse(elements[1], culture),
            float.Parse(elements[2], culture), float.Parse(elements[3], culture));
        } catch {
          throw new ArgumentException("Invalid value: " + value.ToString());
        }
      }
      return base.ConvertFrom (context, culture, value);
    }

    /// <summary>
    /// Returns whether this converter can convert an object of the given type to the type of this converter, 
    /// using the specified context.
    /// </summary>
    /// <param name="context">An ITypeDescriptorContext that provides a format context. </param>
    /// <param name="sourceType">A Type that represents the type you want to convert from. </param>
    /// <returns>True if this converter can perform the conversion; otherwise, false.</returns>
    public override bool CanConvertFrom(ITypeDescriptorContext context, Type sourceType) {
      if (sourceType == typeof(string))
        return true;
      return base.CanConvertFrom (context, sourceType);
    }
  }
}
