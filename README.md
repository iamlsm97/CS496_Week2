# [CS496_Week2]
## Project Name: 
### Team: 윤형준, 이승민

## 1. Client
- Android, java, xml

## 2. Server
- Node.JS, express.js, mongoDB
- Server host: 13.124.143.15

<!--
Install dependencies:
```bash
$ npm install
```

Run server:
```bash
$ node app.js
```
-->

- Our server is running on [`http://13.124.143.15:8080`](http://13.124.143.15:8080)

### RESTful API
- GET /api
    - Check Whether Server is Alive

- GET /api
    - List Available Requests to Server

- GET /api/userlist
    - Returns detailed data of whole users in JSON

- GET /api/contactlist
    - Returns detailed data of whole contacts in JSON

- GET /api/facebooklist
    - Returns detailed data of whole faceboks in JSON

- GET /api/gallerylist
    - Returns detailed data of whole galleries in JSON

- GET /api/user/:email
    - Returns detailed data of a specific user in JSON

- GET /api/user/:email/contact
    - Returns detailed data of specific user's contacts in JSON

- GET /api/user/:email/facebook
    - Returns detailed data of specific user's facebooks in JSON

- GET /api/user/:email/gallery
    - Returns detailed data of specific user's galleries in JSON


### Server Structure
