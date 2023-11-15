package com.datamation.swdsfa.helpers;


import com.datamation.swdsfa.model.apimodel.TaskType;

import java.util.List;

public interface UploadTaskListener {
    void onTaskCompleted(TaskType taskType, List<String> list);
    void onTaskCompleted(List<String> list);
}