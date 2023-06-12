package com.example.adroid_sthepani;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.adroid_sthepani.entities.Publicacion;
import com.example.adroid_sthepani.service.PokemonService;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResgistrarPokemonActivity extends AppCompatActivity {

    ImageView ivPhotoPok ;
    EditText etNombre;
    EditText etTipo;
    EditText etElemento;

    Button btCamara;
    Button btGaleria;
    Button btRegistrarPok;

    String urlImage = "";
    private static final int OPEN_CAMERA_REQUEST = 1001;
    private static final int OPEN_GALLERY_REQUEST = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgistrar_pokemon);

        ivPhotoPok = findViewById(R.id.ivPhotoPok);
        etNombre   = findViewById(R.id.etNombre);
        etTipo     = findViewById(R.id.etTipo);
      //  etElemento = findViewById(R.id.etElemento);

        btCamara   = findViewById(R.id.btCamara);
        btGaleria  = findViewById(R.id.btGaleria);
        btRegistrarPok = findViewById(R.id.btRegistrarPok);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://648577a6a795d24810b6fb31.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PokemonService service = retrofit.create(PokemonService.class);

        btRegistrarPok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Publicacion publicacion = new Publicacion();
                publicacion.nombre    = etNombre.getText().toString();
                publicacion.tipo      = etTipo.getText().toString();
               // pokemon.elemento = etElemento.getText().toString();
                publicacion.urlimagen = "https://demo-upn.bit2bittest.com/"+urlImage;

                Call<Publicacion> call = service.create(publicacion);
                call.enqueue(new Callback<Publicacion>() {
                    @Override
                    public void onResponse(Call<Publicacion> call, Response<Publicacion> response) {

                        if (response.isSuccessful()) {
                            Publicacion data = response.body();
                            Log.i("MAIN_APP", new Gson().toJson(data));

                        }
                    }

                    @Override
                    public void onFailure(Call<Publicacion> call, Throwable t) {

                    }
                });
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Permisos para camara
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    // abrir camara
                    Log.i("MAIN_APP", "Tiene permisos para abrir la camara");
                    openCamara();
                } else {
                    // solicitar el permiso
                    Log.i("MAIN_APP", "No tiene permisos para abrir la camara, solicitando");
                    String[] permissions = new String[]{Manifest.permission.CAMERA};
                    requestPermissions(permissions, 1001);
                }

            }
        });
        btGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Permisos para abrir GALERIA
                if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                }
                else {
                    //Aplicar permisos
                    String[] permissions = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, 2000);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ivPhotoPok.setImageBitmap(photo);

            String base64 = BitmaptoBase64(photo);

            base64toLink(base64);

        }
        if(requestCode == OPEN_GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close(); // close cursor

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            ivPhotoPok.setImageBitmap(bitmap);

            String base64 = BitmaptoBase64(bitmap);

            base64toLink(base64);
        }
    }

    private void base64toLink(String base64) {

        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl("https://demo-upn.bit2bittest.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PokemonService service = retrofit1.create(PokemonService.class);

        Call<PokemonService.ImagenResponse> call = service.guardarImage(new PokemonService.ImagenToSave(base64));
        call.enqueue(new Callback<PokemonService.ImagenResponse>() {
            @Override
            public void onResponse(Call<PokemonService.ImagenResponse> call, Response<PokemonService.ImagenResponse> response) {
                if (response.isSuccessful()) {
                    PokemonService.ImagenResponse imageResponse = response.body();
                    urlImage = imageResponse.getUrl();
                    Toast.makeText(getBaseContext(), "Link GENERADO", Toast.LENGTH_SHORT).show();
                    Log.i("URL:", urlImage);

                } else {

                    Log.e("Error ",response.toString());
                }
            }

            @Override
            public void onFailure(Call<PokemonService.ImagenResponse> call, Throwable t) {

            }
        });
    }

    private String BitmaptoBase64(Bitmap photo) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        Log.i("APP_MAIN", base64);
        return base64;
    }

    private void openCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, OPEN_CAMERA_REQUEST);
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, OPEN_GALLERY_REQUEST);
    }

}