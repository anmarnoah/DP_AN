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
        ReizigerDAOPsql reizigerDAOPsql = new ReizigerDAOPsql(conn);
        AdresDAOPsql adresDAOPsql = new AdresDAOPsql(conn, reizigerDAOPsql);
        reizigerDAOPsql.setAdresDAO(adresDAOPsql);

        OVChipkaartDAOPsql ovChipkaartDAOPsql = new OVChipkaartDAOPsql(conn, reizigerDAOPsql);
        reizigerDAOPsql.setOvChipkaartDAO(ovChipkaartDAOPsql);

        ProductDAOPsql productDAOPsql = new ProductDAOPsql(conn, ovChipkaartDAOPsql);
        ovChipkaartDAOPsql.setProductDAO(productDAOPsql);
        // # ------------------------------------------------------------------ #

        // Tests
        //testReizigerDAO(reizigerDAOPsql);
        //testProductDAO(productDAOPsql, ovChipkaartDAOPsql, reizigerDAOPsql);
        //testAdresDAO(adresDAOPsql, reizigerDAOPsql);
        testOVChipkaartDao(ovChipkaartDAOPsql, reizigerDAOPsql);


//        System.out.println("Alle Adressen:");
//        for (Adres adres : adresDAOPsql.findAll()) {
//            System.out.println(adres);
//        }
//
//        System.out.println("\nAlle Reizigers:");
//        for (Reiziger reiziger : reizigerDAOPsql.findAll()) {
//            System.out.println(reiziger);
//        }
//
//        System.out.println("\nAlle OVChipkaarten:");
//        for (OVChipkaart ovchipkaart : ovChipkaartDAOPsql.findAll()) {
//            System.out.println(ovchipkaart);
//        }
//
//        System.out.println("\nAlle Producten:");
//        for (Product product : productDAOPsql.findAll()) {
//            System.out.println(product);
//        }
    }

    private static void testAdresDAO(AdresDAO adao, ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test AdresDAO -------------");

        List<Adres> adressen = adao.findAll();
        System.out.println("[Test] AdresDao.findAll() geeft de volgende producten:");
        for (Adres adres : adressen) {
            System.out.println("    " + adres);
        }
        System.out.println();

        System.out.print("\n![i] Om een Reiziger te koppelen aan het adres maken we een nieuwe Reiziger aan met id 54321 die we ook opslaan\n!");
        Reiziger reiziger = new Reiziger(54321, "AN", null, "Ram", Date.valueOf("2002-08-28"));
        rdao.save(reiziger);

        // Maak een nieuwe adres aan en persisteer deze in de database
        Adres adres = new Adres(12345, "3581WB", "11","Bloemstraat", "Utrecht", reiziger);
        System.out.print("[Test] Eerst " + adressen.size() + " adressen, na AdresDAO.save(): ");
        try {
            adao.save(adres);
        } catch (SQLException ignored) {}
        adressen = adao.findAll();
        System.out.println(adressen.size() + " adressen\n");

        // .update() testen
        System.out.println("[Test] Adres voor AdresDAO.update(): ");
        System.out.println(adres);
        System.out.println("\n[Test] Adres na AdresDAO.update() met AdresDAO.findById(): ");
        adres.setStraat("nieuwestraat");
        adao.update(adres);
        System.out.println(adao.findById(adres.getId()));

        System.out.println("\n[Test] Adres met AdresDAO.findByReiziger(): ");
        System.out.println(adao.findByReiziger(reiziger));

        // .delete testen
        System.out.println("\n[Test] Eerst " + adressen.size() + " adressen, na AdresDAO.delete(): ");
        adao.delete(adres);
        rdao.delete(reiziger);
        adressen = adao.findAll();
        System.out.print(adressen.size() + " adressen\n");
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

    public static void testOVChipkaartDao(OVChipkaartDAO odao, ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test OVChipkaartDAO -------------");
        // Haal alle ovchipkaarten op uit de database
        List<OVChipkaart> ovchipkaarten = odao.findAll();
        System.out.println("[Test] OVChipkaartDAO.findAll() geeft de volgende OVChipkaarten:");
        for (OVChipkaart ovChipkaart : ovchipkaarten) {
            System.out.println(ovChipkaart);
        }
        System.out.println();

        System.out.print("\n[i] Om een Reiziger te koppelen aan de OVChipkaart maken we een nieuwe Reiziger aan met id 54321 die we ook opslaan\n");
        Reiziger reiziger = new Reiziger(54321, "AN", null, "Ram", Date.valueOf("2002-08-28"));
        rdao.save(reiziger);

        OVChipkaart ovChipkaart = new OVChipkaart(55555, Date.valueOf("2030-01-01"), 1, 999, reiziger);
        System.out.print("[Test] Eerst " + ovchipkaarten.size() + " OVChipkaarten, na OVChipkaartDAO.save(): ");
        odao.save(ovChipkaart);
        ovchipkaarten = odao.findAll();
        System.out.println(ovchipkaarten.size() + " OVChipkaarten\n");

        System.out.println("[i] We veranderen het saldo van de OVChipkaart om OVChipkaartDAO.update() te testen");
        System.out.println("[Test] OVChipkaart eerst met OVChipkaartDAO.findByNummer(): \n" + odao.findByNummer(ovChipkaart.getNummer()) + "\n");
        ovChipkaart.setSaldo(444);
        odao.update(ovChipkaart);
        System.out.println("[Test] OVChipkaart na OVChipkaartDAO.update() met ReizigerDAO.findByReiziger(): \n" + odao.findByReiziger(reiziger).get(0) + "\n");

        System.out.print("[Test] Eerst " + ovchipkaarten.size() + " OVChipkaarten, na OVChipkaartDAO.delete(): ");
        odao.delete(ovChipkaart);
        rdao.delete(reiziger);
        System.out.println(odao.findAll().size() + " OVChipkaarten");
    }
}