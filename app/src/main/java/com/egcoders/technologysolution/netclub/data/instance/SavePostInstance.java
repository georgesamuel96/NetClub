package com.egcoders.technologysolution.netclub.data.instance;

import com.egcoders.technologysolution.netclub.model.post.Post;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class SavePostInstance {

    private static Boolean isFirstLoad = true;
    private static DocumentSnapshot documentSnapshot;
    private static ArrayList<Post> list = new ArrayList<>();

    public SavePostInstance(){

    }

    public Boolean getIsFirstLoad() {
        return isFirstLoad;
    }

    public void setIsFirstLoad(Boolean isFirstLoad) {
        SavePostInstance.isFirstLoad = isFirstLoad;
    }

    public DocumentSnapshot getDocumentSnapshot() {
        return documentSnapshot;
    }

    public void setDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        SavePostInstance.documentSnapshot = documentSnapshot;
    }

    public ArrayList<Post> getList() {
        return list;
    }

    public void setList(ArrayList<Post> list) {
        SavePostInstance.list = list;
    }
}
