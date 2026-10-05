package pe.edu.utp.sigpi.repository;
import pe.edu.utp.sigpi.model.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    long countByStatus(String status);
    List<SalesOrder> findAllByOrderByOrderDateDescIdDesc();
 @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
 @org.springframework.data.jpa.repository.Query("select e from SalesOrder e where e.id = :id")
 java.util.Optional<SalesOrder> lockById(@org.springframework.data.repository.query.Param("id") Long id);
}
