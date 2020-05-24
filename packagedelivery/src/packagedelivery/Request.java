package packagedelivery;

import java.util.*;

public class Request {

	private Station destiny;
	private ArrayList<Station> path = new ArrayList<Station>();
	private Route route;
	private int cost = 100;

	public Request(Station destiny) {
		this.destiny = destiny;
	}

	public boolean alreadyVisited(Station station) {
		return path.contains(station);
	}

	public Station getDestiny() {
		return this.destiny;
	}

	public Route getRoute() {
		return this.route;
	}

	public int getCost() {
		return this.cost;
	}

	public void setRoute(Route min_r) {
		this.route = min_r;
	}

	public void setCost(int min_c) {
		this.cost = min_c;
	}

	public Request buildNewWithPath(Station station) {
		Request newR = new Request(this.destiny);
		ArrayList<Station> newpath = (ArrayList<Station>) this.path.clone();
		newpath.add(station);
		newR.setPath(newpath);
		return newR;
	}

	public void setPath(ArrayList<Station> path) {
		this.path = path;
	}

}
