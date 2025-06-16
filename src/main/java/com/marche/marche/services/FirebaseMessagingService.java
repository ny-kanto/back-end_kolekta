// package com.marche.marche.services;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.google.firebase.messaging.FirebaseMessaging;
// import com.google.firebase.messaging.FirebaseMessagingException;
// import com.google.firebase.messaging.Message;
// import com.google.firebase.messaging.Notification;
// import com.marche.marche.modele.NotificationMessage;

// @Service
// public class FirebaseMessagingService {
//     @Autowired
//     private FirebaseMessaging firebaseMessaging;

//     public String sendNotificationByToken(NotificationMessage notificationMessage) {
//         Notification notification = Notification
//                 .builder()
//                 .setTitle(notificationMessage.getTitle())
//                 .setBody(notificationMessage.getBody())
//                 .build();

//         Message message = Message
//                 .builder()
//                 .setToken(notificationMessage.getRecipientToken())
//                 .setNotification(notification)
//                 .putAllData(notificationMessage.getData())
//                 .build();

//         try {
//             firebaseMessaging.send(message);
//             return "Success Sending Notification";
//         } catch (FirebaseMessagingException e) {
//             e.printStackTrace();
//             return "Error Sending Notification";
//         }
//     }
// }
