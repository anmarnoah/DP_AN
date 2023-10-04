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
        // # ------------------------------------------------------------------ #
        ReizigerDAOsql reizigerDAOsql = new ReizigerDAOsql(conn);
        AdresDAOsql adresDAOsql = new AdresDAOsql(conn, reizigerDAOsql);
        reizigerDAOsql.setAdresDAO(adresDAOsql);

        OVChipkaartDAOsql ovChipkaartDAOsql = new OVChipkaartDAOsql(conn, reizigerDAOsql);
        reizigerDAOsql.setOvChipkaartDAO(ovChipkaartDAOsql);

        ProductDAOsql productDAOsql = new ProductDAOsql(conn, ovChipkaartDAOsql);
        ovChipkaartDAOsql.setProductDAO(productDAOsql);
        // # ------------------------------------------------------------------ #

        // Tests
        //testReizigerDAO(reizigerDAOsql);
        testProductDAO(productDAOsql, ovChipkaartDAOsql, reizigerDAOsql);


//        System.out.println("Alle Adressen:");
//        for (Adres adres : adresDAOsql.findAll()) {
//            System.out.println(adres);
//        }
//
//        System.out.println("\nAlle Reizigers:");
//        for (Reiziger reiziger : reizigerDAOsql.findAll()) {
//            System.out.println(reiziger);
//        }
//
//        System.out.println("\nAlle OVChipkaarten:");
//        for (OVChipkaart ovchipkaart : ovChipkaartDAOsql.findAll()) {
//            System.out.println(ovchipkaart);
//        }
//
//        System.out.println("\nAlle Producten:");
//        for (Product product : productDAOsql.findAll()) {
//            System.out.println(product);
//        }
    }

    private static void testProductDAO(ProductDAO pdao, OVChipkaartDAO ovdao, ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test ProductDAO -------------");

        List<Product> producten = pdao.findAll();
        System.out.println("[Test] productDAO.findAll() geeft de volgende producten:");
        for (Product product : producten) {
            System.out.println("    " + product);
        }
        System.out.println();

        // Maak een nieuwe product aan en persisteer deze in de database
        Product product = new Product(999, "VoorbeeldProduct", "Dit is een voorbeeld product", 12.3);
        System.out.print("[Test] Eerst " + producten.size() + " producten, na ProductDAO.save(): ");
        try {
            pdao.save(product);
        } catch (SQLException ignored) {}
        producten = pdao.findAll();
        System.out.println(producten.size() + " producten\n");


        //Test update
        Reiziger reiziger1 = rdao.findById(1);
        OVChipkaart ovChipkaart1 = new OVChipkaart(900, Date.valueOf("2030-01-01"), 1, 5000, reiziger1);
        OVChipkaart ovChipkaart2 = new OVChipkaart(901, Date.valueOf("2040-12-31"), 2, 3000, reiziger1);
        try {
            ovdao.save(ovChipkaart1);
            ovdao.save(ovChipkaart2);
        } catch (SQLException ignored) {}

        product.addOvChipkaart(ovChipkaart1);
        product.addOvChipkaart(ovChipkaart2);

        System.out.println("[i] We maken 2 OVChipkaarten aan en voegen deze toe aan het voorbeeld Product");
        System.out.println("[Test] Product voor ProductDAO.update() met ProductDAO.findByProductNummer(): ");
        System.out.println("    " + pdao.findByProductNummer(product.getProduct_nummer()));
        System.out.println();

        pdao.update(product);
        System.out.println("[Test] Product na ProductDAO.update() met ProductDAO.findByProductNummer(): ");
        System.out.println("    " + pdao.findByProductNummer(product.getProduct_nummer()));
        System.out.println();

        product.removeOvChipkaart(ovChipkaart2);
        pdao.update(product);
        System.out.println("[i] We halen nu 1 OVChipkaart weg van het voorbeeld Product");
        System.out.println("[Test] Product na ProductDAO.update() met ProductDAO.findByProductNummer(): ");
        System.out.println("    " + pdao.findByProductNummer(product.getProduct_nummer()));
        System.out.println();


        System.out.println("[i] We zoeken nu het product op met behulp van de overgebleven OVChipkaart");
        System.out.println("[Test] Product met ProductDAO.findByOVChipkaart(): ");
        System.out.println("    " + pdao.findByOVChipkaart(ovChipkaart1));
        System.out.println();


        System.out.print("[Test] Eerst " + producten.size() + " producten, na ProductDAO.delete(): ");
        pdao.delete(product);
        ovdao.delete(ovChipkaart1);
        ovdao.delete(ovChipkaart2);
        System.out.println(pdao.findAll().size() + " producten");

        System.out.println("\n---------- END Test ProductDAO -------------");
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
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save(): ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        // Voeg aanvullende tests van de ontbrekende CRUD-operaties in.

        String naam_eerst = sietske.getAchternaam();
        sietske.setAchternaam("Bob");
        rdao.update(sietske);
        String naam_na = sietske.getAchternaam();
        System.out.print("[Test] Achternaam eerst: " + naam_eerst + ", na ReizigerDAO.update(): " + naam_na + "\n\n");

        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.delete(): ");
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