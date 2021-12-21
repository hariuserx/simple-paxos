public class Prepare implements Message{

    final ProposalID proposalID;

    Prepare(ProposalID proposalID) {
        this.proposalID = proposalID;
    }

    @Override
    public int getFromAddress() {
        return 0;
    }
}
