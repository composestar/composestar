using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.VisualStudio.TextManager.Interop;

namespace Trese.ComposestarSupport
{
	class ColorableItem : IVsColorableItem
	{
		private String m_name;
		private COLORINDEX m_foreground;
		private COLORINDEX m_background;

		public ColorableItem(String name, COLORINDEX foreground, COLORINDEX background)
		{
			m_name = name;
			m_foreground = foreground;
			m_background = background;
		}

		public int GetDefaultColors(COLORINDEX[] piForeground, COLORINDEX[] piBackground)
		{
			piForeground[0] = m_foreground;
			piBackground[0] = m_background;
			return Microsoft.VisualStudio.VSConstants.S_OK;
		}

		public int GetDefaultFontFlags(out uint pdwFontFlags)
		{
			pdwFontFlags = 0;
			return Microsoft.VisualStudio.VSConstants.S_OK;
		}

		public int GetDisplayName(out string pbstrName)
		{
			pbstrName = m_name;
			return Microsoft.VisualStudio.VSConstants.S_OK;
		}
	}
}
