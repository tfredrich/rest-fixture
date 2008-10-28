package smartrics.sequencediagram;

import java.io.IOException;

/**
 * Prints {@code PicSequenceDiagram} on a given stream or writer.
 * @author fabrizio
 */
public class Printer {
	private PicDiagram builder;

	public Printer(PicDiagram b) {
		if (b == null)
			throw new IllegalArgumentException("null builder");
		this.builder = b;
	}

	public void flush(java.io.OutputStream os) {
		try {
			os.write(builder.toString().getBytes());
		} catch (IOException ioe) {
			throw new IllegalArgumentException(
					"Unable to flush on Output Stream", ioe);
		}
	}

	public void flush(java.io.PrintStream ps) {
		try {
			ps.write(builder.toString().getBytes());
		} catch (IOException ioe) {
			throw new IllegalArgumentException(
					"Unable to flush on Print Stream", ioe);
		}
	}
}
