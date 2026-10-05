package pe.edu.utp.sigpi.service;

import java.io.*;
import java.nio.charset.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.zip.*;

/** Dependency-free XLSX and paginated PDF exports. Cells are data, never formulas. */
public final class TableExport {
 private TableExport(){}
 private static String xml(Object x){return String.valueOf(x==null?"":x).replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]","").replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");}
 private static String column(int n){String s="";for(n++;n>0;n=(n-1)/26)s=(char)('A'+(n-1)%26)+s;return s;}
 private static void part(ZipOutputStream z,String name,String content)throws IOException{z.putNextEntry(new ZipEntry(name));z.write(content.getBytes(StandardCharsets.UTF_8));z.closeEntry();}
 private static String cell(String ref,Object value,int style){
  if(value instanceof Number){String number=value instanceof BigDecimal?((BigDecimal)value).toPlainString():value.toString();return "<c r=\""+ref+"\" s=\""+style+"\"><v>"+number+"</v></c>";}
  return "<c r=\""+ref+"\" s=\""+style+"\" t=\"inlineStr\"><is><t xml:space=\"preserve\">"+xml(value)+"</t></is></c>";
 }
 public static byte[] xlsx(ReportTable table)throws IOException{
  ByteArrayOutputStream out=new ByteArrayOutputStream();
  try(ZipOutputStream z=new ZipOutputStream(out)){
   part(z,"[Content_Types].xml","<?xml version=\"1.0\" encoding=\"UTF-8\"?><Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/><Default Extension=\"xml\" ContentType=\"application/xml\"/><Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/><Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/><Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/></Types>");
   part(z,"_rels/.rels","<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/></Relationships>");
   part(z,"xl/workbook.xml","<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"><sheets><sheet name=\"SIGPI\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>");
   part(z,"xl/_rels/workbook.xml.rels","<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/><Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/></Relationships>");
   part(z,"xl/styles.xml","<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><fonts count=\"3\"><font><sz val=\"11\"/><name val=\"Calibri\"/></font><font><b/><sz val=\"11\"/><color rgb=\"FFFFFFFF\"/><name val=\"Calibri\"/></font><font><b/><sz val=\"16\"/><name val=\"Calibri\"/></font></fonts><fills count=\"3\"><fill><patternFill patternType=\"none\"/></fill><fill><patternFill patternType=\"gray125\"/></fill><fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFD70B2F\"/><bgColor indexed=\"64\"/></patternFill></fill></fills><borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders><cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs><cellXfs count=\"4\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyAlignment=\"1\"><alignment vertical=\"top\" wrapText=\"1\"/></xf><xf numFmtId=\"0\" fontId=\"1\" fillId=\"2\" borderId=\"0\" xfId=\"0\" applyAlignment=\"1\"><alignment vertical=\"center\" wrapText=\"1\"/></xf><xf numFmtId=\"0\" fontId=\"2\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/><xf numFmtId=\"4\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyNumberFormat=\"1\"/></cellXfs><cellStyles count=\"1\"><cellStyle name=\"Normal\" xfId=\"0\" builtinId=\"0\"/></cellStyles></styleSheet>");
   int columns=table.headers().size();String last=column(columns-1);
   StringBuilder s=new StringBuilder("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetViews><sheetView workbookViewId=\"0\"><pane ySplit=\"4\" topLeftCell=\"A5\" state=\"frozen\"/></sheetView></sheetViews><cols>");
   for(int c=1;c<=columns;c++)s.append("<col min=\"").append(c).append("\" max=\"").append(c).append("\" width=\"25\" customWidth=\"1\"/>");
   s.append("</cols><sheetData><row r=\"1\" ht=\"26\" customHeight=\"1\">").append(cell("A1",table.title(),2)).append("</row><row r=\"2\">").append(cell("A2",table.summary(),0)).append("</row><row r=\"4\" ht=\"28\" customHeight=\"1\">");
   for(int c=0;c<columns;c++)s.append(cell(column(c)+"4",table.headers().get(c),1));s.append("</row>");
   int row=5;for(var values:table.rows()){s.append("<row r=\"").append(row).append("\">");for(int c=0;c<columns;c++){Object val=values.get(c);s.append(cell(column(c)+row,val,val instanceof BigDecimal?3:0));}s.append("</row>");row++;}
   s.append("</sheetData><autoFilter ref=\"A4:").append(last).append(Math.max(4,row-1)).append("\"/><mergeCells count=\"2\"><mergeCell ref=\"A1:").append(last).append("1\"/><mergeCell ref=\"A2:").append(last).append("2\"/></mergeCells><pageSetup orientation=\"landscape\" paperSize=\"9\"/></worksheet>");
   part(z,"xl/worksheets/sheet1.xml",s.toString());
  }return out.toByteArray();
 }
 private static String pdfString(String s){byte[] data=s.getBytes(Charset.forName("windows-1252"));StringBuilder b=new StringBuilder("(");for(byte item:data){int x=item&255;if(x=='('||x==')'||x=='\\')b.append('\\').append((char)x);else if(x<32||x>126)b.append('\\').append(String.format(Locale.ROOT,"%03o",x));else b.append((char)x);}return b.append(')').toString();}
 private static String text(double x,double y,String font,int size,String s){return String.format(Locale.ROOT,"BT /%s %d Tf 1 0 0 1 %.2f %.2f Tm %s Tj ET\n",font,size,x,y,pdfString(s));}
 private static List<String> wrap(Object object,int max){String s=String.valueOf(object==null?"":object).replace('\r',' ').replace('\n',' ');List<String> lines=new ArrayList<>();while(s.length()>max){int end=s.lastIndexOf(' ',max);if(end<max/2)end=max;lines.add(s.substring(0,end));s=s.substring(end).stripLeading();}lines.add(s);return lines;}
 private static String header(ReportTable t){return "0.84 0.04 0.18 rg 32 548 778 3 re f\n0 0 0 rg\n"+text(32,564,"F2",16,"SIGPI | "+t.title())+text(32,532,"F1",9,t.summary());}
 public static byte[] pdf(ReportTable t)throws IOException{
  int cols=t.headers().size();double width=778.0/cols;int chars=Math.max(8,(int)((width-10)/4.8));List<String> pages=new ArrayList<>();StringBuilder page=new StringBuilder(header(t));double y=512;
  List<List<Object>> all=new ArrayList<>();all.add(new ArrayList<>(t.headers()));all.addAll(t.rows());if(t.rows().isEmpty()){List<Object> empty=new ArrayList<>(Collections.nCopies(cols,""));empty.set(0,"Sin registros");all.add(empty);}
  boolean heading=true;
  for(var row:all){List<List<String>> cells=new ArrayList<>();for(Object c:row)cells.add(wrap(c,chars));int count=cells.stream().mapToInt(List::size).max().orElse(1);
   for(int offset=0;offset<count;offset+=12){int lines=Math.min(12,count-offset);double h=lines*10+12;
    if(y-h<40){pages.add(page.toString());page=new StringBuilder(header(t));y=512;
     page.append("0.84 0.04 0.18 rg 32 ").append(y-30).append(" 778 30 re f 1 1 1 rg\n");
     for(int c=0;c<cols;c++){var hs=wrap(t.headers().get(c),chars);for(int a=0;a<Math.min(2,hs.size());a++)page.append(text(37+c*width,y-11-a*10,"F3",8,hs.get(a)));}y-=30;
    }
    if(heading)page.append("0.84 0.04 0.18 rg ");else page.append("0.97 0.98 0.99 rg ");
    page.append(String.format(Locale.ROOT,"32 %.2f 778 %.2f re f\n",y-h,h)).append(heading?"1 1 1 rg\n":"0 0 0 rg\n");
    for(int c=0;c<cols;c++){for(int a=0;a<lines;a++){int at=offset+a;if(at<cells.get(c).size())page.append(text(37+c*width,y-12-a*10,"F3",8,cells.get(c).get(at)));}}
    page.append(String.format(Locale.ROOT,"0.85 0.87 0.9 RG 0.3 w 32 %.2f m 810 %.2f l S\n",y-h,y-h));y-=h;
   }heading=false;
  }pages.add(page.toString());
  List<byte[]> objects=new ArrayList<>();objects.add("<< /Type /Catalog /Pages 2 0 R >>".getBytes(StandardCharsets.US_ASCII));
  StringBuilder kids=new StringBuilder();for(int i=0;i<pages.size();i++)kids.append(6+i*2).append(" 0 R ");objects.add(("<< /Type /Pages /Kids ["+kids+"] /Count "+pages.size()+" >>").getBytes(StandardCharsets.US_ASCII));
  for(String font:List.of("Helvetica","Helvetica-Bold","Courier"))objects.add(("<< /Type /Font /Subtype /Type1 /BaseFont /"+font+" /Encoding /WinAnsiEncoding >>").getBytes(StandardCharsets.US_ASCII));
  for(int i=0;i<pages.size();i++){int id=6+i*2;objects.add(("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 842 595] /Resources << /Font << /F1 3 0 R /F2 4 0 R /F3 5 0 R >> >> /Contents "+(id+1)+" 0 R >>").getBytes(StandardCharsets.US_ASCII));
   byte[] content=(pages.get(i)+"0 0 0 rg\n"+text(32,20,"F1",8,"Generado por SIGPI · Página "+(i+1)+" de "+pages.size())).getBytes(StandardCharsets.US_ASCII);ByteArrayOutputStream stream=new ByteArrayOutputStream();stream.write(("<< /Length "+content.length+" >>\nstream\n").getBytes(StandardCharsets.US_ASCII));stream.write(content);stream.write("endstream".getBytes(StandardCharsets.US_ASCII));objects.add(stream.toByteArray());
  }
  ByteArrayOutputStream out=new ByteArrayOutputStream();out.write("%PDF-1.4\n".getBytes(StandardCharsets.US_ASCII));List<Integer> offsets=new ArrayList<>();for(int i=0;i<objects.size();i++){offsets.add(out.size());out.write(((i+1)+" 0 obj\n").getBytes(StandardCharsets.US_ASCII));out.write(objects.get(i));out.write("\nendobj\n".getBytes(StandardCharsets.US_ASCII));}int xref=out.size();out.write(("xref\n0 "+(objects.size()+1)+"\n0000000000 65535 f \n").getBytes(StandardCharsets.US_ASCII));for(int offset:offsets)out.write(String.format(Locale.ROOT,"%010d 00000 n \n",offset).getBytes(StandardCharsets.US_ASCII));out.write(("trailer\n<< /Size "+(objects.size()+1)+" /Root 1 0 R >>\nstartxref\n"+xref+"\n%%EOF").getBytes(StandardCharsets.US_ASCII));return out.toByteArray();
 }
}
