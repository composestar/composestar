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

namespace Purple.Math
{
	//=================================================================
	/// <summary>
	/// Matrix structure 4*4	
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	///   <para>Last Update: 0.5</para>
	/// </remarks>
	//=================================================================
	public struct Quaternion
	{
		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------
		// This stores the 4D values for the quaternion
		float x, y, z, w;
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Properties
		//---------------------------------------------------------------
		/// <summary>
		/// x - value
		/// </summary>
		public float X {
			get {
				return x;
			}
			set {
				x = value;
			}
		}

		/// <summary>
		/// y - value
		/// </summary>
		public float Y {
			get {
				return y;
			}
			set {
				y = value;
			}
		}

		/// <summary>
		/// z - value
		/// </summary>
		public float Z {
			get {
				return z;
			}
			set {
				z = value;
			}
		}

		/// <summary>
		/// w - value
		/// </summary>
		public float W {
			get {
				return w;
			}
			set {
				w = value;
			}
		}

    /// <summary>
    /// Returns a quaternion where all elements are zero.
    /// </summary>
    public static Quaternion Zero {
      get {
        return new Quaternion(0,0,0,0);
      }
    }

		/// <summary>
		/// converts quaternion to matrix
		/// </summary>
		public Matrix4 Matrix4 {
			get {
				Matrix4 m = Matrix4.Zero;
				
				// First column
				m.A1 = 1.0f - 2.0f * ( y * y + z * z );  
				m.A2 = 2.0f * ( x * y + w * z );  
				m.A3 = 2.0f * ( x * z - w * y );  
				m.A4 = 0.0f;  

				// Second column
				m.B1 = 2.0f * ( x * y - w * z );  
				m.B2 = 1.0f - 2.0f * ( x * x + z * z );  
				m.B3 = 2.0f * ( y * z + w * x );  
				m.B4 = 0.0f;  

				// Third column
				m.C1 = 2.0f * ( x * z + w * y );  
				m.C2 = 2.0f * ( y * z - w * x );  
				m.C3 = 1.0f - 2.0f * ( x * x + y * y );  
				m.C4 = 0.0f;  

				// Fourth column
				m.D1 = 0;  
				m.D2 = 0;  
				m.D3 = 0;  
				m.D4 = 1.0f;

				return m;
			}			
		}

    /// <summary>
    /// converts quaternion to matrix
    /// </summary>
    public Matrix3 Matrix {
      get {
        Matrix3 m = new Matrix3();
				
        // First column
        m.A1 = 1.0f - 2.0f * ( y * y + z * z );  
        m.A2 = 2.0f * ( x * y + w * z );  
        m.A3 = 2.0f * ( x * z - w * y );  

        // Second column
        m.B1 = 2.0f * ( x * y - w * z );  
        m.B2 = 1.0f - 2.0f * ( x * x + z * z );  
        m.B3 = 2.0f * ( y * z + w * x );   

        // Third column
        m.C1 = 2.0f * ( x * z + w * y );  
        m.C2 = 2.0f * ( y * z - w * x );  
        m.C3 = 1.0f - 2.0f * ( x * x + y * y );  

        return m;
      }			
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------
		/// <summary>
		/// standard constructor
		/// </summary>
		/// <param name="x">x-value</param>
		/// <param name="y">y-value</param>
		/// <param name="z">z-value</param>
		/// <param name="w">w-value</param>
		public Quaternion(float x, float y, float z, float w)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = w;
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------
    /// <summary>
    /// Calculates the dot product of two quaternions.
    /// </summary>
    /// <param name="b">The second quaternion to use for calcualting the dot product.</param>
    /// <returns>The dot product of two quaternions.</returns>
    public float Dot(Quaternion b) {
      return (x * b.x) + (y * b.y) + (z * b.z) + (w * b.w);
    }

    /// <summary>
    /// Calculates the magnitude of a quaternion.
    /// </summary>
    /// <param name="q">The quaternion to calculate magnitude for.</param>
    /// <returns>The magnitude of a quaternion.</returns>
    public static float Length(Quaternion  q) {
      return Math.Basic.Sqrt( q.Dot(q));
    }

    /// <summary>
    /// Multiplies a quaternion with a scalar.
    /// </summary>
    /// <param name="s">The scalar.</param>
    /// <param name="q">The quaternion to multiply.</param>
    /// <returns>The result of multiplying a quaternion with a scalar.</returns>
    public static Quaternion operator*(Quaternion q, float s) {
      return new Quaternion( s*q.x, s*q.y, s*q.z, s*q.w );
    }

      /// <summary>
      /// Multiplies a quaternion with a scalar.
      /// </summary>
      /// <param name="s">The scalar.</param>
    /// <param name="q">The quaternion to multiply.</param>
    /// <returns>The result of multiplying a quaternion with a scalar.</returns>
    public static Quaternion operator*(float s, Quaternion q) {
      return new Quaternion( s*q.x, s*q.y, s*q.z, s*q.w );
    }

    /// <summary>
    /// Calculates the sum of two quaternions.
    /// </summary>
    /// <param name="a">The first quaternion.</param>
    /// <param name="b">The second quaternion.</param>
    /// <returns>The sum of both quaternions.</returns>
    public static Quaternion operator+(Quaternion a, Quaternion b) {
      return new Quaternion( a.x+b.x, a.y+b.y, a.z+b.z, a.w+b.w );
    }

    /// <summary>
    /// Subtracts two quaternions.
    /// </summary>
    /// <param name="a">The quaternion to subtract from.</param>
    /// <param name="b">The quaternion to subtract.</param>
    /// <returns>Quaternion b subtracted from a.</returns>
    public static Quaternion operator-(Quaternion a, Quaternion b) {
      return new Quaternion( a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w );
    }

    /// <summary>
    /// Returns the negative quaternion.
    /// </summary>
    /// <param name="a">The quaternion to calculate the negative quaternion from.</param>
    /// <returns>The negative quaternion.</returns>
    public static Quaternion operator-(Quaternion a) {
      return new Quaternion( -a.x, -a.y, -a.z, -a.w);
    }

    /// <summary>
    /// Calculates the conjugate of a quaternion.
    /// </summary>
    /// <param name="q">The quaternion to calculate the inverse from.</param>
    /// <returns>The conjugate of the current quaternion.</returns>
    public static Quaternion Conjugate(Quaternion q) {
      return new Quaternion( -q.x, -q.y, -q.z, q.w);
    }

    /// <summary>
    /// Calculates the norm of a quaternion.
    /// </summary>
    /// <param name="q">The quaternion to calculate norm for.</param>
    /// <returns>The norm of the quaternion.</returns>
    public static float Norm(Quaternion q) {
      return q.x * q.x + q.y*q.y + q.z*q.z + q.w*q.w;
    }

    /// <summary>
    /// Divides a quaternion by a scalar.
    /// </summary>
    /// <param name="a">Quaternion to divide.</param>
    /// <param name="scalar">The scalar to divide the quaternion with.</param>
    /// <returns>The quaternion divided by the scalar.</returns>
    public static Quaternion operator/(Quaternion a, float scalar) {
      float invScalar = 1.0f / scalar;
      return new Quaternion(a.x * invScalar, a.y + invScalar, a.z * invScalar,a.w * invScalar);
    }

    /// <summary>
    /// Caluclates the inverse of a certain quaternion.
    /// </summary>
    /// <param name="q">The quaternion to calculate the inverse from.</param>
    /// <returns>The inverse of the quaternion.</returns>
    public static Quaternion Inverse(Quaternion q) {
      return Conjugate(q)/Norm(q);
    }

    /// <summary>
    /// Divide the first quaternion by the second one.
    /// </summary>
    /// <param name="a">The quaternion to divide.</param>
    /// <param name="b">The quaternion to divide a with.</param>
    /// <returns>A divided by b.</returns>
    public static Quaternion operator/(Quaternion a, Quaternion b) {
      return a*Inverse(b);
    }

    /// <summary>
    /// Multiplies two quaternions.
    /// </summary>
    /// <param name="a">The quaternion that is multiplied by b.</param>
    /// <param name="b">The quaternion to multiply a with.</param>
    /// <returns>A multiplied with B.</returns>
    public static Quaternion operator*(Quaternion a, Quaternion b) {
      return new Quaternion( 
         a.x * b.w + a.y * b.z - a.z * b.y + a.w * b.x,
        -a.x * b.z + a.y * b.w + a.z * b.x + a.w * b.y,
         a.x * b.y - a.y * b.x + a.z * b.w + a.w * b.z,
        -a.x * b.x - a.y * b.y - a.z * b.z + a.w * b.w );
    }

    /// <summary>
    /// Normalizes the current quaternion.
    /// </summary>
    public static Quaternion Normalize(Quaternion q) {
      float len = q.x*q.x + q.y*q.y + q.z*q.z + q.w*q.w;
      float factor = 1.0f/ Math.Basic.Sqrt(len);
      return new Quaternion( q.x*factor, q.y*factor, q.z*factor, q.w*factor);
    }

    /// <summary>
    /// Calculates the logarithm of a quaternion.
    /// </summary>
    /// <param name="q">The quaternion to calculate logarithm for.</param>
    /// <returns>The logarithm of a quaternion.</returns>
    public static Quaternion Log(Quaternion q) {
      float a = Math.Trigonometry.Acos(q.w);
      float sina = Math.Trigonometry.Sin(a);
      
      if (sina > 0)
        return new Quaternion( a*q.X / sina, a*q.Y/sina, a*q.Z/sina, 0.0f);
      return new Quaternion(q.X, q.Y, q.Z, 0.0f);
    } 

    /// <summary>
    /// Calculates e raised by a quaternion.
    /// </summary>
    /// <param name="q">The quaternion to use.</param>
    /// <returns>E raised by a quaternion.</returns>
    public static Quaternion Exp(Quaternion q) {
      float a = Basic.Sqrt(q.x*q.x + q.y*q.y + q.z*q.z);
      float sina = Trigonometry.Sin(a);
      float cosa = Trigonometry.Cos(a);

      if (a > 0 )
        return new Quaternion( sina*q.x / a, sina*q.y / a, sina*q.z /a, cosa);
      return new Quaternion( q.x, q.y, q.z, cosa);
    }

    /// <summary>
    /// Linear interpolation between two quaternions.
    /// </summary>
    /// <param name="a">First quaternion.</param>
    /// <param name="b">Second quaternion.</param>
    /// <param name="t">Time [0..1].</param>
    /// <returns>Interpolated quaternion.</returns>
    public static Quaternion Lerp(Quaternion a, Quaternion b, float t) {
      return Normalize(a + t*(a-b));
    }

    /// <summary>
    /// Spherical interpolation between two quaternions.
    /// </summary>
    /// <param name="a">First quaternion.</param>
    /// <param name="b">Second quaternion.</param>
    /// <param name="t">Time [0..1].</param>
    /// <returns>Interpolated quaternion.</returns>
    public static Quaternion Slerp(Quaternion a, Quaternion b, float t) {			

      float c0, c1;
      float cos = a.Dot(b);
      float sign = 1;
      if (cos < 0.0f) {
        cos = -cos;
        sign = -1.0f;
      }

      if (cos < 1.0f - 1e-3f) {
        float angle = Math.Trigonometry.Acos(cos);
        //float invSin = 1.0f/Math.Trigonometry.Sin(angle);
        float invSin = 1.0f / (Basic.Sqrt( 1.0f - cos*cos));
        c0 = Math.Trigonometry.Sin( (1.0f - t)*angle) * invSin;
        c1 = Math.Trigonometry.Sin( t*angle ) * invSin;
      } else {
        // If a is nearly the same as b we just linearly interpolate
        c0 = 1.0f - t;
        c1 = t;
      }

      Quaternion q = c0*a + (sign*c1)*b;
      return Normalize(q);
    }

    /// <summary>
    /// Spherical cubic interpolation between a and b.
    /// </summary>
    /// <param name="a">The start quaternion.</param>
    /// <param name="b">The end quaternion.</param>
    /// <param name="ta">The tangent for point a. Can be calculated by Spline.</param>
    /// <param name="tb">The tangent for point b. Can be calculated by Spline.</param>
    /// <param name="t">The interpolation time [0..1].</param>
    /// <returns>The interpolated quaternion.</returns>
    public static Quaternion Squad(Quaternion a, Quaternion b, Quaternion ta, Quaternion tb, float t) {
      float slerpT = 2.0f*t*(1.0f-t);
      Quaternion p = Slerp(a, b, t);
      Quaternion q = Slerp(ta, tb, t);
      return Slerp(p, q, slerpT);
    }

    /// <summary>
    /// Spherical cubic interpolation between a and b.
    /// </summary>
    /// <remarks>Simple usage but slow. It is recommended to  </remarks>
    /// <param name="prev">The quaternion before a.</param>
    /// <param name="a">The start quaternion.</param>
    /// <param name="b">The end quaternion.</param>
    /// <param name="post">The quaternion after b.</param>
    /// <param name="t">The time [0..1].</param>
    /// <returns>The interpolated quaternion.</returns>
    public static Quaternion SimpleSquad(Quaternion prev, Quaternion a, 
      Quaternion b, Quaternion post, float t) {

      if (prev.Dot(a) < 0)
        a = -a;
      if (a.Dot(b) < 0)
        b = -b;
      if (b.Dot(post) < 0)
        post = -post;

      Quaternion ta = Spline(prev, a, b);
      Quaternion tb = Spline(a, b, post);

      return Squad(a, b, ta, tb, t);
    }

    /// <summary>
    /// Caclulates a control point to be used in spline interpolation.
    /// </summary>
    /// <param name="pre">The quaternion before the current quaternion.</param>
    /// <param name="q">The quaternion to calculate control point for.</param>
    /// <param name="post">The quaternion after the current quaternion.</param>
    /// <returns>The control point calculated by the three quaternions.</returns>
    public static Quaternion Spline(Quaternion pre, Quaternion q, Quaternion post) {
      Quaternion cj = Conjugate(q);
      Quaternion e = q * Exp( (Log(cj*pre) + Log(cj*post)) * -0.25f );    
      return e;
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}
}