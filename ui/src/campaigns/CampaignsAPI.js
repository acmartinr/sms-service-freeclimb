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
  return fetch((document.URL.indexOf('localhost') > 0 ? '' : 'http://66.175.233.84:9000') + url, {
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

function getStatistics(request, callback) {
  return commonRequest('POST', '/api/campaign/statistics', request, callback);
}

function getSenders(request, callback) {
  return commonRequest('POST', '/api/senders', request, callback);
}

function createSender(request, callback) {
  return commonRequest('POST', '/api/sender', request, callback);
}

function removeSender(request, callback) {
  return commonRequest('DELETE', '/api/sender/' + request.id, request, callback);
}

function getSenderGroups(request, callback) {
  return commonRequest('POST', '/api/sender/groups', request, callback);
}

function createSenderGroup(request, callback) {
  return commonRequest('POST', '/api/sender/group', request, callback);
}

function removeSenderGroup(request, callback) {
  return commonRequest('DELETE', '/api/sender/group/' + request.id, request, callback);
}

function getSendersForGroup(request, callback) {
  return commonGETRequest('/api/group/senders/' + request.id, callback);
}

function getLists(request, callback) {
  return commonRequest('POST', '/api/lists', request, callback);
}

function removeList(request, callback) {
  return commonRequest('DELETE', '/api/list/' + request.id, request, callback);
}

function getCampaigns(request, callback) {
  return commonRequest('POST', '/api/campaigns', request, callback);
}

function createCampaign(request, callback) {
  return commonRequest('POST', '/api/campaign', request, callback);
}

function removeCampaign(request, callback) {
  return commonRequest('DELETE', '/api/campaign/' + request.id, request, callback);
}

function getListsForCampaign(request, callback) {
  return commonGETRequest('/api/campaign/lists/' + request.id, callback);
}

function updateListsForCampaign(request, callback) {
  return commonRequest('POST', '/api/campaign/lists', request, callback);
}

function startCampaign(request, callback) {
  return commonRequest('POST', '/api/campaign/start', request, callback);
}

function stopCampaign(request, callback) {
  return commonRequest('POST', '/api/campaign/stop', request, callback);
}

function getPhones(request, callback) {
  return commonRequest('POST', '/api/phones', request, callback);
}

function getRemovedPhones(request, callback) {
  return commonRequest('POST', '/api/phones/removed', request, callback);
}

function updatePhone(request, callback) {
  return commonRequest('PUT', '/api/phones', request, callback);
}

function updatePhonesForwarding(request, callback) {
  return commonRequest('POST', '/api/phones/forwarding', request, callback);
}

function searchPhoneNumbers(request, callback) {
  return commonRequest('POST', '/api/phones/search', request, callback);
}

function buyPhones(request, callback) {
  return commonRequest('POST', '/api/phones/buy', request, callback);
}

function removePhone(request, callback) {
  return commonRequest('DELETE', '/api/phones/' + request.id, request, callback);
}

function getFreePhones(request, callback) {
  return commonRequest('POST', '/api/phones/free', request, callback);
}

function getChats(request, callback) {
  return commonRequest('POST', '/api/chats', request, callback);
}

function getChatMessages(request, callback) {
  return commonGETRequest('/api/chats/messages/' + request.id, callback);
}

function sendMessage(request, callback) {
  return commonRequest('POST', '/api/chats/message', request, callback);
}

function getSelectedPhones(request, callback) {
  return commonGETRequest('/api/campaign/phones/' + request.id, callback);
}

function sendTestSMS(request, callback) {
  return commonRequest('POST', '/api/campaign/message/test', request, callback);
}

function getDNCLists(request, callback) {
  return commonRequest('POST', '/api/dnc/lists', request, callback);
}

function resetLastCampaignError(request, callback) {
  return commonGETRequest('/api/campaign/reset/error/' + request.id, callback);
}

function getCampaignErrors(request, callback) {
  return commonGETRequest('/api/campaign/errors/' + request.id, callback);
}

function addDNCPhones(request, callback) {
  return commonRequest('POST', '/api/dnc/phones', request, callback);
}

function removeChat(request, callback) {
  return commonRequest('DELETE', '/api/chats/' + request.id, {}, callback);
}

function banChatPhone(request, callback) {
  return commonGETRequest('/api/chats/ban/' + request.id, callback);
}

function getUsers(request, callback) {
  return commonRequest('POST', '/api/users', request, callback);
}

function getResellers(callback) {
  return commonGETRequest('/api/resellers', callback);
}

function updateUser(request, callback) {
  return commonRequest('PUT', '/api/users', request, callback);
}

function removeUser(request, callback) {
  return commonRequest('DELETE', '/api/users/' + request.id, {}, callback);
}

function getSettings(request, callback) {
  return commonGETRequest('/api/settings/' + request.id, callback);
}

function getConfiguredByUserSettings(request, callback) {
  return commonGETRequest('/api/configuring/settings/' + request.id + '/' + request.type, callback);
}

function updateSetting(request, callback) {
  return commonRequest('POST', '/api/settings', request, callback);
}

function addFund(request, callback) {
  return commonRequest('POST', '/api/users/fund', request, callback);
}

function getTransactions(request, callback) {
  return commonRequest('POST', '/api/transactions', request, callback);
}

function getAllTransactions(request, callback) {
  return commonRequest('POST', '/api/transactions/all', request, callback);
}

function getUserBalance(request, callback) {
  return commonGETRequest('/api/user/balance/' + request.id, callback);
}

function getUserBalanceAndMessages(request, callback) {
  return commonGETRequest('/api/user/messages/balance/' + request.id, callback);
}

function getUserSettings(request, callback) {
  return commonGETRequest('/api/user/settings/' + request.id, callback);
}

function removeAutoReply(request, callback) {
  return commonRequest('DELETE', '/api/auto/reply/' + request.id, request, callback);
}

function getAutoReplies(request, callback) {
  return commonRequest('POST', '/api/auto/replies', request, callback);
}

function createAutoReply(request, callback) {
  return commonRequest('POST', '/api/auto/reply', request, callback);
}

function getUserUISettings(request, callback) {
  return commonGETRequest('/api/user/ui/settings/' + request.id, callback);
}

function getPayments(request, callback) {
  return commonRequest('POST', '/api/payments', request, callback);
}

function updateAgentCredentials(request, callback) {
  return commonRequest('POST', '/api/campaign/agent', request, callback);
}

function exportUsers(request, callback) {
  return commonRequest('POST', '/api/users/export', request, callback);
}

function exportChats(callback) {
  return commonGETRequest('/api/chats/export', callback);
}

function getUserMessages(request, callback) {
  commonGETRequest('/api/users/messages/' + request.id, callback);
}

function removeUserMessage(request, callback) {
  return commonRequest('DELETE', '/api/users/message/' + request.id, request, callback);
}

function sendUserMessage(request, callback) {
  return commonRequest('POST', '/api/users/message', request, callback);
}

function resetUnreadMessages() {
  return commonGETRequest('/api/users/reset/messages');
}

function getTransactionsStatistics(request, callback) {
  return commonRequest('POST', '/api/transactions/statistics', request, callback);
}

function getDailyStatistics(request, callback) {
  return commonRequest('POST', '/api/transactions/statistics/daily', request, callback);
}

function getManageMoneyAccess(request, callback) {
  return commonGETRequest('/api/user/manage/money/' + request.id, callback);
}

function getNotes(callback) {
  return commonGETRequest('/api/notes', callback);
}

function saveNote(request, callback) {
  return commonRequest('POST', '/api/notes', request, callback);
}

function deleteNote(request, callback) {
  return commonRequest('DELETE', '/api/notes/' + request.id, request, callback);
}


const CampaignAPI = {
  getStatistics,
  getSenders,
  createSender,
  removeSender,
  getSenderGroups,
  createSenderGroup,
  removeSenderGroup,
  getSendersForGroup,
  getLists,
  removeList,
  getCampaigns,
  createCampaign,
  removeCampaign,
  getListsForCampaign,
  updateListsForCampaign,
  startCampaign,
  stopCampaign,
  getPhones,
  getRemovedPhones,
  searchPhoneNumbers,
  buyPhones,
  removePhone,
  getFreePhones,
  getChats,
  getChatMessages,
  sendMessage,
  getSelectedPhones,
  sendTestSMS,
  getDNCLists,
  resetLastCampaignError,
  getCampaignErrors,
  addDNCPhones,
  removeChat,
  banChatPhone,
  getUsers,
  getResellers,
  removeUser,
  getSettings,
  updateSetting,
  addFund,
  getTransactions,
  getAllTransactions,
  getUserBalance,
  updatePhone,
  updateUser,
  getUserSettings,
  removeAutoReply,
  getAutoReplies,
  createAutoReply,
  getUserUISettings,
  getPayments,
  updateAgentCredentials,
  exportUsers,
  exportChats,
  getConfiguredByUserSettings,
  getUserBalanceAndMessages,
  getUserMessages,
  removeUserMessage,
  sendUserMessage,
  resetUnreadMessages,
  getTransactionsStatistics,
  updatePhonesForwarding,
  getDailyStatistics,
  getManageMoneyAccess,
  getNotes,
  deleteNote,
  saveNote
};

export default CampaignAPI;
