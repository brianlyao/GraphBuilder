package graph.components.gb;

import context.GBContext;
import graph.components.GraphComponent;
import lombok.Getter;

/**
 * Abstract class for a graph component attached to a GBContext.
 */
public abstract class GBComponent extends GraphComponent {

	@Getter
	private GBContext context;

	/**
	 * Initialize with context.
	 *
	 * @param context The context this belongs to.
	 */
	protected GBComponent(GBContext context) {
		super();
		this.context = context;
	}

	/**
	 * Check if this component is selected in the Editor. This will only
	 * succeed if the context has a GBFrame attached to it.
	 *
	 * @return true if the component is selected, false otherwise.
	 */
	public boolean isSelected() {
		return context.getGUI().getEditor().getData().isSelected(this);
	}

	/**
	 * Check if this component is highlighted in the Editor. This will only
	 * succeed if the context has a GBFrame attached to it.
	 *
	 * @return true if the component is highlighted, false otherwise.
	 */
	public boolean isHighlighted() {
		return context.getGUI().getEditor().getData().isHighlighted(this);
	}

	/**
	 * @return The ID of the underlying graph component.
	 */
	@Override
	public abstract int getId();

	/**
	 * A method for converting GBComponents into strings. This is how
	 * GraphBuilder saves graphs to files.
	 *
	 * @return the serialized GBComponent.
	 */
	public abstract String toStorageString();

}
