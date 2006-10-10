#include <structs.fx>
#include <shaders.fx>
#include <ffshaders.fx>

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
    
    SetRenderStates;
    CullMode = None;
    
    SetStencilStates;
    TwoSidedStencilMode = true;
    
    SetZPass(Decr);
    SetCcwZPass(Incr);
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