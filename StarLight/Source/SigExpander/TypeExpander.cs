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
using System.Text;
using System.Collections.Generic;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.StarLight.Entities.LanguageModel;
#endregion

namespace Composestar.StarLight.SigExpander
{
	public class TypeExpander
	{
		private TypeDefinition _type;
		private TypeResolver _resolver;

		public TypeExpander(TypeDefinition type, TypeResolver resolver)
		{
			_type = type;
			_resolver = resolver;
		}

		public void AddEmptyMethod(MethodElement me)
		{
		//	Console.WriteLine("\t" + me.Name);

			TypeReference returnType = _resolver.ForceResolve(me.ReturnType);
			MethodDefinition newMethod = new MethodDefinition(me.Name, MethodAttributes.Public, returnType);

			foreach (ParameterElement pe in me.Parameters)
			{
				TypeReference paramType = _resolver.ForceResolve(pe.Type);
				ParameterDefinition param = new ParameterDefinition(paramType);
				param.Name = pe.Name;

			//	Console.WriteLine("\t\t" + param.Name + " :: " + param.ParameterType);

				newMethod.Parameters.Add(param);
			}

			Type exceptionType = typeof(NotImplementedException);
			MethodReference cons = _type.Module.Import(exceptionType.GetConstructor(new Type[] {}));

			CilWorker worker = newMethod.Body.CilWorker;
			worker.Append(worker.Create(OpCodes.Nop));
			worker.Append(worker.Create(OpCodes.Newobj, cons));
			worker.Append(worker.Create(OpCodes.Throw));

			_type.Methods.Add(newMethod);
		}
	}
}
