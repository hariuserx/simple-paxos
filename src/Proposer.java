import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Proposer {

    ProposalID currentProposalId;
    // Highest proposal id observed in the entire distributed system
    ProposalID highestProposalId;
    final int processUniqueId;
    final int quorum;
    final List<Integer> acceptors;
    Set<Integer> negativeAckedAcceptors;
    Set<Integer> promisedProposalAcceptors;
    int proposalValue;

    Proposer(int processUniqueId, int quorum, List<Integer> acceptors, int proposalValue) {
        this.processUniqueId = processUniqueId;
        this.highestProposalId = new ProposalID(this.processUniqueId, 0);
        this.quorum = quorum;
        this.acceptors = acceptors;
        negativeAckedAcceptors = new HashSet<>();
        promisedProposalAcceptors = new HashSet<>();
        this.proposalValue = proposalValue;
    }


    /**
     * TODO: What happens when enough promises/denials are not received. Should we add a timeout? Do timeouts mean
     * unhealthy nodes
     */
    public Prepare sendPrepare() {
        // reset
        negativeAckedAcceptors.clear();
        promisedProposalAcceptors.clear();

        this.currentProposalId = new ProposalID(this.processUniqueId, this.highestProposalId.sequence + 1);
        this.highestProposalId = this.currentProposalId;

        return new Prepare(currentProposalId, acceptors);
    }
    public Accept receivePromise(Promise message) {

        // ignore old proposals
        if (message.receivedProposalId != currentProposalId)
            return null;

        if (promisedProposalAcceptors.contains(message.fromAcceptorId))
            return null;

        promisedProposalAcceptors.add(message.fromAcceptorId);
        if (message.acceptedProposalId != null && message.acceptedProposalId.compareTo(highestProposalId) > 0) {
            highestProposalId = message.acceptedProposalId;
            proposalValue = message.acceptedProposalValue;
        }

        if (promisedProposalAcceptors.size() >= quorum) {
            return sendAccept();
        }

        return null;
    }

    // After reaching qourum, this calls prepare
    public Prepare receiveNegativeAcks(NegativeAcceptorAck message) {
        if (message.alreadyPromisedProposalId.compareTo(highestProposalId) > 0)
            highestProposalId = message.alreadyPromisedProposalId;

        // could be an old one. Just ignore it.
        if (message.receivedProposalId != currentProposalId) {
            return null;
        } else {
            negativeAckedAcceptors.add(message.fromAcceptorId);
            if (negativeAckedAcceptors.size() >= quorum)
                return sendPrepare();
        }

        return null;
    }

    public Accept sendAccept() {
        return new Accept(currentProposalId, proposalValue, acceptors);
    }

}
