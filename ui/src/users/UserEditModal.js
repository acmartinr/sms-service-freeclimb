import React, {Component} from 'react';

import './UserEditModal.css';
import '../common/Common.css';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';

import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';

import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import InputLabel from '@material-ui/core/InputLabel';

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';

import md5 from 'md5';

class UserAddFundModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      value: this.props.user.fullName ? this.props.user.fullName : "",
      password: "",
      resellers: [],
      username: this.props.user.username,
      name: this.props.user.personalName ? this.props.user.personalName : "",
      reseller: this.props.user.role === 3,
      resellerId: 1,
      allowManageMoney: this.props.user.allowManageMoney,
      allowSubUsersPayments: this.props.user.allowSubUsersPayments,
      admin: this.props.user.role === 0,
      domain: this.props.user.domain ? this.props.user.domain : "",
      blocked: !!this.props.user.blocked,
      disabled: !!this.props.user.disabled,
      allowPayments: !!this.props.user.allowPayments,
      allowTransactionsView: !!this.props.user.allowTransactionsView,
      allowSubUsersTransactionsView: !!this.props.user.allowSubUsersTransactionsView,
      valueError: false
    };
  }

  handleBlock() {
    var that = this;

    this.setState({'blocked': !this.state.blocked}, function () {
      that.updateUser();
    });
  }

  handleDisable() {
    var that = this;

    this.setState({'disabled': !this.state.disabled}, function () {
      that.updateUser();
    });
  }

  componentDidMount() {
    this.updateResellers();
  }

  updateResellers() {
    var that = this;
    CampaignsAPI.getResellers(function (response) {
      if (response.status === 'OK') {
        that.setState({resellers: response.data, resellerId: that.props.user.resellerId});
      }
    });
  }

  updateUser() {
    const request = {
      fullName: this.state.value,
      personalName: this.state.name,
      id: this.props.user.id,
      role: this.state.reseller ? 3 : (this.props.user.role === 3 ? 1 : this.props.user.role),
      domain: this.state.domain ? this.state.domain : '',
      password: this.state.password ? md5(this.state.password) : "",
      allowManageMoney: this.state.allowManageMoney ? this.state.allowManageMoney : false,
      allowSubUsersPayments: this.state.allowSubUsersPayments ? this.state.allowSubUsersPayments : false,
      blocked: this.state.blocked,
      disabled: this.state.disabled,
      allowPayments: this.state.allowPayments,
      allowTransactionsView: this.state.allowTransactionsView,
      allowSubUsersTransactionsView: this.state.allowSubUsersTransactionsView,
      resellerId: this.state.resellerId
    }

    this.setState({valueError: false});

    if (!request.fullName) {
      this.setState({valueError: true});
    }

    if (request.fullName) {
      CampaignsAPI.updateUser(request,
        response => {
          if (response.status === 'OK') {
            this.props.handleClose(true);
          }
        });
    }
  }

  onTextFieldChanged(event, field) {
    this.setState({
      [field]: event.target.value
    });
  }

  handleChange(event, type) {
    this.setState({[type]: event.target.checked});
  }

  handleResellerChange(event) {
    this.setState({resellerId: event.target.value});
  }

  isReseller() {
    return Session.getUser().role === 3;
  }

  isAdmin() {
    return Session.getUser().role === 0;
  }

  isAllowPaymentsAvailable() {
    return this.isAdmin() || (Session.getUser().allowSubUsersPayments === '1');
  }

  isAllowTransactionsView() {
    return this.isAdmin() || (Session.getUser().allowSubUsersTransactionsView === '1');
  }

  render() {
    return (
      <Dialog
        open
        onClose={this.props.handleClose}
        aria-labelledby="form-dialog-title"
        className="custom-edit-user-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">Edit user</DialogTitle>

        <DialogContent>
          {(this.isAdmin() && !this.state.reseller && this.props.user.role === 1) &&
            <FormControl className='full-width margin-bottom-20'>
              <InputLabel htmlFor="reseller">Reseller</InputLabel>
              <Select
                value={this.state.resellerId}
                onChange={(e) => this.handleResellerChange(e)}
                inputProps={{name: 'reseller', id: 'reseller'}}>
                <MenuItem value={1}>No reseller</MenuItem>
                {this.state.resellers.map((reseller) =>
                  <MenuItem key={reseller.id} value={reseller.id}>{reseller.fullName}</MenuItem>
                )}
              </Select>
            </FormControl>}

          <TextField
            className="margin-bottom-20"
            margin="dense"
            id="username"
            label="Enter phone"
            type="text"
            value={this.state.username}
            disabled={true}
            fullWidth
          />

          <TextField
            autoFocus
            className="margin-bottom-20"
            margin="dense"
            id="personalName"
            label="Enter name"
            type="text"
            value={this.state.name}
            onChange={(e) => this.onTextFieldChanged(e, "name")}
            fullWidth
          />

          <TextField
            error={this.state.valueError}
            className="margin-bottom-20"
            margin="dense"
            id="value"
            label="Enter business name"
            type="text"
            value={this.state.value}
            onChange={(e) => this.onTextFieldChanged(e, "value")}
            fullWidth
          />

          <TextField
            className="margin-bottom-20"
            margin="dense"
            id="password"
            label="Enter new password"
            type="password"
            value={this.state.password}
            onChange={(e) => this.onTextFieldChanged(e, "password")}
            fullWidth
          />

          {this.isAllowPaymentsAvailable() && <FormControlLabel
            control={
              <Checkbox
                checked={this.state.allowPayments}
                onChange={(e) => this.handleChange(e, 'allowPayments')}
                value="allowPayments"
                color="primary"
              />
            }
            label="Allow payments"/>}

          {this.isAllowTransactionsView() && <FormControlLabel
            control={
              <Checkbox
                checked={this.state.allowTransactionsView}
                onChange={(e) => this.handleChange(e, 'allowTransactionsView')}
                value="allowTransactionsView"
                color="primary"
              />
            }
            label="Allow transactions view"/>}

          {(!this.state.admin && !this.isReseller()) && <FormControlLabel
            control={
              <Checkbox
                checked={this.state.reseller}
                onChange={(e) => this.handleChange(e, 'reseller')}
                value="reseller"
                color="primary"
              />
            }
            label="Reseller"/>}

          {this.state.reseller &&
            <TextField
              className="margin-bottom-20"
              margin="dense"
              id="domain"
              label="Domain"
              type="text"
              value={this.state.domain}
              onChange={(e) => this.onTextFieldChanged(e, "domain")}
              fullWidth
            />
          }

          {this.state.reseller &&
            <FormControlLabel
              control={
                <Checkbox
                  checked={this.state.allowManageMoney}
                  onChange={(e) => this.handleChange(e, 'allowManageMoney')}
                  value="allowManageMoney"
                  color="primary"
                />
              }
              label="Allow manage money"/>
          }

          {this.state.reseller &&
            <FormControlLabel
              control={
                <Checkbox
                  checked={this.state.allowSubUsersPayments}
                  onChange={(e) => this.handleChange(e, 'allowSubUsersPayments')}
                  value="allowSubUsersPayments"
                  color="primary"
                />
              }
              label="Allow sub users payments"/>
          }

          {this.state.reseller &&
            <FormControlLabel
              control={
                <Checkbox
                  checked={this.state.allowSubUsersTransactionsView}
                  onChange={(e) => this.handleChange(e, 'allowSubUsersTransactionsView')}
                  value="allowSubUsersTransactionsView"
                  color="primary"
                />
              }
              label="Allow sub users transactions view"/>
          }

        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.handleClose()} color="primary">
            Cancel
          </Button>
          {this.props.user.id ? <div>
            <Button onClick={(e) => this.handleBlock()} color="primary">
              {this.state.blocked ? "Unblock" : "Block"}
            </Button>
            <Button onClick={(e) => this.handleDisable()} color="primary">
              {this.state.disabled ? "Enabled" : "Disable"}
            </Button></div> : null
          }
          <Button onClick={(e) => this.updateUser()} color="primary">
            Update
          </Button>
        </DialogActions>
      </Dialog>
    )
  }
}

export default UserAddFundModal;
