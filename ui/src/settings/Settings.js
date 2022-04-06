import React, {Component} from 'react';

import './Settings.css';
import '../common/Common.css'

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableRow from '@material-ui/core/TableRow';

import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';

import MenuItem from '@material-ui/core/MenuItem';

import Save from '@material-ui/icons/Save';

class Settings extends Component {
  constructor(props) {
    super(props);

    this.state = {settings: []};
  }

  componentDidMount() {
    this.updateSettings();
  }

  updateSettings() {
    CampaignsAPI.getConfiguredByUserSettings({id: Session.getUser().id, type: this.props.type},
    response => {
      if (response.status === 'OK') {
        /*if (response.data.length > 0 && response.data[0].skey.indexOf("carrier.ignore") !== -1) {
          var value = true;
          for (var i = 0; i < response.data.length; i++) {
            if (response.data[i].sval === '0') {
              value = false;
              break;
            }
          }

          var verizonValue = '0';
          if (!value) {
            for (var j = 0; j < response.data.length; j++) {
              if (response.data[j].skey.indexOf("carrier.ignore.verizon") !== -1) {
                verizonValue = response.data[j].sval;
                break;
              }
            }
          }

          this.setState({settings: [{
            skey: 'Ignore blockers',
            sval: value ? '1' : '0',
            userId: response.data[0].userId
          }, {
            skey: 'Ignore Verizon only',
            sval: verizonValue,
            userId: response.data[0].userId}
          ]});
        } else {*/
          this.setState({settings: response.data});
        //}
      }
    });
  }

  updateSetting(setting) {
    if (setting.sval) {
      CampaignsAPI.updateSetting(setting,
      response => {
        if (response.status === 'OK') {
          this.updateSettings();
        }
      });
    }
  }

  handleChange(event, type, setting) {
    const value = event.target.value;

    var settings = this.state.settings.slice();
    settings[settings.indexOf(setting)].sval = value;
    settings[settings.indexOf(setting)].dirty = true;

    this.setState({'settings': settings});
  }

  onTextFieldChanged(setting, value) {
    var settings = this.state.settings.slice();
    settings[settings.indexOf(setting)].sval = value;
    settings[settings.indexOf(setting)].dirty = true;

    this.setState({'settings': settings});
  }

  localizedKey(value) {
    if (value.indexOf('master.ignore') !== -1) { return 'Master ignore'; }
    if (value.indexOf('chat.carrier.lookup') !== -1) { return 'Chat carrier lookup'; }
    if (value.indexOf('carrier.ignore.verizon') !== -1) { return 'Ignore Verizon'; }
    if (value.indexOf('carrier.ignore.att') !== -1) { return 'Ignore ATT'; }
    if (value.indexOf('carrier.ignore.t-mobile') !== -1) { return 'Ignore T-Mobile'; }
    if (value.indexOf('carrier.ignore.sprint') !== -1) { return 'Ignore Sprint'; }

    return value;
  }

  render() {
    return (
      <div className='max-width-600 padding-top-10'>
        <Table size='small'>
          <TableBody>
            {this.state.settings.map(row => (
              <TableRow key={row.skey}>
                <TableCell>{this.localizedKey(row.skey)}</TableCell>
                <TableCell>
                  <FormControl className='settings-select'>
                    <Select
                      value={row.sval}
                      onChange={(e) => this.handleChange(e, 'settings', row)}>
                      <MenuItem key='0' value='0'>off</MenuItem>
                      <MenuItem key='1' value='1'>on</MenuItem>
                    </Select>
                  </FormControl>
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

export default Settings;
