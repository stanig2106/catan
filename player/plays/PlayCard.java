package player.plays;

import player.Player;
import player.developmentCards.Card;

public class PlayCard extends Play {
   final int indexOfCard;

   public PlayCard(Player player, int indexOfCard) {
      super(player);
      this.indexOfCard = indexOfCard;
   }

   @Override
   public void execute() {
      final Card card = player.inventory.cards.get(indexOfCard).getKey();
      player.inventory.cards.remove(indexOfCard);

      System.out.println(card + " played");
   }

}