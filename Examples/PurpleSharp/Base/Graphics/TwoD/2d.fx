#include <structs.fx>
#include <shaders.fx>
#include <ffshaders.fx>

shared texture tex : color;

sampler sTex = sampler_state {
  Texture = <tex>;
  MinFilter = Linear;
  MipFilter = Linear;
  MagFilter = Linear;
};

technique PixelShaderVersion {
  pass {
    CullMode = None;
    AlphaBlendEnable = true;
    SrcBlend = SRCALPHA;
    DestBlend = INVSRCALPHA;
    ZEnable = false;
    AddressU[0] = Clamp;
    AddressV[0] = Clamp;
      
    VertexShader = compile vs_1_1 VS_PassThrough();
    PixelShader = compile ps_1_1 PS_ColorTexture(sTex);
  }
}

technique FixedFunctionVersion <
  bool isMinimum = true;
>
{
  pass {
    CullMode = CCW;
    AlphaBlendEnable = true;
    SrcBlend = SRCALPHA;
    DestBlend = INVSRCALPHA;
    ZEnable = false;
    AddressU[0] = Clamp;
    AddressV[0] = Clamp;
    
    VertexShader = compile vs_1_1 VS_PassThrough();
    PixelShader = null; 
        
    FF_ColorTexture(tex);        
  }
}

