// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();
var db = admin.firestore();

// global constants
const REQUESTED_STATUS = 0;
const ACCEPTED_STATUS = 1;
const EXPIRED_STATUS = 2;
const CANCELLED_REQUESTER_STATUS = 3;
const CANCELLED_ACCEPTER_STATUS = 4;
const SUCCESSFULLY_COMPLETED_STATUS = 5;
const COMPLETED_REQUESTER_STATUS = 6;
const COMPLETED_ACCEPTER_STATUS = 7;

const CHAT_TYPE = "chat";
const UPDATE_TYPE = "update";

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

function sendMulticastMessage(message) {
    return admin.messaging().sendMulticast(message)
        .then((response) => {
            if (response.failureCount > 0) {
                var failedTokens = [];
                response.responses.forEach((resp, idx) => {
                    if (!resp.success) {
                        failedTokens.push(message.tokens[idx]);
                    }
                });

                if (failedTokens.length > 0) {
                    return console.log('List of tokens that caused failures: ' + failedTokens);
                }
            }
        });
}

function sendMessageToUsers(notification, userIds, type) {

    // go through all the userIds of the favor
    userIds.forEach(function (id) {

        // get user notification id
        db.collection('/users').where('id', '==', id).get()
            .then((snapshot) => {
                snapshot.forEach((doc) => {
                    const user = doc.data();
                    const notificationId = user.notificationId;
                    //console.log('user', user.name);

                    if ((type === UPDATE_TYPE && user.updateNotifications) || (type === CHAT_TYPE && user.chatNotifications)) {
                        notification.tokens.push(notificationId);
                    }
                });

                console.log('user', notification.tokens);

                return sendMulticastMessage(notification);
            })
    });
}

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
        return db.collection('/users').get()
            .then((snapshot) => {
                snapshot.forEach((doc) => {
                    //Checking each user for location distance from the post
                    var user = doc.data();
                    var latUser = user.location.latitude;
                    var longUser = user.location.longitude;
                    var distance = distanceInKm(longFav, latFav, longUser, latUser);
                    if (distance < user.notificationRadius && user.id !== posterId && user.activeAcceptingFavors === 0 && user.activeRequestingFavors === 0) {
                        usersIds.push(user.notificationId)
                    }
                });

                return sendMulticastMessage(message, usersIds);
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

            // get requesterId, that's always present
            const requesterId = userIds[0];

            // get accepterId, if it exists
            var accepterId = "";
            if (userIds.length > 1) {
                accepterId = userIds[1];
            }

            var receivers = [];
            var titleToSend = "";

            // send notification in case of new status
            if (oldStatus !== newStatus) {

                //console.log('Status has changed');

                // user has been accepted for favor, send notification to accepter
                if (newStatus === ACCEPTED_STATUS) {
                    //console.log('Favor has been accepted');
                    titleToSend = "You have been accepted for favor " + favorTitle;
                    receivers = [accepterId];
                }

                // favor has been cancelled by requester, send notification to accepter, if it exists
                if (newStatus === CANCELLED_REQUESTER_STATUS) {
                    //console.log('Favor has been cancelled by the requester');
                    titleToSend = "Favor " + favorTitle + " has been cancelled by the requester";
                    receivers = [accepterId];
                }

                // favor has been cancelled by accepter, send notification to requester
                if (newStatus === CANCELLED_ACCEPTER_STATUS) {
                    //console.log('Favor has been cancelled by the accepter');
                    titleToSend = "Favor " + favorTitle + " has been cancelled by the accepter";
                    receivers = [requesterId];
                }

                // favor has been completed by accepter, send notification to requester
                if (newStatus === COMPLETED_ACCEPTER_STATUS) {
                    titleToSend = "Favor " + favorTitle + " has been completed by the accepter";
                    receivers = [requesterId];
                }

                // favor has been completed by requester, send notification to accepter
                if (newStatus === COMPLETED_REQUESTER_STATUS) {
                    //console.log('Favor has been cancelled by the accepter');
                    titleToSend = "Favor " + favorTitle + " has been marked as completed by the requester";
                    receivers = [accepterId];
                }

                // favor has been successfully completed, send notification to both requester and accepter
                if (newStatus === SUCCESSFULLY_COMPLETED_STATUS) {
                    titleToSend = "Favor " + favorTitle + " has been successfully completed";
                    receivers = [requesterId, accepterId];
                }

            } else {

                // if no user committed, the favor has changed
                if (oldFavor.userIds.length === userIds.length) {
                    titleToSend = "Favor " + favorTitle + " has been modified";
                    receivers = userIds;
                    receivers.shift();
                } else {

                    if (oldFavor.userIds.length < userIds.length) {
                        titleToSend = "A user committed to favor " + favorTitle;
                    } else {
                        titleToSend = "A user cancelled their commit to favor " + favorTitle;
                    }

                    // take all the users except the requester (first one in the list)
                    receivers = [requesterId];
                }
            }

            console.log('receivers:', receivers);

            if (receivers !== []) {
                // prepare message
                var message = {
                    data: {
                        FavorId: updatedFavor.id,
                    },
                    notification: {
                        title: titleToSend,
                        body: 'Click to check out the post details',
                    },

                    tokens: []
                };

                sendMessageToUsers(message, receivers, UPDATE_TYPE)
            }
        }
    );


exports.expireOldFavorsOnCreate = functions.firestore
    .document('favors/{favorId}')
    .onCreate(() => {

        const TIME_IN_DAYS = 1;
        const MAX_UPDATES = 20;
        var now = Date.now();
        var cutoffTime = now - TIME_IN_DAYS * 24 * 60 * 60 * 1000;
        var cutoff = admin.firestore.Timestamp.fromMillis(cutoffTime)

        let query = db.collection('/favors');

        return query.where("statusId", "==", REQUESTED_STATUS)
            .orderBy("postedTime", "asc").endAt(cutoff).get()
            .then(snapshot => {
                if (snapshot.empty) {
                    console.log("Snapshot is empty")
                } else {
                    let batch = db.batch();
                    var totalUpdateCount = 0;
                    var userUpdates = new Map(); //hashmap with user key and int value
                    const fieldValue = admin.firestore.FieldValue;
                    try {
                        snapshot.forEach(doc => {
                            let favor = doc.data();
                            //console.log(postedTime,",",cutoff);
                            if (favor.userIds.length === 1) {
                                totalUpdateCount++;
                                var userId = favor.requesterId;
                                if (userUpdates.has(userId)) { //update count in map
                                    let newValue = userUpdates.get(userId) - 1;
                                    userUpdates.set(userId, newValue);
                                } else { //insert user in map
                                    totalUpdateCount++;
                                    if (totalUpdateCount > MAX_UPDATES)
                                        throw BreakException;
                                    userUpdates.set(userId, -1);
                                }
                                //console.log("updated");
                                batch.update(doc.ref, {'statusId': EXPIRED_STATUS, 'isArchived': true});
                            }
                        });
                    } catch (e) {
                        console.log("Reached max updates per transaction (20)");
                        if (e !== BreakException)
                            throw e;
                    } //very ugly way of breaking out of foreach loop
                    for (const [userId, updateCount] of userUpdates.entries()) {
                        var userRef = db.collection("/users").doc(userId);
                        batch.update(userRef, {'activeRequestingFavors': fieldValue.increment(updateCount)});
                    }
                    return batch.commit().then(() => console.log("Favors and users successfully updated"));
                }

            }).catch(reason => {
                console.log("Failed expiring docs", reason)
            });
    });


// send new favor notification to users on updates
exports.sendNotificationOnNewChat = functions.firestore
    .document('/chats/{chatId}')
    .onCreate((snap) => {
        const message = snap.data();
        const senderId = message.uid;
        const favorId = message.favorId;

        // find favor with selected favorId
        return db.collection('/favors').where('id', '==', favorId).get()
            .then((snapshot) => {
                snapshot.forEach((doc) => {
                    var favor = doc.data();
                    var userIds = favor.userIds;

                    var notification = {
                        data: {
                            FavorId: favorId,
                        },
                        notification: {
                            title: 'New message in the chat of favor ' + favor.title,
                            body: 'Click to check out the post details',
                        },
                        tokens: []
                    };

                    userIds.splice(userIds.indexOf(senderId), 1)
                    sendMessageToUsers(notification, userIds, CHAT_TYPE)
                });
            })
    });

// delete all favors when a user is deleted
exports.deleteFavorsOnUserDeletion = functions.firestore
    .document('users/{userId}')
    .onDelete((snap) => {

        const deletedUserId = snap.data().id;

        // go through all the users
        return db.collection('/favors').where('userIds', 'array-contains', deletedUserId).get()
            .then((snapshot) => {
                snapshot.forEach((doc) => {
                    const favor = doc.data();

                    if (favor.userIds.length === 1) {
                        doc.ref.delete().then(() => {
                            console.log("Favor successfully deleted!");
                        }).catch(function (error) {
                            console.error("Error deleting favor: ", error);
                        });
                    }
                });
            })
    });


// delete all chats messages when a favor is deleted
exports.deleteChatOnFavorDeletion = functions.firestore
    .document('favors/{favorId}')
    .onDelete((snap) => {

        const deletedFavorId = snap.data().id;

        // go through all the chat messages
        return db.collection('/chats').where('favorId', '==', deletedFavorId).get()
            .then((snapshot) => {
                snapshot.forEach((doc) => {
                    doc.ref.delete().then(() => {
                        console.log("Chat message successfully deleted!");
                    }).catch(function (error) {
                        console.error("Error deleting chat message: ", error);
                    });
                });
            })
    });