package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class ServerVariables {
   static final List<Room> rooms = new ArrayList<Room>();

   public static Optional<Room> getRoom(UUID uuidOfRoom) {
      return rooms.stream().filter(room -> room.uuid.equals(uuidOfRoom)).findFirst();
   }

   public static Optional<Room> getRoomOfPlayer(UUID uuidOfPlayer) {
      return rooms.stream().filter(room -> room.hasPlayerUuid(uuidOfPlayer)).findFirst();
   }

   public static String secureString(String string) {
      return secureString(string, -1);
   }

   public static String secureString(String string, int length) {
      // return string.replaceAll("[^A-Za-z0-9 ]", "").substring(0, length);
      // StringIndexOutOfBoundsException, wtf java !!!

      final String tempStringBCJavaIsABadLanguage = string.replaceAll("[^A-Za-z0-9_ ]", "");
      if (length == -1)
         return tempStringBCJavaIsABadLanguage;
      if (tempStringBCJavaIsABadLanguage.replaceAll(" ", "").length() == 0)
         return "_";

      return tempStringBCJavaIsABadLanguage.substring(0, Math.min(length, tempStringBCJavaIsABadLanguage.length()));
   }
}
