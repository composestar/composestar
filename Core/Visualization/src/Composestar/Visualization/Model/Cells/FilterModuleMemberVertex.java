/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Model.Cells;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;

/**
 * A vertex for FilterModuleMembers (Internals/Externals/Conditions)
 * 
 * @author Michiel Hendriks
 */
public class FilterModuleMemberVertex extends BaseGraphCell
{
	private static final long serialVersionUID = -4055018733199723486L;

	private static Map<MemberType, Icon> iconMap;

	public enum MemberType
	{
		INTERNAL, EXTERNAL, CONDITION
	}

	/**
	 * The string representation of this vertex. Because the Filter object
	 * doesn't return a nice enough string we use this.
	 */
	protected String label;

	public FilterModuleMemberVertex(DeclaredRepositoryEntity entity)
	{
		super(entity);
		processEntity(entity);
	}

	public String toString()
	{
		return label;
	}

	@Override
	public DefaultPort getPort()
	{
		return null;
	}

	protected void setDefaults()
	{
		Map map = getAttributes();
		GraphConstants.setFont(map, new Font("sansserif", Font.PLAIN, 11));
		GraphConstants.setHorizontalAlignment(map, JLabel.LEFT);
		GraphConstants.setHorizontalTextPosition(map, JLabel.RIGHT);
		Rectangle2D bounds = new Rectangle2D.Double(0, 0, 100, 14);
		GraphConstants.setBounds(map, bounds);
	}

	protected void processEntity(DeclaredRepositoryEntity entity)
	{
		Icon ico = null;
		if (entity instanceof External)
		{
			label = entity.asSourceCode();
			ico = getIcon(MemberType.EXTERNAL);
		}
		else if (entity instanceof Internal)
		{
			label = entity.asSourceCode();
			ico = getIcon(MemberType.INTERNAL);
		}
		else if (entity instanceof Condition)
		{
			label = entity.asSourceCode();
			ico = getIcon(MemberType.CONDITION);
		}
		else
		{
			label = "#UNKNOWN MEMBER#";
		}
		if (ico != null)
		{
			GraphConstants.setIcon(getAttributes(), ico);
		}
	}

	/**
	 * Returns an icon for the provided filtermodule member type.
	 * 
	 * @param visibility
	 * @return
	 */
	public static Icon getIcon(MemberType mtype)
	{
		if (iconMap == null)
		{
			iconMap = new HashMap<MemberType, Icon>();
		}
		if (!iconMap.containsKey(mtype))
		{
			URL resUrl = null;
			if (mtype == MemberType.INTERNAL)
			{
				resUrl = ClassMembersVertex.class.getResource("Graphics/fmm_internal.png");
			}
			else if (mtype == MemberType.EXTERNAL)
			{
				resUrl = ClassMembersVertex.class.getResource("Graphics/fmm_external.png");
			}
			else if (mtype == MemberType.CONDITION)
			{
				resUrl = ClassMembersVertex.class.getResource("Graphics/fmm_condition.png");
			}
			Icon ico = null;
			if (resUrl != null)
			{
				ico = new ImageIcon(resUrl);
			}
			iconMap.put(mtype, ico);
		}
		return iconMap.get(mtype);
	}
}
