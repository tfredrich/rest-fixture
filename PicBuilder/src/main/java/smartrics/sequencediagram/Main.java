package smartrics.sequencediagram;

import java.io.File;
import java.io.FileNotFoundException;
/*
groff -p $1.pic > $1.ps
./myps2img.sh $1.ps $1.gif

*/
public class Main {
	public static void main(String[] args) throws Exception {
		Main m = new Main();
		String pic = m.generateDiagram();
		GraphGenerator g = new GraphGenerator("/home/fabrizio/pic");
		g.generateGif(pic, new File("/home/fabrizio/pic/firstx.gif"));
	}

	
	private String generateDiagram() throws FileNotFoundException {
		PicDiagram diag = new PicDiagram();
		Model m = new Model();
		Builder b = new Builder(m, diag);
		m.addEvent(new Message("client", "/resources", "GET", "id=234"));
		m.addEvent(new Return("/resources", "client", "200"));

		m.addEvent(new Message("client", "/resources", "POST"));
		m.addEvent(new Create("/resources", "/resources/0", "POST"), true);
		m.addEvent(new Return("/resources", "client", "201", "0"));

		m.addEvent(new Message("client", "/resources/1", "PUT"));
		m.addEvent(new Return("/resources/1", "client", "200"));

		m.addEvent(new Self("client", "let", "id=1"));

		m.addEvent(new Message("client", "/resources/0", "DELETE"));
		m.addEvent(new Return("/resources/0", "client", "204"));

		b.build();
		return diag.toString();
	}
}
