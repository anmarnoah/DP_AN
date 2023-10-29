import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdresDAOPsql implements AdresDAO {
    private Connection connection;
    private ReizigerDAO reizigerDAO;

    public AdresDAOPsql(Connection connection) {
        this(connection, null);
    }

    public AdresDAOPsql(Connection connection, ReizigerDAO reizigerDAO) {
        this.connection = connection;
        this.reizigerDAO = reizigerDAO;
    }

    public void setReizigerDAO(ReizigerDAO reizigerDAO) {
        this.reizigerDAO = reizigerDAO;
    }

    public ReizigerDAO getReizigerDAO() {
        return this.reizigerDAO;
    }

    @Override
    public boolean save(Adres adres) throws SQLException {
        try (PreparedStatement st = this.connection.prepareStatement("""
                INSERT INTO adres 
                    (adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id) 
                VALUES (?, ?, ?, ?, ?, ?)""")) {
            st.setInt(1, adres.getId());
            st.setString(2, adres.getPostcode());
            st.setString(3, adres.getHuisnummer());
            st.setString(4, adres.getStraat());
            st.setString(5, adres.getWoonplaats());
            st.setInt(6, adres.getReiziger().getId());
            int rowsUpdated = st.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean update(Adres adres) throws SQLException {
        try (PreparedStatement st = this.connection.prepareStatement("""
                UPDATE adres 
                SET 
                    postcode=?, 
                    huisnummer=?, 
                    straat=?, 
                    woonplaats=?, 
                    reiziger_id=? 
                WHERE 
                    adres_id=?""")) {
            st.setString(1, adres.getPostcode());
            st.setString(2, adres.getHuisnummer());
            st.setString(3, adres.getStraat());
            st.setString(4, adres.getWoonplaats());
            st.setInt(5, adres.getReiziger().getId());
            st.setInt(6, adres.getId());
            int rowsUpdated = st.executeUpdate();
                return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean delete(Adres adres) throws SQLException {
        try (PreparedStatement st = connection.prepareStatement("""
                DELETE FROM adres 
                WHERE adres_id=?""")) {
            st.setInt(1, adres.getId());
            int rowsUpdated = st.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    @Override
    public Adres findById(int id) throws SQLException {
        try (PreparedStatement st = this.connection.prepareStatement("""
                SELECT 
                    adres_id, 
                    postcode, 
                    huisnummer, 
                    straat, 
                    woonplaats, 
                    reiziger_id 
                FROM adres 
                WHERE adres_id=?""")) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return resultSetToAdres(rs);
            }
            return null;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    @Override
    public Adres findByReiziger(Reiziger reiziger) throws SQLException {
        try (PreparedStatement st = this.connection.prepareStatement("""
                SELECT 
                    adres_id, 
                    postcode, 
                    huisnummer, 
                    straat, 
                    woonplaats, 
                    reiziger_id 
                FROM adres 
                WHERE reiziger_id=?""")) {
            st.setInt(1, reiziger.getId());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return resultSetToAdres(rs, reiziger);
            }
            return null;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    private Adres resultSetToAdres(ResultSet resultSet) throws SQLException {
        int reiziger_id = resultSet.getInt("reiziger_id");
        Reiziger reiziger = this.reizigerDAO.findById(reiziger_id);
        return this.resultSetToAdres(resultSet, reiziger);
    }

    private Adres resultSetToAdres(ResultSet resultSet, Reiziger reiziger) throws SQLException {
        int adres_id = resultSet.getInt("adres_id");
        String postcode = resultSet.getString("postcode");
        String huisnummer = resultSet.getString("huisnummer");
        String straat = resultSet.getString("straat");
        String woonplaats = resultSet.getString("woonplaats");

        return new Adres(adres_id, postcode, huisnummer, straat, woonplaats, reiziger);
    }

    @Override
    public List<Adres> findAll() throws SQLException {
        List<Adres> adressen = new ArrayList<>();

        try (Statement st = this.connection.createStatement()) {
            ResultSet rs = st.executeQuery("""
                SELECT 
                    adres_id, 
                    postcode, 
                    huisnummer, 
                    straat, 
                    woonplaats, 
                    reiziger_id 
                FROM adres""");
            while (rs.next()) {
                adressen.add(this.resultSetToAdres(rs));
            }
            return adressen;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }
}
