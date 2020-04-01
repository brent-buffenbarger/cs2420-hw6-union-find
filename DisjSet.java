public class DisjSet {

	private int[] set;

	/**
	 *. Default constructor
	 */
	public DisjSet(int elements) {
		// initialize the set array with 'elements' number of indices
		set = new int[elements];
		// fill each index in 'set' with -1 because each index will start as a root
		for(int i = 0; i < elements; i++)
			set[i] = -1;
	}

	/**
	 *. Method to union two sets together (i.e. find the root of the smaller set and
	 *  merge it with the root of the larger set)
	 * 
	 */
	public void union(int root1, int root2) {
		//Check to see which root is deeper
		if(set[root1] > set[root2]) {           // root2 is deeper in this case since depth of roots is represented as a  negative
			set[root1] = root2;             // since root1 is deeper, we will assign the root of root1 as `root2`
		} else { 			        
			if(set[root1] == set[root2]) {  // root1 and root2 are of the same depth
				set[root2] -= 1;        // we will decrement
			}
			set[root1] = root2;		// since root2 is deeper (or equal to root1), we will assign the root of root2 as 'root1'
		}
	}
	
	/**
	 *.  Method to find and return the root of given value
	 */
	public int find(int index) {
		if(set[index] < 0) {	// If set[index] is negative, we know we have found the root
			return index;
		}
		return set[index] = find(set[index]);
	}

	/**
	 *. Main method to test the Disjoint Set class
	*/
	public static void main(String[] args) throws Exception {
		int numElements = 155;
		DisjSet set = new DisjSet(numElements);
	
		// Initial simple test to make sure there are 'numElements' in the set all initialized to -1
		assertTest(set.set.length, numElements, "Expected " + numElements + " elements in 'set', found: " + set.set.length);
		for(int i = 0; i < set.set.length; i++)
			assertTest(set.set[i], -1, " Expected -1 but found: " + set.set[i] + " at index " + i + " in 'set'");
	
		
		// Test basic union with 67 and 50
		int index1 = 12;
		int index2 = 11;
		int findVal = set.find(index1);
		int expectedVal = index1; // First we should test to make sure index1 has a root value of itself because no unions have been done yet
		assertTest(findVal, expectedVal, "Expected " +  expectedVal + "  as parent of " + index1 + ". Found: " + findVal);
		
		set.union(set.find(index1), set.find(index2));
		expectedVal = index2; // Since 12 and 11 are initially equal, and 11 is in place of root2, the root of root1(12) should be root2 (11)
		assertTest(set.find(index1), expectedVal, "Expected the parent of " + index1 + " to be " + expectedVal + " but found " + set.find(index1));
		// Now let's make sure the new root updated its size properly (should now be -2)
		assertTest(set.set[expectedVal], -2, "Expected the size of " + expectedVal + " to be -2, but found " + set.set[expectedVal]);

		// Test basic union with 1 and 2
		index1 = 1;
		index2 = 2;
		
		set.union(set.find(index1), set.find(index2));
		expectedVal = index2; // Make sure 2 is the root of 1 in this case
		assertTest(set.find(index1), expectedVal, "Expected the parent of " + index1 + " to be " + expectedVal + " but found " + set.find(index1));
		// Make sure the new root updated its size (should now be -2)
		assertTest(set.set[expectedVal], -2, "Expected the size of " + expectedVal + " to be -2, but found " + set.set[expectedVal]);
		

		// Test union on non-root values 1 and 12
		index1 = 12; // Should have a root of 11 right now based on previous test
		index2 = 1;  // Should have a root of 2 right now based on previous test

		set.union(set.find(index1), set.find(index2));
		expectedVal = set.find(index2); // The new root should be the root of 2
		assertTest(set.find(index1), expectedVal, "Expected the root of " + index1 + " to be " + expectedVal + " but found " + set.find(index1));

		// Creating a new set to make everything the root again
		numElements = 15;
		set = new DisjSet(numElements);

		// Test to make sure that 0 is the root  if every thing is joined 1 by 1 (1U0, 2U1, 3U2...set.length-1 U set.length-2)
		for(int i = 1; i < set.set.length; i++) {
			set.union(set.find(i), set.find(i - 1));
			assertTest(set.find(i), 0, "Expected the root of " + i  + " to be 0 but found " + set.find(i)); 
		}

		set = new DisjSet(numElements);
		// Test to make sure that i is the root of i - 1 if every thing is joined 1 by 1 (0U1, 1U2, 2U3...set.length-2 U set.length-1)
		for(int i = 1; i < set.set.length; i++) {
			set.union(set.find(i - 1), set.find(i));
			assertTest(set.find(i - 1), i, "Expected the root of " + (i - 1) + " to be 0 but found " + set.find(i - 1)); 
		}
	}

	/**
	 *. Helper test method to throw an error if something goes wrong
	 */
	private static void assertTest(int expr1, int expr2, String  errorMsg) throws Exception {
		if(expr1 != expr2) throw new Exception(errorMsg);
	}

}
