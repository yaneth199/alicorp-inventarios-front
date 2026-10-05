package pe.edu.utp.sigpi.service;
import org.springframework.stereotype.Service;import org.springframework.jdbc.core.JdbcTemplate;import java.time.LocalDate;
@Service public class OrderCodes {
 private final JdbcTemplate jdbc;public OrderCodes(JdbcTemplate jdbc){this.jdbc=jdbc;}
 public String next(LocalDate date){return "PED-"+date.getYear()+"-"+String.format("%06d",jdbc.queryForObject("SELECT nextval('sales_order_code_seq')",Long.class));}
}
