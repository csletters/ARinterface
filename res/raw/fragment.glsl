precision mediump float;
uniform sampler2D uTexture;
varying vec2 vTexCord;
uniform float fading;
void main() {

	/*vec3 lightPos = vec3(0.0,2.0,2.0);
	vec3 v = normalize(-vPosition.xyz);
	
	vec3 l = normalize(lightPos - vPosition.xyz);
	vec3 N = normalize(normalizednormal);
	
	vec3 R = reflect(-l,N);
	
	vec3 diffuse = max(dot(N,l),0.0)*vec3(0.5);
	vec3 specular = pow(max(dot(R,v),0.0),128.0)*vec3(0.5);
	vec3 ambient = vec3(0.4);
	vec3 phongshading = diffuse +specular+ ambient;*/
	
	vec2 flipped_texcoord = vec2(vTexCord.x, 1.0 - vTexCord.y);
	gl_FragColor = texture2D(uTexture, flipped_texcoord)- fading;
}