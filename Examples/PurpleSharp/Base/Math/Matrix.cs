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
//
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
// *****************************************************************************
using System;
using Purple.Serialization;

namespace Purple.Math
{
	//=================================================================
	/// <summary>
	/// Matrix structure 4*4	
	/// </summary>
	/// <remarks>
	///   <para>Author: Markus Wöß</para>
	///   <para>Since: 0.1</para>  
	///   <para>Last change: 0.3</para>
	/// </remarks>
	//=================================================================
  [System.ComponentModel.TypeConverter( typeof(System.ComponentModel.ExpandableObjectConverter) )]
	public struct Matrix4
	{
		//---------------------------------------------------------------
		#region Variables
		//---------------------------------------------------------------
		/// <summary>first row</summary>
		[Serialize(true)]
		public float A1, A2, A3, A4;
		/// <summary>second row</summary>
		[Serialize(true)]
    public float B1, B2, B3, B4;
		/// <summary>third row</summary>
		[Serialize(true)]
    public float C1, C2, C3, C4;
		/// <summary>forth row</summary>
		[Serialize(true)]
    public float D1, D2, D3, D4;
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Properties
		//---------------------------------------------------------------
		/// <summary>
		/// returns the identity matrix
		/// </summary>
		public static Matrix4 Identity {
			get {			
				Matrix4 m = Matrix4.Zero;
				m.A1 = m.B2 = m.C3 = m.D4 = 1.0f;
				return m;
			}
		}

		/// <summary>
		/// returns a matrix filled with 0.0f
		/// </summary>
		public static Matrix4 Zero {
			get {
				return new Matrix4();
			}
		}		

    /// <summary>
    /// returns/sets the first column of the matrix
    /// </summary>
    public Vector4 Column1 {
      get {
        return new Vector4(A1, B1, C1, D1);
      }
      set {
        A1 = value.X;
        B1 = value.Y;
        C1 = value.Z;
        D1 = value.W;
      }
    }

    /// <summary>
    /// returns/sets the second column of the matrix
    /// </summary>
    public Vector4 Column2 {
      get {
        return new Vector4(A2, B2, C2, D2);
      }
      set {
        A2 = value.X;
        B2 = value.Y;
        C2 = value.Z;
        D2 = value.W;
      }
    }

    /// <summary>
    /// returns/sets the third column of the matrix
    /// </summary>
    public Vector4 Column3 {
      get {
        return new Vector4(A3, B3, C3, D3);
      }
      set {
        A3 = value.X;
        B3 = value.Y;
        C3 = value.Z;
        D3 = value.W;
      }
    }

    /// <summary>
    /// returns/sets the fourth column of the matrix
    /// </summary>
    public Vector4 Column4 {
      get {
        return new Vector4(A4, B4, C4, D4);
      }
      set {
        A4 = value.X;
        B4 = value.Y;
        C4 = value.Z;
        D4 = value.W;
      }
    }

		/// <summary>
		/// returns the lookAt vector of the matrix
		/// </summary>
		public Vector3 LookAtVector {
			get {
				return new Vector3( A3, B3, C3 );
			}
			set {
				A3 = value.X;
				B3 = value.Y;
				C3 = value.Z;
			}
		}

		/// <summary>
		/// returns the up vector of the matrix
		/// </summary>
		public Vector3 UpVector {
			get {
				return new Vector3( A2, B2, C2 );
			}
			set {
				A2 = value.X;
				B2= value.Y;
				C2 = value.Z;
			}
		}

		/// <summary>
		/// returns the orthogonal-vector of up and look
		/// </summary>
		public Vector3 RightVector {
			get {
				return new Vector3( A1, B1, C1 );
			}
			set {
				A1 = value.X;
				B1 = value.Y;
				C1 = value.Z;
			}
		}

		/// <summary>
		/// returns the translation vector of the matrix
		/// </summary>
		public Vector3 TranslationVector {
			get {
				return new Vector3( D1, D2, D3 );
			}
			set {
				D1 = value.X;
				D2 = value.Y;
				D3 = value.Z;
			}
		}

    /// <summary>
    /// converts current matrix to a matrix3 object
    /// </summary>
    /// <returns>converted matrix3 object</returns>
    public Matrix3 RotationMatrix {
      get {
        return new Matrix3( A1, A2, A3, B1, B2, B3, C1, C2, C3);
      }
      set {
        A1 = value.A1; B1 = value.B1; C1 = value.C1;
        A2 = value.A2; B2 = value.B2; C2 = value.C2;
        A3 = value.A3; B3 = value.B3; C3 = value.C3;
      }
    }

		/// <summary>
		/// converts matrix to quaternion
		/// </summary>
		public Quaternion Quaternion {
			get {
				float trace = A1 + B2 + C3;
				float scale = 0.0f;
				float x,y,z,w;

				// If the trace is greater than zero
				if(trace > 0) {
					scale = Purple.Math.Basic.Sqrt( trace + 1.0f );
          float t = 0.5f / scale;

					// Calculate the x, y, x and w of the quaternion through the respective equation
					x = ( B3 - C2 ) * t;
					y = ( C1 - A3 ) * t;
					z = ( A2 - B1 ) * t;
					w = 0.5f * scale;
				}
				else {
					// If the first element of the diagonal is the greatest value
					if ( A1 > B2 && A1 > C3 ) {	
						// Find the scale according to the first element, and double that value
						scale  = Purple.Math.Basic.Sqrt( 1.0f + A1 - B2 - C3 );
            float t = 0.5f / scale;

						// Calculate the x, y, x and w of the quaternion through the respective equation
						x = 0.5f * scale;
						y = (A2 + B1 ) * t;
						z = (C1 + A3 ) * t;
						w = (B3 - C2 ) * t;	
					} 
						// Else if the second element of the diagonal is the greatest value
					else if ( B2 > C3 ) {
						// Find the scale according to the second element, and double that value
						scale  = Purple.Math.Basic.Sqrt( 1.0f + B2 - A1 - C3 );
            float t = 0.5f / scale;
			
						// Calculate the x, y, x and w of the quaternion through the respective equation
						x = (A2 + B1) * t;
						y = 0.5f * scale;
						z = (B3 + C2) * t;
						w = (C1 - A3) * t;
					} 
						// Else the third element of the diagonal is the greatest value
					else {	
						// Find the scale according to the third element, and double that value
						scale  = Purple.Math.Basic.Sqrt( 1.0f + C3 - A1 - B2 );
            float t = 0.5f / scale;

						// Calculate the x, y, x and w of the quaternion through the respective equation
						x = (C1 + A3) * t;
						y = (B3 + C2) * t;
						z = 0.5f * scale;
						w = (A2 - B1) * t;
					}
				}
				return new Quaternion(x,y,z,w);
			}
		}
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Initialisation
		//---------------------------------------------------------------
		/// <summary>
		/// constructor for fillin all elements
		/// </summary>
		/// <param name="a1"></param>
		/// <param name="b1"></param>
		/// <param name="c1"></param>
		/// <param name="d1"></param>
		/// <param name="a2"></param>
		/// <param name="b2"></param>
		/// <param name="c2"></param>
		/// <param name="d2"></param>
		/// <param name="a3"></param>
		/// <param name="b3"></param>
		/// <param name="c3"></param>
		/// <param name="d3"></param>
		/// <param name="a4"></param>
		/// <param name="b4"></param>
		/// <param name="c4"></param>
		/// <param name="d4"></param>
		public Matrix4(	float a1, float a2, float a3, float a4, 
										float b1, float b2, float b3, float b4,
										float c1, float c2, float c3, float c4,
										float d1, float d2, float d3, float d4) {
			A1 = a1; B1 = b1; C1 = c1; D1 = d1;
			A2 = a2; B2 = b2; C2 = c2; D2 = d2;
			A3 = a3; B3 = b3; C3 = c3; D3 = d3;
			A4 = a4; B4 = b4; C4 = c4; D4 = d4;
		}

		/// <summary>
		/// build matrix from vectors
		/// </summary>
		/// <param name="A">first row</param>
		/// <param name="B">second row</param>
		/// <param name="C">third row</param>
		/// <param name="D">fourth row</param>
		public Matrix4( Vector3 A, Vector3 B, Vector3 C, Vector3 D) {
			A1 = A.X; B1 = B.X; C1 = C.X; D1 = D.X;
			A2 = A.Y; B2 = B.Y; C2 = C.Y; D2 = D.Y;
			A3 = A.Z; B3 = B.Z; C3 = C.Z; D3 = D.Z;
			A4 = 0;		B4 = 0;   C4 = 0;   D4 = 1.0f;
		}

    /// <summary>
    /// creates a new matrix from a rotation matrix and a translation vector
    /// </summary>
    /// <param name="rot"></param>
    /// <param name="trans"></param>
    public Matrix4( Matrix3 rot, Vector3 trans) {
      A1 = rot.A1; B1 = rot.B1; C1 = rot.C1; D1 = trans.X;
      A2 = rot.A2; B2 = rot.B2; C2 = rot.C2; D2 = trans.Y;
      A3 = rot.A3; B3 = rot.B3; C3 = rot.C3; D3 = trans.Z;
      A4 = 0;      B4 = 0;      C4 = 0;      D4 = 1.0f;
    }

    /*private Matrix4() {
    }*/
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Methods
		//---------------------------------------------------------------
		/// <summary>
		/// retuns the rotation matrix around the x-axis
		/// </summary>
		/// <param name="alpha">angle in rad</param>
		/// <returns>rotation matrix around x-axis</returns>
		public static Matrix4 RotationX( float alpha ) {
			float cos = Math.Trigonometry.Cos(alpha);
			float sin = Math.Trigonometry.Sin(alpha);
			return new Matrix4(   1,   0,   0,   0,
													  0, cos, sin,   0,
														0,-sin, cos,   0,
														0,   0,   0,   1);
		}

		/// <summary>
		/// retuns the rotation matrix around the y-axis
		/// </summary>
		/// <param name="alpha">angle in rad</param>
		/// <returns>rotation matrix around y-axis</returns>
		public static Matrix4 RotationY( float alpha ) {
			float cos = Math.Trigonometry.Cos(alpha);
			float sin = Math.Trigonometry.Sin(alpha);
			return new Matrix4( cos,   0,-sin,   0,
														0,   1,   0,   0,
												  sin,   0, cos,   0,
														0,   0,   0,   1);
		}

		/// <summary>
		/// retuns the rotation matrix around the z-axis
		/// </summary>
		/// <param name="alpha">angle in rad</param>
		/// <returns>rotation matrix around z-axis</returns>
		public static Matrix4 RotationZ( float alpha ) {
			float cos = Math.Trigonometry.Cos(alpha);
			float sin = Math.Trigonometry.Sin(alpha);
			return new Matrix4( cos, sin,   0,   0,
												 -sin, cos,   0,   0,
													  0,   0,   1,   0,
														0,   0,   0,   1);
		}

		/// <summary>
		/// returns the rotation matrix for alpha, beta and gamma
		/// </summary>
		/// <param name="alpha">around x-Axis: yaw</param>
		/// <param name="beta">around y-Axis: pitch</param>
		/// <param name="gamma">around z-Axis: role</param>
		public static Matrix4 Rotation( float alpha, float beta, float gamma ) {	
			// some constants	
			float sa = Math.Trigonometry.Sin(alpha);
			float ca = Math.Trigonometry.Cos(alpha);
			float sb = Math.Trigonometry.Sin(beta);
			float cb = Math.Trigonometry.Cos(beta);
			float sg = Math.Trigonometry.Sin(gamma);
			float cg = Math.Trigonometry.Cos(gamma);
			float sbsa = sb * sa;
			float sbca = sb * ca;

			Matrix4 m = Matrix4.Zero;

			m.A1 = cg * cb;
			m.A2 = sg * cb;
			m.A3 = -sb;
			m.A4 = 0;
			m.B1 = cg * sbsa - sg * ca;
			m.B2 = sg * sbsa + cg * ca;
			m.B3 = cb * sa;
			m.B4 = 0;
			m.C1 = cg * sbca + sg * sa;
			m.C2 = sg * sbca - cg * sa;
			m.C3 = cb * ca;
			m.C4 = 0;
			m.D1 = 0;
			m.D2 = 0;
			m.D3 = 0;
			m.D4 = 1.0f;

			return m;
		}				
		
		/// <summary>
		/// build translation matrix
		/// </summary>
		/// <param name="vec">translation vector to use</param>
		/// <returns>translation matrix</returns>
		public static Matrix4 Translation( Vector3 vec ) {
			return new Matrix4( Vector3.Right, Vector3.Up, Vector3.LookAt, vec);
		}

    /// <summary>
    /// Builds a translation matrix.
    /// </summary>
    /// <param name="x">The x component of the translation.</param>
    /// <param name="y">The y component of the translation.</param>
    /// <param name="z">The z component of the translation.</param>
    /// <returns>A translation matrix.</returns>
    public static Matrix4 Translation( float x, float y, float z ) {
      return Matrix4.Translation( new Vector3(x, y, z) );
    }

		/// <summary>
		/// builds scale matrix
		/// </summary>
		/// <param name="value">value to scale</param>
		/// <returns>scale matrix</returns>
		public static Matrix4 Scaling( float value ) {
			return Scaling( new Vector3( value, value, value ) );
		}

		/// <summary>
		/// builds scale matrix
		/// </summary>
		/// <param name="vec">value to scale</param>
		/// <returns>scale matrix</returns>
		public static Matrix4 Scaling( Vector3 vec) {
			Matrix4 m = Matrix4.Zero;
			m.A1 = vec.X;
			m.B2 = vec.Y;
			m.C3 = vec.Z;
			m.D4 = 1.0f;
			return m;
		}

		/// <summary>
		/// translates the matrix 
		/// </summary>
		/// <param name="vec">translation vector</param>
		public void Translate( Vector3 vec) {
		  D1 += vec.X;
			D2 += vec.Y;
			D3 += vec.Z;
		}

    /// <summary>
    /// Translates the current matrix.
    /// </summary>
    /// <param name="x">The x component of the translation.</param>
    /// <param name="y">The y component of the translation.</param>
    /// <param name="z">The z component of the translation.</param>
    public void Translate( float x, float y, float z) {
      D1 += x;
      D2 += y;
      D3 += z;
    }

		/// <summary>
		/// Builds a left-handed perspective projection matrix
		/// </summary>
		/// <param name="width">Width of the view-volume at the near view-plane</param>
		/// <param name="height">Height of the view-volume at the near view-plane.</param>
		/// <param name="near"> Z-value of the near view-plane</param>
		/// <param name="far">Z-value of the far view-plane</param>
		/// <returns>left-handed perspective projection matrix</returns>
		public static Matrix4 Perspective( float width, float height, float near, float far) {
      if (far == float.PositiveInfinity)
        return PerspectiveInfinity(width, height, near);
			return new Matrix4 (
													2*near/width,	0,									0,										0,
													0,						2*near/height,			0,										0,
													0,						0,		 far/(far-near),									  1,
													0,						0,near*far/(near-far),									  0);   
		}

    /// <summary>
    /// Builds a left-handed perspective projection matrix where the far plane is set to infinity.
    /// </summary>
    /// <param name="width">Width of the view-volume at the near view-plane</param>
    /// <param name="height">Height of the view-volume at the near view-plane.</param>
    /// <param name="near"> Z-value of the near view-plane</param>
    /// <returns>left-handed perspective projection matrix</returns>
    public static Matrix4 PerspectiveInfinity( float width, float height, float near) {
      float epsilon = 0.001f;
      return new Matrix4 (
        2*near/width,	0,									0,										0,
        0,						2*near/height,			0,										0,
        0,						0,		              1 - epsilon,				  1,
        0,						0,                near*(epsilon-1),			  0);   
    }

		/// <summary>
		/// Builds a left-handed perspective projection matrix based on a field of view (FOV).
		/// </summary>
		/// <param name="fovY">Field of view, in the y direction, in radians</param>
		/// <param name="ratio">Aspect ratio, defined as view space height divided by width</param>
		/// <param name="near">Z-value of the near view-plane</param>
		/// <param name="far">Z-value of the far view-plane</param>
		/// <returns>left-handed perspective projection matrix based on a field of view (FOV)</returns>
		public static Matrix4 PerspectiveFOV( float fovY, float ratio, float near, float far) {
      if (far == float.PositiveInfinity)
        return PerspectiveFOVInfinity( fovY, ratio, near );

			float h = Purple.Math.Trigonometry.Cot( fovY/2 );
			float w = h / ratio;

			return new Matrix4(		w,		0,											0,										 0,                           
														0,		h,											0,	 									 0,
														0,		0,  		 far/(far - near),										 1,
														0,		0, -near*far/(far - near),										 0);
		}

    /// <summary>
    /// Creates a left-handed orthogonal matrix.
    /// </summary>
    /// <param name="w">The width of the view volume.</param>
    /// <param name="h">The height of the view volume.</param>
    /// <param name="near">The near clipping plane.</param>
    /// <param name="far">The far clipping plane.</param>
    /// <returns>Orthogonal matrix.</returns>
    public static Matrix4 Orthogonal( float w, float h, float near, float far) {
      return new Matrix4( 2/w,		0,											0,										 0,                           
                            0,	2/h,											0,	 									 0,
                            0,		0,  		   1/(far - near),										 0,
                            0,		0,      near/(near - far),										 1);
    }

    /// <summary>
    /// Builds a left-handed perspective projection matrix based on a field of view (FOV) where the 
    /// far plane is set to infinity.
    /// </summary>
    /// <param name="fovY">Field of view, in the y direction, in radians</param>
    /// <param name="ratio">Aspect ratio, defined as view space height divided by width</param>
    /// <param name="near">Z-value of the near view-plane</param>
    /// <returns>left-handed perspective projection matrix based on a field of view (FOV)</returns>
    public static Matrix4 PerspectiveFOVInfinity( float fovY, float ratio, float near) {

      float epsilon = 0.001f;
      float h = Purple.Math.Trigonometry.Cot( fovY/2 );
      float w = h / ratio;

      return new Matrix4(		
        w,		0,											0,										 0,                           
        0,		h,											0,	 									 0,
        0,		0,  		                1 - epsilon,					 1,
        0,		0,                 near*(epsilon-1),					 0);
    }

		/// <summary>
		/// Builds a left-handed, look-at matrix.
		/// </summary>
		/// <param name="eye">vector that defines the eye point. This value is used in translation.</param>
		/// <param name="at">vector that defines the camera look-at target</param>
		/// <param name="up">vector that defines the current world's up, usually [0, 1, 0]. </param>
		/// <returns>left-handed, look-at matrix</returns>
		public static Matrix4 LookAt( Vector3 eye, Vector3 at, Vector3 up ) {
			Vector3 zAxis = Vector3.Unit( at - eye );
			Vector3 xAxis = Vector3.Unit( Vector3.Cross(up, zAxis) );
			Vector3 yAxis = Vector3.Cross( zAxis, xAxis );

			return new Matrix4(		xAxis.X,		xAxis.Y,		xAxis.Z,	 0,
														yAxis.X,		yAxis.Y,		yAxis.Z,	 0,
														zAxis.X,		zAxis.Y,		zAxis.Z,	 0,
												 -xAxis*eye, -yAxis*eye, -zAxis*eye,	 1);
			
		}

		/// <summary>
		/// builds a rotation matrix for a given vector and an angle
		/// </summary>
		/// <param name="vec">to rotate around</param>
		/// <param name="angle">rotation angle</param>
		/// <returns>rotation matrix</returns>
		public static Matrix4 Rotation( Vector3 vec, float angle ) {
			float c = Math.Trigonometry.Cos(angle);
			float s = Math.Trigonometry.Sin(angle);
			float t = 1-c;
			Matrix4 m = 
					new Matrix4(		t*vec.X*vec.X + c, t*vec.X*vec.Y + s*vec.Z, t*vec.X*vec.Z + s*vec.Y, 0,
													t*vec.X*vec.Y - s*vec.Z, t*vec.Y*vec.Y + c, t*vec.Y*vec.Z + s*vec.X, 0,
													t*vec.X*vec.Z + s*vec.Y, t*vec.Y*vec.Z - s*vec.X,				t*vec.Z*vec.Z + c, 0,
																								0,											 0,												0, 1);	
			return Matrix4.Transpose(m);
		}

		/// <summary>
		/// Spherical linear interpolation between to matrices.
		/// </summary>
		/// <param name="a">First matrix.</param>
		/// <param name="b">Second matrix.</param>
		/// <param name="time">Interpolation time [0..1].</param>
		/// <returns>Interpolated matrix.</returns>
		public static Matrix4 Slerp( Matrix4 a, Matrix4 b, float time) {

			Quaternion qa = a.Quaternion;
			Quaternion qb = b.Quaternion;

    	Quaternion interpolated = Quaternion.Slerp(qa, qb, time);			

			Matrix4 result = interpolated.Matrix4;
			result.TranslationVector = Vector3.Lerp(a.TranslationVector, b.TranslationVector, time);
      return result;
		}

    /// <summary>
    /// Spherical quadratic interpolation between matrices.
    /// </summary>
    /// <remarks>The matrices are converted to quaternion for every invocation => it is recommended to use the Quaternion.Squad 
    /// method instead.</remarks>
    /// <param name="pre">One matrix before the actual interpolation matrices.</param>
    /// <param name="a">First matrix.</param>
    /// <param name="b">Second matrix.</param>
    /// <param name="post">One matrix after the actual interpolation matrices.</param>
    /// <param name="time">The interpolation time.</param>
    /// <returns>The interpolated matrix.</returns>
    public static Matrix4 Squad( Matrix4 pre, Matrix4 a, Matrix4 b, Matrix4 post, float time) {
      
      Quaternion qPre = pre.Quaternion;
      Quaternion qa = a.Quaternion;
      Quaternion qb = b.Quaternion;
      Quaternion qPost = post.Quaternion;

      Quaternion interpolated = //Quaternion.Slerp(qa, qb, time);
        Quaternion.SimpleSquad(qPre, qa, qb, qPost, time);		

      Matrix4 result = interpolated.Matrix4;

      result.TranslationVector = CRSpline.Interpolate(time, pre.TranslationVector,
        a.TranslationVector, b.TranslationVector, post.TranslationVector);
      return result;
    }

		/// <summary>
		/// returns the transposed matrix
		/// </summary>
		/// <param name="matrix">matrix to transpose</param>
		/// <returns>the transposed matrix</returns>
		public static Matrix4 Transpose( Matrix4 matrix) {
			Matrix4 m = Matrix4.Zero;
			m.A1 = matrix.A1;	m.A2 = matrix.B1;	m.A3 = matrix.C1;	m.A4 = matrix.D1;
			m.B1 = matrix.A2;	m.B2 = matrix.B2;	m.B3 = matrix.C2;	m.B4 = matrix.D2;
			m.C1 = matrix.A3;	m.C2 = matrix.B3;	m.C3 = matrix.C3;	m.C4 = matrix.D3;
			m.D1 = matrix.A4;	m.D2 = matrix.B4;	m.D3 = matrix.C4;	m.D4 = matrix.D4;
			return m;
		}

    /// <summary>
    /// get 3x3 matrix from 4x4 matrix without specified column and row
    /// </summary>
    /// <param name="source">source matrix (4x4)</param>
    /// <param name="column">column to remove</param>
    /// <param name="row">row to remove</param>
    /// <returns>3x3 matrix from 4x4 matrix without specified column and row</returns>
    public static Matrix3 Minor( Matrix4 source, int column, int row) {
      int r = 0;
      Matrix3 result = new Matrix3();
      for (int iRow=0; iRow<4; iRow++) {
        int c = 0;
        if (iRow != row) {
          for (int iColumn=0; iColumn<4; iColumn++) {
            if (iColumn != column) {
              result[c, r] = source[iColumn, iRow];
              c++;
            }
          }
          r++;
        }
      }
      return result;
    }

    /// <summary>
    /// returns the adjoint matrix
    /// </summary>
    /// <param name="source">matrix to calculate adjoint matrix from</param>
    /// <returns></returns>
    public static Matrix4 Adjoint( Matrix4 source ) {
      Matrix4 result = Matrix4.Zero;
      for (int iRow = 0; iRow < 4; iRow++)
        for (int iCol = 0; iCol < 4; iCol++) {
          if (((iCol+iRow) % 2) == 0)
            result[iCol, iRow] = Matrix4.Minor(source, iRow, iCol).Det();
          else
            result[iCol, iRow] = -Matrix4.Minor(source, iRow, iCol).Det();
        }
      return result;
    }

    /// <summary>
    /// calcluates the deterimant of the matrix
    /// </summary>
    /// <returns></returns>
    public float Det() {
      /*float det = 0.0f;
      for (int iRow = 0; iRow < 4; iRow++)
        if ((iRow % 2) == 0)
          det += this[0, iRow] * Matrix4.Minor(this, 0, iRow).Det();
        else
          det -= this[0, iRow] * Matrix4.Minor(this, 0, iRow).Det();
      return det;*/
      return 
        A4 * B3 * C2 * D1-A3 * B4 * C2 * D1 - A4 * B2 * C3 * D1 + A2 * B4 * C3 * D1+
        A3 * B2 * C4 * D1-A2 * B3 * C4 * D1 - A4 * B3 * C1 * D2 + A3 * B4 * C1 * D2+
        A4 * B1 * C3 * D2-A1 * B4 * C3 * D2 - A3 * B1 * C4 * D2 + A1 * B3 * C4 * D2+
        A4 * B2 * C1 * D3-A2 * B4 * C1 * D3 - A4 * B1 * C2 * D3 + A1 * B4 * C2 * D3+
        A2 * B1 * C4 * D3-A1 * B2 * C4 * D3 - A3 * B2 * C1 * D4 + A2 * B3 * C1 * D4+
        A3 * B1 * C2 * D4-A1 * B3 * C2 * D4 - A2 * B1 * C3 * D4 + A1 * B2 * C3 * D4;
    }

    /// <summary>
    /// calculates the inverse matrix of a source matrix
    /// </summary>
    /// <param name="m">to calculate inverse matrix from</param>
    /// <returns>inverse matrix</returns>
    public static Matrix4 Invert( Matrix4 m) {
      // not that efficient at the moment - however, who cares?
      //Matrix4 inverse = Matrix4.Adjoint(m) / m.Det();

      // Hmm perhaps I do care ;-) This is about 50% faster
      Matrix4 inverse = Matrix4.Zero;
      inverse.A1 = m.B3*m.C4*m.D2 - m.B4*m.C3*m.D2 + m.B4*m.C2*m.D3 - m.B2*m.C4*m.D3 - m.B3*m.C2*m.D4 + m.B2*m.C3*m.D4;
      inverse.A2 = m.A4*m.C3*m.D2 - m.A3*m.C4*m.D2 - m.A4*m.C2*m.D3 + m.A2*m.C4*m.D3 + m.A3*m.C2*m.D4 - m.A2*m.C3*m.D4;
      inverse.A3 = m.A3*m.B4*m.D2 - m.A4*m.B3*m.D2 + m.A4*m.B2*m.D3 - m.A2*m.B4*m.D3 - m.A3*m.B2*m.D4 + m.A2*m.B3*m.D4;
      inverse.A4 = m.A4*m.B3*m.C2 - m.A3*m.B4*m.C2 - m.A4*m.B2*m.C3 + m.A2*m.B4*m.C3 + m.A3*m.B2*m.C4 - m.A2*m.B3*m.C4;

      inverse.B1 = m.B4*m.C3*m.D1 - m.B3*m.C4*m.D1 - m.B4*m.C1*m.D3 + m.B1*m.C4*m.D3 + m.B3*m.C1*m.D4 - m.B1*m.C3*m.D4;
      inverse.B2 = m.A3*m.C4*m.D1 - m.A4*m.C3*m.D1 + m.A4*m.C1*m.D3 - m.A1*m.C4*m.D3 - m.A3*m.C1*m.D4 + m.A1*m.C3*m.D4;
      inverse.B3 = m.A4*m.B3*m.D1 - m.A3*m.B4*m.D1 - m.A4*m.B1*m.D3 + m.A1*m.B4*m.D3 + m.A3*m.B1*m.D4 - m.A1*m.B3*m.D4;
      inverse.B4 = m.A3*m.B4*m.C1 - m.A4*m.B3*m.C1 + m.A4*m.B1*m.C3 - m.A1*m.B4*m.C3 - m.A3*m.B1*m.C4 + m.A1*m.B3*m.C4;
      
      inverse.C1 = m.B2*m.C4*m.D1 - m.B4*m.C2*m.D1 + m.B4*m.C1*m.D2 - m.B1*m.C4*m.D2 - m.B2*m.C1*m.D4 + m.B1*m.C2*m.D4;    
      inverse.C2 = m.A4*m.C2*m.D1 - m.A2*m.C4*m.D1 - m.A4*m.C1*m.D2 + m.A1*m.C4*m.D2 + m.A2*m.C1*m.D4 - m.A1*m.C2*m.D4;    
      inverse.C3 = m.A2*m.B4*m.D1 - m.A4*m.B2*m.D1 + m.A4*m.B1*m.D2 - m.A1*m.B4*m.D2 - m.A2*m.B1*m.D4 + m.A1*m.B2*m.D4;    
      inverse.C4 = m.A4*m.B2*m.C1 - m.A2*m.B4*m.C1 - m.A4*m.B1*m.C2 + m.A1*m.B4*m.C2 + m.A2*m.B1*m.C4 - m.A1*m.B2*m.C4;
      
      inverse.D1 = m.B3*m.C2*m.D1 - m.B2*m.C3*m.D1 - m.B3*m.C1*m.D2 + m.B1*m.C3*m.D2 + m.B2*m.C1*m.D3 - m.B1*m.C2*m.D3;   
      inverse.D2 = m.A2*m.C3*m.D1 - m.A3*m.C2*m.D1 + m.A3*m.C1*m.D2 - m.A1*m.C3*m.D2 - m.A2*m.C1*m.D3 + m.A1*m.C2*m.D3;    
      inverse.D3 = m.A3*m.B2*m.D1 - m.A2*m.B3*m.D1 - m.A3*m.B1*m.D2 + m.A1*m.B3*m.D2 + m.A2*m.B1*m.D3 - m.A1*m.B2*m.D3;    
      inverse.D4 = m.A2*m.B3*m.C1 - m.A3*m.B2*m.C1 + m.A3*m.B1*m.C2 - m.A1*m.B3*m.C2 - m.A2*m.B1*m.C3 + m.A1*m.B2*m.C3;
      return inverse*(1/m.Det());
     }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------

		//---------------------------------------------------------------
		#region Operations
		//---------------------------------------------------------------
		/// <summary>
		/// add two matrices
		/// </summary>
		/// <param name="a">matrix a</param>
		/// <param name="b">matrix b</param>
		/// <returns>sum of the two matrices</returns>
		public static Matrix4 operator+(Matrix4 a, Matrix4 b) {
			return new Matrix4( a.A1 + b.A1, a.A2 + b.A2, a.A3 + b.A3, a.A4 + b.A4,
													a.B1 + b.B1, a.B2 + b.B2, a.B3 + b.B3, a.B4 + b.B4,
													a.C1 + b.C1, a.C2 + b.C2, a.C3 + b.C3, a.C4 + b.C4,
													a.D1 + b.D1, a.D2 + b.D2, a.D3 + b.D3, a.D4 + b.D4);													
		}

		/// <summary>
		/// indexer for matrix
		/// </summary>
		public float this [int column, int row] {
			get {
				return this[ column + row*4 ];
			}
			set {				
				this[ column + row*4 ] = value;
			}			
		}

		/// <summary>
		/// indexer for matrix
		/// </summary>
		public float this [int index] {			
      get {
        /*fixed( float *f = &this.A1 ) {
          return *(f+index);
        }*/

        switch(index) {
          case 0:
            return A1;
          case 1:
            return A2;
          case 2:
            return A3;
          case 3:
            return A4;
          case 4:
            return B1;
          case 5:
            return B2;
          case 6:
            return B3;
          case 7:
            return B4;
          case 8:
            return C1;
          case 9:
            return C2;
          case 10:
            return C3;
          case 11:
            return C4;
          case 12:
            return D1;
          case 13:
            return D2;
          case 14:
            return D3;
          case 15:
            return D4;
          default:
            throw new IndexOutOfRangeException("Invalid matrix index!");
        }
      }
      set {			
        /*fixed( float *f = &this.A1 ) {
          *(f+index) = value;
        }*/
        switch(index) {
          case 0:
            A1 = value;
            break;
          case 1:
            A2 = value;
            break;
          case 2:
            A3 = value;
            break;
          case 3:
            A4 = value;
            break;
          case 4:
            B1 = value;
            break;
          case 5:
            B2 = value;
            break;
          case 6:
            B3 = value;
            break;
          case 7:
            B4 = value;
            break;
          case 8:
            C1 = value;
            break;
          case 9:
            C2 = value;
            break;
          case 10:
            C3 = value;
            break;
          case 11:
            C4 = value;
            break;
          case 12:
            D1 = value;
            break;
          case 13:
            D2 = value;
            break;
          case 14:
            D3 = value;
            break;
          case 15:
            D4 = value;
            break;
          default:
            throw new IndexOutOfRangeException("Invalid matrix index!");
        }
			}			
		}

		/// <summary>
		/// multiplies two matrices
		/// </summary>
		/// <param name="a">first matrix</param>
		/// <param name="b">second matrix</param>
		/// <returns>result matrix</returns>
		public static Matrix4 operator*(Matrix4 a, Matrix4 b) {
			
      // Matrix4 result = Matrix4.Zero;	
			//for (int x=0; x<4; x++)
			//	for (int y=0; y<4; y++)
			//		for (int i=0; i<4; i++) 
			//			result[x,y] += a[i, y] * b[x, i];
      
      float A1 = a.A1*b.A1 + a.A2*b.B1 + a.A3*b.C1 + a.A4*b.D1;
      float A2 = a.A1*b.A2 + a.A2*b.B2 + a.A3*b.C2 + a.A4*b.D2;
      float A3 = a.A1*b.A3 + a.A2*b.B3 + a.A3*b.C3 + a.A4*b.D3;
      float A4 = a.A1*b.A4 + a.A2*b.B4 + a.A3*b.C4 + a.A4*b.D4;

      float B1 = a.B1*b.A1 + a.B2*b.B1 + a.B3*b.C1 + a.B4*b.D1;
      float B2 = a.B1*b.A2 + a.B2*b.B2 + a.B3*b.C2 + a.B4*b.D2;
      float B3 = a.B1*b.A3 + a.B2*b.B3 + a.B3*b.C3 + a.B4*b.D3;
      float B4 = a.B1*b.A4 + a.B2*b.B4 + a.B3*b.C4 + a.B4*b.D4;
      
      float C1 = a.C1*b.A1 + a.C2*b.B1 + a.C3*b.C1 + a.C4*b.D1;
      float C2 = a.C1*b.A2 + a.C2*b.B2 + a.C3*b.C2 + a.C4*b.D2;
      float C3 = a.C1*b.A3 + a.C2*b.B3 + a.C3*b.C3 + a.C4*b.D3;
      float C4 = a.C1*b.A4 + a.C2*b.B4 + a.C3*b.C4 + a.C4*b.D4;
      
      float D1 = a.D1*b.A1 + a.D2*b.B1 + a.D3*b.C1 + a.D4*b.D1;
      float D2 = a.D1*b.A2 + a.D2*b.B2 + a.D3*b.C2 + a.D4*b.D2;
      float D3 = a.D1*b.A3 + a.D2*b.B3 + a.D3*b.C3 + a.D4*b.D3;
      float D4 = a.D1*b.A4 + a.D2*b.B4 + a.D3*b.C4 + a.D4*b.D4;

			return new Matrix4(A1, A2, A3, A4, B1, B2, B3, B4, C1, C2, C3, C4, D1, D2, D3, D4);
		}

    /// <summary>
    /// multiplies a given matrix with a scalar
    /// </summary>
    /// <param name="source">matrix to multiply with scalar</param>
    /// <param name="scalar">to multiply matrix with</param>
    /// <returns>source*scalar</returns>
    public static Matrix4 operator*(Matrix4 source, float scalar) {
      return new Matrix4( 
        source.A1 * scalar, source.A2 * scalar, source.A3 * scalar, source.A4 * scalar,
        source.B1 * scalar, source.B2 * scalar, source.B3 * scalar, source.B4 * scalar,
        source.C1 * scalar, source.C2 * scalar, source.C3 * scalar, source.C4 * scalar,
        source.D1 * scalar, source.D2 * scalar, source.D3 * scalar, source.D4 * scalar);
    }

    /// <summary>
    /// divides a given matrix with a scalar
    /// </summary>
    /// <param name="source">matrix to divide with scalar</param>
    /// <param name="scalar">to divide matrix with</param>
    /// <returns>source/scalar</returns>
    public static Matrix4 operator/(Matrix4 source, float scalar) {
      return source * (1/scalar);
    }

    /// <summary>
    /// creates a matrix from an array
    /// </summary>
    /// <param name="values">array of 16 float elements</param>
    /// <returns>created matrix</returns>
    public static Matrix4 From(float[] values) {
      System.Diagnostics.Debug.Assert(values.Length == 16);
      return new Matrix4( 
        values[0], values[1], values[2], values[3],
        values[4], values[5], values[6], values[7],
        values[8], values[9], values[10], values[11],
        values[12], values[13], values[14], values[15]);
    }

    /// <summary>
    /// Returns an array of matrices.
    /// </summary>
    /// <param name="data">The data stream containing the matrices.</param>
    /// <param name="offset">The offset in the stream to start with.</param>
    /// <param name="count">The number of matrices to load.</param>
    /// <returns>The array of matrices.</returns>
    public static unsafe Matrix4[] From(byte[] data, int offset, int count) {
      Matrix4[] ret = new Matrix4[count];
      fixed ( byte* p = &data[offset] ) {
        float *floatPtr = (float *)(p);
        for (int i=0; i<count; i++) {
          ret[i] = new Matrix4( 
            floatPtr[0], floatPtr[1], floatPtr[2], floatPtr[3],
            floatPtr[4], floatPtr[5], floatPtr[6], floatPtr[7],
            floatPtr[8], floatPtr[9], floatPtr[10], floatPtr[11],
            floatPtr[12], floatPtr[13], floatPtr[14], floatPtr[15]);
          floatPtr += 16;
        }
      }
      return ret;
    }

    /// <summary>
    /// Creates a matrix from a byte array.
    /// </summary>
    /// <param name="bytes">Array of at least 64 byte elements.</param>
    /// <returns>Created matrix.</returns>
    public static Matrix4 From(byte[] bytes) {
      return From(bytes, 0);
    }

    /// <summary>
    /// Creates a matrix from a byte array.
    /// </summary>
    /// <param name="bytes">Array of at least 64 byte elements.</param>
    /// <param name="offset">The start offset in the buffer.</param>
    /// <returns>Created matrix.</returns>
    public unsafe static Matrix4 From(byte[] bytes, int offset) {
      System.Diagnostics.Debug.Assert(bytes.Length >= 64 + offset);
      fixed ( byte* p = &bytes[offset] ) {
        float *floatPtr = (float *)(p);
          return new Matrix4( 
            floatPtr[0], floatPtr[1], floatPtr[2], floatPtr[3],
            floatPtr[4], floatPtr[5], floatPtr[6], floatPtr[7],
            floatPtr[8], floatPtr[9], floatPtr[10], floatPtr[11],
            floatPtr[12], floatPtr[13], floatPtr[14], floatPtr[15]);
        }
    }

    /// <summary>
    /// creates a matrix from a Matrix3 object
    /// </summary>
    /// <param name="m">3x3 matrix</param>
    /// <returns>4x4 matrix</returns>
    public static Matrix4 From(Matrix3 m) {
      return new Matrix4(m.A1, m.A2, m.A3, 0, m.B1, m.B2, m.B3, 0, m.C1, m.C2, m.C3, 0, 0, 0, 0, 1);
    }
		//---------------------------------------------------------------
		#endregion
		//---------------------------------------------------------------
	}

  //=================================================================
  /// <summary>
  /// Matrix structure 3*3	
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  [System.ComponentModel.TypeConverter( typeof(System.ComponentModel.ExpandableObjectConverter) )]
  public struct Matrix3 {
    //---------------------------------------------------------------
		#region Variables
    //---------------------------------------------------------------
    /// <summary>first row</summary>
    [Serialize(true)]
    public float A1, A2, A3;
    /// <summary>second row</summary>
    [Serialize(true)]
    public float B1, B2, B3;
    /// <summary>third row</summary>
    [Serialize(true)]
    public float C1, C2, C3;
    //---------------------------------------------------------------
		#endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
		#region Properties
    //---------------------------------------------------------------
    /// <summary>
    /// returns the identity matrix
    /// </summary>
    public static Matrix3 Identity {
      get {			
        Matrix3 m = new Matrix3();
        m.A1 = m.B2 = m.C3 = 1.0f;
        return m;
      }
    }

    /// <summary>
    /// returns a matrix filled with 0.0f
    /// </summary>
    public static Matrix3 Zero {
      get {
        return new Matrix3();
      }
    }		

    /// <summary>
    /// returns/sets the first column of the matrix
    /// </summary>
    public Vector3 Column1 {
      get {
        return new Vector3(A1, B1, C1);
      }
      set {
        A1 = value.X;
        B1 = value.Y;
        C1 = value.Z;
      }
    }

    /// <summary>
    /// returns/sets the second column of the matrix
    /// </summary>
    public Vector3 Column2 {
      get {
        return new Vector3(A2, B2, C2);
      }
      set {
        A2 = value.X;
        B2 = value.Y;
        C2 = value.Z;
      }
    }

    /// <summary>
    /// returns/sets the third column of the matrix
    /// </summary>
    public Vector3 Column3 {
      get {
        return new Vector3(A3, B3, C3);
      }
      set {
        A3 = value.X;
        B3 = value.Y;
        C3 = value.Z;
      }
    }

    /// <summary>
    /// returns the lookAt vector of the matrix
    /// </summary>
    public Vector3 LookAtVector {
      get {
        return new Vector3( A3, B3, C3 );
      }
      set {
        A3 = value.X;
        B3 = value.Y;
        C3 = value.Z;
      }
    }

    /// <summary>
    /// returns the up vector of the matrix
    /// </summary>
    public Vector3 UpVector {
      get {
        return new Vector3( A2, B2, C2 );
      }
      set {
        A2 = value.X;
        B2= value.Y;
        C2 = value.Z;
      }
    }

    /// <summary>
    /// returns the orthogonal-vector of up and look
    /// </summary>
    public Vector3 RightVector {
      get {
        return new Vector3( A1, B1, C1 );
      }
      set {
        A1 = value.X;
        B1 = value.Y;
        C1 = value.Z;
      }
    }

    /// <summary>
    /// converts matrix to quaternion
    /// </summary>
    [System.ComponentModel.Browsable(false)]
    public Quaternion Quaternion {
      get {
        float diagonal = A1 + B2 + C3;
        float scale = 0.0f;
        float x,y,z,w;

        // If the diagonal is greater than zero
        if(diagonal > 0.00000001) {
          // Calculate the scale of the diagonal
          scale = Purple.Math.Basic.Sqrt(diagonal ) * 2;

          // Calculate the x, y, x and w of the quaternion through the respective equation
          x = ( B3 - C2 ) / scale;
          y = ( C1 - A3 ) / scale;
          z = ( A2 - B1 ) / scale;
          w = 0.25f * scale;
        }
        else {
          // If the first element of the diagonal is the greatest value
          if ( A1 > B2 && A1 > C3 ) {	
            // Find the scale according to the first element, and double that value
            scale  = Purple.Math.Basic.Sqrt( 1.0f + A1 - B2 - C3 ) * 2.0f;

            // Calculate the x, y, x and w of the quaternion through the respective equation
            x = 0.25f * scale;
            y = (A2 + B1 ) / scale;
            z = (C1 + A3 ) / scale;
            w = (B3 - C2 ) / scale;	
          } 
            // Else if the second element of the diagonal is the greatest value
          else if ( B2 > C3 ) {
            // Find the scale according to the second element, and double that value
            scale  = Purple.Math.Basic.Sqrt( 1.0f + B2 - A1 - C3 ) * 2.0f;
			
            // Calculate the x, y, x and w of the quaternion through the respective equation
            x = (A2 + B1) / scale;
            y = 0.25f * scale;
            z = (B3 + C2) / scale;
            w = (C1 - A3) / scale;
          } 
            // Else the third element of the diagonal is the greatest value
          else {	
            // Find the scale according to the third element, and double that value
            scale  = Purple.Math.Basic.Sqrt( 1.0f + C3 - A1 - B2 ) * 2.0f;

            // Calculate the x, y, x and w of the quaternion through the respective equation
            x = (C1 + A3) / scale;
            y = (B3 + C2) / scale;
            z = 0.25f * scale;
            w = (A2 - B1) / scale;
          }
        }
        return new Quaternion(x,y,z,w);
      }
    }
    //---------------------------------------------------------------
		#endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
		#region Initialisation
    //---------------------------------------------------------------
    /// <summary>
    /// constructor for fillin all elements
    /// </summary>
    /// <param name="a1"></param>
    /// <param name="a2"></param>
    /// <param name="a3"></param>
    /// <param name="b1"></param>
    /// <param name="b2"></param>
    /// <param name="b3"></param>
    /// <param name="c1"></param>
    /// <param name="c2"></param>
    /// <param name="c3"></param>
    public Matrix3(	float a1, float a2, float a3, 
      float b1, float b2, float b3,
      float c1, float c2, float c3) {
      A1 = a1; B1 = b1; C1 = c1;
      A2 = a2; B2 = b2; C2 = c2;
      A3 = a3; B3 = b3; C3 = c3;
    }

    /// <summary>
    /// build matrix from vectors
    /// </summary>
    /// <param name="A">first row</param>
    /// <param name="B">second row</param>
    /// <param name="C">third row</param>
    public Matrix3( Vector3 A, Vector3 B, Vector3 C) {
      A1 = A.X; B1 = B.X; C1 = C.X;
      A2 = A.Y; B2 = B.Y; C2 = C.Y;
      A3 = A.Z; B3 = B.Z; C3 = C.Z;
    }

    //---------------------------------------------------------------
		#endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
		#region Methods
    //---------------------------------------------------------------
    /// <summary>
    /// retuns the rotation matrix around the x-axis
    /// </summary>
    /// <param name="alpha">angle in rad</param>
    /// <returns>rotation matrix around x-axis</returns>
    public static Matrix3 RotationX( float alpha ) {
      float cos = Math.Trigonometry.Cos(alpha);
      float sin = Math.Trigonometry.Sin(alpha);
      return new Matrix3(   1,   0,   0,
        0, cos, sin,
        0,-sin, cos);
    }

    /// <summary>
    /// retuns the rotation matrix around the y-axis
    /// </summary>
    /// <param name="alpha">angle in rad</param>
    /// <returns>rotation matrix around y-axis</returns>
    public static Matrix3 RotationY( float alpha ) {
      float cos = Math.Trigonometry.Cos(alpha);
      float sin = Math.Trigonometry.Sin(alpha);
      return new Matrix3( cos,   0,-sin,
        0,   1,   0,
        sin,   0, cos);
    }

    /// <summary>
    /// retuns the rotation matrix around the z-axis
    /// </summary>
    /// <param name="alpha">angle in rad</param>
    /// <returns>rotation matrix around z-axis</returns>
    public static Matrix3 RotationZ( float alpha ) {
      float cos = Math.Trigonometry.Cos(alpha);
      float sin = Math.Trigonometry.Sin(alpha);
      return new Matrix3( cos, sin,   0,
        -sin, cos,   0,
        0,   0,   1);
    }

    /// <summary>
    /// returns the rotation matrix for alpha, beta and gamma
    /// </summary>
    /// <param name="alpha">around x-Axis: yaw</param>
    /// <param name="beta">around y-Axis: pitch</param>
    /// <param name="gamma">around z-Axis: role</param>
    public static Matrix3 Rotation( float alpha, float beta, float gamma ) {	
      // some constants	
      float sa = Math.Trigonometry.Sin(alpha);
      float ca = Math.Trigonometry.Cos(alpha);
      float sb = Math.Trigonometry.Sin(beta);
      float cb = Math.Trigonometry.Cos(beta);
      float sg = Math.Trigonometry.Sin(gamma);
      float cg = Math.Trigonometry.Cos(gamma);
      float sbsa = sb * sa;
      float sbca = sb * ca;

      Matrix3 m = new Matrix3();

      m.A1 = cg * cb;
      m.A2 = sg * cb;
      m.A3 = -sb;
      m.B1 = cg * sbsa - sg * ca;
      m.B2 = sg * sbsa + cg * ca;
      m.B3 = cb * sa;
      m.C1 = cg * sbca + sg * sa;
      m.C2 = sg * sbca - cg * sa;
      m.C3 = cb * ca;
      return m;
    }				

    /// <summary>
    /// builds scale matrix
    /// </summary>
    /// <param name="value">value to scale</param>
    /// <returns>scale matrix</returns>
    public static Matrix3 Scaling( float value ) {
      return Scaling( new Vector3( value, value, value ) );
    }

    /// <summary>
    /// builds scale matrix
    /// </summary>
    /// <param name="vec">value to scale</param>
    /// <returns>scale matrix</returns>
    public static Matrix3 Scaling( Vector3 vec) {
      Matrix3 m = new Matrix3();
      m.A1 = vec.X;
      m.B2 = vec.Y;
      m.C3 = vec.Z;
      return m;
    }

    /// <summary>
    /// Builds a left-handed, look-at matrix.
    /// </summary>
    /// <param name="eye">vector that defines the eye point. This value is used in translation.</param>
    /// <param name="at">vector that defines the camera look-at target</param>
    /// <param name="up">vector that defines the current world's up, usually [0, 1, 0]. </param>
    /// <returns>left-handed, look-at matrix</returns>
    public static Matrix3 LookAt( Vector3 eye, Vector3 at, Vector3 up ) {
      Vector3 zAxis = Vector3.Unit( at - eye );
      Vector3 xAxis = Vector3.Unit( Vector3.Cross(up, zAxis) );
      Vector3 yAxis = Vector3.Cross( zAxis, xAxis );

      return new Matrix3(		xAxis.X,		xAxis.Y,		xAxis.Z,
        yAxis.X,		yAxis.Y,		yAxis.Z,
        zAxis.X,		zAxis.Y,		zAxis.Z);
			
    }

    /// <summary>
    /// builds a rotation matrix for a given vector and an angle
    /// </summary>
    /// <param name="vec">to rotate around</param>
    /// <param name="angle">rotation angle</param>
    /// <returns>rotation matrix</returns>
    public static Matrix3 Rotation( Vector3 vec, float angle ) {
      float c = Math.Trigonometry.Cos(angle);
      float s = Math.Trigonometry.Sin(angle);
      float t = 1-c;
      Matrix3 m = 
        new Matrix3(		t*vec.X*vec.X + c, t*vec.X*vec.Y - s*vec.Z, t*vec.X*vec.Z + s*vec.Y,
        t*vec.X*vec.Y + s*vec.Z,			 t*vec.Y*vec.Y + c, t*vec.Y*vec.Z - s*vec.X,
        t*vec.X*vec.Z - s*vec.Y, t*vec.Y*vec.Z + s*vec.X,				t*vec.Z*vec.Z + c);	
      return Matrix3.Transpose(m);
    }

   
    /// <summary>
    /// returns the transposed matrix
    /// </summary>
    /// <param name="matrix">matrix to transpose</param>
    /// <returns>the transposed matrix</returns>
    public static Matrix3 Transpose( Matrix3 matrix) {
      Matrix3 m = new Matrix3();
      m.A1 = matrix.A1;	m.A2 = matrix.B1;	m.A3 = matrix.C1;	
      m.B1 = matrix.A2;	m.B2 = matrix.B2;	m.B3 = matrix.C2;
      m.C1 = matrix.A3;	m.C2 = matrix.B3;	m.C3 = matrix.C3;
      return m;
    }

    /// <summary>
    /// calcluates the deterimant of the matrix
    /// </summary>
    /// <returns></returns>
    public float Det() {
      // rule of Sarrus
      return A1*B2*C3 + A2*B3*C1 + A3*B1*C2 -
        A3*B2*C1 - A1*B3*C2 - A2*B1*C3;
    }

    /// <summary>
    /// spherical interpolation between to matrices
    /// </summary>
    /// <param name="a">first matrix</param>
    /// <param name="b">second matrix</param>
    /// <param name="time">interpolation time [0..1]</param>
    /// <returns>interpolated matrix</returns>
    public static Matrix3 Slerp( Matrix3 a, Matrix3 b, float time) {

      Quaternion qa = a.Quaternion;
      Quaternion qb = b.Quaternion;

      Quaternion interpolated = Quaternion.Slerp(qa, qb, time);			

      Matrix3 result = interpolated.Matrix;
      return result;
    }
    //---------------------------------------------------------------
		#endregion
    //---------------------------------------------------------------

    //---------------------------------------------------------------
		#region Operations
    //---------------------------------------------------------------
    /// <summary>
    /// add two matrices
    /// </summary>
    /// <param name="a">matrix a</param>
    /// <param name="b">matrix b</param>
    /// <returns>sum of the two matrices</returns>
    public static Matrix3 operator+(Matrix3 a, Matrix3 b) {
      return new Matrix3( a.A1 + b.A1, a.A2 + b.A2, a.A3 + b.A3,
        a.B1 + b.B1, a.B2 + b.B2, a.B3 + b.B3,
        a.C1 + b.C1, a.C2 + b.C2, a.C3 + b.C3);													
    }

    /// <summary>
    /// indexer for matrix
    /// </summary>
    public float this [int column, int row] {
      get {
        return this[ column + row*3 ];
      }
      set {				
        this[ column + row*3 ] = value;
      }			
    }

    /// <summary>
    /// indexer for matrix
    /// </summary>
    public float this [int index] {			
      get {
        /*fixed( float *f = &this.A1 ) {
          return *(f+index);
        }*/

        switch(index) {
          case 0:
            return A1;
          case 1:
            return A2;
          case 2:
            return A3;
          case 3:
            return B1;
          case 4:
            return B2;
          case 5:
            return B3;
          case 6:
            return C1;
          case 7:
            return C2;
          case 8:
            return C3;
          default:
            throw new IndexOutOfRangeException("Invalid matrix index!");
        }
      }
      set {			
        /*fixed( float *f = &this.A1 ) {
          *(f+index) = value;
        }*/
        switch(index) {
          case 0:
            A1 = value;
            break;
          case 1:
            A2 = value;
            break;
          case 2:
            A3 = value;
            break;
          case 3:
            B1 = value;
            break;
          case 4:
            B2 = value;
            break;
          case 5:
            B3 = value;
            break;
          case 6:
            C1 = value;
            break;
          case 7:
            C2 = value;
            break;
          case 8:
            C3 = value;
            break;
          default:
            throw new IndexOutOfRangeException("Invalid matrix index!");
        }
      }			
    }

    /// <summary>
    /// multiplies two matrices
    /// </summary>
    /// <param name="a">first matrix</param>
    /// <param name="b">second matrix</param>
    /// <returns>result matrix</returns>
    public static Matrix3 operator*(Matrix3 a, Matrix3 b) {
      Matrix3 result = Matrix3.Zero;	
			
      // TODO: optimize!!! ;)
      for (int x=0; x<3; x++)
        for (int y=0; y<3; y++)
          for (int i=0; i<3; i++) 
            result[x,y] += a[i, y] * b[x, i];       

      return result;
    }

    //---------------------------------------------------------------
		#endregion
    //---------------------------------------------------------------
  }
}

