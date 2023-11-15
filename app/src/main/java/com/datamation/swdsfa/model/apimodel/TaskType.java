package com.datamation.swdsfa.model.apimodel;

public enum TaskType

{
    UPLOADPRESALES(1),
    UPLOAD_NONPROD(2),
    UPLOAD_SALREP(3),
    UPLOAD_ATTENDANCE(4),
    UPLOAD_EXPENSE(5),
    UPLOAD_COORDINATES(6),
    UPLOAD_IMAGES(7),
    UPLOAD_NEWCUS(8),
    UPLOAD_EDTCUS(9),
    UPLOAD_TKN(10);
    int value;

    private TaskType(int value) {
        this.value = value;
    }
}
