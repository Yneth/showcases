package ua.abond.netty.game.util.function;

@FunctionalInterface
public interface Callable2<A, B> {
    void apply(A a, B b);
}
