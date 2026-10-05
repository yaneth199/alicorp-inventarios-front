package pe.edu.utp.sigpi.model;
import jakarta.persistence.*;import java.time.LocalDateTime;
@Entity @Table(name="order_status_events")
public class OrderStatusEvent {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) @JoinColumn(name="order_id") private SalesOrder order;
 private String status;
 private LocalDateTime changedAt;
 private String responsible;
 private String note;
 public OrderStatusEvent(){}
 public OrderStatusEvent(SalesOrder order,String status,String responsible,LocalDateTime when,String note){this.order=order;this.status=status;this.responsible=responsible;this.changedAt=when;this.note=note;}
 public Long getId(){return id;}public String getStatus(){return status;}public LocalDateTime getChangedAt(){return changedAt;}public String getResponsible(){return responsible;}public String getNote(){return note;}
}
