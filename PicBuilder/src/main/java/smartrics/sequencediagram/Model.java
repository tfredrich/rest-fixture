package smartrics.sequencediagram;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class Model {

	private Map<String, Obj> objectIds = new LinkedHashMap<String, Obj>();

	private Vector<Event> events = new Vector<Event>();

	private int baseId = 0;

	public Model() {
	}

	public Map<String, Obj> getObjects(){
		return Collections.unmodifiableMap(objectIds);
	}
	
	public List<Event> getEvents(){
		return Collections.unmodifiableList(events);
	}

	public void addEvent(Event e) {
		e.setSourceId(storeObj(e.getSource(), false));
		e.setDestinationId(storeObj(e.getDestination(), false));
		events.add(e);
	}

	public void addEvent(Event e, boolean placeholder) {
		e.setSourceId(storeObj(e.getSource(), false));
		e.setDestinationId(storeObj(e.getDestination(), placeholder));
		events.add(e);
	}
	
	private Obj storeObj(String obj, boolean placeholder) {
		Obj id = objectIds.get(obj);
		if (null == id) {
			id = new Obj(generateId(), placeholder);
			objectIds.put(obj, id);
		}
		return id;
	}

	private String generateId() {
		String idDec = Integer.toString(baseId++);
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i<idDec.length(); i++){
			int c = idDec.charAt(0) - (int)'0' + (int)'A';
			sb.append((char)c);
		}
		return sb.toString();
	}

}
