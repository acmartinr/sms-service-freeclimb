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
      if (response.status === 200) {
        return response.json();
      } else {
        return {"error": "forbidden"};
      }
    })
    .then(callback);
}

function commonGETRequest(url, callback) {
  // return fetch((document.URL.indexOf('localhost') > 0 ? '' : 'https://textalldata.com') + url, {
  return fetch((document.URL.indexOf('localhost') > 0 ? '' : 'https://bizownercells.com') + url, {
    'method': 'GET',
    headers: {
      'accept': 'application/json',
      'token': Session.getToken()
    }
  })
    .then(response => {
      if (response.status === 200) {
        return response.json();
      } else {
        return {"error": "forbidden"};
      }
    })
    .then(callback);
}

function updateKycInfo(request, callback) {
  return commonRequest('PUT', '/api/kyc', request, callback);
}

function getKycInfo(callback) {
  return commonGETRequest('/api/kyc', callback);
}


const KycInfoAPI = {
  updateKycInfo,
  getKycInfo
};

export default KycInfoAPI;
