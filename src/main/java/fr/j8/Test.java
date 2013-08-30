package fr.j8;
import java.util.ArrayList;
import java.util.List;
import static java.util.Arrays.*;

public class Test {

    static interface fun<A, B> {
        B f(A a);
    }

    static interface fun2<A, B, C> {
        C f(A a, B b);

        default fun<A, fun<B, C>> curry() {
            return a -> b -> f(a, b);
        }
    }

    static class Cont<A> {
        List<A> l = new ArrayList<>();

        <B> Cont<B> map(fun<A, B> fc) {
            List<B> lb = new ArrayList<>();
            for (A a : l) lb.add(fc.f(a));
            Cont<B> cont = new Cont<>();
            cont.l = lb;
            return cont;
        }
    }



    public static void main(String[] args) {
        Cont<String> c = new Cont<>();
        c.l = asList("toto", "tatatiti");
        fun<String, Integer> f = String::length;
        fun2<Integer, String, String> f2 = (i, s) -> i + "-" + s;
        Cont<fun<String, String>> c2 = c.map(f).map(f2.curry());
        for (fun<String, String> s : c2.l) System.out.println(s.f("tutu"));
    }

}
