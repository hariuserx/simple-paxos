import org.junit.jupiter.api.Test;

import java.util.List;

class SimplePaxosTest {

    @Test
    void testRunPaxos() {
        SimplePaxos paxos = new SimplePaxos(1, List.of(2, 3, 4),
                List.of(5, 6, 7), List.of(8, 9, 10), List.of(100, 200, 300, 400, 700, 600, 500), 2);

        paxos.runPaxos();
    }

}