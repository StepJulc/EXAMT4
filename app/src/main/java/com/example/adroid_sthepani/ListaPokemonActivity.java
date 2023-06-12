package com.example.adroid_sthepani;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.adroid_sthepani.adapter.PokemonAdapter;
import com.example.adroid_sthepani.entities.Publicacion;
import com.example.adroid_sthepani.service.PokemonService;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListaPokemonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pokemon);

        RecyclerView rvLista = findViewById(R.id.rvLista);
        rvLista.setLayoutManager(new LinearLayoutManager(this));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://648577a6a795d24810b6fb31.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PokemonService service = retrofit.create(PokemonService.class);
        Call<List<Publicacion>> call = service.getAllUser();

        call.enqueue(new Callback<List<Publicacion>>() {
            @Override
            public void onResponse(Call<List<Publicacion>> call, Response<List<Publicacion>> response) {
                if (response.isSuccessful()) {
                    List<Publicacion> data = response.body();

                    Log.i("MAIN_APP",String.valueOf(data.size()));
                    Log.i("MAIN_APP", new Gson().toJson(data));

                    PokemonAdapter adapter = new PokemonAdapter(data);
                    rvLista.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Publicacion>> call, Throwable t) {

            }
        });

    }
}