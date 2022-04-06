import React, {Component} from 'react';

import './UserSettingsModal.css';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';

import Session from '../common/Session';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';

import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';

import MenuItem from '@material-ui/core/MenuItem';

import Save from '@material-ui/icons/Save';

import CampaignsAPI from '../campaigns/CampaignsAPI';

class UserSettingsModal extends Component {
  constructor(props) {
    super(props);

    this.state = {settings: []};
  }

  componentDidMount() {
    this.updateSettings();
  }

  updateSettings() {
    CampaignsAPI.getUserSettings({id: this.props.user.id},
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
            this.updateSettings();
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
    const value = event.target.value;

    var settings = this.state.settings.slice();
    settings[settings.indexOf(setting)].sval = value;
    settings[settings.indexOf(setting)].dirty = true;

    this.setState({'settings': settings});
  }

  localizedKey(value) {
    if (value.indexOf('price.phone_') === 0) {
      return 'User phone price';
    }
    if (value.indexOf('price.sms.inbound_') === 0) {
      return 'User inbound sms price';
    }
    if (value.indexOf('price.sms.outbound_') === 0) {
      return 'User outbound sms price';
    }
    if (value.indexOf('auto.reply.enabled_') === 0) {
      return 'Auto replies enabled';
    }
    if (value.indexOf('chat.carrier.lookup.enabled_') === 0) {
      return 'Chat carrier lookup enabled';
    }
    if (value.indexOf('agents.login.enabled_') === 0) {
      return 'Agents login enabled';
    }
    if (value.indexOf('master.ignore.enabled_') === 0) {
      return 'Master ignore enabled';
    }
    if (value.indexOf('carrier.ignore.enabled_') === 0) {
      return 'Carriers ignore enabled';
    }
    if (value.indexOf('phones.bulk.forward.enabled_') === 0) {
      return 'Bulk forwarding enabled';
    }
    if (value.indexOf('download.lists.enabled_') === 0) {
      return 'Download lists enabled';
    }
    if (value.indexOf('surcharge.factor') === 0) {
      return 'Surcharge factor';
    }
    if (value.indexOf('consumerdnc.upload.filter.ignore_') === 0) {
      return 'Ignore consumer dnc upload filter';
    }

    return 'unknown';
  }

  render() {
    return (
      <Dialog
        open
        onClose={this.props.handleClose}
        aria-labelledby="form-dialog-title"
        className="custom-settings-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">Settings</DialogTitle>

        <DialogContent>
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
                    {(row.skey.indexOf('auto.reply.enabled') !== -1 ||
                      row.skey.indexOf('chat.carrier.lookup.enabled') !== -1 ||
                      row.skey.indexOf('agents.login.enabled') !== -1 ||
                      row.skey.indexOf('master.ignore.enabled') !== -1 ||
                      row.skey.indexOf('consumerdnc.upload.filter.ignore') !== -1 ||
                      row.skey.indexOf('carrier.ignore.enabled') !== -1 ||
                      row.skey.indexOf('download.lists.enabled') !== -1 ||
                      row.skey.indexOf('phones.bulk.forward.enabled') !== -1) &&
                    <FormControl className='settings-select'>
                      <Select
                        value={row.sval}
                        onChange={(e) => this.handleChange(e, 'settings', row)}>
                        <MenuItem key='0' value='0'>disabled</MenuItem>
                        <MenuItem key='1' value='1'>enabled</MenuItem>
                      </Select>
                    </FormControl>}

                    {(row.skey.indexOf('auto.reply.enabled') === -1 &&
                      row.skey.indexOf('chat.carrier.lookup.enabled') === -1 &&
                      row.skey.indexOf('agents.login.enabled') === -1 &&
                      row.skey.indexOf('master.ignore.enabled') === -1 &&
                      row.skey.indexOf('consumerdnc.upload.filter.ignore') === -1 &&
                      row.skey.indexOf('carrier.ignore.enabled') === -1 &&
                      row.skey.indexOf('download.lists.enabled') === -1 &&
                      row.skey.indexOf('phones.bulk.forward.enabled') === -1) &&
                    <TextField
                      error={row.sval.length === 0 && row.skey !== "phone.forwarding"}
                      margin="dense"
                      label="Enter value"
                      type="number"
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
        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.handleClose()} color="primary">
            Close
          </Button>
        </DialogActions>
      </Dialog>
    )
  }
}

export default UserSettingsModal;
