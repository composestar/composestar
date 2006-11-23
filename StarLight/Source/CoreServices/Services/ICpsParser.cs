using System;
using System.Text;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using Composestar.StarLight.Entities.Concerns;   

namespace Composestar.StarLight.CoreServices
{
	/// <summary>
	/// Interface for the CpsParser
	/// </summary>
	public interface ICpsParser
	{
		/// <summary>
		/// Gets the typenames that are referenced from the parsed input.
		/// </summary>
		/// <value>A read-only list with the names of referenced types.</value>
		ReadOnlyCollection<string> ReferencedTypes { get; }

		/// <summary>
		/// Gets a value indicating whether the input parsed had output filters.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if the input had output filters; otherwise, <c>false</c>.
		/// </value>
		bool HasOutputFilters { get; }

		/// <summary>
		/// Gets a value indicating whether the input parsed had embedded code.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if the input had embedded code; otherwise, <c>false</c>.
		/// </value>
		EmbeddedCode EmbeddedCode { get; }

		/// <summary>
		/// Parses the input.
		/// </summary>
		void Parse();
	}
}
