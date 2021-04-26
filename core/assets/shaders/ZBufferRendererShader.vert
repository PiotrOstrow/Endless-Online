#version 330 core

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 texCoords;
layout (location = 2) in float textureSamplerID;

uniform mat4 projection;

out vec2 tc;
flat out int tID;

void main() {
    gl_Position = projection * position;
    tc = texCoords;
    tID = int(textureSamplerID);
}