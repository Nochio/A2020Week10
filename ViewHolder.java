package com.example.mypersonalnotes.ViewHolder;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypersonalnotes.DetailActivity;
import com.example.mypersonalnotes.MainActivity;
import com.example.mypersonalnotes.R;
import com.example.mypersonalnotes.storage.MemoryStorage;

public class ViewHolder extends RecyclerView.ViewHolder {

    int rowNumber = 0;
    TextView textView;

    public ViewHolder(@NonNull View itemView){
        super(itemView);
        textView = itemView.findViewById(R.id.textViewInList);
        handleLongPressed();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                intent.putExtra(MainActivity.INDEX_KEY, rowNumber);
                view.getContext().startActivity(intent);
            }
        });
    }

    private void handleLongPressed() {
        textView.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View view) {
               MemoryStorage.deleteNote(rowNumber);
               return true;
           }
        });
    }

    public void setPosition(int position) {
        rowNumber = position;
        textView.setText(MemoryStorage.notes.get(position).getHead());
    }
}
