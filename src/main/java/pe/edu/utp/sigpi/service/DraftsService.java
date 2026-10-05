package pe.edu.utp.sigpi.service;
import org.springframework.stereotype.Service;import org.springframework.jdbc.core.JdbcTemplate;import com.fasterxml.jackson.databind.ObjectMapper;import java.util.*;
@Service
public class DraftsService {
 public record Content(Long clientId,List<Long> productIds,List<Integer> quantities){}
 private final JdbcTemplate jdbc;private final ObjectMapper json;
 public DraftsService(JdbcTemplate jdbc,ObjectMapper json){this.jdbc=jdbc;this.json=json;}
 public Content get(Long user){var list=jdbc.queryForList("SELECT content FROM order_drafts WHERE user_id=?",String.class,user);if(list.isEmpty())return new Content(null,Collections.singletonList(null),List.of(1));try{return json.readValue(list.get(0),Content.class);}catch(java.io.IOException e){throw new IllegalStateException("No se puede leer el borrador",e);}}
 public void save(Long user,Long client,List<Long> ids,List<Integer> qty){if(ids==null||qty==null||ids.size()!=qty.size()||ids.size()>100)throw new IllegalArgumentException("Revisa las líneas del borrador");for(int i=0;i<ids.size();i++)if(qty.get(i)==null||qty.get(i)<=0)throw new IllegalArgumentException("Cantidad inválida");try{String content=json.writeValueAsString(new Content(client,ids,qty));jdbc.update("INSERT INTO order_drafts(user_id,content,updated_at) VALUES (?,?,CURRENT_TIMESTAMP) ON CONFLICT(user_id) DO UPDATE SET content=EXCLUDED.content,updated_at=EXCLUDED.updated_at",user,content);}catch(com.fasterxml.jackson.core.JsonProcessingException e){throw new IllegalStateException(e);}}
 public void clear(Long user){jdbc.update("DELETE FROM order_drafts WHERE user_id=?",user);}
}
