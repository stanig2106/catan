package util_my;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.sun.net.httpserver.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Params {
   final Param[] params;

   public Params(Param... params) {
      this.params = params;
   }

   public Param[] toArray() {
      return this.params;
   }

   public Stream<Param> stream() {
      return Stream.of(params);
   }

   public Stream<String> keys() {
      return stream().map(param -> param.getKey());
   }

   public boolean contain(String key) {
      return keys().anyMatch(key::equals);
   }

   public Stream<String> getAll(String key) {
      return stream().filter(param -> param.getKey().equals(key)).map(param -> param.getValue());
   }

   public Optional<String> get(String key) {
      return getAll(key).findFirst();
   }

   public Params without(String... keys) {
      return new Params(stream().filter(param -> !Set.of(keys).contains(param.getKey())).toArray(Param[]::new));
   }

   @Override
   public String toString() {
      return "[" + Stream.of(params).map(Param::toString).collect(Collectors.joining(";")) + "]";
   }

   static public Params parse(String body) {
      return new Params(Stream.of(body.split("&")).map(paramString -> {
         String[] param = paramString.split("=");
         return new Param(param[0], param.length > 1 ? param[1] : "");
      }).toArray(Param[]::new));
   }

   public String getPostDataString() {
      return this.stream().map(param -> param.map((key, value) -> {
         try {
            return URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
         } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new Error(e);
         }
      })).collect(Collectors.joining("&"));
   }
}