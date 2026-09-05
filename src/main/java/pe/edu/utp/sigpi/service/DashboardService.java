package pe.edu.utp.sigpi.service;

import org.springframework.stereotype.Service;
import pe.edu.utp.sigpi.model.Product;
import pe.edu.utp.sigpi.repository.ProductRepository;
import pe.edu.utp.sigpi.repository.SalesOrderRepository;

import java.math.BigDecimal;

@Service
public class DashboardService {
    private final ProductRepository productRepository;
    private final SalesOrderRepository orderRepository;

    public DashboardService(ProductRepository productRepository, SalesOrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public long totalProducts() { return productRepository.count(); }
    public long lowStock() { return productRepository.findAll().stream().filter(p -> p.getStock() > 0 && p.getStock() <= p.getMinStock()).count(); }
    public long pendingOrders() { return orderRepository.countByStatus("Pendiente") + orderRepository.countByStatus("En proceso") + orderRepository.countByStatus("Preparado"); }
    public long completedOrders() { return orderRepository.countByStatus("Entregado"); }
    public BigDecimal inventoryValue() {
        return productRepository.findAll().stream()
                .map(p -> p.getPurchasePrice().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public long outOfStock() { return productRepository.findAll().stream().filter(p -> p.getStock() <= 0).count(); }
}
