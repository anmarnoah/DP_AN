import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface ReizigerDAO {
    boolean save(Reiziger reiziger) throws SQLException;
    Reiziger findById(int id) throws SQLException;
    Reiziger findByGbdatum(String datum) throws SQLException;
    List<Reiziger> findAll() throws SQLException;
    boolean update(Reiziger reiziger) throws SQLException;
    boolean delete(Reiziger reiziger) throws SQLException;
}
