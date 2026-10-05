package pe.edu.utp.sigpi.service;
import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.sigpi.model.*;import pe.edu.utp.sigpi.repository.*;
import java.util.*;import java.time.*;import java.math.BigDecimal;
@Service @Transactional(readOnly=true)
public class ReportsService {
 private final ProductRepository products;private final SalesOrderRepository orders;private final MovementRepository movements;
 public ReportsService(ProductRepository p,SalesOrderRepository o,MovementRepository m){products=p;orders=o;movements=m;}
 public static final Map<String,String> TYPES;
 static {var m=new LinkedHashMap<String,String>();m.put("inventory","Reporte de Inventario");m.put("low","Productos con Stock Bajo");m.put("out","Productos Agotados");m.put("entries","Entradas de Inventario");m.put("exits","Salidas de Inventario");m.put("orders","Pedidos por Fecha");m.put("clients","Pedidos por Cliente");m.put("states","Pedidos por Estado");m.put("top","Productos más Solicitados");TYPES=Collections.unmodifiableMap(m);}
 public String normalize(String type){return switch(type){case "pedidos"->"orders";case "inventario"->"inventory";case "bajo"->"low";case "agotados"->"out";default->type;};}
 private boolean date(LocalDate d,LocalDate from,LocalDate to){return (from==null||!d.isBefore(from))&&(to==null||!d.isAfter(to));}
 private boolean product(Product p,Long id,Long category){return (id==null||p.getId().equals(id))&&(category==null||p.getCategory().getId().equals(category));}
 public List<SalesOrder> orders(LocalDate from,LocalDate to,Long client,Long product,Long category,String status){
  if(from!=null&&to!=null&&from.isAfter(to))throw new IllegalArgumentException("La fecha inicial supera la fecha final");
  return orders.findAllByOrderByOrderDateDescIdDesc().stream().filter(o->date(o.getOrderDate(),from,to)&&(client==null||o.getClient().getId().equals(client))&&(status==null||status.isBlank()||status.equals(o.getStatus()))&&((product==null&&category==null)||o.getItems().stream().anyMatch(i->product(i.getProduct(),product,category)))).toList();
 }
 public ReportTable table(String type,LocalDate from,LocalDate to,Long client,Long product,Long category,String status){
  type=normalize(type);if(!TYPES.containsKey(type))throw new IllegalArgumentException("Tipo de reporte inválido");
  var os=orders(from,to,client,product,category,status);List<List<Object>> rows=new ArrayList<>();List<String> headers;
  if(Set.of("inventory","low","out").contains(type)){
   headers=List.of("Código","Producto","Categoría","Unidad","Stock","Mínimo","Precio compra","Precio venta","Estado");
   for(Product p:products.findAll()){if(!product(p,product,category))continue;if(type.equals("low")&&!(p.isActive()&&p.getStock()>0&&p.getStock()<=p.getMinStock()))continue;if(type.equals("out")&&!(p.isActive()&&p.getStock()==0))continue;
    rows.add(Arrays.asList(p.getCode(),p.getName(),p.getCategory().getName(),p.getUnit(),p.getStock(),p.getMinStock(),p.getPurchasePrice(),p.getSalePrice(),p.isActive()?p.getStockStatus():"Inactivo"));}
  }else if(Set.of("entries","exits").contains(type)){
   headers=List.of("Fecha","Código","Producto","Tipo","Cantidad","Saldo","Documento","Responsable","Proveedor");String movementType=type.equals("entries")?"ENTRADA":"SALIDA";
   for(var m:movements.findAllByOrderByDateDescIdDesc())if(date(m.getDate(),from,to)&&product(m.getProduct(),product,category)&&m.getType().equals(movementType))rows.add(Arrays.asList(m.getDate(),m.getProduct().getCode(),m.getProduct().getName(),m.getType(),m.getQuantity(),m.getResultingStock(),m.getDocument(),m.getResponsible(),m.getProvider()==null?"—":m.getProvider().getBusinessName()));
  }else if(type.equals("clients")){
   headers=List.of("Documento","Cliente","Pedidos","Entregados","Pendientes de atención","Importe no cancelado");var grouped=new LinkedHashMap<Long,List<SalesOrder>>();for(var o:os)grouped.computeIfAbsent(o.getClient().getId(),key->new ArrayList<>()).add(o);
   for(var list:grouped.values()){var c=list.get(0).getClient();rows.add(Arrays.asList(c.getDocumentNumber(),c.getName(),list.size(),list.stream().filter(o->o.getStatus().equals("Entregado")).count(),list.stream().filter(o->!Set.of("Entregado","Cancelado").contains(o.getStatus())).count(),list.stream().filter(o->!o.getStatus().equals("Cancelado")).map(SalesOrder::getTotal).reduce(BigDecimal.ZERO,BigDecimal::add)));}
  }else if(type.equals("states")){
   headers=List.of("Estado","Cantidad de pedidos","Importe registrado");for(String st:List.of("Pendiente","En proceso","Preparado","Enviado","Entregado","Cancelado")){var list=os.stream().filter(o->o.getStatus().equals(st)).toList();rows.add(Arrays.asList(st,list.size(),list.stream().map(SalesOrder::getTotal).reduce(BigDecimal.ZERO,BigDecimal::add)));}
  }else if(type.equals("top")){
   headers=List.of("Código","Producto","Categoría","Unidades solicitadas","Importe");Map<Long,Long> qty=new HashMap<>();Map<Long,BigDecimal> sum=new HashMap<>();Map<Long,Product> catalog=new HashMap<>();
   for(var o:os)if(!o.getStatus().equals("Cancelado"))for(var i:o.getItems())if(product(i.getProduct(),product,category)){long id=i.getProduct().getId();catalog.put(id,i.getProduct());qty.merge(id,(long)i.getQuantity(),Long::sum);sum.merge(id,i.getSubtotal(),BigDecimal::add);}
   qty.entrySet().stream().sorted(Map.Entry.<Long,Long>comparingByValue().reversed()).forEach(e->{var p=catalog.get(e.getKey());rows.add(Arrays.asList(p.getCode(),p.getName(),p.getCategory().getName(),e.getValue(),sum.get(e.getKey())));});
  }else{
   headers=List.of("Pedido","Fecha","Cliente","Documento","Estado","Responsable","Total");for(var o:os)rows.add(Arrays.asList(o.getCode(),o.getOrderDate(),o.getClient().getName(),o.getClient().getDocumentNumber(),o.getStatus(),o.getResponsible(),o.getTotal()));
  }
  String summary=rows.size()+" registros · "+(Set.of("inventory","low","out").contains(type)?"Existencias actuales":(from==null?"Inicio":from)+" a "+(to==null?"hoy":to));
  return new ReportTable(TYPES.get(type),headers,rows,summary);
 }
}
