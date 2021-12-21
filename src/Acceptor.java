public class Acceptor {

    ProposalID highestAcceptedProposal;
    ProposalID highestPromisedProposal;
    final int processUniqueId;
    // min value denotes no accepted value
    int acceptedProposalValue = Integer.MIN_VALUE;

    Acceptor(int processUniqueId) {
        this.processUniqueId = processUniqueId;
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
            return new Accepted(this.processUniqueId, highestAcceptedProposal, acceptedProposalValue);
        } else {
            // can also ignore without responding anything
            return new NegativeAcceptorAck(processUniqueId, message.proposalID, highestPromisedProposal);
        }
    }

}
