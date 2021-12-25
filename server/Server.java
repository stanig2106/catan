package server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class Server {

   public static void main() throws IOException {
      new Server().run();
   }

   final HttpServer server;

   Server() throws IOException {
      this.server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
      // init
      server.createContext("/rooms", new RoomsHandler());
      server.createContext("/room", new RoomHandler());
      server.createContext("/start", new StartHandler());
      server.createContext("/started", new StartedHandler());
      server.createContext("/map", new MapHandler());

      // plays
      server.createContext("/wait", new WaitHandler());

      server.createContext("/dices", new DicesHandler());
      server.createContext("/done", new DoneHandler());

      server.createContext("/", new E404Handler());
      server.setExecutor(null);
   }

   void run() {
      server.start();
   }
}
