package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode,
 * with fields for tag/text, first child and sibling.
 * 
 */
public class Tree {

	/**
	 * Root node
	 */
	TagNode root = null;

	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;

	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}

	/**
	 * Builds the DOM tree from input HTML file, through scanner passed in to the
	 * constructor and stored in the sc field of this object.
	 * 
	 * The root of the tree that is built is referenced by the root field of this
	 * object.
	 */
	public void build() {
		/** COMPLETE THIS METHOD **/
		String html = "";
		// while (sc.hasNext()) {
		// html += sc.nextLine();
		// }

		root = buildForReal(sc.nextLine());
	}

	private TagNode buildForReal(String currentTag) {
		TagNode ptr = null;
		if (currentTag == null)
			return ptr;
		if (currentTag.equals(""))
			return buildForReal(sc.nextLine());
		switch (tagType(currentTag)) {
		case 0:
			ptr = new TagNode(currentTag.substring(1, currentTag.length() - 1), null, null);
			ptr.firstChild = buildForReal(sc.nextLine());
			if (!sc.hasNext())
				return ptr;
			ptr.sibling = buildForReal(sc.nextLine());
			break;
		case 1:
			return null;
		case 2:
			ptr = new TagNode(currentTag, null, null);
			ptr.sibling = buildForReal(sc.nextLine());
		}
		return ptr;
	}

	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		replaceTagForReal(oldTag, newTag, root);

	}

	private void replaceTagForReal(String oldTag, String newTag, TagNode ptr) {
		if (ptr == null) {
			return;
		}

		if (ptr.tag.equals(oldTag)) {
			ptr.tag = newTag;
		}
		replaceTagForReal(oldTag, newTag, ptr.firstChild);
		replaceTagForReal(oldTag, newTag, ptr.sibling);
	}

	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The
	 * boldface (b) tag appears directly under the td tag of every column of this
	 * row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		TagNode actualRoot = root;
		findTag(root.firstChild.firstChild, "table");
		int i = 1;
		if (root != null) {
			TagNode ptr = root.firstChild;
			// get correct tr tag
			while (i < row) {
				// System.out.println(ptr);
				ptr = ptr.sibling;
				i++;
			}

			if (ptr.firstChild != null) {
				ptr = ptr.firstChild;

				while (ptr != null) {
					if (ptr.tag.equals("td")) {
						TagNode t = new TagNode("b", ptr.firstChild, null);
						ptr.firstChild = t;
					}
					ptr = ptr.sibling;
				}
			}
		}

		root = actualRoot;

	}

	private void findTag(TagNode ptr, String tag) {
		if (ptr == null) {
			return;
		}
		if (ptr.tag.equals(tag)) {
			// System.out.println(ptr);
			root = ptr;
			;

		}
		findTag(ptr.firstChild, tag);
		findTag(ptr.sibling, tag);

	}

	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b,
	 * all occurrences of the tag are removed. If the tag is ol or ul, then All
	 * occurrences of such a tag are removed from the tree, and, in addition, all
	 * the li tags immediately under the removed tag are converted to p tags.
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/

		TagNode actualRoot = root;

		int i = 0;

		do {
			searchParent(actualRoot, tag);

			if (root.firstChild != null) {
				if (root.firstChild.tag.equals(tag)) {
					TagNode targetTag = root.firstChild;
					if (tag.equals("ul") || tag.equals("ol")) {
						replaceLi(targetTag, 0);
						// SPACE FOR OL AND UL

					}
					if (tag.equals("p") || tag.equals("b") || tag.equals("em") || tag.equals("ul")
							|| tag.equals("ol")) {

						TagNode lastChild = targetTag.firstChild;
						while (lastChild.sibling != null) {
							lastChild = lastChild.sibling;
						}

						root.firstChild = targetTag.firstChild;
						lastChild.sibling = targetTag.sibling;
					}
				}
			}

			if (root.sibling != null) {
				if (root.sibling.tag.equals(tag)) {
					TagNode targetTag = root.sibling;
					if (tag.equals("ul") || tag.equals("ol")) {
						replaceLi(targetTag, 0);
					}

					if (tag.equals("p") || tag.equals("b") || tag.equals("em") || tag.equals("ul")
							|| tag.equals("ol")) {

						TagNode lastChild = targetTag.firstChild;
						while (lastChild.sibling != null) {
							lastChild = lastChild.sibling;
						}
						
						root.sibling = targetTag.firstChild;
						lastChild.sibling = targetTag.sibling;

					}
				}
			}
			i++;
			// print();
			// System.out.println("ROOT: " + root);
			// System.out.println("---------------------------");
		} while (i < 1000);

		root = actualRoot;
		print();

	}

	private void replaceLi(TagNode node, int count) {
		System.out.println(node + "  " + count);

		if (node == null)
			return;

		if (node.tag.equals("li")) {
			node.tag = "p";
		}
		count++;
		replaceLi(node.firstChild, count);
		// System.out.println(node);
		if (count > 1) {
			replaceLi(node.sibling, count);
		}
	}

	private void searchParent(TagNode node, String s) {
		if (node == null)
			return;

		if (node.firstChild != null) {
			if (node.firstChild.tag.equals(s)) {
				root = node;
				return;
			}
		}
		if (node.sibling != null) {
			if (node.sibling.tag.equals(s)) {
				root = node;
				return;
			}
		}
		searchParent(node.firstChild, s);
		// System.out.println(node);
		searchParent(node.sibling, s);
	}

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag  Tag to be added
	 */
	public void addTag(String word, String tag) {
		/** COMPLETE THIS METHOD **/
		TagNode actualRoot = root;
		searchWord(root,tag);
		
		
		root=actualRoot;
	}

	private void searchWord(TagNode node, String s) {
		if (node == null)
			return;

		String nodeTagLowercase = node.tag.toLowerCase();
		String[] punctuations = { ".", ",", ":", ";", "?", "!" };
		String[] words = s.toLowerCase().split("\\s+");

		for (String word : words) {
			if (word.contains(nodeTagLowercase)) {
				if (word.length() == nodeTagLowercase.length()) {
					
				}
				for (String p : punctuations) {
					if (word.substring(word.length() - 1).equals(p)) {
						
					}
				}
			}
		}

		searchParent(node.firstChild, s);
		// System.out.println(node);
		searchParent(node.sibling, s);
	}

	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes new
	 * lines, so that when it is printed, it will be identical to the input file
	 * from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines.
	 */

	/*
	 * 0=open 1=close 2=text/other
	 */

	private int tagType(String s) {
		if (s.length() >= 2) {
			if (s.charAt(0) == '<') {
				if (s.charAt(1) == '/') {
					return 1;// close
				}
				return 0;// open
			}
		}
		return 2;// text
	}

	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}

	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");
			}
		}
	}

	/**
	 * Prints the DOM tree.
	 *
	 */
	public void print() {
		print(root, 1);
	}

	private void print(TagNode root, int level) {
		for (TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
			for (int i = 0; i < level - 1; i++) {
				System.out.print("      ");
			}
			;
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level + 1);
			}
		}
	}
}
