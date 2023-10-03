import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOsql implements ProductDAO {
    private Connection connection;
    private OVChipkaartDAO ovChipkaartDAO;

    public ProductDAOsql(Connection connection) {
        this(connection, null);
    }

    public ProductDAOsql(Connection connection, OVChipkaartDAO ovChipkaartDAO) {
        this.connection = connection;
        this.ovChipkaartDAO = ovChipkaartDAO;
    }
    @Override
    public boolean save(Product product) throws SQLException {
        PreparedStatement st = this.connection.prepareStatement("""
                INSERT INTO product
                    (product_nummer, naam, beschrijving, prijs)
                VALUES 
                    (?, ?, ?, ?) 
                """);
        st.setInt(1, product.getProduct_nummer());
        st.setString(2, product.getNaam());
        st.setString(3, product.getBeschrijving());
        st.setFloat(4, product.getPrijs());
        int rowsUpdated = st.executeUpdate();
        if (rowsUpdated == 0) {
            st.close();
            return false;
        }

        for (OVChipkaart ovChipkaart : product.getOvChipkaarten()) {
            st = this.connection.prepareStatement("""
                INSERT INTO ovchipkaar_product
                    (kaart_nummer, product_nummer, status, last_update)
                VALUES
                    (?, ?, NULL, NULL)
                """);

            st.setInt(1, ovChipkaart.getNummer());
            st.setInt(2, product.getProduct_nummer());
            st.executeUpdate();
        }
        st.close();
        return true;
    }

    @Override
    public Product findByProductNummer(int productNummer) throws SQLException  {
        PreparedStatement st = this.connection.prepareStatement("""
                SELECT
                    product_nummer, naam, beschrijving, prijs
                FROM product
                WHERE product_nummer = ?
                """);
        st.setInt(1, productNummer);
        ResultSet resultSet = st.executeQuery();
        st.close();
        if (resultSet.next()) {
            return resultSetToProduct(resultSet);
        }
        return null;
    }

    private Product resultSetToProduct(ResultSet resultSet) throws SQLException {
        int product_nummer = resultSet.getInt("product_nummer");
        String naam = resultSet.getString("naam");
        String beschrijving = resultSet.getString("beschrijving");
        float prijs = resultSet.getFloat("prijs");

        Product product = new Product(product_nummer, naam, beschrijving, prijs);

        // Alle geconnecte OVChipkaarten uit het koppeltabel halen
        PreparedStatement st = this.connection.prepareStatement("""
                SELECT kaart_nummer
                FROM ov_chipkaart_product
                WHERE product_nummer = ?
                """);
        st.setInt(1, product_nummer);
        ResultSet rs2 = st.executeQuery();
        while (rs2.next()) {
            int kaart_nummer = resultSet.getInt("kaart_nummer");
            OVChipkaart ovChipkaart = ovChipkaartDAO.findByNummer(kaart_nummer);
            product.addOvChipkaart(ovChipkaart);
        }

        return product;
    }

    @Override
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart) throws SQLException  {
        List<Product> producten = new ArrayList<Product>();

        PreparedStatement st = this.connection.prepareStatement("""
                SELECT
                	product.product_nummer,
                	product.naam,
                	product.beschrijving,
                	product.prijs
                FROM product
                INNER JOIN ov_chipkaart_product
                ON ov_chipkaart_product.product_nummer = product.product_nummer
                WHERE ov_chipkaart_product.kaart_nummer = ?
                """);
        st.setInt(1, ovChipkaart.getNummer());
        ResultSet resultSet = st.executeQuery();
        while (resultSet.next()) {
            producten.add(resultSetToProduct(resultSet));
        }
        return producten;
    }

    @Override
    public List<Product> findAll() throws SQLException  {
        List<Product> producten = new ArrayList<Product>();

        PreparedStatement st = this.connection.prepareStatement("""
                SELECT
                    product_nummer, naam, beschrijving, prijs
                FROM product
                """);
        ResultSet resultSet = st.executeQuery();
        while (resultSet.next()) {
            producten.add(resultSetToProduct(resultSet));
        }
        return producten;
    }

    @Override
    public boolean update(Product product) throws SQLException {
        Product oldProduct = this.findByProductNummer(product.getProduct_nummer());

        for (OVChipkaart ovChipkaart : oldProduct.getOvChipkaarten()) {

        }


        PreparedStatement st = this.connection.prepareStatement("""
                UPDATE product
                SET naam = ?, beschrijving = ?, prijs = ?
                WHERE product_nummer = ?
                """);
        st.setString(1, product.getNaam());
        st.setString(2, product.getBeschrijving());
        st.setFloat(3, product.getPrijs());
        st.setInt(4, product.getProduct_nummer());
        int rowsUpdated = st.executeUpdate();
        st.close();

        return rowsUpdated > 0;
    }

    @Override
    public boolean delete(Product product) throws SQLException {
        PreparedStatement st = this.connection.prepareStatement("""
                DELETE FROM product
                WHERE product_nummer = ?
                """);
        st.setInt(1, product.getProduct_nummer());
        int rowsUpdated = st.executeUpdate();
        st.close();

        return rowsUpdated > 0;
    }

    public void setOvChipkaartDAO(OVChipkaartDAO ovChipkaartDAO) {
        this.ovChipkaartDAO = ovChipkaartDAO;
    }
}
