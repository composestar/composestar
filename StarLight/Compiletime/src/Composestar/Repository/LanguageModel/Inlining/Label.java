package Composestar.Repository.LanguageModel.Inlining;

public class Label extends Instruction {
	private int id;

	public Label()
	{
		this.id = -1;
	}

	public Label(int id)
	{
		this.id = id;
	}

	/**
     * @return the id
	 * @property 
     */
	public int get_Id()
	{
		return id;
	}

	/**
     * @param id the id to set
	 * @property 
     */
	public void set_Id(int id)
	{
		this.id = id;
	}
}
