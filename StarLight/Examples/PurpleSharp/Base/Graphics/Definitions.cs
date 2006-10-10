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

namespace Purple.Graphics {	

  //=================================================================
  /// <summary>
  /// All six faces of the cube map.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// </remarks>
  //=================================================================
  public enum CubeMapFaces {
    /// <summary>
    /// Positive x-face of the cube map.
    /// </summary>
    PositiveX,
    /// <summary>
    /// Negative x-face of the cube map. 
    /// </summary>
    NevativeX,
    /// <summary>
    /// Positive y-face of the cube map.
    /// </summary>
    PositiveY,
    /// <summary>
    /// Negative y-face of the cube map.
    /// </summary>
    NegativeY,
    /// <summary>
    /// Positive z-face of the cube map.
    /// </summary>
    PositiveZ,
    /// <summary>
    /// Negative z-face of the cube map.
    /// </summary>
    NegativeZ
  }

  //=================================================================
  /// <summary>
  /// Enumerator for all clear buffers.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  [System.Flags]
  public enum ClearFlags {
    /// <summary>
    /// Clear the frame buffer.
    /// </summary>
    FrameBuffer = 1,
    /// <summary>
    /// Clear the depth buffer (z-buffer).
    /// </summary>
    DepthBuffer = 2,
    /// <summary>
    /// Clear the stencil buffer.
    /// </summary>
    StencilBuffer = 4,  
    /// <summary>
    /// Clears all.
    /// </summary>
    All = FrameBuffer | DepthBuffer | StencilBuffer
  }

  //=================================================================
  /// <summary>
  /// Enumerator for all shader types.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.3</para>  
  /// </remarks>
  //=================================================================
  public enum ShaderType {
    /// <summary>
    /// Vertex Shader.
    /// </summary>
    VertexShader = 1,
    /// <summary>
    /// Pixel Shader.
    /// </summary>
    PixelShader = 2,
    /// <summary>
    /// Vertex and Pixel Shader.
    /// </summary>
    Both = VertexShader | PixelShader
  }

  //=================================================================
  /// <summary>
  /// ShadeMode
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================		
  public enum ShadeMode {
    /// <summary>
    /// Flat shading mode. The color and specular component of the first vertex in the triangle 
    /// are used to determine the color and specular component of the face. 
    /// These colors remain constant across the triangle; that is, they are not interpolated. 
    /// The specular alpha is interpolated. See Remarks.</summary>
    Flat = 1,
    /// <summary>
    /// Gouraud shading mode. 
    /// The color and specular components of the face are determined by a linear interpolation between all three of the triangle's vertices.
    /// </summary>
    Gouraud = 2,
    /// <summary>Not supported.</summary>
    Phong = 3
  }

  //=================================================================
  /// <summary>
  /// Enumeration over all filters.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.7</para>  
  /// </remarks>
  //=================================================================		
  public enum Filter {
    /// <summary>Mipmapping disabled. The rasterizer uses the magnification filter instead.</summary>
    None,
    /// <summary>Each destination pixel is computed by sampling the nearest pixel from the source image.</summary>
    Point,
    /// <summary>Bilinear interpolation filtering is used as a texture magnification or minification filter. A weighted average of a 2x2 area of texels surrounding the desired pixel is used. The texture filter to use between mipmap levels is trilinear mipmap interpolation. The rasterizer interpolates pixel color in a linear manner, using the texels of the two nearest textures.</summary>
    Linear,
    /// <summary>Each pixel in the source image contributes equally to the destination image. This is the slowest of the filters.</summary>
    Triangle,
    /// <summary>Each pixel is computed by averaging a 2x2(x2) box of pixels from the source image. This filter works only when the dimensions of the destination are half those of the source, as is the case with mipmaps.</summary>
    Box
  }
	
  //=================================================================
  /// <summary>
  /// Format of textures
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================		
  public enum Format {
    /// <summary>24-bit RGB pixel format with 8 bits per channel.</summary>
    R8G8B8,
    /// <summary>32-bit ARGB pixel format with alpha, using 8 bits per channel.</summary>		
    A8R8G8B8,
    /// <summary>32-bit RGB pixel format, where 8 bits are reserved for each color.</summary>
    X8R8G8B8,
    /// <summary>16-bit RGB pixel format with 5 bits for red, 6 bits for green, and 5 bits for blue.</summary>
    R5G6B5,
    /// <summary>16-bit pixel format where 5 bits are reserved for each color.</summary>
    X1R5G5B5,
    /// <summary>16-bit pixel format where 5 bits are reserved for each color and 1 bit is reserved for alpha.</summary>
    A1R5G5B5,
    /// <summary>16-bit ARGB pixel format with 4 bits for each channel.</summary>
    A4R4G4B4,
    /// <summary>8-bit RGB texture format using 3 bits for red, 3 bits for green, and 2 bits for blue.</summary>
    R3G3B2,
    /// <summary>8-bit alpha only.</summary>
    A8,
    /// <summary>16-bit ARGB texture format using 8 bits for alpha, 3 bits each for red and green, and 2 bits for blue</summary>
    A8R3G3B2,
    /// <summary>16-bit RGB pixel format using 4 bits for each color.</summary>
    X4R4G4B4,
    /// <summary>32-bit ARGB pixel format with alpha, using 8 bits per channel.</summary>
    A8B8G8R8,
    /// <summary>32-bit RGB pixel format, where 8 bits are reserved for each color.</summary>
    X8B8G8R8,
    /// <summary>64-bit pixel format using 16 bits for each component.</summary>
    A16B16G16R16,
    /// <summary>32-bit RGB pixel format, where 8 bits are reserved for each color.</summary>
    A2B10G10R10,
    /// <summary>Nonlockable format that contains 24 bits of depth (in a 24-bit floating-point format - 20e4) and 8 bits of stencil.</summary>
    D24SingleS8,
    /// <summary>Lockable format in which the depth value is represented as a standard IEEE floating-point number. </summary>
    D32SingleLockable,
    /// <summary>A 32-bit z-buffer bit depth that uses 24 bits for the depth channel and 4 bits for the stencil channel. </summary>
    D24X4S4,
    /// <summary>A 2-bit z-buffer bit depth that uses 24 bits for the depth channel. </summary>
    D24X8,
    /// <summary>16-bit luminance only.</summary>
    L16,
    /// <summary>A 16-bit z-buffer bit depth.</summary>
    D16,
    /// <summary>A 32-bit z-buffer bit depth that uses 24 bits for the depth channel and 8 bits for the stencil channel. </summary>
    D24S8,
    /// <summary>A 16-bit z-buffer bit depth that reserves 15 bits for the depth channel and 1 bit for the stencil channel. </summary>
    D15S1,
    /// <summary>A 32-bit z-buffer bit depth. </summary>
    D32,
    /// <summary>A 16-bit z-buffer bit depth. </summary>
    D16Lockable,
    /// <summary>16-bit float format using 16 bits for the red channel.</summary>
    R16F,
    /// <summary>32-bit float format using 32 bits for the red channel.</summary>
    R32F,
    /// <summary>32-bit float format using 16 bits for the red channel and 16 bits for the green channel.</summary>
    G16R16F,
    /// <summary>64-bit float format using 32 bits for the red channel and 32 bits for the green channel.</summary>
    G32R32F,
    /// <summary>64-bit float format using 16 bits for the each channel (alpha, blue, green, red).</summary>
    A16B16G16R16F,
    /// <summary>128-bit float format using 32 bits for the each channel (alpha, blue, green, red).</summary>
    A32B32G32R32F,
    /// <summary>32-bit pixel format using 16 bits each for green and red.</summary>
    G16R16,
    /// <summary>0 or 1 bit alpha.</summary>
    Dxt1,
    /// <summary>Explicit 4-bit alpha, color data is premultiplied by alpha.</summary>
    Dxt2,
    /// <summary>Explicit 4-bit alpha, not premultiplied.</summary>
    Dxt3,
    /// <summary>Interpolated alpha, color data is premultiplied by alpha.</summary>
    Dxt4,
    /// <summary>Interpolated alpha, not premultiplied.</summary>
    Dxt5
  }

  //=================================================================
  /// <summary>
  /// CullingMode
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================		
  public enum CullMode {
    /// <summary>cull faces that are defined counterClockwise</summary>
    CounterClockwise,
    /// <summary>cull faces that are defined clockwise</summary>
    Clockwise,
    /// <summary>don't cull back faces</summary>
    None
  }

  //=================================================================
  /// <summary>
  /// TextureOperations
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================		
  public enum TextureOperation {
    /// <summary>Disables output from this texture stage and all stages with a higher index. 
    /// To disable texture mapping, set this as the color operation for the first texture stage (stage 0). Alpha operations cannot be disabled when color operations are enabled. 
    /// Setting the alpha operation to Disable when color blending is enabled causes undefined behavior. </summary>
    Disable = 1,
    /// <summary></summary>
    SelectArg1 = 2,
    /// <summary></summary>
    SelectArg2 = 3,
    /// <summary></summary>
    Modulate = 4,
    /// <summary></summary>
    Modulate2x = 5,
    /// <summary></summary>
    Modulate4x = 6,
    /// <summary></summary>
    Add = 7,
    /// <summary></summary>
    AddSigned = 8,
    /// <summary></summary>
    AddSigned2x = 9,
    /// <summary></summary>
    Subtract = 10,
    /// <summary></summary>
    AddSmooth = 11,
    /// <summary></summary>
    BlendDiffuseAlpha = 12,
    /// <summary></summary>
    BlendTextureAlpha = 13,
    /// <summary></summary>
    BlendFactorAlpha = 14,
    /// <summary></summary>
    BlendTextureAlphaPM = 15,
    /// <summary></summary>
    BlendCurrentAlpha = 16,
    /// <summary></summary>
    PreModulate = 17,
    /// <summary></summary>
    ModulateAlphaAddColor = 18,
    /// <summary></summary>
    ModulateColorAddAlpha = 19,
    /// <summary></summary>
    ModulateInvAlphaAddColor = 20,
    /// <summary></summary>
    ModulateInvColorAddAlpha = 21,
    /// <summary></summary>
    BumpEnvironmentMap = 22,
    /// <summary></summary>
    BumoEnvironmentMapLuminance = 23,
    /// <summary></summary>
    DotProduct3 = 24,
    /// <summary></summary>
    MultiplyAdd = 25,
    /// <summary></summary>
    Lerp = 26		
  }

  //=================================================================
  /// <summary>
  /// TextureStageState
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================	
  public enum TextureStageState {
    /// <summary></summary>
    ColorOperation = 1,
    /// <summary></summary>
    ColorArgument1 = 2,
    /// <summary></summary>
    ColorArgument2 = 3,
    /// <summary></summary>
    AlphaOperation = 4,
    /// <summary></summary>
    AlphaArgument1 = 5,
    /// <summary></summary>
    AlphaArgument2 = 6,
    /// <summary></summary>
    BumpEnvironmentMaterial00 = 7,
    /// <summary></summary>
    BumpEnvironmentMaterial01 = 8,
    /// <summary></summary>
    BumpEnvironmentMaterial10= 9,
    /// <summary></summary>
    BumpEnvironmentMaterial11 = 10,
    /// <summary></summary>
    TextureCoordinateIndex = 11,
    /// <summary></summary>
    BumpEnvironmentLuminanceScale = 22,
    /// <summary></summary>
    BumpEnvironmentLuminanceOffset = 23,
    /// <summary></summary>
    TextureTransformFlags = 24,
    /// <summary></summary>
    ColorArgument0= 26,
    /// <summary></summary>
    AlphaArgument0 = 27,
    /// <summary></summary>
    ResultArgument = 28,
  }

  //=================================================================
  /// <summary>
  /// TextureArguments
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================	
  public enum TextureArgument {		
    /// <summary></summary>
    Specular,
    /// <summary></summary>
    Diffuse,
    /// <summary></summary>
    AlphaReplicate,
    /// <summary></summary>
    Complement,
    /// <summary></summary>
    Temp,
    /// <summary></summary>
    TFactor,
    /// <summary></summary>
    TextureColor,
    /// <summary></summary>
    Current,
    /// <summary></summary>
    SelectMask,
    /// <summary></summary>
    Constant 
  }

  //=================================================================
  /// <summary>
  /// TextureTransform
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================	
  public enum TextureTransform {
    /// <summary>Texture coordinates are passed directly to the rasterizer. </summary>
    Disable = 0,
    /// <summary>The rasterizer should expect 1-D texture coordinates.</summary>
    Count1 = 1,
    /// <summary>The rasterizer should expect 2-D texture coordinates.</summary>
    Count2 = 2,
    /// <summary>The rasterizer should expect 3-D texture coordinates.</summary>
    Count3 = 3,
    /// <summary>The rasterizer should expect 4-D texture coordinates.</summary>
    Count4 = 4,
    /// <summary>
    /// The texture coordinates are all divided by the last element before being passed to the rasterizer. 
    /// For example, if this flag is specified with the Count3flag, 
    /// the first and second texture coordinates is divided by the third coordinate before being passed to the rasterizer. 
    /// </summary>
    Projected = 256,
  }

  //=================================================================
  /// <summary>
  /// TextureAddress
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================	
  public enum TextureAddress {
    /// <summary>Tile the texture at every integer junction. For example, for u values between 0 and 3, the texture is repeated three times; no mirroring is performed. </summary>
    Wrap = 1,
    /// <summary>
    /// Similar to wRAP, except that the texture is flipped at every integer junction. 
    /// For u values between 0 and 1, for example, the texture is addressed normally; 
    /// between 1 and 2, the texture is flipped (mirrored); 
    /// between 2 and 3, the texture is normal again, and so on. 
    /// </summary>
    Mirror = 2,
    /// <summary>Texture coordinates outside the range [0.0, 1.0] are set to the texture color at 0.0 or 1.0, respectively. </summary>
    Clamp = 3,
    /// <summary>Texture coordinates outside the range [0.0, 1.0] are set to the border color. </summary>
    Border = 4,
    /// <summary>
    /// Similar to Mirror and Clamp. 
    /// Takes the absolute value of the texture coordinate (thus, mirroring around 0), 
    /// and then clamps to the maximum value. The most common usage is for volume textures, 
    /// where support for the full MirrorOnce texture-addressing mode is not necessary,
    /// but the data is symmetric around the one axis. 
    /// </summary>
    MirrorOnce = 5
  }

  //=================================================================
  /// <summary>
  /// TextureAddress
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  public enum TextureFilter {
    /// <summary>Mipmapping disabled. The rasterizer should use the magnification filter instead.</summary>
    None,
    /// <summary>Mipmapping disabled. The rasterizer should use the magnification filter instead.</summary>
    Point,
    /// <summary>Mipmapping disabled. The rasterizer should use the magnification filter instead.</summary>
    Linear,
    /// <summary>Mipmapping disabled. The rasterizer should use the magnification filter instead.</summary>
    Anisotropic,
    /// <summary>Not supported so far</summary>
    PyramidalQuad,
    /// <summary>Not supported so far</summary>
    GaussianQuad
  }

  //=================================================================
  /// <summary>
  /// Compare functions
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  public enum Compare {
    /// <summary>Always fail the test. </summary>
    Never = 1,
    /// <summary>Accept the new pixel if its value is less than the value of the current pixel. </summary>
    Less = 2,
    /// <summary>Accept the new pixel if its value equals the value of the current pixel. </summary>		
    Equal = 3,
    /// <summary>Accept the new pixel if its value is less than or equal to the value of the current pixel. </summary>
    LessEqual = 4,
    /// <summary>Accept the new pixel if its value is greater than the value of the current pixel. </summary>
    Greater = 5,
    /// <summary>Accept the new pixel if its value does not equal the value of the current pixel. </summary>
    NotEqual = 6,
    /// <summary>Accept the new pixel if its value is greater than or equal to the value of the current pixel. </summary>
    GreaterEqual = 7,
    /// <summary>Always pass the test. </summary>
    Always = 8,
  }

  //=================================================================
  /// <summary>
  /// DeclarationType - Defines a vertex declaration data type.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  public enum DeclarationType {
    /// <summary>1-D float expanded to (float, 0, 0, 1)</summary>
    Float1,
    /// <summary>2-D float expanded to (float, float, 0, 1)</summary>
    Float2,
    /// <summary>3-D float expanded to (float, float, float, 1)</summary>
    Float3,
    /// <summary>4-D float expanded to (float, float, float, float).</summary>
    Float4,
    /// <summary>4-D packed unsigned bytes mapped to 0 to 1 range. Input is in D3DCOLOR format (ARGB) expanded to (R, G, B, A)</summary>
    Color,
    /// <summary>4-D unsigned byte</summary>
    UByte4,
    /// <summary>2-D signed short expanded to (value, value, 0, 1)</summary>
    Short2,
    /// <summary>4-D signed short expanded to (value, value, value, value)</summary>
    Short4,
    /// <summary>Each of 4 bytes is normalized by dividing to 255.0. This type is valid for vertex shader version 2.0 or higher</summary>
    UByte4N,
    /// <summary>Normalized, 2-D signed short, expanded to (First byte/32767.0, second byte/32767.0, 0, 1). This type is valid for vertex shader version 2.0 or higher</summary>
    Short2N,
    /// <summary>Normalized, 4-D signed short, expanded to (First byte/32767.0, second byte/32767.0, third byte/32767.0, fourth byte/32767.0). This type is valid for vertex shader version 2.0 or higher</summary>
    Short4N,
    /// <summary>Normalized, 2-D unsigned short, expanded to (First byte/65535.0, second byte/65535.0, 0, 1). This type is valid for vertex shader version 2.0 or higher</summary>
    UShort2N,
    /// <summary>A normalized 4-D unsigned short, expanded to (First byte/65535.0, second byte/65535.0, third byte/65535.0, fourth byte/65535.0). This type is valid for vertex shader version 2.0 or higher</summary>
    UShort4N,
    /// <summary>3-D unsigned 10 10 10 format expanded to (value, value, value, 1). This type is valid for vertex shader version 2.0 or higher</summary>
    UDec3,
    /// <summary>3-D signed 10 10 10 format normalized and expanded to (v[0]/511.0, v[1]/511.0, v[2]/511.0, 1). This type is valid for vertex shader version 2.0 or higher</summary>
    Dec3N,
    /// <summary>Two 16-bit floating point values expanded to (value, value, 0, 1)</summary>
    Float16Two,
    /// <summary>Four 16-bit floating point values expanded to (value, value, value, value).</summary>
    Float16Four,
    /// <summary>The type field in the declaration is unused. This is designed for use with D3DDECLMETHOD_UV and D3DDECLMETHOD_LOOKUPPRESAMPLED</summary>
    Unused,
    /// <summary>This vertex element is just for software.</summary>
    Software
  }

  //=================================================================
  /// <summary>
  /// DeclarationMethod -Defines the vertex declaration method. 
  /// The method tells the tessellator what data needs to be generated from the vertex data during tessellation.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  public enum DeclarationMethod {
    /// <summary>
    /// Default value. The tessellator copies the vertex data (spline data for patches) as is, with no additional calculations. 
    /// When the tessellator is used, this element is interpolated. Otherwise vertex data is copied into the input register. 
    /// The input and output type can be any value.
    /// </summary>
    Default = 1,
    /// <summary>
    /// Computes the tangent at a point on the rectangle or triangle patch in the U direction. 
    /// The input type can be Float3, Float4, Color, UByte4, Short4. 
    /// The output type is always float3.
    /// </summary>
    PartialU,
    /// <summary>
    /// Computes the tangent at a point on the rectangle or triangle patch in the V direction. 
    /// The input type can be Float3, Float4, Color, UByte4, or Short4. 
    /// The output type is always Float3.
    /// </summary>
    PartialV,
    /// <summary>
    /// Computes the normal at a point on the rectangle or triangle patch by taking the cross product of the two tangents. 
    /// The input type can be Float[43], Color, UByte4, or Short4. The output type is always Float3.
    /// </summary>
    CrossUV,
    /// <summary>
    /// Copy out the U, V values at a point on the rectangle or triangle patch. 
    /// This results in a 2-D float. The input type must be set to Unused. 
    /// The output data type is always Float2. The input stream and offset are also unused (but must be set to 0).
    /// </summary>
    UV,
    /// <summary>
    /// Look up a displacement map. The input type can be Float2, Float3, or Float4. 
    /// Only the .x and .y components are used for the texture map lookup. The output type is always Float1. 
    /// The device must support displacement mapping. For more information about displacement mapping, see Displacement Mapping. 
    /// This constant is supported only by the programmable pipeline on N-patch data, if N-patches are enabled. 
    /// </summary>
    LookUp,
    /// <summary>
    /// Look up a presampled displacement map. The input type must be set to Unused. 
    /// The stream index and the stream offset must be set to 0. The output type for this operation is always Float1. 
    /// The device must support displacement mapping. For more information about displacement mapping, see Displacement Mapping. 
    /// This constant is supported only by the programmable pipeline on N-patch data, if N-patches are enabled. 
    /// </summary>
    LookUpPresampled,
  }

  //=================================================================
  /// <summary>
  /// DeclarationUsage - Identifies the intended use of vertex data.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  public enum DeclarationUsage {
    /// <summary>Position data.</summary>
    Position = 0,
    /// <summary>Blending weight data.</summary>
    BoneWeights,
    /// <summary>Blending indices data.</summary>
    BoneIndices,
    /// <summary>Vertex normal data.</summary>
    Normal,
    /// <summary>Point size data.</summary>
    PointSize,
    /// <summary>Diffuse color data.</summary>		
    TextureCoordinate,
    /// <summary>Vertex tangent data.</summary>
    Tangent,
    /// <summary>Vertex binormal data.</summary>
    Binormal,
    /// <summary>A single positive floating point value that controls the rate of tessellation. For more information about the data type, see Float1.</summary>
    TessellateFactor,
    /// <summary>Vertex data contains position data that has been transformed and lit.</summary>
    PositionTransformed,
    /// <summary>Vertex data contains diffuse or specular color. 0 specifies diffuse color and 1 specifies specular color. This usage is for fixed function vertex processing and pixel shaders prior to ps_3_0. </summary>
    Color,
    /// <summary>Vertex data contains fog data</summary>
    Fog,
    /// <summary>Vertex data contains depth data</summary>
    Depth,
    /// <summary>Vertex data contains sampler data</summary>
    Sample,
    /// <summary>None</summary>
    None = 255
  }

  //=================================================================
  /// <summary>
  /// Defines the supported blend factors. 
  /// </summary>
  /// <remarks>
  /// <c>Final color = source color * SourceBlend + destination color * DestinationBlend.</c>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  public enum Blend {
    /// <summary>Blend factor is (0, 0, 0, 0). </summary>
    Zero = 1,
    /// <summary>Blend factor is (1, 1, 1, 1). </summary>
    One,
    /// <summary>Blend factor is (Rs,Gs,Bs,As). </summary>
    SourceColor,
    /// <summary>Blend factor is (1–Rs,1–Gs,1–Bs,1–As). </summary>
    InvSourceColor,
    /// <summary>Blend factor is (As,As,As,As). </summary>
    SourceAlpha,
    /// <summary>Blend factor is (1–As,1–As,1–As,1–As). </summary>
    InvSourceAlpha,
    /// <summary>Blend factor is (Ad,Ad,Ad,Ad). </summary>
    DestinationAlpha,
    /// <summary>Blend factor is (1–Ad,1–Ad,1–Ad,1–Ad). </summary>
    InvDestinationAlpha,
    /// <summary>Blend factor is (Rd,Gd,Bd,Ad). </summary>
    DestionationColor,
    /// <summary>Blend factor is (1–Rd,1–Gd,1–Bd,1–Ad).</summary>
    InvDestionationColor,
    /// <summary>Blend factor is (f, f, f, 1); f = min(A, 1 –Ad). </summary>
    SourceAlphaSat,
    /// <summary>Obsolete. For Microsoft® DirectX® 6.0 and later, you can achieve the same effect by setting the source and destination blend factors to D3DBLEND_SRCALPHA and D3DBLEND_INVSRCALPHA in separate calls. </summary>
    BothSourceAlpha,
    /// <summary>Source blend factor is (1–As,1–As,1–As,1–As), and destination blend factor is (As,As,As,As); the destination blend selection is overridden. This blend mode is supported only for the D3DRS_SRCBLEND render state. </summary>
    BothInvSourceAlpha,
    /// <summary>Constant color blending factor used by the frame-buffer blender. This blend mode is supported only if D3DPBLEND_BLENDFACTOR is on. </summary>
    BlendFactor,
    /// <summary>Inverted constant color blending factor used by the frame-buffer blender. This blend mode is supported only if D3DPBLEND_BLENDFACTOR is on.</summary>
    InvBlendFactor
  }

  //=================================================================
  /// <summary>
  /// Stencil Operations
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.1</para>  
  /// </remarks>
  //=================================================================
  public enum StencilOperation {
    /// <summary>Do not update the entry in the stencil buffer. This is the default value.</summary>
    Keep = 1,
    /// <summary>Set the stencil-buffer entry to 0</summary>
    Zero = 2, 
    /// <summary>Replace the stencil-buffer entry with reference value.</summary>
    Replace = 3,
    /// <summary>Increment the stencil-buffer entry, clamping to the maximum value.</summary>
    IncrementClamp = 4,
    /// <summary>Decrement the stencil-buffer entry, clamping to zero.</summary>
    DecrementClamp = 5,
    /// <summary>Invert the bits in the stencil-buffer entry.</summary>
    Invert = 6,
    /// <summary>Increment the stencil-buffer entry, wrapping to zero if the new value exceeds the maximum value.</summary>
    Increment = 7,
    /// <summary>Decrement the stencil-buffer entry, wrapping to the maximum value if the new value is less than zero.</summary>
    Decrement = 8
  }

  //=================================================================
  /// <summary>
  /// Type of texture
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.2</para>  
  ///   <para>Last Update: 0.7</para>
  /// </remarks>
  //=================================================================
  public enum TextureUsage {
    /// <summary>
    /// use it as a normal texture
    /// </summary>
    Normal = 0,

    /// <summary>
    /// use texture as render target
    /// </summary>
    RenderTarget = 1,

    /// <summary>
    /// use texture as depth/stencil buffer
    /// </summary>
    DepthStencil = 2,

    /// <summary>
    /// This is a dynamic texture.
    /// </summary>
    /// <remarks>This flag is only valid for Normal textures.</remarks>
    Dynamic = 4,
  }

  //=================================================================
  /// <summary>
  /// The enumeration of all color channels.
  /// </summary>
  /// <remarks>
  ///   <para>Author: Markus Wöß</para>
  ///   <para>Since: 0.6</para>  
  /// </remarks>
  //=================================================================
  public enum ColorChannels {
    /// <summary>
    /// No channel.
    /// </summary>
    None = 0,
    /// <summary>
    /// The alpha channel.
    /// </summary>
    Red = 1,
    /// <summary>
    /// The channel for the green component.
    /// </summary>
    Green = 2,
    /// <summary>
    /// The channel for the blue component.
    /// </summary>
    Blue = 4,
    /// <summary>
    /// All channels.
    /// </summary>
    Alpha = 8,
    /// <summary>
    /// The channel for the red component.
    /// </summary>
    All = Alpha | Red | Green | Blue
  }
}
