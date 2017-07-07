var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var userSchema = new Schema({
	email: String,
	contact: Array,
	facebook: Array,
	gallery: Array
});

var contactSchema = new Schema({
	name: String,
	number: String,
	profile_image: String
})

var facebookSchema = new Schema({
	name: String,
	profile_image: String
})

var gallerySchema = new Schema({
	gallery: String
})


module.exports = {
	User: mongoose.model('user', userSchema),
	Contact: mongoose.model('contact', contactSchema),
	Facebook: mongoose.model('facebook', facebookSchema),
	Gallery: mongoose.model('gallery', gallerySchema)
}

