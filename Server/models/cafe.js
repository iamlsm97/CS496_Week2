var mongoose = require('mongoose');
require('mongoose-double')(mongoose);
var SchemaTypes = mongoose.Schema.Types;
var Schema = mongoose.Schema;

var cafeSchema = new Schema({
	name: String, // primary key
	time: String,
	lat: SchemaTypes.Double,
	lng: SchemaTypes.Double,
	roastery: String,
	engname: String
}, {versionKey: false});

module.exports = mongoose.model('cafes', cafeSchema);
