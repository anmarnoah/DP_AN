import java.sql.*;
import java.util.List;
import java.util.Properties;



public class Main {
    public static void main(String[] args) throws SQLException {

        String url = "jdbc:postgresql://localhost/ovchip";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "root");
        Connection conn = DriverManager.getConnection(url, props);

        // DAOsql aanmaken
        ReizigerDAOsql reizigerDAOsql = new ReizigerDAOsql(conn);
        AdresDAOsql adresDAOsql = new AdresDAOsql(conn, reizigerDAOsql);
        reizigerDAOsql.setAdresDAO(adresDAOsql);

        OVChipkaartDAOsql ovChipkaartDAOsql = new OVChipkaartDAOsql(conn);
        ovChipkaartDAOsql.setReizigerDAO(reizigerDAOsql);
        reizigerDAOsql.setOvChipkaartDAO(ovChipkaartDAOsql);
        testReizigerDAO(reizigerDAOsql);
        System.out.println();


        System.out.println("Alle Adressen:");
        for (Adres adres : adresDAOsql.findAll()) {
            System.out.println(adres);
        }

        System.out.println("\nAlle Reizigers:");
        for (Reiziger reiziger : reizigerDAOsql.findAll()) {
            System.out.println(reiziger);
        }

        System.out.println("\nAlle OVChipkaarten:");
        for (OVChipkaart ovchipkaart : ovChipkaartDAOsql.findAll()) {
            System.out.println(ovchipkaart);
        }

    }

    private static void testReizigerDAO(ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        // Haal alle reizigers op uit de database
        List<Reiziger> reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();

        // Maak een nieuwe reiziger aan en persisteer deze in de database
        String gbdatum = "1981-03-14";
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", java.sql.Date.valueOf(gbdatum));
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        // Voeg aanvullende tests van de ontbrekende CRUD-operaties in.

        String naam_eerst = sietske.getAchternaam();
        sietske.setAchternaam("Bob");
        rdao.update(sietske);
        String naam_na = sietske.getAchternaam();
        System.out.print("[Test] Achternaam eerst: " + naam_eerst + ", na ReizigerDAO.update(): " + naam_na + "\n\n");

        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.delete() ");
        rdao.delete(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");


        //
        Reiziger reiziger1 = rdao.findAll().get(0);
        System.out.println("[Test] Reiziger 1: " + reiziger1.toString());
        Reiziger reiziger2 = rdao.findByGbdatum(reiziger1.getGeboortedatum());
        System.out.println("       findByGbdatum Reiziger2: " + reiziger2.toString());
        System.out.println("       Reiziger1.equals(Reiziger2): " + reiziger1.equals(reiziger2));
        System.out.println("\n---------- END Test ReizigerDAO -------------");
    }
}