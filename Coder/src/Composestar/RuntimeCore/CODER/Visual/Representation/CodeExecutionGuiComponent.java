package Composestar.RuntimeCore.CODER.Visual.Representation;

import Composestar.RuntimeCore.CODER.ExecutionStackItem;
import Composestar.RuntimeCore.CODER.DesignTime;
import Composestar.RuntimeCore.CODER.SourceReference;
import Composestar.RuntimeCore.CODER.SymbolManager;

import java.awt.*;


/**
 * Insert the type's description here.
 * Creation date: (5-4-2003 15:59:35)
 *
 * @author: Administrator
 */
public class CodeExecutionGuiComponent extends Panel {
    TextArea source = new TextArea("Source");
    TextArea[] sources = {new TextArea("Target")};
    TextArea[] messages = {new TextArea("Message")};

    public CodeExecutionGuiComponent()
	{
		setLayout(new GridLayout(0,1));

        add(source);
        source.setEditable(false);

        add(messages[0]);
        messages[0].setEditable(false);
        
        add(sources[0]);
        sources[0].setEditable(false);
    }

    private ExecutionStackItem state;
    private DesignTime designTime;

    public void renderState(ExecutionStackItem state, DesignTime designTime){
        this.state = state;
        this.designTime = designTime;

        SourceReference sourceReference = SymbolManager.getSourceReferenceSender(this.state.getSender());
        source.setText(sourceReference.readSource());
    }

    /*
    public void createNewRow(int i)
	{
		accepting[i] = new AcceptRejectGuiComponent(100);

		components[i] = new TextArea();
		components[i].setEditable(false);
				
		messages[i] = new TextArea();
		messages[i].setEditable(false);
		
		Panel panel = new Panel();
		panel.setLayout(new BorderLayout());
		panel.add(accepting[i],BorderLayout.WEST);
		panel.add(components[i], BorderLayout.CENTER);
		panel.add(messages[i], BorderLayout.EAST);
		add(panel);
	}

	public String getSenderText(StateHandler handler)
	{
		EntryPoint point = handler.getEntryPoint();
		if(point.getLineNumber() == EntryPoint.UNDEFINED)
		{
			return point.getFileName();
		}
		return getCodeFormfile(point.getFileName(),point.getLineNumber());
	}

	private String getCodeFormfile(String filename, int lineNumber)
	{
		String line;
		if(filename == null)
		{
			return "Unable to read codefile";
		}
		try
		{
			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			int lines = lineNumber;
			line = in.readLine();
			lines--;
			while(lines > 0 )
			{
				line = in.readLine();
				if(line == null)
					return "Unable to read " + filename + ':' + lineNumber;
				lines--;
			}
		}
		catch(Exception e)
		{
			return "Unable to read " + filename + ':' + lineNumber;
		}
		return filename + ':' + lineNumber + '\n' + line;
	}

	public String getCode(FilterRuntime filter)
	{
		if(filter.isDummy())
		{
			return "Default dispatch filter";
		}
		return getCodeFormfile(filter.getDeclerationFileName(),filter.getDeclerationLineNumber());
	}   */
}