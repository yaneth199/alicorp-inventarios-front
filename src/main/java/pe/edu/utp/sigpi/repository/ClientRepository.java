package pe.edu.utp.sigpi.repository;
import pe.edu.utp.sigpi.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByNameContainingIgnoreCaseOrDocumentNumberContainingIgnoreCase(String name, String documentNumber);
}
