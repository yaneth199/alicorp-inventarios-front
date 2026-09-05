package pe.edu.utp.sigpi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.sigpi.model.*;
import pe.edu.utp.sigpi.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {
    private final SalesOrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final MovementRepository movementRepository;

    public OrderService(SalesOrderRepository orderRepository, ClientRepository clientRepository,
                        ProductRepository productRepository, MovementRepository movementRepository) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
    }

    @Transactional
    public SalesOrder create(Long clientId, List<Long> productIds, List<Integer> quantities, String responsible) {
        if (productIds == null || quantities == null || productIds.isEmpty() || productIds.size() != quantities.size()) {
            throw new IllegalArgumentException("Agrega al menos un producto al pedido");
        }
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        SalesOrder order = new SalesOrder();
        order.setCode(String.format("PED-2026-%03d", orderRepository.count() + 1));
        order.setClient(client);
        order.setOrderDate(LocalDate.now());
        order.setStatus("Pendiente");
        order.setResponsible(responsible == null || responsible.isBlank() ? "Administrador" : responsible);

        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Integer qtyObj = quantities.get(i);
            int qty = qtyObj == null ? 0 : qtyObj;
            if (qty <= 0) throw new IllegalArgumentException("Todas las cantidades deben ser mayores a cero");

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            if (qty > product.getStock()) {
                throw new IllegalArgumentException("Stock insuficiente para " + product.getName() + ". Disponible: " + product.getStock());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(qty);
            item.setPrice(product.getSalePrice());
            item.setSubtotal(product.getSalePrice().multiply(BigDecimal.valueOf(qty)));
            order.getItems().add(item);
            total = total.add(item.getSubtotal());

            product.setStock(product.getStock() - qty);
            productRepository.save(product);
            movementRepository.save(new InventoryMovement(LocalDate.now(), product, "SALIDA", qty, product.getStock(), order.getCode(), order.getResponsible()));
        }
        order.setTotal(total);
        return orderRepository.save(order);
    }

    @Transactional
    public void updateStatus(Long id, String status) {
        SalesOrder order = orderRepository.findById(id).orElseThrow();
        order.setStatus(status);
        orderRepository.save(order);
    }
}
