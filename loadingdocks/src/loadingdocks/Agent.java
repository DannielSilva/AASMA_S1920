package loadingdocks;

import java.awt.Color;
import java.awt.Point;
import loadingdocks.Block.Shape;
import java.util.Random;
/**
 * Agent behavior
 * @author Rui Henriques
 */
public class Agent extends Entity {

	public int direction = 90;
	public Box load = null;

	public Agent(Point point, Color color){ 
		super(point, color);
	} 
	
	
	/**********************
	 **** A: decision ***** 
	 **********************/
	
	public void agentDecision() {
    if (isWall()) rotate();
	  else if(isFreeCell()) {
      boolean val = new Random().nextInt(25)==0;
      if (val) {
        rotate();
      }
      else
      moveAhead();
    }
	  else if(load == null &&  next_cell_box() && next_cell_ramp()) pickup();
    else if(next_cell_shelf() && hasBox() && (shelfcolor() == boxcolor()) && !next_cell_box()) drop();
    else if (!isFreeCell()) rotate();
	}
	
	/********************/
	/**** B: sensors ****/
	/********************/
	
	/* Check if the cell ahead is floor (which means not a wall, not a shelf nor a ramp) and there are any robot there */
	protected boolean isFreeCell() {
	  Point ahead = aheadPosition();
	  return Board.getBlock(ahead).shape.equals(Shape.free) && Board.getEntity(ahead) == null;
	}

	/* Check if the cell ahead is a wall */
	protected boolean isWall() {
		Point ahead = aheadPosition();
		return ahead.x<0 || ahead.y<0 || ahead.x>=Board.nX || ahead.y>=Board.nY;
	}

	/* Check if agent is carrying box */
	protected boolean hasBox() {
		return load != null;
	}

	/* Check if next cell has box */
	protected boolean next_cell_box() {
		Point ahead = aheadPosition();
		//Box b;
		Entity p = Board.getEntity(ahead);
		//test = new Box(p.point,p.color);
		//return b.isInstance(test);
		return p instanceof Box;

	}

	/* Check if next cell is a shelf */
	protected boolean next_cell_shelf() {
		Point ahead = aheadPosition();
		return Board.getBlock(ahead).shape.equals(Shape.shelf);
	}

	/* Check if next cell is a ramp */
	protected boolean next_cell_ramp() {
		Point ahead = aheadPosition();
		return Board.getBlock(ahead).shape.equals(Shape.ramp);
	}

	/* Check the box color */
	protected Color boxcolor() {
		return load.color;
	}

	/* Check the box shelfcolor */
	protected Color shelfcolor() {
		Point ahead = aheadPosition();
		return Board.getBlock(ahead).color;
	}





	/**********************/
	/**** C: actuators ****/
	/**********************/

	/* Rotate agent to right */
	public void rotate() {
		direction = (direction+90)%360;
	}
	
	/* Move agent forward */
	public void moveAhead() {
		Point ahead = aheadPosition();
		Board.updateEntityPosition(point,ahead);
		point = ahead;
	}
	
	/* Agent pick box */
	public void pickup() {
		Point ahead = aheadPosition();
		Entity p = Board.getEntity(ahead);

		load = new Box(p.point,p.color);
    Board.removeEntity(ahead);
    System.out.println("PEGUEII");
	}

	/* Agent drop box */
	public void drop() {
		Point ahead = aheadPosition();
		Board.insertEntity(load, ahead);
    load = null;
    System.out.println("DROPEII");
	}

	/**********************/
	/**** D: auxiliary ****/
	/**********************/

	/* Position ahead */
	private Point aheadPosition() {
		Point newpoint = new Point(point.x,point.y);
		switch(direction) {
			case 0: newpoint.y++; break;
			case 90: newpoint.x++; break;
			case 180: newpoint.y--; break;
			default: newpoint.x--; 
		}
		return newpoint;
	}
}
