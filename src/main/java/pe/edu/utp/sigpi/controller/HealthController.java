package pe.edu.utp.sigpi.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
@RestController
public class HealthController {
 private final JdbcTemplate jdbc;
 public HealthController(JdbcTemplate jdbc){this.jdbc=jdbc;}
 @GetMapping("/health/ready") public ResponseEntity<String> ready(){
  try{return Integer.valueOf(1).equals(jdbc.queryForObject("SELECT 1",Integer.class))?ResponseEntity.ok("UP"):ResponseEntity.status(503).body("DOWN");}
  catch(org.springframework.dao.DataAccessException e){return ResponseEntity.status(503).body("DOWN");}
 }
}
