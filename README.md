# simple-paxos
An attempt to implement Paxos protocol and test it in a mock environment. Paxos has many variants. Here we try to 
implement some basic paxos protocols. 

##Notes 
### Some definitions
Before getting to the Paxos protocol, lets understand few problems with distributed systems

#### Consensus Problem
Consensus is agreement of different distributed nodes/systems/processes for a single data value.
<br>
This is important because systems/processes are inherently faulty and for overall system reliability there needs to be 
a consensus. If there is no consensus, different nodes might perform different operations and maintain different states 
effecting the actual computation to be performed as a whole. Let's say, a database cluster has to elect a new leader, 
if there is no consensus among the nodes, multiple nodes may be leaders breaking the whole system.  
<br>
Consensus is not only to agree upon the data value/event but to agree it in a durable manner when there is a fault in 
the system/node.   
<br>
Ex: Database transaction to commit. When a slave is back up after restart, it has to agree upon all the commits that 
have taken place during the downtime.


Some Protocols that can help to achieve consensus in distributed systems.
1. Paxos
2. Raft

No matter the protocol we choose, there are three conditions that should be satisfied for distributed consensus.

1. Agreement -- all healthy nodes should agree on same value/commit decision
2. Validity -- The agreed value should be suggested by a healthy node
3. Termination -- Every healthy node should eventually come to consensus.

#### FLP Impossibility result
In asynchronous network where messages can be indefinitely delayed but not lost, no 
deterministic consensus algorithm is guaranteed to terminate in every algorithm run
if there is at-least one unhealthy node.<br>_Using timeouts/heartbeats, we can detect failures bypassing this result._
<br>_We can also use randomized algorithms_

#### Types of failures in distributed systems
1. Crash Failures --- Node crash due to some failure. Ex: Disk failure
2. Byzantine failure --- Unreliable nodes (Invalid data sent by a node that can effect the whole system). 
   Ex: Hacked node, Bad rolling deployment (older nodes send invalid data)

Byzantine fault tolerant systems are the most robust but require exponential number of message transmissions for 
consensus and more replications than other consensus protocols.

### Quorum
A quorum is the minimum number of votes that a distributed transaction has to obtain in order to be allowed to perform 
an operation in a distributed system. A quorum-based technique is implemented to enforce consistent operation in a 
distributed system.

#### Paxos Protocol
Here we have consensus on the majority value. So if we have n unhealthy nodes, we need at-least n + 1 nodes to agree on 
the value. So minimum number of nodes are 2n + 1.
This protocol doesn't solve Byzantine failures.

Requirements for Consensus are

* Only the proposed value may be chosen
* Only single value is chosen
* A process learns a value to be chosen only if it is actually chosen

Now the above three roles can be performed by agents of different classes:

* proposers
* acceptors
* learners

Assume that a process can take any role, process has memory, and there is asynchronous communication between processes.

**Choosing a value**

_Requirements:_
1. Assign different proposals to different numbers. A proposal must have a unique number/identifier.**
2. Acceptor is allowed to accept more than one proposal/value
3. Value is chosen when the majority of acceptors accept it.
4. Multiple proposals can be chosen, but they must have the same value.

To satisfy the above requirements, the following algorithm for proposals can be adopted

1. A proposer chooses a new proposal with number/identifier n as asks acceptors to respond with
   1. A promise to not accept a proposal < n
   2. If already accepted, send the highest number of the accepted proposal.
2. If no acceptors accepted any proposal, the proposer proposes with its value
3. If any acceptor has already accepted, then the proposer sends the proposal with the value of the highest numbered 
   proposal already accepted.
4. Steps 2 and 3 are part of the _accept_ phase.
5. An acceptor can accept a proposal n iff it has not responded to a prepare request having number > n.
6. An acceptor can always respond to a prepare request.
7. An acceptor ignore a _prepare_ proposal numbered n if it already promised a proposal numbered > n.
8. An acceptor also ignores _prepare_ for a proposal n if it has already accepted the same proposal.
9. An acceptor need to only remember the highest proposal it accepted and the highest _prepare_ request it has 
   responded to even if it has restarted/up after any crash.
10. A proposer can abandon a proposal and ignore any further incoming responses. One optimization could be to drop a 
    proposal if an acceptor responded to a higher prepare request given that it gives the proposer this information.
11. Proper remembers just the highest proposal it has issued.

**Learning a chosen value**
Learner can learn a chosen value in the following ways:
1. Have acceptors tell learners when a value is accepted. Majority one can be accepted.
2. Have a set of distinguished learners that learn from the acceptors and inform other learners. (Reduces the number 
   of messages)
3. A learner can have a proposer issue a new proposal to learn the value in case of failed acceptors.


**Algorithm termination**
When there are more than two proposers, the algorithm may not terminate as proposers compete in the _prepare_ phase.
To avoid this, a distinguished proposer (which can only issue proposals) can be chosen randomly or 
using the highest id. **

**Paxos proof for requirements satisfaction**<br>
Requirements 1, 2 and 3 are trivial by seeing how the algorithm works.
For requirement 3 (multiple proposals can be chosen but they must have the same value) 
lets use induction.
To prove requirement 3, lets try to prove that if an acceptor accepted value v, then
no value v' != v can be chosen in any previous round.
This statement is much strict and directly implies Requirement 3.
For round i = 0, it is trivial as there is no previous round. Lets say that the
statement holds till i-1 rounds. Now for round i, let v be accepted value by an acceptor from
a set Q of acceptors with |Q| >= 2n + 1 (Quorum). Let j be the highest round already 
accepted from the promises in Q. if v is accepted value in i, then atleast one acceptor
in Q in round j must have accepted value v. Now lets say that round j has consensus with
value v'. This implies in round j, there are >= 2n + 1 acceptors with v' and other 
acceptors in round j can have either a null value or v' as the proposals(rounds)
are unique (Every proposer while proposing creates a unique round identifier) and a
proposer only proposes one value in a round. Now, Q has >= 2n + 1 acceptors, so an
acceptor in Q accepting v must be an acceptor in round j with value v'. This proves v = v'.

**Multi paxos**
In practical scenarios, we require a sequence of transactions to be performed. So we run Paxos for each transaction.

#### Brief overview of other consensus algorithms
* **Raft**
   * Majority based with single leader
   * All nodes start in follower state
   * Follower can become a candidate if they don't hear from the leader
   * candidate requests votes
   * candidate becomes a leader with majority votes
   * This phase is leader election
     * Using an election timeout(randomized) a follower can become a candidate
     * Now the candidate starts an election term and request other nodes for a vote. Other nodes vote for this term if 
       not already votes resetting the election timeout. ** (How to decide term number)
     * If received majority the candidate becomes leader and sends heartbeats to other nodes (resetting their election 
       timeout).
     * If majority is not reached (when there is competition), reelection happens (new term) and eventually a node 
       becomes a leader (due to timeout randomness)
   * Any new change is now propagated through the leader and a change is committed if the majority of the nodes write 
     the change/value to the log entry
   * This is log replication
   * In case of network partitions
     * We have multiple leaders
     * Partitions with non-majority nodes stay uncommitted
        * Special case when network partition is such that no partition has a majority.
     * Upon fixing the partition, the leader with lower election term will step down and match the log of the leader 
       with the latest term.
   * This doesn't solve for Byzantine failures
* **PoW**
  * In PoW (Proof of Work), a node has to prove that it spend a reasonable amount of computational work to add a block 
    in the decentralized ledger.
  * Here, even if there is an adversary, it has to overtake the majority of the work done by the entire system in order 
    to add corrupt block
  * This deals with byzantine failures
  * Used in blockchain (Ex: Bitcoin, Ether)
* **PoS**
  * PoW requires a lot of computation power and time. In Proof of Stake (PoS), a node's validity is determined by the 
    amount of resources/tokens it puts on stake. 
* Depending on the type of fault tolerance, we have some other algorithms suited for differenet use cases like, PoA, 
  PoB, PoC, etc.
* Paxos also has several variants
  * _Cheap Paxos_ -- Reduces the number of processes required to be healthy by using auxiliary processors 
    for reconfiguration
  * _Fast Paxos_ -- Reduces the end-to-end delay by directly connecting client to acceptors when leader has
    no value to propose. This requires 3n + 1 processors to tolerate n failures.
  * _Multi-Paxos_ where Phase1[3] (Propose/Promise) can be skipped for future Paxos instances with the same leader. 

#### Applications
Google BigTable, Spanner, ZooKeeper

#### References
1. https://en.wikipedia.org/wiki/Byzantine_fault <br>
2. https://en.wikipedia.org/wiki/Consensus_(computer_science) <br>
3. https://lamport.azurewebsites.net/pubs/paxos-simple.pdf
4. https://en.wikipedia.org/wiki/Quorum_(distributed_computing)
5. https://lamport.azurewebsites.net/pubs/lamport-paxos.pdf
6. http://wwwusers.di.uniroma1.it/~stefa/Distributed_Systems_18-19/Schedule_files/fastpaxosfordummies.pdf
7. https://en.wikipedia.org/wiki/Paxos_(computer_science)
8. https://zookeeper.apache.org/doc/r3.2.2/zookeeperInternals.html


## Code explanation
