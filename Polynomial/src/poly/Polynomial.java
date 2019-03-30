package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		
		if(poly1==null&&poly2==null) {
			return null;
		}
		if(poly1==null) {
			return poly2;
		}
		if(poly2==null) {
			return poly1;
		}
		
		Node r = new Node(0,0,null);
		
		Node head=r;
		
		while(poly1!=null&&poly2!=null) {
			
			if(poly1.term.degree<poly2.term.degree) {
				r.next=new Node (poly1.term.coeff,poly1.term.degree,null);
				poly1=poly1.next;
				r=r.next;
				
			}
			
			else if(poly1.term.degree>poly2.term.degree) {
				r.next=new Node (poly2.term.coeff,poly2.term.degree,null);
				poly2=poly2.next;
				r=r.next;

			}
			
			else if(poly1.term.degree==poly2.term.degree) {
				
				r.next=new Node (poly1.term.coeff+poly2.term.coeff,poly1.term.degree,null);
				
				poly1=poly1.next;
				poly2=poly2.next;
				r=r.next;
			}
		
		}
		
		while(poly1!=null) {
			r.next=new Node (poly1.term.coeff,poly1.term.degree,null);
			poly1=poly1.next;
			r=r.next;
		}
	
		while(poly2!=null) {
		r.next=new Node (poly2.term.coeff,poly2.term.degree,null);
		poly2=poly2.next;
		r=r.next;
		}
		
		Node ptr=head;
		Node prev=null;
		
		//remove all 0's, except the first
		while(ptr!=null) {
			
			if(ptr.term.coeff==0&&prev!=null) {
				prev.next=ptr.next;
			}
			
			
			prev=ptr;
			ptr=ptr.next;
		}
		
		return head.next;
	
	}


	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		
		if(poly1==null||poly2==null) {
			return null;
		}
		
		Node r=new Node(Integer.MIN_VALUE,Integer.MIN_VALUE,null);
		Node head=r;		
		
		for(Node ptr1=poly1;ptr1!=null;ptr1=ptr1.next){
			for(Node ptr2=poly2;ptr2!=null;ptr2=ptr2.next){
				Node multiply=new Node (ptr1.term.coeff*ptr2.term.coeff,ptr2.term.degree+ptr1.term.degree,null);
				
				
				
				r=head;
				boolean added=false;
				while(r!=null) {
					
					if(multiply.term.coeff==0) {
						added=true;
						continue;
					}
					if(r.term.degree==multiply.term.degree) {
						
						if(r.term.coeff+multiply.term.coeff==0) {
							//remove node if 0
						Node temp =head;
						Node prev=null;
						boolean finished=false;
						if(temp!=null&&temp.term.degree==multiply.term.degree) {
							head=temp.next;
							finished=true;
						}
						
						while(finished==false&&temp!=null&&temp.term.degree!=multiply.term.degree) {
							prev=temp;
							temp=temp.next;
						}
						prev.next=temp.next;
						
						//end of remove code
						}
						else {
							r.term.coeff+=multiply.term.coeff;
						}
						added=true;
					}
					r=r.next;
				}
				//add to the end, can't add to existing term
				if(!added) {
					r=head;
					Node prev=null;
					while(r!=null) {
						
						
						if(r.term.degree<multiply.term.degree&&prev!=null) {
							
							prev.next=new Node(multiply.term.coeff,multiply.term.degree,r);
							break;
						}
						
						if(r.next==null) {
							
							r.next=multiply;
							break;
							
						}
						
						prev=r;
						r=r.next;
					}
				}
			}
		}
		//reverse list
		Node prev = null; 
        Node current = head.next; 
        Node next = null; 
        while (current != null) { 
            next = current.next; 
            current.next = prev; 
            prev = current; 
            current = next; 
        } 
        head = prev; 
		
		return head;
	}
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE THIS METHOD COMPILE
		// CHANGE IT AS NEEDED FOR YOUR IMPLEMENTATION
		
		if(poly==null) {
			return 0;
		}
		
		float evaluation=0;
		
		while(poly!=null) {
			evaluation+=Math.pow(x, poly.term.degree)*poly.term.coeff;
			poly=poly.next;
		}
		
		return evaluation;
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}
	
}