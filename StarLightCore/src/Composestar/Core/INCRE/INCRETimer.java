package Composestar.Core.INCRE;

public class INCRETimer 
{
	public static final int TYPE_ALL = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_INCREMENTAL = 2;
    public static final int TYPE_OVERHEAD = 3;	
   
    private String module;
    private String description;
    private int type = 0;
    private long startTime;
	private long stopTime;
	private long elapsedTime;
	
	public INCRETimer(String module, String description, int type){
		this.module = module;
		this.description = description;
		this.type = type;
	} 

	public void start()
	{
		this.startTime = System.currentTimeMillis();
	}

	public void stop()
	{
		this.stopTime = System.currentTimeMillis();
		this.elapsedTime = this.stopTime - this.startTime;	
	}

	public long getElapsed()
	{
		return this.elapsedTime;
	}
	
	public String getModule(){
		return this.module;		
	}
	
	public String getDescription(){
		return this.description;		
	}
	
	public int getType(){
		return this.type;		
	}
	
	public String strType(){
		switch (this.type)
        {
            case 0:
                return "all";
            case 1:
                return "normal";
            case 2:
                return "incremental";    
            default:
                return "overhead";
        }
	}
}

