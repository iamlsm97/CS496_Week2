// app.js

// [LOAD PACKAGES]
let express = require('express');
let app = express();
let bodyParser = require('body-parser');
let mongoose = require('mongoose');
let fs = require('fs')
let multer = require('multer')

// [CONFIGURE APP TO USE bodyParser]
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
	
// [CONFIGURE SERVER PORT]
let port = process.env.PORT || 8080;


app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.engine('html', require('ejs').renderFile);

// [ CONFIGURE mongoose ]
// CONNECT TO MONGODB SERVER
let db = mongoose.connection;
db.on('error', console.error);
db.once('open', function(){
  // CONNECTED TO MONGODB SERVER
  console.log("Connected to mongod server");
});

mongoose.connect('mongodb://localhost/CS496_Week2');

// DEFINE MODEL
let Models = require('./models/user');
let Cafes = require('./models/cafe')

// [CONFIGURE ROUTER]
let router = require('./routes')(app, Models, Cafes, fs, multer)

// [RUN SERVER]
let server = app.listen(port, function(){
 console.log("Express server has started on port " + port)
});