package smartrics.sequencediagram;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class Main {
	public static void main(String[] args) throws Exception{
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
		Printer p = new Printer(diag);
		File f = new File("/home/fabrizio/pic/first.pic");
		OutputStream os = new FileOutputStream(f);
		p.flush(os);
		p.flush(System.out);
//		ProcessBuilder builder = new ProcessBuilder();
//		builder.directory(new File("/home/fabrizio/pic"));
//		builder.command("./run", "first");
//		Process pr = builder.start();
//		System.out.println(pr.exitValue());
	}
}
