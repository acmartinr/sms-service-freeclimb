import React, {Component} from 'react';

import './Prices.css';
import '../common/Common.css'

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';

import Autocomplete from '@material-ui/lab/Autocomplete';
import TextField from '@material-ui/core/TextField';

import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';

import MenuItem from '@material-ui/core/MenuItem';

import Save from '@material-ui/icons/Save';

class Prices extends Component {
  constructor(props) {
    super(props);

    this.state = {settings: [], users: [], selectedUser: {id: 0, username: ''}};

    this.handleChange = this.handleChange.bind(this);
  }

  componentDidMount() {
    this.updateUsers();
  }

  updateUsers() {
    const request = {
      userId: Session.getUser().id,
      page: 0,
      limit: 1000
    };

    CampaignsAPI.getUsers(request,
    response => {
      if (response.status === "OK") {
        var that = this;
        this.setState({
          users: response.data.users,
          selectedUser: response.data.users.length > 0 ? response.data.users[0] : {id: 0, username: ''}
        }, function() {that.updatePriceSettings();});
      }
    });
  }

  updatePriceSettings() {
    if (this.state.selectedUser.id === 0) {return 0;}

    CampaignsAPI.getUserSettings({id: this.state.selectedUser.id},
    response => {
      if (response.status === 'OK') {
        var settings = [];
        for (var i = 0; i < response.data.length; i++) {
          var setting = response.data[i];

          setting.userId = Session.getUser().id;
          settings.push(setting);
        }

        this.setState({'settings': response.data});
      }
    });
  }

  updateSetting(setting) {
    if (setting.sval || setting.skey === "phone.forwarding") {
      CampaignsAPI.updateSetting(setting,
      response => {
        if (response.status === 'OK') {
          this.updatePriceSettings();
        }
      });
    }
  }

  onTextFieldChanged(setting, value) {
    var settings = this.state.settings.slice();
    settings[settings.indexOf(setting)].sval = value;
    settings[settings.indexOf(setting)].dirty = true;

    this.setState({'settings': settings});
  }

  handleChange(event, type, setting) {
    if (type === 'users') {
      const userId = event.target.value;
      for (var i = 0; i < this.state.users.length; i++) {
        if (this.state.users[i].id === userId) {
          this.setState({selectedUser: this.state.users[i]}, this.updatePriceSettings);
          return;
        }
      }
    } else if (type === 'settings') {
      const value = event.target.value;

      var settings = this.state.settings.slice();
      settings[settings.indexOf(setting)].sval = value;
      settings[settings.indexOf(setting)].dirty = true;

      this.setState({'settings': settings});
    }
  }

  prettyTitle(user) {
    var title = user.username;
    if (user.fullName) {
      title = title + ' (' + user.fullName + ')';
    }

    return title;
  }

  localizedKey(value) {
    if (value.indexOf('price.phone_') === 0) { return 'User phone price'; }
    if (value.indexOf('price.sms.inbound_') === 0) { return 'User inbound sms price'; }
    if (value.indexOf('price.sms.outbound_') === 0) { return 'User outbound sms price'; }
    if (value.indexOf('auto.reply.enabled_') === 0) { return 'Auto replies'; }
    if (value.indexOf('chat.carrier.lookup.enabled_') === 0) { return 'Chat carrier lookup'; }
    if (value.indexOf('agents.login.enabled_') === 0) { return 'Agents login'; }

    return 'unknown';
  }

  onValueChanged(e, field) {
    var selectedItem = e.target.innerText;

    var that = this;
    var handleSelectedUser = function() {that.updatePriceSettings();};

    for (var i = 0; i < this.state.users.length; i++) {
      if (this.prettyTitle(this.state.users[i]) === selectedItem) {
        this.setState({selectedUser: this.state.users[i]}, handleSelectedUser);
        break;
      }
    }
  }

  render() {
    return (
      <div className='max-width-600 padding-top-10'>
        <Autocomplete
          id="user_autocomplete"
          className='users-select'
          options={this.state.users}
          getOptionLabel={option => this.prettyTitle(option)}
          value={this.state.selectedUser}
          onChange={(e) => this.onValueChanged(e, "user")}
          renderInput={params => (
            <TextField
              {...params}
              margin="dense"
              id="user"
              label="Select user"
              type="text"
              error={this.state.searchError}
              className={this.state.loadingSearch ? 'area-code-input-loading' : 'area-code-input'}
            />
          )}
        />

        <Table size='small'>
          <TableHead>
            <TableRow>
              <TableCell>Setting</TableCell>
              <TableCell>Value</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {this.state.settings.map(row => (
              <TableRow key={row.skey}>
                <TableCell>{this.localizedKey(row.skey)}</TableCell>
                <TableCell>
                  {(row.skey.indexOf('auto.reply.enabled') !== -1 || row.skey.indexOf('chat.carrier.lookup.enabled') !== -1 || row.skey.indexOf('agents.login.enabled') !== -1) &&
                  <FormControl className='settings-select'>
                    <Select
                      value={row.sval}
                      onChange={(e) => this.handleChange(e, 'settings', row)}>
                      <MenuItem key='0' value='0'>disabled</MenuItem>
                      <MenuItem key='1' value='1'>enabled</MenuItem>
                    </Select>
                  </FormControl>}

                  {(row.skey.indexOf('auto.reply.enabled') === -1 && row.skey.indexOf('chat.carrier.lookup.enabled') === -1 && row.skey.indexOf('agents.login.enabled') === -1) &&
                  <TextField
                    error={row.sval.length === 0 && row.skey !== "phone.forwarding"}
                    margin="dense"
                    label="Enter value"
                    type="text"
                    value={row.sval}
                    onChange={(e) => this.onTextFieldChanged(row, e.target.value)}
                    fullWidth
                  />}
                </TableCell>
                <TableCell className='settings-action-cell'>
                  <Save
                    color={row.sval.length === 0 || !row.dirty ? 'disabled' : 'primary'}
                    className='pointer'
                    onClick={(e) => this.updateSetting(row)}/>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    )
  }
}

export default Prices;
