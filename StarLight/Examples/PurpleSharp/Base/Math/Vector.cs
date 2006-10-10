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
using Purple.Data;

namespace Purple.Math {
  //=================================================================
  /// <summary>
  /// Vector structure (4 elements)
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  [System.ComponentModel.TypeConverter(typeof(Vector4Converter))]
  public struct Vector4 {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    private float x, y, z, w;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>X element of the vector.</summary>
    [Purple.Serialization.Serialize(true)]
    public float X {
      get {
        return x;
      }
      set {
        x= value;
      }
    }

    /// <summary>Y element of the vector.</summary>
    [Purple.Serialization.Serialize(true)]
    public float Y {
      get {
        return y;
      }
      set {
        y = value;
      }
    }

    /// <summary>Z element of the vector.</summary>
    [Purple.Serialization.Serialize(true)]
    public float Z {
      get {
        return z;
      }
      set {
        z = value;
      }
    }

    /// <summary>W element of the vector.</summary>
    [Purple.Serialization.Serialize(true)]
    public float W {
      get {
        return w;
      }
      set {
        w = value;
      }
    }

    /// <summary>
    /// returns the identity matrix
    /// </summary>
    [System.Diagnostics.DebuggerHidden]
    public static Vector4 Zero {	
      get {
        return new Vector4();
      }
    }		

    /// <summary>
    /// Returns a vector containing the first three components.
    /// </summary>
    public Vector3 Vector3{
      get {
        return new Vector3( x, y, z);
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// constructor for filling all elements
    /// </summary>
    /// <param name="x"></param>
    /// <param name="y"></param>
    /// <param name="z"></param>
    /// <param name="w"></param>
    public Vector4(	float x, float y, float z, float w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
    }

    /// <summary>
    /// creates a new vector4 from a vector3
    /// </summary>
    /// <param name="vec">object to create vector4 from</param>
    public Vector4( Vector3 vec) : this(vec, 0.0f) {
    }

    /// <summary>
    /// creates a new vector4 from a vector3
    /// </summary>
    /// <param name="vec"></param>
    /// <param name="w"></param>
    public Vector4( Vector3 vec, float w) {
      this.x = vec.X;
      this.y = vec.Y;
      this.z = vec.Z;
      this.w = w;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// new hashcode function
    /// </summary>
    /// <returns></returns>
    public override int GetHashCode() {
      return x.GetHashCode() ^ y.GetHashCode() ^ z.GetHashCode() ^ w.GetHashCode();
    }

    /// <summary>
    /// new equals function
    /// </summary>
    /// <param name="obj"></param>
    /// <returns></returns>
    public override bool Equals(object obj) {
      if (obj == null)
        return false;
      Vector4 vec = (Vector4)obj;
      return vec.x == x && vec.y == y && vec.z == z && vec.w == w;
    }

    /// <summary>
    /// Returns the string representation of the object.
    /// </summary>
    /// <returns>The string representation of the object.</returns>
    public override string ToString() {
      return "X: " + x + " Y: " + y + " Z: " + z + " W: " + w;
    }

    /// <summary>
    /// calc unit vector from a certain Vector
    /// </summary>
    /// <param name="a">vector to calc unit vector from</param>
    /// <returns>normalized vector</returns>
    public static Vector4 Unit(Vector4 a) {
      return a / a.Length();
    }

    /// <summary>
    /// normalizes the current instance
    /// </summary>
    public void Normalize() {
      this /= this.Length();
    }

    /// <summary>
    /// Calculates the length of the current vector.
    /// </summary>
    /// <returns>Length of the current vector.</returns>
    public float Length() {
      return Basic.Sqrt ( this*this );
    }

    /// <summary>
    /// Calculates the squared length of the current vector.
    /// </summary>
    /// <returns>The squared length of the current vector.</returns>
    public float LengthSquared() {
      return this*this;
    }

    /// <summary>
    /// linear interpolation between to vectors
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <param name="time">interpolation time [0..1]</param>
    /// <returns>interpolated vector</returns>
    public static Vector4 Lerp(Vector4 a, Vector4 b, float time) {
      return new Vector4( a.x + (b.x - a.x)*time,
        a.y + (b.y - a.y)*time,
        a.z + (b.z - a.z)*time,
        a.W + (b.W - a.W)*time);
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Operations
    //---------------------------------------------------------------
    /// <summary>
    /// add two vectors
    /// </summary>
    /// <param name="a">vector a</param>
    /// <param name="b">vector b</param>
    /// <returns>sum of the two vedctors</returns>
    public static Vector4 operator+(Vector4 a, Vector4 b) {
      return new Vector4( a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w );								
    }

    /// <summary>
    /// dot product of two vectors
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <returns>dot product</returns>
    public static float operator*(Vector4 a, Vector4 b) {
      return a.x*b.x + a.y*b.y + a.z*b.z + a.w*b.w;
    }

    /// <summary>
    /// multiply a vector with a scalar
    /// </summary>
    /// <param name="vec">vector to multiply with scalar</param>
    /// <param name="scalar">to multiply vector with</param>
    /// <returns>result of vec*scalar</returns>
    public static Vector4 operator*(Vector4 vec, float scalar) {
      return new Vector4( vec.x*scalar, vec.y*scalar, vec.z*scalar, vec.w*scalar);
    }

    /// <summary>
    /// dot product of two vectors
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <returns>dot product</returns>
    public static float Dot(Vector4 a, Vector4 b) {
      return a*b;  
    }

    /// <summary>
    /// transform a vector by a given Matrix
    /// </summary>
    /// <param name="v">vector to transform</param>
    /// <param name="m">matrix to use for Transformation</param>
    /// <returns>transformed vector</returns>
    public static Vector4 operator*(Vector4 v, Matrix4 m) {
      return new Vector4( v.x*m.A1 + v.y*m.B1+ v.z*m.C1+ v.w*m.D1,
        v.x*m.A2 + v.y*m.B2 + v.z*m.C2 + v.w*m.D2,
        v.x*m.A3 + v.y*m.B3 + v.z*m.C3 + v.w*m.D3,
        v.x*m.A4 + v.y*m.B4 + v.z*m.C4 + v.w*m.D4);
    }

    /// <summary>
    /// divide vector
    /// </summary>
    /// <param name="vec">vector to divide</param>
    /// <param name="divisor">divisor to divide vector with</param>
    /// <returns>vector/divisor</returns>
    public static Vector4 operator/(Vector4 vec, float divisor) {
      return new Vector4( vec.x / divisor, vec.y / divisor, vec.z / divisor, vec.w / divisor);
    }

    /// <summary>
    /// indexer for matrix
    /// </summary>
    public float this [int index] {			
      get {
        switch(index) {
          case 0:
            return x;
          case 1:
            return y;
          case 2:
            return z;
          case 3:
            return w;
          default:
            throw new IndexOutOfRangeException("Invalid vector index!");
        }
      }
      set {			
        switch(index) {
          case 0:
            x = value;
            break;
          case 1:
            y = value;
            break;
          case 2:
            z = value;
            break;
          case 3:
            w = value;
            break;
          default:
            throw new IndexOutOfRangeException("Invalid vector index!");
        }
      }			
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }

	
  //=================================================================
  /// <summary>
  /// Vector structure (3 elements)
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  [System.ComponentModel.TypeConverter(typeof(Vector3Converter))]
  public struct Vector3 {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    private float x, y, z;		
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>X element of the vector.</summary>
    [Purple.Serialization.Serialize(true)]
    public float X {
      get {
        return x;
      }
      set {
        x= value;
      }
    }

    /// <summary>Y element of the vector.</summary>
    [Purple.Serialization.Serialize(true)]
    public float Y {
      get {
        return y;
      }
      set {
        y = value;
      }
    }

    /// <summary>Z element of the vector.</summary>
    [Purple.Serialization.Serialize(true)]
    public float Z {
      get {
        return z;
      }
      set {
        z = value;
      }
    }

    /// <summary>
    /// returns the identity matrix
    /// </summary>
    public static Vector3 Zero {	
      get {
        return new Vector3();
      }
    }

    /// <summary>
    /// a column of matrix
    /// </summary>
    public static Vector3 LookAt {
      get {
        return new Vector3( 0, 0, 1);
      }
    }

    /// <summary>
    /// b column of matrix
    /// </summary>
    public static Vector3 Up {
      get {
        return new Vector3( 0, 1.0f, 0);
      }
    }

    /// <summary>
    /// c column of matrix
    /// </summary>
    public static Vector3 Right {
      get {
        return new Vector3( 1, 0, 0);
      }
    }

    /// <summary>
    /// Returns a vector containing the first three components.
    /// </summary>
    public Vector2 Vector2{
      get {
        return new Vector2( x, y);
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// constructor for filling all elements
    /// </summary>
    /// <param name="x"></param>
    /// <param name="y"></param>
    /// <param name="z"></param>		
    public Vector3(	float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;			
    }

    /// <summary>
    /// construct vector3 from vector2
    /// z component is filled with 0
    /// </summary>
    /// <param name="vec2"></param>
    public Vector3( Vector2 vec2) {
      this.x = vec2.X;
      this.y = vec2.Y;
      this.z = 0;
    }

    /// <summary>
    /// construct vector3 from vector2
    /// z component is filled with 0
    /// </summary>
    /// <param name="vec2"></param>
    /// <param name="z"></param>
    public Vector3( Vector2 vec2, float z) {
      this.x = vec2.X;
      this.y = vec2.Y;
      this.z = z;
    }
    /// <summary>
    /// Creates a vector from a byte array.
    /// </summary>
    /// <param name="bytes">Array of at least 12 byte elements.</param>
    /// <returns>Created vector.</returns>
    public static Vector3 From(byte[] bytes) {
      return From(bytes, 0);
    }


    /// <summary>
    /// Creates a vector from a byte array.
    /// </summary>
    /// <param name="bytes">Array of at least 12 byte elements.</param>
    /// <param name="offset">Start offset in buffer.</param>
    /// <returns>Created vector.</returns>
    public unsafe static Vector3 From(byte[] bytes, int offset) {
      System.Diagnostics.Debug.Assert(bytes.Length >= (12 + offset));
      fixed ( byte* p = &bytes[offset] ) {
        float *floatPtr = (float *)(p);
        return new Vector3( floatPtr[0], floatPtr[1], floatPtr[2]);
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// Adds the weighted skinned source vector.
    /// </summary>
    public void AddSkinned( Vector3 sourceVec, ref Matrix4 joint, float weight) {
      x += (sourceVec.x*joint.A1 + sourceVec.y*joint.B1 + sourceVec.z*joint.C1 + joint.D1)*weight;
      y += (sourceVec.x*joint.A2 + sourceVec.y*joint.B2 + sourceVec.z*joint.C2 + joint.D2)*weight;
      z += (sourceVec.x*joint.A3 + sourceVec.y*joint.B3 + sourceVec.z*joint.C3 + joint.D3)*weight;
    }

    /// <summary>
    /// new hashcode function
    /// </summary>
    /// <returns></returns>
    public override int GetHashCode() {
      return x.GetHashCode() ^ y.GetHashCode() ^ z.GetHashCode();
    }

    /// <summary>
    /// new equals function
    /// </summary>
    /// <param name="obj"></param>
    /// <returns></returns>
    public override bool Equals(object obj) {
      if (obj == null)
        return false;
      Vector3 vec = (Vector3)obj;
      return vec.x == x && vec.y == y && vec.z == z;
    }

    /// <summary>
    /// Returns the string representation of the object.
    /// </summary>
    /// <returns>The string representation of the object.</returns>
    public override string ToString() {
      return "X: " + x + " Y: " + y + " Z: " + z;
    }

    /// <summary>
    /// calc unit vector from a certain Vector
    /// </summary>
    /// <param name="a">vector to calc unit vector from</param>
    /// <returns>unit vector</returns>
    public static Vector3 Unit(Vector3 a) {
      float length = 1/a.Length();
      return a * length;
    }

    /// <summary>
    /// normalizes the current instance
    /// </summary>
    public void Normalize() {
      float length = 1/Length();
      this.x = x*length;
      this.y = y*length;
      this.z = z*length;
    }

    /// <summary>
    /// Returns the length of the vector.
    /// </summary>
    /// <returns>Length of the vector.</returns>
    public float Length() {
      return Basic.Sqrt ( this*this);
    }

    /// <summary>
    /// Returns the squared length of the vector.
    /// </summary>
    /// <returns>Squared length of the vector.</returns>
    public float LengthSquared() {
      return this*this;
    }

    /// <summary>
    /// calcs the cross product of two vectors
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <returns>normal vector of a and b</returns>
    public static Vector3 Cross(Vector3 a, Vector3 b) {
      return new Vector3 (	
        a.y * b.z - a.z * b.y,
        a.z * b.x - a.x * b.z,
        a.x * b.y - a.y * b.x);	
    }

    /// <summary>
    /// calcs the cross product of two vectors
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <returns>normal vector of a and b</returns>
    public static Vector3 CrossUnit(Vector3 a, Vector3 b) {
      Vector3 vec = new Vector3 (	
        a.y * b.z - a.z * b.y,
        a.z * b.x - a.x * b.z,
        a.x * b.y - a.y * b.x);	
      float length = Math.Basic.InvSqrt(vec.x * vec.x + vec.y*vec.y * vec.z*vec.z);
      vec.x = vec.x*length;
      vec.y = vec.y*length;
      vec.z = vec.z*length;
      return vec;
    }

    /// <summary>
    /// creates a vector from an array
    /// </summary>
    /// <param name="vec"></param>
    /// <returns></returns>
    public static Vector3 FromArray(float[] vec) {
      System.Diagnostics.Debug.Assert( vec.Length == 3, "Array.Length != 3");
      return new Vector3( vec[0], vec[1], vec[2] );
    }

    /// <summary>
    /// linear interpolation between to vectors
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <param name="time">interpolation time [0..1]</param>
    /// <returns>interpolated vector</returns>
    public static Vector3 Lerp(Vector3 a, Vector3 b, float time) {
      return new Vector3( a.x + (b.x - a.x)*time,
        a.y + (b.y - a.y)*time,
        a.z + (b.z - a.z)*time);
    }

    /// <summary>
    /// Sets the vector to zero.
    /// </summary>
    public void SetZero() {
      x = 0;
      y = 0;
      z = 0;
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Operations
    //---------------------------------------------------------------
    /// <summary>
    /// add two vectors
    /// </summary>
    /// <param name="a">vector a</param>
    /// <param name="b">vector b</param>
    /// <returns>sum of the two vedctors</returns>
    public static Vector3 operator+(Vector3 a, Vector3 b) {
      return new Vector3( a.x + b.x, a.y + b.y, a.z + b.z );								
    }

    /// <summary>
    /// Adds a certain vector to the current vector.
    /// </summary>
    /// <param name="a">The vector to add.</param>
    public void Add(Vector3 a) {
      x += a.x;
      y += a.y;
      z += a.z;
    }

    /// <summary>
    /// Adds a weighted vector.
    /// </summary>
    /// <param name="a">The vector to add.</param>
    /// <param name="scalar">The scalar to multiply vector beforea adding.</param>
    public void AddWeighted(Vector3 a, float scalar) {
      x += a.x*scalar;
      y += a.y*scalar;
      z += a.z*scalar;
    }

    /// <summary>
    /// subtracts Vector be from Vector a
    /// </summary>
    /// <param name="a">vector to subtract from</param>
    /// <param name="b">vector to subtract</param>
    /// <returns>result of subtraction</returns>
    public static Vector3 operator-(Vector3 a, Vector3 b) {
      return new Vector3( a.x - b.x, a.y - b.y, a.z - b.z);
    }

    /// <summary>
    /// Negate vector
    /// </summary>
    /// <param name="a">vector to negate</param>
    /// <returns>negative vector</returns>
    public static Vector3 operator-(Vector3 a) {
      return new Vector3( -a.x, -a.y, -a.z );
    }	

    /// <summary>
    /// divide vector
    /// </summary>
    /// <param name="vec">vector to divide</param>
    /// <param name="divisor">divisor to divide vector with</param>
    /// <returns>vector/divisor</returns>
    public static Vector3 operator/(Vector3 vec, float divisor) {
      return new Vector3( vec.x / divisor, vec.y / divisor, vec.z / divisor );
    }

    /// <summary>
    /// scalar product
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <returns>scalar product</returns>
    public static float operator*(Vector3 a, Vector3 b) {
      return a.x*b.x + a.y*b.y + a.z*b.z;
    }

    /// <summary>
    /// dot product of two vectors
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <returns>dot product</returns>
    public static float Dot(Vector3 a, Vector3 b) {
      return a*b;  
    }

    /// <summary>
    /// transform a vector by a given Matrix
    /// </summary>
    /// <param name="m">matrix to use for Transformation</param>
    /// <param name="v">vector to transform</param>		
    /// <returns>transformed vector</returns>
    public static Vector3 operator*(Vector3 v, Matrix4 m) {
      Vector3 vec =  new Vector3( v.x*m.A1 + v.y*m.B1 + v.z*m.C1 + m.D1,
        v.x*m.A2 + v.y*m.B2 + v.z*m.C2 + m.D2,
        v.x*m.A3 + v.y*m.B3 + v.z*m.C3 + m.D3);
      return vec;
    }

    /// <summary>
    /// Transforms the current vector by a matrix.
    /// </summary>
    /// <param name="m">Matrix to transform current vector with.</param>
    public void Mul(ref Matrix4 m) {
      float ox = x;
      float oy = y;
      float oz = z;
      
      x = ox*m.A1 + oy*m.B1 + oz*m.C1 + m.D1;
      y = ox*m.A2 + oy*m.B2 + oz*m.C2 + m.D2;
      z = ox*m.A3 + oy*m.B3 + oz*m.C3 + m.D3;
    }

    /// <summary>
    /// transform a vector by a given Matrix
    /// </summary>
    /// <param name="m">matrix to use for Transformation</param>
    /// <param name="v">vector to transform</param>		
    /// <returns>transformed vector</returns>
    public static Vector3 operator*(Vector3 v, Matrix3 m) {
      return new Vector3( v.x*m.A1 + v.y*m.B1 + v.z*m.C1,
        v.x*m.A2 + v.y*m.B2 + v.z*m.C2,
        v.x*m.A3 + v.y*m.B3 + v.z*m.C3);
    }

    /// <summary>
    /// scales the vector by a scale vector
    /// </summary>
    /// <param name="a">the vector to scale</param>
    /// <param name="b">the scale vector</param>
    /// <returns>the element wise multiplication of the two vectors</returns>
    public static Vector3 Scale(Vector3 a, Vector3 b) {
      return new Vector3(a.x*b.x, a.y*b.y, a.z*b.z);
    }

    /// <summary>
    /// tests if all elements of one vector are smaller than all of another vector
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <returns></returns>
    public static bool AllLess(Vector3 a, Vector3 b) {
      return a.x < b.x && a.y < b.y && a.z < b.z;
    }

    /// <summary>
    /// tests if all elements of one vector are smaller than all of another vector
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <returns></returns>
    public static bool AllLessOrEqual(Vector3 a, Vector3 b) {
      return a.x <= b.x && a.y <= b.y && a.z <= b.z;
    }

    /// <summary>
    /// test if at least one element of a is smaller than in b
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <returns></returns>
    public static bool OneLess(Vector3 a, Vector3 b) {
      return a.x < b.x || a.y < b.y || a.z < b.z;
    }

    /// <summary>
    /// test if at least one element of a is smaller than in b
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <returns></returns>
    public static bool OneLessOrEqual(Vector3 a, Vector3 b) {
      return a.x <= b.x || a.y <= b.y || a.z <= b.z;
    }

    /// <summary>
    /// multiply a vector with a scalar
    /// </summary>
    /// <param name="vec">vector to multiply with scalar</param>
    /// <param name="scalar">to multiply vector with</param>
    /// <returns>result of vec*scalar</returns>
    public static Vector3 operator*(Vector3 vec, float scalar) {
      return new Vector3( vec.x*scalar, vec.y*scalar, vec.z*scalar);
    }

    /// <summary>
    /// Multiplies the current vector with a scalar.
    /// </summary>
    /// <param name="scalar"></param>
    public void Mul(float scalar) {
      x = x*scalar;
      y = y*scalar;
      z = z*scalar;
    }

    /// <summary>
    /// multiply a vector with a scalar
    /// </summary>
    /// <param name="scalar">to multiply vector with</param>
    /// <param name="vec">vector to multiply with scalar</param>
    /// <returns>result of scalar*vec</returns>
    public static Vector3 operator*(float scalar, Vector3 vec) {
      return new Vector3( vec.x*scalar, vec.y*scalar, vec.z*scalar);
    }

    /// <summary>
    /// tests if two vectors are the same
    /// </summary>
    /// <param name="vec">first vector to compare</param>
    /// <param name="vec2">second vector ti compare</param>
    /// <returns>true if the elements of the vectors are the same</returns>
    public static bool operator==(Vector3 vec, Vector3 vec2) {
      return (vec.x == vec2.x && vec.y == vec2.y && vec.z == vec2.z);
    }

    /// <summary>
    /// tests if two vectors are different
    /// </summary>
    /// <param name="vec">first vector to compare</param>
    /// <param name="vec2">second vector ti compare</param>
    /// <returns>true if at least one elements of vec is different from vec2</returns>
    public static bool operator!=(Vector3 vec, Vector3 vec2) {
      return (vec.x != vec2.x || vec.y != vec2.y || vec.z != vec2.z);
    }

    /// <summary>
    /// indexer for matrix
    /// </summary>
    public float this [int index] {			
      get {
        switch(index) {
          case 0:
            return x;
          case 1:
            return y;
          case 2:
            return z;
          default:
            throw new IndexOutOfRangeException("Invalid vector index!");
        }
      }
      set {			
        switch(index) {
          case 0:
            x = value;
            break;
          case 1:
            y = value;
            break;
          case 2:
            z = value;
            break;
          default:
            throw new IndexOutOfRangeException("Invalid vector index!");
        }
      }				
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------
  }

  //=================================================================
  /// <summary>
  /// Vector structure (2 elements)
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  [System.ComponentModel.TypeConverter(typeof(Vector2Converter))]
  public struct Vector2 {
    //---------------------------------------------------------------
    #region Variables
    //---------------------------------------------------------------
    private float x, y;
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Properties
    //---------------------------------------------------------------
    /// <summary>X element of the vector.</summary>
    [Purple.Serialization.Serialize(true)]
    public float X {
      get {
        return x;
      }
      set {
        x= value;
      }
    }

    /// <summary>Y element of the vector.</summary>
    [Purple.Serialization.Serialize(true)]
    public float Y {
      get {
        return y;
      }
      set {
        y = value;
      }
    }

    /// <summary>
    /// returns the a vector initialised with 0.0f
    /// </summary>
    public static Vector2 Zero {	
      get {
        return new Vector2();
      }
    }

    /// <summary>
    /// returns a vector initialised with 1.0f
    /// </summary>
    public static Vector2 One {	
      get {
        return new Vector2( 1.0f, 1.0f );
      }
    }
    //---------------------------------------------------------------
    #endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
    #region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// constructor for filling all elements
    /// </summary>
    /// <param name="x"></param>
    /// <param name="y"></param>		
    public Vector2(	float x, float y) {
      this.x = x;
      this.y = y;			
    }

    /// <summary>
    /// Creates a vector from a byte array.
    /// </summary>
    /// <param name="bytes">Array of at least 8 byte elements.</param>
    /// <returns>Created vector.</returns>
    public static Vector2 From(byte[] bytes) {
      return From(bytes, 0);
    }

    /// <summary>
    /// Creates a vector from a byte array.
    /// </summary>
    /// <param name="bytes">Array of at least 8 byte elements.</param>
    /// <param name="offset">The start offset in the buffer.</param>
    /// <returns>Created vector.</returns>
    public unsafe static Vector2 From(byte[] bytes, int offset) {
      System.Diagnostics.Debug.Assert(bytes.Length >= (8 + offset));
      fixed ( byte* p = &bytes[offset] ) {
        float *floatPtr = (float *)(p);
        return new Vector2( floatPtr[0], floatPtr[1]);
      }
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------
    /// <summary>
    /// new hashcode function
    /// </summary>
    /// <returns></returns>
    public override int GetHashCode() {
      return x.GetHashCode() ^ y.GetHashCode();
    }

    /// <summary>
    /// new equals function
    /// </summary>
    /// <param name="obj"></param>
    /// <returns></returns>
    public override bool Equals(object obj) {
      if (obj == null)
        return false;
      Vector2 vec = (Vector2)obj;
      return vec.x == x && vec.y == y;
    }

    /// <summary>
    /// Returns the string representation of the object.
    /// </summary>
    /// <returns>The string representation of the object.</returns>
    public override string ToString() {
      return "X: " + x + " Y: " + y;
    }

		/// <summary>
		/// calc unit vector from a certain Vector
		/// </summary>
		/// <param name="a">vector to calc unit vector from</param>
		/// <returns>unit vector</returns>
		public static Vector2 Unit(Vector2 a) {
			return a / a.Length();
		}

    /// <summary>
    /// Calculates the orthogonal vector that points to the right.
    /// </summary>
    /// <param name="a">Vector to calculate orthogonal vector from.</param>
    /// <returns>The orthogonal vector.</returns>
    public static Vector2 Ortho(Vector2 a) {
      return new Vector2( a.Y, -a.X);
    }

    /// <summary>
    /// normalizes the current instance
    /// </summary>
    public void Normalize() {
      this /= this.Length();
    }

		/// <summary>
		/// Calculates the length of the current vector.
		/// </summary>
		/// <returns>Length of the current vector.</returns>
		public float Length() {
			return Basic.Sqrt(this*this);
		}

    /// <summary>
    /// Calculates the squared length of the current vector.
    /// </summary>
    /// <returns>The squared length of the current vector.</returns>
    public float LengthSquared() {
      return this*this;
    }

		/// <summary>
		/// creates a vector from an array
		/// </summary>
		/// <param name="vec"></param>
		/// <returns></returns>
		public static Vector2 FromArray(float[] vec) {
			System.Diagnostics.Debug.Assert( vec.Length == 2, "Array.Length != 2");
			return new Vector2( vec[0], vec[1]);
		}

		/// <summary>
		/// linear interpolation between to vectors
		/// </summary>
		/// <param name="a">first vector</param>
		/// <param name="b">second vector</param>
		/// <param name="time">interpolation time [0..1]</param>
		/// <returns>interpolated vector</returns>
		public static Vector2 Lerp(Vector2 a, Vector2 b, float time) {
			return new Vector2( a.x + (b.x - a.x)*time,
				a.y + (b.y - a.y)*time);				
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Operations
		//---------------------------------------------------------------
		/// <summary>
		/// add two vectors
		/// </summary>
		/// <param name="a">vector a</param>
		/// <param name="b">vector b</param>
		/// <returns>sum of the two vedctors</returns>
		public static Vector2 operator+(Vector2 a, Vector2 b) {
			return new Vector2( a.x + b.x, a.y + b.y );								
		}

		/// <summary>
		/// subtracts Vector be from Vector a
		/// </summary>
		/// <param name="a">vector to subtract from</param>
		/// <param name="b">vector to subtract</param>
		/// <returns>result of subtraction</returns>
		public static Vector2 operator-(Vector2 a, Vector2 b) {
			return new Vector2( a.x - b.x, a.y - b.y);
		}

		/// <summary>
		/// Negate vector
		/// </summary>
		/// <param name="a">vector to negate</param>
		/// <returns>negative vector</returns>
		public static Vector2 operator-(Vector2 a) {
			return new Vector2( -a.x, -a.y);
		}	

		/// <summary>
		/// divide vector
		/// </summary>
		/// <param name="vec">vector to divide</param>
		/// <param name="divisor">divisor to divide vector with</param>
		/// <returns>vector/divisor</returns>
		public static Vector2 operator/(Vector2 vec, float divisor) {
			return new Vector2( vec.x / divisor, vec.y / divisor);
		}

		/// <summary>
		/// scalar product
		/// </summary>
		/// <param name="a">first vector</param>
		/// <param name="b">second vector</param>
		/// <returns>scalar product</returns>
		public static float operator*(Vector2 a, Vector2 b) {
			return a.x*b.x + a.y*b.y;
		}

    /// <summary>
    /// dot product of two vectors
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <returns>dot product</returns>
    public static float Dot(Vector2 a, Vector2 b) {
      return a*b;  
    }

		/// <summary>
		/// multiply a vector with a scalar
		/// </summary>
		/// <param name="vec">vector to multiply with scalar</param>
		/// <param name="scalar">to multiply vector with</param>
		/// <returns>result of vec*scalar</returns>
		public static Vector2 operator*(Vector2 vec, float scalar) {
			return new Vector2( vec.x*scalar, vec.y*scalar);
		}

		/// <summary>
		/// indexer for matrix
		/// </summary>
		public float this [int index] {			
      get {
        switch(index) {
          case 0:
            return x;
          case 1:
            return y;
          default:
            throw new IndexOutOfRangeException("Invalid vector index!");
        }
      }
      set {			
        switch(index) {
          case 0:
            x = value;
            break;
          case 1:
            y = value;
            break;
          default:
            throw new IndexOutOfRangeException("Invalid vector index!");
        }
      }
		}

    /// <summary>
    /// convert from Vector2 to Vector3
    /// </summary>
    /// <returns>returns vector3 where x,y = vec2.x,y and z = 0</returns>
    public Vector3 ToVector3() {
      return new Vector3(this);
    }


    /// <summary>
    /// rotates a given vector
    /// </summary>
    /// <param name="vec">vector to rotate</param>
    /// <param name="angle">angle to use for roation</param>
    /// <returns>the rotated vector</returns>
    public static Vector2 Rotate(Vector2 vec, float angle) {
      float cos = Math.Trigonometry.Cos(angle);
      float sin = Math.Trigonometry.Sin(angle);
      return new Vector2( vec.x * cos + vec.y * sin, 
                         -vec.x * sin + vec.y * cos);
    }

    /// <summary>
    /// multiplies two vectors elementwise
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <returns></returns>
    public static Vector2 MultiplyElements(Vector2 a, Vector2 b) {
      return new Vector2(a.x * b.x, a.y * b.y);
    }

    /// <summary>
    /// multiplies two vectors elementwise
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <returns></returns>
    public static Vector2 DivideElements(Vector2 a, Vector2 b) {
      return new Vector2(a.x / b.x, a.y / b.y);
    }

    /// <summary>
    /// compares two vectors if they are equal
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <returns>true if the two vectors are equal</returns>
    public static bool operator==(Vector2 a, Vector2 b) {
      return (a.x == b.x && a.y == b.y);
    }

    
    /// <summary>
    /// compares two vectors if they are equal
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <returns>true if the two vectors are equal</returns>
    public static bool operator!=(Vector2 a, Vector2 b) {
      return (a.x != b.x || a.y != b.y);
    }

    /// <summary>
    /// calculats the maximum of the two vectors
    /// </summary>
    /// <param name="a">first vector</param>
    /// <param name="b">second vector</param>
    /// <returns></returns>
    public static Vector2 Max(Vector2 a, Vector2 b) {
      return new Vector2( System.Math.Max(a.x, b.x), System.Math.Max(a.y, b.y));
    }

    /// <summary>
    /// Explicit conversion.
    /// </summary>
    /// <param name="size">The variable to convert.</param>
    /// <returns>The Vector2 object.</returns>
    public static explicit operator Vector2(System.Drawing.Size size) {
      return new Vector2(size.Width, size.Height);
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}
