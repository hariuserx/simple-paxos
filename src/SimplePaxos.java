import java.util.*;

public class SimplePaxos {

    List<Proposer> proposers = new ArrayList<>();
    List<Acceptor> acceptors = new ArrayList<>();
    List<Learner> learners = new ArrayList<>();
    Map<Integer, Acceptor> acceptorMap = new HashMap<>();
    Map<Integer, Learner> learnerMap = new HashMap<>();
    Map<Integer, Proposer> proposerMap = new HashMap<>();
    Random rand = new Random();

    SimplePaxos(int clientAddr, List<Integer> proposerAddr,
                List<Integer> learnerAddr,
                List<Integer> acceptorAddr, List<Integer> proposalValues,
                int quorum) {
        for (int add : proposerAddr) {
            Proposer p = new Proposer(add, quorum, acceptorAddr,
                    proposalValues.get(rand.nextInt(proposalValues.size())));
            proposers.add(p);
            proposerMap.put(add, p);
        }

        for (int add : learnerAddr) {
            Learner l = new Learner(clientAddr, add, quorum);
            learners.add(l);
            learnerMap.put(add, l);
        }

        for (int add : acceptorAddr) {
            Acceptor a = new Acceptor(add, learnerAddr);
            acceptors.add(a);
            acceptorMap.put(add, a);
        }
    }

    void runPaxos() {
        TransportLayer tp = new TransportLayer();

        for (Proposer p : proposers) {
            Prepare prepareMsg = p.sendPrepare();
            tp.sendMessage(prepareMsg, this);
        }
    }

}
