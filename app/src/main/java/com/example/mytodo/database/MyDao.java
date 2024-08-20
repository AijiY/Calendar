package com.example.mytodo.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.mytodo.data.model.Category;
import com.example.mytodo.data.model.Plan;
import com.example.mytodo.data.model.Result;
import com.example.mytodo.data.model.Task;
import java.util.List;

@Dao
public interface MyDao {

  @Insert
  void insertPlan(Plan plan);

  @Insert
  void insertTask(Task task);

  @Insert
  void insertResult(Result result);

  @Insert
  void insertCategory(Category category);

  @Query("SELECT * FROM `Plan`")
  List<Plan> getPlans();

  @Query("SELECT * FROM Task")
  List<Task> getTasks();

  @Query("SELECT * FROM Result")
  List<Result> getResults();

  @Query("SELECT name FROM Category")
  List<String> getCategoryNames();

  @Query("SELECT * FROM Category")
  List<Category> getCategories();

}
