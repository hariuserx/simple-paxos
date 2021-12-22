import java.util.List;

public class Accepted implements Message {

    final int acceptorId;
    final ProposalID acceptedProposalId;
    final int acceptedValue;
    final List<Integer> learnerAddresses;

    public Accepted(int acceptorId, ProposalID acceptedProposalId, int acceptedValue,
                    List<Integer> learnerAddresses) {
        this.acceptorId = acceptorId;
        this.acceptedProposalId = acceptedProposalId;
        this.acceptedValue = acceptedValue;
        this.learnerAddresses = learnerAddresses;
    }

    @Override
    public int getFromAddress() {
        return acceptorId;
    }

    @Override
    public List<Integer> getToAddress() {
        return learnerAddresses;
    }
}
