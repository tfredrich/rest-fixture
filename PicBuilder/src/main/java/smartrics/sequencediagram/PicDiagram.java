package smartrics.sequencediagram;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Buffer of PIC macros. The interface maps to the macros defined in <a
 * href="http://www.umlgraph.org">UMLGraph</a>.
 * 
 * @author fabrizio
 */
public class PicDiagram {
	private List<String> linesBuffer = new Vector<String>();

	public void fileComment(String comment){
		linesBuffer.add(String.format("\n# %s", comment));
	}
	
	/**
	 * Defines an object with the given name, labeled on the diagram as
	 * specified.
	 */
	public void object(String name, String label) {
		linesBuffer.add(String.format("object(%s,%s);", name, quote(label)));
	}

	/**
	 * Defines a place where the named object will later be created. Can also be
	 * written as pobject.
	 */
	public void placeholderObject(String name) {
		linesBuffer.add(String.format("placeholder_object(%s);", name));
	}

	/**
	 * Defines an actor with the given name, labeled on the diagram as
	 * specified. Actors are typically used instead of objects to indicate
	 * operations initiated by human actions.
	 */
	public void actor(String name, String label) {
		linesBuffer.add(String.format("actor(%s,%s);", name, quote(label)));
	}

	/**
	 * Completes the lifeline of a given object (or actor) by drawing its
	 * lifeline to the bottom of the diagram.
	 */
	public void complete(String name) {
		linesBuffer.add(String.format("complete(%s);", name));
	}

	/**
	 * Draws a message between two objects, with the given label. Self messages
	 * (where an objects sends a message to itself) are supported.
	 */
	public void message(String from_object, String to_object, String label) {
		linesBuffer.add(String.format("message(%s,%s,%s);", from_object,
				to_object, quote(label)));
	}

	/**
	 * Draws a return message between two objects, with the given label. Can
	 * also be written as rmessage.
	 */
	public void returnMessage(String from_object, String to_object, String label) {
		linesBuffer.add(String.format("return_message(%s,%s,%s);",
				from_object, to_object, quote(label)));
	}

	/**
	 * Has from_object create the to_object, labeled with object_label. The
	 * message is labeled with the «create» stereotype. Can also be written as
	 * cmessage.
	 */
	public void createMessage(String from_object, String to_object,
			String object_label) {
		linesBuffer.add(String.format("create_message(%s,%s,%s);",
				from_object, to_object, quote(object_label)));
	}

	/**
	 * Sends a message labeled with the «destroy» stereotype from the
	 * from_object to the to_object. The object to_object is marked as
	 * destroyed, with an X at the end of its lifeline. The object's lifeline
	 * need not be otherwise completed. Can also be written as dmessage.
	 */
	public void destroyMessage(String from_object, String to_object) {
		linesBuffer.add(String.format("destroy_message(%s,%s);", from_object,
				to_object));
	}

	/**
	 * Changes the object's status to active, and changes its lifeline drawing
	 * style correspondingly. An active call in an already active object will
	 * result in a swimlane showing a nested object activation.
	 */
	public void active(String object) {
		linesBuffer.add(String.format("active(%s);", object));
	}

	/**
	 * Changes the object's status to inactive, and changes its lifeline drawing
	 * style correspondingly. An inactive call on a nested object invocation
	 * will result in showing a simple active swimlane.
	 */
	public void inactive(String object) {
		linesBuffer.add(String.format("inactive(%s);", object));
	}

	/**
	 * The object deletes itself, drawing an X at the end of its lifeline. The
	 * object's lifeline need not be otherwise completed.
	 */
	public void delete(String object) {
		linesBuffer.add(String.format("delete(%s);", object));
	}

	/**
	 * Displays a constraint label (typically given inside curly braces) for the
	 * given object. The constraint will appear on the right of the object's
	 * lifeline at the time it appears. Can also be used to place an message
	 * label on the left of a message arrow, rather than its center. Can also be
	 * written as lconstraint.
	 */
	public void lifelineConstraint(String object, String label) {
		linesBuffer.add(String.format("lifeline_constraint(%s,%s);", object,
				quote(label)));
	}

	/**
	 * same as lconstraint, but it will be shown below the current line instead
	 * of above.
	 */
	public void lconstraintBelow(String object, String label) {
		linesBuffer.add(String.format("lconstraint_below(%s,%s);", object,
				quote(label)));
	}

	/**
	 * Displays an object constraint (typically given inside curly braces) for
	 * the last object defined. Can also be written as oconstraint.
	 */
	public void objectConstraint(String label) {
		linesBuffer.add(String.format("object_constraint(%s);", label));
	}

	/** Steps the time by a single increment, extending all lifelines. */
	public void step() {
		linesBuffer.add(String.format("step();"));
	}

	/**
	 * All subsequent messages are asynchronous and will be drawn
	 * correspondingly.
	 */
	public void async() {
		linesBuffer.add(String.format("async();"));
	}

	/**
	 * All subsequent messages are synchronous and will be drawn
	 * correspondingly.
	 */
	public void sync() {
		linesBuffer.add(String.format("sync();"));
	}

	/**
	 * Begins a frame with the upper left corner at left_object column and the
	 * current line. The specified label_text is shown in the upper left corner.
	 */
	public void beginFrame(String left_object, String name, String label_text) {
		linesBuffer.add(String.format("begin_frame(%s,%s,%s);", left_object,
				quote(name), quote(label_text)));
	}

	/**
	 * Ends a frame with the lower right corner at right_object column and the
	 * current line. The name must correspond to a begin_frame's name.
	 */
	public void endFrame(String right_object, String name) {
		linesBuffer.add(String.format("end_frame(%s,%s);", right_object,
				quote(name)));
	}

	/**
	 * Displays a comment about the object. The name can be used with
	 * connect_to_comment(object2,name); to get additional connecting lines to
	 * the comment. line_movement changes the position of the comment and
	 * box_size its size. Note that there's no comma between box_size and text.
	 * text is the (multiline) comment-text that will be displayed. name,
	 * line_movement and box_size are optional (but the commas must still
	 * appear).
	 */
	public void comment(String object, String name, String text) {
		comment(object, name, "", "", text);
	}

	public void comment(String object, String name, String box_size, String text) {
		comment(object, name, "", box_size, text);
	}

	public void comment(String object, String name, String line_movement, String box_size, String text) {
		linesBuffer.add(String.format("comment(%s,%s,%s,%s %s);", object,
				quote(name), line_movement, box_size, quote(text)));
	}

	/** See comment. */
	public void connectToComment(String object, String name) {
		linesBuffer.add(String.format("connect_to_comment(%s,%s);", object, quote(name)));
	}

	private String quote(String s) {
		return String.format("\"%s\"", s);
	}

	private List<String> getLinesBuffer() {
		return linesBuffer;
	}

	private String getHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append(".PS\n");
		sb.append("copy \"sequence.pic\";\n\n");
		sb.append("boxwid = 0.9;\n");
		sb.append("movewid = 0.3;\n");
		sb.append("spacing = 0.2;\n\n");
		return sb.toString();
	}

	private String getFooter() {
		return ".PE\n\n";
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator<String> it = getLinesBuffer().iterator();
		sb.append(getHeader());
		while (it.hasNext()) {
			sb.append(it.next());
			sb.append("\n");
		}
		sb.append(getFooter());
		return sb.toString();
	}
}
