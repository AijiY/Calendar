package com.example.mytodo.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.mytodo.data.model.Category;
import com.example.mytodo.data.model.Plan;
import com.example.mytodo.data.model.Result;
import com.example.mytodo.data.model.Task;
import java.util.concurrent.Executors;

@Database(entities = {Plan.class, Task.class, Result.class, Category.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class MyToDoDatabase extends RoomDatabase {
    public abstract MyDao myDao();

    private static MyToDoDatabase INSTANCE;

    public static MyToDoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyToDoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MyToDoDatabase.class, "my_to_do_database")
                        .build();
                }
            }
        }
        return INSTANCE;
    }

    public static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // データベース初回作成時にデフォルトカテゴリーを挿入
            Executors.newSingleThreadExecutor().execute(() -> {
                MyDao dao = INSTANCE.myDao();
                dao.insertCategory(new Category("None"));
                dao.insertCategory(new Category("Add New"));
            });
        }
    };

}
