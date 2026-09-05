package pe.edu.utp.sigpi.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String code;
    @Column(nullable = false)
    private String name;
    private String description;
    @ManyToOne(optional = false)
    private Category category;
    private String unit;
    @Column(precision = 12, scale = 2)
    private BigDecimal purchasePrice = BigDecimal.ZERO;
    @Column(precision = 12, scale = 2)
    private BigDecimal salePrice = BigDecimal.ZERO;
    private int stock;
    private int minStock;
    private boolean active = true;

    public Product() {}
    public Product(String code, String name, Category category, String unit, BigDecimal purchasePrice, BigDecimal salePrice, int stock, int minStock) {
        this.code = code; this.name = name; this.category = category; this.unit = unit;
        this.purchasePrice = purchasePrice; this.salePrice = salePrice; this.stock = stock; this.minStock = minStock;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public int getMinStock() { return minStock; }
    public void setMinStock(int minStock) { this.minStock = minStock; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    @Transient public String getStockStatus() { return stock <= 0 ? "Agotado" : (stock <= minStock ? "Stock bajo" : "Disponible"); }
}
