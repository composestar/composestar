using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;

namespace Composestar.StarLight.WeaveSpec.Instructions
{
    /// <summary>
    /// The type of the <see cref="T:ContextInstruction"></see>.
    /// </summary>
    [Serializable]
    [XmlRoot("ContextType", Namespace = "Entities.TYM.DotNET.Composestar")]
    public enum ContextType
    {
        /// <summary>
        /// Removed from the list.
        /// </summary>
        Removed = 0,
        /// <summary>
        /// Place the code for setting the inner call context.
        /// </summary>
        SetInnerCall = 10,
        /// <summary>
        /// Inject the code to check for an inner call.
        /// </summary>
        CheckInnerCall = 11,
        /// <summary>
        /// Inject the code to reset the inner call context.
        /// </summary>
        ResetInnerCall = 12,
        /// <summary>
        /// Inject code to create an action store.
        /// </summary>
        CreateActionStore = 20,
        /// <summary>
        /// Inject the code the store an action in the action store.
        /// </summary>
        StoreAction = 21,
        /// <summary>
        /// Create a JoinPointContext object.
        /// </summary>
        CreateJPC = 30,
        /// <summary>
        /// Restore a JoinPointContext object.
        /// </summary>
        RestoreJPC = 31,
        /// <summary>
        /// Emit a return statement.
        /// </summary>
        ReturnAction = 100,
    }
}
