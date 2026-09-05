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
                       @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to, Model m){
        if(from==null) from=LocalDate.of(2026,8,1); if(to==null) to=LocalDate.now();
        var list=repo.findByDateBetweenOrderByDateDescIdDesc(from,to); m.addAttribute("movements",list);m.addAttribute("from",from);m.addAttribute("to",to);
        m.addAttribute("entries",list.stream().filter(x->"ENTRADA".equals(x.getType())).count());
        m.addAttribute("exits",list.stream().filter(x->"SALIDA".equals(x.getType())).count()); return "movements";
    }
    @GetMapping("/export")
    public void export(HttpServletResponse r)throws IOException{r.setContentType("text/csv; charset=UTF-8");r.setHeader("Content-Disposition","attachment; filename=movimientos_sigpi.csv");r.getWriter().write("Fecha,Código,Producto,Tipo,Cantidad,Stock,Documento,Responsable\n");for(var x:repo.findAllByOrderByDateDescIdDesc())r.getWriter().printf("%s,%s,\"%s\",%s,%d,%d,%s,\"%s\"%n",x.getDate(),x.getProduct().getCode(),x.getProduct().getName(),x.getType(),x.getQuantity(),x.getResultingStock(),x.getDocument(),x.getResponsible());}
}
