#include <structs.fx>
#include <shaders.fx>
#include <ffshaders.fx>

shared float4x4 mWorldViewProjection : mWorldViewProjection; 

shared texture tex : color;
sampler sTex = sampler_state {
  Texture = <tex>;
}; 

technique PixelShaderVersion {
  pass { 
    VertexShader = compile vs_1_1 VS_PositionTexture(mWorldViewProjection);
    PixelShader = compile ps_1_1 PS_Texture(sTex);
  }
}

technique FixedFunctionVersion <
  bool isMinimum = true;
> {
  pass {
    VertexShader = compile vs_1_1 VS_PositionTexture(mWorldViewProjection);
    PixelShader = null;    
    
    FF_Texture(tex);
  }
}