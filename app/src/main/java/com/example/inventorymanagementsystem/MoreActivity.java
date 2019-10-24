package com.example.inventorymanagementsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MoreActivity extends AppCompatActivity {
    private Button btnsignout, btngenReport, manUsersbtn, genLabels;
    private String id;
    DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private Barcode128 barcode128;
    private static final String TAG = "MoreActivity";
    ArrayList<Item> list;
    ArrayList<String> namesList;
    ArrayList<String> keys;
    private File pdfFile;
    PdfPTable table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        id = getIntent().getStringExtra("id");
        databaseReference = FirebaseDatabase.getInstance().getReference("Item_Details").child(id);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        btnsignout = (Button) findViewById(R.id.btnlogout);
        genLabels = (Button) findViewById(R.id.genLabels);
        keys = new ArrayList<>();
        list = new ArrayList<>();
        namesList = new ArrayList<>();
        manUsersbtn = (Button) findViewById(R.id.manUsersBtn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btngenReport = (Button) findViewById(R.id.genReportBtn);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keys.add(dataSnapshot.getKey());
                list.add(dataSnapshot.getValue(Item.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        for( Item k : list){
            namesList.add(k.getName());
        }

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:
                        Intent a = new Intent(MoreActivity.this, DashboardActivity.class);
                        a.putExtra("id", id);
                        startActivity(a);
                        break;
                    case R.id.navigation_supplier:
                        Intent b = new Intent(MoreActivity.this, SupplierListActivity.class);
                        b.putExtra("id", id);
                        startActivity(b);
                        break;
                    case R.id.navigation_item:
                        Intent c = new Intent(MoreActivity.this, ItemListActivity.class);
                        c.putExtra("id", id);
                        startActivity(c);

                        break;
                }
                return false;
            }
        });

        btnsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                startActivity(intent);
            }
        });

        manUsersbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(MoreActivity.this, ManageUserActivity.class);
                a.putExtra("id", id);
                startActivity(a);
            }
        });

        btngenReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(MoreActivity.this, ManageUserActivity.class);
                a.putExtra("id", id);
                startActivity(a);
            }
        });

        genLabels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                try {
                    createPDFLabels();
                }catch (FileNotFoundException | DocumentException ex){}
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void createPDFLabels() throws FileNotFoundException, DocumentException {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }

        pdfFile = new File(docsFolder.getAbsolutePath(), "InventoryBarcodeLabels.pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, output);
        PdfContentByte pdfContentByte = writer.getDirectContent();
        document.open();
        document.add(new Paragraph("Labels"));
        document.add(new Paragraph(" "));

        table = new PdfPTable(2); // 2 columns.
        table.setWidthPercentage(100); //Width 100%
        table.setSpacingBefore(10f); //Space before table
        table.setSpacingAfter(10f); //Space after table

        PdfPCell cell1 = new PdfPCell(new Phrase("Item Name"));
        table.addCell(cell1);
        cell1 = new PdfPCell(new Phrase("Barcode"));
        table.addCell(cell1);

        for (String i : keys){
            barcode128 = new Barcode128();
            barcode128.setCode(i);
            barcode128.setCodeType(Barcode128.CODE128);
            String name = namesList.get(keys.indexOf(i));
           // Image barcode128Im = barcode128.createImageWithBarcode( pdfContentByte, null, null);
            table.addCell(name);
            table.addCell(barcode128.createImageWithBarcode(pdfContentByte, null, null));
            //cell1.addElement(new Phrase("PO #: " + code));
            //cell1.addElement(barcode128Im);
           // table.addCell(cell1);
        }

        document.add(table);
        document.close();
    }
}
