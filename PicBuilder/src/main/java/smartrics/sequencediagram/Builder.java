package smartrics.sequencediagram;

import java.util.Map;
import java.util.Map.Entry;


public class Builder {
	private PicDiagram diagram;
	private Model model;

	public Builder(Model m, PicDiagram d) {
		this.model = m;
		this.diagram = d;
	}

	public void build() {
		Map<String, Obj> objects = model.getObjects();
		diagram.fileComment("Objects declaration");
		for (Entry<String, Obj> e : objects.entrySet()) {
			if (e.getValue().isPlaceholder())
				diagram.placeholderObject(e.getValue().getObjectId());
			else
				diagram.object(e.getValue().getObjectId(), e.getKey());
		}
		diagram.fileComment("Sequence of events");
		for (Event e : model.getEvents()) {
			e.render(diagram);
		}
		diagram.fileComment("Objects completion");
		boolean done1step = false;
		for (Entry<String, Obj> e : objects.entrySet()) {
			if(e.getValue().isActive()){
				if(!done1step){
					diagram.step();
					done1step = true;
				}
				diagram.inactive(e.getValue().getObjectId());
			}
		}
		if(done1step){
			diagram.step();
		}
		for (Entry<String, Obj> e : objects.entrySet()) {
			diagram.complete(e.getValue().getObjectId());
		}
	}

}
