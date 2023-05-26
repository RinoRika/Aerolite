#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 iResolution;
uniform float iTime;
                   uniform vec2 iMouse;

                   float length2(vec2 p) { return dot(p, p); }

float noise(vec2 p){
                       return fract(sin(fract(sin(p.x) * (43.13311)) + p.y) * 31.0011);
                   }

float worley(vec2 p) {
                         float d = 1e30;
                         for (int xo = -1; xo <= 1; ++xo) {
                                                              for (int yo = -1; yo <= 1; ++yo) {
                                                                                                   vec2 tp = floor(p) + vec2(xo, yo);
                                                                                                                             d = min(d, length2(p - tp - vec2(noise(tp))));
                                                                                               }
                                                          }
                         return 3.0*exp(-4.0*abs(2.0*d - 1.0));
                     }

float fworley(vec2 p) {
                          return sqrt(sqrt(sqrt(
                          2.1 * // light
                          worley(p*5. + .3 + iTime*.0525) *
                                                   sqrt(worley(p * 50. + 0.9 + iTime * -0.15)) *
                                                                                               sqrt(sqrt(worley(p * -10. + 9.3))))));
                      }

float ffworley(vec2 p) {
                           return sqrt(sqrt(sqrt(
                           2.1 * // light
                           fworley(p*5. + .3 + iTime*.0525) *
                                                     sqrt(fworley(p * 50. + 0.9 + iTime * -0.15)) *
                                                                                                  sqrt(sqrt(fworley(p * -10. + 9.3))))));
                       }


void main() {
                vec2 uv = gl_FragCoord.xy / iResolution.xy;
                                                          float t = ffworley(uv * iResolution.xy / 1100.0);
                                                                                              t *= exp(-length2(abs(0.66*uv - 1.0)));
                                                                                                                                  gl_FragColor = vec4(t * vec3(0.8, 2.2*t, pow(t, 0.2-t)), 1.0);
            }