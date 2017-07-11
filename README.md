# [CS496_Week2]
## Project Name: 퇴근시간 (Quitting Time)
### Team: [HyungJun](https://github.com/diamond264), [SeungMin Lee](https://github.com/iamlsm97)

## 1. Client
- Android (java, xml)

#### Tab A [Contacts]
- Should be logged in Facebook to use this Tab
- Send whole contacts list of device to server at very first use
- Load contacts list from server
- Can search contacts list instantly
- Can call to the number directly and indirectly

#### Tab B [Facebook]
- Should be logged in Facebook to use this Tab
- Send whole facebook friends list to server at very first use
- Load facebook friends list from server
- Can search facebook friends list instantly

#### Tab C [Gallery]
- Should be logged in Facebook to use this Tab
- Send whole images of device to server at very first use
- Load images from server with [Glide](https://github.com/bumptech/glide)
- Can add images to server from device's gallery
- Can change number of images in a row by moving the seek bar

#### Tab D [Cafe Map]
- You can use this Tab without Facebook login
- Search Cafe name. Autocomplete will help you searching
- Used Google Map Api for showing location of the Cafe
- When click on the pin, information will be shown on the map
- Name/Time/Type information of the Cafe will be shown


#### Tab E [HTTP Test]
- You can use this Tab without Facebook login
- Simple tab for sending HTTP requests
- Only HTTP requests that our server can handle are sendable


## 2. Server
- Node.JS, mongoDB
- Code dependencies: express, boody-parser, mongoose, mongoose-double, fs, multer
- Server host: 13.124.143.15

Install dependencies:
```bash
$ npm install
```
Run server:
```bash
$ node app.js
```

- Our server is running on [`http://13.124.143.15:8080`](http://13.124.143.15:8080) (It may be discontinued without prior notice)

### RESTful API
- GET /
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
- POST /api/adduser
    - Add a New User
- PUT /api/:email/addcontact
    - Add a single Contact data to specific User
- PUT /api/:email/addfacebook
    - Add a single Facebook data to specific User
- POST /api/:email/addimage
    - Add a single Image File to specific User's Gallery
- GET /images/*
    - Download Image from Url Path
- GET /api/cafelist
    - Returns detailed data of Cafe List in JSON

### Server Structure
- images/
    - username@email.com/
        - contact/ : stores a specific user's contact profile images
        - facebook/ : stores a specific user's facebook profile images
        - gallery/ : stores a specific user's gallery images
- models/
    - cafe.js: mongoDB Schema of cafe
    - user.js: mongoDB Schema of user, contact, facebook, gallery
- routes/
    - index.js: routing Server code
- views/
- app.js: main JavaScript Server code
- package.json: include code dependencies