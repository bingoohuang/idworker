package org.n3r.idworker;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SidTest {
    @Test
    public void test1() {
        for (int i = 0; i < 10000; ++i) {
            assertThat(Sid.next().length(), is(21));
            assertThat(Sid.nextShort().length(), is(16));
        }
    }


}
