concern ShowNumbers
{
	filtermodule ShowNumbersFM
	{
		internals
			show : Composestar.Patterns.Strategy.Show;

		inputfilters
			print1 : Before = {[*.sort] show.showACTBefore };
			print2 : After = {[*.sort] show.showACTAfter }
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

		import Composestar.StarLight.ContextInfo.JoinPointContext;
		import java.util.List;

		public class Show
		{
			List numbers;
			
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

			public void showACTBefore(JoinPointContext jpc)
			{
				numbers = (List) jpc.get_ArgumentValue((short) 0);
				System.out.println("Before Sorting: "+show(numbers)); 
			}
			
			public void showACTAfter(JoinPointContext jpc)
			{
				System.out.println("After Sorting:   "+show(numbers));  
				System.out.println("\n");
			}
		}
	}
}