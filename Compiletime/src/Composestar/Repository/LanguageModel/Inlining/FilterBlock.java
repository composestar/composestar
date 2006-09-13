package Composestar.Repository.LanguageModel.Inlining;

public class FilterBlock extends Block {
	private String type;

	public FilterBlock(Label label, String type)
	{
		this.type = type;
	}
	
	/**
     * @return the type
	 * @property 
	 */
	public String get_Type()
	{
		return type;
	}
}
