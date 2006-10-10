#line 1 "D:/dev/Purple/PurpleSharp/source/Graphics/Lighting/StencilShadow.fx"
#line 1 "Purple/Graphics/Effect/Library/structs.fx"
struct Position {
  float4 Position : POSITION;
};

struct PositionColor {
  float4 Position : POSITION;
  float4 Color : COLOR;
};

struct PositionTexture {
  float4 Position : POSITION;
  float2 Texture : TEXCOORD0;
};

struct PositionColorTexture {
  float4 Position : POSITION;
  float4 Color : COLOR;
  float2 Texture : TEXCOORD0;
};

struct PositionTexture2 {
  float4 Position : POSITION;
  float2 Texture : TEXCOORD0;
  float2 Texture2 : TEXCOORD1;
};
#line 2 "D:/dev/Purple/PurpleSharp/source/Graphics/Lighting/StencilShadow.fx"
#line 1 "Purple/Graphics/Effect/Library/shaders.fx"
float4 VS_Position(float4 position : POSITION,
                   uniform matrix wvp : mWorldViewProjection) : POSITION {
	return mul(position, wvp);	
}

PositionColor VS_PositionColor(float4 position : POSITION,
                               float4 color : COLOR,
                               uniform matrix wvp : mWorldViewProjection) {
	PositionColor output;
	output.Position = mul(position, wvp);
	output.Color = color;	
	return output;	
}

PositionTexture VS_PositionTexture(float4 position : POSITION,
                                   float2 texCoords : TEXCOORD,
                                   uniform matrix wvp : mWorldViewProjection) {
	PositionTexture output;
	output.Position = mul(position, wvp);
	output.Texture = texCoords;	
	return output;	
}

PositionColorTexture VS_PositionColorTexture(float4 position : POSITION,
                                        float4 color : COLOR,
                                        float2 texCoords : TEXCOORD,
                                        uniform matrix wvp : mWorldViewProjection) {
	PositionColorTexture output;
	output.Position = mul(position, wvp);
	output.Color = color;
	output.Texture = texCoords;	
	return output;	
}

PositionColorTexture VS_PassThrough(float4 position : POSITION,
                                        float4 color : COLOR,
                                        float2 texCoords : TEXCOORD) {
	PositionColorTexture output;
	output.Position = position;
	output.Color = color;
	output.Texture = texCoords;	
	return output;	
}

float4 PS_Color(float4 color : COLOR) : COLOR {
  return color;
}

float4 PS_Texture(float2 texCoords : TEXCOORD,
                  uniform sampler2D sTex) : COLOR {
  return tex2D(sTex, texCoords);
}

float4 PS_ColorTexture(float4 color : COLOR,
                       float2 texCoords : TEXCOORD,
                       uniform sampler2D sTex) : COLOR {
  return tex2D(sTex, texCoords) * color;
}

float4 PS_Constant(uniform float4 color: COLOR) : COLOR {
  return color;
}
#line 3 "D:/dev/Purple/PurpleSharp/source/Graphics/Lighting/StencilShadow.fx"
#line 1 "Purple/Graphics/Effect/Library/ffshaders.fx"
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
#line 4 "D:/dev/Purple/PurpleSharp/source/Graphics/Lighting/StencilShadow.fx"

#define ShadowVolumeEnable 0 //Alpha | Red | Green | Blue 

shared float4x4 mWorldViewProjection : mWorldViewProjection; 
shared float4 vLight;
float4 shadowVolumeColor = { 1.0f, 1.0f, 1.0f, 0.5f };

float4 VertexShader( float3 pos: POSITION, float away: DEPTH ) : POSITION
{
  float4 newPos; 
  if (away.x < 0.5f)
    newPos = float4( pos * vLight.w - vLight.xyz, 0);
  else
    newPos = float4(pos, 1);
  return mul(newPos, mWorldViewProjection);
}

#define SetRenderStates \
  ColorWriteEnable = ShadowVolumeEnable; \
  ZWriteEnable = false; \
  ZFunc = LessEqual
    
#define SetStencilStates \
  StencilEnable = true; \
  StencilRef = 1; \
  StencilMask = 0xFFFFFFFF; \
  StencilWriteMask = 0xFFFFFFFF
  
#define SetZPass(op) \
  StencilFunc = Always; \
  StencilZFail = Keep; \
  StencilFail = Keep; \
  StencilPass = op
  
#define SetZFail(op) \
  StencilFunc = Always; \
  StencilZFail = op; \
  StencilFail = Keep; \
  StencilPass = Keep  
  
#define SetCcwZPass(op) \
  Ccw_StencilFunc = Always; \
  Ccw_StencilZFail = Keep; \
  Ccw_StencilFail = Keep; \
  Ccw_StencilPass = op
  
#define SetCcwZFail(op) \
  Ccw_StencilFunc = Always; \
  Ccw_StencilZFail = op; \
  Ccw_StencilFail = Keep; \
  Ccw_StencilPass = Keep  

technique PSZPass < 
  string StencilMethod="ZPass"; 
  bool UsePixelShader=true;
  bool UseTwoSidedStencil=true;
>{
  pass {
    VertexShader = compile vs_1_1 VertexShader();
    PixelShader = compile ps_1_1 PS_Constant(shadowVolumeColor);
    
    //SetRenderStates;
    //CullMode = None;
    
    //SetStencilStates;
    //TwoSidedStencilMode = true;
    
    //SetZPass(Decr);
    //SetCcwZPass(Incr);
  }
}

technique PSZFail < 
  string StencilMethod="ZFail";
  bool UsePixelShader=true; 
  bool UseTwoSidedStencil=true;
> {
  pass {
    VertexShader = compile vs_1_1 VertexShader();
    PixelShader = compile ps_1_1  PS_Constant(shadowVolumeColor);
    
    SetRenderStates;
    CullMode = None;
    DepthBias = 0.001;
    
    SetStencilStates;
    TwoSidedStencilMode = true;
    
    SetZFail(Incr);
    SetCcwZFail(Decr);
  }
}

technique FFZPass < 
  string StencilMethod="ZPass"; 
  bool UsePixelShader=false;
  bool UseTwoSidedStencil=true;
> {
  pass {
    VertexShader = compile vs_1_1 VertexShader();
    PixelShader = null;    
    
    FF_Constant(shadowVolumeColor);
       
    SetRenderStates;
    CullMode = None;
    
    SetStencilStates;
    TwoSidedStencilMode = true;
    
    SetZPass(Decr);
    SetCcwZPass(Incr);
  }
}
  
technique FFZFail < 
  string StencilMethod="ZFail"; 
  bool UsePixelShader=false;
  bool UseTwoSidedStencil=true;
>{
  pass {
    VertexShader = compile vs_1_1 VertexShader();
    PixelShader = null;    
    
    FF_Constant(shadowVolumeColor);
    
    SetRenderStates;
    CullMode = None;
    DepthBias = 0.001;
    
    SetStencilStates;
    TwoSidedStencilMode = true;
    SetZFail(Incr);
    SetCcwZFail(Decr);
  }
}

technique FFZPassTwo < 
  string StencilMethod="ZPass"; 
  bool UsePixelShader=false;
  bool UseTwoSidedStencil=true;
  bool isMinimum=true;
> {
  pass ClockWise {
    VertexShader = compile vs_1_1 VertexShader();
    PixelShader = null;    
    
    FF_Constant(shadowVolumeColor);
       
    SetRenderStates;
    CullMode = Cw;
    
    SetStencilStates;
    SetZPass(Decr);
  }
  pass CounterClockWise {
    VertexShader = compile vs_1_1 VertexShader();
    PixelShader = null;    
    
    FF_Constant(shadowVolumeColor);
       
    SetRenderStates;
    CullMode = Ccw;
    
    SetStencilStates;
    SetZPass(Incr);
  }
}
  
technique FFZFail < 
  string StencilMethod="ZFail"; 
  bool UsePixelShader=false;
  bool UseTwoSidedStencil=true;
  bool isMinimum=true;
>{
  pass CloclWise {
    VertexShader = compile vs_1_1 VertexShader();
    PixelShader = null;    
    
    FF_Constant(shadowVolumeColor);
    
    SetRenderStates;
    CullMode = Cw;
    DepthBias = 0.001;
    
    SetStencilStates;
    SetZFail(Incr);
    SetCcwZFail(Decr);
  }
  pass CounterClockWise {
    VertexShader = compile vs_1_1 VertexShader();
    PixelShader = null;    
    
    FF_Constant(shadowVolumeColor);
    
    SetRenderStates;
    CullMode = Ccw;
    DepthBias = 0.001;
    
    SetStencilStates;
    SetZFail(Decr);
  }
}
