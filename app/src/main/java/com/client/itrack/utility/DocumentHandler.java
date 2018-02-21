package com.client.itrack.utility;

import android.app.Activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.client.itrack.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sony on 25-05-2016.
 */
public class DocumentHandler {

    String userChoosenTask;
    Activity context ;
    Fragment frag ;
    public DocumentHandler(Activity context, Fragment frag ) {
        this.context =  context ;
        this.frag =  frag ;
    }

    public void showImage(String data, final String documentName)
    {
        if(data !=null && !data.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
//            ImageView imageView = new ImageView(this.context);
//            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//           imageView.setLayoutParams(new ViewGroup.LayoutParams( imageView.getLayoutParams().MATCH_PARENT,  imageView.getLayoutParams().MATCH_PARENT));
//
            LayoutInflater inflater = context.getLayoutInflater();
            View document_view  = inflater.inflate(R.layout.document_view, null);
            final ImageButton btnDownload = (ImageButton) document_view.findViewById(R.id.btnDownload) ;

            ImageView imageView = (ImageView) document_view.findViewById(R.id.ivDoc) ;
            Display display = context.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            imageView.setLayoutParams(new LinearLayout.LayoutParams(width,height));
            final byte[] imageAsBytes = Base64.decode(data.getBytes(), Base64.DEFAULT);
            imageView.setImageBitmap(
                    BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
            );

            builder.setView(document_view);
            final Dialog dialog =  builder.create();
            btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File directory = null ;
                    if(isExternalStorageWritable())
                    {
                        directory = getAlbumStorageDir("Truck Docs");
                        File mypath = new File(directory,documentName);
                        saveToStorageStorage(mypath, BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                    }else{

                        ContextWrapper cw = new ContextWrapper(context);
                        directory = cw.getDir("Truck Docs", Context.MODE_PRIVATE);
                        // Create imageDir
                        File mypath=new File(directory,documentName);
                        saveToStorageStorage(mypath, BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                    }

                    Toast.makeText(context, "Saved to : "+directory.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
        else
        {
            Toast.makeText(context, "Image is not located!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToStorageStorage(File mypath ,   Bitmap bitmapImage){



        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void showImageByURL(String path)
    {
        if(path !=null && !path.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            LayoutInflater inflater = context.getLayoutInflater();
            View document_view  = inflater.inflate(R.layout.document_view, null);
            ImageView imageView = (ImageView) document_view.findViewById(R.id.ivDoc) ;
            Display display = context.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            Picasso.with(this.context).load(path).resize(width,height).into(imageView);
            builder.setView(document_view);
            builder.create();
            builder.show();
        }
        else
        {
            Toast.makeText(context, "Image is not located!", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectDocumentToAdd() {

        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Document!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(context);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        frag.startActivityForResult(intent,Constants.REQUEST_CAMERA);
                    }

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                    {
                        /*Intent intent = new Intent();
                        intent.setType("image*//*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);//
                        frag.startActivityForResult(Intent.createChooser(intent, "Select File"),Constants.REQUEST_GALLERY);*/

                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(context);
                if (items[item].equals("Take Photo")) {
                    if (result) {
                        camFunction();
                    }

                } else if (items[item].equals("Choose from Gallery")) {
                    if (result)
                    {
                        gallaryFun();
                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private  void gallaryFun(){

        try {

            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            Intent gintent = new Intent();
            gintent.setType("image/*");
            gintent.setAction(Intent.ACTION_GET_CONTENT);
            frag.startActivityForResult(
                    i,
                    1);
        } catch (Exception e) {
            Toast.makeText(context,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
    }

    private void camFunction() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(context.getPackageManager())!=null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                frag.startActivityForResult(intent, 102);
            }

        }

    }
    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }



    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("Document Download Error", "Directory not created");
        }
        return file;
    }



}
