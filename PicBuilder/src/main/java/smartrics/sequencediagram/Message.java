package smartrics.sequencediagram;

import java.util.List;

public class Message extends Event{

	public Message(String source, String dest, String name, List<String> data) {
		super(source, dest, name, data);
	}

	public Message(String source, String dest, String name, String data) {
		super(source, dest, name, data);
	}

	public Message(String source, String dest, String name) {
		super(source, dest, name);
	}

	
	public void render(PicDiagram diagram) {
		diagram.step();
		if(!getSourceId().isActive()){
			diagram.active(getSourceId().getObjectId());
			getSourceId().setActive(true);
		}
		diagram.message(getSourceId().getObjectId(), getDestinationId().getObjectId(), buildLabel());
		diagram.active(getDestinationId().getObjectId());
	}

}
