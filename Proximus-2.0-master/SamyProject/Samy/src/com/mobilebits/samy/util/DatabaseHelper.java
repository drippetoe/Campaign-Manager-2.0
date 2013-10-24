package com.mobilebits.samy.util;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import net.aixum.webservice.CompanySummary;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	private static final String TAG = DatabaseHelper.class.getSimpleName();
	private static final String DATABASE_NAME = "SamyApp.db";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_STORAGE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator
			+ DATABASE_NAME;

	private Dao<CompanySummary, Integer> companyDao;


	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// super(context, DATABASE_NAME, null,
		// DATABASE_VERSION,R.raw.ormlite_config);
		companyDao = null;
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			//TableUtils.dropTable(connectionSource, CompanySummary.class, true);
			TableUtils.createTableIfNotExists(connectionSource, CompanySummary.class);
			Log.e(TAG, "Created table");
			System.err.println("CREATED TABLE");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "FAILED Created table");
			e.printStackTrace();
		}

	}
	
	
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {

	}

	@Override
	public void close() {
		super.close();

	}

	public List<CompanySummary> getAllCompanies() {
		try {
			return this.getCompanyDao().queryForAll();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Dao<CompanySummary, Integer> getCompanyDao() {
		if (null == companyDao) {
			try {
				companyDao = getDao(CompanySummary.class);
			} catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
		}
		return companyDao;
	}
}
