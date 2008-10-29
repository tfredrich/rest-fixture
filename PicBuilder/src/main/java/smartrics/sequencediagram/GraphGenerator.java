package smartrics.sequencediagram;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

public class GraphGenerator {

	private File sequencePicDir;

	public GraphGenerator(String sequencePicDir){
		this.sequencePicDir = new File(sequencePicDir);
	}
	
	public void generateGif(String pic, File name){
		File picFile = new File(sequencePicDir, "file.pic");
		File psFile = new File(sequencePicDir, "file.ps");
		try{
			writeOnFile(picFile, pic);
			int ret = runProcess(sequencePicDir, psFile, "groff", "-p", picFile.getAbsolutePath());
			if(ret!=0)
				throw new IllegalStateException("Can't run groff");
			ret = runProcess(sequencePicDir, new File(sequencePicDir, "myps2img.sh").getAbsolutePath(), 
					psFile.getAbsolutePath(), name.getAbsolutePath());
			if(ret!=0)
				throw new IllegalStateException("Can't run myps2img.sh");
		} catch(Exception e){
			throw new IllegalArgumentException("Can't generate file " + name.getAbsolutePath(), e);
		} finally {
			try {
				picFile.delete();
			} catch(Exception e){}
			try {
				psFile.delete();
			} catch(Exception e){}
		}
	}
	
	private void writeOnFile(File picFile, String content) throws Exception{
		FileWriter w = new FileWriter(picFile);
		w.write(content);
		w.flush();
	}

	private int runProcess(File baseDir, File output, String... command) throws Exception{
		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(baseDir);
		Process process = builder.command(command).start();
		if(output!=null){
			BufferedInputStream bis = new BufferedInputStream(process.getInputStream());
			FileOutputStream fos = new FileOutputStream(output);
			int len = 4096;
			byte[] buffer = new byte[len];
			while(len>0){
				len = bis.read(buffer, 0, len);
				if(len>0)
					fos.write(buffer, 0, len);
			} 
			fos.close();
			bis.close();
		}
		process.waitFor();
		return process.exitValue();
	}
	
	private int runProcess(File baseDir, String... command) throws Exception{
		return runProcess(baseDir, null, command);
	}

}
