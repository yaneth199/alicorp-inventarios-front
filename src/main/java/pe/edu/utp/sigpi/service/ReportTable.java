package pe.edu.utp.sigpi.service;
import java.util.List;
public record ReportTable(String title,List<String> headers,List<List<Object>> rows,String summary) {}
