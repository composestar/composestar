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