import java.util.List;

public class Proposer {

    ProposalID currentProposalId;
    ProposalID highestProposalId;
    final int processUniqueId;
    final int quorum;
    final List<Integer> acceptors;

    Proposer(int processUniqueId, int quorum, List<Integer> acceptors) {
        this.processUniqueId = processUniqueId;
        this.highestProposalId = new ProposalID(this.processUniqueId, 0);
        this.quorum = quorum;
        this.acceptors = acceptors;
    }

    public Accept sendAccept(int value) {
        if (this.currentProposalId == null) {
            currentProposalId = new ProposalID(this.processUniqueId, value);
            return new Accept(currentProposalId, value, acceptors);
        }

        return null;
    }

    /**
     * TODO. What happens when enough promises/denials are not received. Should we add a timeout? Do timeouts mean
     * unhealthy nodes
     * */
    public Prepare sendPrepare() {
        this.currentProposalId = new ProposalID(this.processUniqueId, this.highestProposalId.sequence + 1);
        this.highestProposalId = this.currentProposalId;

        return new Prepare(currentProposalId);
    }

}
