package pe.edu.utp.sigpi.model;

import jakarta.persistence.*;

@Entity
@Table(name="company_settings")
public class CompanySetting {
    @Id
    private Long id = 1L;
    private String businessName;
    private String ruc;
    private String address;
    private String branch;
    private String phone;
    private String email;
    private String systemName;
    private String version;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
}
