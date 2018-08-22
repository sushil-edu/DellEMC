package com.kestone.dellpartnersummit.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.kestone.dellpartnersummit.Activities.GalleryActivity;
import com.kestone.dellpartnersummit.ApiHandler.ApiUrl;
import com.kestone.dellpartnersummit.ConnectionCheck.ConnectionCheck;
import com.kestone.dellpartnersummit.PoJo.LibraryData;
import com.kestone.dellpartnersummit.PoJo.UserDetails;
import com.kestone.dellpartnersummit.ProgressView.Progress;
import com.kestone.dellpartnersummit.R;
import com.kestone.dellpartnersummit.TopCropImageView;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {

    private static int RequestCode = 1;
    ArrayList<Image> images = new ArrayList<>();
    // private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    File Uploadfile;
    ArrayList<LibraryData> libraryList = new ArrayList<>();
    LibraryAdapter libraryAdapter;
    LinkedHashMap<Integer, String> selectMap = new LinkedHashMap<>();
    FloatingActionButton fab;
    Activity activity;

    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        activity = (Activity) getContext();


                fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectMap.size() == 0) {
                    images.clear();
                    chooseImage();
                } else {
                    JSONArray jsonArray = new JSONArray();

                    try {


                        for (Object value : selectMap.values()) {
                            // ...
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("ID", value.toString());
                            jsonObject.put("RefEmailID", UserDetails.getEmailID());

                            jsonArray.put(jsonObject);
                        }

                        Progress.showProgress(getContext());

                        new DeleteImage(ApiUrl.DeleteGalleryImage, jsonArray.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("JsonArr", jsonArray.toString());
                }

            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        libraryAdapter = new LibraryAdapter(getContext(), libraryList, fab);
        libraryAdapter.setHasStableIds(true);
        recyclerView.setAdapter(libraryAdapter);


        if (ConnectionCheck.connectionStatus(getContext())) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("EmailID", UserDetails.getEmailID());
                Log.d("LibraryParams", jsonObject.toString());

                new PostData(ApiUrl.Gallery, jsonObject.toString());

                Progress.showProgress(getContext());
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


        return view;


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode && resultCode == -1 && data != null) {
            images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            final File f = new File(images.get(0).getPath());

            compressImage(f.getAbsolutePath());

            Progress.showProgress(getContext());
            new PostImage(ApiUrl.File, "");

            Log.d("f.getAbsolutePath()", f.getAbsolutePath());
        }

    }

    void chooseImage() {
        Intent intent = new Intent(getContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_MODE, true);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_SINGLE);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_LIMIT, 10);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, true);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES, images);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_TITLE, "Album");
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_IMAGE_TITLE, "Tap to select images");
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_IMAGE_DIRECTORY, "Camera");
        startActivityForResult(intent, RequestCode);
    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String getFilename() {

        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        Uploadfile = new File(uriSting);

        Log.e("UpFile", Uploadfile.getPath().toString());
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContext().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    private class PostImage {

        // private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private final MediaType IMAGE = MediaType.parse("multipart/form-data");

        private PostImage(String url, String postBody) {

            try {
                postRequest(url, postBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void postRequest(String postUrl, String postBody) throws IOException {

            OkHttpClient client = new OkHttpClient();

            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(postBody, "android.jpg", RequestBody.create(IMAGE, Uploadfile))
                    .build();

            Request request = new Request.Builder()
                    .url(postUrl)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {


                    if (response.isSuccessful()) {

                        String myResponse = response.body().string();
                        Log.d("myResponse", myResponse);

                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            if (jsonObject.has("ImageURL")) {

                                if (jsonObject.getString("ImageURL").length() > 0) {

                                    JSONObject jObj = new JSONObject();
                                    jObj.put("RefEmailID", UserDetails.EmailID);
                                    jObj.put("ImageURL", "http://kestoneapps.in/DellPS2017/" + jsonObject.getString("ImageURL"));
                                    new PostUrl(ApiUrl.MapUserImages, jObj.toString());

                                } else {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Progress.closeProgress();
                                        }
                                    });
                                }

                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Progress.closeProgress();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                }
            });
        }
    }

    public class PostUrl {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        public PostUrl(String url, String postBody) {

            try {
                postRequest(url, postBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void postRequest(String postUrl, String postBody) throws IOException {

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(JSON, postBody);

            Request request = new Request.Builder()
                    .url(postUrl)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {

                    Progress.closeProgress();

                    if (response.isSuccessful()) {

                        String myResponse = response.body().string();
                        Log.d("myResponse", myResponse);

                        try {
                            JSONObject jObj = new JSONObject(myResponse);
                            if (jObj.getString("retval").equals("Image submitted successfully")) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (ConnectionCheck.connectionStatus(getContext())) {

                                            JSONObject jsonObject = new JSONObject();
                                            try {
                                                jsonObject.put("EmailID", UserDetails.getEmailID());
                                                Log.d("LibraryParams", jsonObject.toString());

                                                new PostData(ApiUrl.Gallery, jsonObject.toString());

                                                Progress.showProgress(getContext());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    }
                                });

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });
        }
    }

    class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.MyHolder> {

        private ArrayList<LibraryData> libraryList;
        private Context context;
        private FloatingActionButton fab;
        private Activity activity;

        public LibraryAdapter(Context context, ArrayList<LibraryData> libraryList, FloatingActionButton fab) {
            this.context = context;
            this.libraryList = libraryList;
            this.fab = fab;
            activity = (Activity) context;

        }

        @Override
        public LibraryAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_cell, parent, false);
            return new LibraryAdapter.MyHolder(view);

        }

        @Override
        public void onBindViewHolder(final LibraryAdapter.MyHolder holder, final int position) {

            final LibraryData libraryData = libraryList.get(position);


            if (libraryData.getImageURL().length() > 0) {
                Picasso.with(context).load(libraryData.getImageURL())
                        .placeholder(R.drawable.ic_gallery_logo)
                        .resize(100,100)
                        .into(holder.image);
            }

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, GalleryActivity.class);
                    intent.putExtra("link", libraryData.getImageURL());
                    context.startActivity(intent);
                }
            });

//        if(selectMap.get(position).equals("0")){
//
//            holder.alphaIv.setVisibility(View.GONE);
//            holder.tickIv.setVisibility(View.GONE);
//
//        }else {
//
//            holder.alphaIv.setVisibility(View.VISIBLE);
//            holder.tickIv.setVisibility(View.VISIBLE);
//
//        }

            if (selectMap.containsKey(position)) {
                holder.alphaIv.setVisibility(View.VISIBLE);
                holder.tickIv.setVisibility(View.VISIBLE);
            } else {
                holder.alphaIv.setVisibility(View.GONE);
                holder.tickIv.setVisibility(View.GONE);
            }

            holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

//                if(selectMap.get(position).equals("0")){
//                    selectMap.put(position,"1");
//                    holder.alphaIv.setVisibility(View.VISIBLE);
//                    holder.tickIv.setVisibility(View.VISIBLE);
//                }else {
//                    selectMap.put(position,"0");
//                    holder.alphaIv.setVisibility(View.GONE);
//                    holder.tickIv.setVisibility(View.GONE);
//                }

                    if (selectMap.containsKey(position)) {
                        selectMap.remove(position);
                        holder.alphaIv.setVisibility(View.GONE);
                        holder.tickIv.setVisibility(View.GONE);
                    } else {
                        selectMap.put(position, libraryData.getID());
                        holder.alphaIv.setVisibility(View.VISIBLE);
                        holder.tickIv.setVisibility(View.VISIBLE);
                    }
                    Log.d("Size", selectMap.size() + "");

                    if (selectMap.size() > 0) {
                        fab.setImageResource(R.drawable.ic_delete);
                    } else fab.setImageResource(R.drawable.ic_action_add);

                    return true;
                }
            });


        }

        @Override
        public int getItemCount() {
            return libraryList.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {

            private TopCropImageView image;
            private ImageView alphaIv, tickIv;

            public MyHolder(View itemView) {
                super(itemView);
                image = (TopCropImageView) itemView.findViewById(R.id.image);
                alphaIv = (ImageView) itemView.findViewById(R.id.alphaIv);
                tickIv = (ImageView) itemView.findViewById(R.id.tickIv);
            }
        }

    }

    public class DeleteImage {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        public DeleteImage(String url, String postBody) {

            try {
                postRequest(url, postBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void postRequest(String postUrl, String postBody) throws IOException {

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(JSON, postBody);

            Request request = new Request.Builder()
                    .url(postUrl)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {

                    Progress.closeProgress();

                    if (response.isSuccessful()) {

                        String myResponse = response.body().string();
                        Log.d("myResponse", myResponse);

                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            if (jsonObject.getString("retval").equals("Images Deleted successfully")) {


                                // ...
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (ConnectionCheck.connectionStatus(getContext())) {

                                            JSONObject jsonObject = new JSONObject();
                                            try {
                                                jsonObject.put("EmailID", UserDetails.getEmailID());
                                                Log.d("LibraryParams", jsonObject.toString());

                                                new PostData(ApiUrl.Gallery, jsonObject.toString());

                                                Progress.showProgress(getContext());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                        } else {
                                            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });
        }
    }


    class PostData {

        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        public PostData(String url, String postBody) {

            try {
                postRequest(url, postBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void postRequest(String postUrl, String postBody) throws IOException {

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(JSON, postBody);

            Request request = new Request.Builder()
                    .url(postUrl)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                    Progress.closeProgress();

                    if (response.isSuccessful()) {
                        libraryList.clear();
                        selectMap.clear();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fab.setImageResource(R.drawable.ic_action_add);
                            }
                        });


                        try {
                            String myRes = response.body().string();
                            Log.d("myRes", myRes);

                            try {
                                JSONArray jArr = new JSONArray(myRes);
                                for (int i = 0; i < jArr.length(); i++) {
                                    JSONObject obj = jArr.getJSONObject(i);
                                    LibraryData libData = new LibraryData();
                                    libData.setRefEmailID(obj.getString("RefEmailID"));
                                    libData.setImageURL(obj.getString("ImageURL"));
                                    libData.setID(obj.getString("ID"));
                                    libraryList.add(libData);
                                }

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        libraryAdapter.notifyDataSetChanged();
                                    }
                                });


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });
        }
    }




}
