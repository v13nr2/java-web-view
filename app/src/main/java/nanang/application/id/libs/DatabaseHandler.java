package nanang.application.id.libs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import nanang.application.id.model.aset;

public class DatabaseHandler extends SQLiteOpenHelper {

	Context context;
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAMA = "iad";
	public static final String TABLE_ASET = "aset";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAMA, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void createTable() {
		SQLiteDatabase db = this.getWritableDatabase();

		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASET);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ASET + "(" +
				"_id INTEGER PRIMARY KEY, " +
				"jenisbarang TEXT, " +
				"kodebarang TEXT, " +
				"identitasbarang TEXT, " +
				"jumlah_barang TEXT, " +
				"apbdesa TEXT, " +
				"lain TEXT, " +
				"kekayaan TEXT, " +
				"tanggal_aset TEXT, " +
				"keterangan TEXT, " +
				"gambar TEXT)");
		
		db.close();
	}

	public int getRowCount(String TABLE_NAME) {
		String countQuery = "SELECT COUNT(*) AS TOTAL FROM " + TABLE_NAME;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.moveToFirst();
		int rowCount = cursor.getCount()>0?cursor.getInt(0):0;

		db.close();
		cursor.close();

		return rowCount;
	}

	public void insertDataAset(ArrayList<aset> aset_items) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		for(aset aset_item: aset_items) {
			values.put("jenisbarang", aset_item.getJenisbarang());
			values.put("kodebarang", aset_item.getKodebarang());
			values.put("identitasbarang", aset_item.getIdentitasbarang());
			values.put("gambar", aset_item.getGambar());
			db.insert(TABLE_ASET, null, values);
		}
		db.close();
	}

	public void insertDataAset(aset aset_item) {
		int total_qty = getTotalAsetItem(aset_item.getId());

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("jenisbarang", aset_item.getJenisbarang());
		values.put("kodebarang", aset_item.getKodebarang());
		values.put("identitasbarang", aset_item.getIdentitasbarang());
		values.put("gambar", aset_item.getGambar());

		if(total_qty==0) {
			db.insert(TABLE_ASET, null, values);
		} else {
			db.update(TABLE_ASET, values, "_id=?", new String[]{ String.valueOf(aset_item.getId()) });
		}
		db.close();
	}

	public void deleteAsetlist(String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ASET, "_id=?", new String[] { id });
		db.close();
	}

	/*public void deleteAsetlist() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ASET, null, null);
		db.close();
	}*/

	public int getTotalAsetItem(int id) {
		int result = 0;

		try {
			String sql = "SELECT COUNT(*) AS TOTAL FROM " + TABLE_ASET + " WHERE _id='"+id+"'";
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, null);
			if(cursor.getCount() > 0) {
				cursor.moveToFirst();
				result = cursor.getInt(0);
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public ArrayList<aset> getAsetlist() {
		ArrayList<aset> result = new ArrayList<>();

		try {
			String sql = "SELECT _id, jenisbarang, kodebarang, identitasbarang, jumlah_barang, apbdesa, lain, kekayaan, tanggal_aset, keterangan, gambar FROM " + TABLE_ASET;
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, null);
			if(cursor.getCount() > 0) {
				cursor.moveToFirst();
				for(int i=0; i<cursor.getCount(); i++) {
					aset data = new aset(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(3));
					result.add(data);
					cursor.moveToNext();
				}
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public aset getDataAset(String id) {
		aset result = null;
		try {
			String sql = "SELECT _id, jenisbarang, kodebarang, identitasbarang, jumlah_barang, apbdesa, lain, kekayaan, tanggal_aset, keterangan, gambar FROM " + TABLE_ASET +" WHERE _id='"+id+"'";
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, null);
			if(cursor.getCount() > 0) {
				cursor.moveToFirst();
				for(int i=0; i<cursor.getCount(); i++) {
					result = new aset(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(3));
					cursor.moveToNext();
				}
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public aset getDataAset() {
		aset result = null;
		try {
			String sql = "SELECT _id, jenisbarang, kodebarang, identitasbarang, jumlah_barang, apbdesa, lain, kekayaan, tanggal_aset, keterangan, gambar FROM " + TABLE_ASET +" LIMIT 0, 1";
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, null);
			if(cursor.getCount() > 0) {
				cursor.moveToFirst();
				for(int i=0; i<cursor.getCount(); i++) {
					result = new aset(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),cursor.getString(3));
					cursor.moveToNext();
				}
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
