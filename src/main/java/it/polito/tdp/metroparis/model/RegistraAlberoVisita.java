package it.polito.tdp.metroparis.model;

import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;

public class RegistraAlberoVisita implements TraversalListener<Fermata, DefaultEdge> {
	private Map<Fermata, Fermata> alberoInverso;
	private Graph<Fermata, DefaultEdge> grafo;
	@Override
	public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
		// TODO Auto-generated method stub
	//	System.out.println(e.getEdge()); //stampo l'arco che è appena stato attraversato -> mi stampa la coppia ( partenza : arrivo )
		//molto meglio perché così capisco come raggiungere ogni singola stazione
		// sotto ogni stazione ho tra parentesi le coppie (stazione : stazione adiacente)
		
		//estermi dell'arco appena visitato da aggiungere nell'albero inverso
		Fermata source = this.grafo.getEdgeSource(e.getEdge());
		Fermata target = this.grafo.getEdgeTarget(e.getEdge());
		System.out.println(source+" -- "+target);
		
		//aggiungo arco ad albero inverso
		if(!alberoInverso.containsKey(target))
			alberoInverso.put(target, source);
		else if(!alberoInverso.containsKey(source))
			alberoInverso.put(source, target);
		//else niente perché ho già l'arco;
	}

	@Override
	public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void vertexFinished(VertexTraversalEvent<Fermata> e) {
		// TODO Auto-generated method stub
		
	}
	//La clqww3 implementa l'interfaccia traversal listener
}
