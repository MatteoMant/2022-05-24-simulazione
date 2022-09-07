package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {

	private ItunesDAO dao;
	private Graph<Track, DefaultWeightedEdge> grafo;
	private Map<Integer, Track> idMap;
	
	// variabili utili per la ricorsione
	private List<Track> best;
	
	public Model() {
		dao = new ItunesDAO();
		idMap = new HashMap<>();
		dao.getAllTracks(idMap);
	}

	public void creaGrafo(Genre genere) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		// Aggiunta dei vertici
		Graphs.addAllVertices(this.grafo, dao.getAllTracksWithGenre(genere));

		// Aggiunta degli archi
		for (Adiacenza a : dao.getAllAdiacenze(genere, idMap)) {
			Graphs.addEdge(this.grafo, a.getT1(), a.getT2(), a.getPeso());
		}
		
	}
	
	public List<Track> creaLista(Track preferita, int memoriaMassima){
		// calcoliamo la componente connessa di c
		ConnectivityInspector<Track, DefaultWeightedEdge> ci = new ConnectivityInspector<>(this.grafo);
		Set<Track> componenteConnessa = ci.connectedSetOf(preferita);
		
		List<Track> canzoniValide = new ArrayList<>();
		canzoniValide.add(preferita);
		componenteConnessa.remove(preferita);
		canzoniValide.addAll(componenteConnessa);
		
		List<Track> parziale = new ArrayList<>();
		best = new ArrayList<>();
		parziale.add(preferita);

		cerca(parziale, canzoniValide, memoriaMassima, 1);		
		
		return best;
	}
	
	private void cerca(List<Track> parziale, List<Track> canzoniValide, int memoriaMassima, int livello) {
		
		if (sommaBytes(parziale) > memoriaMassima)
			return;
		
		// se non esco, vuol dire che la soluzione parziale è ancora valida
		if (parziale.size() > best.size()) {
			best = new LinkedList<>(parziale);
		}
		
		if (livello == canzoniValide.size()) 
			return; // esco perchè non abbiamo più canzoni da poter aggiungere alla lista
		
		
		
		parziale.add(canzoniValide.get(livello));
		cerca(parziale, canzoniValide, memoriaMassima, livello+1);
		parziale.remove(canzoniValide.get(livello));
		cerca(parziale, canzoniValide, memoriaMassima, livello+1);
			
	}
	
//	public List<Track> creaLista(Track preferita, int memoriaMassima){
//		// calcoliamo la componente connessa di c
//		ConnectivityInspector<Track, DefaultWeightedEdge> ci = new ConnectivityInspector<>(this.grafo);
//		Set<Track> componenteConnessa = ci.connectedSetOf(preferita);
//		
//		List<Track> canzoniValide = new ArrayList<>();
//	
//		canzoniValide.addAll(componenteConnessa);
//		
//		List<Track> parziale = new ArrayList<>();
//		best = new ArrayList<>();
//		parziale.add(preferita);
//
//		cerca(parziale, canzoniValide, memoriaMassima);		
//		
//		return best;
//	}
//	
//	private void cerca(List<Track> parziale, List<Track> canzoniValide, int memoriaMassima) {
//		
//		if (parziale.size() > best.size()) {
//			best = new LinkedList<>(parziale);
//		}
//				
//		for (Track t : canzoniValide) {
//			if (!parziale.contains(t) && (sommaBytes(parziale) + t.getBytes()) <= memoriaMassima) {
//				parziale.add(t);
//				cerca(parziale, canzoniValide, memoriaMassima);
//				parziale.remove(parziale.size()-1);
//			}
//		}
//		
//	}
	
	private int sommaBytes(List<Track> parziale) {
		
		int somma = 0;
		
		for (Track t : parziale) {
			somma += t.getBytes();
		}
		
		return somma;
	}

	public List<Track> getAllTracks() {
		List<Track> canzoni = new LinkedList<>(this.grafo.vertexSet());
		Collections.sort(canzoni);
		return canzoni;
	}
	
	public List<Adiacenza> getCoppieConDeltaMassimo() {
		List<Adiacenza> result = new LinkedList<>();
		int massimo = 0;
		
		for (DefaultWeightedEdge edge : this.grafo.edgeSet()) {
			if (this.grafo.getEdgeWeight(edge) > massimo) {
				massimo = (int)this.grafo.getEdgeWeight(edge);
			}
		}
		
		for (DefaultWeightedEdge edge : this.grafo.edgeSet()) {
			if (this.grafo.getEdgeWeight(edge) == massimo) {
				result.add(new Adiacenza(this.grafo.getEdgeSource(edge), this.grafo.getEdgeTarget(edge), massimo));
			}
		}
		
		return result;
	}

	public List<Genre> getAllGenres() {
		return dao.getAllGenres();
	}
	
	public int getNumVertici() {
		return this.grafo.vertexSet().size();
	}

	public int getNumArchi() {
		return this.grafo.edgeSet().size();
	}

}
