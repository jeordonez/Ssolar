#version 330 core
        out vec4 fragColor;
        in vec3 fragPos;
        in vec3 Normal;
        in vec2 UV;
        uniform sampler2D diffuseTex;
        void main(void){
            vec3 result = texture(diffuseTex, UV).rgb;
            fragColor = vec4(result, 1.0);
        }
