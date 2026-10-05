package pe.edu.utp.sigpi.service;
import org.springframework.http.*;import java.io.IOException;
public final class Downloads {
 private Downloads(){}
 public static ResponseEntity<byte[]> table(ReportTable table,String format,String name)throws IOException{
  if(!java.util.Set.of("xlsx","pdf").contains(format))throw new IllegalArgumentException("Formato no válido");
  byte[] bytes=format.equals("xlsx")?TableExport.xlsx(table):TableExport.pdf(table);
  return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+name+"."+format+"\"").contentType(MediaType.parseMediaType(format.equals("xlsx")?"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":"application/pdf")).body(bytes);
 }
}
