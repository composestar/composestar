#define FF_Color \
  ColorArg1[0] = Diffuse; \
  AlphaOp[0] = SelectArg1; \
  AlphaArg1[0] = Diffuse; \
  ColorOp[0] = SelectArg1
  
#define FF_Texture(tex) \
  Texture[0] = <tex>; \
  ColorArg1[0] = Texture; \
  AlphaOp[0] = SelectArg1; \
  AlphaArg1[0] = Texture; \
  ColorOp[0] = SelectArg1

#define FF_ColorTexture(tex) \
  Texture[0] = <tex>; \
  ColorArg1[0] = Texture; \
  ColorArg2[0] = Diffuse; \
  AlphaOp[0] = Modulate; \
  AlphaArg1[0] = Texture; \
  AlphaArg2[0]= Diffuse; \
  ColorOp[0] = Modulate
  
#define FF_Constant(color) \
    TextureFactor = <color>; \
    ColorArg1[0] = TFACTOR; \
    AlphaOp[0] = SelectArg1; \
    AlphaArg1[0] = TFACTOR; \
    ColorOp[0] = SelectArg1