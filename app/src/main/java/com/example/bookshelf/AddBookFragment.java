package com.example.bookshelf;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddBookFragment extends Fragment {
    private ImageButton imageButton;
    private ActivityResultLauncher<Intent> pickImageFromGalleryResult;
    private ActivityResultLauncher<Intent> takePictureResult;
    private EditText editTextDate;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);
        editTextDate = view.findViewById(R.id.editTextDate);
        editTextDate.setOnClickListener(v -> showDatePicker());
        return view;
    }

    @Override //画像設定
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageButton = view.findViewById(R.id.imageButton);

        pickImageFromGalleryResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri imageUri = data != null ? data.getData() : null;
                        imageButton.setImageURI(imageUri);
                    }
                }
        );

        takePictureResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        imageButton.setImageBitmap(imageBitmap);
                    }
                }
        );

        imageButton.setOnClickListener(this::showImagePickerDialog);
    }

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

    //日付選択
    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("日付を選択")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                String dateString = dateFormat.format(new Date(selection));
                editTextDate.setText(dateString);
            }
        });

        datePicker.show(getParentFragmentManager(), "datePicker");
    }

}