var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var userSchema = new Schema({
	email: String, // primary key
	contact: Array,
	facebook: Array,
	gallery: Array
}, {versionKey: false});

var contactSchema = new Schema({
	name: String,
	number: String, // primary key
	profile_image: String
}, {versionKey: false})

var facebookSchema = new Schema({
	id: String, // primary key
	name: String,
	profile_image: String
}, {versionKey: false})

var gallerySchema = new Schema({
	gallery: String
}, {versionKey: false})


module.exports = {
	User: mongoose.model('user', userSchema),
	Contact: mongoose.model('contact', contactSchema),
	Facebook: mongoose.model('facebook', facebookSchema),
	Gallery: mongoose.model('gallery', gallerySchema)
}

