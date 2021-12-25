package server;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;

import util_my.Params;

public class E404Handler extends Handler {

   @Override
   public void handle(HttpExchange req) {
      try {
         final String body = new String(req.getRequestBody().readAllBytes());
         // System.out.println("404 in : " + req.getRequestURI() + " params : " +
         // Params.parse(body));
         req.sendResponseHeaders(404, 0);
         OutputStream os = req.getResponseBody();
         os.write(new byte[0]);
         os.close();
      } catch (Exception e) {
         e.printStackTrace();
         throw new Error(e);
      }

   }

   @Override
   String getResponse(HttpExchange req, Params params) throws IOException {
      return "";
   }

}
