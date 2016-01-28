function signin() {
  /*lock.show({
      callbackURL: 'http://localhost:9000/secure/login'
    , responseType: 'code'
    , authParams: {
      scope: 'openid profile'
    }
  });*/

  var lock = new Auth0Lock('dR7GdOhtI3HFNxfm4HySDL4Ke8uyGfTe', 'conveyal.eu.auth0.com');


  console.log("showing lock..");
  lock.show(function(err, profile, token) {
    if(err) {
      console.log(err)
    } else {
      console.log('login OK!');

      // save profile and token to localStorage
      localStorage.setItem('userToken', token);
      console.log('set token', token);

      $.ajax({
        url: '/auth0login',
        data: { token : token },
        success: function() {
          console.log('auth0login success');

          console.log('redirecting to main');
          window.location = '/';
        }
      })

    }
  }, {
    container: 'auth0login'
  });
}

window.onload = function(){
  console.log('login window.onload');
  // if code exists from callback
  if (window.location.search){
    $.ajaxSetup({
      beforeSend(jqXHR) {
        jqXHR.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('userToken'));
        return true;
      }
    });
  }
  else{

    signin();
  }

};
