package edu.cnm.deepdive.craps.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This is a simulator for a game of craps.
 */
public class Game {

  private final Object lock = new Object();

  private State state;
  private int point;
  private Random rng;
  private List<Roll> rolls;
  private int wins;
  private int losses;

  /**
   * Starts game with 0 wins or losses
   *
   */
  public Game(Random rng) {
    this.rng = rng;
    rolls = new LinkedList<>();
    wins = 0;
    losses = 0;
  }

  /**
   * resets game to come out state
   */
  public void reset() {
    state = State.COME_OUT;
    point = 0;
    synchronized (lock) {
      rolls.clear();
    }
  }

  private State roll() {
    int[] dice = {
        1 + rng.nextInt(6),
        1 + rng.nextInt(6)
    };
    int total = dice[0] + dice[1];
    State state = this.state.roll(total, point);
    if (this.state == State.COME_OUT && state == State.POINT) {
      point = total;
    }
    this.state = state;
    synchronized (lock) {
      rolls.add(new Roll(dice, state));
    }
    return state;
  }

  /**
   *  In the point state, this calls the roll method.
   *  If the point is rolled then the win tally will increment,
   *  otherwise the losses tally will increment.
   */
  public State play() {
    reset();
    while (state != State.WIN && state != State.LOSS) {
      roll();
    }
    if (state == State.WIN) {
      wins++;
    } else {
      losses++;
    }
    return state;
  }

  /**
   *
   * @return state
   */
  public State getState() {
    return state;
  }

  public List<Roll> getRolls() {
    synchronized (lock) {
      return new LinkedList<>(rolls);
    }
  }

  /**
   *
   * @return wins
   */
  public int getWins() {
    return wins;
  }

  /**
   *
   * @return losses
   */
  public int getLosses() {
    return losses;
  }

  /**
   * Creates an array of two dice with random values
   */
  public static class Roll {

    private final int[] dice;
    private final State state;

    private Roll(int[] dice, State state) {
      this.dice = Arrays.copyOf(dice, 2);
      this.state = state;
    }

    /**
     *
     * @return copy of array of dice length 2
     */
    public int[] getDice() {
      return Arrays.copyOf(dice, 2);
    }

    /**
     *
     * @return state
     */
    public State getState() {
      return state;
    }

    @Override
    public String toString() {
      return String.format("%s %s%n", Arrays.toString(dice), state);
    }

  }

  public enum State {

    COME_OUT {
      @Override
      public State roll(int total, int point) {
        switch (total) {
          case 2:
          case 3:
          case 12:
            return LOSS;
          case 7:
          case 11:
            return WIN;
          default:
            return POINT;
        }
      }
    },
    WIN,
    LOSS,
    POINT {
      @Override
      public State roll(int total, int point) {
        if (total == point) {
          return WIN;
        } else if (total == 7) {
          return LOSS;
        } else {
          return POINT;
        }
      }
    };

    public State roll(int total, int point) {
      return this;
    }

  }

}
