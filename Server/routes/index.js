// routes/index.js

module.exports = function(app, Models, fs, multer)
{
	// Check Whether Server is Alive
	app.get('/', function(req, res){
		console.log("request to /");
		res.render("index.html");
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
	app.post('/api/adduser/', function(req, res){
		console.log("request to /api/adduser")
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



	// multertest
	app.post('/multertest', function(req, res){
		console.log("-----multertest-----")
		console.log(req.body)
		console.log(req.body.name)
		var upload = multer().single("rain")
		upload(req, res, function(err){
			if (err) console.log("ERROR ERROR ERROR ERROR ERROR");
			console.log(req.body.number)
			console.log(req.file)
		})
	})

	

	// Add Contact to User
	app.put('/api/:email/addcontact', async function(req, res){
		console.log("request to /api/"+req.params.email+"/addcontact")

		stop = false
		email = req.params.email

		function fileFilter (req, file, cb){
			Models.User.findOne({email: email}, function(err, user){
				if (err) {
					stop = true
					cb(null, false)
					return res.status(500).json({error: err});
				}
				if (!user) {
					stop = true
					cb(null, false)
					res.status(404).json({error: 'no such user exists'});
				}
				console.log(user)
			})
			cb(null, true)
		}

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


		var upload = multer({storage: storage, fileFilter: fileFilter}).single('profile_image')

		upload(req, res, function(err){
			if (stop) return ;
			if (err) return res.status(500).json({error: err});

			Models.User.findOne({email: email}, function(err, user){
				Models.Contact.findOne({_id: {$in: user.contact}, number: req.body.number}, function(err, contact){
					if (err) return res.status(500).json({error: err});
					if (!contact) {
						console.log("Add New User Contact")
						newcontact = new Models.Contact()
						newcontact.name = req.body.name
						newcontact.number = req.body.number
						console.log(req.file)
						newcontact.profile_image = 'images/'+email+"/contact/"+req.file.uploadFile.name+'.'+req.file.uploadFile.ext

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
						contact.profile_image = 'images/'+email+"/contact/"+req.file.uploadFile.name+'.'+req.file.uploadFile.ext

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
	app.post('/api/addfacebook', function(req, res){
		console.log("request to /api/addfacebook")
		res.end()
	})	

	// Add Gallery to User
	app.post('/api/addgallery', function(req, res){
		console.log("request to /api/addgallery")
		res.end()
	})





	

	// Image Test
	app.post('/image/:filename',  function(req, res){
		console.log("-----Image Test-----")

		var storage = multer.diskStorage({
			destination: function(req, file, cb){
				cb(null, 'rongrong')
			},
			filename: function(req, file, cb){
				file.uploadFile = {
					name: req.params.filename,
					ext: file.mimetype.split('/')[1]
				}
				cb(null, file.uploadFile.name+'.'+file.uploadFile.ext)
			}
		})
		var upload = multer({storage: storage}).single('testimage')

		upload(req, res, function(err){
			return
			console.log("entered upload")
			if(err) return res.status(500).json({error: err});
			console.log(req)
			console.log(req.file)
		})



		// upload(req, res, function(err){
		// 	if(err) return res.status(500).json({error: err})
		// })

		// upload(req, res).then(function (file) {
		// 	res.json(file);
		// }, function (err) {
		// 	res.send(500, err);
		// });

	})





}