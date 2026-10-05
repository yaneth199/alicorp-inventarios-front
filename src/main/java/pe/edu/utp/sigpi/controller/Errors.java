package pe.edu.utp.sigpi.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
@ControllerAdvice
public class Errors {
 @ExceptionHandler({IllegalArgumentException.class,ArithmeticException.class,org.springframework.validation.BindException.class,org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class})
 @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
 public String invalid(Exception e,Model m){m.addAttribute("message",e instanceof IllegalArgumentException?e.getMessage():"Revisa los campos y las cantidades ingresadas");return "problem";}
 @ExceptionHandler(java.util.NoSuchElementException.class) @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
 public String missing(Model m){m.addAttribute("message","El registro solicitado no existe");return "problem";}
 @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class) @ResponseStatus(org.springframework.http.HttpStatus.CONFLICT)
 public String conflict(Model m){m.addAttribute("message","Código o documento duplicado, datos inválidos o registro con referencias. Revisa los datos.");return "problem";}
 @ExceptionHandler(org.springframework.transaction.TransactionSystemException.class) @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
 public String transaction(org.springframework.transaction.TransactionSystemException e,Model m){Throwable cause=e.getMostSpecificCause();m.addAttribute("message",cause instanceof IllegalArgumentException?cause.getMessage():"No se guardaron los cambios. Revisa los campos ingresados.");return "problem";}
}

