package fiap.tds;

import fiap.tds.entities.MyEntity;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }

    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public String helloJson(){
        return "{\"message\": \"Hello from Quarkus REST\"}";
    }

    @GET
    @Path("/myentityjson")
    @Produces(MediaType.APPLICATION_JSON)
    public MyEntity myEntityJson(){
        return new MyEntity(1, "My entity");
    }

    @GET
    @Path("/xml")
    @Produces(MediaType.APPLICATION_XML)
    public String helloXML(){
        return "<message>Hello from Quarkus REST</message>";
    }
}
