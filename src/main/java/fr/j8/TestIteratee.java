package fr.j8;

import fj.Effect;
import fj.F;
import fj.F2;
import fj.control.Trampoline;
import fj.data.Stream;
import org.supercsv.io.ICsvBeanWriter;

import java.io.IOException;

import static fj.Bottom.error;
import static fj.data.Iteratee.Input;
import static fj.data.Iteratee.Input.el;
import static fj.data.Iteratee.IterV;
import static fj.data.Iteratee.IterV.cont;
import static fj.data.Iteratee.IterV.done;
import static fj.data.Stream.cycle;
import static fj.data.Stream.stream;

class TestIteratee {

    /**
     * Takes a list and an iteratee and feeds the list’s elements to the iteratee.
     */
    static F2<Stream<String>, IterV<String, Integer>, IterV<String, Integer>> oldEnumerate =
        (xs, i) -> {
            if (xs.isEmpty()) return i;
            else return i.fold(t -> i, fi -> oldEnumerate.f(xs.tail()._1(), fi.f(el(xs.head())))
            );
        };

    static F2<Stream<String>, IterV<String, Integer>, Trampoline<IterV<String, Integer>>> enumerate =
        (xs, i) -> {
            if (xs.isEmpty()) return Trampoline.pure(i);
            else return Trampoline.suspend(() -> i.fold(
                t -> Trampoline.pure(i),
                fi -> enumerate.f(xs.tail()._1(), fi.f(el(xs.head())))
            ));
        };

    static F<Integer, F<Input<String>, IterV<String, Integer>>> count =
        n -> i -> i.apply(
            () -> cont(count.f(n)),
            () -> s -> cont(count.f(n + 1)),
            () -> done(n, i)
        );

    /**
     * An iteratee that counts the number of strings it has seen
     */
    static IterV<String, Integer> counter() {
        return cont(count.f(0));
    }

    @FunctionalInterface
    static interface SuperCSV<A> {

        A run() throws IOException;

        static <B> SuperCSV<B> superCSV(final B b) {
            return () -> b;
        }

        default <B> SuperCSV<B> map(final F<A, B> f) {
            return () -> f.f(run());
        }

        default <B> SuperCSV<B> bind(final F<A, SuperCSV<B>> f) {
            return () -> f.f(run()).run();
        }

        static class Step<T> {
            F<Effect<ICsvBeanWriter>, F<Input<T>, IterV<T, Effect<ICsvBeanWriter>>>> step =
                ei -> i -> i.apply(
                    () -> cont(step.f(ei)),
                    () -> t -> cont(step.f(iw -> {
                        try {
                            ei.e(iw);
                            iw.write(t, "name");
                        } catch (Exception e) {
                            error(e.getLocalizedMessage());
                        }
                    })),
                    () -> done(ei, i));
        }


        static <T> IterV<T, Effect<ICsvBeanWriter>> write() {
            return cont(new Step<T>().step.f(iw -> {
                try {
                    iw.writeHeader("name");
                } catch (IOException e) {
                    error(e.getLocalizedMessage());
                }
            }));
        }



        final class Loop<T> {
            F2<Stream<T>, IterV<T, Effect<ICsvBeanWriter>>, Trampoline<SuperCSV<IterV<T, Effect<ICsvBeanWriter>>>>> loop =
                (xs, iv) -> Trampoline.suspend(() -> iv.fold(
                    t -> Trampoline.pure(superCSV(iv)),
                    fi -> {
                        if (xs.isEmpty()) return Trampoline.pure(superCSV(iv));
                        else return loop.f(xs.tail()._1(), fi.f(el(xs.head())));
                    }
                ));
        }
        static <T> SuperCSV<IterV<T, Effect<ICsvBeanWriter>>> enumWriter(Stream<T> source, IterV<T, Effect<ICsvBeanWriter>> it) {
            return new Loop<T>().loop.f(source, it).run();
        }
    }

    public static final class Person {
        private final String name;

        private Person(String name) {
            this.name = name;
        }

        static Person pers(String name) {
            return new Person(name);
        }

        public String getName() { return name; }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(enumerate.f(cycle(stream("toto", "tata", "tutu")).take(100000), counter()).run().run());

//        try (final Writer w = new FileWriter(Paths.get("/tmp/test.csv").toFile());
//             final ICsvBeanWriter iw = new CsvBeanWriter(w, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE)) {
//            final Stream<Person> source = cycle(stream(pers("toto"), pers("tata"), pers("tutu"))).take(10000);
//            SuperCSV.enumWriter(source, SuperCSV.<Person>write()).run().run().e(iw);
//        }
    }

}
