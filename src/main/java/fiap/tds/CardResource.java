package fiap.tds;

import fiap.tds.dtos.SearchCardDto;
import fiap.tds.entities.Card;
import fiap.tds.services.CardService;
import io.smallrye.faulttolerance.api.RateLimit;
import io.smallrye.faulttolerance.api.RateLimitType;
import io.vertx.core.net.impl.pool.Task;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Path("/card")
public class CardResource {

    public static final int PAGE_SIZE = 3;
    private final CardService cardService = new CardService();

    static List<Card> cards = new ArrayList<Card>(List.of(
            new Card(1, "Card 1", "Description 1"),
            new Card(2, "Card 2", "Description 2"),
            new Card(3, "Card 3", "Description 3"),
            new Card(4, "Card 4", "Description 4"),
            new Card(5, "Card 5", "Description 5")
    ));

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RateLimit
    @Timeout
    @Fallback(fallbackMethod = "faultToleranceFallback")
    public Response getCards() throws InterruptedException {
        return Response.ok(cards).build();
    }

    public Response faultToleranceFallback(){
        return Response.status(Response.Status.TOO_MANY_REQUESTS)
                .entity("You're exceed the rate limit of this endpoint")
                .build();
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public SearchCardDto searchCards(
            @QueryParam("page") int page,
            @QueryParam("name") Optional<String> name,
            @QueryParam("text") Optional<String> text,
            @QueryParam("direction") Optional<String> direction)
    {

        var filteredCards = cards.stream()
                .filter(e -> e.getName().contains(name.orElse("")))
                .filter(e -> e.getDescription().contains(text.orElse("")))
                .sorted(direction.orElse("asc").equals("desc") ?
                        (c1, c2) -> c2.getName().compareToIgnoreCase(c1.getName()):
                        (c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName())
                        )
                .toList();

        if(filteredCards.isEmpty())
            return null;

        var start = page > 1 ? (page - 1) * PAGE_SIZE : 0; // aqui eu calculo o inicio da pagina
        var end = Math.min(start + PAGE_SIZE, filteredCards.size());

        return start > filteredCards.size() ?
                null : new SearchCardDto(page,
                direction.orElse("asc"),
                PAGE_SIZE,
                filteredCards.size(),
                filteredCards.subList(start,end));
    }

    @GET
    @Path("/random")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRandomCard(){
        var randomCard = cards.get((int) (Math.random() * cards.size()));
        return Response.ok(randomCard).build();
    }

    @GET
    @Path("/collection/{collection}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCardByCollection(@PathParam("collection") String collection){
        return Response.status(501).build(); // Not implemented yet
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCard(@PathParam("id") int id){
        var card = cards.stream().filter(c -> c.getId() == id)
                .findFirst().orElse(null);

        return card == null ? Response.status(Response.Status.NOT_FOUND)
                .build() : Response.ok(card).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCard(Card card){

        if(!cardService.validateCard(card))
            return Response.status(Response.Status.BAD_REQUEST).build();

        cards.add(card);
        return Response.status(Response.Status.CREATED)
                .entity(card)
                .build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCard(@PathParam("id") int id, Card card){
        var cardToUpdate = cards.stream().filter(c -> c.getId() == id)
                .findFirst().orElse(null);

        if(cardToUpdate == null)
            return Response.status(RestResponse.Status.NOT_FOUND).build();

        cards.stream().filter(c -> c.getId() == id)
                .forEach(c -> {
                    c.setName(card.getName());
                    c.setDescription(card.getDescription());
                });

        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCard(@PathParam("id") int id){
        var card = cards.stream().filter(c -> c.getId() == id)
                        .findFirst().orElse(null);
        if(card == null)
            return Response.status(RestResponse.Status.NOT_FOUND).build();

        cards.removeIf(c -> c.getId() == id);
        return Response.noContent().build();
    }
}
