import java.util.List;

public interface Message {
    int getFromAddress();

    List<Integer> getToAddress();
}
