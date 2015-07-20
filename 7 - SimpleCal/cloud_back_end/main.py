import webapp2

app = webapp2.WSGIApplication(debug=True)
app.router.add(webapp2.Route(r'/event<slash:/?>', 'event.Event'))
app.router.add(webapp2.Route(r'/event/<eid:[^/]+><:/?>', 'event.Event'))
app.router.add(webapp2.Route(r'/calendar<slash:/?>', 'cal.Calendar'))
app.router.add(webapp2.Route(r'/calendar/<cid:[^/]+><:/?>', 'cal.Calendar'))
app.router.add(webapp2.Route(
        r'/calendar/<cid:[^/]+>/event/<eid:[^/]+><:/?>', 'cal.Calendar'))
app.router.add(webapp2.Route(r'/user<slash:/?>', 'user.User'))
app.router.add(webapp2.Route(r'/user/<uid:[^/]+><:/?>', 'user.User'))