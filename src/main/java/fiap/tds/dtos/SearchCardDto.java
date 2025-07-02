package fiap.tds.dtos;

import fiap.tds.entities.Card;

import java.util.List;

public record SearchCardDto(int page, String direction, int pageSize,
     int totalItems, List<Card> data){
}

