function createPost() {
  var req = new XMLHttpRequest();
  if (!req) {
    throw 'Unable to create HttpRequest.';
  }

  var heading = document.getElementsByName('heading')[0].value;
  var body = document.getElementsByName('body')[0].value;

  var params = [];
  params['heading'] = heading;
  params['body'] = body;

  var encodedParams = [];
  for (var prop in params) {
    var pair = encodeURIComponent(prop) +
              '=' + encodeURIComponent(params[prop]);
    encodedParams.push(pair);
  }

  req.onreadystatechange = function() {
    if (req.readyState === 4 && req.status === 200) {
      //var smb = JSON.parse(this.responseText);
      if (req.responseText == 'success') {
        var fieldset = document.createElement('fieldset');
        var legend = document.createElement('legend');
        var h1 = document.createElement('h1');
        var headingTxt = document.createTextNode(heading);
        h1.appendChild(headingTxt);
        legend.appendChild(h1);
        var p = document.createElement('p');
        var bodyTxt = document.createTextNode(body);
        p.appendChild(bodyTxt);
        var p2 = document.createElement('p');
        var auth = "Posted by ";
        var authorTxt = document.createTextNode(auth);
        p2.appendChild(authorTxt);
        fieldset.appendChild(legend);
        fieldset.appendChild(p);
        fieldset.appendChild(p2);
        document.getElementById('addedContent').appendChild(fieldset);
      } else {
        document.getElementById('insertError').innerHTML = req.responseText;
      }
    }
  };

  req.open('POST', 'insertSmb.php');
  req.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
  req.send(encodedParams.join('&'));
}