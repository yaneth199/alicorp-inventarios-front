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
    private final OrderCodes codes;
    private final SalesOrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final MovementRepository movementRepository;

    public OrderService(SalesOrderRepository orderRepository, ClientRepository clientRepository,
                        ProductRepository productRepository, MovementRepository movementRepository,OrderCodes codes) {
        this.codes=codes;
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
    }

    @Transactional
    public SalesOrder create(Long clientId, List<Long> productIds, List<Integer> quantities, String responsible) {
        var now=java.time.LocalDateTime.now();
        return createInternal(clientId,productIds,quantities,responsible,now.toLocalDate(),now);
    }

    @Transactional
    public SalesOrder createAt(Long clientId,List<Long> productIds,List<Integer> quantities,String responsible,LocalDate date) {
        return createInternal(clientId,productIds,quantities,responsible,date,date.atTime(9,0));
    }

    private SalesOrder createInternal(Long clientId,List<Long> productIds,List<Integer> quantities,String responsible,LocalDate date,java.time.LocalDateTime createdAt) {
        if (productIds == null || quantities == null || productIds.isEmpty() || productIds.size() != quantities.size()) {
            throw new IllegalArgumentException("Agrega al menos un producto al pedido");
        }
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        if(!client.isActive())throw new IllegalArgumentException("El cliente está inactivo");
        java.util.Map<Long,Integer> merged=new java.util.TreeMap<>();
        for(int k=0;k<productIds.size();k++) {if(productIds.get(k)==null||quantities.get(k)==null||quantities.get(k)<=0)throw new IllegalArgumentException("Cantidad inválida"); merged.merge(productIds.get(k),quantities.get(k),Math::addExact);}
        productIds=new java.util.ArrayList<>(merged.keySet()); quantities=new java.util.ArrayList<>(merged.values());
        SalesOrder order = new SalesOrder();
        order.setCode(codes.next(date));
        order.setClient(client);
        order.setOrderDate(date);
        order.setStatus("Pendiente");
        order.setResponsible(responsible == null || responsible.isBlank() ? "Administrador" : responsible);

        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Integer qtyObj = quantities.get(i);
            int qty = qtyObj == null ? 0 : qtyObj;
            if (qty <= 0) throw new IllegalArgumentException("Todas las cantidades deben ser mayores a cero");

            Product product = productRepository.lockById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            if(!product.isActive()||!product.getCategory().isActive())throw new IllegalArgumentException("Producto o categoría inactivos");
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
            movementRepository.save(new InventoryMovement(date, product, "SALIDA", qty, product.getStock(), order.getCode(), order.getResponsible()));
        }
        order.setTotal(total);
        order.getHistory().add(new OrderStatusEvent(order,"Pendiente",order.getResponsible(),createdAt,"Pedido registrado"));
        return orderRepository.save(order);
    }

    @Transactional
    public void updateStatus(Long id,String status){updateStatusAt(id,status,"Sistema",java.time.LocalDateTime.now());}
    @Transactional
    public void updateStatus(Long id,String status,String responsible){updateStatusAt(id,status,responsible,java.time.LocalDateTime.now());}
    @Transactional
    public void updateStatusAt(Long id, String status,String responsible,java.time.LocalDateTime when) {
        SalesOrder order = orderRepository.lockById(id).orElseThrow();
        String old=order.getStatus();
        if(old.equals(status))return;
        java.util.Map<String,String> next=java.util.Map.of("Pendiente","En proceso","En proceso","Preparado","Preparado","Enviado","Enviado","Entregado");
        if(!status.equals(next.get(old)) && !(status.equals("Cancelado") && next.containsKey(old)))throw new IllegalArgumentException("Cambio de estado no permitido");
        if(status.equals("Cancelado")) {
          for(OrderItem item:order.getItems().stream().sorted(java.util.Comparator.comparing(i->i.getProduct().getId())).toList()){
           Product p=productRepository.lockById(item.getProduct().getId()).orElseThrow();
           p.setStock(Math.addExact(p.getStock(),item.getQuantity()));
           movementRepository.save(new InventoryMovement(when.toLocalDate(),p,"ENTRADA",item.getQuantity(),p.getStock(),order.getCode(),responsible+" (cancelación)"));
          }
        }
        order.setStatus(status);
        order.getHistory().add(new OrderStatusEvent(order,status,responsible,when,"Estado actualizado"));
        orderRepository.save(order);
    }
}
