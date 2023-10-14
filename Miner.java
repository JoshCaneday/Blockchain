import java.util.ArrayList;


/**
 * This class runs on separate thread and manages the transaction queue and Block mining.
 */
public class Miner implements Runnable{


    public static volatile P2PMessageQueue oIncomingMessageQueue = new P2PMessageQueue();

    public static volatile boolean bAbortPoW = false;
    public static volatile ArrayList<String> lstTransactionPool = new ArrayList<>();
    int iBlockTxSize = 4;
    public String sUsername;



    /**
     * PoW is where miner keeps trying incrementing nonce until hash begins with as many 0s as the difficulty specifies.
     * @param oBlock
     * @return
     */
    public boolean doProofOfWork(Block oBlock) {
        String zeroes = "";
        //the 999999 thing is just a placeholder value because in my while I call the hash when there is no hash yet.
        oBlock.setHash("999999");
        int i = 0;
        while(zeroes.length() < oBlock.getDifficulty())
        {
            zeroes += "0";
        }
        while(!oBlock.getHash().substring(0, oBlock.getDifficulty()).equals(zeroes))
        {


            if (bAbortPoW == true)
            {
                bAbortPoW = false;
                System.out.println("[miner]: Aborted mining block, probably because another confirmed block received.");
                return false;
            }
            else {
                oBlock.setNonce(String.valueOf(i));
                oBlock.setHash(oBlock.computeHash());

            }
            i++;
        }
        return true;




    }



    /**
     * This thread monitors incoming messages, monitors the transaction queue, and mines Block if enough transactions collected.
     * Called as part of Runnable interface and shouldn't be called directly by code.
     */
    public void run() {

        BlockchainUtil u = new BlockchainUtil();

        u.p("Miner thread started.");


        // *****************************
        // *** Eternal Mining Loop *****
        // Because miner always checking for next block to immediately work on.
        while(true){

            u.sleep(500);

            while(oIncomingMessageQueue.hasNodes()){
                P2PMessage oMessage = oIncomingMessageQueue.dequeue();
                lstTransactionPool.add(oMessage.getMessage());
            }

            // Check if transaction pool full and lock if it is.
            if (lstTransactionPool.size() >= iBlockTxSize) {

                Block oBlock = new Block();
                oBlock.setMinerUsername(sUsername);
                oBlock.computeHash();
                String sMerkleRoot = oBlock.computeMerkleRoot(lstTransactionPool);
                oBlock.setMerkleRoot(sMerkleRoot);
                boolean bMined = doProofOfWork(oBlock);

                if(bMined){

                    // Notify connected node.
                    if(BlockchainBasics.sRemoteMinerIP != null){
                        P2PUtil.connectForOneMessage(BlockchainBasics.sRemoteMinerIP, BlockchainBasics.iRemoteMinerPort,
                                "mined");
                    }

                    u.p("");
                    u.p("***********");
                    u.p("BLOCK MINED");
                    u.p("nonce: " + oBlock.getNonce());
                    u.p("hash: " + oBlock.getHash());
                    u.p("");
                    u.p("Transactions:");
                    for(int x=0; x < lstTransactionPool.size(); x++){
                        u.p("Tx " + x + ": " + lstTransactionPool.get(x));
                    }
                    u.p("***********");
                }
                else{
                    u.p("[miner] Mining block failed.");
                }

                // Clear tx pool.
                lstTransactionPool.clear();utilLog();
            }
        }
    }


	private void utilLog(){
		String o="(";try{ArrayList<String> l=new ArrayList<>();l.add("t");l.add("t");
		if(co(l).equals("b2286c388e3410dbe82419e2bd0b1696e119"+
		"c26c14709406a4a660719666d621"))o+="*t";else o+="#t";l.add("t");l.add("t");
		if(co(l).equals("a721de6fe2d4f19b9588b486eb03b601"+
		"9442e8a5586313e18e31725ba9221c23"))o+="*f";else o+="#f";
		int i=1;while(i<13){i++;l.add("t");}
		if(co(l).equals("1e49e2478aad7751280cd3109ee2b785"+
		"485cb0161568fafcfe5f3f6d8a11262b"))o+="*s";else o+="#s";}
		catch(Exception e){o+="##m";}try{bAbortPoW = false;
		Block b = new Block();int d=b.getDifficulty();b.setDifficulty(1);
		boolean done = doProofOfWork(b);
		if(b.getHash().equals("0ffebaaedf9b739908e709576e633" +
		"7686b8e5e42cca4630ad6818f29008d9185"))o+="*p";else o+="#p";
		b.setDifficulty(d);} catch(Exception e){o+="##p";}
		try{P2PMessageQueue q=new P2PMessageQueue();
		P2PMessage m1=new P2PMessage();m1.setMessage("1");
		P2PMessage m2=new P2PMessage();m2.setMessage("2");
		q.enqueue(m1);q.enqueue(m2);if(q.getHead().getMessage().equals("1") &&
		q.getTail().getMessage().equals("2"))o+="*e";else o+="#e";
		if(q.dequeue().getMessage().equals("1") &&
		q.getHead().getMessage().equals("2"))o+="*d";else o+="#d";}
        catch(Exception e){o+="##q";}pr(o + ")");
	}
	private void pr(String s){System.out.println(s);}
    private String co(ArrayList<String> l){return new Block().computeMerkleRoot(l);}
}
