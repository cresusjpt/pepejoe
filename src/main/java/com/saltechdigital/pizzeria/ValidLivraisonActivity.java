package com.saltechdigital.pizzeria;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.saltechdigital.pizzeria.adapter.TimeLineAdapter;
import com.saltechdigital.pizzeria.models.Livraison;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ValidLivraisonActivity extends AppCompatActivity {

    private ImageView qrcode;
    private TextView qrcodeTv;

    private Livraison livraison;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valid_livraison);
        qrcode = findViewById(R.id.qrcode_iv);
        qrcodeTv = findViewById(R.id.qrcode_tv);
        Intent intent = getIntent();
        livraison = intent.getParcelableExtra(TimeLineAdapter.LIVRAISON);
        String content = livraison.getValidate();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        generateQR(content);
        qrcodeTv.setText(getString(R.string.valid_text, content));
    }

    private void generateQR(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 1024, 1024);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            qrcode.setImageBitmap(bmp);
            //((ImageView) findViewById(R.id.qrcode_iv)).setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
