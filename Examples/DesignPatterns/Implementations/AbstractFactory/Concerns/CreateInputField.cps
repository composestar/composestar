concern AbstractConcreteFactories
{
	//Evolution1: add a new product kind inputfield
	filtermodule InputFieldPlain
	{
		internals
			inputField : Composestar.Patterns.AbstractFactory.CreateInputField;

		inputfilters
			if : Dispatch = { [*.createInputField] inputField.createInputFieldPlain}
	}
	filtermodule InputFieldBorder
	{
		internals
			inputField : Composestar.Patterns.AbstractFactory.CreateInputField;

		inputfilters
			if : Dispatch = { [*.createInputField] inputField.createInputFieldBorder}
	}
	superimposition
	{
		selectors
			framedFactory = { C | isClassWithName(C, 'Composestar.Patterns.AbstractFactory.FramedFactory') };
			regularFactory = { C | isClassWithName(C, 'Composestar.Patterns.AbstractFactory.RegularFactory') };
			
		filtermodules
			regularFactory <- InputFieldPlain;
			framedFactory <- InputFieldBorder;
	}
	implementation in JSharp by	CreateInputField as	"CreateInputField.jsl"
	{
		package	Composestar.Patterns.AbstractFactory;

		import javax.swing.JTextField;
		import javax.swing.border.Border;
		import javax.swing.BorderFactory;

		public class CreateInputField
		{
			public CreateInputField(){}

			public JTextField createInputFieldPlain()
			{
				System.out.println("RegularFactory.createInputField()");
				return new JTextField("This	is an input	field");
			}

			public JTextField createInputFieldBorder()
			{
				System.out.println("FramedFactory.createInputField()");
				JTextField inputField =	new	JTextField("This is	an input field");
				Border raisedbevel = BorderFactory.createRaisedBevelBorder();
				Border loweredbevel	= BorderFactory.createLoweredBevelBorder();
				inputField.setBorder(BorderFactory.createCompoundBorder(raisedbevel, loweredbevel));
				return inputField;
			}

			//createInputFieldColored can invasively be	added here but is not nice
			//create an	input field	for	a new factory immediately in the factory
		}

	}
}
