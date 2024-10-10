package edu.iastate.cs472.proj1;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Aren Ashlock
 */

/**
 * This class describes a circular doubly-linked list of states to represent both the OPEN and CLOSED lists
 * used by the A* algorithm.  The states on the list are sorted in the  
 *     a) order of non-decreasing cost estimate for the state if the list is OPEN, or 
 *     b) lexicographic order of the state if the list is CLOSED.  
 */
public class OrderedStateList {
	/**
	 * Implementation of a circular doubly-linked list with a dummy head node.
	 */
	private State head;           // dummy node as the head of the sorted linked list 
	private int size = 0;
	private boolean isOPEN;       // true if this OrderedStateList object is the list OPEN and false if the list CLOSED.

	/**
	 *  Default constructor constructs an empty list. Initialize heuristic. Set the fields next and previous of head to the 
	 *  node itself. Initialize instance variables size and heuristic. 
	 * 
	 * @param h 
	 * @param isOpen   
	 */
	public OrderedStateList(Heuristic h, boolean isOpen) {
		State.heu = h;   // initialize heuristic used for evaluating all State objects. 

		head = new State();
		head.next = head;
		head.previous = head;
		size = 0;
		isOPEN = isOpen;
	}
	  

	public int size() {
		return size; 
	}
	  

	/**
	 * A new state is added to the sorted list.  Traverse the list starting at head. Stop right before the first state t such 
	 * that compareStates(s, t) <= 0, and add s before t. If no such state exists, simply add s to the end of the list. 
	 * 
	 * Precondition: s does not appear on the sorted list. 
	 * 
	 * @param s
	 */
	public void addState(State s) {
		State currState = head.next;
		
		// As long as the current state s is being compared to is not the head, it may be inserted somewhere internally in the list
		while(!currState.equals(head)) {
			if(compareStates(s, currState) <= 0) {
				// Add in the new state
				s.previous = currState.previous;
				s.next = currState;

				// Update the previous and next state pointers
				s.previous.next = s;
				currState.previous = s;

				// Exit the loop since it has been inserted
				break;
			}
			else {
				currState = currState.next;
			}
		}

		// Reached the head node again, so t does not exist -> add to the end of the list
		if(currState.equals(head)) {
			// Add in the new state
			s.previous = head.previous;
			s.next = head;

			// Update the previous and next state pointers
			s.previous.next = s;
			head.previous = s;
		}

		// Increase the size of the list
		size++;
	}  
	  

	/**
	 * Conduct a sequential search on the list for a state that has the same board configuration as the argument state s.  
	 * 
	 * Calls equals() from the State class. 
	 * 
	 * @param s
	 * @return the state on the list if found
	 *         null if not found 
	 */
	public State findState(State s) {
		State currState = head.next;

		// As long as we have not reached the head, there are still states in the list to search
		while(!currState.equals(head)) {
			if(s.equals(currState)) {
				return currState;
			}
			else {
				currState = currState.next;
			}
		}

		return null; 
	}
	  

	/**
	 * Remove the argument state s from the list.  It is used by the A* algorithm in maintaining both the OPEN and CLOSED lists. 
	 * 
	 * @param s
	 * @throws IllegalStateException if s is not on the list 
	 */
	public void removeState(State s) throws IllegalStateException {
		State currState = head.next;

		// As long as we have not reached the head, there are still states in the list to search
		while(!currState.equals(head)) {
			if(compareStates(currState, s) == 0) {
				// Update the previous and next state pointers
				currState.previous.next = currState.next;
				currState.next.previous = currState.previous;
				
				// Remove the state's links
				currState.previous = null;
				currState.next = null;

				// Reduce the size of the list
				size--;

				// Exit the loop since it has been inserted
				break;
			}
			else {
				currState = currState.next;
			}
		}

		if(currState.equals(head)) {
			throw new IllegalStateException("State is not in the list");
		}
	}
	   

	/**
	 * Remove the first state on the list and return it.  This is used by the A* algorithm in maintaining the OPEN list. 
	 * 
	 * @return  
	 */
	public State remove() {
		State removeState = head.next;

		head.next = removeState.next;
		removeState.next.previous = head;

		removeState.next = null;
		removeState.previous = null;

		size--;

		return removeState; 
	}
	  

	/**
	 * Compare two states depending on whether this OrderedStateList object is the list OPEN or the list CLOSE used by the 
	 * A* algorithm. More specifically,  
	 *     a) call the method compareTo() of the State if isOPEN == true, or 
	 *     b) create a StateComparator object to call its compare() method if isOPEN == false. 
	 * 
	 * @param s1
	 * @param s2
	 * @return -1 if s1 is less than s2 as determined by the corresponding comparison method
	 *         0  if they are equal 
	 *         1  if s1 is greater than s2
	 */
	private int compareStates(State s1, State s2) {
		if(isOPEN) {
			return s1.compareTo(s2);
		}
		else {
			StateComparator closedCompare = new StateComparator();
			return closedCompare.compare(s1, s2);
		}
	}
}