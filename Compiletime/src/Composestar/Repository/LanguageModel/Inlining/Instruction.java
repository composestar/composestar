package Composestar.Repository.LanguageModel.Inlining;

public abstract class Instruction {
	private Label label;

	public Instruction()
	{

	}

	public Instruction(Label label)
	{
		this.label = label;
	}

	/**
     * @return the label
	 * @property 
     */
	public Label get_Label()
	{
		return label;
	}
}
