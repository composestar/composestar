#include <structs.fx>
#include <shaders.fx>
#include <ffshaders.fx>

shared float4x4 mWorldViewProjection : mWorldViewProjection; 

shared texture tex : color;
shared texture lightMap : lightMap;
sampler sTex = sampler_state {
  Texture = <tex>;
}; 
sampler sLightMap = sampler_state {
  Texture = <lightMap>;
};

float4 PS(float4 color : COLOR,
          float2 texCoords : TEXCOORD,
          float2 texCoords2 : TEXCOORD1,
          uniform sampler2D sTex,
          uniform sampler2D sLightMap) : COLOR {
  return tex2D(sTex, texCoords) * tex2D(sLightMap, texCoords2)*4;
}

technique PixelShaderVersion {
  pass { 
    VertexShader = compile vs_1_1 VS_PositionTexture2(mWorldViewProjection);
    PixelShader = compile ps_1_1 PS(sTex, sLightMap);
  }
}

technique FixedFunctionVersion <
  bool isMinimum = true;
> {
  pass {
    VertexShader = compile vs_1_1 VS_PositionTexture2(mWorldViewProjection);
    PixelShader = null;  
 
    Texture[0] = <tex>;
    ColorArg1[0] = Texture;
    AlphaOp[0] = MODULATE4X ;
    AlphaArg1[0] = Texture;
    ColorOp[0] = MODULATE4X ;
    Texture[1] = <lightMap>;
    ColorArg1[1] = Texture;    
  }
}