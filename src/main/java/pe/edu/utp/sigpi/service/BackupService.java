package pe.edu.utp.sigpi.service;
import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.*;import org.springframework.jdbc.core.JdbcTemplate;
import java.util.*;import java.io.*;import java.nio.charset.StandardCharsets;
@Service
public class BackupService {
 private final JdbcTemplate jdbc;
 public BackupService(JdbcTemplate jdbc){this.jdbc=jdbc;}
 @Transactional(readOnly=true,isolation=Isolation.REPEATABLE_READ)
 public byte[] snapshot()throws IOException{
  List<String> tables=List.of("app_users","categories","clients","providers","company_settings","products","sales_orders","order_items","inventory_movements","order_status_events","order_drafts","demo_batches");
  StringBuilder out=new StringBuilder("-- Respaldo SIGPI. Restaurar solamente sobre una base vacía.\n-- Incluye información y credenciales derivadas. Conservar de forma privada.\nBEGIN;\n");
  try(var input=getClass().getResourceAsStream("/db/schema.sql")){if(input==null)throw new IOException("Esquema no encontrado");out.append(new String(input.readAllBytes(),StandardCharsets.UTF_8)).append('\n');}
  for(String table:tables){jdbc.query("SELECT * FROM "+table,rs->{var meta=rs.getMetaData();int count=meta.getColumnCount();StringJoiner columns=new StringJoiner(",");StringJoiner values=new StringJoiner(",");for(int c=1;c<=count;c++){columns.add("\""+meta.getColumnName(c)+"\"");Object value=rs.getObject(c);values.add(value==null?"NULL":value instanceof Number||value instanceof Boolean?value.toString():"'"+value.toString().replace("'","''")+"'");}out.append("INSERT INTO ").append(table).append(" (").append(columns).append(") VALUES (").append(values).append(");\n");});}
  for(String table:List.of("app_users","categories","clients","providers","products","sales_orders","order_items","inventory_movements","order_status_events"))out.append("SELECT setval(pg_get_serial_sequence('").append(table).append("','id'),COALESCE((SELECT MAX(id) FROM ").append(table).append("),1),(SELECT count(*)>0 FROM ").append(table).append("));\n");
  Long sequence=jdbc.queryForObject("SELECT last_value FROM sales_order_code_seq",Long.class);out.append("SELECT setval('sales_order_code_seq',").append(sequence).append(",true);\nCOMMIT;\n");return out.toString().getBytes(StandardCharsets.UTF_8);
 }
}
