#region License
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * ComposeStar StarLight [http://janus.cs.utwente.nl:8000/twiki/bin/view/StarLight/WebHome]
 * Copyright (C) 2009, University of Twente.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions
 * are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Twente nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
*/
#endregion

using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;

namespace Composestar.StarLight.Entities.WeaveSpec.Instructions
{
    [Serializable]
    [XmlType("FilterArgument", Namespace = Constants.NS)]
    public class FilterArgument
    {
        private string _name;
        private string _value;
        private string _valueEx;
        private FilterArgumentType _type;

        /// <summary>
        /// Gets or sets the name.
        /// </summary>
        /// <value>The name.</value>
        [XmlAttribute]
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }

        [XmlAttribute]
        public string Value
        {
            get { return _value; }
            set { _value = value; }
        }

        [XmlAttribute]
        public string ValueEx
        {
            get { return _valueEx; }
            set { _valueEx = value; }
        }

        [XmlAttribute]
        public FilterArgumentType ArgType
        {
            get {return _type;}
            set { _type = value; }
        }

    }

    [Serializable]
    [XmlType("FilterArgumentType", Namespace = Constants.NS)]
    public enum FilterArgumentType
    {
        Object = 0,
        Selector = 1,
        Type = 2,
        Literal = 3,
        ProgramElement = 4,
    }
}
