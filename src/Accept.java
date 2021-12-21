import java.util.List;

public class Accept implements Message{

    final ProposalID proposalID;
    final int proposedValue;
    final List<Integer> acceptors;

    Accept(ProposalID proposalID, int proposedValue, List<Integer> acceptors) {
        this.proposalID = proposalID;
        this.proposedValue = proposedValue;
        this.acceptors = acceptors;
    }

    @Override
    public int getFromAddress() {
        return this.proposalID.processUniqueId;
    }

    @Override
    public List<Integer> getToAddress() {
        return this.acceptors;
    }
}
