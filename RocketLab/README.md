EXTENSIONS: 
- Locations of goal is randomly determined, and will always generate near the top, right, or bottom side of window
- Obstacles are randomly generated in random amounts, and will block rockets from moving.
- Autonomous mode is available as an option to automatically evolve the current generation once it has been simulated
- The highest-fitness rocket is always highlighted in blue
- The mutation rate can be changed via key presses

1)  Gene can be used eactly as written, because it doesn't require modifications to 
    change from forming the properties of a polygon, to the properties of a rocket, as 
    long as the data is interpreted differently. Popiation also does not need to change, 
    since it is general enough to work with any individual/gene.

2)  Individal will need to be modified. Individual needs to be modified to have different 
    constants, since the genotypes will be of different ranges. 

3)  The Genes needs to store the angle and magnitude of each frame. My plan is to store an 
    array of Genes in Individual that specifically store the first 50 movements of the 
    individual, with the angle's value being between -15 and 15 degrees (i need a new 
    method to evalue specific cases of genes dealing with negatives), and the magnitude.

4) The main file's grid will no longer exist, and all individuals will be drawn from the 
    same point. Fitness will be displayed on each indivdual.