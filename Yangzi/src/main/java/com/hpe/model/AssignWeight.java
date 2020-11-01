package com.hpe.model;

public class AssignWeight {
    private String name;    // request file name
    private int weight;     //  use weight
    
    private InputRequest jsonRequest; // request content object

	public String getName() {
		return name;
	}
	public InputRequest getJsonRequest() {
		return jsonRequest;
	}
	public void setJsonRequest(InputRequest jsonRequest) {
		this.jsonRequest = jsonRequest;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}

}