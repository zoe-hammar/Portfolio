import webapp2
import json
import db_defs
import helpers
from datetime import datetime

from google.appengine.ext import ndb
    

class Event(webapp2.RequestHandler):
    def get(self, **kwargs):
        if not helpers.isJSONRequest(self):
            return
        
        if not helpers.isValidUserID(self):
            return
            
        user_id = self.request.get('user_id')
        user_key = ndb.Key(db_defs.User, int(user_id))
        user = user_key.get()
        
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
            out = ev.to_dict()
            self.response.write(json.dumps(out))
        else:
            q = db_defs.Event.query(ancestor=user_key)
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
        new_event = db_defs.Event(parent=user_key)
        name = self.request.get('name').strip(' \t\n\r')
        date_time = self.request.get('date_time')
        new_event.color = self.request.get('color', default_value='blue').strip(' \t\n\r')
        
        if name:
            new_event.name = name
        else:
            self.response.set_status(400, 
                    "Bad request, name required to create new event")
            self.response.write(self.response.status)
            return
        if date_time:
            try:
                new_event.date_time = datetime.strptime(
                                    date_time, "%m/%d/%y %H:%M")
            except ValueError:
                self.response.set_status(400, 
                        "Bad request, date_time must have format of \"m/d/yy h:m\" (military time)")
                self.response.write(self.response.status)
                return
        else:
            new_event.date_time = datetime.today()

        # store and output the new event
        new_event.put()
        # add new_event to the user's list of events
        user.events.append(new_event.key)
        user.put()
        # output the new event
        out = new_event.to_dict()
        self.response.write(json.dumps(out))
        

    def put(self, **kwargs):
        if not helpers.isJSONRequest(self):
            return
        
        if not helpers.isValidUserID(self):
            return
            
        user_id = self.request.get('user_id')
        user_key = ndb.Key(db_defs.User, int(user_id))
        user = user_key.get()
        
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

            new_name = self.request.get('name').strip(' \t\n\r')
            new_date_time = self.request.get('date_time')
            new_color = self.request.get('color').strip(' \t\n\r')
            
            if new_name:
                ev.name = new_name
            if new_date_time:
                try:
                    ev.date_time = datetime.strptime(
                                        new_date_time, "%m/%d/%y %H:%M")
                except ValueError:
                    self.response.set_status(400, 
                            "Bad request, date_time must have format of \"m/d/yy h:m\" (military time)")
                    self.response.write(self.response.status)
                    return
            if new_color:
                ev.color = new_color
            ev.put()
            out = ev.to_dict()
            self.response.write(json.dumps(out))
        else:
            self.response.set_status(400, 
                    "Bad Request, event key must be provided")
            self.response.write(self.response.status)
            return
    
        
    def delete(self, **kwargs):
        if not helpers.isJSONRequest(self):
            return
        
        if not helpers.isValidUserID(self):
            return
            
        user_id = self.request.get('user_id')
        user_key = ndb.Key(db_defs.User, int(user_id))
        user = user_key.get()
        
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
            for cal_key in user.calendars:
                cal = cal_key.get()
                if ev_key in cal.events:
                    cal.events.remove(ev_key)
                    cal.put()
            if ev_key in user.events:
                user.events.remove(ev_key)
                user.put()

            ev_key.delete()
            # self.response.set_status(200, "Event successfully deleted")
            # self.response.write("200, Event successfully deleted")
            out = {}
            self.response.write(json.dumps(out))
        else:
            self.response.set_status(400, 
                    "Bad Request, event key must be provided")
            self.response.write(self.response.status)
            return
        