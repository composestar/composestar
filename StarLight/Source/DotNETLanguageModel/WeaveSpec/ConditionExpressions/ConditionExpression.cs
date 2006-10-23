#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.Visitor;
#endregion

/// <summary>
/// Composestar. star light. weave spec. condition expression
/// </summary>
namespace Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions
{

    /// <summary>
    /// The base of all the ConditionExpressions.
    /// </summary>
    [Serializable]
    [XmlRoot("ConditionExpression", Namespace = "Entities.TYM.DotNET.Composestar")]
    public abstract class ConditionExpression
    {
        public ConditionExpression()
        {

        } // ConditionExpression()

    } // class ConditionExpression

} // namespace Composestar.StarLight.Entities.WeaveSpec.ConditionExpression
