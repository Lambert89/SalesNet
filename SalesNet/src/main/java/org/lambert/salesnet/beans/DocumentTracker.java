package org.lambert.salesnet.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DocumentTracker implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	private long id = 1L;

	private Map<String, String> files = new ConcurrentHashMap<String, String>();

	public void addDocument(final String fileName, final String fileLocation) {
		this.files.put(fileName, fileLocation);
	}

	public String removeDocument(final String fileName) {
		final String location = this.files.get(fileName);
		this.files.remove(fileName);
		return location;
	}

	public String getDocumentLocation(final String fileName) {
		return this.files.get(fileName);
	}

	public List<String> getAllDocumentNames() {
		final List<String> result = new ArrayList<String>();
		result.addAll(this.files.keySet());
		return result;
	}

}
