import Session from '../common/Session';

function commonRequest(method, url, request, callback) {
  // return fetch((document.URL.indexOf('localhost') > 0 ? '' : 'https://textalldata.com') + url, {
  return fetch((document.URL.indexOf('localhost') > 0 ? '' : 'https://bizownercells.com') + url, {
      'method': method,
      headers: {
        'accept': 'application/json',
        'Content-Type': 'application/json',
        'token': Session.getToken()
      },
      body: JSON.stringify(request),
    })
    .then(response => {
      return response.json();
    })
    .then(callback);
}

function auth(request, callback) {
  commonRequest('POST', '/api/auth', request, callback)
}

function sendPinCode(request, callback) {
  commonRequest('POST', '/api/pin/code', request, callback);
}

function register(request, callback) {
  commonRequest('POST', '/api/register', request, callback);
}

function sendResetPasswordPinCode(request, callback) {
  commonRequest('POST', '/api/reset/password/pin/code', request, callback);
}

function resetPassword(request, callback) {
  commonRequest('POST', '/api/reset/password', request, callback);
}

function loginAsUser(request, callback) {
  commonRequest('POST', '/api/auth/as', request, callback);
}

const LoginAPI = {
  auth,
  sendPinCode,
  register,
  sendResetPasswordPinCode,
  resetPassword,
  loginAsUser
};

export default LoginAPI;
