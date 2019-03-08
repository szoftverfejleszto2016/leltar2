package leltar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.ObservableList;

/**
 *
 * @author Joe
 */
public class DB {
    final String db = "jdbc:mysql://localhost:3306/nyilvantartas" +
                      "?useUnicode=true&characterEncoding=UTF-8";
    final String user = "raktaros";		
    final String pass = "raktaros";
    
    public void terem_be(ObservableList<Terem> tabla, ObservableList<String> lista) {
        String s = "SELECT * FROM termek ORDER BY terem;";

        try (Connection kapcs = DriverManager.getConnection(db, user, pass);
             PreparedStatement ekp = kapcs.prepareStatement(s)) {
            ResultSet eredmeny = ekp.executeQuery();
            tabla.clear();
            lista.clear();
            while (eredmeny.next()) {
                tabla.add(new Terem(
                    eredmeny.getInt("teremid"),
                    eredmeny.getString("terem"),
                    eredmeny.getString("felhasznalas")
                ));
                lista.add(eredmeny.getString("terem"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }    
    
    public void eszkoz_be(ObservableList<Eszkoz> tabla, 
                          ObservableList<String> lista) {
        String s = "SELECT * FROM eszkozok ORDER BY nev;";

        try (Connection kapcs = DriverManager.getConnection(db, user, pass);
             PreparedStatement ekp = kapcs.prepareStatement(s)) {
            ResultSet eredmeny = ekp.executeQuery();
            tabla.clear();
            lista.clear();
            while (eredmeny.next()) {
                tabla.add(new Eszkoz(
                    eredmeny.getInt("eszkozid"),
                    eredmeny.getString("nev"),
                    eredmeny.getString("jellemzok")
                ));
                lista.add(eredmeny.getString("nev"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }    
    
    public void leltar_be(ObservableList<Tetel> tabla) {
        String s = "SELECT leltarid,terem,nev,ar,ev,megjegyzes "
                 + "FROM leltar "
                 + "JOIN eszkozok ON leltar.eszkozid=eszkozok.eszkozid "
                 + "JOIN termek ON leltar.teremid=termek.teremid "
                +  "ORDER BY terem, nev, leltarid;";

        try (Connection kapcs = DriverManager.getConnection(db, user, pass);
             PreparedStatement ekp = kapcs.prepareStatement(s)) {
            ResultSet eredmeny = ekp.executeQuery();
            tabla.clear();
            while (eredmeny.next()) {
                Tetel t = new Tetel();
                t.setID(eredmeny.getInt("leltarid"));
                t.setTerem(eredmeny.getString("terem"));
                t.setEszkoz(eredmeny.getString("nev"));
                int ar = eredmeny.getInt("ar");
                if (!eredmeny.wasNull()) 
                    t.setAr(ar);
                int ev = eredmeny.getInt("ev");
                if (!eredmeny.wasNull()) 
                    t.setEv(ev);
                t.setMegjegyzes(eredmeny.getString("megjegyzes"));                
                tabla.add(t);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }    
    
    public String terem_hozzaad(String terem, String felh) {
        String s = "INSERT INTO termek (terem, felhasznalas) "
                 + "VALUES(?,?);";

        try (Connection kapcs = DriverManager.getConnection(db, user, pass);
             PreparedStatement ekp = kapcs.prepareStatement(s)) {
            ekp.setString(1, terem);
            if (felh.equals("")) 
                ekp.setNull(2, java.sql.Types.VARCHAR);
            else 
                ekp.setString(2, felh);            
            ekp.executeUpdate();
            return "";
        } catch (SQLException ex) {
            return ex.getMessage();
        }
    }    

    public String eszkoz_hozzaad(String nev, String jellemzok) {
        String s = "INSERT INTO eszkozok (nev, jellemzok) "
                 + "VALUES(?,?);";

        try (Connection kapcs = DriverManager.getConnection(db, user, pass);
             PreparedStatement ekp = kapcs.prepareStatement(s)) {
            ekp.setString(1, nev);
            if (jellemzok.equals("")) 
                ekp.setNull(2, java.sql.Types.VARCHAR);
            else 
                ekp.setString(2, jellemzok);            
            ekp.executeUpdate();
            return "";
        } catch (SQLException ex) {
            return ex.getMessage();
        }
    }    

    public String leltar_hozzaad(int teremid, int eszkozid, 
                                 Integer ar, Integer ev, String megjegyzes) {
        String s = "INSERT INTO leltar (teremid, eszkozid, ar, ev, megjegyzes) "
                 + "VALUES(?,?,?,?,?);";
        
        try (Connection kapcs = DriverManager.getConnection(db, user, pass);
             PreparedStatement ekp = kapcs.prepareStatement(s)) {
            
            ekp.setInt(1, teremid);
            ekp.setInt(2, eszkozid);
            if (ar == null)
                ekp.setNull(3, java.sql.Types.INTEGER);
            else 
                ekp.setInt(3, ar);            
            if (ev == null)
                ekp.setNull(4, java.sql.Types.INTEGER);
            else 
                ekp.setInt(4, ev);
            if (megjegyzes == null)
                ekp.setNull(5, java.sql.Types.VARCHAR);
            else 
                ekp.setString(5, megjegyzes);            
            ekp.executeUpdate();
            return "";
        } catch (SQLException ex) {
            return ex.getMessage();
        }
    }    

    public String terem_modosit(int teremid, String terem, String felh) {
        String s = "UPDATE termek SET terem=?, felhasznalas=? "
                 + "WHERE teremid=?";
        try (Connection kapcs = DriverManager.getConnection(db, user, pass);
                PreparedStatement ekp = kapcs.prepareStatement(s)) {
            ekp.setString(1, terem);
            if (felh.isEmpty())
                ekp.setNull(2, java.sql.Types.VARCHAR);
            else
                ekp.setString(2, felh);
            ekp.setInt(3, teremid);
            ekp.executeUpdate();
            return "";
        } catch (SQLException ex) {
            return ex.getMessage();
        }
    }
    
    public String eszkoz_modosit(int eszkozid, String nev, String jellemzok) {
        String s = "UPDATE eszkozok SET nev=?, jellemzok=? "
                 + "WHERE eszkozid=?";
        try (Connection kapcs = DriverManager.getConnection(db, user, pass);
                PreparedStatement ekp = kapcs.prepareStatement(s)) {
            ekp.setString(1, nev);
            if (jellemzok.isEmpty())
                ekp.setNull(2, java.sql.Types.VARCHAR);
            else
                ekp.setString(2, jellemzok);
            ekp.setInt(3, eszkozid);
            ekp.executeUpdate();
            return "";
        } catch (SQLException ex) {
            return ex.getMessage();
        }
    }
    
    public String leltar_modosit(int leltarid, int teremid, int eszkozid, 
                          Integer ar, Integer ev, String megjegyzes) {
        String s = "UPDATE leltar SET teremid=?, eszkozid=?, ar=?, ev=?, "
                 + "megjegyzes=? WHERE leltarid=?;";
        
        try (Connection kapcs = DriverManager.getConnection(db, user, pass);
             PreparedStatement ekp = kapcs.prepareStatement(s)) {
            
            ekp.setInt(1, teremid);
            ekp.setInt(2, eszkozid);
            if (ar == null)
                ekp.setNull(3, java.sql.Types.INTEGER);
            else 
                ekp.setInt(3, ar);            
            if (ev == null)
                ekp.setNull(4, java.sql.Types.INTEGER);
            else 
                ekp.setInt(4, ev);
            if (megjegyzes == null)
                ekp.setNull(5, java.sql.Types.VARCHAR);
            else 
                ekp.setString(5, megjegyzes);
            ekp.setInt(6, leltarid);
            ekp.executeUpdate();
            return "";
        } catch (SQLException ex) {
            return ex.getMessage();
        }
    }    

    public String terem_torol(int teremid) {
        String s = "DELETE FROM termek WHERE teremid=?;";
        try (Connection kapcs = DriverManager.getConnection(db, user, pass);
                PreparedStatement ekp = kapcs.prepareStatement(s)) {
            ekp.setInt(1, teremid);
            ekp.executeUpdate();
            return "";
        } catch (SQLException ex) {
            return ex.getMessage();
        }                
    }
   
    public String eszkoz_torol(int eszkozid) {
        String s = "DELETE FROM eszkozok WHERE eszkozid=?;";
        try (Connection kapcs = DriverManager.getConnection(db, user, pass);
                PreparedStatement ekp = kapcs.prepareStatement(s)) {
            ekp.setInt(1, eszkozid);
            ekp.executeUpdate();
            return "";
        } catch (SQLException ex) {
            return ex.getMessage();
        }                
    }

    public String leltar_torol(int leltarid) {
        String s = "DELETE FROM leltar WHERE leltarid=?;";
        try (Connection kapcs = DriverManager.getConnection(db, user, pass);
                PreparedStatement ekp = kapcs.prepareStatement(s)) {
            ekp.setInt(1, leltarid);
            ekp.executeUpdate();
            return "";
        } catch (SQLException ex) {
            return ex.getMessage();
        }                
    }

}
