package fr.j8;

public class TestLazyness {

    interface P1<A> {
        abstract A _1();
    }

    static <A> P1<A> p(A a) {
        return () -> a;
    }

    static <A> void printP1(P1<A> p1) {
        System.out.println(p1);
    }

    static String bomb() {
        throw new Error("Not lazy !!!");
    }

    public static void main(String[] args) {
        try {
            System.out.println("EAGER CALL : ");
            printP1(p(bomb()));
        } catch (Error e) {
            System.out.println("Bomb message : " + e.getMessage());
        } finally {
            System.out.println("**********");
            System.out.println("LAZY CALL : ");
            printP1(() -> bomb());
        }

    }

}
