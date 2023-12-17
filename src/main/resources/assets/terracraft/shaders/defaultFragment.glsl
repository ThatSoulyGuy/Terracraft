#version 330 core

in vec3 color;
in vec2 texCoords;

out vec4 fragColor;

uniform sampler2D diffuse;

void main()
{
    fragColor = texture(diffuse, texCoords) * vec4(color, 1.0);
}