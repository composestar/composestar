package Composestar.RuntimeCore.CODER.Visual.Representation;

import Composestar.RuntimeCore.CODER.DesignTime;
import Composestar.RuntimeCore.CODER.ExecutionStackItem;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.*;

/**
 * Summary description for CodeDebugger.
 */
public class CodeDebugger extends JPanel implements  ActionListener{
	private CodeExecutionGuiComponent component = new CodeExecutionGuiComponent();

    private JFrame frame;
    private DesignTime designTime;

    private Button next = new Button("Next");
    private Button into = new Button("Into");
    private Button out = new Button("Out");
    private Panel control = new Panel();
    private WindowAdapter closer = new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		};

    public CodeDebugger(DesignTime designTime)
	{
        this.designTime = designTime;
        control.add(next);
        next.addActionListener(this);
        control.add(into);
        into.addActionListener(this);
        control.add(out);
        out.addActionListener(this);
    }

    public void go(ExecutionStackItem state){
        frame = new JFrame();
        frame.setSize(1024,800);
        frame.addWindowListener(closer);
        setLayout(new BorderLayout());

        add(control,BorderLayout.NORTH);
        component.renderState(state, this.designTime);
        add(component,BorderLayout.SOUTH);
        frame.add(this);
        frame.show();
    }

    public void actionPerformed(ActionEvent evt)
	{
        synchronized(this){
            if(frame != null){
                frame.dispose();
                frame = null;
            }
        }
        designTime.nextStep();
    }
}
