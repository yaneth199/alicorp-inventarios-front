package pe.edu.utp.sigpi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "providers")
public class Provider {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String code;
    private String ruc;
    @Column(nullable = false)
    private String businessName;
    private String contact;
    private String phone;
    private String email;
    private boolean active = true;

    public Provider() {}
    public Provider(String code, String ruc, String businessName, String contact, String phone, String email) {
        this.code=code; this.ruc=ruc; this.businessName=businessName; this.contact=contact; this.phone=phone; this.email=email;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
