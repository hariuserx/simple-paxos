import java.util.List;

public class Accepted implements Message{

    final int acceptorId;
    final ProposalID acceptedProposalId;
    final int acceptedValue;

    public Accepted(int acceptorId, ProposalID acceptedProposalId, int acceptedValue) {
        this.acceptorId = acceptorId;
        this.acceptedProposalId = acceptedProposalId;
        this.acceptedValue = acceptedValue;
    }

    @Override
    public int getFromAddress() {
        return 0;
    }

    @Override
    public List<Integer> getToAddress() {
        return null;
    }
}
