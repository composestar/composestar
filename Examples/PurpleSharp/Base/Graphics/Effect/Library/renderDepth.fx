#include <structs.fx>
#include <shaders.fx>

shared float4x4 mWorldViewProjection : mWorldViewProjection; 

technique RenderDepth <
  bool isMinimum = true;
>{
  pass { 
    VertexShader = compile vs_1_1 VS_Position(mWorldViewProjection);
    PixelShader = null;
    
    ColorWriteEnable = 0;
    ZEnable = true;
    ZWriteEnable = true;
    ZFunc = Less;
    AlphaTestEnable = false;
    
    StencilEnable = false;
  }
}