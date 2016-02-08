package com.pimamobile.pima.utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pimamobile.pima.models.Activity;
import com.pimamobile.pima.models.Category;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.models.Item;
import com.pimamobile.pima.models.LibraryItem;
import com.pimamobile.pima.models.SoldItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PimiMobile.sql";
    // the name of the first table is "pimi_items"
    private static final String ITEMS_TABLE = "pimi_items";
    // names of the column for our table name pimi_items
    private static final String UID = "_id";
    private static final String ITEM_NAME = "item_name";
    private static final String ITEM_CATEGORY = "item_category";
    private static final String ITEM_PRICE = "item_price";

    // the name of the 2nd table
    private static final String CATEGORIES_TABLE = "pimi_categories";
    // column name for 2nd table
    private static final String CATEGORIES_NAME = "category_name";

    // table name for 3rd table
    private static final String DISCOUNTS_TABLE = "pimi_discounts";
    // names of the column for table pimi_discount
    private static final String DISCOUNT_NAME = "discount_name";
    private static final String DISCOUNT_AMOUNT = "discount_amount";
    private static final String DISCOUNT_IS_PERCENTAGE = "discount_is_percentage";

    // table name for 4th table
    private static final String ACTIVITIES_TABLE = "pimi_activities";
    // column names for pimi_history
    private static final String ACTIVITY_DATE = "activity_date";
    private static final String ACTIVITY_TIME = "activity_time";
    private static final String ACTIVITY_SALES_AMOUNT = "activity_sales_mount";

    // table name for 5th table
    private static final String SOLD_ITEMS_TABLE = "pimi_sold_items";
    // column names for pimi_sold_items
    private static final String SOLD_ITEM_ID = "sold_id";
    private static final String SOLD_ITEM_NAME = "sold_item_name";
    private static final String SOLD_ITEM_AMOUNT = "sold_item_amount";


    // sql query for creating table -
    private static final String CREATE_TABLE_ITEMS = "CREATE TABLE "
            + ITEMS_TABLE + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ITEM_NAME + " VARCHAR(50), "
            + ITEM_CATEGORY + " VARCHAR(50), " + ITEM_PRICE + " DOUBLE DEFAULT 0)";

    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE "
            + CATEGORIES_TABLE + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CATEGORIES_NAME + " VARCHAR(50) UNIQUE)";

    private static final String CREATE_TABLE_DISCOUNT = "CREATE TABLE "
            + DISCOUNTS_TABLE + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DISCOUNT_NAME + " VARCHAR(50)," + DISCOUNT_AMOUNT + " DOUBLE DEFAULT 0, "
            + DISCOUNT_IS_PERCENTAGE + " TINY(1) DEFAULT 1 )";

    private static final String CREATE_TABLE_ACTIVIES = "CREATE TABLE "
            + ACTIVITIES_TABLE + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ACTIVITY_DATE + " DATE, " + ACTIVITY_TIME + " TIME, "
            + ACTIVITY_SALES_AMOUNT + " DOUBLE DEFAULT 0)";

    private static final String CREATE_TABLE_SOLD_ITEMS = "CREATE TABLE "
            + SOLD_ITEMS_TABLE + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SOLD_ITEM_ID + " INT(20), " + SOLD_ITEM_NAME + " VARCHAR(50), "
            + SOLD_ITEM_AMOUNT + " DOUBLE DEFAULT 0)";
    private static final String TAG = "SQLiteHelper";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.w(TAG, "SQLiteHelper constructor is called");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(TAG, "onCreate is called");
        try {
            db.execSQL(CREATE_TABLE_ITEMS);
            db.execSQL(CREATE_TABLE_CATEGORIES);
            db.execSQL(CREATE_TABLE_DISCOUNT);
            db.execSQL(CREATE_TABLE_ACTIVIES);
            db.execSQL(CREATE_TABLE_SOLD_ITEMS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "onUpgrade is called");
        final String DROP_TABLE = "DROP TABLE IF EXISTS ";
        try {
            db.execSQL(DROP_TABLE + ACTIVITIES_TABLE);
            db.execSQL(DROP_TABLE + CATEGORIES_TABLE);
            db.execSQL(DROP_TABLE + DISCOUNTS_TABLE);
            db.execSQL(DROP_TABLE + ITEMS_TABLE);
            db.execSQL(DROP_TABLE + SOLD_ITEMS_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        onCreate(db);
    }

    public long createItem(Item item) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ITEM_NAME, item.getItemName().toLowerCase());
        values.put(ITEM_CATEGORY, item.getItemCategory().toLowerCase());
        values.put(ITEM_PRICE, item.getItemPrice());
        long id = db.insert(ITEMS_TABLE, null, values);
        return id;
    }

    public long createCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CATEGORIES_NAME, category.getCategoryName().toLowerCase());
        long id = db.insert(CATEGORIES_TABLE, null, values);
        return id;
    }

    public long createDiscount(Discount discount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DISCOUNT_NAME, discount.getDiscountName().toLowerCase());
        values.put(DISCOUNT_AMOUNT, discount.getDiscountAmount());
        values.put(DISCOUNT_IS_PERCENTAGE, discount.isPercentage() ? 1 : 0);
        long id = db.insert(DISCOUNTS_TABLE, null, values);
        return id;
    }

    public long createActivity(Activity activity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACTIVITY_DATE, activity.getDate());
        values.put(ACTIVITY_TIME, activity.getTime());
        values.put(ACTIVITY_SALES_AMOUNT, activity.getSalesAmount());
        long id = db.insert(ACTIVITIES_TABLE, null, values);
        return id;
    }

    public long createSoldItem(SoldItem soldItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SOLD_ITEM_ID, soldItem.getId());
        values.put(SOLD_ITEM_NAME, soldItem.getItemName().toLowerCase());
        values.put(SOLD_ITEM_AMOUNT, soldItem.getAmount());
        long id = db.insert(SOLD_ITEMS_TABLE, null, values);
        return id;
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(ITEMS_TABLE, null, null, null, null, null, ITEM_NAME + " ASC");
            if (cursor.moveToFirst()) {
                do {
                    Item item = new Item();
                    item.setId(cursor.getInt(cursor.getColumnIndex(UID)));
                    String name = cursor.getString(cursor.getColumnIndex(ITEM_NAME));
                    String category = cursor.getString(cursor.getColumnIndex(ITEM_CATEGORY));
                    item.setItemName(name.substring(0, 1).toUpperCase() + name.substring(1));
                    item.setItemCategory(category.substring(0, 1).toUpperCase() + category.substring(1));
                    String price = cursor.getString(cursor.getColumnIndex(ITEM_PRICE));
                    if (!price.contains(".")) {
                        price = price + ".00";
                    } else if (price.substring(price.indexOf(".")).length() == 2) {
                        price = price + "0";
                    }
                    item.setItemPrice(price);
                    items.add(item);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<Category> getAllCategory() {
        List<Category> categories = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(CATEGORIES_TABLE, null, null, null, null, null, CATEGORIES_NAME + " ASC");
            if (cursor.moveToFirst()) {
                do {
                    Category category = new Category();
                    String name = cursor.getString(cursor.getColumnIndex(CATEGORIES_NAME));
                    category.setCategoryName(name.substring(0, 1).toUpperCase() + name.substring(1));
                    categories.add(category);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public List<Discount> getAllDiscount() {
        List<Discount> discounts = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(DISCOUNTS_TABLE, null, null, null, null, null, DISCOUNT_NAME + " ASC");
            if (cursor.moveToFirst()) {
                do {
                    Discount discount = new Discount();
                    String name = cursor.getString(cursor.getColumnIndex(DISCOUNT_NAME));
                    discount.setDiscountName(name.substring(0, 1).toUpperCase() + name.substring(1));

                    boolean isPercentage = cursor.getShort(cursor.getColumnIndex(DISCOUNT_IS_PERCENTAGE)) == 1 ? true : false;
                    discount.setIsPercentage(isPercentage);
                    String price = cursor.getString(cursor.getColumnIndex(DISCOUNT_AMOUNT));
                    if (!isPercentage) {
                        if (!price.contains(".")) {
                            price = price + ".00";
                        } else if (price.substring(price.indexOf(".")).length() == 2) {
                            price = price + "0";
                        }
                    }
                    discount.setDiscountAmount(price);
                    discounts.add(discount);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return discounts;
    }

    public List<Item> getAllItemInCategory(Category category) {
        Log.i(TAG, category.getCategoryName());
        List<Item> items = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String selection = ITEM_CATEGORY + " = ?";
            String[] selectionArgs = new String[]{category.getCategoryName().toLowerCase()};
            Cursor cursor = db.query(ITEMS_TABLE, null, selection, selectionArgs, null, null, ITEM_NAME + " ASC");
            if (cursor.moveToFirst()) {
                do {
                    Item item = new Item();
                    item.setId(cursor.getInt(cursor.getColumnIndex(UID)));
                    String name = cursor.getString(cursor.getColumnIndex(ITEM_NAME));
                    item.setItemName(name.substring(0, 1).toUpperCase() + name.substring(1));
                    item.setItemCategory(cursor.getString(cursor.getColumnIndex(ITEM_CATEGORY)));
                    String price = cursor.getString(cursor.getColumnIndex(ITEM_PRICE));
                    if (!price.contains(".")) {
                        price = price + ".00";
                    } else if (price.substring(price.indexOf(".")).length() == 2) {
                        price = price + "0";
                    }
                    item.setItemPrice(price);
                    // add this data to our list
                    items.add(item);
                    Log.i(TAG, item.getItemName() + " " + item.getItemPrice());
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return items;
    }


    public int deleteCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = CATEGORIES_NAME + " = ?";
        int affected = db.delete(CATEGORIES_TABLE, where,
                new String[]{category.getCategoryName().toLowerCase()});
        return affected;
    }

    public int deleteItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = ITEM_NAME + " = ? AND " + ITEM_CATEGORY + " = ? AND " + ITEM_PRICE + " = ?";
        String[] whereArgs = new String[]{
                item.getItemName().toLowerCase(),
                item.getItemCategory().toLowerCase(),
                item.getItemPrice()
        };
        int affected = db.delete(ITEMS_TABLE, where, whereArgs);
        return affected;
    }

    public int deleteDiscount(Discount discount) {
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(DISCOUNTS_TABLE,
                DISCOUNT_NAME + " =? AND " + DISCOUNT_AMOUNT + " =? AND " + DISCOUNT_IS_PERCENTAGE + " = ?",
                new String[]{discount.getDiscountName().toLowerCase(), discount.getDiscountAmount(), discount.isPercentage() ? "1" : "0"});
        return delete;
    }

    public int updateItem(Item oldItem, Item newItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ITEM_NAME, newItem.getItemName().toLowerCase());
        values.put(ITEM_CATEGORY, newItem.getItemCategory().toLowerCase());
        values.put(ITEM_PRICE, newItem.getItemPrice());
        int update = db.update(ITEMS_TABLE, values, UID + "=?", new String[]{"" + oldItem.getId()});
        return update;
    }

    public int updateCategory(Category oldCategory, Category newCategory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CATEGORIES_NAME, newCategory.getCategoryName().toLowerCase());
        int update = db.update(CATEGORIES_TABLE, values, CATEGORIES_NAME + "=?", new String[]{oldCategory.getCategoryName()});
        return update;
    }

    public int updateDiscount(Discount oldDiscount, Discount newDiscount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DISCOUNT_NAME, newDiscount.getDiscountName().toLowerCase());
        values.put(DISCOUNT_AMOUNT, newDiscount.getDiscountAmount());
        values.put(DISCOUNT_IS_PERCENTAGE, newDiscount.isPercentage() ? "1" : "0");
        int update = db.update(DISCOUNTS_TABLE, values,
                DISCOUNT_NAME + "=? AND " + DISCOUNT_AMOUNT + "=? AND " + DISCOUNT_IS_PERCENTAGE + "=?",
                new String[]{
                        oldDiscount.getDiscountName().toLowerCase(),
                        oldDiscount.getDiscountAmount(),
                        oldDiscount.isPercentage() ? "1" : "0"
                });
        return update;
    }


}
