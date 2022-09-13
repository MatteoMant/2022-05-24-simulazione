/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.itunes;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.itunes.model.Adiacenza;
import it.polito.tdp.itunes.model.Genre;
import it.polito.tdp.itunes.model.Model;
import it.polito.tdp.itunes.model.Track;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	private Model model;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnCreaLista"
    private Button btnCreaLista; // Value injected by FXMLLoader

    @FXML // fx:id="btnMassimo"
    private Button btnMassimo; // Value injected by FXMLLoader

    @FXML // fx:id="cmbCanzone"
    private ComboBox<Track> cmbCanzone; // Value injected by FXMLLoader

    @FXML // fx:id="cmbGenere"
    private ComboBox<Genre> cmbGenere; // Value injected by FXMLLoader

    @FXML // fx:id="txtMemoria"
    private TextField txtMemoria; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void btnCreaLista(ActionEvent event) {

    	int memoriaMassima;
    	
    	try {
    		memoriaMassima = Integer.parseInt(txtMemoria.getText());
    	} catch (NumberFormatException e) {
    		txtResult.appendText("Per favore inserire un numero valido di capacità!\n");
    		return;
    	}
    	
    	Track preferita = cmbCanzone.getValue();
    	if (preferita == null) {
    		txtResult.appendText("Per favore selezionare una canzone preferita dalla tendina!\n");
    		return;
    	}
    	
    	List<Track> listaCanzoni = this.model.creaLista(preferita, memoriaMassima);
    	txtResult.appendText("\n\nLa lista di canzoni trovata è: \n");
    	if (listaCanzoni == null) {
    		txtResult.appendText("Non è stata trovata alcuna lista!");
    		return;
    	} else {
	    	for (Track t : listaCanzoni) {
	    		txtResult.appendText(t + "\n");
	    	}
    	}
    	
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	cmbCanzone.getItems().clear();
    	
    	Genre genere = cmbGenere.getValue();
    	if (genere == null) {
    		txtResult.appendText("Per favore selezionare un genere dalla tendina!\n");
    		return;
    	}
    	
    	this.model.creaGrafo(genere);
    	txtResult.setText("Grafo creato!\n");
    	txtResult.appendText("# Vertici : " + this.model.getNumVertici() + "\n");
    	txtResult.appendText("# Archi : " + this.model.getNumArchi() + "\n");
    	
    	// Dopo che creo il grafo, posso popolare il menu a tendina delle canzoni presenti nel grafo stesso
    	cmbCanzone.getItems().addAll(this.model.getAllTracks());
    }

    @FXML
    void doDeltaMassimo(ActionEvent event) {
    	List<Adiacenza> coppieDeltaMassimo = this.model.getCoppieConDeltaMassimo();
    	txtResult.appendText("\nCOPPIA CANZONI DELTA MASSIMO: \n");
    	for (Adiacenza a : coppieDeltaMassimo) {
    		txtResult.appendText(a.getT1().getName() + " *** " + a.getT2().getName() + " --> " + a.getPeso());
    	}
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaLista != null : "fx:id=\"btnCreaLista\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnMassimo != null : "fx:id=\"btnMassimo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbCanzone != null : "fx:id=\"cmbCanzone\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbGenere != null : "fx:id=\"cmbGenere\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtMemoria != null : "fx:id=\"txtMemoria\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	
    	cmbGenere.getItems().addAll(this.model.getAllGenres());
    }

}
