import webapp2
import json
import db_defs
import helpers

from google.appengine.ext import ndb


class User(webapp2.RequestHandler):
    def get(self, **kwargs):
        if not helpers.isJSONRequest(self):
            return

        if 'uid' in kwargs:
            try:
                user_key = ndb.Key(db_defs.User, int(kwargs['uid']))
            except ValueError:
                self.response.set_status(400, 
                        "Bad Request, user ID must be an integer")
                self.response.write(self.response.status)
                return
            user = user_key.get()
            if not user:
                self.response.set_status(404, "User not found")
                self.response.write(self.response.status)
                return
            if self.request.get('password') != user.password:
                self.response.set_status(400, 
                        "Bad Request, incorrect password")
                self.response.write(self.response.status)
                return
            out = user.to_dict()
            self.response.write(json.dumps(out))
        else:
            q = db_defs.User.query()
            results = [{'username': x.username, 'key': x.key.id()}
                        for x in q.fetch()]
            self.response.write(json.dumps(results))
            

    def post(self, **kwargs):
        if not helpers.isJSONRequest(self):
            return

        new_user = db_defs.User()
        username = self.request.get('username').strip(' \t\n\r')
        password = self.request.get('password')
        events = self.request.get_all('events[]')
        calendars = self.request.get_all('calendars[]')

        if username:
            q = db_defs.User.query()
            for x in q.fetch():
                if x.username == username:
                    self.response.set_status(400,
                            "Bad request, that username is already taken")
                    self.response.write(self.response.status)
                    return
            new_user.username = username
        else:
            self.response.set_status(400,
                    "Bad request, username required to create new user")
            self.response.write(self.response.status)
            return
        if password:
            new_user.password = password
        else:
            self.response.set_status(400,
                    "Bad request, password required to create new user")
            self.response.write(self.response.status)
            return
        if events:
            for ev in events:
                try:
                    new_user.events.append(ndb.Key(db_defs.Event, int(ev)))
                except ValueError:
                    self.response.set_status(400,
                            "Bad Request, events[] must have only integers")
                    self.response.write(self.response.status)
                    return
        if calendars:
            for cal in calendars:
                try:
                    new_user.calendars.append(
                            ndb.Key(db_defs.Calendar, int(cal)))
                except ValueError:
                    self.response.set_status(400,
                            "Bad Request, calendars[] must have only integers")
                    self.response.write(self.response.status)
                    return

        new_user.put()
        out = new_user.to_dict()
        self.response.write(json.dumps(out))


    def delete(self, **kwargs):
        if not helpers.isJSONRequest(self):
            return

        if 'uid' in kwargs:
            try:
                user_key = ndb.Key(db_defs.User, int(kwargs['uid']))
            except ValueError:
                self.response.set_status(400, 
                        "Bad Request, user ID must be an integer")
                self.response.write(self.response.status)
                return
            user = user_key.get()
            if not user:
                self.response.set_status(404, "User not found")
                self.response.write(self.response.status)
                return
            if self.request.get('password') != user.password:
                self.response.set_status(400, 
                        "Bad Request, incorrect password")
                self.response.write(self.response.status)
                return
            # delete every calendar an event belonging to the user
            for cal in user.calendars:
                cal.delete()
            for ev in user.events:
                ev.delete()
            user_key.delete()
            # self.response.set_status(200, "User successfully deleted")
            # self.response.write("200, User successfully deleted")
            out = {}
            self.response.write(json.dumps(out))
        else:
            self.response.set_status(400, 
                    "Bad Request, user key must be provided")
            self.response.write(self.response.status)
            return

