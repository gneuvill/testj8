package fr.j8;

import fj.F;
import fj.Show;
import fj.data.Stream;

import static fj.data.Stream.range;
import static fj.data.Stream.stream;

public class TestStreamFilter {

  public static void main(String[] args) {
    Show<Stream<Integer>> streamShow = Show.streamShow(Show.intShow);

    F<Integer, Boolean> filter = i -> {
      System.out.println("Eval in Filter : " + i);
      return i % 2 == 0;
    };

    F<Integer, Integer> mapper1 = i -> {
      System.out.println("Eval in Mapper1 : " + i);
      return i + 2;
    };

    F<Integer, Integer> mapper2 = i -> {
      System.out.println("Eval in Mapper2 : " + i);
      return i;
    };

    Stream<Integer> integers =
        stream(3, 5, 7, 9, 11, 13, 15).append(range(18)).takeWhile(i -> i < 31);

    //streamShow.println(integers.map(mapper).filter(filter));

    for (Integer i : integers.map(mapper1).filter(filter).map(mapper2))
      System.out.println(i);
  }
}
