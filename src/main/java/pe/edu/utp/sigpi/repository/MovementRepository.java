package pe.edu.utp.sigpi.repository;
import pe.edu.utp.sigpi.model.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
public interface MovementRepository extends JpaRepository<InventoryMovement, Long> {
    List<InventoryMovement> findAllByOrderByDateDescIdDesc();
    List<InventoryMovement> findByDateBetweenOrderByDateDescIdDesc(LocalDate from, LocalDate to);
}
