package pe.edu.utp.sigpi;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import pe.edu.utp.sigpi.model.*;import pe.edu.utp.sigpi.repository.*;import pe.edu.utp.sigpi.service.*;
import java.util.*;import java.math.BigDecimal;
class OrderServiceTest {
 SalesOrderRepository orders;ClientRepository clients;ProductRepository products;MovementRepository movements;OrderService service;Product product;Client client;
 @BeforeEach void setup(){orders=mock(SalesOrderRepository.class);clients=mock(ClientRepository.class);products=mock(ProductRepository.class);movements=mock(MovementRepository.class);var codes=mock(OrderCodes.class);when(codes.next(any())).thenReturn("PED-2026-000001");service=new OrderService(orders,clients,products,movements,codes);client=new Client("C","20000000001","Cliente","","","",true);client.setId(1L);product=new Product("P","Producto",new Category("C","Categoría","",true),"Unidad",BigDecimal.ONE,new BigDecimal("7.50"),10,2);product.setId(1L);when(clients.findById(1L)).thenReturn(Optional.of(client));when(products.lockById(1L)).thenReturn(Optional.of(product));when(orders.save(any())).thenAnswer(i->i.getArgument(0));}
 @Test void calculatesAndMergesDuplicateLines(){var order=service.create(1L,List.of(1L,1L),List.of(2,3),"Ventas");assertEquals(1,order.getItems().size());assertEquals(5,product.getStock());assertEquals(new BigDecimal("37.50"),order.getTotal());verify(movements).save(any());}
 @Test void rejectsInsufficientStock(){assertThrows(IllegalArgumentException.class,()->service.create(1L,List.of(1L),List.of(11),"Ventas"));assertEquals(10,product.getStock());verify(orders,never()).save(any());}
 @Test void rejectsInactiveClient(){client.setActive(false);assertThrows(IllegalArgumentException.class,()->service.create(1L,List.of(1L),List.of(1),"Ventas"));}
 @Test void rejectsInactiveProduct(){product.setActive(false);assertThrows(IllegalArgumentException.class,()->service.create(1L,List.of(1L),List.of(1),"Ventas"));}
 @Test void rejectsNegativeQuantity(){assertThrows(IllegalArgumentException.class,()->service.create(1L,List.of(1L),List.of(-1),"Ventas"));}
 @Test void cancelReturnsStockOnce(){var o=service.create(1L,List.of(1L),List.of(3),"Ventas");when(orders.lockById(2L)).thenReturn(Optional.of(o));service.updateStatus(2L,"Cancelado");service.updateStatus(2L,"Cancelado");assertEquals(10,product.getStock());assertEquals("Cancelado",o.getStatus());}
 @Test void cannotReopenDeliveredOrSkipSteps(){var o=service.create(1L,List.of(1L),List.of(1),"Ventas");when(orders.lockById(2L)).thenReturn(Optional.of(o));assertThrows(IllegalArgumentException.class,()->service.updateStatus(2L,"Entregado"));service.updateStatus(2L,"En proceso");service.updateStatus(2L,"Preparado");service.updateStatus(2L,"Enviado");service.updateStatus(2L,"Entregado");assertThrows(IllegalArgumentException.class,()->service.updateStatus(2L,"Cancelado"));}
}
