package Composestar.Repository.LanguageModel.Inlining;

public class Jump extends Instruction {
	private Label target;

	public Jump(Label target)
	{
		this.target = target;
	}

	/**
     * @return the target
	 * @property 
	 */
	public Label get_Target()
	{
		return target;
	}
}
