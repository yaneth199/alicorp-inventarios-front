package pe.edu.utp.sigpi.model;
import jakarta.persistence.*;
public class EntityValidation {
 private void required(String value,String label){if(value==null||value.isBlank()||value.length()>255)throw new IllegalArgumentException(label+" es obligatorio (máximo 255 caracteres)");}
 @PrePersist @PreUpdate public void validate(Object e){
  if(e instanceof Category c){required(c.getCode(),"Código");required(c.getName(),"Nombre");}
  if(e instanceof Client c){required(c.getCode(),"Código");required(c.getName(),"Nombre");required(c.getDocumentNumber(),"Documento");if(c.getDocumentType()==null||!java.util.Set.of("RUC","DNI","CE").contains(c.getDocumentType()))throw new IllegalArgumentException("Tipo de documento inválido");if(c.getDocumentType().equals("RUC")&&!c.getDocumentNumber().matches("[0-9]{11}")||c.getDocumentType().equals("DNI")&&!c.getDocumentNumber().matches("[0-9]{8}"))throw new IllegalArgumentException("RUC requiere 11 dígitos y DNI 8 dígitos");}
  if(e instanceof Provider p){required(p.getCode(),"Código");required(p.getBusinessName(),"Razón social");if(p.getRuc()==null||!p.getRuc().matches("[0-9]{11}"))throw new IllegalArgumentException("RUC requiere 11 dígitos");}
  if(e instanceof AppUser u){required(u.getFullName(),"Nombre");required(u.getUsername(),"Usuario");}
  if(e instanceof Product p){required(p.getCode(),"Código");required(p.getName(),"Producto");required(p.getUnit(),"Unidad");if(p.getPurchasePrice()==null||p.getSalePrice()==null||p.getPurchasePrice().signum()<0||p.getSalePrice().signum()<0||p.getStock()<0||p.getMinStock()<0)throw new IllegalArgumentException("Precios y existencias deben ser mayores o iguales a cero");}
 }
}
