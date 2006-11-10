using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.Entities.LanguageModel
{
    /// <summary>
    /// Contains attributes support
    /// </summary>   
    public interface ICustomAttributes
    {
        /// <summary>
        /// Gets a value indicating whether this instance has attributes.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance has attributes; otherwise, <c>false</c>.
        /// </value>
        bool HasAttributes { get; }

        /// <summary>
        /// Gets or sets the attributes.
        /// </summary>
        /// <value>The attributes.</value>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
        List<AttributeElement> Attributes { get; }

    }
}
