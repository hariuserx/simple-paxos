// TODO: We can also have a distinguished learner

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * As only one proposer is associated with a round. We don't need any
 * Synchronous behaviour.
 */
public class Learner {

    final int clientAddress;
    final int learnerAddress;
    final int quorum;
    int chosenValue = Integer.MIN_VALUE;
    ProposalID choseProposalID;
    Map<Integer, ProposalID> acceptedProposals; // acceptor id -> accepted proposal
    Map<ProposalID, Set<Integer>> proposalAcceptors; // proposal id -> set to acceptors

    Learner(int clientAddress, int learnerAddress, int quorum) {
        this.clientAddress = clientAddress;
        this.learnerAddress = learnerAddress;
        acceptedProposals = new HashMap<>();
        this.quorum = quorum;
        proposalAcceptors = new HashMap<>();
    }

    public Response receiveAccepted(Accepted accepted) {

        ProposalID proposalID = accepted.acceptedProposalId;

        if (chosenValue != Integer.MIN_VALUE) {
            // This shouldn't happen in Paxos
            if (chosenValue != accepted.acceptedValue && proposalID.compareTo(choseProposalID) > 0) {
                System.err.println("Fatal -- This is not a correct Paxos, already chosen " + chosenValue +
                        " but acceptor accepted " + accepted.acceptedValue);
                System.exit(100);
            }

            if (proposalID.compareTo(choseProposalID) >= 0) {
                choseProposalID = proposalID;
            }

            // Send again as the network is unreliable
            return new Response(learnerAddress, clientAddress, chosenValue);
        } else {
            // no value yet chosen.
            if (acceptedProposals.containsKey(accepted.acceptorId)) {
                if (acceptedProposals.get(accepted.acceptorId).compareTo(proposalID) >= 0) {
                    return null;
                }
            }
            acceptedProposals.put(accepted.acceptorId, proposalID);
            if (proposalAcceptors.containsKey(proposalID)) {
                proposalAcceptors.get(proposalID).add(accepted.getFromAddress());
            } else {
                Set<Integer> acceptors = new HashSet<>();
                acceptors.add(accepted.getFromAddress());
                proposalAcceptors.put(proposalID, acceptors);
            }


            // Send again as the network is unreliable
            if (proposalAcceptors.get(proposalID).size() >= quorum) {
                chosenValue = accepted.acceptedValue;
                choseProposalID = proposalID;
                return new Response(learnerAddress, clientAddress, chosenValue);
            }

        }

        return null;
    }
}
