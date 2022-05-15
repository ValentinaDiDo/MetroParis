package it.polito.tdp.metroparis.model;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import java.util.*;

import it.polito.tdp.metroparis.db.MetroDAO;
public class Model {

	private Graph<Fermata, DefaultEdge> grafo;
	
	List<Fermata> fermate;
	
	public List<Fermata> getFermate() {
		if(this.fermate == null) { // così se non è nullo evito di rifare l'interrogazione al database
			MetroDAO dao = new MetroDAO();
			this.fermate = dao.getAllFermate();
		}
		return this.fermate;
	}
	public List<Fermata> calcolaPercorso(Fermata partenza, Fermata arrivo){
		//calcola il percorso tra due fermate
		List<Fermata> percorso = new ArrayList<Fermata>();
		creaGrafo();
		visitaGrafo(partenza);
		return percorso;
	}
	
	public void creaGrafo() {
		this.grafo = new SimpleDirectedGraph<Fermata, DefaultEdge>(DefaultEdge.class);
	
		MetroDAO dao = new MetroDAO();
		List<Fermata> fermate = dao.getAllFermate();
		Map<Integer,Fermata> fermateIdMap = new HashMap<Integer,Fermata>();
		for(Fermata f: fermate)
			fermateIdMap.put(f.getIdFermata(), f) ;
		
		Graphs.addAllVertices(this.grafo, fermate); //uso fermate anzicché getFermate così non devo richiamare il database
		System.out.println(this.grafo); //stamperà l'insieme di tutte le fermate di parigi
		System.out.println("Vertici = "+this.grafo.vertexSet().size());
		
		//DIFETTO : AD OGNI ITERAZIONE DEVO ESEGUIRE UNA NUOVA INTERROGAZIONE AL DATABASE -> USO ALTRO METODO: PER OGNI STAZIONE PRENDO DIRETTAMENTE TUTTE LE SUE CONNESSIONI
	/*	for(Fermata partenza : fermate) {
			for(Fermata arrivo : fermate) {
				if(dao.isFermateConnesse(partenza, arrivo)) {
					this.grafo.addEdge(partenza, arrivo);
				}
			}
			
		}*/
		
		//METODO 2
		/*for(Fermata partenza : fermate) {
			List<Integer> idConnesse = dao.getIdFermateConnesse(partenza);
			for(Integer id : idConnesse) {
				Fermata arrivo = null;
				for(Fermata f : fermate) {
					if(f.getIdFermata()==id) {
						arrivo = f;
						break;
					}
				}
				this.grafo.addEdge(partenza, arrivo);
			}
		}*/
		
		//METODO 2B: prendo gli elementi FERMATA 
		/*for(Fermata partenza : fermate) {
			List<Fermata> arrivi = dao.getFermateConnesse(partenza);
			for(Fermata arrivo: arrivi) {
				this.grafo.addEdge(partenza, arrivo);
			}
		}*/
		
		
		//METODO 3: DELEGO TUTTO IL LAVORO AL DATABASE -> SE MI DA L'ELENCO DEI VERTICI DA COLLEGARE IL MODEL NON DEVE FARE NULLA
		
		List<CoppiaID> fermateDaCollegare = dao.getAllFermateConnesse();
		for(CoppiaID coppia : fermateDaCollegare) {
			this.grafo.addEdge(fermateIdMap.get(coppia.idPArtenza), fermateIdMap.get(coppia.idArrivo));
		}
		
		System.out.println(this.grafo);
		System.out.println("Vertici : "+this.grafo.vertexSet().size());
		System.out.println("Archi : "+this.grafo.edgeSet().size());
	}
	
	public void visitaGrafo(Fermata partenza) {
		GraphIterator<Fermata, DefaultEdge> visita = new DepthFirstIterator<>(this.grafo,partenza);
		
		//credo un albero inverso
		Map<Fermata, Fermata> alberoInverso = new HashMap<>();
		alberoInverso.put(partenza, null); //imposto che il primo vertice non ha predecessore;
		//lancio il listener
		visita.addTraversalListener(new RegistraAlberoVisita());
		while(visita.hasNext()) {
			Fermata f = visita.next();
			System.out.println(f);
		}
	}
	
	
}
