#region License
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * ComposeStar StarLight [http://janus.cs.utwente.nl:8000/twiki/bin/view/StarLight/WebHome]
 * Copyright (C) 2003, University of Twente.
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

#region Using directives
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Diagnostics.CodeAnalysis;
using System.Reflection;
using System.Runtime.CompilerServices;
using System.Text;
using Composestar.StarLight.ContextInfo.RuBCoDe;
#endregion

namespace Composestar.StarLight.ContextInfo
{

    /// <summary>
    /// The JoinPointContext class is used to pass information about the joinpoint to methods.
    /// </summary>
    [DebuggerNonUserCode()]
    public sealed class JoinPointContext
    {

        #region Private Variables

        /// <summary>
        /// Arguments of the method.
        /// </summary>
        private Dictionary<short, ArgumentInfo> _arguments;
        /// <summary>
        /// Properties collection.
        /// </summary>
        private Dictionary<string, object> _properties;

        private object _sender;
        private object _startTarget;
        private string _startSelector;
        private object _currentTarget;
        private string _currentSelector;
        private object _substitutionTarget;
        private string _substitutionSelector;
        private ArgumentInfo _returnValue;
        private bool _hasReturnValueSet;
        private MethodBase _methodInformation;

        #endregion

        #region BookKeeping
        private bool _bookkeeping = false;
        private bool _autobk = true;
        private SimpleBK _returnBK = null;
        private IDictionary<string, SimpleBK> _customBKs = null;
        #endregion

        #region Properties

        /// <summary>
        /// Gets or sets the current method information. 
        /// </summary>
        /// <value>The method information as a <see cref="T:System.Reflection.MethodBase"></see> object.</value>
        public MethodBase MethodInformation
        {
            get
            {
                return _methodInformation;
            }
            set
            {
                _methodInformation = value;
            }
        }

        /// <summary>
        /// Gets or sets the sender.
        /// </summary>
        /// <value>The sender.</value>
        public object Sender
        {
            get
            {
                return _sender;
            }
            set
            {
                _sender = value;
            }
        }
        /// <summary>
        /// Gets or sets the start-target.
        /// </summary>
        /// <value>The start-target.</value>
        public object StartTarget
        {
            get
            {
                return _startTarget;
            }
            set
            {
                _startTarget = value;
            }
        }

        /// <summary>
        /// Gets or sets the start-selector.
        /// </summary>
        /// <value>The start-selector.</value>
        public string StartSelector
        {
            get
            {
                return _startSelector;
            }
            set
            {
                _startSelector = value;
            }
        }

        /// <summary>
        /// Gets or sets the current-target.
        /// </summary>
        /// <value>The current-target.</value>
        public object CurrentTarget
        {
            get
            {
                return _currentTarget;
            }
            set
            {
                _currentTarget = value;
            }
        }

        /// <summary>
        /// Gets or sets the current-selector.
        /// </summary>
        /// <value>The current-selector.</value>
        public string CurrentSelector
        {
            get
            {
                return _currentSelector;
            }
            set
            {
                _currentSelector = value;
            }
        }

        /// <summary>
        /// Gets or sets the substitution-target.
        /// </summary>
        /// <value>The substitution-target.</value>
        public object SubstitutionTarget
        {
            get
            {
                return _substitutionTarget;
            }
            set
            {
                _substitutionTarget = value;
            }
        }

        /// <summary>
        /// Gets or sets the substitution-selector.
        /// </summary>
        /// <value>The substitution-selector.</value>
        public string SubstitutionSelector
        {
            get
            {
                return _substitutionSelector;
            }
            set
            {
                _substitutionSelector = value;
            }
        }

        #endregion

        #region ctor

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Composestar.StarLight.ContextInfo.JoinPointContext"/> class.
        /// </summary>
        public JoinPointContext()
        {
            _arguments = new Dictionary<short, ArgumentInfo>();
            _properties = new Dictionary<string, object>();
        }

        /// <summary>
        /// Release the bookkeeper to the pool
        /// </summary>
        ~JoinPointContext()
        {
            //ReleaseBK();
        }

        #endregion

        #region BookKeeping

        /// <summary>
        /// Set resource operation book keeping. If false no bookkeeping will be performed.
        /// It should be set to true at the end of the JPC creation, not before.
        /// </summary>
        public bool BookKeeping
        {
            get { return _bookkeeping; }
            set
            {
                _bookkeeping = value;
                foreach (ArgumentInfo ai in _arguments.Values)
                {
                    ai.BookKeeping = value;
                }
            }
        }

        /// <summary>
        /// Sets/gets the value of automatic book keeping. When automatic book keeping is true (default)
        /// all read and write operations will automatically be captured.
        /// </summary>
        public bool AutoBookKeeping
        {
            get { return _autobk; }
            set
            {
                _autobk = value;
                foreach (ArgumentInfo ai in _arguments.Values)
                {
                    ai.AutoBookKeeping = value;
                }
            }
        }

        /// <summary>
        /// This will check the current resource operation books for conflicts
        /// </summary>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void FinalizeBookKeeping()
        {
            if (!_bookkeeping) return;
            if (_startTarget != null)
            {
                Console.Error.WriteLine("$$$ FinalizeBookKeeping for {0}.{1}", _startTarget.GetType(), _startSelector);
            }
            else
            {
                Console.Error.WriteLine("$$$ FinalizeBookKeeping for <static>.{0}", _startSelector);
            }
            if (_returnBK != null)
            {
                _returnBK.report();
                _returnBK.validate();
            }
            foreach (ArgumentInfo ai in _arguments.Values)
            {
                if (ai.ArgumentBK != null)
                {
                    ai.ArgumentBK.report();
                    ai.ArgumentBK.validate();
                }
            }
            if (_customBKs != null)
            {
                foreach (SimpleBK bk in _customBKs.Values)
                {
                    bk.report();
                    bk.validate();
                }
            }
            BookKeeping = false;
            ReleaseBK();
        }

        /// <summary>
        /// Releases the book keeping instances to the pool. Should never be called during processing.
        /// </summary>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void ReleaseBK()
        {
            if (_returnBK != null)
            {
                BookKeeperPool.release(ref _returnBK);
            }
            foreach (ArgumentInfo ai in _arguments.Values)
            {
                ai.ReleaseBK();
            }
            if (_customBKs != null)
            {
                foreach (SimpleBK bk in _customBKs.Values)
                {
                    SimpleBK _bk = bk;
                    BookKeeperPool.release(ref _bk);
                }
                _customBKs.Clear();
            }
        }

        /// <summary>
        /// Get the book keeper for the return value; Returns null when bookkeeping is disabled.
        /// </summary>
        public SimpleBK ReturnValueBK
        {
            get
            {
                if (_returnBK == null && _bookkeeping && _returnValue != null)
                {
                    _returnBK = BookKeeperPool.getSimpleBK(ResourceType.Return, "return");
                }
                return _returnBK;
            }
        }

        /// <summary>
        /// Add a single resource operation to a resource. Calling this method with the resource type set to  
        /// ArgumentEntry will add this operation to all entries.
        /// </summary>
        /// <param name="rt"></param>
        /// <param name="op"></param>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddResourceOp(ResourceType rt, string op)
        {
            if (!_bookkeeping) return;
            switch (rt)
            {
                case ResourceType.Message:
                case ResourceType.Target:
                case ResourceType.Selector:
                case ResourceType.ArgumentList:
                    // ignore these, operations not tracked
                    break;
                case ResourceType.Return:
                    if (_returnValue != null)
                    {
                        ReturnValueBK.AddOperation(op);
                    }
                    break;
                case ResourceType.ArgumentEntry:
                    foreach (ArgumentInfo ai in _arguments.Values)
                    {
                        ai.ArgumentBK.AddOperation(op);
                    }
                    break;
                case ResourceType.Custom:
                    // TODO: throw error
                    break;
            }
        }

        /// <summary>
        /// Add a single resource operation to a specific argument
        /// </summary>
        /// <param name="ordinal"></param>
        /// <param name="op"></param>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddResourceOp(short ordinal, string op)
        {
            if (!_bookkeeping) return;
            if (_arguments.ContainsKey(ordinal))
            {
                _arguments[ordinal].ArgumentBK.AddOperation(op);
            }
            else
            {
                throw new ArgumentOutOfRangeException("ordinal", Properties.Resources.OrdinalCouldNotBeFound);
            }
        }

        /// <summary>
        /// Adds a operation to a custom resource. Do not call this method for standard resources 
        /// like arg, return, target, selector.
        /// </summary>
        /// <param name="resname"></param>
        /// <param name="op"></param>
        public void AddResourceOp(string resname, string op)
        {
            if (!_bookkeeping) return;
            if (_customBKs == null)
            {
                _customBKs = new Dictionary<string, SimpleBK>();
            }
            SimpleBK bk;
            if (!_customBKs.TryGetValue(resname.ToLower(), out bk))
            {
                /*
                short ord;
                ResourceType rtype = BookKeeper.getResourceType(resname, out ord);
                if (rtype != ResourceType.Custom)
                {
                    if (ord > -1 && rtype == ResourceType.ArgumentEntry)
                    {
                        AddResourceOp(ord, op);
                    }
                    else
                    {
                        AddResourceOp(rtype, op);
                    }
                    return;
                }
                */

                resname = resname.ToLower();
                bk = BookKeeperPool.getSimpleBK(ResourceType.Custom, resname);
                _customBKs.Add(resname, bk);
            }
            bk.AddOperation(op);
        }

        /// <summary>
        /// Add a list of resource operations. Each operation has the form of: type.operation and it divided by semicolons.
        /// </summary>
        /// <param name="op"></param>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddResourceOperationList(string op)
        {
            if (!_bookkeeping) return;

            int startIdx = 0;
            int idx = op.IndexOf(";", startIdx);
            if (idx < 0)
            {
                idx = op.Length;
            }
            while (idx > startIdx)
            {
                string[] sop = op.Substring(startIdx, idx - startIdx).Trim().Split('.');
                if (sop.Length == 2)
                {
                    short ord;
                    ResourceType rtype = BookKeeper.getResourceType(sop[0], out ord);
                    switch (rtype)
                    {
                        case ResourceType.Message:
                        case ResourceType.Target:
                        case ResourceType.Selector:
                        case ResourceType.ArgumentList:
                            // ignore these, operations not tracked
                            Console.Error.WriteLine("$$$ Not tracker operation: {0}.{1}", sop[0], sop[1]);
                            break;
                        case ResourceType.Return:
                            if (_returnValue != null)
                            {
                                ReturnValueBK.AddOperation(sop[1]);
                            }
                            break;
                        case ResourceType.ArgumentEntry:
                            if (ord == -1)
                            {
                                foreach (ArgumentInfo ai in _arguments.Values)
                                {
                                    ai.ArgumentBK.AddOperation(sop[1]);
                                }
                            }
                            else
                            {
                                _arguments[ord].ArgumentBK.AddOperation(sop[1]);
                            }
                            break;
                        case ResourceType.Custom:
                            AddResourceOp(sop[0], sop[1]);
                            break;
                    }
                }

                startIdx = idx + 1;
                if (startIdx >= op.Length)
                {
                    break;
                }
                idx = op.IndexOf(";", startIdx);
                if (idx < 0)
                {
                    idx = op.Length;
                }
            }
        }
        #endregion

        #region Arguments Handling

        #region Add Arguments

        /// <summary>
        /// Adds the argument of a method to the list of arguments.
        /// </summary>
        /// <param name="ordinal">The ordinal of the argument.</param>
        /// <param name="argumentType">Type of the argument.</param>
        /// <param name="value">The value of the argument.</param>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddArgument(short ordinal, Type argumentType, object value)
        {
            ArgumentInfo ai = new ArgumentInfo(argumentType, value, ordinal);
            ai.BookKeeping = _bookkeeping;
            ai.AutoBookKeeping = _autobk;
            if (_arguments.ContainsKey(ordinal))
            {
                _arguments[ordinal] = ai;
            }
            else
            {
                _arguments.Add(ordinal, ai);
            }
        }

        /// <summary>
        /// Adds the argument of a method to the list of arguments.
        /// </summary>
        /// <param name="ordinal">The ordinal.</param>
        /// <param name="argumentType">Type of the argument.</param>
        /// <param name="argumentAttributes">The argument attributes.</param>
        /// <param name="value">The value.</param>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddArgument(short ordinal, Type argumentType, ArgumentAttributes argumentAttributes, object value)
        {
            ArgumentInfo ai = new ArgumentInfo(argumentType, value, ordinal, argumentAttributes);
            ai.BookKeeping = _bookkeeping;
            ai.AutoBookKeeping = _autobk;
            if (_arguments.ContainsKey(ordinal))
            {
                _arguments[ordinal] = ai;
            }
            else
            {
                _arguments.Add(ordinal, ai);
            }
        }

        /// <summary>
        /// Adds the argument of a method to the list of arguments in the given JoinPointContext.
        /// This static method is useful to add an argument that is on the stack, because we cannot 
        /// directly place the JoinPointContext object before it on the stack.
        /// </summary>
        /// <param name="value">The value of the argument.</param>
        /// <param name="ordinal">The ordinal of the argument.</param>
        /// <param name="argumentType">Type of the argument.</param>
        /// <param name="context">The JoinPointContext to which the argument needs to be added</param>
        public static void AddArgument(object value, short ordinal, Type argumentType, JoinPointContext context)
        {
            if (context != null)
            {
                context.AddArgument(ordinal, argumentType, value);
            }
        }

        /// <summary>
        /// Adds the argument of a method to the list of arguments in the given JoinPointContext.
        /// This static method is useful to add an argument that is on the stack, because we cannot 
        /// directly place the JoinPointContext object before it on the stack.
        /// </summary>
        /// <param name="value">The value.</param>
        /// <param name="ordinal">The ordinal.</param>
        /// <param name="argumentType">Type of the argument.</param>
        /// <param name="argumentAttributes">The argument attributes.</param>
        /// <param name="context">The context.</param>
        public static void AddArgument(object value, short ordinal, Type argumentType, ArgumentAttributes argumentAttributes, JoinPointContext context)
        {
            if (context != null)
            {
                context.AddArgument(ordinal, argumentType, argumentAttributes, value);
            }
        }

        #endregion

        #region Retrieve Arguments

        /// <summary>
        /// Gets the argument value based on the ordinal.
        /// </summary>
        /// <param name="ordinal">The ordinal of the argument.</param>
        /// <returns>An <c>object</c> containing the value, or a <see langword="null"/> when the ordinal was not present in the argument list.</returns>
        /// <example>Use the <c>GetArgumentValue</c> function to retrieve a value from the list of arguments.
        /// <code>
        /// JoinPointContext jpc = new JoinPointContext();
        /// jpc.AddArgument(1, typeof(int), 1024);
        /// Object o = jpc.GetArgumentValue(1);
        /// int value = (int) o;
        /// </code>
        /// </example> 
        /// <exception cref="T:System.ArgumentOutOfRangeException">
        /// Thrown when the ordinal could not be found.
        /// </exception>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public object GetArgumentValue(short ordinal)
        {
            ArgumentInfo ai;
            if (_arguments.TryGetValue(ordinal, out ai))
            {
                return ai.Value;
            }
            else
            {
                throw new ArgumentOutOfRangeException("ordinal", Properties.Resources.OrdinalCouldNotBeFound);
            }
        }

        /// <summary>
        /// Sets the argument value based on the ordinal.
        /// </summary>
        /// <param name="ordinal">The ordinal of the argument.</param>
        /// <param name="value">The value</param>
        /// <example>Use the <c>SetArgumentValue</c> function to set a value of an argument (The argument needs to
        /// be added already by the AddArgument method).
        /// <code>
        /// JoinPointContext jpc = new JoinPointContext();
        /// jpc.AddArgument(1, typeof(int), 1024);
        /// object o = (object) 1025;
        /// jpc.SetArgumentValue(1, o);
        /// </code>
        /// </example> 
        /// <exception cref="T:System.ArgumentOutOfRangeException">
        /// Thrown when the ordinal could not be found.
        /// </exception>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void SetArgumentValue(short ordinal, object value)
        {
            ArgumentInfo ai;
            if (_arguments.TryGetValue(ordinal, out ai))
            {
                ai.Value = value;
            }
            else
            {
                throw new ArgumentOutOfRangeException("ordinal", Properties.Resources.OrdinalCouldNotBeFound);
            }
        }

        /// <summary>
        /// Gets the argument attributes.
        /// </summary>
        /// <param name="ordinal">The ordinal.</param>
        /// <returns>Returns a <see cref="T:ArgumentAttributes"/> object containing the attributes for the specified <paramref name="ordinal"/>.</returns>
        /// <exception cref="T:System.ArgumentOutOfRangeException">
        /// This exception will be raised when the ordinal could not be found in the list of arguments.
        /// </exception>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public ArgumentAttributes GetArgumentAttributes(short ordinal)
        {
            ArgumentInfo ai;
            if (_arguments.TryGetValue(ordinal, out ai))
            {
                return ai.Attributes;
            }
            else
            {
                throw new ArgumentOutOfRangeException("ordinal", Properties.Resources.OrdinalCouldNotBeFound);
            }
        }

        /// <summary>
        /// Get the <see cref="T:Composestar.StarLight.ContextInfo.ArgumentInfo"></see> object or <see langword="null"></see> when the <paramref name="ordinal"/> is not found.
        /// </summary>
        /// <param name="ordinal">The ordinal of the argument.</param>
        /// <returns>Argument info object or <see langword="null"></see> when the <paramref name="ordinal"/> is not found.</returns>
        public ArgumentInfo GetArgumentInfo(short ordinal)
        {
            ArgumentInfo ai;
            _arguments.TryGetValue(ordinal, out ai);

            return ai;

        }

        /// <summary>
        /// Gets the argument value using a generic type.
        /// </summary>
        /// <param name="ordinal">The ordinal.</param>
        /// <typeparam name="T">The type to return.</typeparam> 
        /// <returns>
        /// A strong typed value or the <c>default(T)</c> when the value could not be found.
        /// </returns>
        /// <example>Use the <c>GetGenericArgumentValue</c> function to retrieve a value from the list of arguments using a generic type.
        /// <code>
        /// JoinPointContext jpc = new JoinPointContext();
        /// jpc.AddArgument(1, typeof(int), 1024);
        /// int value = jpc.GetArgumentValue&lt;int&gt;(1);
        /// </code>
        /// </example>
        [MethodImpl(MethodImplOptions.Synchronized)]
        [SuppressMessage("Microsoft.Design", "CA1004:GenericMethodsShouldProvideTypeParameter",
            Justification = "Use the GetArgumentValue method. The weaver could not easily determine if it had to inject a generic or non generic version of the GetArgumentValue method. So by using the word Generic in the method name, it is possible. If the weaver can detect the difference between generics and non generic methods, we can change the name of this method the <T>GetArgumentValue.")]
        public T GetGenericArgumentValue<T>(short ordinal)
        {
            ArgumentInfo ai;
            if (_arguments.TryGetValue(ordinal, out ai))
                return ((T)ai.Value);
            else
                return default(T);
        }

        /// <summary>
        /// Gets the type of the argument.
        /// </summary>
        /// <param name="ordinal">The ordinal.</param>
        /// <returns>The type of the argument, or an exception when the type could not be found.</returns>
        /// <exception cref="T:System.ArgumentOutOfRangeException">
        /// This exception will be raised when the ordinal could not be found in the list of arguments.
        /// </exception>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public Type GetArgumentType(short ordinal)
        {
            ArgumentInfo ai;
            if (_arguments.TryGetValue(ordinal, out ai))
                return ai.Type;
            else
                throw new ArgumentOutOfRangeException("ordinal", Properties.Resources.OrdinalCouldNotBeFound);
        }

        /// <summary>
        /// Property to get the number of arguments
        /// </summary>
        /// <value>The number of arguments.</value>
        public int ArgumentCount
        {
            get
            {
                return _arguments.Count;
            }
        }

        /// <summary>
        /// Get an ordered list of arguments.
        /// </summary>
        /// <value>The arguments.</value>
        /// <returns>List</returns>
        public Dictionary<short, ArgumentInfo> GetArguments
        {
            get
            {
                return _arguments;
            }
        }

        #endregion

        #endregion

        #region Set Target

        /// <summary>
        /// Sets the target in the given JoinPointContext to obj. This static method is useful
        /// to store a target that is on the stack, because we cannot directly place the JoinPointContext 
        /// object before it on the stack.
        /// </summary>
        /// <param name="obj">The target object to store.</param>
        /// <param name="context">The JoinPointContext to store it in.</param>
        public static void SetTarget(object obj, JoinPointContext context)
        {
            if (context != null)
            {
                context.StartTarget = obj;
            }
        }

        #endregion

        #region Return Values

        /// <summary>
        /// Gets a value indicating whether this JoinPointContext has a return value.
        /// </summary>
        /// <value>
        /// 	<see langword="true"/> if this JoinPointContext has a return value; otherwise, <see langword="false"/>.
        /// </value>
        public bool HasReturnValue
        {
            get
            {
                return _hasReturnValueSet;
            }
        }

        /// <summary>
        /// Gets or sets the type of the return value.
        /// </summary>
        /// <value>The type of the return value.</value>
        public Type ReturnType
        {
            get
            {
                if (_returnValue != null)
                {
                    return _returnValue.Type;
                }
                else
                {
                    return null;
                }
            }
            set
            {
                if (_returnValue == null)
                {
                    _returnValue = new ArgumentInfo(value, null, -1);
                }
                else
                {
                    _returnValue.Type = value;
                }
            }
        }

        /// <summary>
        /// Gets or sets the return value.
        /// </summary>
        /// <value>The return value.</value>
        /// <exception cref="ArgumentNullException">
        /// Thrown when the <see cref="ReturnType"></see> property is <see langword="null" />.
        /// </exception>
        public Object ReturnValue
        {
            get
            {
                if (HasReturnValue)
                {
                    if (_bookkeeping && _autobk)
                    {
                        ReturnValueBK.AddOperation(BookKeeper.READ);
                    }
                    return _returnValue.Value;
                }
                else
                {
                    return null;
                }
            }
            set
            {
                if (_returnValue == null)
                {
                    throw new ArgumentNullException("returnType");
                }
                else
                {
                    if (_bookkeeping && _autobk)
                    {
                        ReturnValueBK.AddOperation(BookKeeper.WRITE);
                    }
                    _returnValue.Value = value;
                    _hasReturnValueSet = true;
                }
            }
        }

        /// <summary>
        /// Sets the return value in the given JoinPointContext to obj. This static method is useful
        /// to store a return value that is on the stack, because we cannot directly place the JoinPointContext
        /// object before it on the stack.
        /// </summary>
        /// <param name="obj">The object to store as return value.</param>
        /// <param name="context">The join point context it applies to.</param>
        public static void SetReturnValue(Object obj, JoinPointContext context)
        {
            if (context != null)
            {
                context.ReturnValue = obj;
            }
        }

        #endregion

        #region Context Properties

        /// <summary>
        /// Adds a property to the JoinPointContext, so you can use it in a following FilterAction.
        /// </summary>
        /// <param name="key">The key of the property</param>
        /// <param name="property">The value of the property</param>
        /// <example>
        /// See the following example:
        /// <code>
        /// public override void Execute(JoinPointContext context)
        /// {
        ///     long starttime = DateTime.Now.Ticks;
        ///     context.AddProperty("starttime", starttime);
        /// }
        /// </code>
        /// </example> 
        /// <exception cref="ArgumentNullException">
        /// Thrown when the <paramref name="key"/> is empty or <see langword="null" />.
        /// </exception>
        public void AddProperty(string key, object property)
        {
            if (string.IsNullOrEmpty(key))
            {
                throw new ArgumentNullException("key");
            }
            if (_bookkeeping && _autobk)
            {
                AddResourceOp(key, BookKeeper.WRITE);
            }
            _properties[key] = property;
        }

        /// <summary>
        /// Returns a property, or <see langword="null" /> if the property does not exist.
        /// </summary>
        /// <param name="key">The key of the property</param>
        /// <returns>The property, or <see langword="null" /> if the property does not exist.</returns>
        /// <example>
        /// The following example shows a StopTimerAction.
        /// <code>
        /// public override void Execute(JoinPointContext context)
        /// {
        ///     long stoptime = DateTime.Now.Ticks;
        ///     long starttime = (long)context.GetProperty("starttime");
        ///     TimeSpan buildtime = new TimeSpan(stoptime - starttime);
        ///     double timervalue = Convert.ToDouble(buildtime.Milliseconds);
        ///     Console.WriteLine("The execution of message: " + context.GetProperty("target") + "." + 
        ///     context.GetProperty("selector") + " took: " + timervalue + " msecs");
        /// }
        /// </code>
        /// </example>
        /// <exception cref="ArgumentNullException">
        /// Thrown when the <paramref name="key"/> is empty or <see langword="null" />.
        /// </exception>
        public object GetProperty(string key)
        {
            if (string.IsNullOrEmpty(key))
            {
                throw new ArgumentNullException("key");
            }

            if (_bookkeeping && _autobk)
            {
                AddResourceOp(key, BookKeeper.READ);
            }

            if (_properties.ContainsKey(key))
            {
                return _properties[key];
            }
            else
            {
                return null;
            }
        }

        /// <summary>
        /// Returns a strong typed property, or the default value of <typeparamref name="T"/> if the property does not exist.
        /// </summary>
        /// <param name="key">The key of the property</param>
        /// <typeparam name="T">The type to return.</typeparam> 
        /// <returns>
        /// The property based on the type of T, or default if the property does not exist.
        /// </returns>
        /// <example>
        /// The following example shows a StopTimerAction using the <see cref="M:GetGenericProperty(string)"/> method.
        /// <code>
        /// public override void Execute(JoinPointContext context)
        /// {
        /// long stoptime = DateTime.Now.Ticks;
        /// long starttime = context.GetGenericProperty&lt;long&gt;("starttime");
        /// TimeSpan buildtime = new TimeSpan(stoptime - starttime);
        /// double timervalue = Convert.ToDouble(buildtime.Milliseconds);
        /// Console.WriteLine("The execution of message: " + context.GetProperty("target") + "." +
        /// context.GetProperty("selector") + " took: " + timervalue + " msecs");
        /// }
        /// </code>
        /// </example>
        /// <exception cref="ArgumentNullException">
        /// Thrown when the <paramref name="key"/> is empty or <see langword="null"/>.
        /// </exception>
        [SuppressMessage("Microsoft.Design", "CA1004:GenericMethodsShouldProvideTypeParameter", Justification = "Use the GetProperty method.")]
        public T GetGenericProperty<T>(string key)
        {
            if (string.IsNullOrEmpty(key))
            {
                throw new ArgumentNullException("key");
            }

            if (_bookkeeping && _autobk)
            {
                AddResourceOp(key, BookKeeper.READ);
            }

            if (_properties.ContainsKey(key))
            {
                return ((T)_properties[key]);
            }
            else
            {
                return default(T);
            }
        }

        #endregion

    }

    #region ArgumentInfo class

    /// <summary>
    /// Internal class for storing the arguments.
    /// </summary>
    public sealed class ArgumentInfo
    {

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Composestar.StarLight.ContextInfo.ArgumentInfo"/> class.
        /// </summary>
        internal ArgumentInfo()
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Composestar.StarLight.ContextInfo.ArgumentInfo"/> class.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <param name="value">The value.</param>
        /// <param name="ordinal">The ordinal.</param>
        internal ArgumentInfo(Type type, Object value, short ordinal)
        {
            _type = type;
            _value = value;
            _ordinal = ordinal;
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Composestar.StarLight.ContextInfo.ArgumentInfo"/> class.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <param name="value">The value.</param>
        /// <param name="ordinal">The ordinal.</param>
        /// <param name="argumentAttributes">The argument attributes.</param>
        internal ArgumentInfo(Type type, Object value, short ordinal, ArgumentAttributes argumentAttributes)
        {
            _type = type;
            _value = value;
            _ordinal = ordinal;
            _argumentAttributes = argumentAttributes;
        }

        /// <summary>
        /// Release the bookkeeper to the pool
        /// </summary>
        ~ArgumentInfo()
        {
            //ReleaseBK();
        }

        #region Book Keeping
        private bool _bookkeeping = false;
        private bool _autobk = true;
        private SimpleBK _book = null;

        /// <summary>
        /// Set/get the current bookkeeping state
        /// </summary>
        public bool BookKeeping
        {
            get { return _bookkeeping; }
            set { _bookkeeping = value; }
        }


        /// <summary>
        /// Sets/gets the value of automatic book keeping. When automatic book keeping is true (default)
        /// all read and write operations will automatically be captured.
        /// </summary>
        public bool AutoBookKeeping
        {
            get { return _autobk; }
            set { _autobk = value; }
        }

        /// <summary>
        /// Release bookkeeping records from this instance
        /// </summary>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void ReleaseBK()
        {
            if (_book != null)
            {
                BookKeeperPool.release(ref _book);
            }
        }

        /// <summary>
        /// Get the bookkeeper for this object
        /// </summary>
        public SimpleBK ArgumentBK
        {
            get
            {
                if (_book == null && _bookkeeping)
                {
                    _book = BookKeeperPool.getSimpleBK(ResourceType.ArgumentEntry,
                        BookKeeper.resourceTypeAsString(ResourceType.ArgumentEntry) + _ordinal);
                }
                return _book;
            }
        }

        /// <summary>
        /// Quick method to add a resource operation to the book keeper of this instance.
        /// </summary>
        /// <param name="op"></param>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddResourceOp(string op)
        {
            if (!_bookkeeping) return;
            ArgumentBK.AddOperation(op);
        }
        #endregion

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
            get
            {
                if (_bookkeeping && _autobk)
                {
                    ArgumentBK.AddOperation(BookKeeper.READ);
                }
                return _value;
            }
            set
            {
                _value = value;
                if (_bookkeeping && _autobk)
                {
                    ArgumentBK.AddOperation(BookKeeper.WRITE);
                }
            }
        }

        private short _ordinal;

        /// <summary>
        /// The ordinal value of this argument info, the return argumentingo instance will have the ordinal -1
        /// </summary>
        public short Ordinal
        {
            get { return _ordinal; }
        }

        /// <summary>
        /// Returns true when the argument is an 'in' argument
        /// </summary>
        /// <returns><see langword="true" /> when the argument is an input argument, <see langword="false" /> other else.</returns>
        public bool IsIn()
        {
            return (Attributes & ArgumentAttributes.In) == ArgumentAttributes.In;
        }

        /// <summary>
        /// Returns true when the argument is an 'out' argument
        /// </summary>
        /// <returns><see langword="true" /> when the argument is an output argument, <see langword="false" /> other else.</returns>
        public bool IsOut()
        {
            return (Attributes & ArgumentAttributes.Out) == ArgumentAttributes.Out;
        }

        private ArgumentAttributes _argumentAttributes;

        /// <summary>
        /// Gets or sets the argument attributes.
        /// </summary>
        /// <value>The argument attributes.</value>
        public ArgumentAttributes Attributes
        {
            get { return _argumentAttributes; }
            set { _argumentAttributes = value; }
        }

    }

    #endregion

    #region Argument Attributes

    /// <summary>
    /// Argument attributes. This is a <see cref="T:System.FlagsAttribute" /> enumeration so multiple options are possible.
    /// </summary>
    [Flags]
    public enum ArgumentAttributes : int
    {
        /// <summary>
        /// Argument is an input argument.
        /// </summary>
        In = 0x0001,
        /// <summary>
        /// Argument is an output argument.
        /// </summary>
        Out = 0x0002,
        /// <summary>
        /// Argument is optional.
        /// </summary>
        Optional = 0x0010,
        /// <summary>
        /// Argument has a default value.
        /// </summary>
        HasDefault = 0x1000,
        /// <summary>
        /// Argument has a field marshal.
        /// </summary>
        HasFieldMarshal = 0x2000
    }

    #endregion
}
