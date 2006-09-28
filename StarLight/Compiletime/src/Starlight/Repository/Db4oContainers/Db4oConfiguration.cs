using System;
using System.Collections.Generic;
using System.Text;

using com.db4o;

using Composestar.Repository.LanguageModel;

namespace Composestar.Repository.Db4oContainers
{
    static class Db4oConfiguration
    {
        private static bool _isInitialized = false;

        public static void Configure()
        {
            if (!_isInitialized)
            {
                Db4o.Configure().CallConstructors(false);

                Db4o.Configure().ObjectClass(typeof(MethodElement)).CascadeOnUpdate(true);
                Db4o.Configure().ObjectClass(typeof(Composestar.Repository.LanguageModel.Inlining.Block)).CascadeOnActivate(true);
                Db4o.Configure().ObjectClass(typeof(Composestar.Repository.LanguageModel.Inlining.FilterAction)).CascadeOnActivate(true);
                Db4o.Configure().ObjectClass(typeof(Composestar.Repository.LanguageModel.LinkedList)).CascadeOnActivate(true);
                Db4o.Configure().ObjectClass(typeof(Composestar.Repository.LanguageModel.LinkedListEntry)).CascadeOnActivate(true);
                Db4o.Configure().ObjectClass(typeof(Composestar.Repository.LanguageModel.MethodBody)).CascadeOnActivate(true);
                Db4o.Configure().ObjectClass(typeof(Composestar.Repository.LanguageModel.Condition)).CascadeOnActivate(true);               
                Db4o.Configure().ObjectClass(typeof(Composestar.Repository.LanguageModel.ConditionExpressions.ConditionExpression)).CascadeOnActivate(true);               
                Db4o.Configure().ObjectClass(typeof(Composestar.Repository.LanguageModel.ConditionExpressions.And)).CascadeOnActivate(true);               
                Db4o.Configure().ObjectClass(typeof(Composestar.Repository.LanguageModel.ConditionExpressions.Or)).CascadeOnActivate(true);               
                Db4o.Configure().ObjectClass(typeof(Composestar.Repository.LanguageModel.ConditionExpressions.Not)).CascadeOnActivate(true);         
                Db4o.Configure().ObjectClass(typeof(Composestar.Repository.LanguageModel.ConditionExpressions.ConditionLiteral)).CascadeOnActivate(true);               

                // Indexes
                Db4o.Configure().ObjectClass(typeof(AssemblyElement)).ObjectField("_fileName").Indexed(true);

                Db4o.Configure().ObjectClass(typeof(TypeElement)).ObjectField("_id").Indexed(true);
                Db4o.Configure().ObjectClass(typeof(TypeElement)).ObjectField("_fullName").Indexed(true);
                Db4o.Configure().ObjectClass(typeof(TypeElement)).ObjectField("_AFQN").Indexed(true);
                Db4o.Configure().ObjectClass(typeof(TypeElement)).ObjectField("_fromDLL").Indexed(true);

                Db4o.Configure().ObjectClass(typeof(FieldElement)).ObjectField("_parentTypeId").Indexed(true);

                Db4o.Configure().ObjectClass(typeof(MethodElement)).ObjectField("_parentTypeId").Indexed(true);
                Db4o.Configure().ObjectClass(typeof(MethodElement)).ObjectField("_signature").Indexed(true);

                Db4o.Configure().ObjectClass(typeof(ParameterElement)).ObjectField("_parentMethodId").Indexed(true);

                Db4o.Configure().ObjectClass(typeof(AttributeElement)).ObjectField("_parentId").Indexed(true);

                _isInitialized = true;
            }
        }
    }
}
