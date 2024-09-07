package com.example.bookshelf;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ListBookDetailFragment extends DialogFragment {
    private ImageButton imageButton;
    private EditText titleEditText;
    private EditText authorEditText;
    private CheckBox yetCheckBox;
    private EditText dateEditText;
    private RatingBar ratingBar;
    private EditText thoughtEditText;
    private boolean isEditMode = false;
    private String imagePath;

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public  void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            dismiss();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.bookDetailToolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Button editButton = view.findViewById(R.id.buttonEdit);
        editButton.setOnClickListener(v -> {
            isEditMode = !isEditMode;
            if (isEditMode) {
                imageButton.setEnabled(true);
                titleEditText.setEnabled(true);
                authorEditText.setEnabled(true);
                yetCheckBox.setEnabled(true);
                dateEditText.setEnabled(true);
                ratingBar.setEnabled(true);
                thoughtEditText.setEnabled(true);
                editButton.setText("保存");
            } else {
                imageButton.setEnabled(false);
                titleEditText.setEnabled(false);
                authorEditText.setEnabled(false);
                yetCheckBox.setEnabled(false);
                dateEditText.setEnabled(false);
                ratingBar.setEnabled(false);
                thoughtEditText.setEnabled(false);
                editButton.setText("編集");

                saveChanges();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_book_detail, container, false);
        imageButton = view.findViewById(R.id.imageButton);
        titleEditText = view.findViewById(R.id.editText);
        authorEditText = view.findViewById(R.id.editText2);
        yetCheckBox = view.findViewById(R.id.checkBox);
        dateEditText = view.findViewById(R.id.editTextDate);
        ratingBar = view.findViewById(R.id.ratingBar);
        thoughtEditText = view.findViewById(R.id.editText5);

        imageButton.setEnabled(false);
        titleEditText.setEnabled(false);
        authorEditText.setEnabled(false);
        yetCheckBox.setEnabled(false);
        dateEditText.setEnabled(false);
        ratingBar.setEnabled(false);
        thoughtEditText.setEnabled(false);

        int bookId = getArguments().getInt("BOOK_ID", -1);
        if (bookId != -1) {
            Book book = getBookFromDatabase(bookId);
            if (book != null) {
                displayBookDetails(book);
            }
        }

        imageButton.setOnClickListener(v -> {
            if (isEditMode) {
                showImagePickerDialog(v);
            }
        });

        dateEditText.setOnClickListener(v -> {
            if (isEditMode) {
                showDatePicker();
            }
        });
        return view;
    }

    private void displayBookDetails(Book book) {
        if (book.image != null) {
            Glide.with(requireContext()).load(book.image).placeholder(android.R.drawable.ic_menu_gallery).error(android.R.drawable.ic_menu_gallery).into(imageButton);
        }
        titleEditText.setText(book.title);
        authorEditText.setText(book.author);
        yetCheckBox.setChecked(book.yet == 1);
        dateEditText.setText(book.date);
        float ratingValue = book.rating != null ? book.rating : 0f;
        ratingBar.setRating(ratingValue);
        thoughtEditText.setText(book.thought);
    }

    private Book getBookFromDatabase(int bookId) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        Book book = null;

        try {
            cursor = db.query("books", null, "id = ?", new String[]{String.valueOf(bookId)}, null, null, null);
            if (cursor.moveToFirst()) {
                book = new Book();
                book.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                book.image = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                book.title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                book.author = cursor.getString(cursor.getColumnIndexOrThrow("author"));
                book.date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                book.yet = cursor.getInt(cursor.getColumnIndexOrThrow("yet"));
                book.rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"));
                book.thought = cursor.getString(cursor.getColumnIndexOrThrow("thought"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return book;
    }

    private void saveChanges() {
        if (getArguments() != null) {
            int bookId = getArguments().getInt("BOOK_ID", -1);
            if (bookId != -1) {
                String newTitle = titleEditText.getText().toString();
                String newAuthor = authorEditText.getText().toString();
                boolean newYet = yetCheckBox.isChecked();
                String newDate = dateEditText.getText().toString();
                float newRating = ratingBar.getRating();
                String newThought = thoughtEditText.getText().toString();

                DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues cv = new ContentValues();
                cv.put("image", imagePath);
                cv.put("title", newTitle);
                cv.put("author", newAuthor);
                cv.put("yet", newYet ? 1 : 0);
                cv.put("date", newDate);
                cv.put("rating", newRating);
                cv.put("thought", newThought);

                String selection = "id = ?";
                String[] selectionArgs = {String.valueOf(bookId)};

                int count = db.update("books", cv, selection, selectionArgs);
                if (count > 0) {
                    Toast.makeText(requireContext(), "保存しました", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) {
                        ((MainActivity) getActivity()).displayDuration();
                    }
                    dismiss();
                } else {
                    Toast.makeText(requireContext(), "保存に失敗しました", Toast.LENGTH_SHORT).show();
                }
                db.close();
            }
        }
    }

   private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
       if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
           Intent data = result.getData();
           if (data != null) {
               Uri imageUri = data.getData();
               if (imageUri != null) {
                   imagePath = imageUri.toString();
               } else {
                   Bundle extras = data.getExtras();
                   Bitmap imageBitmap = (Bitmap) extras.get("data");
                   if (imageBitmap != null) {
                       imagePath = saveImage(imageBitmap);
                   } else {
                       Toast.makeText(requireContext(), "画像の取得に失敗しました", Toast.LENGTH_SHORT).show();
                       return;
                   }
               }
               if (imagePath != null) {
                   Glide.with(requireContext()).load(imagePath).placeholder(android.R.drawable.ic_menu_gallery).error(android.R.drawable.ic_menu_gallery).into(imageButton);
               }
           }
       }
   });

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("日付を選択").setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            LocalDate date = Instant.ofEpochMilli(selection).atZone(ZoneId.systemDefault()).toLocalDate();
            String dateString = date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            dateEditText.setText(dateString);
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    public void showImagePickerDialog(View view) {
        CharSequence[] options = new CharSequence[]{"写真を撮る", "ギャラリーから画像を選ぶ", "キャンセル"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("画像を選択");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("写真を撮る")) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                pickImageLauncher.launch(takePictureIntent);
            } else if (options[item].equals("ギャラリーから画像を選ぶ")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImageLauncher.launch(pickPhoto);
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private  String saveImage(Bitmap bitmap) {
        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File directory = contextWrapper.getDir("images", Context.MODE_PRIVATE);
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("ListBookDetailFragment", "画像の保存に失敗しました", e);
            return null;
        }
    }
}