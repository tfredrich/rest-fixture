package smartrics.sequencediagram;

import java.util.List;

public class Delete extends Event{

	public Delete(String source, String dest, String name, List<String> data) {
		super(source, dest, name, data);
	}

	public Delete(String source, String dest, String name, String data) {
		super(source, dest, name, data);
	}

	public Delete(String source, String dest, String name) {
		super(source, dest, name);
	}


	public void render(PicDiagram diagram) {
		diagram.step();
		diagram.destroyMessage(getSourceId().getObjectId(), getDestinationId().getObjectId());
	}

}
