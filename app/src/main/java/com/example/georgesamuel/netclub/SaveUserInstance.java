package com.example.georgesamuel.netclub;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class SaveUserInstance {

    private static Boolean isFirstLoad = true;
    private static DocumentSnapshot documentSnapshot;
    private static ArrayList<User> list = new ArrayList<>();

    public SaveUserInstance(){

    }

    public Boolean getIsFirstLoad() {
        return isFirstLoad;
    }

    public void setIsFirstLoad(Boolean isFirstLoad) {
        SaveUserInstance.isFirstLoad = isFirstLoad;
    }

    public DocumentSnapshot getDocumentSnapshot() {
        return documentSnapshot;
    }

    public void setDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        SaveUserInstance.documentSnapshot = documentSnapshot;
    }

    public ArrayList<User> getList() {
        return list;
    }

    public void setList(ArrayList<User> list) {
        SaveUserInstance.list = list;
    }
}
