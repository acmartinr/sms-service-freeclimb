import React, {Component} from 'react';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Typography from '@material-ui/core/Typography';
import Container from '@material-ui/core/Container';
import md5 from 'md5';

import Checkbox from '@material-ui/core/Checkbox';
import queryString from 'query-string';

import {Redirect} from 'react-router-dom';
import Session from '../common/Session.js';
import LoginAPI from './LoginAPI';
import RegistrationModal from './RegistrationModal';
import ForgotPasswordModal from './ForgotPasswordModal';

import './Login.css';
import '../common/Common.css';

class Login extends Component {
  constructor(props) {
    super(props);

    var state = {
      redirect: false,
      loginError: false,
      errors: {username: false, password: false, agreed: false},
      errorMessage: '',
      username: '',
      password: '',
      showRegistrationModal: false,
      showForgotPasswordModal: false,
      agreed: false,
      message: '',
      site: 'TextAllData.com',
      siteTitle: 'TextAllData'
    };

    this.closeRegistrationModal = this.closeRegistrationModal.bind(this);
    this.closeForgotPasswordModal = this.closeForgotPasswordModal.bind(this);
    this.handleAgreeChange = this.handleAgreeChange.bind(this);

    if (document.URL.indexOf('textdatasales.com') > -1) {
      state['site'] = 'TextDataSales.com';
      state['siteTitle'] = 'TextDataSales';
    } else if (document.URL.indexOf('businesssmsmarketing.com') > -1) {
      state['site'] = 'BusinessSMSMarketing.com';
      state['siteTitle'] = 'BusinessSMSMarketing';
    } else if (document.URL.indexOf('axiomtext.com') > -1) {
      state['site'] = 'AxiomText.com';
      state['siteTitle'] = 'AxiomText';
    } else if (document.URL.indexOf('pennytextapp.com') > -1) {
      state['site'] = 'PennyTextApp.com';
      state['siteTitle'] = 'PennyTextApp';
    } else if (document.URL.indexOf('votercloud.com') > -1) {
      state['site'] = 'VoterCloud.com';
      state['siteTitle'] = 'VoterCloud';
    } else if (document.URL.indexOf('smschatleads.com') > -1) {
      state['site'] = 'SMSChatLeads.com';
      state['siteTitle'] = 'SMSChatLeads';
    }

    this.state = state;
  }

  isPennyTextApp() {
    return document.URL.indexOf('pennytextapp.com') > -1;
  }

  closeRegistrationModal(username, password) {
    this.setState({showRegistrationModal: false});

    if (username && password) {
      LoginAPI.auth({
        'username': username,
        'password': password},
      response => {
        if (response.status === 'OK') {
          Session.updateUser(response.data);

          this.setState({redirect: true});
        } else {
          this.setState({loginError: true, errors: {username: true, password: true}});
        }
      });
    }
  }

  closeForgotPasswordModal(success) {
    this.setState({showForgotPasswordModal: false});

    if (success) {
      this.setState({message: 'Your password has been changed successfully'});
    }
  }

  showRegistrationModal() {
    this.setState({showRegistrationModal: true});
  }

  showForgotPasswordModal() {
    this.setState({showForgotPasswordModal: true});
  }

  handleSubmit(event) {
    event.preventDefault();

    this.setState(
      {
        loginError: false,
        errors: {
          username: this.state.username.length === 0,
          password: this.state.password.length === 0,
          agreed: !this.state.agreed
        }
      }
    );

    if (this.state.username.length > 0 && this.state.password.length > 0 && this.state.agreed) {
      LoginAPI.auth({
        username: this.state.username,
        password: md5(this.state.password)},
      response => {
        if (response.status === 'OK') {
          Session.updateUser(response.data);

          this.setState({redirect: true});
        } else {
          this.setState({loginError: true, errors: {username: true, password: true}});
        }
      });
    }
  }

  onTextFieldChange(field, event) {
    this.setState({
      errors: {username: false, password: false},
      [field]: event.target.value
    });
  }

  handleAgreeChange(event) {
    this.setState({agreed: event.target.checked});
  }

  isLimitedUser() {
    return Session.getUser().role === 2;
  }

  getRedirectPath() {
    var parsedQuery = queryString.parse(this.props.location.search);
    if (parsedQuery.redirect) {
      if (parsedQuery.phoneFrom) {
        return '/' + parsedQuery.redirect + '?phoneFrom=' + parsedQuery.phoneFrom;
      } else {
        return '/' + parsedQuery.redirect;
      }
    }
  }

  render() {
    if (Session.isAuthenticated() && this.isLimitedUser()) {
      return (<Redirect to='/chat'/>);
    }

    if (Session.isAuthenticated() && this.getRedirectPath()) {
      return (<Redirect to={this.getRedirectPath()}/>);
    }

    if (Session.isAuthenticated()) {
      return (<Redirect to='/campaigns'/>);
    }

    return (
      <Container component="main" maxWidth="xs">
        <div className="login-paper">
          {this.isPennyTextApp() &&
            <img alt='penny-logo' className='penny-logo' src='/penny-logo.jpg'/>}

          <Typography className='login-title-text' component="h1" variant="h5">
            {!this.isPennyTextApp() && <img alt='logo' className='logo-image' src='/logo.png'/>}
            {this.state.site} - Signup & Send It
          </Typography>
          <ul className='functions-list'>
            <li>Bulk SMS Sender</li>
            <li>Incoming Call Router</li>
            <li>Notification Software</li>
            <li>Text Message Marketing</li>
            <li>Conversation Chat Messenger</li>
          </ul>

          <Typography className="login-title-text margin-top-20" component="h1" variant="h5">
            Sign in
          </Typography>

          {this.state.message && <span className='login-message'>{this.state.message}</span>}

          <form className="login-form"
                onSubmit={(e) => this.handleSubmit(e)}
                noValidate>

            <TextField
              error={this.state.errors.username}
              variant="outlined"
              margin="normal"
              required
              fullWidth
              id="email"
              label="Phone or Username"
              name="email"
              autoComplete="email"
              autoFocus
              onChange={(e) => this.onTextFieldChange("username", e)}
            />

            <TextField
              error={this.state.errors.password}
              variant="outlined"
              margin="normal"
              required
              fullWidth
              name="password"
              label="Password"
              type="password"
              id="password"
              autoComplete="current-password"
              onChange={(e) => this.onTextFieldChange("password", e)}
            />

            {this.state.loginError &&
              <Typography color="error" component="h1" variant="subtitle1">
                Entered email or password are incorrect
              </Typography>
            }

            <div>
              <Checkbox
                checked={this.state.agreed}
                onChange={this.handleAgreeChange}
                className={this.state.errors.agreed ? "error-checkbox" : ""}
                color='primary'/>

              <span className={this.state.errors.agreed ? "error-checkbox terms-of-use-span" : "terms-of-use-span"}>
                By using {this.state.siteTitle} software, you agree to the
                <a className='terms-of-use-link'
                   target='_blank'
                   rel="noopener noreferrer"
                   href='/end_user_agreement'>{this.state.siteTitle} End User Agreement</a>
              </span>
            </div>

            <Button
              type="submit"
              fullWidth
              variant="contained"
              color="primary"
              className="login-submit"
            >
              Sign In
            </Button>

            <span onClick={(e) => this.showRegistrationModal()}
                  className="registration-button">Set up New Account</span>

            <span onClick={(e) => this.showForgotPasswordModal()}
                  className="forgot-password-button">Forgot password?</span>

          </form>
        </div>

        {this.state.showRegistrationModal &&
          <RegistrationModal handleClose={this.closeRegistrationModal}/>}

        {this.state.showForgotPasswordModal &&
          <ForgotPasswordModal handleClose={this.closeForgotPasswordModal}/>}
      </Container>
    )
  }
}

export default Login;

