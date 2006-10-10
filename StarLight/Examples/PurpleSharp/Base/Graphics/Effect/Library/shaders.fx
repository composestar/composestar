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

PositionTexture2 VS_PositionTexture2(float4 position : POSITION,
                                     float2 texCoords : TEXCOORD0,
                                     float2 texCoords2 : TEXCOORD1,
                                     uniform matrix wvp : mWorldViewProjection) {
	PositionTexture2 output;
	output.Position = mul(position, wvp);
	output.Texture = texCoords;	
	output.Texture2 = texCoords2;
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
