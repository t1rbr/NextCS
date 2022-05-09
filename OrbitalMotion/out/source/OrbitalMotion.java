/* autogenerated by Processing revision 1282 on 2022-05-09 */
import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class OrbitalMotion extends PApplet {

final float GRAVITY = 0.1f;
OrbList orbs;
PVector g;
boolean moving, gravity;

 public void setup() {
  /* size commented out by preprocessor */;
  g = new PVector(0, GRAVITY);
  println("==========================================================================================");
  println("SPACE to enable/disable movement, H to apply force, G to toggle gravity, and R to reset");
  println("==========================================================================================");
  reset();
}

 public void reset() {
  moving = false;
  gravity = true;
  int x = width / 2 - 300;
  int y = 100;
  orbs = new OrbList();
  orbs.append(x, y, true);
  x += OrbNode.SPRING_LENGTH;
  for (int i = 1; i < 20; i++) {
    orbs.append(x, y, false);
    x += OrbNode.SPRING_LENGTH;
    if(i % 5 == 0){
      orbs.append(x, y, true);
      x+= OrbNode.SPRING_LENGTH;
    }
  }
  orbs.append(x, y, true);
}


 public void draw() {
  background(255);
  fill(255);
  strokeWeight(1);
  if (moving) {
    runAStep();
  }
  strokeWeight(1);
  fill(255, 0, 0, 100);
  orbs.display();

  fill(0);
  text("Movement: " + (moving ? "ENABLED" : "DISABLED"), 10, 20);
  text("Gravity: " + (gravity ? "ENABLED" : "DISABLED"), 10, 30);
}

 public void runAStep() {
  orbs.applySprings();
  if (gravity) orbs.applyForce(g);
  orbs.run();
}

 public void keyPressed() {
  if (key == ' ') {
    moving = !moving;
  }

  if (key == 'h') {
    moving = true;
    orbs.applyForce(new PVector((mouseX - orbs.getPosOrb(orbs.getLength() / 2).x) * 0.01f, (mouseY - orbs.getPosOrb(orbs.getLength() / 2).y) * 0.01f));
  }

  if (key == 'g') {
    gravity = !gravity;
  }

  if (key == 'r') {
    reset();
  }
}
class FixedOrbNode extends OrbNode {
  public FixedOrbNode(int x, int y) {
    super(x, y);
  }

  @Override public 
    void run() {
  }
}
class OrbList {
  private OrbNode startOrb, endOrb;
  private int length;

  public OrbList() {
    this(null);
    length = 0;
  }

  public OrbList(OrbNode startOrb) {
    this.startOrb = startOrb;
    endOrb = startOrb;
    length = 1;
  }

  public OrbList(int x, int y) {
    this(new OrbNode(x, y));
  }

  public PVector getPosOrb(int n) {
    OrbNode current = startOrb;
    for (int i = 0; i < n; i++) {
      current = current.getNext();
    }
    return current.getPos();
  }

  public int getLength(){
    return length;
  }

  public void display() {
    OrbNode iter = startOrb;
    for (int i = 0; i < length; i++) {
      iter.display();
      if (i < length - 1) iter = iter.getNext();
    }
  }

  public void addFront(OrbNode o) {
    if (length == 0) {
      this.startOrb = o;
      endOrb = startOrb;
      length = 1;
      return;
    }
    OrbNode prevStart = startOrb;
    startOrb = o;
    startOrb.setNext(prevStart);
    startOrb.setPrev(null);
    prevStart.setPrev(startOrb);
    length++;
  }

  public void append(int x, int y, boolean fixed) {
    OrbNode o;
    if(fixed){
      o = new FixedOrbNode(x, y);
    } else {
      o = new OrbNode(x, y);
    }
    if (length == 0) {
      this.startOrb = o;
      endOrb = startOrb;
      length = 1;
      return;
    }
    OrbNode prevLast = endOrb;
    endOrb = o;
    prevLast.setNext(endOrb);
    endOrb.setPrev(prevLast);
    endOrb.setNext(null);
    length ++;
  }

  public void run() {
    OrbNode iter = startOrb;
    for (int i = 0; i < length; i++) {
      iter.run();
      if (i < length - 1) iter = iter.getNext();
    }
  }

  public void applySprings() {
    OrbNode iter = startOrb;
    for (int i = 0; i < length; i++) {
      iter.applySpringForce();
      if (i < length - 1) iter = iter.getNext();
    }
  }

  public void applyForce(PVector force) {
    OrbNode iter = startOrb;
    for (int i = 0; i < length; i++) {
      iter.applyForce(force);
      if (i < length - 1) iter = iter.getNext();
    }
  }
}
class OrbNode {
  private PVector pos, vel, nextAccel;
  private float psize;
  private int orbColor;
  private boolean drawVector;
  private OrbNode next, prev;
  static final float SPRING_LENGTH = 20, SPRING_CONST = 0.1f, AIR_DAMPING = 0.995f, VECTOR_SIZE = 3;

  public OrbNode(int x, int y) {
    if (psize == 0) psize = 20;
    drawVector = true;
    pos = new PVector(x, y);
    vel = new PVector(0, 0);
    nextAccel = new PVector(0, 0);
    next = null;
    prev = null;
    orbColor = color(255);
  }

  public void display() {
    stroke(0);
    strokeWeight(1);
    fill(orbColor);
    circle(pos.x, pos.y, psize);
    if (drawVector) {
      stroke(0);
      strokeWeight(5);
      line(pos.x, pos.y, pos.x + vel.x * VECTOR_SIZE, pos.y + vel.y * VECTOR_SIZE);
    }
    if (next != null) {
      stroke(20, 100, 255);
      strokeWeight(2);
      line(pos.x + 5, pos.y + 5, next.getPos().x + 5, next.getPos().y + 5);
    }
    if (prev != null) {
      stroke(255, 0, 0);
      strokeWeight(2);
      line(pos.x, pos.y, prev.getPos().x, prev.getPos().y);
    }
  }

  public PVector getPos() {
    return pos;
  }

  public OrbNode getNext() {
    return next;
  }

  public OrbNode getPrev() {
    return prev;
  }

  public void setSize(float psize) {
    this.psize = psize;
  }

  public void setColor(int c) {
    orbColor = c;
  }

  public void drawVector(boolean b) {
    drawVector = b;
  }

  public void setNext(OrbNode next) {
    this.next = next;
  }

  public void setPrev(OrbNode prev) {
    this.prev = prev;
  }

  public void applyForce(PVector f) {
    nextAccel.add(f);
  }

  public boolean checkInXBound() {
    return pos.x >= psize / 2f && pos.x <= width - psize / 2f;
  }

  public boolean checkInYBound() {
    return pos.y >= psize / 2f && pos.y <= height - psize / 2f;
  }

  public void applySpringForce() {
    if (next != null) this.applyForce(calculateSpringForce(next));
    if (prev != null) this.applyForce(calculateSpringForce(prev));
  }

  private PVector calculateSpringForce(OrbNode other) {
    float displacement = -(pos.dist(other.getPos()) - SPRING_LENGTH);
    PVector force = new PVector(other.getPos().x - pos.x, other.getPos().y - pos.y);
    force.setMag(-SPRING_CONST * displacement);
    return force;
  }

  public void run() {
    vel.add(nextAccel);
    if (!checkInXBound()) {
      if (pos.x < psize / 2f) pos.x = psize / 2f;
      if (pos.x > width - psize / 2f) pos.x = width - psize / 2f;
      vel.x = -vel.x;
    }
    if (!checkInYBound()) {
      if (pos.y < psize / 2f) pos.y = psize / 2f;
      if (pos.y > height - psize / 2f) pos.y = height - psize / 2f;
      vel.y = -vel.y;
    }
    vel.mult(AIR_DAMPING);
    pos.add(vel);
    nextAccel = new PVector(0, 0);
  }
}


  public void settings() { size(800, 400); }

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "OrbitalMotion" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
