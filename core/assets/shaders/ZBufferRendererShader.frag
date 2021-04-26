#version 330 core

out vec4 color;

uniform sampler2D tex;

in vec2 tc;
flat in int tID;

uniform sampler2D textures[32];

void main() {
    color = texture(textures[tID], tc);
    if(color.rgb == 0)
        discard;
}
