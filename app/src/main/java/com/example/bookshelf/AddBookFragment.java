package com.example.bookshelf;

import android.app.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;

public class AddBookFragment extends Fragment {
    private ImageButton imageButton;
    private ActivityResultLauncher<Intent> pickImageFromGalleryResult;
    private ActivityResultLauncher<Intent> takePictureResult;
    private EditText editTextDate;
    private String imagePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);
        editTextDate = view.findViewById(R.id.editTextDate);
        editTextDate.setOnClickListener(v -> showDatePicker());
        return view;
    }

    @Override //画像設定
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //画像設定
        imageButton = view.findViewById(R.id.imageButton);
        Consumer<Uri> handleImage = this::handleImageImpl;

        pickImageFromGalleryResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri imageUri = data != null ? data.getData() : null;
                        handleImage.accept(imageUri);
                    }
                }
        );

        takePictureResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        imagePath = saveImageToInternalStorage(imageBitmap);
                        imageButton.setImageBitmap(imageBitmap);
                    }
                }
        );

        imageButton.setOnClickListener(this::showImagePickerDialog);

        //DB
        DatabaseHelper dbHelper = new DatabaseHelper(this.getContext());

        EditText editTitle = getView().findViewById(R.id.editText);
        EditText editAuthor = getView().findViewById(R.id.editText2);
        CheckBox checkBox = getView().findViewById(R.id.checkBox);
        EditText editDate = getView().findViewById(R.id.editTextDate);
        RatingBar ratingBar = getView().findViewById(R.id.ratingBar);
        EditText editThought = getView().findViewById(R.id.editText5);
        Button buttonAdd = getView().findViewById(R.id.buttonAdd);

        buttonAdd.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String author = editAuthor.getText().toString();
            String date = editDate.getText().toString();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(getContext(), "タイトルを入力してください", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(author)) {
                Toast.makeText(getContext(), "著者を入力してください", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(date)) {
                Toast.makeText(getContext(), "日付を入力してください", Toast.LENGTH_SHORT).show();
                return;
            }

            try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
                ContentValues cv = new ContentValues();
                if (imagePath != null) {
                    cv.put("image", imagePath);
                }
                cv.put("title", title);
                cv.put("author", author);
                cv.put("date", date);
                cv.put("yet", checkBox.isChecked() ? 1 : 0);
                cv.put("rating", ratingBar.getRating());
                cv.put("thought", editThought.getText().toString());
                long newRowId = db.insert("books", null, cv);
                if (newRowId != -1) {
                    Toast.makeText(getContext(), "データを追加しました", Toast.LENGTH_SHORT).show();
                    editTitle.setText("");
                    editAuthor.setText("");
                    editDate.setText("");
                    checkBox.setChecked(false);
                    ratingBar.setRating(0);
                    editThought.setText("");
                    if (getActivity() != null){
                        ((MainActivity) getActivity()).displayDuration();
                    }
                    if (imagePath != null) {
                        imageButton.setImageResource(android.R.drawable.ic_menu_gallery);
                        imagePath = null;
                    }
                } else {
                    Toast.makeText(getContext(), "データの追加に失敗しました", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleImageImpl(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
            imagePath = saveImageToInternalStorage(bitmap);
            imageButton.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //画像選択
    public void showImagePickerDialog(View view) {
        CharSequence[] options = new CharSequence[]{"写真を撮る", "ギャラリーから画像を選ぶ", "キャンセル"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("画像を選択");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("写真を撮る")) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureResult.launch(takePictureIntent);
            } else if (options[item].equals("ギャラリーから画像を選ぶ")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImageFromGalleryResult.launch(pickPhoto);
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    //画像を内部ストレージ保存
    private String saveImageToInternalStorage(Bitmap bitmap) {
        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File directory = contextWrapper.getDir("images", Context.MODE_PRIVATE);
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File filepath = new File(directory, fileName);
        FileOutputStream fos =null;
        try {
            fos = new FileOutputStream(filepath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return filepath.getAbsolutePath();
    }

    //日付選択
    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("日付を選択")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            String dateString = dateFormat.format(new Date(selection));
            editTextDate.setText(dateString);
        });

        datePicker.show(getParentFragmentManager(), "datePicker");
    }
}