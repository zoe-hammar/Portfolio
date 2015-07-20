from datetime import datetime

from google.appengine.ext import ndb


class Model(ndb.Model):
    def to_dict(self):
        d = super(Model, self).to_dict()
        d['key'] = self.key.id()
        return d
        

class Event(Model):
    name = ndb.StringProperty(required=True)
    date_time = ndb.DateTimeProperty(required=True)
    color = ndb.StringProperty(required=True)
    
    def to_dict(self):
        d = super(Event, self).to_dict()
        d['date_time'] = self.date_time.strftime("%m/%d/%y %H:%M")
        return d

        
class Calendar(Model):
    name = ndb.StringProperty(required=True)
    events = ndb.KeyProperty(repeated=True)
    color = ndb.StringProperty(required=True)
    
    def to_dict(self):
        d = super(Calendar, self).to_dict()
        d['events'] = [e.id() for e in d['events']]
        return d
    
    
class User(Model):
    username = ndb.StringProperty(required=True)
    password = ndb.StringProperty(required=True)
    events = ndb.KeyProperty(repeated=True)
    calendars = ndb.KeyProperty(repeated=True)
    
    def to_dict(self):
        d = super(User, self).to_dict()
        d['events'] = [e.id() for e in d['events']]
        d['calendars'] = [c.id() for c in d['calendars']]
        return d
        