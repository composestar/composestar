#include <structs.fx>
#include <shaders.fx>
#include <ffshaders.fx>

shared float4x4 mViewProjection : mViewProjection;
shared texture tex : color;

sampler sTex = sampler_state {
  Texture = <tex>;
};

technique PixelShaderVersion {
  pass {
    VertexShader = compile vs_1_1 VS_PositionColorTexture(mViewProjection);
    PixelShader = compile ps_1_1 PS_ColorTexture(sTex);
  }
}

technique FixedFunctionVersion  <
  bool isMinimum = true;
>{
  pass {
    VertexShader = compile vs_1_1 VS_PositionColorTexture(mViewProjection);
    PixelShader = null;    
    
    FF_ColorTexture(tex);    
  }
}