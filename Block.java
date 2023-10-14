import java.sql.SQLOutput;
import java.util.ArrayList;


/**
 * This holds the values for a Block in the Blockchain, and it has methods to compute the Merkle Root and generate a hash.
 */
public class Block {


    private String sMerkleRoot;
    private int iDifficulty = 5; // Mining seconds in testing 5: 6,10,15,17,20,32 | testing 6: 12,289,218
    private String sNonce;
    private String sMinerUsername;
    private String sHash;



    /**
     * This computes the Merkle Root. It either accepts 2 or 4 items, or if made to be dynamic, then accepts any
     * multiple of 2 (2,4,8.16.32,etc.) items.
     * @param lstItems
     * @return
     */
    public synchronized String computeMerkleRoot(ArrayList<String> lstItems) {
        //made this object so I didn't keep creating multiple objects when hashing below
        BlockchainUtil oUtil = new BlockchainUtil();

        //this array list was used so that I could continue up the tree for any number of items in the array list such as 64
        ArrayList<String> newList = new ArrayList<>();

        //this variable is used for the while loop to sort through the entire array list
        int i = 0;

        //this variable is used for the flow control to make sure only array lists that contained a power of 2 got through
        int placeholder = 1;

        //had to declare the nodes out here so that I could access them throughout the entire method
        MerkleNode oNode1 = new MerkleNode();
        MerkleNode oNode2 = new MerkleNode();
        MerkleNode oNode3 = new MerkleNode();

        //for and if statement used to check if a power of 2 list was submitted
        for (int j = (lstItems.size() / 2); j > 0; j--)
        {
            placeholder = placeholder * 2;
            if (placeholder == lstItems.size())
            {
                //while loop goes through all transactions submitted in the list
                while (i+1 < lstItems.size())
                {



                    oNode1.sHash = oUtil.generateHash(lstItems.get(i));
                    oNode2.sHash = oUtil.generateHash(lstItems.get(i + 1));

                    //this code in the next line seems weird, but I needed to just do this so that I didn't hash twice later on when it recurred
                    oNode3.sHash = oNode1.sHash + oNode2.sHash;

                    newList.add(oNode3.sHash);

                    i += 2;

                }
                //checks to see if the merkle root was found or if it needs to redo the method to continue up the tree
                if(newList.size() > 1)
                {
                    //recurs the list so that I can go up the merkle tree
                    return(computeMerkleRoot(newList));
                }
                else {
                    //I populate here because in any other case of hashing, it is done in the while, but here I am required to hash the final merkle root.
                    populateMerkleNode(oNode3,oNode1,oNode2);
                    newList.add(oNode3.sHash);

                    //I get the index 1 and not 0 because if you look above at line 61, I add there as well which is actually not a hash at all.
                    return newList.get(1);
                }

            }

        }
        //if a block was not a power of two, then this happens
        return "Did not enter a block that was a power of 2";


    }



    /**
     * This method populates a Merkle node's left, right, and hash variables.
     * @param oNode
     * @param oLeftNode
     * @param oRightNode
     */
    private void populateMerkleNode(MerkleNode oNode, MerkleNode oLeftNode, MerkleNode oRightNode){

        oNode.oLeft = oLeftNode;
        oNode.oRight = oRightNode;
        oNode.sHash = new BlockchainUtil().generateHash(oNode.oLeft.sHash + oNode.oRight.sHash);
    }


    // Hash this block, and hash will also be next block's previous hash.

    /**
     * This generates the hash for this block by combining the properties and hashing them.
     * @return
     */
    public String computeHash() {

        return new BlockchainUtil().generateHash(sMerkleRoot + iDifficulty + sMinerUsername + sNonce);
    }



    public int getDifficulty() {
        return iDifficulty;
    }
    public void setDifficulty(int iDifficulty) {
		this.iDifficulty = iDifficulty;
	}


    public String getNonce() {
        return sNonce;
    }
    public void setNonce(String nonce) {
        this.sNonce = nonce;
    }

    public void setMinerUsername(String sMinerUsername) {
        this.sMinerUsername = sMinerUsername;
    }

    public String getHash() { return sHash; }
    public void setHash(String h) {
        this.sHash = h;
    }

    public synchronized void setMerkleRoot(String merkleRoot) { this.sMerkleRoot = merkleRoot; }




    /**
     * Run this to test your merkle tree logic.
     * @param args
     */
    public static void main(String[] args){

        ArrayList<String> lstItems = new ArrayList<>();
        Block oBlock = new Block();
        String sMerkleRoot;

        // These merkle root hashes based on "t1","t2" for two items, and then "t3","t4" added for four items.
        String sExpectedMerkleRoot_2Items = "3269f5f93615478d3d2b4a32023126ff1bf47ebc54c2c96651d2ac72e1c5e235";
        String sExpectedMerkleRoot_4Items = "e08f7b0331197112ff8aa7acdb4ecab1cfb9497cbfb84fb6d54f1cfdb0579d69";

        lstItems.add("t1");
        lstItems.add("t2");


        // *** BEGIN TEST 2 ITEMS ***

        sMerkleRoot = oBlock.computeMerkleRoot(lstItems);

        if(sMerkleRoot.equals(sExpectedMerkleRoot_2Items)){

            System.out.println("Merkle root method for 2 items worked!");
        }

        else{
            System.out.println("Merkle root method for 2 items failed!");
            System.out.println("Expected: " + sExpectedMerkleRoot_2Items);
            System.out.println("Received: " + sMerkleRoot);

        }


        // *** BEGIN TEST 4 ITEMS ***

        lstItems.add("t3");
        lstItems.add("t4");
        sMerkleRoot = oBlock.computeMerkleRoot(lstItems);

        if(sMerkleRoot.equals(sExpectedMerkleRoot_4Items)){

            System.out.println("Merkle root method for 4 items worked!");
        }

        else{
            System.out.println("Merkle root method for 4 items failed!");
            System.out.println("Expected: " + sExpectedMerkleRoot_4Items);
            System.out.println("Received: " + sMerkleRoot);

        }
    }
}
