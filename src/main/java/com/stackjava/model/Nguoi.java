package com.stackjava.model;

public class Nguoi {
	private int id;
	private int value;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "Nguoi [id=" + id + ", value=" + value + "]";
	}
	public Nguoi(int id, int value) {
		this.id = id;
		this.value = value;
	}
	
	public Nguoi() { this.value = 0;}
}
