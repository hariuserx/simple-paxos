import java.util.List;

public class Acceptor {

    final int processUniqueId;
    final List<Integer> learnerAddresses;
    ProposalID highestAcceptedProposal;
    ProposalID highestPromisedProposal;
    // min value denotes no accepted value
    int acceptedProposalValue = Integer.MIN_VALUE;

    Acceptor(int processUniqueId, List<Integer> learnerAddresses) {
        this.processUniqueId = processUniqueId;
        this.learnerAddresses = learnerAddresses;
    }

    // TODO: Test for Asynchronous behaviour
    public Message receivePrepare(Prepare message) {
        if (highestPromisedProposal == null || message.proposalID.compareTo(highestPromisedProposal) >= 0) {
            highestPromisedProposal = message.proposalID;
            return new Promise(processUniqueId, message.proposalID, highestAcceptedProposal, acceptedProposalValue);
        } else {
            // Send this to tell proposer to stop listening to promises. An acceptor can also simply ignore the prepare
            // without responding.
            return new NegativeAcceptorAck(processUniqueId, message.proposalID, highestPromisedProposal);
        }
    }

    // TODO: Test for Asynchronous behaviour
    public Message receiveAccept(Accept message) {
        if (highestPromisedProposal == null || message.proposalID.compareTo(highestPromisedProposal) >= 0) {
            highestPromisedProposal = message.proposalID;
            acceptedProposalValue = message.proposedValue;
            highestAcceptedProposal = message.proposalID;
            return new Accepted(this.processUniqueId, highestAcceptedProposal, acceptedProposalValue, learnerAddresses);
        } else {
            // can also ignore without responding anything
            return new NegativeAcceptorAck(processUniqueId, message.proposalID, highestPromisedProposal);
        }
    }

}
