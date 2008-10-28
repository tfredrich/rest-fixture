package smartrics.sequencediagram;

import java.util.List;


public abstract class Event {
	private String source;
	private String destination;
	private String name;
	private String data;
	private Obj sourceId;
	private Obj destinationId;

	public Event(String source, String dest, String name, String data) {
		this.source = source;
		this.destination = dest;
		this.name = name;
		this.data = data;
	}

	public Event(String source, String dest, String name){
		this(source, dest, name, (String)null);
	}
	
	public Event(String source, String dest, String name, List<String> data){
		this(source, dest, name);
		if (data != null) {
			StringBuffer flattenData = new StringBuffer();
			for (String s : data) {
				flattenData.append("|").append(s);
			}
			this.data = flattenData.toString().substring(2);
		}
	}
	
	public String getSource() {
		return source;
	}

	public String getDestination() {
		return destination;
	}

	public String getName() {
		return name;
	}

	public String getData() {
		return data;
	}

	public String toString() {
		return String.format("Event[%s,%s,%s][%s]", source, destination, name, data);
	}

	public Obj getSourceId() {
		return sourceId;
	}

	public void setSourceId(Obj sourceId) {
		this.sourceId = sourceId;
	}

	public Obj getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(Obj destinationId) {
		this.destinationId = destinationId;
	}

	protected String buildLabel() {
		String label = getName();
		if (getData() != null) {
			label = String.format("%s(%s)", getName(), getData());
		}
		return label;
	}

	public abstract void render(PicDiagram diagram);

	
}
