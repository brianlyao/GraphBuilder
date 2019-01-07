package graph.components;

import lombok.*;

/**
 * An abstract class for a component of the graph.
 *
 * @author Brian
 */
@AllArgsConstructor @NoArgsConstructor
public abstract class GraphComponent {

	@Getter @Setter(AccessLevel.PROTECTED)
	private int id; // The unique id of this component

}
