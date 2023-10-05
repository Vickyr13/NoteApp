package com.example.appnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.List;

public class AddNoteActivity extends AppCompatActivity {

    EditText titleNoteEditText, contentNoteEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    //boton eliminar
    ImageButton deleteNoteBtn;

    String title, content, id;
    //variable para poder editar la nota
    boolean modeEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        titleNoteEditText = findViewById(R.id.notes_title_text);
        contentNoteEditText = findViewById(R.id.notes_content_text);



        //guardar boton
        saveNoteBtn = findViewById(R.id.save_note_btn);


        //inicio de modoEdit
        //obtener el tituto de la nota para poderlo modificar
        pageTitleTextView = findViewById(R.id.page_title);
        //recibe los datos guardados en las notas
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        id = getIntent().getStringExtra("id");

        if(id!= null &&!id.isEmpty()){
            pageTitleTextView.setText("Editar nota");
            modeEdit = true;

        }

        titleNoteEditText.setText(title);
        contentNoteEditText.setText(content);
        if (modeEdit){
            pageTitleTextView.setText("Editar nota");
        }//fin de editar nota
        saveNoteBtn.setOnClickListener(v -> saveNote());
        //inicio de modoEdit
        //obtener el tituto de la nota para poderlo modificar
        pageTitleTextView = findViewById(R.id.page_title);
        //recibe los datos guardados en las notas
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        id = getIntent().getStringExtra("id");

        if(id != null && !id.isEmpty()){
            pageTitleTextView.setText("Editar nota");
            modeEdit = true;

        }

        titleNoteEditText.setText(title);
        contentNoteEditText.setText(content);
        if (modeEdit){
            pageTitleTextView.setText("Editar nota");
        }//fin de editar nota
        saveNoteBtn.setOnClickListener(v -> saveNote());
    }

    void saveNote() {
        String titleNote = titleNoteEditText.getText().toString();
        String contentNote = contentNoteEditText.getText().toString();

        if (titleNote==null || titleNote.isEmpty()) {
            titleNoteEditText.setError("El titulo es requerido");
            return;
        }

            // Obtén la fecha actual en formato Timestamp
            Timestamp currentTimestamp = Timestamp.now();
            //agregar la clase del modelo
            Note note = new Note();
            note.setTitle(titleNote);
            note.setContent(contentNote);
            note.setDate(currentTimestamp); // Asigna la fecha actual
            saveNoteToFirebase(note);
        
        //finish();
    }


    void saveNoteToFirebase(Note note) {//se encarga de guardar los datos en el firestore
        DocumentReference documentReference;
        if(modeEdit){
            //editar nota
            documentReference = Utility.getCollectionReferenceForNotes().document(id);
        }else{
            //crear nota
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }
        //se obtiene la referencia a la coleccion de notas utilizada
        //se ha creado un metodo en utility para reducir codigo al momento de crear la coleccion
        //documentReference = Utility.getCollectionReferenceForNotes().document();
        //guarda la nota en el documento recien creado
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddNoteActivity.this, "Nota guardada", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddNoteActivity.this, "No se pudo guardar la nota", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }


}