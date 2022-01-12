package online;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import util_my.Pair;
import util_my.Param;
import util_my.Params;
import util_my.Promise;

public class Request {
   final URL url;
   final Params params;

   public Request(String url, Param... params) {

      try {
         this.url = new URL(url);
      } catch (MalformedURLException e) {
         throw new Error(e);
      }

      this.params = new Params(params);
   }

   public Request(String url, Params params) {

      try {
         this.url = new URL(url);
      } catch (MalformedURLException e) {
         throw new Error(e);
      }

      this.params = params;
   }

   public Promise<String> send() {
      return this.send(0);
   }

   public Promise<String> send(int timeout) {
      return new Promise<String>((resolve, reject) -> {
         try {

            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(timeout);
            conn.setConnectTimeout(timeout);

            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);
            final OutputStream os = conn.getOutputStream();

            final BufferedWriter writer = new BufferedWriter(
                  new OutputStreamWriter(os, "UTF-8"));

            writer.write(this.params.getPostDataString());
            writer.flush();
            writer.close();
            os.close();
            final int responseCode = conn.getResponseCode();

            String response = "";
            if (responseCode == 200) {
               String line;
               final BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
               while ((line = br.readLine()) != null) {
                  response += line;
               }
            } else {
               reject.accept(new NOT200(responseCode));
            }

            resolve.accept(response);
            return;
         } catch (final SocketTimeoutException timeoutException) {
            reject.accept(new TIMEOUT(timeoutException));
            return;
         } catch (final Exception e) {
            e.printStackTrace();
            reject.accept(e);
            return;
         }
      });
   }

   // class Exception
   public static class NOT200 extends Exception {
      public final int code;

      NOT200(int code) {
         super("response code : " + code);
         this.code = code;
      }
   }

   public static class TIMEOUT extends Exception {
      TIMEOUT() {
         super();
      }

      TIMEOUT(SocketTimeoutException timeoutException) {
         super(timeoutException);
      }
   }

}
