concern TemplateMethod{	
	filtermodule TM	{
		internals
			tmc : Composestar.Patterns.TemplateMethod.TemplateMethodClass;
			
		inputfilters
			dis : Dispatch = { [*.generate] tmc.generate }
	}
	superimposition	{
		selectors
			concreteClasses = { ConcreteClasses | classHasAnnotationWithName(ConcreteClasses, 'Composestar.Patterns.TemplateMethod.Annotations.ConcreteClass')};
			abstractClass = { AbstractClass | classHasAnnotationWithName(AbstractClass, 'Composestar.Patterns.TemplateMethod.Annotations.AbstractClass')};

		filtermodules
			abstractClass <- TM;
			concreteClasses <- TM;
	}
	implementation in JSharp by	TemplateMethodClass as "TemplateMethodClass.jsl"
	{
		package Composestar.Patterns.TemplateMethod;

		public class TemplateMethodClass
		{
			public TemplateMethodClass(){}

			public String generate(String s) 
			{
				DecoratedStringGenerator dsg = (DecoratedStringGenerator)((Object)this);
				s = dsg.prepare(s);
				s = dsg.filtering(s);
				s = dsg.finalize(s);
				return s;
			}
		}

	}
}	