# Rock Paper Scissors the Real-time Strategy Game #
## -Implemented in Java- ##

I should initially point out that this was my first experience with both Java and with writing a game of this complexity. 

I have tried to retrospectively streamline it, though the code and program flow remains rather ugly in places.

![Global Warfare][1]

## To Run ##

In Eclipse, import project with RPSRTS/src as the Build Path.

Add a new Java Applet run configuration for com.timboe.rpsrts.RPSRTS

## Features ##

 - Procedurally generated game island, landmasses assigned utilising a particle physics favourite: the *kt* algorithm.
 - 2.5D graphics rendered through Java AWT library. 
 - Rock, Paper & Scissor units. All units gain a x2 damage bonus when fighting their natural enemy.
  - **Paper** are plentiful, cheap and weak. Archetypal skirmishers. 
  - **Scissors** are the infantry, good all-rounders.
  - **Rock** are the expensive, slow and strong tank-like units. 
 - Two further elite units to discover with additional devastating special attacks.
 - Play as **RED** vs. the evil AI controlled **BLU** opponent.
 - Building Types:
  - **Rockery:** *Stone* is collected and deposited here by your *Paper* and used to assemble more *Rocks*.
  - **Smelter:** *Iron* is collected by *Rocks* and forged here into new *Scissors*.
  - **Wood-shop:** *Wood* is collected by *Scissors* and used in the manufacture of more *Paper* 
  - **Totems:** One per unit type, these movable constructions are used to maneuver your troops in battle.
 - Particle effect system.
 - A* Path-finding.
 - High scores and statistics.

![Attacking BLU][2]

----------

Work-in-progress: Android compatibility. All dependence on AWT is contained within the applet package, some very early code is present in the android package which needs to be revisited. Mostly working with different render calls to Canvas rather than Graphics2D and Matrix rather than AffineTransform objects.

This project was originally inspired by Notch's [Breaking The Tower][3]

  [1]: http://tim-martin.co.uk/images/rpsrts/rpsrts_demo_01.png
  [2]: http://tim-martin.co.uk/images/rpsrts/rpsrts_demo_02.png
  [3]: http://www.mojang.com/notch/ld12/breaking/