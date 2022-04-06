import React, {Component} from 'react';
import {Redirect} from 'react-router-dom';
import {isMobile} from 'react-device-detect';

import Session from '../common/Session';

import '../common/Common.css';
import AppToolbar from '../toolbar/AppToolbar';
import DashboardDrawer from '../drawer/DashboardDrawer';

import Senders from '../senders/Senders';
import SenderGroups from '../groups/SenderGroups'
import Lists from '../lists/Lists';
import Phones from '../phones/Phones';
import RemovedPhones from '../phones/RemovedPhones';
import Campaigns from '../campaigns/Campaigns';
import Chat from '../chat/Chat';
import DNC from '../dnc/DNC';
import Users from '../users/Users';

import Settings from '../settings/Settings';
import SystemSettings from '../settings/SystemSettings';

import Transactions from '../transactions/Transactions';
import AllTransactions from '../transactions/AllTransactions';
import Account from '../account/Account';
import Prices from '../prices/Prices';
import AutoReplies from '../replies/AutoReplies';
import Payments from '../payments/Payments';
import Notes from '../notes/Notes';
import Kyc from "../kyc/Kyc";

class Dashboard extends Component {
  constructor(props) {
    super(props);

    this.state = {
      open: true,
      show: false,
      selectedTab: this.props.selectedTab,
      title: this.props.title,
      subtitle: ''
    };

    this.setSubtitle = (subtitle) => this.setState({subtitle})

    this.handleDrawerOpen = this.handleDrawerOpen.bind(this);
    this.handleDrawerClose = this.handleDrawerClose.bind(this);

    this.handleChangePage = this.handleChangePage.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);
  }

  handleChangePage(event, value) {
    this.setState({page: value}, this.updateNotifications);
  }

  handleChangeRowsPerPage(event) {
    this.setState({rowsPerPage: event.target.value}, this.updateNotifications);
  }

  handleDrawerOpen() {
    this.setState({open: true});
  }

  handleDrawerClose(tab, title) {
    //this.setState({open: false});

    if (title && title !== this.state.title) {
      this.setState({redirect: tab});
    }
  }

  closeReceiptInfoModal() {
    this.setState({showReceiptInfoDetails: false});
  }

  isLimitedUser() {
    return Session.getUser().role === 2;
  }

  isAdmin() {
    return Session.getUser().role === 0;
  }

  isReseller() {
    return Session.getUser().role === 3;
  }

  isAllowTransactionsView() {
    return this.isAdmin() || Session.getUser().allowTransactionsView;
  }

  render() {
    if (!Session.isAuthenticated()) {
      return (<Redirect to='/login'/>);
    }
// ROLES ADMIN, REGULAR, LIMITED, RESELLER
// PROTECTED TABS NORMAL USERS:
    if ((this.isLimitedUser() && ["campaigns", "lists", "dnc", "phones", "removed_phones", "transactions", "balance_changes", "system_settings", "users", "payments"].includes(this.state.selectedTab)) ||
      (!this.isAdmin() && !this.isReseller() && ["system_settings", "users", "payments",].includes(this.state.selectedTab)) ||
      (!this.isAdmin() && ["removed_phones", "balance_changes"].includes(this.state.selectedTab)) ||
      (!this.isAllowTransactionsView() && this.state.selectedTab === "transactions")) {
      return (<Redirect to='/login'/>);
    }

    if (this.state.redirect) {
      return (<Redirect to={this.state.redirect}/>);
    }

    return (
      <div className="dashboard-root">
        <AppToolbar
          title={<div>{this.state.title}<span
            className={this.state.subtitle.toLowerCase().includes('not') ? 'header-subtitle-wrong' : 'header-subtitle-successful'}>
            {this.state.subtitle}</span>
          </div>}
          drawer={true}
          handleDrawerOpen={(e) => this.handleDrawerOpen(e)}
          applicationsMenuItem={true}/>

        <DashboardDrawer
          open={this.state.open && !isMobile}
          handleDrawerClose={this.handleDrawerClose}/>

        <main style={{marginLeft: (isMobile || this.isLimitedUser()) ? 0 : (this.isAdmin() ? 245 : 225)}}>
          {this.state.selectedTab === 'campaigns' &&
            <Campaigns/>
          }
          {this.state.selectedTab === 'lists' &&
            <Lists/>
          }
          {this.state.selectedTab === 'dnc' &&
            <DNC/>
          }
          {this.state.selectedTab === 'senders' &&
            <Senders/>
          }
          {this.state.selectedTab === 'groups' &&
            <SenderGroups/>
          }
          {this.state.selectedTab === 'phones' &&
            <Phones/>
          }
          {this.state.selectedTab === 'removed_phones' &&
            <RemovedPhones/>
          }
          {this.state.selectedTab === 'chat' &&
            <Chat/>
          }
          {this.state.selectedTab === 'users' &&
            <Users/>
          }
          {this.state.selectedTab === 'notes' &&
            <Notes/>
          }
          {this.state.selectedTab === 'settings' &&
            <Settings/>
          }
          {this.state.selectedTab === 'system_settings' &&
            <SystemSettings/>
          }
          {this.state.selectedTab === 'transactions' &&
            <Transactions/>
          }
          {this.state.selectedTab === 'balance_changes' &&
            <AllTransactions/>
          }
          {this.state.selectedTab === 'account' &&
            <Account/>
          }
          {this.state.selectedTab === 'kyc' &&
            <Kyc setSubtitle={this.setSubtitle}/>
          }
          {this.state.selectedTab === 'user_settings' &&
            <Prices/>
          }
          {this.state.selectedTab === 'auto_replies' &&
            <AutoReplies/>
          }
          {this.state.selectedTab === 'payments' &&
            <Payments/>
          }
        </main>
      </div>
    )
  }
}

export default Dashboard;
