# SDP Proposal - Team 4


## Team / App name: Favo

[![Build Status](https://travis-ci.com/favo-sdp/Favo.svg?branch=master)](https://travis-ci.com/favo-sdp/Favo)
[![Maintainability](https://api.codeclimate.com/v1/badges/63b8fcc1446ae6ef57d7/maintainability)](https://codeclimate.com/github/favo-sdp/Favo/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/63b8fcc1446ae6ef57d7/test_coverage)](https://codeclimate.com/github/favo-sdp/Favo/test_coverage)

## Description:

The purpose of the app is to allow people to connect with other users in their area by asking and receiving favors for simple and common daily scenarios. Contrary to regular announces, this app will promote fast interactions due to its location-based notification system. Contrary to service-providing companies, this app is not centralized and will be run by users both to carry out favors and request them.
Possible use cases include lending/renting objects, rallying people for an event, volunteering events communication system, etc. 

The main features of the app we envision are:
1. Allow users to post favor requests/offers with various different settings and details (e.g. urgency, severity, skills required, etc)
2. Utilize a geolocation system to only show posts in a given radius and send push notifications to users within that radius
3. Provide logic to connect users (through integrated messenger) and settle for provided services 
4. Use a feedback-based reputation system to rate users and decrease malicious activity
5. Filter posts according to user's interests and needs to rule out irrelevant notifications

Other optional features we think could be interesting to offer are:
1. Add rich map visualization to interactively see requests and offers around
2. Allow users to ‘provide a service’ based on their expertise
3. Handle payments and transactions (e.g. integrate mobile payment systems like Twint, Paypal) for favors and services to build credit history
4. Personalize user experience with numerous settings and options


## Requirements:

### Split app model:

Which cloud-based service(s) will your app use? For what purpose(s)?
1. Database to store user data, posts and transaction data (Dynamo/Cassandra) 
2. Notification/SMS service (AWS SNS) 
3. Compute/Hosting service (EC2, API Gateway, etc)


### Sensor usage:

What sensor(s) will your app use? For what purpose(s)?
1. Utilize GPS to show posts according to user location.
optional:
2. Use camera for posting details about request/service and for sign-up verification
3. Leverage microphone to initiate in-app voice conversations


### User support:

What will a user be able to do? Are there advantages to being logged in? Will the content be personalized for each user? How?

Users will be able to login with a common third-party service (facebook, google, twitter) and access their account to manage their posts and settings. After logging in, users will be notified if there is a request that matches their abilities or if there's a new event for any of the user's currently active posts. At the same time, users are allowed to change their settings and preferences (minus a couple constant fields) at any time, and personalize their experience in the way they most prefer. Only after logging in can a user ask for a favor, otherwise we can not distinguish trusted users with malicious users.  


### Local cache:

What content will be cached locally?

User's settings and location, drafted posts, and relevant post information that concerns the user will all be stored locally in a database. Not only will this give availability to the latest information even in the case internet connection is lost but also provide good performance and a smooth experience without always relying on the remote database.


### Offline mode:

Which basic functionalities will your offline mode support?

The most important offline functionality will occur if the user loses connection after engaging in a favor. The local database will be synced with the remote database (if out-of-date) as soon as there’s an active internet connection to confirm that the favor was carried out successfully. Users will also be able to create posts as drafts and change their settings.
In some cases, users might be able to do some operations (e.g. write a message, do a transaction) that will be cached locally and executed as soon as the app regains
