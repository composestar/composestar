concern ShowNumbers
{
	filtermodule ShowNumbersFM
	{
		internals
			show : Composestar.Patterns.Strategy.Show;

		inputfilters
			print : Meta = {[*.sort] show.showACT }
	}
	superimposition
	{
		selectors
			context = { Context | classHasAnnotationWithName(Context, 'Composestar.Patterns.Strategy.Annotations.Context') };
			
		filtermodules
			context <- ShowNumbersFM;
	}
	implementation in JSharp by	Show as	"Show.jsl"
	{
		package Composestar.Patterns.Strategy;

		import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;
		import java.util.List;

		public class Show
		{
			public Show(){}

			public String show(List numbers) 
			{
				String out = "";
				for (int i=0; i<numbers.size(); i++)
				{
					out += (numbers.get(i) + " ");
				}  
				return out;
			}

			public void showACT(ReifiedMessage message)
			{
				List numbers = (List) message.getArg(0);
				System.out.println("Before Sorting: "+show(numbers)); 
				message.proceed();
				System.out.println("After Sorting:   "+show(numbers));  
				System.out.println("\n");
			}
		}
	}
}