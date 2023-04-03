package com.example.realapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.realapp.R;
import com.example.realapp.database.NotesDataBase;
import com.example.realapp.entities.Note;
import com.example.realapp.receiver.MemoBroadcast;
import java.io.InputStream;

public class CreateNoteActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    private EditText inputNoteTitle, inputNoteText;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private ImageView imageNote;
    private String selectedImagePath;
    private Note alreadyAvailableNote;
    private AlertDialog dialogDeleteNote;
    private static final String CHANNEL_NAME = "myChannel";
    private static String CHANNEL_ID = "channelId";

    int codeForNotificationChannel = MainActivity.codeForNotificationChannel;
    public static String inputNoteTitleForNotif, inputNoteTextFornotif;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_note);
        createNotificationChannel();


        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener((v) -> onBackPressed());

        ////////////////////////////////////////////////////////////////////////////
        //проверка есть ли у приложения разрешение на чтение внешнего хранилища. Если у него нет разрешения, запрашиваем разрешение у пользователя
        selectedImagePath = "";
        ImageView addImageButton = findViewById(R.id.imageAddImage);
        addImageButton.setOnClickListener((v) -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(
                        CreateNoteActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                selectImage();
            }
        });
        //////////////////////////////////////////////////////////////////////////

        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteText = findViewById(R.id.inputNote);
        imageNote = findViewById(R.id.imageNote);
        ImageView imageSave = findViewById(R.id.imageSave);

        //Сохраняем заметку и устанавливаем уведомления
        imageSave.setOnClickListener(view -> {
            codeForNotificationChannel++;
            if (saveNote()) {
                sendNotificationAtTime();
                onBackPressed();
            }
        });
        if (getIntent().getBooleanExtra("isViewOrUpdate", false)){
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }
        findViewById(R.id.imageRemoveImage).setOnClickListener(view -> {
            imageNote.setImageBitmap(null);
            imageNote.setVisibility(View.GONE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
            selectedImagePath = "";
        });

        //Show delete button only in already existing notes
        if (alreadyAvailableNote != null){
            ImageView imageDelete = findViewById(R.id.imageDelete);
            imageDelete.setVisibility(View.VISIBLE);
            imageDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteNoteDialog();
                }
            });
        }
    }
    @SuppressLint("ShortAlarm")
    public void sendNotificationAtTime(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // Через 5 минуты
        alarmManager.set(AlarmManager.RTC_WAKEUP, (long) (System.currentTimeMillis() + ((1000 * 1) * 1 * 0.7)), getUniquePI());
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HALF_HOUR, getUniquePI());
        // Через 20 минуты
        alarmManager.set(AlarmManager.RTC_WAKEUP, (long) (System.currentTimeMillis() + (1000 * 60) * 20 * 0.7), getUniquePI());
        // Через 1 час
        alarmManager.set(AlarmManager.RTC_WAKEUP, (long) (System.currentTimeMillis() + 1000 * 60 * 60 * 0.7), getUniquePI());
        // Через 5 часов
        alarmManager.set(AlarmManager.RTC_WAKEUP, (long) (System.currentTimeMillis() + 1000 * 60 * 60 * 5 * 0.6), getUniquePI());
        // Через 1 день
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 24 * 3600000, getUniquePI());
        // Через 2 дня
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 24 * 3600000 * 2, getUniquePI());

    }
    // Уникальный PI, чтобы сообщения не накладывались друг на друга
    public PendingIntent getUniquePI(){
        Intent intent = new Intent(CreateNoteActivity.this, MemoBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),MainActivity.codeForNotificationChannel,intent, PendingIntent.FLAG_IMMUTABLE);
        MainActivity.codeForNotificationChannel ++;
        return pendingIntent;
    }
    // создает канал уведомлений с высоким приоритетом
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(){
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("1010101", CHANNEL_NAME, importance);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
    //Диалог для удаления заметки
    private void showDeleteNoteDialog(){
        if (dialogDeleteNote == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note, findViewById(R.id.layoutDeleteNoteContainer)
            );
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if (dialogDeleteNote.getWindow() != null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    @SuppressWarnings("deprecation")
                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void, Void, Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NotesDataBase.getDataBase(getApplicationContext()).noteDao().deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            Intent intent = new Intent();
                            intent.putExtra("isNoteDeleted", true);
                            finish();
                        }
                    }
                    new DeleteNoteTask().execute();
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(view1 -> dialogDeleteNote.dismiss());
        }
        dialogDeleteNote.show();
    }

    //Обновление заметки
    private void setViewOrUpdateNote (){
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteText.setText(alreadyAvailableNote.getNoteText());
        if (alreadyAvailableNote.getImagePath() != null && !alreadyAvailableNote.getImagePath().trim().isEmpty()){
            //Устанавивает изображение и делвет его видимым
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailableNote.getImagePath();
        }
    }


    private boolean saveNote() {
        //Проверка на пустые поля
        if (inputNoteText.getText().toString().trim().isEmpty() && inputNoteTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Note can't be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }

        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setImagePath(selectedImagePath);
        //Текст для уведомления
        inputNoteTitleForNotif = inputNoteTitle.getText().toString();
        inputNoteTextFornotif = inputNoteText.getText().toString();
        //Отображение | обновление заметки
        if (alreadyAvailableNote != null){
            note.setId(alreadyAvailableNote.getId());
        }

        @SuppressWarnings("deprecation")
        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                NotesDataBase.getDataBase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }
            protected void OnPostExecute(Void aVoid){
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
        return true;
    }

    //ДОБАВЛЯЕМ ИЗОБРАЖЕНИЕ В ЗАМЕТКИ
    @SuppressLint("QueryPermissionsNeeded")
    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null){
            //noinspection deprecation
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }
    //Проверяем разрешение пользователя
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("Recycle")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if (data != null){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    try{
                        @SuppressLint("Recycle")
                        //Читаем и декодируем файл
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        //Устанавливаем
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
                        selectedImagePath = getPathFromUri(selectedImageUri);
                        //Поворачиваем картинку, чтобы она всегда была вертикально
                        ExifInterface exif = new ExifInterface(getPathFromUri(selectedImageUri));
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                imageNote.animate().rotation(270);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                imageNote.animate().rotation(180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                imageNote.animate().rotation(90);
                                break;
                            case ExifInterface.ORIENTATION_UNDEFINED:
                                imageNote.animate().rotation(0);
                        }
                    } catch (Exception exception) {
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }
    private String getPathFromUri (Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null, null);
        if (cursor == null){
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }
    //Убрать клавиатуру после записи
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
            hideKeyboard();
        return super.dispatchTouchEvent(ev);
    }
}
