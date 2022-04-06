import React, {Component} from 'react';
import {Redirect} from 'react-router-dom';

import './Users.css';
import '../common/Common.css'

import CampaignsAPI from '../campaigns/CampaignsAPI';
import LoginAPI from '../login/LoginAPI';

import Session from '../common/Session';
import RemoveModal from '../common/RemoveModal';
import UserAddFundModal from './UserAddFundModal';
import UserEditModal from './UserEditModal';
import UserSettingsModal from './UserSettingsModal';
import SendMessageToUserModal from './SendMessageToUserModal';

import Button from '@material-ui/core/Button';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TablePagination from '@material-ui/core/TablePagination';
import TextField from '@material-ui/core/TextField';

import Delete from '@material-ui/icons/Delete';
import Edit from '@material-ui/icons/Edit';
import AttachMoney from '@material-ui/icons/AttachMoney';
import ExitToApp from '@material-ui/icons/ExitToApp';
import Settings from '@material-ui/icons/Settings';
import Email from '@material-ui/icons/Email';

class Users extends Component {
  constructor(props) {
    super(props);

    this.state = {
      selectedUser: {},
      users: [],
      count: 0,
      rowsPerPage: 10,
      page: 0,
      showRemoveUserModal: false,
      title: '',
      message: '',
      showAddFundModal: false,
      showEditUserModal: false,
      showUserSettingsModal: false,
      redirect: false,
      sort: 'date',
      sortDesc: true,
      search: '',
      showUserMessagesModal: false,
      allowManageMoney: Session.getUser().role === 0
    };

    this.updateUsers = this.updateUsers.bind(this);

    this.handleChangePage = this.handleChangePage.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);

    this.removeSelectedUser = this.removeSelectedUser.bind(this);
    this.closeRemoveModal = this.closeRemoveModal.bind(this);
    this.removeUser = this.removeUser.bind(this);

    this.closeAddFundModal = this.closeAddFundModal.bind(this);

    this.showEditUserModal = this.showEditUserModal.bind(this);
    this.closeEditUserModal = this.closeEditUserModal.bind(this);

    this.showUserSettingsModal = this.showUserSettingsModal.bind(this);
    this.closeUserSettingsModal = this.closeUserSettingsModal.bind(this);

    this.onTextFieldChange = this.onTextFieldChange.bind(this);
    this.closeUserMessagesModal = this.closeUserMessagesModal.bind(this);
  }

  removeSelectedUser() {
    CampaignsAPI.removeUser(this.state.selectedUser, response => {
      if (response.status === 'OK') {
        this.closeRemoveModal();
        this.updateUsers();
      }
    });
  }

  removeUser(user) {
    if (user.role !== 0) {
      this.setState({
        selectedUser: user,
        showRemoveUserModal: true,
        title: 'Remove user',
        message: 'Are you sure you want to remove this user?'
      })
    }
  }

  closeRemoveModal(user) {
    this.setState({selectedUser: {}, showRemoveUserModal: false})
  }

  editUser(user) {
    this.setState({selectedUser: user, showAddUserModal: true})
  }

  showAddUserModal() {
    this.setState({showAddUserModal: true});
  }

  showAddFundModal(user) {
    this.setState({selectedUser: user, showAddFundModal: true});
  }

  closeAddFundModal() {
    this.setState({showAddFundModal: false, selectedUser: {}});
    this.updateUsers();
  }

  showEditUserModal(user) {
    this.setState({selectedUser: user, showEditUserModal: true});
  }

  closeEditUserModal() {
    this.setState({selectedUser: {}, showEditUserModal: false});
    this.updateUsers();
  }

  showUserSettingsModal(user) {
    this.setState({selectedUser: user, showUserSettingsModal: true});
  }

  closeUserSettingsModal() {
    this.setState({selectedUser: {}, showUserSettingsModal: false});
  }

  showSendMessageToUserModal(user) {
    this.setState({selectedUser: user, showUserMessagesModal: true});
  }

  closeUserMessagesModal() {
    this.setState({selectedUser: {}, showUserMessagesModal: false});
  }

  componentDidMount() {
    this.updateUsers();
    this.updateManageMoneyAccess();
  }

  updateManageMoneyAccess() {
    var that = this;
    CampaignsAPI.getManageMoneyAccess({id: Session.getUser().id}, function (response) {
      if (response.status === 'OK') {
        that.setState({allowManageMoney: response.data});
      }
    });
  }

  handleChangePage(event, value) {
    this.setState({page: value}, this.updateUsers);
  }

  handleChangeRowsPerPage(event) {
    this.setState({rowsPerPage: event.target.value}, this.updateUsers);
  }

  prettyDate(dateMS) {
    if (dateMS > 0) {
      const date = new Date(dateMS);
      return date.toLocaleDateString(date) + ' ' + date.toLocaleTimeString(date);
    } else {
      return 'empty';
    }
  }

  loginAsUser(row) {
    var that = this;
    LoginAPI.loginAsUser(row, function (response) {
      Session.updateUser(response.data);
      that.setState({redirect: true});
    });
  }

  changeSortOrder(newSort) {
    var that = this;
    if (this.state.sort !== newSort) {
      this.setState({sort: newSort, sortDesc: true}, function () {
        that.updateUsers();
      });
    } else {
      this.setState({sortDesc: !this.state.sortDesc}, function () {
        that.updateUsers();
      });
    }
  }

  updateUsers() {
    const request = {
      userId: Session.getUser().id,
      page: this.state.page,
      limit: this.state.rowsPerPage,
      search: this.state.search,
      order: this.state.sort,
      orderDesc: this.state.sortDesc
    };

    CampaignsAPI.getUsers(request,
      response => {
        if (response.status === "OK") {
          this.setState({
            users: response.data.users,
            count: response.data.count
          });
        }
      });
  }

  localizedRole(user) {
    if (user.role === 0) {
      return 'admin';
    } else if (user.role === 1) {
      if (user.resellerId > 1) {
        return 'subuser';
      } else {
        return 'user';
      }
    } else if (user.role === 3) {
      return 'reseller';
    }

    return 'unknown';
  }

  isReseller() {
    return Session.getUser().role === 3;
  }

  isAdmin() {
    return Session.getUser().role === 0;
  }

  onTextFieldChange(field, event) {
    var that = this;
    this.setState({search: event.target.value}, function () {
      that.updateUsers();
    });
  }

  exportUsers() {
    const request = {
      userId: Session.getUser().id,
      page: 0,
      limit: 1000,
      search: this.state.search,
      order: this.state.sort,
      orderDesc: this.state.sortDesc
    };

    CampaignsAPI.exportUsers(request, function (response) {
      if (response.status === 'OK') {
        var path = '/api/users/export/' + response.message;
        var frame = document.createElement('iframe');
        frame.setAttribute('src', path);
        frame.style.width = 0;
        frame.style.height = 0;
        document.body.appendChild(frame);
      }
    });
  }

  render() {
    if (this.state.redirect) {
      return (<Redirect to='/campaigns'/>);
    }

    return (
      <div className='padding-left-15'>
        <TextField
          margin="normal"
          className={this.isAdmin() ? "user-search-with-button" : "user-search"}
          fullWidth
          autoFocus
          name="search"
          label="Enter Name, Business or phone Number"
          id="search"
          value={this.state.search}
          onChange={(e) => this.onTextFieldChange("search", e)}
        />

        {this.isAdmin() &&
          <Button
            type="button"
            variant="contained"
            color="primary"
            onClick={(e) => this.exportUsers()}
            className="users-export-button">Export</Button>}

        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Phone</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Business name</TableCell>
              <TableCell>Role</TableCell>
              <TableCell>Balance</TableCell>
              <TableCell className='pointer'
                         onClick={(e) => this.changeSortOrder("lastCampaignDate")}>Last campaign
                {this.state.sort === "lastCampaignDate" &&
                  <span className='sort-arrow'>{this.state.sortDesc ? "▼" : "▲"}</span>
                }
              </TableCell>
              <TableCell className='pointer'
                         onClick={(e) => this.changeSortOrder("date")}>Date
                {this.state.sort === "date" &&
                  <span className='sort-arrow'>{this.state.sortDesc ? "▼" : "▲"}</span>
                }
              </TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {this.state.users.map(row => (
              <TableRow key={row.id}>
                <TableCell
                  className={row.blocked ? "user-blocked" : row.disabled ? "user-disabled" : ""}>{row.username}</TableCell>
                <TableCell>{row.personalName}</TableCell>
                <TableCell>{row.fullName}</TableCell>
                <TableCell>{this.localizedRole(row)}</TableCell>
                <TableCell>${row.balance.toFixed(2)}</TableCell>
                <TableCell>{this.prettyDate(row.lastCampaignDate)}</TableCell>
                <TableCell>{this.prettyDate(row.date)}</TableCell>
                <TableCell className={!this.state.allowManageMoney ? 'users-small-action-cell' : 'users-action-cell'}>
                  {this.state.allowManageMoney && <AttachMoney
                    className='pointer action-icon'
                    color='primary'
                    onClick={(e) => this.showAddFundModal(row)}/>}
                  <Settings
                    className='pointer action-icon'
                    color='primary'
                    onClick={(e) => this.showUserSettingsModal(row)}/>
                  <ExitToApp
                    className='pointer action-icon'
                    color='primary'
                    onClick={(e) => this.loginAsUser(row)}/>
                  <Edit
                    className='pointer margin-right-10'
                    color='primary'
                    onClick={(e) => this.showEditUserModal(row)}/>
                  <Email
                    className='pointer margin-right-10'
                    color='primary'
                    onClick={(e) => this.showSendMessageToUserModal(row)}/>
                  <Delete
                    className='pointer action-icon'
                    color={row.role === 0 ? 'disabled' : 'primary'}
                    onClick={(e) => this.removeUser(row)}/>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        <TablePagination
          rowsPerPageOptions={[10, 25, 50]}
          component="div"
          count={this.state.count}
          rowsPerPage={this.state.rowsPerPage}
          page={this.state.page}
          backIconButtonProps={{
            'aria-label': 'Previous Page',
          }}
          nextIconButtonProps={{
            'aria-label': 'Next Page',
          }}
          onChangePage={this.handleChangePage}
          onChangeRowsPerPage={this.handleChangeRowsPerPage}
        />

        {this.state.showRemoveUserModal &&
          <RemoveModal
            title={this.state.title}
            message={this.state.message}
            remove={this.removeSelectedUser}
            close={this.closeRemoveModal}/>
        }

        {this.state.showAddFundModal &&
          <UserAddFundModal
            user={this.state.selectedUser}
            handleClose={this.closeAddFundModal}/>
        }

        {this.state.showEditUserModal &&
          <UserEditModal
            user={this.state.selectedUser}
            handleClose={this.closeEditUserModal}/>
        }

        {this.state.showUserSettingsModal &&
          <UserSettingsModal
            user={this.state.selectedUser}
            handleClose={this.closeUserSettingsModal}/>
        }

        {this.state.showUserMessagesModal &&
          <SendMessageToUserModal
            user={this.state.selectedUser}
            handleClose={this.closeUserMessagesModal}/>}
      </div>
    )
  }
}

export default Users;
