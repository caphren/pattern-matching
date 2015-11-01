import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;


public class CompressedSuffixTrie {
	private TrieNode root;
	private String str;
	
	/** Constructor 
	 * Running time analysis:
	 * openFile: O(n) -see analysis below; n is length of file/sequence/string
	 * addSubstring O(s) -see analysis below; s is length of substring
	 * 		s is sum(1, 2, ..., n-1, n)/n = n(1+n)/2n = (1+n)/2 => O(n)
	 * 		Note: this method is called n times, so in this constructor
            it has running time O(sn) => O(n^2)
	 * compress O(n) -see analysis below; n is size of subtree, which is proportional to length of sequence
	 * 
	 * Combining the above gives O(n + n^2 + n) => O(2n + n^2) => O(n^2)
	 * 
	 */
	public CompressedSuffixTrie(String f) {
		// part 1: Create a compressed suffix trie from file f -run time O(n)
		str = openFile(f);
		str = str.concat("$");
		
		root = new TrieNode();
		
		String sub = str;
		int index = 0;
		while (index <= (sub.length() - 1)) {
			root.addSubstring(sub.substring(index), index, 0, root);  // this is O(s) and runs n times
			index++;
		}
		
		//Compress the trie
		root.compress();  // run time O(n)
	}

	/**
	 * Method for finding the first occurrence of a pattern s in the DNA
	 * sequence
	 * Running time analysis:
	 * 	The method uses a single while loop to iterate through the characters
	 *  of the string s. Each comparison uses calls which execute in constant 
	 *  time, so the overall run time is O(|s|)
	 */
	public int findString(String s) {
		// part 2
		int ref = -1;
		boolean found = true;
		
		char c = s.charAt(0);
		char d;
		TrieNode current = root.getChild(c); // constant time O(5)
		
		int i = 0;
		int j = 0;
		
		while (found && (current != null) && (i < s.length())) { 
			// this loop repeats at most |s| times, where |s| is the length of the string s
			c = s.charAt(i);
			
			d = str.charAt(current.index + current.start + j);
			
			if (c == d) {
				ref = current.index;
				i++;
				if (i >= s.length()) {
					break;
				}
				j++;
				if ((current.end - current.start) <= j) {
					c = s.charAt(i);
					current = current.getChild(c); // constant time O(5)
					j = 0;
				}
			}
			else {
				found = false;
			}
		}
		
		if (!found) {
			return -1;
		}
		return ref;
	}

	/**
	 * Method for computing the degree of similarity of two DNA sequences stored
	 * in the text files f1 and f2
	 * Running time:
	 * This is a brute-force analysis, using two (nested) loops to iterate through all possible strings
	 * 
	 * The analysis is O(n) * O(m) for the two strings, coming to O(mn) 
	 */
	public static float similarityAnalyser(String f1, String f2, String f3) {
		// part 3: Brute force pattern match
		String seq1 = openFile(f1);
		String seq2 = openFile(f2);
		
		int length = 0;
		int max = 0;
		String maxSeq = "";
		String seq = "";
		
		
		if (seq1.length() < seq2.length()) { //make seq1 the longer input
			String temp = seq1;
			seq1 = seq2;
			seq2 = temp;
		}
		int length1 = seq1.length();
		int length2 = seq2.length();
		
		for (int i = 0; i < length1; i++) {			
			for (int j = 0; j < length2; j++) {
				if ((i + j) >= length1) {
					// save max, reset match length
					if (max < length) {
						max = length;
						maxSeq = seq;
					}
					length = 0;
					seq = "";
					break;
				}
				char a = seq1.charAt(i+j);
				char b = seq2.charAt(j);
				if (a == b) {
					length++;
					seq = seq + a;
				} else {
					// save max, reset match length
					if (max < length) {
						max = length;
						maxSeq = seq;
					}
					length = 0;
					seq = "";
				}
			}
		}
		
		float degree;
		degree = (float) max/length1;
		
		//write sequence to file f3
		PrintWriter writer;
		try {
			writer = new PrintWriter(f3, "UTF-8");
			writer.println(maxSeq);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return degree;
	}

	/**
	 * Class for the nodes in the CommpressedSuffixTrie 
	 */
	public class TrieNode {
		private TrieNode parent;
		/* Children (max 5 children, one for each letter plus sentinel "$") */
		private TrieNode n1;
		private TrieNode n2;
		private TrieNode n3;
		private TrieNode n4;
		private TrieNode n5;
		
		private int index;
		private int start;
		private int end;

		/* TrieNode constructor */
		public TrieNode() {
			parent = null;
			n1 = null; n2 = null; n3 = null; n4 = null; n5 = null;
			index = 0; start = 0; end = 0;
		}
		public TrieNode(int stringIndex, int startIndex, int endIndex) {
			parent = null;
			n1 = null; n2 = null; n3 = null; n4 = null; n5 = null;
			index = stringIndex;
			start = startIndex;
			end = endIndex;
		}
		/**
		 * Method to find a child node whose substring begins with the character c
		 * Running time: worst case O(5) constant
		 */
		private TrieNode getChild(char c) {
			//n1
			if (n1 != null) {
				char temp = str.charAt(n1.index + n1.start);
				if (c == temp)
					return n1;
			}
			//n2
			if (n2 != null) {
				char temp = str.charAt(n2.index + n2.start);
				if (c == temp)
					return n2;
			}
			//n3
			if (n3 != null) {
				char temp = str.charAt(n3.index + n3.start);
				if (c == temp)
					return n3;
			}
			//n4
			if (n4 != null) {
				char temp = str.charAt(n4.index + n4.start);
				if (c == temp)
					return n4;
			}
			//n5
			if (n5 != null) {
				char temp = str.charAt(n5.index + n5.start);
				if (c == temp)
					return n5;
			}
			//not found
			return null;
		}
		/**
		 * Method to add a new child node
		 * Running time: worst case O(5) constant
		 */
		private void addChild(TrieNode tmp) {
			if (n1 == null) {
				n1 = tmp;
				return;
			}
			if (n2 == null) {
				n2 = tmp;
				return;
			}
			if (n3 == null) {
				n3 = tmp;
				return;
			}
			if (n4 == null) {
				n4 = tmp;
				return;
			}
			if (n5 == null) {
				n5 = tmp;
				return;
			}
			
		}
		
		/**
		 * Method to remove a child for the purpose of updating the trie
		 * Running time: worse case O(5) constant
		 */
		private void removeChild(TrieNode child) {
			if (n1 == child) { 
				n1 = null;
			}
			if (n2 == child) { 
				n2 = null;
			}
			if (n3 == child) { 
				n3 = null;
			}
			if (n4 == child) { 
				n4 = null;
			}
			if (n5 == child) { 
				n5 = null;
			}
		}
		
		/**
		 * Method to add a substring to the CommpressedSuffixTrie
		 * (for use only by the constructor)
		 * Args: 
		 * 		String sub is the substring to be added
		 * 		int index is the index of the substring in the original string
		 * Running time analysis:
		 * 	This method adds or visits a node for each character in the substring
		 * 	Therefore for a substring of length s, it has a running time O(s) 
		 */
		private void addSubstring(String sub, int stringIndex, int subIndex, TrieNode current) {
			// add strings to the CompressedSuffixTrie
			char firstChar = sub.charAt(0);
			TrieNode child = current.getChild(firstChar);
			if (child == null) {
				TrieNode tmp = new TrieNode(stringIndex, subIndex, subIndex);
				tmp.parent = current;
				current.addChild(tmp);  // runs in constant time
				child = tmp;
			}
			if (sub.length() > 1) {
				String sub2 = sub.substring(1);
				current.addSubstring(sub2, stringIndex, subIndex + 1, child);  // recursion to length s
			}
			return;
		}
		
		/**
		 * Method to count the number of children on a node
		 * Running time: O(5) constant
		 */
		private int countChildren() {
			int count = 0;
			if (n1 != null) {
				count++;
			}
			if (n2 != null) {
				count++;
			}
			if (n3 != null) {
				count++;
			}
			if (n4 != null) {
				count++;
			}
			if (n5 != null) {
				count++;
			}
			return count;
		}
		
		/**
		 * Method for compressing the trie after constructing it in the compact form
		 * Running time: 
		 * In the worst case, the size of the trie is O(2n) 
		 * 	(for a Fibonacci word  for length n, which requires 2n nodes to represent all suffixes)
		 * This compression algorithm visits every node in the trie
		 * Therefore its run time is O(2n) => O(n) time
		 */
		private void compress() {
			// First trim from below
			if (n1 != null) {
				n1.compress();
			}
			if (n2 != null) {
				n2.compress();
			}
			if (n3 != null) {
				n3.compress();
			}
			if (n4 != null) {
				n4.compress();
			}
			if (n5 != null) {
				n5.compress();
			}
			
			// if this node only has one child, it can be trimmed
			if (this.countChildren() == 1) {
				TrieNode par = this.parent;
				TrieNode ch = n1;
				if (n2 != null) {
					ch = n2;
				}
				if (n3 != null) {
					ch = n3;
				}
				if (n4 != null) {
					ch = n4;
				}
				if (n5 != null) {
					ch = n5;
				}
				
				// replace this node in the parent
				ch.start = this.start;
				par.removeChild(this);  // constant time
				ch.parent = par;
				par.addChild(ch);  // constant time
			}
		}
	}

	/**
	 * Method for opening a file and reading the contents as a single string
	 * Running time analysis: Constant time O(n) for file of length n
	 */
	private static String openFile(String f) {
		String gene = "";
		try {
			File file = new File(f);
			Scanner scan = new Scanner(file);
			
			while (scan.hasNextLine()) {
				gene = gene.concat(scan.nextLine().trim());
			}
			scan.close();
		} catch (FileNotFoundException e) {
			// file not available
			e.printStackTrace();
		}
		return gene;
	}
    
	public static void main(String args[]) throws Exception {

		/** Construct a trie named trie1 */
		CompressedSuffixTrie trie1 = new CompressedSuffixTrie("file1");

		System.out.println("AAC is at: "
				+ trie1.findString("AAC"));
		
		System.out.println("ACTTCGTAAG is at: "
				+ trie1.findString("ACTTCGTAAG"));

		System.out.println("AAAACAACTTCG is at: "
				+ trie1.findString("AAAACAACTTCG"));

		System.out.println("ACTTCGTAAGGTT : "
				+ trie1.findString("ACTTCGTAAGGTT"));

		System.out.println(CompressedSuffixTrie.similarityAnalyser("file2",
				"file3", "file4"));
	}

}
