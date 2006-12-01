using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight
{
	/// <summary>
	/// Indicates if the element has properties which should be analyzed and weaved upon by StarLight.
	/// Default action is to skip properties. By applying this attribute at assembly or class level, it is possible 
	/// to enable processing of properties. To exclude a specific property, apply the SkipWeaveAttribute to that property.
	/// </summary>
	/// <example>
	/// In the following example, the class is marked with the ProcessProperties attribute. So the Enabled property will be processed, 
	/// the Skipped property will be skipped because of the SkipWeaving attribute applied to it.
	/// <code>
	/// [Composestar.StarLight.ProcessProperties]
	/// public class ClassWithProperties
	/// {
	///    private bool _enabled = true;
	/// 
	///    public bool Enabled
	///	   {
	///		  get { return _enabled; }
	///		  set { _enabled = value; }
	///	   }
	/// 
	///    private bool _skipped = true;
	/// 
	///    [Composestar.StarLight.SkipWeaving()]
	///    public bool Skipped
	///	   {
	///		  get { return _skipped; }
	///		  set { _skipped = value; }
	///	   }
	/// } 
	/// </code>
	/// </example>
	[AttributeUsage(AttributeTargets.Assembly | AttributeTargets.Class )]
	public sealed class ProcessPropertiesAttribute : Attribute
	{
		private bool _enabled = true;

		/// <summary>
		/// Gets or sets a value indicating whether this <see cref="T:ProcessPropertiesAttribute"/> is enabled.  Default value is <see langword="true"/>.
		/// </summary>
		/// <value><c>true</c> if enabled; otherwise, <c>false</c>. Default value is <see langword="true"/>.</value>
		public bool Enabled
		{
			get { return _enabled; }
			set { _enabled = value; }
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:ProcessPropertiesAttribute"/> class.
		/// </summary>
		public ProcessPropertiesAttribute()
		{
			_enabled = true; 
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:ProcessPropertiesAttribute"/> class.
		/// </summary>
		/// <param name="enabled">if set to <c>true</c> [enabled].</param>
		public ProcessPropertiesAttribute(Boolean enabled)
		{
			_enabled = enabled;
		}
	}
}
