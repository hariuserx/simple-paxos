import java.util.List;

public class NegativeAcceptorAck implements Message {

    final int fromAcceptorId;
    final ProposalID receivedProposalId;
    final ProposalID alreadyPromisedProposalId;

    public NegativeAcceptorAck(int fromAcceptorId, ProposalID receivedProposalId, ProposalID alreadyPromisedProposalId) {
        this.fromAcceptorId = fromAcceptorId;
        this.receivedProposalId = receivedProposalId;
        this.alreadyPromisedProposalId = alreadyPromisedProposalId;
    }

    @Override
    public int getFromAddress() {
        return fromAcceptorId;
    }

    @Override
    public List<Integer> getToAddress() {
        return List.of(receivedProposalId.processUniqueId);
    }
}
