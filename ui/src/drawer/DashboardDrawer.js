import React, {Component} from 'react';

import Drawer from '@material-ui/core/Drawer';
import List from '@material-ui/core/List';
import Divider from '@material-ui/core/Divider';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import Textsms from '@material-ui/icons/Textsms';
import ListIcon from '@material-ui/icons/List';
import Phone from '@material-ui/icons/Phone';
import PhoneDisabled from '@material-ui/icons/PhoneDisabled';
import Chat from '@material-ui/icons/Chat';
import CancelScheduleSend from '@material-ui/icons/CancelScheduleSend';
import SupervisedUsers from '@material-ui/icons/SupervisedUserCircle';
import SettingsApplications from '@material-ui/icons/SettingsApplications';
import CreditCard from '@material-ui/icons/CreditCard';
import Reply from '@material-ui/icons/Reply';
import AttachMoney from '@material-ui/icons/AttachMoney';

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';

class DashboardDrawer extends Component {
  constructor(props) {
    super(props);

    this.state = {
      autoReplyEnabled: '1',//Session.getUser().autoReplyEnabled,
      masterIgnoreEnabled: Session.getUser().masterIgnoreEnabled
    };
  }

  componentDidMount() {
    var that = this;
    CampaignsAPI.getUserUISettings({id: Session.getUser().id}, function (response) {
      if (response.status === 'OK') {
        if (response.data && response.data.length > 0) {
          that.setState({
            //autoReplyEnabled: response.data[0].sval,
            agentsLoginEnabled: response.data.find(setting => setting.skey.includes("agents.login.enabled")).sval,
            masterIgnoreEnabled: response.data.find(setting => setting.skey.includes("master.ignore.enabled")).sval,
            allowTransactionsView: response.data.find(setting => setting.skey.includes("allowTransactionsView")).sval === "1"
          });

          //Session.updateUserProperty('autoReplyEnabled', response.data[0].sval);
          Session.updateUserProperty('agentsLoginEnabled', response.data.find(setting => setting.skey.includes("agents.login.enabled")).sval);
          Session.updateUserProperty('masterIgnoreEnabled', response.data.find(setting => setting.skey.includes("master.ignore.enabled")).sval);
          Session.updateUserProperty('allowTransactionsView', response.data.find(setting => setting.skey.includes("allowTransactionsView")).sval === "1");
          Session.updateUserProperty('allowSubUsersTransactionsView', response.data.find(setting => setting.skey.includes("allowSubUsersTransactionsView")).sval === "1");
        }
      }
    });
  }

  isAllowTransactionsView() {
    return this.isAdmin() || Session.getUser().allowTransactionsView;
  }

  isAdmin() {
    return Session.getUser().role === 0;
  }

  isReseller() {
    return Session.getUser().role === 3;
  }

  isAutoReplyEnabled() {
    return true;//Session.getUser().autoReplyEnabled === '1';
  }

  isLimitedUser() {
    return Session.getUser().role === 2;
  }

  render() {
    var menuItems = [
      {text: 'Campaigns', value: 'campaigns', icon: <Textsms/>},
      {text: 'Lists', value: 'lists', icon: <ListIcon/>},
      {text: 'DNC', value: 'dnc', icon: <CancelScheduleSend/>},
      {text: 'Caller IDs', value: 'phones', icon: <Phone/>},
      {text: 'Chat', value: 'chat', icon: <Chat/>}
    ];

    if (this.isAllowTransactionsView()) {
      menuItems.push({text: 'Transactions', value: 'transactions', icon: <CreditCard/>})
    }

    if (this.isLimitedUser()) {
      menuItems = [
        {text: 'Chat', value: 'chat', icon: <Chat/>},
      ];
    }

    if (this.isAdmin()) {
      menuItems = [
        {text: 'Campaigns', value: 'campaigns', icon: <Textsms/>},
        {text: 'Lists', value: 'lists', icon: <ListIcon/>},
        {text: 'DNC', value: 'dnc', icon: <CancelScheduleSend/>},
        {text: 'Caller IDs', value: 'phones', icon: <Phone/>},
        {text: 'Removed Caller IDs', value: 'removed_phones', icon: <PhoneDisabled/>},
        {text: 'Chat', value: 'chat', icon: <Chat/>},
        {text: 'Transactions', value: 'transactions', icon: <CreditCard/>},
        {text: 'Balance changes', value: 'balance_changes', icon: <CreditCard/>}
      ];
    }

    return (
      <Drawer
        variant="persistent"
        anchor="left"
        open={this.props.open && !this.isLimitedUser()}
        onClose={(e) => this.props.handleDrawerClose()}>

        {/*<div>
          <IconButton
            className="right"
            onClick={(e) => this.props.handleDrawerClose()}>
            <ChevronLeftIcon />
          </IconButton>
        </div>

        <Divider />*/}

        <List>
          {menuItems.map((value, index) => (
            <ListItem
              className="margin-right-15 min-width-200"
              onClick={(e) => this.props.handleDrawerClose(value.value, value.text)}
              button
              key={value.text}>
              <ListItemIcon>{value.icon}</ListItemIcon>
              <ListItemText primary={value.text}/>
            </ListItem>
          ))}
          {this.state.autoReplyEnabled === '1' &&
          <ListItem
            className="margin-right-15"
            onClick={(e) => this.props.handleDrawerClose('auto_replies', 'Auto replies')}
            button
            key='Auto replies'>
            <ListItemIcon><Reply/></ListItemIcon>
            <ListItemText primary="Auto reply"/>
          </ListItem>
          }
          {(this.isAdmin() || this.isReseller()) && <Divider/>}
          {(this.isAdmin() || this.isReseller()) &&
          [
            {text: 'System settings', value: 'system_settings', icon: <SettingsApplications/>},
            {text: 'Users', value: 'users', icon: <SupervisedUsers/>},
            //{text: 'Users settings', value: 'user_settings', icon: <Settings/>},
            {text: 'Payments', value: 'payments', icon: <AttachMoney/>}
          ]
            .map((value, index) => (
              <ListItem
                className="margin-right-15"
                onClick={(e) => this.props.handleDrawerClose(value.value, value.text)}
                button
                key={value.text}>
                <ListItemIcon>{value.icon}</ListItemIcon>
                <ListItemText primary={value.text}/>
              </ListItem>
            ))
          }
        </List>
      </Drawer>
    )
  }
}

export default DashboardDrawer;
