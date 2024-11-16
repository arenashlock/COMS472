package edu.iastate.cs472.proj2;

/**
 * @author Aren Ashlock
 */

/**
 * Node type for the Monte Carlo search tree.
 */
public class MCNode {
  // TODO - Done
  // Tree properties
  private MCNode parent;
  private MCNode[] children;

  // Monte Carlo traversal stats
  private double wins;
  private double playouts;

  // State properties
  private CheckersData state;
  private int player;

  // ---------- constructors ----------

  // Constructor for the root
  MCNode(CheckersData state, int player) {
    // Set the parent
    this.parent = null;

    // Set the new state
    this.state = state;

    // Set the current player
    this.player = player;
  }

  MCNode(MCNode parent, CheckersData state, int player) {
    // Set the parent
    this.parent = parent;

    // Set the new state
    this.state = state;

    // Set the current player
    this.player = player;
  }

  // ---------- getters ----------

  public MCNode parent() {
    return parent;
  }

  public MCNode[] children() {
    return children;
  }

  public double wins() {
    return wins;
  }

  public double playouts() {
    return playouts;
  }

  public CheckersData state() {
    return state;
  }

  public int player() {
    return player;
  }

  // ---------- "setters" ----------

  public void children(MCNode[] children) {
    this.children = children;
  }

  public void win() {
    wins += 1.0;
    playouts += 1.0;
  }

  public void draw() {
    wins += 0.5;
    playouts += 1.0;
  }

  public void loss() {
    playouts += 1.0;
  }
}