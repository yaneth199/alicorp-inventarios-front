import java.net.URI;
import java.net.http.*;
import java.time.Duration;
public final class Healthcheck {
 public static void main(String[] args) {
  try {
   var client=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
   String port=System.getenv().getOrDefault("PORT","8080");
   var request=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+"/health/ready")).timeout(Duration.ofSeconds(3)).GET().build();
   var response=client.send(request,HttpResponse.BodyHandlers.ofString());
   System.exit(response.statusCode()==200 && response.body().equals("UP")?0:1);
  }catch(Exception e){System.exit(1);}
 }
}
