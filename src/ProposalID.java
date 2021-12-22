import java.util.Objects;

/**
 * All proposal identifiers must be unique.
 * Use a combination of process unique id and a number.
 **/
public class ProposalID implements Comparable<ProposalID> {
    // Ex: This could be IP address given static assignment
    final int processUniqueId;
    final int sequence;


    public ProposalID(int processUniqueId, int sequence) {
        this.processUniqueId = processUniqueId;
        this.sequence = sequence;
    }

    @Override
    public int compareTo(ProposalID o) {
        int val = Integer.compare(this.sequence, o.sequence);
        return val == 0 ? Integer.compare(this.processUniqueId, o.processUniqueId) : val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProposalID that = (ProposalID) o;
        return processUniqueId == that.processUniqueId && sequence == that.sequence;
    }

    @Override
    public int hashCode() {
        return Objects.hash(processUniqueId, sequence);
    }
}
