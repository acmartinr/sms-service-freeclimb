import React, {Component} from 'react';
import {Redirect, withRouter} from 'react-router-dom';
import {isMobile} from 'react-device-detect';

import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import ArrowDropDown from '@material-ui/icons/ArrowDropDown';
import AccountCircle from '@material-ui/icons/AccountCircle';
import Email from '@material-ui/icons/Email';

import Session from '../common/Session';
import CampaignsAPI from '../campaigns/CampaignsAPI';
import './UserInfo.css';

import MessagesModal from './MessagesModal';
import SendingTipsModal from './SendingTipsModal';

import {red} from '@material-ui/core/colors';

class UserInfo extends Component {

  constructor(props) {
    super(props);

    this.state = {
      open: false,
      balance: 0,
      updateIntervalId: '',
      messages: [],
      showMessagesModal: false,
      showSendingTipsModal: false
    };
    this.location = this.props.location.pathname;

    this.closeMessagesModal = this.closeMessagesModal.bind(this);
    this.closeSendingTipsModal = this.closeSendingTipsModal.bind(this);
  }

  getUsername() {
    var username = Session.getUser().fullName;
    if (!username) {
      username = Session.getUser().username;
    }

    if (!username) {
      username = Session.getUser().email;
    }

    return username;
  }

  componentDidMount() {
    this.updateUserBalance();
    this.scheduleUserBalanceUpdate();
  }

  componentWillUnmount() {
    if (this.state.updateIntervalId) {
      clearInterval(this.state.updateIntervalId);
    }
  }

  scheduleUserBalanceUpdate() {
    var that = this;

    const intervalId = setInterval(function () {
      that.updateUserBalance();
    }, 10000);
    this.setState({updateIntervalId: intervalId});
  }

  updateUserBalance() {
    CampaignsAPI.getUserBalanceAndMessages({id: Session.getUser().id},
      response => {
        if (response.status === 'OK') {
          this.setState({balance: response.data.balance, messages: response.data.messages});
        } else {
          Session.removeUser();
          this.setState({redirect: '/login'});
        }
      });
  }

  showMessagesModal() {
    this.setState({showMessagesModal: true});
  }

  closeMessagesModal() {
    var messages = this.state.messages.slice();
    for (var i = 0; i < messages.length; i++) {
      messages[i].read = true;
    }

    this.setState({showMessagesModal: false, 'messages': messages});
  }

  showSendingTipsModal() {
    this.setState({showSendingTipsModal: true});
  }

  closeSendingTipsModal() {
    this.setState({showSendingTipsModal: false});
  }

  getUnreadMessagesCount() {
    var count = 0;

    for (var i = 0; i < this.state.messages.length; i++) {
      var message = this.state.messages[i];
      if (!message.read) {
        count = count + 1;
      }
    }

    return count;
  }

  handleSignOut() {
    Session.removeUser();
    this.setState({redirect: '/login'});
  }

  showAccount() {
    if (this.location !== '/account')
      this.setState({redirect: '/account'});
  }

  showKYC() {
    if (this.location !== '/kyc')
      this.setState({redirect: '/kyc'});
  }

  showNotes() {
    if (this.location !== '/notes')
      this.setState({redirect: '/notes'});
  }

  handleMenu(event) {
    this.setState({anchorEl: event.currentTarget, open: true});
  }

  handleClose() {
    this.setState({open: false, anchorEl: null});
  }

  isLimitedUser() {
    return Session.getUser().role === 2;
  }

  isSubUser() {
    return Session.getUser().resellerId > 1;
  }

  isMainDomain() {
    return document.URL.indexOf('textalldata') > 0 || document.URL.indexOf('localhost') > 0;
  }

  isAxiomDomain() {
    return document.URL.indexOf('axiomtext') > 0;
  }

  isPennyTextingDomain() {
    return document.URL.indexOf('pennytextapp') > 0;
  }

  isVoterCloudDomain() {
    return document.URL.indexOf('votercloud') > 0;
  }

  render() {
    if (this.state.redirect) {
      return (<Redirect to={this.state.redirect}/>);
    }

    return (
      <div className="user-info">
        {!isMobile && <Typography
          onClick={(e) => this.showSendingTipsModal()}
          component="h1" variant="h6" color="inherit"
          noWrap className="pointer dashboard-support-title send-tips-label">
          Sending Tips
        </Typography>}

        {(!isMobile && !this.isSubUser() && this.isMainDomain()) && <Typography
          component="h1" variant="h6" color="inherit" noWrap className="dashboard-support-title">
          Tech Support 410-457-9779
        </Typography>}

        {(!isMobile && this.isSubUser() && this.isAxiomDomain()) && <Typography
          component="h1" variant="h6" color="inherit" noWrap className="dashboard-support-title">
          Tech Support 855-246-2100
        </Typography>}

        {(!isMobile && this.isSubUser() && this.isPennyTextingDomain()) && <Typography
          component="h1" variant="h6" color="inherit" noWrap className="dashboard-support-title">
          Tech Support 775-790-1932
        </Typography>}

        {(!isMobile && this.isSubUser() && this.isVoterCloudDomain()) && <Typography
          component="h1" variant="h6" color="inherit" noWrap className="dashboard-support-title">
          Tech Support 651-571-4953
        </Typography>}

        {!isMobile && <IconButton
          aria-label="Messages"
          aria-controls="menu-appbar"
          aria-haspopup="true"
          onClick={(e) => this.showMessagesModal()}>
          {this.getUnreadMessagesCount() > 0 &&
            <Email style={{color: red[600]}}/>}
          {this.getUnreadMessagesCount() === 0 &&
            <Email style={{color: "#ffffff"}}/>}
        </IconButton>}

        {!isMobile && <IconButton
          aria-label="Account of current user"
          aria-controls="menu-appbar"
          aria-haspopup="true"
          color="inherit">
          <AccountCircle/>
        </IconButton>}

        <Typography
          component="h1"
          variant="subtitle2"
          color="inherit"
          className="inline"
          noWrap
          onClick={(e) => this.handleMenu(e)}>
          {this.getUsername()} {!this.isLimitedUser() && <span>(${this.state.balance.toFixed(2)})</span>}
        </Typography>

        <ArrowDropDown
          className="pointer vertical-middle"
          onClick={(e) => this.handleMenu(e)}/>

        <Menu
          id="menu-appbar"
          anchorOrigin={{
            vertical: 'top',
            horizontal: 'right',
          }}
          keepMounted
          transformOrigin={{
            vertical: 'top',
            horizontal: 'right',
          }}
          anchorEl={this.state.anchorEl}
          open={this.state.open}
          onClose={(e) => this.handleClose()}>
          {!this.isLimitedUser() && <MenuItem onClick={(e) => this.showAccount()}>Account</MenuItem>}
          {!this.isLimitedUser() && <MenuItem onClick={(e) => this.showKYC()}>KYC</MenuItem>}
          {!this.isLimitedUser() && <MenuItem onClick={(e) => this.showNotes()}>Notes</MenuItem>}
          <MenuItem onClick={(e) => this.handleSignOut()}>Sign Out</MenuItem>
        </Menu>

        {this.state.showMessagesModal &&
          <MessagesModal
            messages={this.state.messages}
            handleClose={this.closeMessagesModal}/>}

        {this.state.showSendingTipsModal &&
          <SendingTipsModal
            handleClose={this.closeSendingTipsModal}/>}
      </div>
    );
  }
}

export default withRouter(props => <UserInfo {...props}/>);
;
