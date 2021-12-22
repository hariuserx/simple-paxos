import java.util.List;

public class Prepare implements Message {

    final ProposalID proposalID;
    final List<Integer> acceptorAddresses;

    Prepare(ProposalID proposalID, List<Integer> acceptorAddresses) {
        this.proposalID = proposalID;
        this.acceptorAddresses = acceptorAddresses;
    }

    @Override
    public int getFromAddress() {
        return proposalID.processUniqueId;
    }

    @Override
    public List<Integer> getToAddress() {
        return acceptorAddresses;
    }
}
