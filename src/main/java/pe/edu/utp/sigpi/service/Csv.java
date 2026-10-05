package pe.edu.utp.sigpi.service;
import java.io.*;import jakarta.servlet.http.HttpServletResponse;
public final class Csv {
 private Csv(){}
 public static PrintWriter start(HttpServletResponse r,String name)throws IOException{r.setContentType("text/csv; charset=UTF-8");r.setCharacterEncoding("UTF-8");r.setHeader("Content-Disposition","attachment; filename="+name);PrintWriter w=r.getWriter();w.write('\uFEFF');return w;}
 public static String cell(Object value){String s=value==null?"":value.toString();String t=s.stripLeading();if(!t.isEmpty()&&"=+-@".indexOf(t.charAt(0))>=0)s="'"+s;return "\""+s.replace("\"","\"\"")+"\"";}
 public static void row(PrintWriter w,Object... values){w.println(java.util.Arrays.stream(values).map(Csv::cell).collect(java.util.stream.Collectors.joining(",")));}
}
