package maze.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import core.Direction;
import core.Pos;

public class MazeExplorer {
	private Maze m;
	private Pos location;
	private TreeSet<Pos> treasureFound;
	private MazeExplorer goal;
	
	public MazeExplorer(Maze m, Pos location) {
		this.m = m;
		this.location = location;
		treasureFound = new TreeSet<>();
	}
	
	public Pos getLocation() {return location;}

	public Set<Pos> getAllTreasureFromMaze() {
		return m.getTreasures();
	}

	public Set<Pos> getAllTreasureFound() {
		return treasureFound;
	}

	public int getNumTreasuresFound() {
		return treasureFound.size();
	}

	public MazeExplorer getGoal() {
		if (goal == null) {
			goal = m.getGoal();
		}
		return goal;
	}

	public ArrayList<MazeExplorer> getSuccessors() {
		ArrayList<MazeExplorer> result = new ArrayList<MazeExplorer>();
		ArrayList<Pos> neighborPos = location.getNeighbors();

		for (Pos newPos : neighborPos) {
			if (!m.blocked(location, Direction.between(location, newPos))) {
				MazeExplorer avenger = new MazeExplorer(m, newPos);
				avenger.addTreasures(this.getAllTreasureFound());

				if (m.isTreasure(newPos) && getAllTreasureFound().size() < getAllTreasureFromMaze().size()) {
					TreeSet<Pos> upTreasures = new TreeSet<>(treasureFound);
					upTreasures.add(newPos);
					avenger.addTreasures(upTreasures);
				}
				result.add(avenger);
			}
		}

		return result;
	}
	
	public void addTreasures(Collection<Pos> treasures) {
		treasureFound.addAll(treasures);
	}
	
	public String toString() {
		StringBuilder treasures = new StringBuilder();
		for (Pos t: treasureFound) {
			treasures.append(";");
			treasures.append(t.toString());
		}
		return "@" + location.toString() + treasures;
	}
	
	@Override
	public int hashCode() {return toString().hashCode();}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof MazeExplorer that) {
			return this.location.equals(that.location) && this.treasureFound.equals(that.treasureFound);
		} else {
			return false;
		}
	}

	public boolean achievesGoal() {
		return this.equals(getGoal());
	}

	public Maze getM() {
		return m;
	}
}
