import java.util.Arrays;
import java.util.List;

public class Promise implements Message{

    final int fromAcceptorId;
    final ProposalID receivedProposalId;
    final ProposalID acceptedProposalId;
    final int acceptedProposalValue;

    public Promise(int fromAcceptorId, ProposalID receivedProposalId,
                   ProposalID acceptedProposalId, int acceptedProposalValue) {
        this.fromAcceptorId = fromAcceptorId;
        this.receivedProposalId = receivedProposalId;
        this.acceptedProposalId = acceptedProposalId;
        this.acceptedProposalValue = acceptedProposalValue;
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
