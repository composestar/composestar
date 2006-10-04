using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using System.Text;

namespace Composestar.StarLight.ContextInfo 
{

    /// <summary>
    /// 
    /// </summary>
    public sealed class JoinPointContext
    {

        /// <summary>
        /// Initializes a new instance of the <see cref="T:JoinPointContext"/> class.
        /// </summary>
        public JoinPointContext()
        {
            _arguments = new Dictionary<short, ArgumentInfo>();
        }

        private object _sender;

        /// <summary>
        /// Gets or sets the sender.
        /// </summary>
        /// <value>The sender.</value>
        public object Sender
        {
            get { return _sender; }
            set { _sender = value; }
        }
	

        private object _target;

        /// <summary>
        /// Gets or sets the target.
        /// </summary>
        /// <value>The target.</value>
        public object Target
        {
            get { return _target; }
            set { _target = value; }
        }

        private string _methodName;

        /// <summary>
        /// Gets or sets the name of the method.
        /// </summary>
        /// <value>The name of the method.</value>
        public string MethodName
        {
            get { return _methodName; }
            set { _methodName = value; }
        }

        private Dictionary<short, ArgumentInfo> _arguments;

        /// <summary>
        /// Adds the argument.
        /// </summary>
        /// <param name="ordinal">The ordinal.</param>
        /// <param name="argumentType">Type of the argument.</param>
        /// <param name="value">The value.</param>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddArgument(short ordinal, Type argumentType, object value)
        {
            if (_arguments.ContainsKey(ordinal))
                _arguments[ordinal] = new ArgumentInfo(argumentType, value);
            else
                _arguments.Add(ordinal, new ArgumentInfo(argumentType, value));
        }

        /// <summary>
        /// Gets the argument value.
        /// </summary>
        /// <param name="ordinal">The ordinal.</param>
        /// <returns></returns>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public object GetArgumentValue(short ordinal)
        {
            ArgumentInfo ai;
            if (_arguments.TryGetValue(ordinal, out ai))
                return ai.Value;
            else
                return null;
        }

        /// <summary>
        /// Gets the type of the argument.
        /// </summary>
        /// <param name="ordinal">The ordinal.</param>
        /// <returns></returns>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public Type GetArgumentType(short ordinal)
        {
            ArgumentInfo ai;
            if (_arguments.TryGetValue(ordinal, out ai))
                return ai.Type;
            else
                return null;
        }

        /// <summary>
        /// Property to get the number of arguments
        /// </summary>
        public int ArgumentCount
        {
            get
            {
                return _arguments.Count;
            }
        }

        private ArgumentInfo _returnValue;

        /// <summary>
        /// Gets a value indicating whether this instance has return value.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance has return value; otherwise, <c>false</c>.
        /// </value>
        public bool HasReturnValue
        {
            get { return (_returnValue != null); }
        }

        /// <summary>
        /// Gets or sets the type of the return.
        /// </summary>
        /// <value>The type of the return.</value>
        public Type ReturnType
        {
            get
            {
                if (HasReturnValue)
                    return _returnValue.Type;
                else
                    return null;
            }
            set
            {
                if (_returnValue == null)
                    _returnValue = new ArgumentInfo(value, null);
                else
                    _returnValue.Type = value;
            }
        }

        /// <summary>
        /// Gets or sets the return value.
        /// </summary>
        /// <value>The return value.</value>
        public Object ReturnValue
        {
            get
            {
                if (HasReturnValue)
                    return _returnValue.Value;
                else
                    return null;
            }
            set
            {
                if (_returnValue == null)
                    throw new ArgumentNullException("returnType");
                else
                    _returnValue.Value = value;
            }
        }

        /// <summary>
        /// Internal class for storing the arguments.
        /// </summary>
        private sealed class ArgumentInfo
        {

            /// <summary>
            /// Initializes a new instance of the <see cref="T:ArgumentInfo"/> class.
            /// </summary>
            public ArgumentInfo()
            {
            }

            /// <summary>
            /// Initializes a new instance of the <see cref="T:ArgumentInfo"/> class.
            /// </summary>
            /// <param name="type">The type.</param>
            /// <param name="value">The value.</param>
            public ArgumentInfo(Type type, Object value)
            {
                _type = type;
                _value = value;
            }

            private Type _type;

            /// <summary>
            /// Gets or sets the type.
            /// </summary>
            /// <value>The type.</value>
            public Type Type
            {
                get { return _type; }
                set { _type = value; }
            }

            private object _value;

            /// <summary>
            /// Gets or sets the value.
            /// </summary>
            /// <value>The value.</value>
            public object Value
            {
                get { return _value; }
                set { _value = value; }
            }

        }

    }
}