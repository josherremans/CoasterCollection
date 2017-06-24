package josh.android.coastercollection.bl;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import josh.android.coastercollection.R;
import josh.android.coastercollection.bo.Coaster;

/**
 * Created by Jos on 20/12/2016.
 */
public class ImageManager {
    private static final String LOG_TAG = "IMAGE_MANAGER";

    // Tags:
    public final static int TAG_KEY_IMAGENAME = 1001;

    // Folders:
    public final static String DIR_TEMP_IMAGES = "/mnt/extSdCard/Android/data/josh.android.coastercollection/Coasters/temp";
    public final static String DIR_DEF_IMAGES = "/mnt/extSdCard/Android/data/josh.android.coastercollection/Coasters/images";


    public final static String ENDPART_ORIG_IMAGEFILE = "_orig.jpg";
    public final static String ENDPART_CROPPED_IMAGEFILE = "_cropped.jpg";

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static ArrayList<String> imageCacheHistList = new ArrayList<>();
    private static HashMap<String, Bitmap> imageCache = new HashMap<>();

    private static int currentImageCacheIndex = 0;

    public static void clearCache() {
        imageCacheHistList.clear();
        imageCache.clear();

        currentImageCacheIndex = 0;
    }

    public void load(String fileName, String filePath, ImageView v){
        if(cancelPotentialSDLoad(fileName, filePath, v)) {
            SDLoadImageTask task = new SDLoadImageTask(v);
            SDLoadDrawable sdDrawable = new SDLoadDrawable(task);
            v.setImageDrawable(sdDrawable);
            task.execute(fileName, filePath);
        }
    }

    private synchronized Bitmap loadImageFromSDCard(String fileName, String filePath, int width) {
        /*
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 4;
        bfo.outWidth = 150;
        bfo.outHeight = 150;
        Bitmap photo = BitmapFactory.decodeFile(filePath, bfo);

        return photo;
        */

//        Log.i(LOG_TAG, "BEGIN: currentImageCacheIndex=" + currentImageCacheIndex + ", size imageCacheHistList=" + imageCacheHistList.size());

        if (imageCache.containsKey(fileName)) {
            return imageCache.get(fileName);
        }

        Bitmap myBitmap = BitmapFactory.decodeFile(filePath + File.separator + fileName);

        //int width = 550; //350;
        if (width <= 0) {
            width = 500;
        }

        int height = (width * myBitmap.getHeight()) / myBitmap.getWidth();

        myBitmap = Bitmap.createScaledBitmap(myBitmap, width, height, true);

        if (currentImageCacheIndex == 10) {
            currentImageCacheIndex = 0;
        }

        if ((imageCacheHistList.size() > (currentImageCacheIndex))
                && (imageCacheHistList.get(currentImageCacheIndex) != null)) {
            imageCache.remove(imageCacheHistList.get(currentImageCacheIndex));
            imageCacheHistList.set(currentImageCacheIndex, fileName);
        } else {
            imageCacheHistList.add(fileName);
        }

        imageCache.put(fileName, myBitmap);

        currentImageCacheIndex++;

//        Log.i(LOG_TAG, "END: size imageCacheHistList=" + imageCacheHistList.size());

        return myBitmap;
    }

    private static boolean cancelPotentialSDLoad(String fileName, String filePath, ImageView v) {
        SDLoadImageTask sdLoadTask = getAsyncSDLoadImageTask(v);

        if(sdLoadTask != null) {
            String name = sdLoadTask.getmFileName();
            if((name == null) || (!name.equals(fileName))) {
                sdLoadTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private static SDLoadImageTask getAsyncSDLoadImageTask(ImageView v) {
        if(v != null) {
            Drawable drawable = v.getDrawable();
            if(drawable instanceof SDLoadDrawable) {
                SDLoadDrawable asyncLoadedDrawable = (SDLoadDrawable)drawable;
                return asyncLoadedDrawable.getAsyncSDLoadTask();
            }
        }
        return null;
    }

    private class SDLoadImageTask extends AsyncTask<String, Void, Bitmap> {

        private String mFileName;
        private String mFilePath;
        private int width;
        private final WeakReference<ImageView> mImageViewReference;

        public String getmFileName() {
            return mFileName;
        }

        public SDLoadImageTask(ImageView v) {
            Object tagObj = v.getTag(R.id.TAG_SIZE);

            if (tagObj != null) {
                width = (Integer) tagObj;
            }

            mImageViewReference = new WeakReference<ImageView>(v);
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            if(mImageViewReference != null) {
                ImageView v = mImageViewReference.get();
                SDLoadImageTask sdLoadTask = getAsyncSDLoadImageTask(v);
                // Change bitmap only if this process is still associated with it
                if(this == sdLoadTask) {
                    v.setImageBitmap(bmp);
                }
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            mFileName = params[0];
            mFilePath = params[1];
            return loadImageFromSDCard(mFileName, mFilePath, width);
        }
    }

    private class SDLoadDrawable extends ColorDrawable {
        private final WeakReference<SDLoadImageTask> asyncSDLoadTaskReference;

        public SDLoadDrawable(SDLoadImageTask asyncSDLoadTask) {
            super(Color.BLACK);
            asyncSDLoadTaskReference = new WeakReference<SDLoadImageTask>(asyncSDLoadTask);
        }

        public SDLoadImageTask getAsyncSDLoadTask() {
            return asyncSDLoadTaskReference.get();
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static File getImgFile(String img) {
        File imgFile = new  File(DIR_DEF_IMAGES + "/" + img);

        return imgFile;
    }

    public static File getTmpImgFile(String img) {
        File imgFile = new  File(DIR_TEMP_IMAGES + "/" + img);

        return imgFile;
    }

    public static Bitmap getBitmap(String img, int width) {
        File imgFile;

        if (img.endsWith(ENDPART_CROPPED_IMAGEFILE)) {
            imgFile = getTmpImgFile(img);
        } else {
            imgFile = getImgFile(img);
        }

        Bitmap myBitmap = null;

        if (imgFile.exists()) {
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            int height = (width * myBitmap.getHeight()) / myBitmap.getWidth();

            myBitmap = Bitmap.createScaledBitmap(myBitmap, width, height, true);
        }

        return myBitmap;
    }

    public static Bitmap getBitmap(File imgFile) {
        Bitmap myBitmap = null;

        int width = 250;

        if (imgFile.exists()) {
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            int height = (width * myBitmap.getHeight()) / myBitmap.getWidth();

            myBitmap = Bitmap.createScaledBitmap(myBitmap, width, height, true);
        }

        return myBitmap;
    }

    public static String getTempImageName() {
        String imageName = "Coaster_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";

        return imageName;
    }

    public static String getPathImageName(String img) {
        String imageName = DIR_DEF_IMAGES + File.separator + img;

        return imageName;
    }

    public static void moveImages(long prevCoasterID, Coaster coaster) {
        String newFrontName = coaster.getCoasterImageFrontName();
        String newBackName = coaster.getCoasterImageBackName();
        long newCoasterID = coaster.getCoasterID();

        File dir = new File(DIR_DEF_IMAGES);

        if ((newFrontName != null) && (newFrontName.contains("_" + newCoasterID + "_"))) {
            String prevFrontName = newFrontName.replace("_" + newCoasterID + "_", "_" + prevCoasterID + "_");

            File prevFile = new File(dir, prevFrontName);
            File newFile = new File(dir, newFrontName);

            if (prevFile.exists ()) prevFile.renameTo(newFile);
        }

        if ((newBackName != null) && (newBackName.contains("_" + newCoasterID + "_"))) {
            String prevBackName = newBackName.replace("_" + newCoasterID + "_", "_" + prevCoasterID + "_");

            File prevFile = new File(dir, prevBackName);
            File newFile = new File(dir, newBackName);

            if (prevFile.exists ()) prevFile.renameTo(newFile);
        }
    }

    public static void saveTempImage(ImageView imgVw, Bitmap bm) {
        File myDir = new File(DIR_TEMP_IMAGES);

        myDir.mkdirs();

        String imageName = "Coaster_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";

        File file = new File (myDir, imageName);

        if (file.exists ()) file.delete ();

        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            imgVw.setTag(TAG_KEY_IMAGENAME, imageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveDefImage(String strTmpName, String strDefName) {
        InputStream in = null;
        OutputStream out = null;

        try {
            //create output directory if it doesn't exist
            File dir = new File(DIR_DEF_IMAGES);

            if (!dir.exists())
            {
                dir.mkdirs(); // mkdirs doesn't seem to work on the sd card! So created it manually!
            }

            in = new FileInputStream(DIR_TEMP_IMAGES + "/" + strTmpName);
            out = new FileOutputStream(DIR_DEF_IMAGES + "/" + strDefName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            // write the output file
            out.flush();

            // delete the original file
            new File(DIR_TEMP_IMAGES + "/" + strTmpName).delete();
        } catch (FileNotFoundException excNF) {
            Log.e(LOG_TAG, excNF.getMessage());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
            }
        }
    }
}
