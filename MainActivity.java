package com.example.mypersonalnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;

import com.example.mypersonalnotes.adapter.MyRecyclerViewAdapter;
import com.example.mypersonalnotes.model.Note;
import com.example.mypersonalnotes.storage.MemoryStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static com.example.mypersonalnotes.storage.MemoryStorage.db;

public class MainActivity extends AppCompatActivity {

    private final static String notes = "notes";
    MyRecyclerViewAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    public static final String INDEX_KEY = "INDEX_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        startNoteListener();
    }

    public void addItemToList (View view){
        addNewNote();
    }

    private void editNote(){
        DocumentReference docRef = db.collection(notes).document("");
        Map<String, String> map = new HashMap<>();
        map.put("head", "changed head");
        map.put("body", "changed body");
        docRef.set(map);
    }

    private void deleteNote(){
        DocumentReference docRef = db.collection(notes).document();
        docRef.delete();
    }

    private void startNoteListener(){
        db.collection(notes).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    private void addNewNote(){
        DocumentReference docRef = db.collection(notes).document();
        Map<String, String> map = new HashMap<>();
        map.put("head", "new headline 2");
        map.put("body", "new body 2");
        docRef.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) { Log.i("all", "added succesfully"); }
        });
    }
}
