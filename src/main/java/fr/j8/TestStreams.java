package fr.j8;

import fj.data.List;
import fj.data.Stream;

import static fj.data.List.list;
import static fj.data.Stream.stream;

public class TestStreams {

  public static void main(String[] args) {
    System.out.println("****** List are EAGER ******");
    List<String> lstrs = list("a", "b", "c");
    lstrs.map(s -> {
      System.out.println("In map");
      return s;
    }).foreach(s -> {
      System.out.print("In foreach ");
      System.out.println(s);
    });

    System.out.println("****** Streams are LAZY ******");
    Stream<String> strs = stream("a", "b", "c");
    strs.map(s -> {
      System.out.println("In map");
      return s;
    }).foreach(s -> {
      System.out.print("In foreach ");
      System.out.println(s);
    });

  }




}
