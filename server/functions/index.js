// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();
var db = admin.firestore();

// function to calculate the distance between two points given their coordinates
function distanceInKm(lon1, lat1, lon2, lat2) {
    var R = 6371; // Radius of the earth in km
    var dLat = (lat2 - lat1) * Math.PI / 180;  // Javascript functions in radians
    var dLon = (lon2 - lon1) * Math.PI / 180;
    var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2);
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
}

function sendMessage(message, usersIds) {
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
}

// send new favor notification to users in the area
exports.sendNotificationNearbyOnNewFavor = functions.firestore
    .document('favors/{favorId}')
    .onCreate((change) => {

        // get new favor that has just been posted
        const newFavorLocation = change.data().location;
        var latFav = newFavorLocation[0];
        var longFav = newFavorLocation[1];
        var usersIds = [];
        var maxDistance = 5;

        // go through all the users
        db.collection('/users').get()
            .then((snapshot) => {
                snapshot.forEach((doc) => {
                    //Checking each user for location distance from the post
                    var user = doc.data();
                    var location = user.locations;
                    var latUser = location[0];
                    var longUser = location[1];

                    var distance = distanceInKm(longFav, latFav, longUser, latUser);
                    if (distance < maxDistance) {
                        usersIds.push(user.notificationIds)
                    }
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

        sendMessage(message, usersIds);
    });