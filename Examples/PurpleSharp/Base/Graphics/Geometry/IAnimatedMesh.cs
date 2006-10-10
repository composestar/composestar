using System;
using System.Collections;

using Purple.Math;
using Purple.Graphics.Lighting;

namespace Purple.Graphics.Geometry
{
  /// <summary>
  /// Interface for an object containing an animated mesh.
  /// </summary>
	public interface IAnimatedMesh : IRenderAble, IShadowCaster
	{
    /// <summary>
    /// The assosicated textures.
    /// </summary>
    ArrayList Textures { get; }

    /// <summary>
    /// Returns the current mesh.
    /// </summary>
    Mesh Current { get; }

    /// <summary>
    /// Updates the mesh with the current animation.
    /// </summary>
    void Update();
	}
}
