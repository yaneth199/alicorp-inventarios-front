document.addEventListener('DOMContentLoaded',()=>{
  const menuBtn=document.querySelector('[data-menu]');
  if(menuBtn)menuBtn.addEventListener('click',()=>document.querySelector('.sidebar')?.classList.toggle('open'));
  document.querySelectorAll('[data-confirm]').forEach(el=>el.addEventListener('click',e=>{if(!confirm(el.dataset.confirm||'¿Confirmar esta acción?'))e.preventDefault();}));
  const eye=document.querySelector('[data-toggle-password]');
  if(eye)eye.addEventListener('click',()=>{const input=document.getElementById('password');input.type=input.type==='password'?'text':'password';eye.textContent=input.type==='password'?'👁':'🙈';});
});

function forgotPassword(){alert('Para este prototipo, comunícate con el administrador del sistema para restablecer la contraseña.');}
function printReport(){window.print();}

let orderLineIndex=0;
function addOrderLine(){
  const template=document.getElementById('order-line-template');
  const container=document.getElementById('order-lines');
  if(!template||!container)return;
  const node=template.content.cloneNode(true);
  container.appendChild(node); orderLineIndex++; recalcOrder();
}
function removeOrderLine(btn){
  const lines=document.querySelectorAll('#order-lines .line');
  if(lines.length<=1){alert('El pedido debe tener al menos un producto.');return;}
  btn.closest('.line').remove();recalcOrder();
}
function recalcOrder(){
  let subtotal=0;
  document.querySelectorAll('#order-lines .line').forEach(line=>{
    const select=line.querySelector('select'); const qty=Number(line.querySelector('input[type=number]').value||0);
    const option=select.options[select.selectedIndex]; const price=Number(option?.dataset.price||0);
    const stock=Number(option?.dataset.stock||0); const priceCell=line.querySelector('.price');
    if(priceCell)priceCell.textContent='S/ '+price.toFixed(2)+' · stock '+stock;
    if(qty>stock && stock>=0)line.querySelector('input[type=number]').setCustomValidity('La cantidad supera el stock disponible'); else line.querySelector('input[type=number]').setCustomValidity('');
    subtotal+=price*qty;
  });
  const igv=subtotal*0.18,total=subtotal;
  const sub=document.getElementById('orderSubtotal'),tax=document.getElementById('orderIgv'),tot=document.getElementById('orderTotal');
  if(sub)sub.textContent='S/ '+(subtotal/1.18).toFixed(2); if(tax)tax.textContent='S/ '+(subtotal-subtotal/1.18).toFixed(2); if(tot)tot.textContent='S/ '+total.toFixed(2);
}
