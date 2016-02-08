function signin() {
  /*lock.show({
      callbackURL: 'http://localhost:9000/secure/login'
    , responseType: 'code'
    , authParams: {
      scope: 'openid profile'
    }
  });*/

  var lock = new Auth0Lock('dR7GdOhtI3HFNxfm4HySDL4Ke8uyGfTe', 'conveyal.eu.auth0.com');

  // check if this is an SSO callback
  console.log("checking for SSO callback");
  var hash = lock.parseHash(window.location.hash);
  console.log("hash", hash);
  if (hash && hash.id_token) {
    console.log("found sso callback!");
    // the user came back from the login (either SSO or regular login),
    // save the token
    //localStorage.setItem('userToken', hash.id_token);

    console.log('hash state', hash.state);
    loginWithToken(hash.id_token, hash.state || '');

    // redirect to "targetUrl" if any
    //window.location.href = hash.state || '';
    return;
  }

  // check if logged in elsewhere via SSO
  console.log("checking for SSO signin");
  lock.$auth0.getSSOData(function(err, data) {
    if (!err && data.sso) {
      console.log("found sso!");
      // there is! redirect to Auth0 for SSO

      console.log("** rT0=" + redirectTo);
      lock.$auth0.signin({
        state: redirectTo,
        callbackOnLocationHash: true
      });
    } else { // assume that we are not logged in

      console.log("showing lock..");
      lock.show(function(err, profile, token) {
        if(err) {
          console.log(err)
        } else {
          console.log('login OK!');
          oginWithToken(token, "/");
          // save profile and token to localStorage
          /*localStorage.setItem('userToken', token);
          console.log('set token', token);

          $.ajax({
            url: '/auth0login',
            data: { token : token },
            success: function() {
              console.log('auth0login success');

              console.log('redirecting to main');
              window.location = '/';
            }
          })*/

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
  console.log('set token', token);

  $.ajax({
    url: '/auth0login',
    data: { token : token },
    success: function() {
      console.log('auth0login success');
      window.location = redirectTo;
    }
  });
}

window.onload = function(){
  console.log('login window.onload');
  console.log(window.location);
  signin();
};
