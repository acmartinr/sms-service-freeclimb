import Session from '../common/Session';

function commonRequest(method, url, request, callback) {
  // return fetch((document.URL.indexOf('localhost') > 0 ? '' : 'https://textalldata.com') + url, {
  return fetch((document.URL.indexOf('localhost') > 0 ? '' : 'http://66.175.233.84:9000') + url, {
      'method': method,
      headers: {
        'accept': 'application/json',
        'Content-Type': 'application/json',
        'token': Session.getToken()
      },
      body: JSON.stringify(request),
    })
    .then(response => { return response.json() })
    .then(callback);
}

function changePassword(request, callback) {
  commonRequest('POST', '/api/change/password', request, callback);
}

function addPayment(request, callback) {
  commonRequest('POST', '/api/add/payment', request, callback);
}

function updateTimeZone(request, callback) {
  commonRequest('POST', '/api/users/timezone', request, callback);
}

const AccountAPI = {
  changePassword,
  addPayment,
  updateTimeZone
};

export default AccountAPI;
