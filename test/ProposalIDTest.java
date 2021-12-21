import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ProposalIDTest {

    @Test
    void testProposalIDs() {
        ProposalID p11 = new ProposalID( 1, 1);
        ProposalID p12 = new ProposalID( 1, 2);
        ProposalID p13 = new ProposalID( 1, 3);
        ProposalID p21 = new ProposalID( 2, 1);
        ProposalID p22 = new ProposalID( 2, 2);
        ProposalID p3100 = new ProposalID( 3, 100);

        List<ProposalID> ids = new ArrayList<>();

        ids.add(p12);
        ids.add(p13);
        ids.add(p3100);
        ids.add(p22);
        ids.add(p21);
        ids.add(p11);

        Collections.sort(ids);

        assertEquals(p11, ids.get(0));
        assertEquals(p21, ids.get(1));
        assertEquals(p12, ids.get(2));
        assertEquals(p22, ids.get(3));
        assertEquals(p13, ids.get(4));
        assertEquals(p3100, ids.get(5));
    }
}