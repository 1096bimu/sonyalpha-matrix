export function matrix({a,b,c,d,e,f}){return [1+a,b,-a-b,c,1+d,-c-d,-e,f,1+e-f];}
export function transform(m,r,g,b){return [m[0]*r+m[1]*g+m[2]*b,m[3]*r+m[4]*g+m[5]*b,m[6]*r+m[7]*g+m[8]*b];}
