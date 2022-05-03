/* autogenerated by Processing revision 1282 on 2022-05-03 */
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

float GRAVITY = 0.1f;

OrbList orbs;
PVector g;

boolean moving;

 public void setup() {
  /* size commented out by preprocessor */;
  g = new PVector(0, GRAVITY);
  println("==========================================================================================");
  println("SPACE to enable/disable movement, H to apply force based on mouse position, and R to reset");
  println("==========================================================================================");
  reset();
}

 public void reset() {
  moving = false;
  
  int x = width / 2 - 50;
  int y = 200;
  orbs = new OrbList(new OrbNode(x, y));
  x += 50;
  for(int i = 1; i < 3; i++){
    orbs.append(new OrbNode(x, y));
    x += 50;
  }
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
}

 public void runAStep() {
}

 public void keyPressed() {
  if (key == ' ') {
    moving = !moving;
  }
  
  if (key == 'h') {
    moving = true;
    //orbs[1].applyForce(new PVector((mouseX - orbs[1].getPos().x) * 0.01, (mouseY - orbs[1].getPos().y) * 0.01));
  }

  if (key == 'r') {
    reset();
  }
}
class OrbList{
  private OrbNode startOrb;
  private int length;
  
  public OrbList(OrbNode startOrb){
    this.startOrb = startOrb;
    length = 1;
  }
  
  public void calculateLength(){
    length = 1;
    OrbNode test = startOrb;
    while(test.getNext() != null){
      test = test.getNext();
      length++;
    }
  }

  public void display(){
    OrbNode iter = startOrb;
    calculateLength();
    for(int i = 0; i < length; i++){
      iter.display();
      if(i < length - 1) iter = iter.getNext();
    }
  }
  
  public void addFront(OrbNode o){
    OrbNode prevStart = startOrb;
    startOrb = o;
    startOrb.setNext(prevStart);
    startOrb.setPrev(null);
    prevStart.setPrev(startOrb);
    length++;
  }
  
  public void append(OrbNode o){
    OrbNode last = startOrb;
    for(int i = 1; i < length; i++){
      last = last.getNext();
    }
    if(last == null){
      println("fuck");
      return;
    }
    o.setPrev(last);
    o.setNext(null);
    last.setNext(o);
    length ++;
  }
}
class OrbNode {
  private PVector pos, vel, nextAccel;
  private float psize;
  private int orbColor;
  private boolean drawVector;
  private OrbNode next, prev;
  static final float SPRING_LENGTH = 50, SPRING_CONST = 0.005f, AIR_DAMPING = 0.995f, VECTOR_SIZE = 3;

  public OrbNode(int x, int y){
    if(psize == 0) psize = 20;
    drawVector = true;
    pos = new PVector(x, y);
    vel = new PVector(0, 0);
    nextAccel = new PVector(0, 0);
    next = null;
    prev = null;
    orbColor = color(255);
  }
  
  public void display(){
    stroke(0);
    strokeWeight(1);
    fill(orbColor);
    circle(pos.x, pos.y, psize);
    if(drawVector){
      stroke(0);
      strokeWeight(5);
      line(pos.x, pos.y, pos.x + vel.x * VECTOR_SIZE , pos.y + vel.y * VECTOR_SIZE);
    }
    if(next != null){
      stroke(0, 255, 255);
      strokeWeight(2);
      line(pos.x + 5, pos.y + 5, next.getPos().x + 5, next.getPos().y + 5);
    }
    if(prev != null){
      stroke(255, 0, 0);
      strokeWeight(2);
      line(pos.x, pos.y, prev.getPos().x, prev.getPos().y);
    }
  }

  public PVector getPos(){
    return pos;
  }
  
  public OrbNode getNext(){
    return next;
  }
  
  public OrbNode getPrev(){
    return prev;
  }
  
  public void setSize(float psize){
    this.psize = psize;
  }

  public void setColor(int c){
    orbColor = c;
  }

  public void drawVector(boolean b){
    drawVector = b;
  }
  
  public void setNext(OrbNode next){
    this.next = next;
  }
  
  public void setPrev(OrbNode prev){
    this.prev = prev;
  }

  public void applyForce(PVector f){
    nextAccel.add(f);
  }
  
  public boolean checkInXBound(){
    return pos.x >= psize / 2f && pos.x <= width - psize / 2f; 
  }
  
  public boolean checkInYBound(){
    return pos.y >= psize / 2f && pos.y <= height - psize / 2f; 
  }
  
  public void applySpringForce(){
    if(next != null) this.applyForce(calculateSpringForce(next));
    if(prev != null) this.applyForce(calculateSpringForce(prev));
  }

  private PVector calculateSpringForce(OrbNode other){
    float displacement = -(pos.dist(other.getPos()) - SPRING_LENGTH);
    PVector force = new PVector(other.getPos().x - pos.x, other.getPos().y - pos.y);
    force.setMag(-SPRING_CONST * displacement);
    return force;
  }
  
  public void run(){
    vel.add(nextAccel);
    if(!checkInXBound()){
      if(pos.x < psize / 2f) pos.x = psize / 2f;
      if(pos.x > width - psize / 2f) pos.x = width - psize / 2f;
      vel.x = -vel.x;
    } 
    if(!checkInYBound()){
      if(pos.y < psize / 2f) pos.y = psize / 2f;
      if(pos.y > height - psize / 2f) pos.y = height - psize / 2f;
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
