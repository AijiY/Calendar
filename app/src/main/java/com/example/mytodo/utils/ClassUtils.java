package com.example.mytodo.utils;

import com.example.mytodo.data.model.Plan;
import com.example.mytodo.data.model.Result;
import com.example.mytodo.data.model.Task;

public class ClassUtils {
  public static int getClassOrder(Object obj) {
    if (obj instanceof Result) {
      return 1;
    } else if (obj instanceof Task) {
      return 2;
    } else if (obj instanceof Plan) {
      return 3;
    }
    return Integer.MAX_VALUE; // 未知の型
  }

}
