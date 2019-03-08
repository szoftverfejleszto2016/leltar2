package leltar;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author Joe
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private TableView<Terem> tblTermek;

    @FXML
    private TableColumn<Terem, String> oTerem;

    @FXML
    private TableColumn<Terem, String> oFelhasznalas;

    @FXML
    private TextField txtTerem;

    @FXML
    private TextField txtFelhasznalas;

    @FXML
    private Label txtTeremUzenet;

    @FXML
    private TableView<Eszkoz> tblEszkozok;

    @FXML
    private TableColumn<Eszkoz, String> oNev;

    @FXML
    private TableColumn<Eszkoz, String> oJellemzok;

    @FXML
    private TextField txtNev;

    @FXML
    private TextField txtJellemzok;

    @FXML
    private ComboBox<String> cbxTerem;

    @FXML
    private ComboBox<String> cbxEszkoz;

    @FXML
    private TableView<Tetel> tblLeltar;

    @FXML
    private TableColumn<Tetel, Integer> oID;

    @FXML
    private TableColumn<Tetel, String> oTerem2;

    @FXML
    private TableColumn<Tetel, String> oEszkoz2;

    @FXML
    private TableColumn<Tetel, Integer> oAr;

    @FXML
    private TableColumn<Tetel, Integer> oEv;

    @FXML
    private TableColumn<Tetel, String> oMegjegyzes;
    
    @FXML
    private TextField txtMegjegyzes;
    
    @FXML
    private TextField txtAr;
    
    @FXML
    private TextField txtEv;
    
    @FXML
    void eszkoz_uj() {
        txtNev.setText("");
        txtJellemzok.setText("");
    }

    @FXML
    void eszkoz_hozzaad() {
        String nev = txtNev.getText().trim();
        if (nev.isEmpty()) {
            hiba("Hiba!", "Add meg at eszköz nevét!");
            return;
        }
        if (nev.length() > 50) {
            hiba("Hiba!", "Az eszköz neve legfeljebb 50 karakter lehet!");
            return;            
        }
        String jellemzok = txtJellemzok.getText().trim();
        if (jellemzok.length() > 255) {
            hiba("Hiba!", "A jellemzők hossza legfeljebb 255 karakter lehet!");
            return;            
        }
        
        String v = ab.eszkoz_hozzaad(nev, jellemzok);
        if (v.isEmpty()) {
            ab.eszkoz_be(tblEszkozok.getItems(), cbxEszkoz.getItems());
            for (int i = 0; i < tblEszkozok.getItems().size(); i++) {
                if (tblEszkozok.getItems().get(i).getNev().equals(nev)) {
                    tblEszkozok.getSelectionModel().select(i);
                    break;
                }
            }
        }
        else
            hiba("Hiba!",v);
    }

    @FXML
    void eszkoz_modosit() {
        int index = tblEszkozok.getSelectionModel().getSelectedIndex();
        if (index == -1)
            return;
        String nev = txtNev.getText().trim();
        if (nev.isEmpty()) {
            hiba("Hiba!", "Add meg at eszköz nevét!");
            return;
        }
        if (nev.length() > 50) {
            hiba("Hiba!", "Az eszköz neve legfeljebb 50 karakter lehet!");
            return;            
        }
        String jellemzok = txtJellemzok.getText().trim();
        if (jellemzok.length() > 255) {
            hiba("Hiba!", "A jellemzők hossza legfeljebb 255 karakter lehet!");
            return;            
        }
        int id = tblEszkozok.getItems().get(index).getEszkozid();

        String v = ab.eszkoz_modosit(id, nev, jellemzok);
        if (v.isEmpty()) {
            ab.eszkoz_be(tblEszkozok.getItems(), cbxEszkoz.getItems());
            for (int i = 0; i < tblEszkozok.getItems().size(); i++) {
                if (tblEszkozok.getItems().get(i).getEszkozid() == id) {
                    tblEszkozok.getSelectionModel().select(i);
                    break;
                }
            }
        }
        else
            hiba("Hiba!",v);        
    }

    @FXML
    void eszkoz_torol() {
        int index = tblEszkozok.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            return;
        }
        if (!igennem("Törlés","Biztosan törölni akarod ezt az eszközt?")) {
            return;
        }
        int id = tblEszkozok.getItems().get(index).getEszkozid();
        String v = ab.eszkoz_torol(id);
        if (v.isEmpty()) {
            ab.eszkoz_be(tblEszkozok.getItems(), cbxEszkoz.getItems());
        } else {
            hiba("Hiba!", v);
        }
    }

    @FXML
    void leltar_uj() {
        cbxTerem.setValue(null);
        cbxEszkoz.setValue(null);
        txtAr.setText("");
        txtEv.setText("");
        txtMegjegyzes.setText("");
    }

    private int get_teremid(String terem) {
        int i = 0;
        while (!tblTermek.getItems().get(i).getTerem().equals(terem))
            i++;
        return tblTermek.getItems().get(i).getTeremid();
    }
    
    private int get_eszkozid(String nev) {
        int i = 0;
        while (!tblEszkozok.getItems().get(i).getNev().equals(nev))
            i++;
        return tblEszkozok.getItems().get(i).getEszkozid();
    }

    @FXML
    void leltar_hozzaad() {
        int teremid = get_teremid(cbxTerem.getValue());
        int eszkozid = get_eszkozid(cbxEszkoz.getValue());
        Integer ar, ev;
        if (txtAr.getText().isEmpty())
            ar = null;
        else
            try {
                ar = Integer.parseInt(txtAr.getText());
                if (ar < 0) {
                    hiba("Hiba!", "Az ár nem lehet negatív!");
                    return;                    
                }
            } catch (NumberFormatException ex) {
                hiba("Hiba!", "Az ár nem szám!");
                return;
            }
        if (txtEv.getText().isEmpty())
            ev = null;
        else 
            try {
                ev = Integer.parseInt(txtEv.getText().trim());
                if (ev < 1980 || ev > LocalDate.now().getYear()) {
                    hiba("Hiba!", "Hibás év!");
                    return;
                }
            } catch (NumberFormatException ex) {
                hiba("Hiba!", "Az év nem szám!");
                return;
            }
        String megjegyzes = txtMegjegyzes.getText().trim();
        
        String v = ab.leltar_hozzaad(teremid, eszkozid, ar, ev, megjegyzes);
        if (v.isEmpty()) {
            ab.leltar_be(tblLeltar.getItems());
            int max = 0, hely = 0;
            for (int i = 0; i < tblLeltar.getItems().size(); i++) {
                int id = tblLeltar.getItems().get(i).getID();
                if (id > max) {
                    max = id;
                    hely = i;
                }
            }
            tblLeltar.getSelectionModel().select(hely);
        }
        else
            hiba("Hiba!",v);
    }

    @FXML
    void leltar_modosit() {
        int index = tblLeltar.getSelectionModel().getSelectedIndex();
        if (index == -1)
            return;
        int teremid = get_teremid(cbxTerem.getValue());
        int eszkozid = get_eszkozid(cbxEszkoz.getValue());
        Integer ar, ev;
        if (txtAr.getText().isEmpty())
            ar = null;
        else
            try {
                ar = Integer.parseInt(txtAr.getText());
                if (ar < 0) {
                    hiba("Hiba!", "Az ár nem lehet negatív!");
                    return;                    
                }
            } catch (NumberFormatException ex) {
                hiba("Hiba!", "Az ár nem szám!");
                return;
            }
        if (txtEv.getText().isEmpty())
            ev = null;
        else 
            try {
                ev = Integer.parseInt(txtEv.getText().trim());
                if (ev < 1980 || ev > LocalDate.now().getYear()) {
                    hiba("Hiba!", "Hibás év!");
                    return;
                }
            } catch (NumberFormatException ex) {
                hiba("Hiba!", "Az év nem szám!");
                return;
            }
        String megjegyzes = txtMegjegyzes.getText().trim();
        
        int leltarid = tblLeltar.getItems().get(index).getID();
        String v = ab.leltar_modosit(leltarid,teremid, eszkozid, ar, 
                                     ev, megjegyzes);
        if (v.isEmpty()) {
            ab.leltar_be(tblLeltar.getItems());
            for (int i = 0; i < tblLeltar.getItems().size(); i++) {
                if (tblLeltar.getItems().get(i).getID()== leltarid) {
                    tblLeltar.getSelectionModel().select(i);
                    break;
                }
            }
        }
        else
            hiba("Hiba!",v);
    }

    @FXML
    void leltar_torol() {
        int index = tblLeltar.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            return;
        }
        if (!igennem("Törlés","Biztosan törölni akarod ezt a tételt?")) {
            return;
        }
        int id = tblLeltar.getItems().get(index).getID();
        String v = ab.leltar_torol(id);
        if (v.isEmpty()) {
            ab.leltar_be(tblLeltar.getItems());
        } else {
            hiba("Hiba!", v);
        }        
    }

    @FXML
    void terem_uj() {
        txtTerem.setText("");
        txtFelhasznalas.setText("");        
    }

    @FXML
    void terem_hozzaad() {
        String terem = txtTerem.getText().trim();
        if (terem.isEmpty()) {
            hiba("Hiba!", "Add meg a terem azonosítóját!");
            return;
        }
        if (terem.length() > 4) {
            hiba("Hiba!", "A terem azonosítója legfeljebb 4 karakter lehet!");
            return;            
        }
        String felh = txtFelhasznalas.getText().trim();
        if (felh.length() > 30) {
            hiba("Hiba!", "A felhasználás legfeljebb 30 karakter lehet!");
            return;            
        }
        
        String v = ab.terem_hozzaad(terem, felh);
        if (v.isEmpty()) {
            ab.terem_be(tblTermek.getItems(), cbxTerem.getItems());
            for (int i = 0; i < tblTermek.getItems().size(); i++) {
                if (tblTermek.getItems().get(i).getTerem().equals(terem)) {
                    tblTermek.getSelectionModel().select(i);
                    break;
                }
            }
        }
        else
            hiba("Hiba!",v);
    }

    @FXML
    void terem_modosit() {
        int index = tblTermek.getSelectionModel().getSelectedIndex();
        if (index == -1)
            return;
        String terem = txtTerem.getText().trim();
        if (terem.isEmpty()) {
            hiba("Hiba!", "Add meg a terem azonosítóját!");
            return;
        }
        if (terem.length() > 4) {
            hiba("Hiba!", "A terem azonosítója legfeljebb 4 karakter lehet!");
            return;            
        }
        String felh = txtFelhasznalas.getText().trim();
        if (felh.length() > 30) {
            hiba("Hiba!", "A felhasználás legfeljebb 30 karakter lehet!");
            return;            
        }
        int id = tblTermek.getItems().get(index).getTeremid();

        String v = ab.terem_modosit(id, terem, felh);
        if (v.isEmpty()) {
            ab.terem_be(tblTermek.getItems(), cbxTerem.getItems());
            for (int i = 0; i < tblTermek.getItems().size(); i++) {
                if (tblTermek.getItems().get(i).getTeremid() == id) {
                    tblTermek.getSelectionModel().select(i);
                    break;
                }
            }            
        }
        else
            hiba("Hiba!",v);        
    }

    @FXML
    void terem_torol() {
        int index = tblTermek.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            return;
        }
        if (!igennem("Törlés","Biztosan törölni akarod ezt a termet?")) {
            return;
        }
        int id = tblTermek.getItems().get(index).getTeremid();
        String v = ab.terem_torol(id);
        if (v.isEmpty()) {
            ab.terem_be(tblTermek.getItems(), cbxTerem.getItems());
        } else {
            hiba("Hiba!", v);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        oTerem.setCellValueFactory(new PropertyValueFactory<>("terem"));
        oFelhasznalas.setCellValueFactory(new PropertyValueFactory<>("felhasznalas"));

        oNev.setCellValueFactory(new PropertyValueFactory<>("nev"));
        oJellemzok.setCellValueFactory(new PropertyValueFactory<>("jellemzok"));
        
        oID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        oTerem2.setCellValueFactory(new PropertyValueFactory<>("terem"));
        oEszkoz2.setCellValueFactory(new PropertyValueFactory<>("eszkoz"));
        oAr.setCellValueFactory(new PropertyValueFactory<>("ar"));
        oEv.setCellValueFactory(new PropertyValueFactory<>("ev"));
        oMegjegyzes.setCellValueFactory(new PropertyValueFactory<>("megjegyzes"));
        
        ab.terem_be(tblTermek.getItems(), cbxTerem.getItems());
        ab.eszkoz_be(tblEszkozok.getItems(), cbxEszkoz.getItems());
        ab.leltar_be(tblLeltar.getItems());
        
        tblTermek.getSelectionModel().selectedIndexProperty().addListener(
                (o,regi,uj) -> terem_tablabol(uj.intValue())
        );
        tblEszkozok.getSelectionModel().selectedIndexProperty().addListener(
                (o,regi,uj) -> eszkoz_tablabol(uj.intValue())
        );
        tblLeltar.getSelectionModel().selectedIndexProperty().addListener(
                (o,regi,uj) -> leltar_tablabol(uj.intValue())
        );
    }    
    
    DB ab = new DB();
    
    private void terem_tablabol(int i) {
        if (i == -1) return;
        Terem t = tblTermek.getItems().get(i);
        txtTerem.setText(t.getTerem());
        if (t.getFelhasznalas() == null)
            txtFelhasznalas.setText("");
        else
            txtFelhasznalas.setText(t.getFelhasznalas());
    }
    
    private void eszkoz_tablabol(int i) {
        if (i == -1) return;
        Eszkoz e = tblEszkozok.getItems().get(i);
        txtNev.setText(e.getNev());
        if (e.getJellemzok() == null)
            txtJellemzok.setText("");
        else
            txtJellemzok.setText(e.getJellemzok());
    }
    
    private void leltar_tablabol(int i) {
        if (i == -1) return;
        Tetel t = tblLeltar.getItems().get(i);
        cbxTerem.setValue(t.getTerem());
        cbxEszkoz.setValue(t.getEszkoz());
        if (t.getAr() != null)
            txtAr.setText(t.getAr().toString());
        else
            txtAr.setText("");
        if (t.getEv() != null)
            txtEv.setText(t.getEv().toString());
        else
            txtEv.setText("");
        if (t.getMegjegyzes() != null)
            txtMegjegyzes.setText(t.getMegjegyzes());
        else
            txtMegjegyzes.setText("");
    }

    private void hiba(String cim, String uzenet) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(cim);
        alert.setHeaderText(null);
        alert.setContentText(uzenet);
        alert.showAndWait();            
    }
    
    private boolean igennem(String cim, String uzenet) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(cim);
        alert.setHeaderText(null);
        alert.setContentText(uzenet);
        ButtonType btIgen = new ButtonType("Igen");
        ButtonType btNem = new ButtonType("Nem");
        alert.getButtonTypes().setAll(btIgen, btNem);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == btIgen;
    }
}
