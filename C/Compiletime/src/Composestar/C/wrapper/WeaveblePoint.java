package Composestar.C.wrapper;

import java.util.ArrayList;

import Composestar.C.CONE.WeavingInstruction;
import Composestar.C.specification.Advice;
import Composestar.C.specification.AdviceApplication;

public class WeaveblePoint
{
	public String filename = "";

	public ArrayList weavinginstructions = new ArrayList();

	public WeaveblePoint(String filename)
	{
		this.filename = filename;
	}

	public void addWeavingInstruction(WeavingInstruction wi)
	{
		this.weavinginstructions.add(wi);
	}

	public WeavingInstruction getWeavingInstruction(int i)
	{
		return (WeavingInstruction) this.weavinginstructions.get(i);
	}

	public int getNumberOfWeaveInstructions()
	{
		return this.weavinginstructions.size();
	}

	public static void main(String[] args)
	{
		WeaveblePoint wp = new WeaveblePoint("");
		Advice adv0 = new Advice();
		adv0.setId("ADV0");
		adv0.setPriority(0);
		wp.addWeavingInstruction(new WeavingInstruction("", adv0, new AdviceApplication()));
		Advice adv2 = new Advice();
		adv2.setId("ADV2");
		adv2.setPriority(2);
		wp.addWeavingInstruction(new WeavingInstruction("", adv2, new AdviceApplication()));
		Advice adv1 = new Advice();
		adv1.setId("ADV1");
		adv1.setPriority(1);
		wp.addWeavingInstruction(new WeavingInstruction("", adv1, new AdviceApplication()));
		Advice adv00 = new Advice();
		adv00.setId("ADV00");
		adv00.setPriority(0);
		wp.addWeavingInstruction(new WeavingInstruction("", adv00, new AdviceApplication()));

		wp.prioritizeWeavingInstructions();
	}

	public ArrayList prioritizeWeavingInstructions()
	{
		// System.out.println("Evaluating advices:
		// "+this.weavinginstructions.size());
		ArrayList backup = new ArrayList(this.weavinginstructions);
		ArrayList newlist = new ArrayList();
		// System.out.println("Old: "+backup);
		// System.out.println("New: "+newlist);
		// For the first simply add it to the new list!
		if (backup.size() > 0)
		{
			newlist.add(backup.remove(0));
		}
		for (int i = 0; i < backup.size(); i++)
		{
			// System.out.println("Old: "+backup);
			// System.out.println("New: "+newlist);
			WeavingInstruction wi = (WeavingInstruction) backup.remove(i);
			// We removed an element thus decrease counter!
			i--;
			Advice advice = wi.getAdvice();
			// System.out.println("advice["+advice.getId()+"]:
			// \t\t"+advice.getPriority()+" <= ");
			int tmp = newlist.size();
			boolean found = false;
			for (int j = 0; j < tmp; j++)
			{
				WeavingInstruction wit = (WeavingInstruction) newlist.get(j);
				Advice adv = wit.getAdvice();
				// System.out.print("\tadvice["+adv.getId()+"]:
				// \t"+adv.getPriority());
				// System.out.println(" ==
				// "+(advice.getPriority()<=adv.getPriority()));
				if (advice.getPriority() <= adv.getPriority())
				{
					newlist.add(j, wi);
					j = tmp;
					found = true;
				}
			}
			// System.out.println("FOUND: "+found);
			if (!found)
			{
				newlist.add(wi);
			}
		}
		// System.out.println("Old: "+backup);
		// System.out.println("New: "+newlist);
		return newlist;
	}

	public String getFileName()
	{
		return this.filename;
	}

	public void SetFileName(String name)
	{
		this.filename = name;
	}
}
