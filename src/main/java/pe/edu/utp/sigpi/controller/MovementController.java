package pe.edu.utp.sigpi.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.sigpi.repository.MovementRepository;

import java.io.IOException;
import java.time.LocalDate;

@Controller
@RequestMapping("/movements")
public class MovementController {
    private final MovementRepository repo; public MovementController(MovementRepository repo){this.repo=repo;}
    @GetMapping
    public String list(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
                       @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to, @RequestParam(defaultValue="") String q, @RequestParam(defaultValue="") String type, Model m){
        if(from==null) from=LocalDate.now().withDayOfMonth(1); if(to==null) to=LocalDate.now();
        if(from.isAfter(to))throw new IllegalArgumentException("Rango de fechas inválido");
        var list=repo.findByDateBetweenOrderByDateDescIdDesc(from,to).stream().filter(x->x.getProduct().getName().toLowerCase().contains(q.toLowerCase())&&(type.isBlank()||type.equals(x.getType()))).toList();m.addAttribute("q",q);m.addAttribute("type",type); m.addAttribute("movements",list);m.addAttribute("from",from);m.addAttribute("to",to);
        m.addAttribute("entries",list.stream().filter(x->"ENTRADA".equals(x.getType())).count());
        m.addAttribute("exits",list.stream().filter(x->"SALIDA".equals(x.getType())).count()); return "movements";
    }
    @GetMapping("/export") public void export(HttpServletResponse r)throws IOException{var w=pe.edu.utp.sigpi.service.Csv.start(r,"movimientos_sigpi.csv");pe.edu.utp.sigpi.service.Csv.row(w,"Fecha","Código","Producto","Tipo","Cantidad","Stock","Documento","Responsable");for(var x:repo.findAllByOrderByDateDescIdDesc())pe.edu.utp.sigpi.service.Csv.row(w,x.getDate(),x.getProduct().getCode(),x.getProduct().getName(),x.getType(),x.getQuantity(),x.getResultingStock(),x.getDocument(),x.getResponsible());}
}
