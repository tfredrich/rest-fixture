package smartrics.sequencediagram;

import java.util.List;

public class Self extends Event{

	public Self(String source, String name, List<String> data) {
		super(source, source, name, data);
	}

	public Self(String source, String name, String data) {
		super(source, source, name, data);
	}

	public Self(String source, String name) {
		super(source, source, name);
	}

	public void render(PicDiagram diagram) {
		diagram.step();
		diagram.active(getSourceId().getObjectId());
		diagram.message(getSourceId().getObjectId(), getSourceId().getObjectId(), buildLabel());
		diagram.inactive(getSourceId().getObjectId());
	}

}
