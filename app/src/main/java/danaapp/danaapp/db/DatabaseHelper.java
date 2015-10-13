package danaapp.danaapp.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	private static Logger log = LoggerFactory.getLogger(DatabaseHelper.class);

	public static final String DATABASE_NAME = "DanaAppDb";

	private static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		try {
			log.info("onCreate");
			TableUtils.createTable(connectionSource, TempBasal.class);
			TableUtils.createTable(connectionSource, PumpStatus.class);
            TableUtils.createTableIfNotExists(connectionSource, Bolus.class);
		} catch (SQLException e) {
			log.error(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			log.info(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, TempBasal.class, true);
			TableUtils.dropTable(connectionSource, PumpStatus.class, true);
			onCreate(database, connectionSource);
		} catch (SQLException e) {
			log.error(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
    }

    /**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
	}

    public Dao<TempBasal, Long> getDaoTempBasals() throws SQLException {
        return  getDao(TempBasal.class);
    }

    public Dao<PumpStatus, Long> getDaoPumpStatus() throws SQLException {
//        getDao(PumpStatus.class).executeRaw("ALTER TABLE 'PumpStatus' ADD COLUMN 'tempBasalStart' VARCHAR;");
        return  getDao(PumpStatus.class);
    }

    public Dao<Bolus, Long> getDaoBolus() throws SQLException {

        return  getDao(Bolus.class);
    }

    public Dao<Carbs, Long> getDaoCarbs() throws SQLException {
//        TableUtils.createTableIfNotExists(connectionSource, Carbs.class);
        return  getDao(Carbs.class);
    }

}
