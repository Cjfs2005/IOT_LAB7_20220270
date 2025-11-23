package com.example.lab6_20220270.service;

import android.net.Uri;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CloudStorageService {
    private static CloudStorageService instance;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private CloudStorageService() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public static CloudStorageService getInstance() {
        if (instance == null) {
            instance = new CloudStorageService();
        }
        return instance;
    }

    public void uploadProfileImage(Uri imageUri, String userId, OnCompleteListener<UploadTask.TaskSnapshot> listener) {
        StorageReference profileRef = storageRef.child("profile_pictures/" + userId + "/profile.jpg");
        profileRef.putFile(imageUri).addOnCompleteListener(listener);
    }

    public void getProfileImageUrl(String userId, OnCompleteListener<Uri> listener) {
        StorageReference profileRef = storageRef.child("profile_pictures/" + userId + "/profile.jpg");
        profileRef.getDownloadUrl().addOnCompleteListener(listener);
    }
}
