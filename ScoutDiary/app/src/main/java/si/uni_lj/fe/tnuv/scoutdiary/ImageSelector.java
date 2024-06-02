package si.uni_lj.fe.tnuv.scoutdiary;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class ImageSelector {
    private final AppCompatActivity activity;
    private final ActivityResultLauncher<Intent> resultLauncher;
    private Uri imageUri;
    private ImageSelectedCallback callback;

    public interface ImageSelectedCallback {
        void onImageSelected(Uri uri);
        void onSelectionError(String message);
    }

    public ImageSelector(AppCompatActivity activity, ImageSelectedCallback callback) {
        this.activity = activity;
        this.callback = callback;
        this.resultLauncher = this.activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            this.imageUri = uri;
                            this.callback.onImageSelected(uri);
                            grantPersistableUriPermission(uri);
                        }
                    } else {
                        this.callback.onSelectionError(activity.getString(R.string.slika_ni_bila_izbrana));
                    }
                });
    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        resultLauncher.launch(intent);
    }

    private void grantPersistableUriPermission(Uri uri) {
        try {
            int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
            activity.getContentResolver().takePersistableUriPermission(uri, takeFlags);
        } catch (SecurityException e) {
            Log.e("ImageSelector", "Failed to obtain persistable URI permission", e);
            callback.onSelectionError("Failed to obtain persistable URI permission");
        }
    }

    public Uri getImageUri() {
        return imageUri;
    }
}
