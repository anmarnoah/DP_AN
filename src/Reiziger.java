import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Reiziger {

    private int id;
    private String voorletters;
    private String tussenvoegsel;
    private String achternaam;
    private Date geboortedatum;
    private Adres adres;
    private List<OVChipkaart> ovChipkaarten = new ArrayList<>();

    public Reiziger(int id, String voorletters, String tussenvoegsel, String achternaam, Date geboortedatum) {
        this(id, voorletters, tussenvoegsel, achternaam, geboortedatum, null);
    }

    public Reiziger(int id, String voorletters, String tussenvoegsel, String achternaam, Date geboortedatum, Adres adres) {
        this.id = id;
        this.voorletters = voorletters;
        this.tussenvoegsel = tussenvoegsel;
        this.achternaam = achternaam;
        this.geboortedatum = geboortedatum;
        this.adres = adres;
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    public String toString(int depth) {
        String tsvgsl = "";
        if (this.tussenvoegsel != null) {
             tsvgsl += " " + this.tussenvoegsel;
        }
        if (depth >= 1 || this.adres == null) {
            return("Reiziger{#" + Integer.toString(id) + voorletters + tsvgsl + " " + achternaam + " Geb. " + geboortedatum.toString() + "}");
        }
        return("Reiziger{#" + Integer.toString(id) + voorletters + tsvgsl + " " + achternaam + " Geb. " + geboortedatum.toString() + " " + this.adres.toString(depth + 1) + "}");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVoorletters() {
        return voorletters;
    }

    public void setVoorletters(String voorletters) {
        this.voorletters = voorletters;
    }

    public String getTussenvoegsel() {
        return tussenvoegsel;
    }

    public void setTussenvoegsel(String tussenvoegsel) {
        this.tussenvoegsel = tussenvoegsel;
    }

    public String getAchternaam() {
        return achternaam;
    }

    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }

    public Date getGeboortedatum() {
        return geboortedatum;
    }

    public void setGeboortedatum(Date geboortedatum) {
        this.geboortedatum = geboortedatum;
    }

    public Adres getAdres() {
        return adres;
    }

    public void setAdres(Adres adres) {
        this.adres = adres;
    }

    public List<OVChipkaart> getOvChipkaarten() {
        return ovChipkaarten;
    }

    public void addOVChipkaart(OVChipkaart ovChipkaart) {
        this.ovChipkaarten.add(ovChipkaart);
    }

    public void removeOVChipkaart(OVChipkaart ovChipkaart) {
        this.ovChipkaarten.remove(ovChipkaart);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Reiziger reiziger) {
            return this.id == reiziger.getId();
        }
        return false;
    }

}
