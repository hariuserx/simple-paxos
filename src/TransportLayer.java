public class TransportLayer {
    // TODO
    // Idea is to assign probabilities of message loss at each node
    // and observe if the algorithm terminates and in how many steps.
    // Currently using unit tests to mock transport layer

    void sendMessage(Message message, SimplePaxos sp) {

        if (message == null)
            return;

        if (message instanceof Response resp) {
            System.out.println("Value Learned!!!! " + resp.chosenValue);
            return;
        }

        if (message instanceof Prepare prep) {
            for (int add : prep.getToAddress()) {
                Acceptor a = sp.acceptorMap.get(add);
                Message m = a.receivePrepare(prep);
                sendMessage(m, sp);
                //new Thread(() -> sendMessage(m, sp)).start();
            }
        }

        if (message instanceof Promise prom) {
            Proposer prop = sp.proposerMap.get(prom.getToAddress().get(0));
            Message m = prop.receivePromise(prom);
            sendMessage(m, sp);
            //new Thread(() -> sendMessage(m, sp)).start();
        }

        if (message instanceof NegativeAcceptorAck nack) {
            Proposer prop = sp.proposerMap.get(nack.getToAddress().get(0));
            Message m = prop.receiveNegativeAcks(nack);
            sendMessage(m, sp);
            //new Thread(() -> sendMessage(m, sp)).start();
        }

        if (message instanceof Accept acc) {
            for (int add : acc.getToAddress()) {
                Acceptor a = sp.acceptorMap.get(add);
                Message m = a.receiveAccept(acc);
                sendMessage(m, sp);
                //new Thread(() -> sendMessage(m, sp)).start();
            }
        }

        if (message instanceof Accepted accepted) {
            for (int add : accepted.getToAddress()) {
                Learner l = sp.learnerMap.get(add);
                Message m = l.receiveAccepted(accepted);
                sendMessage(m, sp);
                //new Thread(() -> sendMessage(m, sp)).start();
            }
        }

    }


}
