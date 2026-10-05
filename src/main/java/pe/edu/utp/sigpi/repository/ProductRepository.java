package pe.edu.utp.sigpi.repository;
import pe.edu.utp.sigpi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code);
 @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
 @org.springframework.data.jpa.repository.Query("select e from Product e where e.id = :id")
 java.util.Optional<Product> lockById(@org.springframework.data.repository.query.Param("id") Long id);
}
