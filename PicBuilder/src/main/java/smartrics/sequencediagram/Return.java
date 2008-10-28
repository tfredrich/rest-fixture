package smartrics.sequencediagram;

import java.util.List;

public class Return extends Event{

	private String code;
	
	public Return(String source, String dest, String code, List<String> data) {
		super(source, dest, "", data);
		this.code = code;
		System.out.println(this);
	}

	public Return(String source, String dest, String code, String data) {
		super(source, dest, "", data);
		this.code = code;
		System.out.println(this);
	}

	public Return(String source, String dest, String code) {
		super(source, dest, "");
		this.code = code;
		System.out.println(this);
	}
	
	public String getCode(){
		return code;
	}

	public void render(PicDiagram diagram) {
		String code = getCode();
		String sep = "/";
		if (code == null){
			code = "";
			sep = "";
		}
		String data = getData();
		if(data==null){
			data = "";
			sep = "";
		}
		String label = code + sep + data;
		diagram.returnMessage(getSourceId().getObjectId(), getDestinationId().getObjectId(), label);
		diagram.inactive(getSourceId().getObjectId());
	}

	public String toString() {
		return String.format("Return[s:%s,d:%s,n:%s][d:%s][c:%s]", getSource(), getDestination(), getName(), getData(), getCode());
	}

}
