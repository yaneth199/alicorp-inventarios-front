package pe.edu.utp.sigpi.config;
import org.springframework.stereotype.Component;import org.springframework.web.servlet.*;import jakarta.servlet.http.*;import java.util.*;import org.springframework.web.util.UriComponentsBuilder;
@Component
public class PaginationInterceptor implements HandlerInterceptor {
 public record Link(int number,String url,boolean current){}
 public void postHandle(HttpServletRequest req,HttpServletResponse res,Object handler,ModelAndView mv){
  if(mv==null||mv.getViewName()==null||!Set.of("products","inventory","clients","providers","orders","movements","categories","users").contains(mv.getViewName()))return;
  String key=mv.getViewName().equals("inventory")?"products":mv.getViewName();Object value=mv.getModel().get(key);if(!(value instanceof List<?> list))return;
  int size=15,count=list.size(),pages=Math.max(1,(count+size-1)/size),page=1;
  try{if(req.getParameter("page")!=null)page=Integer.parseInt(req.getParameter("page"));}catch(NumberFormatException e){page=1;}page=Math.max(1,Math.min(page,pages));int from=(page-1)*size,to=Math.min(from+size,count);
  mv.addObject(key,list.subList(from,to));mv.addObject("tableLabel",count==0?"Sin registros":"Mostrando "+(from+1)+" a "+to+" de "+count+" registros");mv.addObject("pageCount",pages);mv.addObject("currentPage",page);
  List<Link> links=new ArrayList<>();for(int i=1;i<=pages;i++)if(i==1||i==pages||Math.abs(i-page)<=2){String url=UriComponentsBuilder.fromUriString(req.getRequestURI()+(req.getQueryString()==null?"":"?"+req.getQueryString())).replaceQueryParam("page",i).build(true).toUriString();links.add(new Link(i,url,i==page));}mv.addObject("pageLinks",links);
 }
}
