#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
void main()
{
    vec4 tex = texture2D(u_texture, v_texCoords);
    if(tex.rgb == 0)
        gl_FragColor = vec4(0);
    else
        gl_FragColor = v_color * tex;
}
