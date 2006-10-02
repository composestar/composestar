set VariablesFile=%~dp0Variables.wxi
candle -dVariablesFile="%VariablesFile%" WixStarLight.wxs WixProlog.wxs WixDb4o.wxs WixGroove.wxs WixAntlr.wxs VSIntegration.wxs CoreElements.wxs
light -out StarLight.msi WixStarLight.wixobj WixProlog.wixobj WixDb4o.wixobj VSIntegration.wixobj CoreElements.wixobj WixAntlr.wixobj WixGroove.wixobj wixui.wixlib -loc WixUI_en-us.wxl