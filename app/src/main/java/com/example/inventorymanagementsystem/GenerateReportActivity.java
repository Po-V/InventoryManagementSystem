package com.example.inventorymanagementsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class GenerateReportActivity extends AppCompatActivity {

    private String id;
    private Button optionButton;
    final int REQUEST_CODE_GALLERY = 888;
    private File pdfFile;
    private String name;
    private RadioButton chooseOption;
    private RadioGroup chooseOne;
    ArrayList<Item> list;
    ArrayList<Supplier> supplierList;
    private ProgressBar progressBar;
    PdfPTable table;
    private static final String TAG = "GenerateReportActivity";
    DatabaseReference databaseReference;
    String z;
    private int option =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_report);

        id = getIntent().getStringExtra("id");
        list = new ArrayList<>();
        supplierList = new ArrayList<>();
        optionButton = findViewById(R.id.btnGenerate);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        chooseOne = (RadioGroup) findViewById(R.id.radioType);

        optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int selectedId = chooseOne.getCheckedRadioButtonId();
                //chooseOption = (RadioButton) findViewById(selectedId);
                int index = chooseOne.indexOfChild(findViewById(chooseOne.getCheckedRadioButtonId()));
                switch (index){
                    case 0:
                        option = 0;
                        break;
                    case 1:
                        option = 1;
                        break;
                }
                try {
                    createPdfWrapper();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

        private void createPdfWrapper() throws FileNotFoundException, DocumentException {
            int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                        showMessageOKCancel("You need to allow access to Storage",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                    REQUEST_CODE_GALLERY);
                                        }
                                    }
                                });
                        return;
                    }
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_GALLERY);
                }
                return;
            } else {
                createPdf();
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            switch (requestCode) {
                case REQUEST_CODE_GALLERY:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        try {
                            createPdfWrapper();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Permission Denied
                        Toast.makeText(this, "WRITE_EXTERNAL Permission Denied", Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }

        private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
            new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setPositiveButton("OK", okListener)
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        }

    private void createPdf() throws FileNotFoundException, DocumentException {
        progressBar.setVisibility(View.VISIBLE);
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }

        pdfFile = new File(docsFolder.getAbsolutePath(), "InventoryManagement.pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();
        document.add(new Paragraph("Inventory Details"));
        document.add(new Paragraph(" "));
        if(option == 0){
            addSupplierDetails();
        }else{
            addItemDetails();
        }
        document.add(table);
        document.close();
        progressBar.setVisibility(View.GONE);
    }

    private void addItemDetails (){
        table = new PdfPTable(10); // 10 columns.
        table.setWidthPercentage(100); //Width 100%
        table.setSpacingBefore(10f); //Space before table
        table.setSpacingAfter(10f); //Space after table

        databaseReference = FirebaseDatabase.getInstance().getReference("Item_Details").child(id);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(Item.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(Item.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                list.add(dataSnapshot.getValue(Item.class));
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(Item.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        PdfPCell cell1 = new PdfPCell(new Phrase("Name"));
        table.addCell(cell1);
        cell1 = new PdfPCell(new Phrase("Unit price"));
        table.addCell(cell1);
        cell1 = new PdfPCell(new Phrase("Quantity"));
        table.addCell(cell1);
        cell1 = new PdfPCell(new Phrase("Category"));
        table.addCell(cell1);
        cell1 = new PdfPCell(new Phrase("Purchase Date"));
        table.addCell(cell1);
        cell1 = new PdfPCell(new Phrase("Location"));
        table.addCell(cell1);
        cell1 = new PdfPCell(new Phrase("Supplier Name"));
        table.addCell(cell1);
        cell1 = new PdfPCell(new Phrase("Warranty End Date"));
        table.addCell(cell1);
        cell1 = new PdfPCell(new Phrase("Comments"));
        table.addCell(cell1);
        cell1 = new PdfPCell(new Phrase("Image"));
        table.addCell(cell1);

        Image maimg;
        for (Item i : list){
            table.addCell(i.getName());
            table.addCell(i.getPrice());
            table.addCell(i.getQuantity());
            table.addCell(i.getCategory());
            table.addCell(i.getPurchaseDate());
            table.addCell(i.getLocation());
            table.addCell(i.getSupplName());
            table.addCell(i.getWarrantyEnd());
            table.addCell(i.getComments());
            try {
                byte[] decodedString = Base64.decode(i.getImagePath(), Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream3);
                maimg = Image.getInstance(stream3.toByteArray());
                table.addCell(maimg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void addSupplierDetails(){
        table = new PdfPTable(6); // 6 columns.
        table.setWidthPercentage(100); //Width 100%
        table.setSpacingBefore(10f); //Space before table
        table.setSpacingAfter(10f); //Space after table

        databaseReference = FirebaseDatabase.getInstance().getReference("Supplier_Details").child(id);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                supplierList.add(dataSnapshot.getValue(Supplier.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                supplierList.add(dataSnapshot.getValue(Supplier.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                supplierList.add(dataSnapshot.getValue(Supplier.class));
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                supplierList.add(dataSnapshot.getValue(Supplier.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        PdfPCell cell1 = new PdfPCell(new Phrase("Name"));
        table.addCell(cell1);
        cell1 = new PdfPCell(new Phrase("email"));
        table.addCell(cell1);
        cell1 = new PdfPCell(new Phrase("contact"));
        table.addCell(cell1);
        cell1 = new PdfPCell(new Phrase("address"));
        table.addCell(cell1);
        cell1 = new PdfPCell(new Phrase("comments"));
        table.addCell(cell1);
        cell1 = new PdfPCell(new Phrase("Image"));
        table.addCell(cell1);

        Image maimg;
        for (Supplier i : supplierList){
            table.addCell(i.getName());
            table.addCell(i.getEmail());
            table.addCell(i.getContact());
            table.addCell(i.getAddress());
            table.addCell(i.getComments());
            try {
                byte[] decodedString = Base64.decode(i.getImagePath(), Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream3);
                maimg = Image.getInstance(stream3.toByteArray());
                table.addCell(maimg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



}
