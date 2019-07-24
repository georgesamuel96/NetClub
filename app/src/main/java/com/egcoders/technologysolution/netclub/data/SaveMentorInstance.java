package com.egcoders.technologysolution.netclub.data;

import com.egcoders.technologysolution.netclub.model.Mentor;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class SaveMentorInstance {

    private static Boolean isFirstLoad = true;
    private static DocumentSnapshot documentSnapshot;
    private static ArrayList<Mentor> list = new ArrayList<>();
    private static String bookMentorId;

    public SaveMentorInstance(){

    }

    public Boolean getIsFirstLoad() {
        return isFirstLoad;
    }

    public void setIsFirstLoad(Boolean isFirstLoad) {
        SaveMentorInstance.isFirstLoad = isFirstLoad;
    }

    public DocumentSnapshot getDocumentSnapshot() {
        return documentSnapshot;
    }

    public void setDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        SaveMentorInstance.documentSnapshot = documentSnapshot;
    }

    public ArrayList<Mentor> getList() {
        return list;
    }

    public void setList(ArrayList<Mentor> list) {
        SaveMentorInstance.list = list;
    }

    public String getBookMentorId() {
        return bookMentorId;
    }

    public void setBookMentorId(String bookMentorId) {
        SaveMentorInstance.bookMentorId = bookMentorId;
    }
}
