package smartrics.sequencediagram;

import java.io.IOException;
import java.io.Writer;

/**
 * Prints {@code PicSequenceDiagram} on a given stream or writer.
 * @author fabrizio
 */
public class Printer {
	private PicDiagram diagram;

	public Printer(PicDiagram b) {
		if (b == null)
			throw new IllegalArgumentException("null builder");
		this.diagram = b;
	}
	
	public void flush(Writer w){
		try {
			w.write(diagram.toString());
		} catch (IOException ioe) {
			throw new IllegalArgumentException(
					"Unable to flush on Writer", ioe);
		}
	}

	public void flush(java.io.OutputStream os) {
		try {
			os.write(diagram.toString().getBytes());
		} catch (IOException ioe) {
			throw new IllegalArgumentException(
					"Unable to flush on Output Stream", ioe);
		}
	}

	public void flush(java.io.PrintStream ps) {
		try {
			ps.write(diagram.toString().getBytes());
		} catch (IOException ioe) {
			throw new IllegalArgumentException(
					"Unable to flush on Print Stream", ioe);
		}
	}
}
