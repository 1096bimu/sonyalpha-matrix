import {matrix} from './matrix.mjs';
export const U=[2,-1,-1].map(v=>v/Math.sqrt(6)), V=[0,1,-1].map(v=>v/Math.sqrt(2)), N=[1,1,1].map(v=>v/Math.sqrt(3));
const dot=(a,b)=>a.reduce((s,v,i)=>s+v*b[i],0);
const mul=(m,v)=>[0,1,2].map(i=>dot(m.slice(i*3,i*3+3),v));
export function decompose(values){const m=matrix(values),mu=mul(m,U),mv=mul(m,V);return {a:[dot(U,mu),dot(U,mv),dot(V,mu),dot(V,mv)],t:[dot(N,mu),dot(N,mv)]};}
export function compose({a,t}){const m=[];for(let i=0;i<3;i++)for(let j=0;j<3;j++)m.push(N[i]*N[j]+U[i]*(a[0]*U[j]+a[1]*V[j])+V[i]*(a[2]*U[j]+a[3]*V[j])+N[i]*(t[0]*U[j]+t[1]*V[j]));return {a:m[0]-1,b:m[1],c:m[3],d:m[4]-1,e:-m[6],f:m[7]};}
export const vertices=[{name:'Red',rgb:[1,0,0],color:'#f37170'},{name:'Yellow',rgb:[1,1,0],color:'#e8d76b'},{name:'Green',rgb:[0,1,0],color:'#84d68a'},{name:'Cyan',rgb:[0,1,1],color:'#74d5d8'},{name:'Blue',rgb:[0,0,1],color:'#809bfa'},{name:'Magenta',rgb:[1,0,1],color:'#d48ce4'}].map(v=>({...v,p:[dot(U,v.rgb),dot(V,v.rgb)]}));
export function project(state,p){return [state.a[0]*p[0]+state.a[1]*p[1],state.a[2]*p[0]+state.a[3]*p[1]];}
export function dragTarget(values,index,delta){const state=decompose(values),p=vertices[index].p,den=dot(p,p);state.a=state.a.map((v,i)=>v+delta[i<2?0:1]*p[i%2]/den);return compose(state);}
