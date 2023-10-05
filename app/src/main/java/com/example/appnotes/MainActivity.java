package com.example.appnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
public class MainActivity extends AppCompatActivity {

    FloatingActionButton addNoteBtn;
    ImageButton btnMenu;
    RecyclerView recyclerView;
    NoteAdapter noteAdapter;
    SearchView searchView;
    TextView textViewResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addNoteBtn = findViewById(R.id.add_note_btn);
        btnMenu = findViewById(R.id.menu_btn);
        recyclerView = findViewById(R.id.recycler_view);
        textViewResultado = findViewById(R.id.textViewNoResults);
        //buscador
        searchView = findViewById(R.id.searchViewhome);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchText) {
                filterRecyclerView(searchText);
                return true;
            }
        });

        addNoteBtn.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, AddNoteActivity.class)));
        btnMenu.setOnClickListener((v) -> showMenu());


        setupRecyclerView();
    }

    void showMenu() {
        /*Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);*/
    }

    void setupRecyclerView() {
        Query query = Utility.getCollectionReferenceForNotes().orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options, this);
        recyclerView.setAdapter(noteAdapter);
    }

    void filterRecyclerView(String searchText) {
        Query filteredQuery = Utility.getCollectionReferenceForNotes()
                .orderBy("title")  // Ordenar por el campo que deseas buscar (en este caso, "title")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");

        // Crear nuevas opciones del adaptador con la consulta filtrada
        FirestoreRecyclerOptions<Note> filteredOptions = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(filteredQuery, Note.class)
                .build();

        // Actualizar el adaptador con las nuevas opciones
        //noteAdapter.updateOptions(filteredOptions);
        // Verificar si hay resultados o no




        if (noteAdapter.getItemCount() == 0) {
            textViewResultado.setVisibility(View.VISIBLE);
            //ocultar recyclerView
            recyclerView.setVisibility(View.GONE);
        } else {
            //ocultar textViewResultado
            textViewResultado.setVisibility(View.GONE);
            //mostrar recyclerView
            recyclerView.setVisibility(View.VISIBLE);



        }
        noteAdapter.updateOptions(filteredOptions);
        noteAdapter.startListening();




    }



    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}