package graph.components;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An abstract class for a component of the graph.
 *
 * @author Brian
 */
@AllArgsConstructor @NoArgsConstructor
public abstract class GraphComponent {

	@Getter @Setter
	private int id; // The unique id of this component

}
