package smartrics.sequencediagram;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
/*
groff -p $1.pic > $1.ps
./myps2img.sh $1.ps $1.gif

*/
public class Main {
	public static void main(String[] args) throws Exception {
		Main m = new Main();
		String pic = m.generateDiagram();
		m.writeOnFile("/home/fabrizio/pic/file.pic", pic);
		int ret = m.runProcess("/home/fabrizio/pic", new File("/home/fabrizio/pic/file.ps"), "/usr/bin/groff", "-p", "/home/fabrizio/pic/file.pic");
		System.out.println(ret);
		ret = m.runProcess("/home/fabrizio/pic", "/home/fabrizio/pic/myps2img.sh", "/home/fabrizio/pic/file.ps", "/home/fabrizio/pic/file.gif");
		System.out.println(ret);
	}

	private void writeOnFile(String fname, String content) throws Exception{
		File f = new File(fname);
		FileWriter w = new FileWriter(f);
		w.write(content);
		w.flush();
	}

	private int runProcess(String baseDir, File f, String... command) throws Exception{
		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(new File(baseDir));
		Process process = builder.command(command).start();
		if(f!=null){
			BufferedInputStream bis = new BufferedInputStream(process.getInputStream());
			FileOutputStream fos = new FileOutputStream(f);
			int len = 4096;
			byte[] buffer = new byte[len];
			while(len>0){
				len = bis.read(buffer, 0, len);
				if(len>0)
					fos.write(buffer, 0, len);
			}
		}
		process.waitFor();
		return process.exitValue();
	}
	
	private int runProcess(String baseDir, String... command) throws Exception{
		return runProcess(baseDir, null, command);
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
