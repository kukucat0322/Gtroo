package cn.burgeon.core.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	public Context context;

	public DbHelper(Context context) {
		super(context, DbConstant.DB_NAME, null, DbConstant.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS c_vip" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, cardno VARCHAR,status varchar,"+
				"name VARCHAR, sex VARCHAR,idno VARCHAR,mobile VARCHAR,birthday VARCHAR,"+
                "employee VARCHAR,email VARCHAR,createTime VARCHAR,type VARCHAR,discount varchar)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS c_settle" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, orderno VARCHAR,"+
				"settleTime VARCHAR, type VARCHAR,count VARCHAR,money VARCHAR,"
				+ "orderEmployee VARCHAR,employeeID VARCHAR,status VARCHAR,settleDate VARCHAR,"
				+ "settleMonth VARCHAR,settleUUID VARCHAR)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS c_settle_detail" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, settleUUID VARCHAR,"+
				"price VARCHAR, discount VARCHAR,count VARCHAR,money VARCHAR,settleDate VARCHAR,"
				+ "pdtname VARCHAR,barcode VARCHAR,color VARCHAR,size VARCHAR,settleType VARCHAR)");

        // -----------------------------------------------------------------------------------盘点 begin
		db.execSQL("CREATE TABLE IF NOT EXISTS c_check" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, barcode VARCHAR, checkno VARCHAR, shelfid varchar,shelf varchar,"+
				"checkTime VARCHAR, type VARCHAR,count VARCHAR,money VARCHAR,isChecked VARCHAR,"
				+ "orderEmployee VARCHAR,employeeID VARCHAR,status VARCHAR,checkUUID VARCHAR)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS c_check_detail" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, checkUUID VARCHAR,"+
				"price VARCHAR, discount VARCHAR,count VARCHAR,money VARCHAR,checkDate VARCHAR,"
				+ "pdtname VARCHAR,barcode VARCHAR,color VARCHAR,size VARCHAR)");
        // -----------------------------------------------------------------------------------盘点 end

        // -----------------------------------------------------------------------------------调拨出库 begin
        db.execSQL("CREATE TABLE IF NOT EXISTS c_allot_out" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, dj_no VARCHAR, upload_status VARCHAR, dj_status VARCHAR, " +
                "dj_date VARCHAR, in_store VARCHAR, num VARCHAR, checkUUID VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS c_allot_out_detail" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, checkUUID VARCHAR, fahuofang VARCHAR, "+
                "remark VARCHAR, barcode VARCHAR, num VARCHAR, color VARCHAR, size VARCHAR, price VARCHAR, style VARCHAR)");
        // -----------------------------------------------------------------------------------调拨出库 end

        // -----------------------------------------------------------------------------------补货 begin
        db.execSQL("CREATE TABLE IF NOT EXISTS c_replenishment" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, dj_no VARCHAR, upload_status VARCHAR, " +
                "dj_date VARCHAR, out_store VARCHAR, apply_people VARCHAR, remark VARCHAR, checkUUID VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS c_replenishment_detail" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, checkUUID VARCHAR, fahuofang VARCHAR, "+
                "remark VARCHAR, barcode VARCHAR, color VARCHAR, size VARCHAR, num VARCHAR, style VARCHAR)");
        // -----------------------------------------------------------------------------------补货 end

        // -----------------------------------------------------------------------------------补货订单 begin
        db.execSQL("CREATE TABLE IF NOT EXISTS c_replenishment_order" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, dj_no VARCHAR, upload_status VARCHAR, " +
                "dj_date VARCHAR, out_store VARCHAR, apply_people VARCHAR, remark VARCHAR, checkUUID VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS c_replenishment_order_detail" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, checkUUID VARCHAR, fahuofang VARCHAR, "+
                "remark VARCHAR, barcode VARCHAR, color VARCHAR, size VARCHAR, num VARCHAR, style VARCHAR)");
        // -----------------------------------------------------------------------------------补货订单 end

		db.execSQL("CREATE TABLE IF NOT EXISTS tc_sku (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "sku varchar, style varchar, clr varchar, sizeid varchar, pname varchar, skuout varchar default null,timestamp varchar default null)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS tc_style (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "style varchar, style_name varchar, attrib1 varchar, attrib2 varchar, attrib3 varchar, "
				+ "attrib4 varchar,attrib5 varchar,attrib6 varchar,attrib7 varchar,attrib8 varchar,"
				+ "attrib9 varchar,attrib10 varchar)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS tc_styleprice (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "style varchar, store v  archar, fprice varchar, timestamp varchar)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS tdefclr(clr varchar PRIMARY KEY,clrname varchar, timestamp varchar)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS tdefsize(sizeid varchar PRIMARY KEY,sizename varchar, timestamp varchar)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS sys_user(user_id varchar,user_name varchar, password varchar,usercode varchar,lowestdiscount varchar,storeid varchar,isemployee varchar,timestamp varchar)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS tc_store(store varchar PRIMARY KEY,st_name varchar, abolishied varchar,buyerid varchar,buyerid1 varchar,storeno varchar,clientid varchar,organiseid varchar,timestamp varchar)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS tc_vip(_id INTEGER PRIMARY KEY,name varchar,discount varchar,rate varchar)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS c_payway_detail(_id INTEGER PRIMARY KEY,paywayID INTEGER,name varchar,money varchar,settleUUID VARCHAR)");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS tc_payway(_id INTEGER PRIMARY KEY,name varchar)");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
