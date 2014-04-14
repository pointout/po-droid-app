package to.pointout.model;

import java.util.ArrayList;


public class HeaderInfo {

	private String device;
	private String subject;
	private String location;
	
	private ArrayList<DetailInfo> responses = new ArrayList<DetailInfo>();


	public String getDevice() {
		return device;
	}


	public void setDevice(String device) {
		this.device = device;
	}


	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public ArrayList<DetailInfo> getResponses() {
		return responses;
	}


	public void setResponses(ArrayList<DetailInfo> responses) {
		this.responses = responses;
	}
	
	
	
}
