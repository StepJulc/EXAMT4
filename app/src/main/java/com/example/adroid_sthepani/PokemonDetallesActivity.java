package com.example.adroid_sthepani;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adroid_sthepani.entities.Publicacion;
import com.example.adroid_sthepani.service.PokemonService;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonDetallesActivity extends AppCompatActivity {

    ImageView imagenDet;
    TextView tvNombrePok;
    TextView tvTipoPok;
   // TextView tvElementoPok;
    TextView tvURLPok;
    Button btEliminarPok;



    Button addCommentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_detalles);

        imagenDet = findViewById(R.id.imagenDet);
        tvNombrePok  = findViewById(R.id.tvNombrePok);
        tvTipoPok    = findViewById(R.id.tvTipoPok);
       // tvElementoPok= findViewById(R.id.tvElementoPok);
        tvURLPok     = findViewById(R.id.tvUrlPok);
        btEliminarPok   = findViewById(R.id.btEliminarPok);


        addCommentButton = findViewById(R.id.addCommentButton);

        //Obtener ID del POkemon del Lista pokemon
        int idObtener1;
        idObtener1 = getIntent().getIntExtra("id",1);
        Log.d("APP_MAIN2", String.valueOf(idObtener1));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://648577a6a795d24810b6fb31.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PokemonService service = retrofit.create(PokemonService.class);
        //Llamar aun solo elemento
        Call<Publicacion> call = service.findUser(idObtener1);
        call.enqueue(new Callback<Publicacion>() {
            @Override
            public void onResponse(Call<Publicacion> call, Response<Publicacion> response) {
                if (response.isSuccessful()) {
                    Publicacion data = response.body();

                    Picasso.get().load(data.urlimagen).into(imagenDet);
                    tvNombrePok.setText(data.nombre);
                    tvTipoPok.setText(data.tipo);
                   // tvElementoPok.setText(data.elemento);
                    tvURLPok.setText(data.urlimagen);

                    Log.i("MAIN_APP", new Gson().toJson(data));

                }
            }

            @Override
            public void onFailure(Call<Publicacion> call, Throwable t) {

            }
        });
//*******Eliminar
        btEliminarPok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Void> call = service.delete(idObtener1);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), ListaPokemonActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
            }
        });

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí se ejecutará tu lógica para agregar comentarios
            }
        });


    }
}