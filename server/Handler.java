package server;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.*;

import config.Config;
import util_my.Param;
import util_my.Params;

abstract class Handler implements HttpHandler {

   @Override
   public void handle(HttpExchange req) {
      try {
         String response;
         final String body = new String(req.getRequestBody().readAllBytes());
         try {
            response = getResponse(req, Params.parse(body));
         } catch (Exception e) {
            e.printStackTrace();
            response = "error=internal_error:" + e;
         }
         if (Config.serverMode && !response.equals("wait")) {
            System.out.println("serv in : " + req.getRequestURI() + " params : " +
                  Params.parse(body));
            System.out.println("response : " + response);
         }
         req.sendResponseHeaders(200, response.length());
         OutputStream os = req.getResponseBody();
         os.write(response.getBytes());
         os.close();
      } catch (Exception e) {
         e.printStackTrace();
         throw new Error(e);
      }
   }

   abstract String getResponse(HttpExchange req, Params params) throws IOException;
}