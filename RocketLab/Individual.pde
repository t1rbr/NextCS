public static final int CHROMOSOME_LENGTH = 600; //length of chromosome
public static final int ACTIONS_LENGTH = 300; //length of actions, first [ACTIONS_LENGTH] genes of chromosome are magnitude, next [ACTIONS_LENGTH] genes are angle

public enum GeneLengths {
  //magnitude comes in increments of 1/150, up to 0.1
  //angle should be be in increments of (2 * PI)/511, up to 2 PI
  MAGNITUDE(4),
    ANGLE(9);

  private int length;

  private GeneLengths(int length) {
    this.length = length;
  }

  private int getLength() {
    return length;
  }
}

public class Individual {
  private Gene[] chromosome;
  private Rocket rocket;
  private float fitness = 0;

  public Individual(boolean random) {
    chromosome = new Gene[CHROMOSOME_LENGTH];
    for (int i = 0; i < ACTIONS_LENGTH; i++) {
      chromosome[i] = new Gene(GeneLengths.MAGNITUDE.getLength());
      chromosome[i + ACTIONS_LENGTH] = new Gene(GeneLengths.ANGLE.getLength());
    }
    if (random) for (Gene g : chromosome) g.randomize();
    else setRocket();
  }

  public Individual() {
    this(false);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Gene g : chromosome) {
      sb.append(g.toString() + "|");
    }
    return sb.toString();
  }

  public void display(boolean showFitness, boolean highlight) {
    rocket.display(highlight);
    fill(0);
    if (showFitness) text(fitness, rocket.position.x, rocket.position.y);
  }

  public float getX() {
    return rocket.position.x;
  }

  public float getY() {
    return rocket.position.y;
  }

  public float getFitness() {
    return fitness;
  }

  public Gene[] getChromosome() {
    return chromosome;
  }
  
  public float getRocketSize(){
    return rocket.r;
  }

  public void setRocket() {
    PVector[] angles = new PVector[ACTIONS_LENGTH];
    float[] mags = new float[ACTIONS_LENGTH];
    for (int i = ACTIONS_LENGTH; i < 2 * ACTIONS_LENGTH; i++) {
      float theta = float(chromosome[i].getValue()) * (2 * PI) / 511f;
      angles[i - ACTIONS_LENGTH] = new PVector(cos(theta), sin(theta));
      mags[i - ACTIONS_LENGTH] = float(chromosome[i - ACTIONS_LENGTH].getValue()) / (2*150f);
    }
    rocket = new Rocket(angles, mags, ACTIONS_LENGTH);
  }

  public void resetRocket() {
    rocket.reset();
    setRocket();
  }

  public void moveRocket() {
    rocket.run();
  }

  public void mutate(float rate) {
    for (Gene g : chromosome) if (random(1) < rate) g.mutate();
  }

  public Individual crossover(Individual partner) {
    Individual child = new Individual(true);
    for (int i = 0; i < CHROMOSOME_LENGTH; i++) {
      if (random(1) < 0.5) child.chromosome[i] = new Gene(chromosome[i]);
      else child.chromosome[i] = new Gene(partner.chromosome[i]);
    }
    child.setRocket();
    return child;
  }

  public void updateFitness(int x, int y) { //update fitness based on the center of the goal
    //update fitness based on distance from x, y
    fitness = pow(max(0, 1 - (dist(x, y, rocket.position.x, rocket.position.y) / max(max(dist(width, 0, goalX, goalY), dist(width, height, goalX, goalY)), max(dist(0, 0, goalX, goalY), dist(0, height, goalX, goalY))))), 3);
    //fitness = 1 / dist(x, y, getX(), getY());
  }
}
