// package com.marche.marche.configuration;

// import java.io.IOException;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.io.ClassPathResource;

// import com.google.auth.oauth2.GoogleCredentials;
// import com.google.firebase.FirebaseApp;
// import com.google.firebase.FirebaseOptions;
// import com.google.firebase.messaging.FirebaseMessaging;

// @Configuration
// public class FirebaseConfig {
//     @Bean
//     FirebaseMessaging firebaseMessaging() throws IOException {
//         GoogleCredentials googleCredentials = GoogleCredentials
//                 .fromStream(new ClassPathResource("marhce-3bf53-firebase-adminsdk-1l5ge-c8cd414bba.json").getInputStream());

//         FirebaseOptions firebaseOptions = FirebaseOptions.builder()
//                 .setCredentials(googleCredentials).build();

//         FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "marche");

//         return FirebaseMessaging.getInstance(app);
//     }
// }
