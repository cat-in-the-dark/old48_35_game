#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;
uniform vec2 pos;
uniform vec2 cam_pos;
uniform float player_rot;
uniform float phi;
uniform float max_dist;
uniform vec2 resolution;

void main() {
        vec2 orig = vec2(1161, 652);
        vec4 color = texture2D(u_texture, v_texCoords).rgba;

        //vec2 lpos = vec2(max(cam_pos.x - pos.x, 0), max(cam_pos.y - pos.y, 0));
//        vec4 lpos = vec4(pos.xy ,0 ,0) * cam_proj;
//        lpos = vec4(pos.xy, 0, 0);
        vec2 cam_delta = vec2(orig.x / 2.0, orig.y / 2.0) - cam_pos;
        vec2 lpos = pos + cam_delta;
        lpos = lpos * resolution / orig;

        vec2 dir = vec2(1.0, tan(radians(player_rot)));
        vec4 texColor = texture2D(u_texture, v_texCoords);
        float cos_t = dot(normalize(gl_FragCoord.xy - lpos.xy), normalize(dir));

        cos_t = cos_t * sign(cos(radians(player_rot)));
        cos_t = max(cos_t, 0.0);
        cos_t = pow(cos_t, 5);
        cos_t = min(cos_t * phi, 1.0);

        float deg =  pow(0.95, distance(lpos.xy, gl_FragCoord.xy) / (max_dist / 70.0));
        cos_t = cos_t * deg;

        if(distance(lpos.xy, gl_FragCoord.xy ) > max_dist)
            cos_t = 0.0;

        gl_FragColor = vec4(texColor.r * cos_t, texColor.g * cos_t, texColor.b * cos_t, texColor.a);
}