import cookie from 'react-cookies'

function isAuthenticated() {
  const user = getUser();
  if (!user) {
    return false;
  }

  return user.username || user.email;
}

function getUser() {
  var user = cookie.load('session');
  return user ? user : {};
}

function getToken() {
  return cookie.load('token');
}

function updateUserProperty(key, value) {
  var user = getUser();
  user[key] = value;
  updateUser(user, true);
}

function updateUser(user, ignoreToken) {
  var token = user.password;
  user.password = undefined;

  cookie.save('session', JSON.stringify(user));

  if (!ignoreToken) {
    cookie.save('token', token);
  }
}

function removeUser() {
  cookie.remove('session');
  cookie.remove('token');
}

const Session = { getUser, isAuthenticated, updateUser, removeUser, updateUserProperty, getToken };
export default Session;
