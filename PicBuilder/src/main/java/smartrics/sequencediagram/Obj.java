package smartrics.sequencediagram;

public class Obj {
	private String objectId;
	private boolean placeholder;
	private boolean active;

	public Obj(String objectId) {
		this(objectId, false);
	}

	public Obj(String objectId, boolean placeholder) {
		this.placeholder = placeholder;
		this.objectId = objectId;
		this.active = false;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setPlaceholder(boolean v) {
		this.placeholder = v;
	}

	public boolean isPlaceholder() {
		return placeholder;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean a){
		this.active = a;
	}
}
