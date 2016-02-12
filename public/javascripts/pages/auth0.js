function signin() {
  var lock = new Auth0Lock('dR7GdOhtI3HFNxfm4HySDL4Ke8uyGfTe', 'conveyal.eu.auth0.com');

  // check if this is an SSO callback
  var hash = lock.parseHash(window.location.hash);
  if (hash && hash.id_token) {
    // the user came back from the login (either SSO or regular login),
    loginWithToken(hash.id_token, hash.state || '');
    return;
  }

  // check if logged in elsewhere via SSO
  lock.$auth0.getSSOData(function(err, data) {
    if (!err && data.sso) {
      // there is! redirect to Auth0 for SSO
      lock.$auth0.signin({
        state: redirectTo,
        callbackOnLocationHash: true
      });
    } else { // assume that we are not logged in

      lock.show(function(err, profile, token) {
        if(err) {
          console.log(err)
        } else {
          loginWithToken(token, "/");
        }
      }, {
        container: 'auth0login'
      });
    }
  });
}

function loginWithToken(token, redirectTo) {
  // save profile and token to localStorage
  localStorage.setItem('userToken', token);

  $.ajax({
    url: '/auth0login',
    data: { token : token },
    success: function() {
      window.location.replace(redirectTo);
    }
  });
}

window.onload = function(){
  console.log('signin');
  signin();
};
