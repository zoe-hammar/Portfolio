import db_defs

from google.appengine.ext import ndb


def isJSONRequest(self):
    if 'application/json' not in self.request.accept:
        self.response.set_status(406, 
                "Not acceptable, API only replies using JSON")
        self.response.write(self.response.status)
        return False
        
    return True
    
    
def isValidUserID(self):
        user_id = self.request.get('user_id')
        if user_id:
            try:
                user_key = ndb.Key(db_defs.User, int(user_id))
            except ValueError:
                self.response.set_status(400, 
                        "Bad Request, user_id must be an integer")
                self.response.write(self.response.status)
                return False
            user = user_key.get()
            if not user:
                self.response.set_status(404, "User not found")
                self.response.write(self.response.status)
                return False
            if self.request.get('password') != user.password:
                self.response.set_status(400, 
                        "Bad Request, incorrect password")
                self.response.write(self.response.status)
                return False
        else:
            self.response.set_status(400, 
                    "Bad Request, user_id must be provided")
            self.response.write(self.response.status)
            return False
            
        return True