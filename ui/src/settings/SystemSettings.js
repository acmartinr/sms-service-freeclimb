import React, {Component} from 'react';

import './Settings.css';
import '../common/Common.css'

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TextField from '@material-ui/core/TextField';

import Save from '@material-ui/icons/Save';

class SystemSettings extends Component {
  constructor(props) {
    super(props);

    this.state = {settings: []};
  }

  componentDidMount() {
    this.updateSettings();
  }

  updateSettings() {
    CampaignsAPI.getSettings({id: Session.getUser().id},
      response => {
        if (response.status === 'OK') {
          this.setState({settings: response.data});
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

  localizedKey(value) {
    if (value === 'phone.system') {
      return 'System phone number';
    }
    if (value === 'phone.forwarding') {
      return 'Default forwarding phone number';
    }
    if (value === 'phone.limit') {
      return 'Account phones limit';
    }

    if (value === 'price.phone') {
      return 'Common user phone price';
    }
    if (value === 'price.sms.inbound') {
      return 'Common user inbound sms price';
    }
    if (value === 'price.sms.outbound') {
      return 'Common user outbound sms price';
    }
    if (value === 'price.lookup') {
      return 'Common phone carrier lookup price';
    }

    if (value === 'unanswered.messages.count') {
      return 'Unanswered messages before removing';
    }
    if (value === 'unanswered.messages.time') {
      return 'Unanswered messages time delay [min]';
    }
    if (value === 'dnc.words') {
      return 'DNS words';
    }
    if (value === 'surcharge.factor') {
      return 'Surcharge factor';
    }

    return value;
  }

  render() {
    return (
      <div className='max-width-800 padding-top-10'>
        <Table size='small'>
          <TableHead>
            <TableRow>
              <TableCell className='key-column'>Setting</TableCell>
              <TableCell>Value</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {this.state.settings.map(row => (
              <TableRow key={row.id}>
                <TableCell>{this.localizedKey(row.skey)}</TableCell>
                <TableCell>
                  <TextField
                    error={row.sval.length === 0 && row.skey !== "phone.forwarding"}
                    margin="dense"
                    label="Enter value"
                    type="text"
                    multiline={row.skey === 'dnc.words'}
                    rows={4}
                    value={row.sval}
                    onChange={(e) => this.onTextFieldChanged(row, e.target.value)}
                    fullWidth
                  />
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

export default SystemSettings;
