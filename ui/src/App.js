import React, {Component} from 'react';
import {
  BrowserRouter as Router,
  Route,
  Redirect,
  Switch,
} from 'react-router-dom';

import 'react-app-polyfill/ie11';
import 'core-js/features/string/includes';

import CssBaseline from '@material-ui/core/CssBaseline';

import Login from './login/Login';
import EndUserAgreement from './login/EndUserAgreement';

import Dashboard from './dashboard/Dashboard';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {title: ''};
  }

  render() {
    return (
      <div className="App">
        <CssBaseline />
        <Router>
          <Switch>
            <Route path="/login" component={Login} />
            <Route path="/end_user_agreement" component={EndUserAgreement}/>

            <Route
              key='campaigns'
              path="/campaigns"
              render={(props) => <Dashboard {...props} selectedTab='campaigns' title='Campaigns'/>} />

            <Route
              key='lists'
              path="/lists"
              render={(props) => <Dashboard {...props} selectedTab='lists' title='Lists'/>} />

            <Route
              key='dnc'
              path="/dnc"
              render={(props) => <Dashboard {...props} selectedTab='dnc' title='DNC'/>} />

            <Route
              key='phones'
              path="/phones"
              render={(props) => <Dashboard {...props} selectedTab='phones' title='Caller IDs'/>} />

            <Route
              key='removed_phones'
              path="/removed_phones"
              render={(props) => <Dashboard {...props} selectedTab='removed_phones' title='Removed caller IDs'/>} />

            <Route
              key='chat'
              path="/chat"
              render={(props) => <Dashboard {...props} selectedTab='chat' title='Chat'/>} />

            <Route
              key='users'
              path="/users"
              render={(props) => <Dashboard {...props} selectedTab='users' title='Users'/>} />

            <Route
              key='notes'
              path="/notes"
              render={(props) => <Dashboard {...props} selectedTab='notes' title='Notes'/>} />

            <Route
              key='settings'
              path="/settings"
              render={(props) => <Dashboard {...props} selectedTab='settings' title='Settings'/>} />

            <Route
              key='system_settings'
              path="/system_settings"
              render={(props) => <Dashboard {...props} selectedTab='system_settings' title='System settings'/>} />

            <Route
              key='transactions'
              path="/transactions"
              render={(props) => <Dashboard {...props} selectedTab='transactions' title='Transactions'/>} />

            <Route
              key='balance_changes'
              path="/balance_changes"
              render={(props) => <Dashboard {...props} selectedTab='balance_changes' title='Balance changes'/>} />

            <Route
              key='account'
              path="/account"
              render={(props) => <Dashboard {...props} selectedTab='account' title='Account'/>} />

            <Route
              key='kyc'
              path="/kyc"
              render={(props) => <Dashboard {...props} selectedTab='kyc' title='KYC'/>} />

            <Route
              key='user_settings'
              path="/user_settings"
              render={(props) => <Dashboard {...props} selectedTab='user_settings' title='User settings'/>} />

            <Route
              key='auto_replies'
              path="/auto_replies"
              render={(props) => <Dashboard {...props} selectedTab='auto_replies' title='Auto replies'/>} />

            <Route
              key='payments'
              path="/payments"
              render={(props) => <Dashboard {...props} selectedTab='payments' title='Payments'/>} />

            <Route exact path="/:id" component={Login} />
            <Redirect to="/login" />
          </Switch>
        </Router>
      </div>
    );
  }
}

export default App;
