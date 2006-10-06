using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes
{
    [AttributeUsage( AttributeTargets.Class, Inherited = false, AllowMultiple = false )]
    public class FilterTypeAnnotation : Attribute
    {
        /// <summary>
        /// The name of the filtertype
        /// </summary>
        public string name;

        /// <summary>
        /// The accept-call action
        /// </summary>
        public string acceptCallAction;

        /// <summary>
        /// The reject-call action
        /// </summary>
        public string rejectCallAction;

        /// <summary>
        /// The accept-return action
        /// </summary>
        public string acceptReturnAction;

        /// <summary>
        /// The reject-return action
        /// </summary>
        public string rejectReturnAction;



        public FilterTypeAnnotation(string name, string acceptCallAction, string rejectCallAction,
            string acceptReturnAction, string rejectReturnAction)
        {
            this.name = name;
            this.acceptCallAction = acceptCallAction;
            this.rejectCallAction = rejectCallAction;
            this.acceptReturnAction = acceptReturnAction;
            this.rejectReturnAction = rejectReturnAction;
        }

       
    }
}