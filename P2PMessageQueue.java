/**
 * This Queue maintains the queue of messages coming from connected clients.
 */
public class P2PMessageQueue {

    private P2PMessage head = null;
    private P2PMessage tail = null;


    /**
     * This method allows adding a message object to the existing queue.
     * @param oMessage
     */
    public synchronized void enqueue(P2PMessage oMessage){

		P2PMessage newMessage = new P2PMessage();
		newMessage = oMessage;
		if (head == null)
		{
			head = newMessage;
			tail = newMessage;
		}
		else
		{
			tail.next = newMessage;
			tail = newMessage;
		}


    }


    /**
     * This method allows removing a message object from the existing queue.
     * @return
     */
    public synchronized P2PMessage dequeue(){
		P2PMessage removedMessage = new P2PMessage();

		removedMessage = head;
		head = head.next;

		return removedMessage;

    }


    public boolean hasNodes(){

		if (head == null)
		{
			return false;
		}
		return true;
    }


	public P2PMessage getHead(){
		return head;
    }


	public P2PMessage getTail(){
		return tail;
    }
}

