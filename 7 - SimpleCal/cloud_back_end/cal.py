import webapp2
import json
import db_defs
import helpers

from google.appengine.ext import ndb
    

class Calendar(webapp2.RequestHandler):
    def get(self, **kwargs):
        if not helpers.isJSONRequest(self):
            return
        
        if not helpers.isValidUserID(self):
            return
            
        user_id = self.request.get('user_id')
        user_key = ndb.Key(db_defs.User, int(user_id))
        user = user_key.get()
        
        if 'cid' in kwargs:
            try:
                cal_key = ndb.Key(user_key.kind(), user_key.id(), 
                                db_defs.Calendar, int(kwargs['cid']))
            except ValueError:
                self.response.set_status(400, 
                        "Bad Request, calendar ID must be an integer")
                self.response.write(self.response.status)
                return
            cal = cal_key.get()
            if not cal:
                self.response.set_status(404, "Calendar not found")
                self.response.write(self.response.status)
                return
            out = cal.to_dict()
            self.response.write(json.dumps(out))
        else:
            q = db_defs.Calendar.query(ancestor=user_key)
            results = [{'name': x.name, 'key': x.key.id()}
                        for x in q.fetch()]
            self.response.write(json.dumps(results))
            
        
    def post(self, **kwargs):
        if not helpers.isJSONRequest(self):
            return
        
        if not helpers.isValidUserID(self):
            return
            
        user_id = self.request.get('user_id')
        user_key = ndb.Key(db_defs.User, int(user_id))
        user = user_key.get()
         
        # make the user be the parent of the event
        new_cal = db_defs.Calendar(parent=user_key)
        name = self.request.get('name').strip(' \t\n\r')
        events = self.request.get_all('events[]')
        new_cal.color = self.request.get('color', default_value='green').strip(' \t\n\r')
        
        if name:
            new_cal.name = name
        else:
            self.response.set_status(400, 
                    "Bad request, name required to create new calendar")
            self.response.write(self.response.status)
            return
        if events:
            for ev in events:
                try:
                    new_cal.events.append(ndb.Key(db_defs.Event, int(ev)))
                except ValueError:
                    self.response.set_status(400, 
                            "Bad Request, events[] must have only integers")
                    self.response.write(self.response.status)
                    return
            
        # store the new calendar
        new_cal.put()
        # add new_cal to the user's list of calendars
        user.calendars.append(new_cal.key)
        user.put()
        # output the new calendar
        out = new_cal.to_dict()
        self.response.write(json.dumps(out))
        
            
    def put(self, **kwargs):
        if not helpers.isJSONRequest(self):
            return
        
        if not helpers.isValidUserID(self):
            return
            
        user_id = self.request.get('user_id')
        user_key = ndb.Key(db_defs.User, int(user_id))
        user = user_key.get()
        
        if 'cid' in kwargs:
            try:
                cal_key = ndb.Key(user_key.kind(), user_key.id(), 
                                db_defs.Calendar, int(kwargs['cid']))
            except ValueError:
                self.response.set_status(400, 
                        "Bad Request, calendar ID must be an integer")
                self.response.write(self.response.status)
                return
            cal = cal_key.get()
            if not cal:
                self.response.set_status(404, "Calendar not found")
                self.response.write(self.response.status)
                return
        else:
            self.response.set_status(400, 
                    "Bad Request, calendar key must be provided")
            self.response.write(self.response.status)
            return
        if 'eid' in kwargs:
            try:
                ev_key = ndb.Key(user_key.kind(), user_key.id(), 
                                db_defs.Event, int(kwargs['eid']))
            except ValueError:
                self.response.set_status(400, 
                        "Bad Request, event ID must be an integer")
                self.response.write(self.response.status)
                return
            ev = ev_key.get()
            if not ev:
                self.response.set_status(404, "Event not found")
                self.response.write(self.response.status)
                return
        else:
            self.response.set_status(400, 
                    "Bad Request, event key must be provided")
            self.response.write(self.response.status)
            return
            
        if ev_key not in cal.events:
            cal.events.append(ev_key)
        else:
            cal.events.remove(ev_key)
        cal.put()
        
        out = cal.to_dict()
        self.response.write(json.dumps(out))
        
        
    def delete(self, **kwargs):
        if not helpers.isJSONRequest(self):
            return
        
        if not helpers.isValidUserID(self):
            return
            
        user_id = self.request.get('user_id')
        user_key = ndb.Key(db_defs.User, int(user_id))
        user = user_key.get()
        
        if 'cid' in kwargs:
            try:
                cal_key = ndb.Key(user_key.kind(), user_key.id(), 
                                    db_defs.Calendar, int(kwargs['cid']))
            except ValueError:
                self.response.set_status(400, 
                        "Bad Request, calendar ID must be an integer")
                self.response.write(self.response.status)
                return
            cal = cal_key.get()
            if not cal:
                self.response.set_status(404, "Calendar not found")
                self.response.write(self.response.status)
                return
            if cal_key in user.calendars:
                user.calendars.remove(cal_key)
                user.put()

            cal_key.delete()
            # self.response.set_status(200, "Calendar successfully deleted")
            # self.response.write("200, Calendar successfully deleted")
            out = {}
            self.response.write(json.dumps(out))
        else:
            self.response.set_status(400, 
                    "Bad Request, calendar key must be provided")
            self.response.write(self.response.status)
            return
            