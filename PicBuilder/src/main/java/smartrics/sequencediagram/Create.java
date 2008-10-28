package smartrics.sequencediagram;

import java.util.List;

public class Create extends Event{

	public Create(String source, String dest, String name, List<String> data) {
		super(source, dest, name, data);
	}

	public Create(String source, String dest, String name, String data) {
		super(source, dest, name, data);
	}

	public Create(String source, String dest, String name) {
		super(source, dest, name);
	}

	public void render(PicDiagram diagram) {
		diagram.step();
		diagram.createMessage(getSourceId().getObjectId(), getDestinationId().getObjectId(), buildLabel());
	}
}
