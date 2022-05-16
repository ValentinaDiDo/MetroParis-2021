package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	Graph<Fermata, DefaultEdge> grafo;
	Map<Fermata, Fermata> predecessore;
	public void creaGrafo() {
		this.grafo = new SimpleGraph<Fermata, DefaultEdge>(DefaultEdge.class);
		
		//DEVO POI AGGIUNGERE I VERTICI E GLI ARCHI AL GRAFO
		
		//1. I VERTICI SONO LE FERMATE -> HO GIA' UN METODO NEL DAO CHE ME LE DA
		MetroDAO dao = new MetroDAO();
		List<Fermata> fermate = dao.getAllFermate();
		
		//AGGIUNGO FERMATE AL GRAFO: METODO 1
		/*for(Fermata f : fermate) {
			this.grafo.addVertex(f);
			}*/
		
		//AGGIUNGO FERMATE AL GRAFO: METODO 2 -> MOLTO PIU' CORTO USO LIBRERIA GRAPHS CHE HA TANTI METODI UTILI PER LAVORARE CON I GRAFI(HA SOLO METODI STATICI QUINDI DEVO PASSARGLI UL GRAFO COME PARAMETRO) 
		Graphs.addAllVertices(this.grafo, fermate);
		
		
		//ORA BISOGNA AGGIUNGERE GLI ARCHI -> COPPIE DI STAZIONI
		
		//METODO 1: TEMPO INFINITO
		/*for(Fermata f1: this.grafo.vertexSet()) {
			for(Fermata f2: this.grafo.vertexSet()) {
				if(!f1.equals(f2) && dao.fermateCollegate(f1, f2)) {
					this.grafo.addEdge(f1, f2) ;
  			  }
			}
		}*/
		
		
		//METODO 2: IL DAO MI DA UNA LISTA DI CONNESSIONI CHE POSSO TRADURRE IN ARCHI
		List<Connessione> connessioni = dao.getAllConnessioni(fermate);
		for(Connessione c : connessioni) {
			this.grafo.addEdge(c.getStazP(), c.getStazA());
		}
		//System.out.println(this.grafo);
	
	}
		/* Fermata f = null;
		 Set<DefaultEdge> archi = this.grafo.edgesOf(f);
		 for(DefaultEdge e : archi) {
			 Fermata f1 = this.grafo.getEdgeSource(e);
			 Fermata f2 = this.grafo.getEdgeTarget(e);
			 if(f1.equals(f)) {
				 //E' LA FERMATA CHE MI SERVE
			 }else{
				 //MI SERVE F2
			 }*/
			// Graphs.getOppositeVertex(this.grafo, e, f); //MI RITORNA IL VERTICE OPPOSTO AD F
		//	 List<Fermata> fermateAdiacenti = Graphs.successorListOf(this.grafo, f); //RITORNA UNA LISTA DI SUCCESSORI DIRETTI DEL VERTICE PASSATO COME PARAMETRO
		 	
			 //VERTICE ADIACENTE != VERTICE RAGGIUNGIBILE
			 /*PER OTTENERE I VERTICI RAGGIUNGIBILI DEVO EFFETTUARE UNA VISITA DEL GRAFO
			  
			 USO METODO : BREATH FIRST ITERATOR
			 
			 DEPTH FIST VISIT / DEPTH FIRST SEARCH: 	COMPLESSITA' LINEARE
			 
			  - ALBERO DI VISITA IN AMPIEZZA: DA I CAMMINI MINIMI IN GRAFI NON PESATI
			 		ESPLORO A STRATI CONCENTRICI: LIVELLO 0, LIVELLO 1, ... , LIVELLO N
			 		
			  -	ALBERO DI VISITA IN PROFONDITA': LASCIA IN SOSPESO MOLTI VERTICI -> PARTENDO DA UN VERTICE, VEDO QUELLO ADIACENTE,
			 		POI QUELLI ADIACENTI E COSI' VIA
			 
			  IN OGNI CASO DEVO MEMORIZZARE QUALI SONO I VERTICI CHE HO GIA' VISITATO (PARZIALE RICORSIVO)
			  AD OGNI PASSO MI CHIEDO SE DAL VERTICE V CI SONO ALTRI VERTICI ADIACENTI DA VISITARE -> SE LI HO FINITI PASSO AL VERTICE SUCCESSIVO
			 
			 
			  IMPLEMENTO QUESTI METODI CON org.jgrapht.traverse
			  
			  INIZIALIZZO UN ITERATORE IMPOSTANDO UN VERTICE DI PARTENZA E AD OGNI CHIAMATA DI NEXT() OTTENGO UN NUOVO VERTICE
			  SCOPERTO DALL'ARLGORITMO DI VISITA STESSO. QUANDO L'ALGORITMO NON TROVA PIù NULLA, hasNext() RITORNA FALSO 	
			  */
			 
			 
	
			 public List<Fermata> fermateRaggiungibili(Fermata partenza){
				 List<Fermata> result = new ArrayList<Fermata>();
				 //DEFINISCO ITERATORE IN AMPIEZZA
				 BreadthFirstIterator<Fermata,DefaultEdge> bfv = new BreadthFirstIterator<>(this.grafo,partenza);
				 Fermata precedente;
				 
				 predecessore = new HashMap<>();
				 this.predecessore.put(partenza, null);
				  
				 bfv.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>(){

					 
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
					// INFO SULL' ARCO -> VIENE CHIAMATO OGNI VOLTA IN CUI  VIENE ATTRAVERSATO UN ARCO (MEANS CHE HO ANCHE ATTRAVERSATO UN VERTICE)
						DefaultEdge arco = e.getEdge();
						Fermata a = grafo.getEdgeSource(arco);
						Fermata b = grafo.getEdgeTarget(arco);
						//CASO 1: HO SCOPERTO A ARRIVANDO DA B (SE CONOSCEVO GIA' B)
						if(predecessore.containsKey(b) && !predecessore.containsKey(a)) { //SE DOVESSI AVERLI TUTTI E DUE, ALLA MAPPA NON VIENE AGGIUNTO NIENTE
							//avevo già visitato b
							predecessore.put(a, b);
						//	System.out.println(a+" Scoperto da "+b);
						}else if(!predecessore.containsKey(b) && predecessore.containsKey(a)) {
							//non conoscevo b ma conoscevo a, e ora ho scoperto b
							predecessore.put(a, b);
						//	System.out.println(b+" Scoperto da "+a);
						}
					}

					@Override
					public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
//						System.out.println(e.getVertex()); //stampa il vertice che viene scoperto
//						Fermata nuova = e.getVertex();
//						Fermata precedente;
//						predecessore.put(nuova,precedente);
						
					}

					@Override
					public void vertexFinished(VertexTraversalEvent<Fermata> e) {
						// TODO Auto-generated method stub
						
					}
					 
				 });
				 //nel costrutture: grafo, grafo + vertice di partenza, oppure grafo + lista di vertici adiacenti
				 
				 while(bfv.hasNext()) { //FINCHE' HASNEXT E' VERO HO NUOVI VERTICI DA SCOPRIRE
					 Fermata f = bfv.next();
					 result.add(f);
				 } 
				 return result;
			 }
			 
			 //VA BENE SE DEVO FARLO POCHE VOLTE -> SENNO' USO UNA MAPPA
			 public Fermata trovaFermata(String nome) {
				 for(Fermata f : this.grafo.vertexSet()) {
					 if(f.getNome().equals(nome))
						 return f;
				 }
				 return null;
			 }
			 
			 /* L'ITERATORE MI DA SOLO L'ELENCO DI FERMATE CHE POSSO RAGGIUNGERE, MA NON MI DICE COME ARRIVARCI 
			    -> MENTRE L'ITERATORE VA AVANTI SALVO IN UNA MAPPA IL VERTICE SCOPERTO E IL VALORE DA CUI E' STATO SCOPERTO
			    
			  USERO' L'ITERATORE:  TraversalListener: E' UN INTERFACCIA CHE DEVO IMPLEMENTARE IN UN'ALTRA CLASSE
			  
			  */
			 
			 public List<Fermata> trovaCammino(Fermata partenza, Fermata arrivo){
				 fermateRaggiungibili(partenza);
				 List<Fermata> result = new ArrayList<>();
				 result.add(arrivo);
				 Fermata f = arrivo;
				 while(predecessore.get(f)!=null) {
					 f = (predecessore.get(f)); //finché f ha un predecessore aggiungo questo al cammino per arrivare all'arrivo partendo dalla partenza 
					 //USO F PERCHE' IL VALORE DEVE AGGIORNARSI DURANTE IL WHILE
					 result.add(0,f);
					 //AGGIUNGO L'ELEMENTO IN TESTA ANZICHE' IN CODA, IN MODO TALE DA AVERE IL CAMMINO INVERSO, CHE PERO' E' L'ORDINE GIUSTO
					 //IN QUESTO MODO AVRO' IL CAMMINO DA PARTENZA->ARRIVO ANZICHE' ARRIVO->PARTENZA
				 }
				 return result;
			 }
			 	 
	}
	

