#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor;
#endregion

/// <summary>
/// Composestar. star light. weave spec. instructions
/// </summary>
namespace Composestar.StarLight.Entities.WeaveSpec.Instructions
{

    /// <summary>
    /// This expression determines the type of code to be injected for a context expression.
    /// </summary>
    [Serializable]
    [XmlRoot("ContextExpression", Namespace = "Entities.TYM.DotNET.Composestar")]
    public enum ContextExpression
    {
        /// <summary>
        /// Checks if there are more actions in the action store.
        /// </summary>
        HasMoreActions = 22,
        /// <summary>
        /// Retrieves the next action in the action store.
        /// </summary>
        RetrieveAction = 23        
    } // enum ContextExpression

} // namespace Composestar.StarLight.Entities.WeaveSpec.Instructions
