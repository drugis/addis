package edu.uci.ics.jung.graph;

import java.util.ArrayList;

import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;

public class FixedObservableGraph<V, E> extends ObservableGraph<V, E> {
	private static final long serialVersionUID = 6225574196524068745L;

	public FixedObservableGraph(Graph<V, E> delegate) {
		super(delegate);
	}

	@Override
	protected void fireGraphEvent(GraphEvent<V, E> evt) {
		for(GraphEventListener<V,E> listener : new ArrayList<GraphEventListener<V,E>>(listenerList)) {
			listener.handleGraphEvent(evt);
		 }
	}
}
