package Composestar.RuntimeCore.CODER.Visual.Inspector;

import java.awt.*;
import java.awt.event.*;

/**
 * Summary description for ObjectInspector.
 */
public class ObjectInspector extends Panel implements ActionListener{
    private class ObjectInspectorFrame extends Frame {
        public ObjectInspectorFrame(String name) {
            setTitle("Object inspector:" + name);
            setSize(500, 700);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });
        }
    }

    protected Object target;
	protected List internals;
	protected String[] variables;

    public ObjectInspector(String name, Object target) {
        Frame frame = new ObjectInspectorFrame(name);
        frame.add(this);
        this.target = target;

        setLayout(new GridLayout(0, 1));
		add(new Label("Object:"), BorderLayout.NORTH);
		List list = new List();
		list.add("Class:" + getClassName());
		if(target != null)
		{
			Class stype = target.getClass();
			while(!stype.equals(Object.class))
			{
				stype = stype.getSuperclass();
				list.add("Subclass of:" + stype.getName());
			}
		}
		list.add("Object:" + getObjectId());
		add(list);
        add(new Label("Internals:"), BorderLayout.NORTH);
		internals = new List();
		internals.addActionListener(this);
		getInternals();
		add(internals);

        frame.show();
    }

    private String getClassName() {
        return target == null ? "Null" : target.getClass().getName();
    }

    private String getObjectId() {
        return target == null ? "0" : Integer.toHexString(target.hashCode());
    }

    private void getInternals() {
        /*if (target == null) return;
        ObjectInterface inter=  ObjectInterface.getInstance();
        variables = inter.getFields(target);
        for (int i = 0; i < variables.length; i++) {
			Object value = inter.getFieldValue(target, variables[i]);
			if(value == null || value instanceof java.lang.String)
			{
				internals.add(variables[i] + '=' + value);
			}
			else
			{
				internals.add(value.getClass().getName() + ':' + variables[i] + '=' + value);
			}
        } */
    }

	public void actionPerformed(ActionEvent e)
	{
		/*int index = internals.getSelectedIndex();
		if(index >= 0 && index < variables.length)
		{
			ObjectInterface inter=  ObjectInterface.getInstance();
			String keyname = variables[index];
			Object value = inter.getFieldValue(target, variables[index]);
			if(value != null && (!(value instanceof java.lang.Number)))
			{
				new ObjectInspector(keyname,value);
			}
		}  */
	}
}
