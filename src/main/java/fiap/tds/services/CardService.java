package fiap.tds.services;

import fiap.tds.entities.Card;

public class CardService {

    public boolean validateCard(Card card) {
        if(card == null)
            return false;
        if(card.getName() == null || card.getName().isEmpty())
            return false;

        return true;
    }
}
