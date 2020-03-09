package com.example.mypersonalnotes.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypersonalnotes.model.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class MemoryStorage {

    public static List<Note> notes = new ArrayList<>();
    private final static String notesPath = "notes";
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static RecyclerView.Adapter adapter;

    public static Note getNote(int index){
        if(index >= notes.size()) return new Note("", "empty", "");
        return notes.get(index);
    }

    static {
        startNoteListener();
    }

    private static void startNoteListener() {
        MemoryStorage.db.collection(notesPath).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot values, @Nullable FirebaseFirestoreException e) {
                MemoryStorage.notes.clear();
                for (DocumentSnapshot snap: values.getDocuments()) {
                    MemoryStorage.notes.add(new Note(snap.getId(),
                            snap.get("head").toString(), snap.get("body").toString()));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    public static void editNote(int index, String head, String body) {
        String id = notes.get(index).getId();
        DocumentReference docRef = MemoryStorage.db.collection(notesPath).document(id);
        Map<String, String> map = new HashMap<>();
        map.put("head", head);
        map.put("body", body);
        docRef.set(map);
    }

    public static void deleteNote(int index) {
        String key = notes.get(index).getId();
        DocumentReference docRef = MemoryStorage.db.collection(notesPath).document(key);
        docRef.delete();
    }

    public static void uploadFile(Context context){
        try {
            InputStream is = context.getAssets().open("alice.txt");
            StorageReference ref = storage.getReference("alice.txt");

            ref.putStream(is).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    System.out.println("upload completed");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("error " + e.getMessage());
                }
            });

            if(is != null){
                System.out.println("found it");
            }else {
                System.out.println("no file");
            }
        }catch (Exception e){

        }
    }

    public static void downloadImage(String name, final ImageView iv) {
        StorageReference ref = storage.getReference(name);
        int max = 1024 * 1024 * 2;
        ref.getBytes(max).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                iv.setImageBitmap(bm);
            }
        });
    }
}
