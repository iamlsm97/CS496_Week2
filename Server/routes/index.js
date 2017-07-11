// routes/index.js

module.exports = function(app, Models, Cafes, fs, multer)
{
	// Check Whether Server is Alive
	app.get('/', function(req, res){
		console.log("request to /");
		res.render("index.html");
	})

	// Show Data of Cafe List
	app.get('/api/cafelist', function(req, res){
		console.log("request to /api/cafelist")
		Cafes.find(function(err, cafes){
			if (err) return res.status(500).send({error: 'database failure'});
			res.json(cafes);
		})
	})

	// Download Image from Url Path
	app.get('/images/*', function(req, res){
		console.log("request to "+req.originalUrl);
		// console.log(__dirname)
		// console.log(req.protocol)
		// console.log(req.get('host'))
		// console.log(req.originalUrl)
		// console.log(req.protocol+"://"+req.get('host')+req.originalUrl)
		res.download(__dirname + '/..'+req.originalUrl)
	})

	// List Available Requests to Server
	app.get('/api', function(req, res){
		console.log("request to /api");
		res.render("list.html");
	})

	// Show Data of whole User
	app.get('/api/userlist', function(req, res){
		console.log("request to /api/userlist")
		Models.User.find(function(err, users){
			if (err) return res.status(500).send({error: 'database failure'});
			res.json(users);
		})
	})

	// Show Data of whole Contact
	app.get('/api/contactlist', function(req, res){
		console.log("request to /api/contactlist")
		Models.Contact.find(function(err, contacts){
			if (err) return res.status(500).send({error: 'database failure'});
			res.json(contacts);
		})
	})

	// Show Data of whole Facebook
	app.get('/api/facebooklist', function(req, res){
		console.log("request to /api/facebooklist")
		Models.Facebook.find(function(err, facebooks){
			if (err) return res.status(500).send({error: 'database failure'});
			res.json(facebooks);
		})
	})

	// Show Data of whole Gallery
	app.get('/api/gallerylist', function(req, res){
		console.log("request to /api/gallerylist")
		Models.Gallery.find(function(err, galleries){
			if (err) return res.status(500).send({error: 'database failure'});
			res.json(galleries);
		})
	})

	// Show Data of Specific User
	app.get('/api/:email', function(req, res){
		console.log("request to /api/" + req.params.email)
		Models.User.findOne({email: req.params.email}, function(err, user){
			if (err) return res.status(500).json({error: err});
			if (!user) return res.status(404).json({error: 'no such user exists'});
			res.json(user);
		})
	})

	// Show Data of Specific User's Contact
	app.get('/api/:email/contact', function(req, res){
		console.log("request to /api/" + req.params.email + "/contact")
		Models.User.findOne({email: req.params.email}, function(err, user){
			if (err) return res.status(500).json({error: err});
			if (!user) return res.status(404).json({error: 'no such user exists'});
			Models.Contact.find({_id: {$in: user.contact}}, function(err, contacts){
				if (err) return res.status(500).json({error: err});
				res.json(contacts);
			})
		})
	})

	// Show Data of Specific User's Facebook
	app.get('/api/:email/facebook', function(req, res){
		console.log("request to /api/" + req.params.email + "/facebook")
		Models.User.findOne({email: req.params.email}, function(err, user){
			if (err) return res.status(500).json({error: err});
			if (!user) return res.status(404).json({error: 'no such user exists'});
			Models.Facebook.find({_id: {$in: user.facebook}}, function(err, facebooks){
				if (err) return res.status(500).json({error: err});
				res.json(facebooks);
			})
		})
	})

	// Show Data of Specific User's Gallery
	app.get('/api/:email/gallery', function(req, res){
		console.log("request to /api/" + req.params.email + "/gallery")
		Models.User.findOne({email: req.params.email}, function(err, user){
			if (err) return res.status(500).json({error: err});
			if (!user) return res.status(404).json({error: 'no such user exists'});
			Models.Gallery.find({_id: {$in: user.gallery}}, function(err, galleries){
				if (err) return res.status(500).json({error: err});
				res.json(galleries);
			})
		})
	})

	// Add New User
	app.post('/api/adduser', function(req, res){
		console.log("request to /api/adduser")

		if (req.body.email == null){
			return res.status(400).json({error: 'email not found in request'});
		}

		Models.User.findOne({email: req.body.email}, function(err, user){
			if (err) return res.status(500).json({error: err});
			if (user) return res.status(406).json({error: 'user already exists'});

			var newuser = new Models.User();
			newuser.email = req.body.email
			newuser.contact = []
			newuser.facebook = []
			newuser.gallery = []

			newuser.save(function(err){
				if (err) {
					console.log(err)
					res.json({result: 0})
					return
				}
				res.json({result: 1})
			})

			fs.mkdir("images/"+req.body.email, function(err){
				if (err) console.log(err)
			})
			fs.mkdir("images/"+req.body.email+"/contact", function(err){
				if (err) console.log(err)
			})
			fs.mkdir("images/"+req.body.email+"/facebook", function(err){
				if (err) console.log(err)
			})
			fs.mkdir("images/"+req.body.email+"/gallery", function(err){
				if (err) console.log(err)
			})

		})
	})

	// Add Contact to User
	app.put('/api/:email/addcontact', async function(req, res){
		console.log("request to /api/"+req.params.email+"/addcontact")

		email = req.params.email

		var storage = multer.diskStorage({
			destination: function(req, file, cb){
				cb(null, 'images/'+email+"/contact")
			},
			filename: function(req, file, cb){
				file.uploadFile = {
					name: req.body.number,
					ext: file.mimetype.split('/')[1]
				}
				cb(null, file.uploadFile.name+'.'+file.uploadFile.ext)
			}
		})

		var upload = multer({storage: storage}).single('profile_image')

		upload(req, res, function(err){
			if (err) return res.status(500).send({error: err});

			if(req.body.number == ""){ 
				return res.send({error: 'number not found in request'});
			}

			Models.User.findOne({email: email}, function(err, user){
				if (err) return res.status(500).send({error: err});	
				if (!user) {
					return res.send({error: 'no such user exists'});
				}

				Models.Contact.findOne({_id: {$in: user.contact}, number: req.body.number}, function(err, contact){
					if (err) return res.status(500).send({error: err});
					if (!contact) {
						console.log("Add New User Contact")
						newcontact = new Models.Contact()
						newcontact.name = req.body.name
						newcontact.number = req.body.number
						if(req.file!=null){
							console.log(req.file)
							newcontact.profile_image = 'images/'+email+"/contact/"+req.file.uploadFile.name+'.'+req.file.uploadFile.ext
						}
						newcontact.save(function(err){
							if (err) {
								console.log(err)
								res.json({result: 0})
								return
							}
							res.json({result: 1})

						})
						user.contact.push(newcontact._id)
						user.save()
					} else {
						console.log("Edit Existing User Contact")
						contact.name = req.body.name
						contact.number = req.body.number
						if(req.file!=null){
							console.log(req.file)
							contact.profile_image = 'images/'+email+"/contact/"+req.file.uploadFile.name+'.'+req.file.uploadFile.ext
						} else {
							contact.profile_image = null
						}

						contact.save(function(err){
							if (err) {
								console.log(err)
								res.json({result: 0})
								return
							}
							res.json({result: 1})
						})
					}
				})
			})
		})
	})


	// Add Facebook to User
	app.put('/api/:email/addfacebook', async function(req, res){
		console.log("request to /api/"+req.params.email+"/addfacebook")

		email = req.params.email

		var storage = multer.diskStorage({
			destination: function(req, file, cb){
				cb(null, 'images/'+email+"/facebook")
			},
			filename: function(req, file, cb){
				file.uploadFile = {
					name: req.body.id,
					ext: file.mimetype.split('/')[1]
				}
				cb(null, file.uploadFile.name+'.'+file.uploadFile.ext)
			}
		})

		var upload = multer({storage: storage}).single('profile_image')

		upload(req, res, function(err){
			console.log("into the upload")
			if (err) return res.status(500).send({error: err});

			if (req.body.id == ""){
				return res.send({error: 'id not found in request'});
			}

			Models.User.findOne({email: email}, function(err, user){
				if (err) return res.status(500).send({error: err});
				if (!user) {
					return res.send({error: 'no such user exists'});
				}

				Models.Facebook.findOne({_id: {$in: user.facebook}, id: req.body.id}, function(err, facebook){
					if (err) return res.status(500).send({error: err});
					if (!facebook) {
						console.log("Add New User Facebook Friends")
						newfacebook = new Models.Facebook()
						newfacebook.name = req.body.name
						newfacebook.id = req.body.id
						if (req.file!=null){
							console.log(req.file)
							newfacebook.profile_image = 'images/'+email+"/facebook/"+req.file.uploadFile.name+'.'+req.file.uploadFile.ext
						}
						newfacebook.save(function(err){
							if (err) {
								console.log(err)
								res.json({result: 0})
								return
							}
							res.json({result: 1})

						})
						user.facebook.push(newfacebook._id)
						user.save()
					} else {
						console.log("Edit Existing User Facebook Friends")
						facebook.id = req.body.id
						facebook.name = req.body.name
						if(req.file!=null){
							console.log(req.file)
							facebook.profile_image = 'images/'+email+"/facebook/"+req.file.uploadFile.name+'.'+req.file.uploadFile.ext
						} else {
							facebook.profile_image = null
						}
						
						contact.save(function(err){
							if (err) {
								console.log(err)
								res.json({result: 0})
								return
							}
							res.json({result: 1})
						})
					}
				})
			})
		})
	})	

	// Add Image to User's Gallery
	app.post('/api/:email/addimage', function(req, res){
		console.log("request to /api/"+req.params.email+"/addimage")

		email = req.params.email

		var storage = multer.diskStorage({
			destination: function(req, file, cb){
				cb(null, 'images/'+email+"/gallery")
			},
			filename: function(req, file, cb){
				file.fixedname = file.originalname.replace(/\s+/g, '');
				cb(null, file.fixedname)
				
			}
		})

		var upload = multer({storage: storage}).single('image')

		upload(req, res, function(err){

			if (req.file==null){
				console.log(req.file)
				return res.send({error: 'no image attached'});
			}

			console.log(req.file)
			if (err) return res.status(500).send({error: err});

			Models.User.findOne({email: email}, function(err, user){
				if (err) return res.status(500).send({error: err});	
				if (!user) {
					return res.send({error: 'no such user exists'});
				}
				newimage = new Models.Gallery()
				newimage.image = 'images/'+email+"/gallery/"+req.file.fixedname
				newimage.save(function(err){
					if (err) {
						console.log(err)
						res.json({result: 0})
						return
					}
					res.json({result: 1})
				})
				user.gallery.push(newimage._id)
				user.save()
			})
		})


	})

}