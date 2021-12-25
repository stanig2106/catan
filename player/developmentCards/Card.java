package player.developmentCards;

import java.util.Collections;
import java.util.LinkedList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.sound.midi.Instrument;

import globalVariables.GameVariables;

public enum Card {
   Knight, Library, Monopoly, YearOfPlenty, RoadBuilding;

   @Override
   public String toString() {
      switch (this) {
         case Knight:
         case Library:
         case Monopoly:
            return super.toString();
         case YearOfPlenty:
            return "Year of plenty";
         case RoadBuilding:
            return "Road building";
         default:
            throw new EnumConstantNotPresentException(this.getClass(), this.name());
      }
   }

   public String getTitle() {
      return this.toString();
   }

   public Stream<String> getDescription() {
      switch (this) {
         case Knight:
            return Stream.of("Move the robber.",
                  "Steal 1 resource from the",
                  "owner of a settlement or",
                  "city adjacent to the",
                  "robber’s new hex.");
         case Library:
            return Stream.of("1 Victory Point !",
                  "Reveal this card on your",
                  "turn if, with it, you reach",
                  "the number of points",
                  "required for victory.");
         case Monopoly:
            return Stream.of("When you play this card,",
                  "announce 1 type of resource.",
                  "All other players must give",
                  "you all of their resources",
                  "of that type.");
         case YearOfPlenty:
            return Stream.of("When you play this card,",
                  "you can immediately",
                  "draw 2 resources",
                  "of your choice in the",
                  "bank.");
         case RoadBuilding:
            return Stream.of("When you play this card,",
                  "you can immediately",
                  "build 2 road.");
         default:
            throw new EnumConstantNotPresentException(this.getClass(), this.name());
      }
   }

   public int numberOfCopy() {
      switch (this) {
         case Knight:
            return 14;
         case Library:
            return 5;
         case RoadBuilding:
         case YearOfPlenty:
         case Monopoly:
            return 2;
         default:
            throw new EnumConstantNotPresentException(this.getClass(), this.name());
      }
   }

   public static LinkedList<Card> newPoolCards() {
      LinkedList<Card> res = new LinkedList<Card>();
      Stream.of(Card.values()).forEach(card -> IntStream.range(0, card.numberOfCopy()).forEach(_i -> res.add(card)));
      Collections.shuffle(res);
      return res;
   }

}