export function exportLook(matrix,curve){
 if(matrix.length!==9||!matrix.every(Number.isFinite))throw new Error('Expected nine finite matrix coefficients');
 const data=new ArrayBuffer(4136),view=new DataView(data);
 const put=(offset,value)=>{if(!Number.isFinite(value))throw new Error('Non-finite value');const q=Math.floor(value+.5);if(q < -2147483648 || q > 2147483647)throw new Error('Coefficient exceeds signed 32-bit range');view.setInt32(offset,q,false);};
 put(0,1);
 for(let i=0;i<1024;i++){const y=curve(i/1023);if(!Number.isFinite(y))throw new Error('Non-finite curve output');put(4+4*i,Math.max(0,Math.min(1023,Math.floor(1023*y+.5))));}
 matrix.forEach((m,i)=>put(4100+4*i,1024*m));
 return data;
}
