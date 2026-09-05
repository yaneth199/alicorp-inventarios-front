package pe.edu.utp.sigpi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.sigpi.model.InventoryMovement;
import pe.edu.utp.sigpi.model.Product;
import pe.edu.utp.sigpi.repository.MovementRepository;
import pe.edu.utp.sigpi.repository.ProductRepository;

import java.time.LocalDate;

@Service
public class InventoryService {
    private final ProductRepository productRepository;
    private final MovementRepository movementRepository;

    public InventoryService(ProductRepository productRepository, MovementRepository movementRepository) {
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
    }

    @Transactional
    public InventoryMovement register(Long productId, String type, int quantity, String document, String responsible, String note) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        if (quantity <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a cero");

        int newStock;
        if ("ENTRADA".equalsIgnoreCase(type)) {
            newStock = product.getStock() + quantity;
        } else if ("SALIDA".equalsIgnoreCase(type)) {
            if (quantity > product.getStock()) throw new IllegalArgumentException("La salida supera el stock disponible");
            newStock = product.getStock() - quantity;
        } else {
            throw new IllegalArgumentException("Tipo de movimiento no válido");
        }

        product.setStock(newStock);
        productRepository.save(product);

        InventoryMovement movement = new InventoryMovement(LocalDate.now(), product, type.toUpperCase(), quantity, newStock,
                document == null || document.isBlank() ? "S/D" : document,
                responsible == null || responsible.isBlank() ? "Administrador" : responsible);
        movement.setNote(note);
        return movementRepository.save(movement);
    }
}
