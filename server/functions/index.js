// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();
var db = admin.firestore();

exports.sendNotificationNearbyOnNewFavor = functions.firestore
    .document('favors/{favorId}')
    .onCreate((change, context) => {

        // get new favor that has just been posted
        const newFavorLocation = change.data().location;

        var latRadFav = newFavorLocation[0];
        var longRadFav = newFavorLocation[1];

        //const R = 6371e3; // metres
        const pi = 0.017453292519943295;    // Math.PI / 180
        const maxDistance = 5;
        var usersIds = [];

        db.collection('/users').get()
            .then((snapshot) => {
                snapshot.forEach((doc) => {
                    //Checking each document in that collection
                    var user = doc.data();
                    var location = user.locations;
                    var latRadUser = location[0];
                    var longRadUser = location[1];

                    var c = Math.cos;
                    var a = 0.5 - c((latRadUser - latRadFav) * pi)/2 + c(latRadFav * pi) * c(latRadUser * pi) * (1 - c((longRadUser - longRadFav) * pi))/2;

                    var dist = 12742 * Math.asin(Math.sqrt(a));

                    if (dist < maxDistance) {
                        usersIds.push(user.notificationIds)
                    }

                    // ALTERNATIVE Pythagoras method
                    // var x = (longRadUser-longRadFav) * Math.cos((latRadFav+latRadUser)/2);
                    // var y = (latRadFav-latRadFav);
                    // var d = Math.sqrt(x*x + y*y) * R;
                    // if (d < distance) {
                    //
                    // }
                    console.log(doc.id, '=>', doc.data().clientID);
                });
            })
            .catch((err) => {
                console.log('Error getting documents', err);
            });

        const message = {
            notification: {
                title: 'New favor nearby',
                body: 'Check out the post in the map',
            },
            token: usersIds
        };


        admin.messaging().sendMulticast(message)
            .then((response) => {
                if (response.failureCount > 0) {
                    const failedTokens = [];
                    response.responses.forEach((resp, idx) => {
                        if (!resp.success) {
                            failedTokens.push(usersIds[idx]);
                        }
                    });
                    console.log('List of tokens that caused failures: ' + failedTokens);
                }
            });
    });

