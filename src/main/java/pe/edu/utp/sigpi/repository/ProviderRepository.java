package pe.edu.utp.sigpi.repository;
import pe.edu.utp.sigpi.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    List<Provider> findByBusinessNameContainingIgnoreCaseOrRucContainingIgnoreCase(String businessName, String ruc);
}
