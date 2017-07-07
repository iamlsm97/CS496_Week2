// routes/index.js

module.exports = function(app, Models)
{
	// Check Whether Server is Alive
	app.get('/', function(req, res){
		console.log("request to /")
		res.render("index.html")
	})

	// List Available Requests to Server
	app.get('/api', function(req, res){
		console.log("request to /api")
		res.render("list.html")
	})

	// Show Data of whole User
	app.get('/api/userlist', function(req, res){
		console.log("request to /api/userlist")
		Models.User.find(function(err, users){
			if(err) return res.status(500).send({error: 'database failure'})
			res.json(users)
		})
	})

	// Show Data of whole Contact
	app.get('/api/contactlist', function(req, res){
		console.log("request to /api/contactlist")
		Models.Contact.find(function(err, contacts){
			if(err) return res.status(500).send({error: 'database failure'})
			res.json(contacts)
		})
	})

	// Show Data of whole Facebook
	app.get('/api/facebooklist', function(req, res){
		console.log("request to /api/facebooklist")
		Models.Facebook.find(function(err, facebooks){
			if(err) return res.status(500).send({error: 'database failure'})
			res.json(facebooks)
		})
	})

	// Show Data of whole Gallery
	app.get('/api/gallerylist', function(req, res){
		console.log("request to /api/gallerylist")
		Models.Gallery.find(function(err, galleries){
			if(err) return res.status(500).send({error: 'database failure'})
			res.json(galleries)
		})
	})

	// Show Data of Specific User
	app.get('/api/user/:email', function(req, res){
		console.log("request to /api/user/" + req.params.email)
		Models.User.findOne({email: req.params.email}, function(err, user){
			if(err) return res.status(500).json({error: err})
			if(!user) return res.status(404).json({error: 'no such user exists'})
			res.json(user)
		})
	})

	// Show Data of Specific User's Contact
	app.get('/api/user/:email/contact', function(req, res){
		console.log("request to /api/user/" + req.params.email + "/contact")
		Models.User.findOne({email: req.params.email}, function(err, user){
			if(err) return res.status(500).json({error: err})
			if(!user) return res.status(404).json({error: 'no such user exists'})
			Models.Contact.find({_id: {$in: user.contact}}, function(err, contacts){
				if(err) return res.status(500).json({error: err})
				res.json(contacts)
			})
		})
	})

	// Show Data of Specific User's Facebook
	app.get('/api/user/:email/facebook', function(req, res){
		console.log("request to /api/user/" + req.params.email + "/facebook")
		Models.User.findOne({email: req.params.email}, function(err, user){
			if(err) return res.status(500).json({error: err})
			if(!user) return res.status(404).json({error: 'no such user exists'})
			Models.Facebook.find({_id: {$in: user.facebook}}, function(err, facebooks){
				if(err) return res.status(500).json({error: err})
				res.json(facebooks)
			})
		})
	})

	// Show Data of Specific User's Gallery
	app.get('/api/user/:email/gallery', function(req, res){
		console.log("request to /api/user/" + req.params.email + "/gallery")
		Models.User.findOne({email: req.params.email}, function(err, user){
			if(err) return res.status(500).json({error: err})
			if(!user) return res.status(404).json({error: 'no such user exists'})
			Models.Gallery.find({_id: {$in: user.gallery}}, function(err, galleries){
				if(err) return res.status(500).json({error: err})
				res.json(galleries)
			})
		})
	})



}