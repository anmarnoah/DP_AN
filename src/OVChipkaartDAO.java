import java.sql.SQLException;
import java.util.List;

public interface OVChipkaartDAO {
    boolean save(OVChipkaart ovChipkaart);
    OVChipkaart findByNummer(int nummer);
    List<OVChipkaart> findByReiziger(Reiziger reiziger);
    List<OVChipkaart> findAll();
    boolean update(OVChipkaart ovChipkaart);
    boolean delete(OVChipkaart ovChipkaart) throws SQLException;
}
