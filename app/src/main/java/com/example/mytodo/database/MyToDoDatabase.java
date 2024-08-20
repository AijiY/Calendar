package com.example.mytodo.database;

import android.content.Context;
import android.util.Log;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.mytodo.data.model.Category;
import com.example.mytodo.data.model.Plan;
import com.example.mytodo.data.model.Result;
import com.example.mytodo.data.model.Task;

@Database(entities = {Plan.class, Task.class, Result.class, Category.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class MyToDoDatabase extends RoomDatabase {
    public abstract MyDao myDao();

    private static MyToDoDatabase INSTANCE;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // テーブルに新しいカラムを追加するSQLコマンド
            database.execSQL("ALTER TABLE Task ADD COLUMN calendar_start INTEGER");
        }
    };

    public static MyToDoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyToDoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MyToDoDatabase.class, "my_to_do_database")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        } else {
            Log.d("MyToDoDatabase", "Returning existing database instance");
        }
        return INSTANCE;
    }

}
