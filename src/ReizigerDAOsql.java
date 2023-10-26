import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOsql implements ReizigerDAO {
    private Connection connection;
    private AdresDAO adresDAO;
    private OVChipkaartDAO ovChipkaartDAO;

    public ReizigerDAOsql(Connection connection) {
        this.connection = connection;
    }

    public void setAdresDAO(AdresDAO adresDAO) {
        this.adresDAO = adresDAO;
    }

    @Override
    public boolean save(Reiziger reiziger) throws SQLException {
        try (PreparedStatement st = connection.prepareStatement("""
                    INSERT INTO reiziger 
                        (reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum) 
                    VALUES 
                        (?, ?, ?, ?, ?)
                """)) {
            st.setInt(1, reiziger.getId());
            st.setString(2, reiziger.getVoorletters());
            st.setString(3, reiziger.getTussenvoegsel());
            st.setString(4, reiziger.getAchternaam());
            st.setDate(5, (Date) reiziger.getGeboortedatum());
            int rowsUpdated = st.executeUpdate();

            if (this.ovChipkaartDAO != null) {
                for (OVChipkaart ovChipkaart : reiziger.getOvChipkaarten()) {
                    this.ovChipkaartDAO.save(ovChipkaart);
                }
            }

            if (this.adresDAO != null) {
                Adres reizigerAdres = reiziger.getAdres();
                if (reizigerAdres != null) {
                    this.adresDAO.save(reizigerAdres);
                }
            }

            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    @Override
    public Reiziger findById(int id) throws SQLException {
        try (PreparedStatement st = this.connection.prepareStatement("""
                SELECT 
                    reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum 
                FROM reiziger 
                WHERE 
                    reiziger_id=?
            """)) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return resultSetToReiziger(rs);
            }
            return null;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    @Override
    public Reiziger findByGbdatum(java.util.Date datum) throws SQLException {
        try (PreparedStatement st = connection.prepareStatement("""
            SELECT 
                reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum 
            FROM reiziger 
            WHERE 
                geboortedatum=?
        """)) {
            st.setDate(1, (Date) datum);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return resultSetToReiziger(rs);
            }
            return null;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Reiziger> findAll() throws SQLException {
        List<Reiziger> reizigers = new ArrayList<>();

        try (Statement st = this.connection.createStatement();
        ResultSet rs = st.executeQuery("""
            SELECT 
                reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum 
            FROM reiziger
        """)) {
            while (rs.next()) {
                reizigers.add(this.resultSetToReiziger(rs));
            }
            return reizigers;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean update(Reiziger reiziger) throws SQLException {
        try (PreparedStatement st = connection.prepareStatement("""
            UPDATE reiziger 
            SET 
                voorletters=?, tussenvoegsel=?, achternaam=?, geboortedatum=? 
            WHERE 
                reiziger_id=?
        """)) {
            st.setString(1, reiziger.getVoorletters());
            st.setString(2, reiziger.getTussenvoegsel());
            st.setString(3, reiziger.getAchternaam());
            st.setDate(4, (Date) reiziger.getGeboortedatum());
            st.setInt(5, reiziger.getId());
            int rowsUpdated = st.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean delete(Reiziger reiziger) throws SQLException {
        try (PreparedStatement st = connection.prepareStatement("DELETE FROM reiziger WHERE reiziger_id=?")) {
            st.setInt(1, reiziger.getId());
            int rowsUpdated = st.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.printf("Ging niet helemaal goed: %s\n", e.getMessage());
            throw e;
        }
    }

    private Reiziger resultSetToReiziger(ResultSet rs) throws SQLException {
        int reiziger_id = rs.getInt("reiziger_id");
        String voorletters = rs.getString("voorletters");
        String tussenvoegsel = rs.getString("tussenvoegsel");
        String achternaam = rs.getString("achternaam");
        Date geboortedatum = rs.getDate("geboortedatum");

        Reiziger reiziger = new Reiziger(reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum);
        reiziger.setAdres(this.adresDAO.findByReiziger(reiziger));
        return reiziger;
    }

    public void setOvChipkaartDAO(OVChipkaartDAO ovChipkaartDAO) {
        this.ovChipkaartDAO = ovChipkaartDAO;
    }
}
