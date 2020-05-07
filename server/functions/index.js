// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();
var db = admin.firestore();

// global constants
const maxDistance = 100.0; // in km

const accepted = 1;
const cancelledByRequester = 3;
const cancelledByAccepter = 4;

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

function sendMulticastMessage(message, usersIds) {
    admin.messaging().sendMulticast(message)
        .then((response) => {
            if (response.failureCount > 0) {
                var failedTokens = [];
                response.responses.forEach((resp, idx) => {
                    if (!resp.success) {
                        failedTokens.push(usersIds[idx]);
                    }
                });

                if (failedTokens.length > 0) {
                    return console.log('List of tokens that caused failures: ' + failedTokens);
                }
            }
        return});

// send new favor notification to users in the area
exports.sendNotificationNearbyOnNewFavor = functions.firestore
    .document('/favors/{favorId}')
    .onCreate((change) => {

        // get new favor that has just been posted
        const newFavor = change.data();
        const latFav = newFavor.location.latitude;
        const longFav = newFavor.location.longitude;

        const ids = newFavor.userIds;

        if (ids === null) {
            return;
        }

        const posterId = ids[0];
        const title = newFavor.title;

        var usersIds = [];

        const message = {
            data: {
                FavorId: newFavor.id,
            },
            notification: {
                title: 'New favor nearby: ' + title,
                body: 'Click to check out the post details',
            },
            tokens: usersIds
        };

        // go through all the users
        db.collection('/users').get()
            .then((snapshot) => {
                snapshot.forEach((doc) => {
                    //Checking each user for location distance from the post
                    var user = doc.data();
                    var latUser = user.location.latitude;
                    var longUser = user.location.longitude;
                    var distance = distanceInKm(longFav, latFav, longUser, latUser);
                    if (distance < maxDistance && user.id !== posterId) {
                        usersIds.push(user.notificationId)
                    }
                });

                sendMulticastMessage(message, usersIds);
                return;
            })
    });


// send new favor notification to users on updates
exports.sendNotificationOnUpdate = functions.firestore
    .document('/favors/{favorId}')
    .onUpdate((change) => {

        const oldFavor = change.before.data();
        const updatedFavor = change.after.data();
        const oldStatus = oldFavor.statusId;
        const newStatus = updatedFavor.statusId;

        const favorTitle = updatedFavor.title;

        const userIds = updatedFavor.userIds;

        if (userIds === null) {
            return;
        }

        // that's always present
        const requesterId = userIds[0];

        // get accepterId, if it exists
        var accepterId = "";
        if (userIds.length > 1) {
            accepterId = userIds[1];
        }

        var titleToSend = "";
        var userReceiver = "";

        // send notification if new status
        if (oldStatus !== newStatus) {

            //console.log('Status has changed');

            // favor has been accepted, send notification to requester
            if (newStatus === accepted) {
                //console.log('Favor has been accepted');
                titleToSend = "Favor " + favorTitle + " has been accepted";
                userReceiver = requesterId;
            }

            // favor has been cancelled by requester, send notification to accepter, if it exists
            if (newStatus === cancelledByRequester) {
                //console.log('Favor has been cancelled by the requester');
                titleToSend = "Favor " + favorTitle + " has been cancelled by the requester";
                userReceiver = accepterId;
            }

            // favor has been cancelled by accepter, send notification to requester
            if (newStatus === cancelledByAccepter) {
                //console.log('Favor has been cancelled by the accepter');
                titleToSend = "Favor " + favorTitle + " has been cancelled by the accepter";
                userReceiver = requesterId;
            }

        } else { // if status is not different, there must have been an update of some other field, so notify the accepter
            //console.log('Favor has changed');

            titleToSend = "Favor " + favorTitle + " has been modified";
            userReceiver = accepterId;
        }

        // console.log('Title ', titleToSend);
        // console.log('Receiver ', userReceiver);

        if (userReceiver !== "") {

            var receivers = [];

            // prepare message
            const message = {
                data: {
                    FavorId: updatedFavor.id,
                },
                notification: {
                    title: titleToSend,
                    body: 'Click to check out the post details',
                },

                tokens: receivers
            };

            db.collection('/users').where("id", "==", userReceiver).get()
                .then((snapshot) => {
                    snapshot.forEach((doc) => {
                        var user = doc.data();
                        receivers.push(user.notificationId);

                        //console.log('One matching user', user.id);
                    });

                    //console.log('Users found: ', receivers);

                    sendMulticastMessage(message, receivers);
                    return;
                });
        }
    });
//Expire old requests: https://us-central1-favo-11728.cloudfunctions.net/expireOldFavors

exports.expireOldFavors = functions.https.onRequest((req,res)=>{
    const timeInDays = req.body.timeInDays;
    const EXPIRED_STATUS = 2;
    const REQUESTED_STATUS = 0;
    var now = Date.now();
    var cutoff = now - timeInDays*24*60*60*1000;
    let query = db.collection('favors');
    var oldItemsQuery = query.orderBy('postedTime').endAt(cutoff)
    .get().then(snapshot=>{
    if (snapshot.empty){
    res.status(100).send("No expired favors");
    return;
    } else {
              const promises = [];
              snapshot.forEach(doc=>{
                  let favor = doc.data();
                  if(favor.statusId===REQUESTED_STATUS){
                      promises.push(doc.ref.update({
                      statusId:EXPIRED_STATUS,
                      isArchived : true
                      }))
                  }
              })

              Promise.all(promises).then(data=>{
                      console.log("Succesfully updated favor statuses.");
                      res.status(100).send("Favors successfully expired");
                      return;})}

    } )});