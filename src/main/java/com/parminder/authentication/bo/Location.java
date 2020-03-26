package com.parminder.authentication.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Location {

	public Location() {

	}

	public Location(Double lat, Double lng) {
		coordinates = new ArrayList<Double>();
		coordinates.add(lat);
		coordinates.add(lng);
		date = new Date();
	}

	Date date;
	String type = "Point";

	List<Double> coordinates;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Double> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Double> coordinates) {
		this.coordinates = coordinates;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
