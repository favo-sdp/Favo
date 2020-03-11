// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotificationOnDBChange = functions.firestore
    .document('users/{userId}')
    .onWrite((change, context) => {

        // This registration token comes from the client FCM SDKs.
        var registrationToken = 'TOKEN HERE';

        var message = {
            notification: {
                title: 'Notification from Firebase',
                body: 'DB updated !!!',
            },
            token: registrationToken
        };

        // Send a message to the device corresponding to the provided
        // registration token.
        admin.messaging().send(message)
            .then((response) => {
                // Response is a message ID string.
                console.log('Successfully sent message:', response);
            })
            .catch((error) => {
                console.log('Error sending message:', error);
            });
    });

