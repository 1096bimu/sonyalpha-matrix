// Normalize the positive derivative's integral to anchor (0,0) and (1,1).
export function normalizedSlopes(slopes){
 const s=slopes.map(v=>Math.max(.05,Math.min(3,v)));
 const total=s.slice(0,4).reduce((sum,v,i)=>sum+(v+s[i+1])/8,0);
 return s.map(v=>v/total);
}
export function toneCurve(slopes){
 const s=normalizedSlopes(slopes),area=[0];
 for(let i=0;i<4;i++)area.push(area[i]+(s[i]+s[i+1])/8);
 return x=>{if(x<=0)return x*s[0];if(x>=1)return 1+(x-1)*s[4];const i=Math.min(3,Math.floor(x*4)),t=x-i/4;return area[i]+s[i]*t+2*(s[i+1]-s[i])*t*t;};
}
