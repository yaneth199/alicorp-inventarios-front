package pe.edu.utp.sigpi.service;
import java.util.*;import java.math.BigDecimal;import java.time.*;
import pe.edu.utp.sigpi.model.*;
public final class ReportData {
 private ReportData(){}
 public static List<Long> monthly(List<SalesOrder> orders){int year=LocalDate.now().getYear();return java.util.stream.IntStream.rangeClosed(1,12).mapToObj(month->orders.stream().filter(o->o.getOrderDate().getYear()==year&&o.getOrderDate().getMonthValue()==month).count()).toList();}
 public static Map<String,Long> states(List<SalesOrder> orders){Map<String,Long> m=new LinkedHashMap<>();for(String s:List.of("Pendiente","En proceso","Preparado","Entregado","Cancelado"))m.put(s,orders.stream().filter(o->s.equals(o.getStatus())).count());return m;}
 public static Map<Long,Long> units(List<SalesOrder> orders){Map<Long,Long> m=new HashMap<>();orders.stream().filter(o->o.getStatus().equals("Entregado")).flatMap(o->o.getItems().stream()).forEach(i->m.merge(i.getProduct().getId(),(long)i.getQuantity(),Long::sum));return m;}
 public static List<String> months(){return List.of("Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic");}
}
