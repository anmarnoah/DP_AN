import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {
    private Connection connection;
    private ReizigerDAO reizigerDAO;
    private ProductDAO productDAO;

    public OVChipkaartDAOPsql(Connection connection) {
        this(connection, null, null);
    }

    public OVChipkaartDAOPsql(Connection connection, ReizigerDAO reizigerDAO) {
        this(connection, reizigerDAO, null);
    }

    public OVChipkaartDAOPsql(Connection connection, ReizigerDAO reizigerDAO, ProductDAO productDAO) {
        this.connection = connection;
        this.reizigerDAO = reizigerDAO;
        this.productDAO = productDAO;
    }

    @Override
    public boolean save(OVChipkaart ovChipkaart) throws SQLException {

        if (this.reizigerDAO != null) {
            Reiziger gekoppeldeReiziger = ovChipkaart.getReiziger();
            if (gekoppeldeReiziger != null && this.reizigerDAO.findById(gekoppeldeReiziger.getId()) == null) {
                reizigerDAO.save(gekoppeldeReiziger);
            }
        }

        try (PreparedStatement st = this.connection.prepareStatement("""
                INSERT INTO ov_chipkaart
                    (kaart_nummer, geldig_tot, klasse, saldo, reiziger_id)
                VALUES 
                    (?, ?, ?, ?, ?) 
                """)) {
            st.setInt(1, ovChipkaart.getNummer());
            st.setDate(2, (Date) ovChipkaart.getGeldigTot());
            st.setInt(3, ovChipkaart.getKlasse());
            st.setFloat(4, ovChipkaart.getSaldo());
            st.setInt(5, ovChipkaart.getReiziger().getId());

            int rowsUpdated = st.executeUpdate();
            st.close();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    @Override
    public OVChipkaart findByNummer(int nummer) throws SQLException {
        try (PreparedStatement st = this.connection.prepareStatement("""
                SELECT
                    kaart_nummer, geldig_tot, klasse, saldo, reiziger_id 
                FROM 
                    ov_chipkaart 
                WHERE 
                    kaart_nummer=?""")) {
            st.setInt(1, nummer);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                OVChipkaart returnOVChipkaart = this.resultSetToOVChipkaart(rs);
                st.close();
                return returnOVChipkaart;
            }
            st.close();
            return null;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger)  throws SQLException {
        List<OVChipkaart> ovchipkaarten = new ArrayList<>();
        try (PreparedStatement st = this.connection.prepareStatement("""
                SELECT 
                    kaart_nummer, geldig_tot, klasse, saldo, reiziger_id 
                FROM 
                    ov_chipkaart 
                WHERE 
                    reiziger_id=?""")) {
            st.setInt(1, reiziger.getId());
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                ovchipkaarten.add(this.resultSetToOVChipkaart(rs));
            }
            st.close();
            return ovchipkaarten;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<OVChipkaart> findAll() throws SQLException {
        List<OVChipkaart> ovchipkaarten = new ArrayList<>();
        try (PreparedStatement st = this.connection.prepareStatement("""
                SELECT 
                    kaart_nummer, geldig_tot, klasse, saldo, reiziger_id 
                FROM 
                    ov_chipkaart""")) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                ovchipkaarten.add(this.resultSetToOVChipkaart(rs));
            }
            st.close();
            return ovchipkaarten;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    private OVChipkaart resultSetToOVChipkaart(ResultSet rs) throws SQLException {

            int kaart_nummer = rs.getInt("kaart_nummer");
            Date geldig_tot = rs.getDate("geldig_tot");
            int klasse = rs.getInt("klasse");
            float saldo = rs.getFloat("saldo");
            int reiziger_id = rs.getInt("reiziger_id");

            OVChipkaart ovChipkaart = new OVChipkaart(kaart_nummer, geldig_tot, klasse, saldo, this.reizigerDAO.findById(reiziger_id));

            // Alle geconnecte Producten uit het koppeltabel halen
            try (PreparedStatement st = this.connection.prepareStatement("""
                    SELECT product_nummer
                    FROM ov_chipkaart_product
                    WHERE kaart_nummer = ?
                    """)) {
                st.setInt(1, kaart_nummer);
                ResultSet rs2 = st.executeQuery();
                while (rs2.next()) {
                    kaart_nummer = rs.getInt("kaart_nummer");
                    Product product = this.productDAO.findByProductNummer(kaart_nummer);
                    ovChipkaart.addProduct(product);
                }
                rs2.close();

                return ovChipkaart;
            } catch (SQLException e) {
                System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
                throw e;
            }
    }

    @Override
    public boolean update(OVChipkaart ovChipkaart) throws SQLException {
        try (PreparedStatement st = this.connection.prepareStatement("""
                UPDATE ov_chipkaart 
                    SET 
                        geldig_tot=?, klasse=?, saldo=?, reiziger_id=?
                    WHERE 
                        kaart_nummer=?""")) {
            st.setDate(1, (Date) ovChipkaart.getGeldigTot());
            st.setInt(2, ovChipkaart.getKlasse());
            st.setFloat(3, ovChipkaart.getSaldo());
            st.setInt(4, ovChipkaart.getReiziger().getId());
            st.setInt(5, ovChipkaart.getNummer());

            int rowsUpdated = st.executeUpdate();
            st.close();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean delete(OVChipkaart ovChipkaart) throws SQLException {
        try (PreparedStatement st = connection.prepareStatement("DELETE FROM ov_chipkaart WHERE kaart_nummer=?")) {
            st.setInt(1, ovChipkaart.getNummer());
            int rowsUpdated = st.executeUpdate();
            st.close();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    public ReizigerDAO getReizigerDAO() {
        return reizigerDAO;
    }

    public void setReizigerDAO(ReizigerDAO reizigerDAO) {
        this.reizigerDAO = reizigerDAO;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }
}
