package pe.edu.utp.sigpi.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "inventory_movements")
public class InventoryMovement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    @ManyToOne(optional = false)
    private Product product;
    private String type;
    private int quantity;
    private int resultingStock;
    private String document;
    private String responsible;
    private String note;

    public InventoryMovement() {}
    public InventoryMovement(LocalDate date, Product product, String type, int quantity, int resultingStock, String document, String responsible) {
        this.date=date; this.product=product; this.type=type; this.quantity=quantity; this.resultingStock=resultingStock; this.document=document; this.responsible=responsible;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getResultingStock() { return resultingStock; }
    public void setResultingStock(int resultingStock) { this.resultingStock = resultingStock; }
    public String getDocument() { return document; }
    public void setDocument(String document) { this.document = document; }
    public String getResponsible() { return responsible; }
    public void setResponsible(String responsible) { this.responsible = responsible; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
