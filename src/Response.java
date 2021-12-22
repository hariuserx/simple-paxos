import java.util.List;

public class Response implements Message {

    final int learnerAddress;
    final int clientAddress;
    final int chosenValue;

    Response(int learnerAddress, int clientAddress, int chosenValue) {
        this.learnerAddress = learnerAddress;
        this.chosenValue = chosenValue;
        this.clientAddress = clientAddress;
    }

    @Override
    public int getFromAddress() {
        return learnerAddress;
    }

    @Override
    public List<Integer> getToAddress() {
        return List.of(clientAddress);
    }
}
