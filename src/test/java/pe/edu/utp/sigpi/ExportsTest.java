package pe.edu.utp.sigpi;
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;import pe.edu.utp.sigpi.service.*;import java.util.*;import java.util.zip.*;import java.io.*;import java.nio.charset.StandardCharsets;
class ExportsTest {
 @Test void excelTreatsFormulaLikeTextAsText()throws Exception{
  var table=new ReportTable("Prueba",List.of("Nombre","Importe"),List.of(List.of("=1+1",12)),"Prueba");byte[] bytes=TableExport.xlsx(table);String sheet="";try(var zip=new ZipInputStream(new ByteArrayInputStream(bytes))){ZipEntry entry;while((entry=zip.getNextEntry())!=null)if(entry.getName().equals("xl/worksheets/sheet1.xml"))sheet=new String(zip.readAllBytes(),StandardCharsets.UTF_8);}assertTrue(sheet.contains("=1+1"));assertFalse(sheet.contains("<f>"));assertTrue(sheet.contains("<v>12</v>"));
 }
 @Test void pdfHasRealPagesAndCrossReference()throws Exception{
  List<List<Object>> rows=new ArrayList<>();for(int i=0;i<100;i++)rows.add(List.of("Peña y Compañía",i));String pdf=new String(TableExport.pdf(new ReportTable("Pedidos",List.of("Cliente","Cantidad"),rows,"100 registros")),StandardCharsets.ISO_8859_1);assertTrue(pdf.startsWith("%PDF-1.4"));assertTrue(pdf.contains("xref"));assertTrue(pdf.contains("/Count 5"));
 }
}
