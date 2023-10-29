import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OVChipkaart {
    private int nummer;
    private Date geldigTot;
    private int klasse;
    private float saldo;
    private Reiziger reiziger;
    private List<Product> producten = new ArrayList<>();

    public OVChipkaart(int nummer, Date geldigTot, int klasse, float saldo, Reiziger reiziger) {
        this.nummer = nummer;
        this.geldigTot = geldigTot;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger = reiziger;
    }


    public int getNummer() {
        return nummer;
    }

    public void setNummer(int nummer) {
        this.nummer = nummer;
    }

    public Date getGeldigTot() {
        return geldigTot;
    }

    public void setGeldigTot(Date geldigTot) {
        this.geldigTot = geldigTot;
    }

    public int getKlasse() {
        return klasse;
    }

    public void setKlasse(int klasse) {
        this.klasse = klasse;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public Reiziger getReiziger() {
        return reiziger;
    }

    public void setReiziger(Reiziger reiziger) {
        this.reiziger = reiziger;
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    public String toString(int depth) {
        if (depth >= 1 || this.reiziger == null) {
            return("OVChipkaart{#" + this.nummer + ", verloopt: " + this.geldigTot.toString() + ", klasse: " + this.klasse + ", saldo: " + this.saldo + ", Producten gelinked: " + this.producten.toArray().length + "}");
        }
        return("OVChipkaart{#" + this.nummer + ", verloopt: " + this.geldigTot.toString() + ", klasse: " + this.klasse + ", saldo: " + this.saldo + ", Producten gelinked: " + this.producten.toArray().length + ", " + this.reiziger.toString(depth + 1) + "}");
    }

    public List<Product> getProducten() {
        return producten;
    }

    public void addProduct(Product product) {
        this.producten.add(product);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof OVChipkaart compareOVChipkaart) {
            return compareOVChipkaart.nummer == this.nummer &&
                    compareOVChipkaart.geldigTot.equals(this.geldigTot) &&
                    compareOVChipkaart.klasse == this.klasse &&
                    compareOVChipkaart.saldo == this.saldo;
        }
        return false;
    }
}
