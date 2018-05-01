package com.w11k.techday.docker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FooTest {

    private Foo foo;

    @BeforeEach
    void setupFoo() {
        this.foo = new Foo();
    }

    @Test
    void doUsefullStuff() {
        assertEquals(0, foo.getCounter());
        foo.doUsefullStuff();
        assertEquals(1, foo.getCounter());
    }
}