package com.example.appnotes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder>{
    Context context;

    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {

        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {
        holder.titleTextView.setText(note.title);
        holder.contentTextView.setText(note.content);

        //fecha
        // Convierte la marca de tiempo a una cadena de fecha y hora legible.

        if (note.date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            String formattedTimestamp = sdf.format(note.date.toDate());
            holder.timestampTextView.setText( formattedTimestamp);
        } else {
            // Manejar el caso en el que note.date es nulo.
            holder.timestampTextView.setText("No disponible la fecha");
        }


        //metodo para un usuario hace clic en un elemento de la lista en el RecyclerView, se crea un intent que se
        // utiliza para iniciar la actividad AddNoteActivity
        holder.itemView.setOnClickListener((v) -> {
            // Este bloque de código se ejecutará cuando el usuario haga clic en un elemento de la lista.

            // Crea una nueva instancia de Intent que se utilizará para iniciar una nueva actividad.
            Intent intent = new Intent(context, AddNoteActivity.class);
            // Agrega datos adicionales (extras) al intent. Estos datos se pueden recuperar en la actividad de destino.
            // Se están pasando tres extras:
            // 1. "title": El título de la nota que se obtiene de la propiedad "note.title".
            intent.putExtra("title", note.title);
            // 2. "content": El contenido de la nota que se obtiene de la propiedad "note.content".
            intent.putExtra("content", note.content);
            //"id": El ID del documento de Firestore correspondiente a esta nota.
            // Se obtiene utilizando "getSnapshots().getSnapshot(position).getId()".
            String id = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("id", id);
            //Inicia la actividad "AddNoteActivity" con el intent configurado y los extras.
            context.startActivity(intent);
        });

        //eliminar


        holder.deleteNoteButton.setOnClickListener(v -> {
            // Este bloque de código se ejecutará cuando el usuario haga clic en un elemento de la lista.
            // Se obtiene el ID del documento de Firestore correspondiente a esta nota.
            String id = this.getSnapshots().getSnapshot(position).getId();
            // Se utiliza la función "deleteDocument()" para eliminar el documento de Firestore.
            // Se utiliza la función "getSnapshots().getSnapshot(position).getId()".
            Utility.getCollectionReferenceForNotes().document(id).delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Nota eliminada", Toast.LENGTH_SHORT).show();
                    // Actualiza el RecyclerView después de la eliminación
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Error al eliminar la nota", Toast.LENGTH_SHORT).show();
                }
            });
        });


    }
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,parent,false);
        return new NoteViewHolder(view);
    }


    class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView, contentTextView, timestampTextView;
        ImageView deleteNoteButton; // Agrega el botón de eliminación aquí

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.note_title_text_view);
            contentTextView = itemView.findViewById(R.id.note_content_text_view);
            timestampTextView = itemView.findViewById(R.id.note_timestamp_text_view);
            deleteNoteButton = itemView.findViewById(R.id.deleteNoteButton);

        }
    }
}
