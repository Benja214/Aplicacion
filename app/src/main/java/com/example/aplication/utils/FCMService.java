package com.example.aplication.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.widget.Toast;

import okhttp3.*;
import org.json.JSONObject;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class FCMService {
    private static Context context;

    public FCMService(Context context) {
        this.context = context;
    }

    public void sendNotification(String token, String title, String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String accessToken = getAccessToken();

                    OkHttpClient client = new OkHttpClient();

                    JSONObject messageBody = new JSONObject();
                    messageBody.put("title", title);
                    messageBody.put("body", message);

                    JSONObject notification = new JSONObject();
                    notification.put("token", token);
                    notification.put("notification", messageBody);

                    JSONObject requestBody = new JSONObject();
                    requestBody.put("message", notification);

                    RequestBody body = RequestBody.create(
                            requestBody.toString(),
                            MediaType.parse("application/json; charset=utf-8")
                    );

                    Request request = new Request.Builder()
                            .url("https://fcm.googleapis.com/v1/projects/login-63d21/messages:send")
                            .addHeader("Authorization", "Bearer " + accessToken)
                            .addHeader("Content-Type", "application/json")
                            .post(body)
                            .build();

                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Notificación enviada", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error al enviar notificación", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Error al enviar notificación", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static String getAccessToken() throws IOException {
        String SCOPES = "https://www.googleapis.com/auth/firebase.messaging";

        AssetManager assetManager = context.getAssets();
        InputStream serviceAccount = assetManager.open("serviceAccountKey.json");

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(serviceAccount)
                .createScoped(Arrays.asList(SCOPES));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}

