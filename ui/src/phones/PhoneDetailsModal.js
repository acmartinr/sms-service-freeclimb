import React, {Component} from 'react';

import './PhoneDetailsModal.css';

import Button from '@material-ui/core/Button';

import Autocomplete from '@material-ui/lab/Autocomplete';
import TextField from '@material-ui/core/TextField';

import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import Typography from '@material-ui/core/Typography';
import CircularProgress from '@material-ui/core/CircularProgress';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableRow from '@material-ui/core/TableRow';
import Checkbox from '@material-ui/core/Checkbox';

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';
import AreaCodeHelper from './AreaCodeHelper';

class PhoneDetailsModal extends Component {
  constructor(props) {
    super(props);

    this.state = {
      title: 'Add New Phones',
      state: '',
      phones: [],
      selectedPhones: [],
      loadingSearch: false,
      loadingBuying: false,
      searchError: false,
      buyError: false,
      errorMessage: '',
      selectAll: false
    };

    this.searchPhoneNumbers = this.searchPhoneNumbers.bind(this);
  }

  searchPhoneNumbers() {
    this.setState({'selectedPhones': [], 'phones': [], 'loadingSearch': true, 'searchError': false, 'buyError': false});
    CampaignsAPI.searchPhoneNumbers({areaCodes: AreaCodeHelper.getAreaCodes(this.state.state)},
      response => {
        if (response.status === 'OK') {
          this.setState({'phones': response.data, 'loadingSearch': false, 'searchError': response.data.length === 0});
        }
      });
  }

  buyPhones() {
    const request = {
      phones: this.state.selectedPhones,
      userId: Session.getUser().id
    }

    this.setState({'loadingBuying': true, buyError: false});

    CampaignsAPI.buyPhones(request,
      response => {
        this.setState({'loadingBuying': false});

        if (response.status === 'OK') {
          this.props.handleClose(true);
        } else {
          if (response.message === 'limit') {
            this.setState({
              'buyError': true,
              errorMessage: 'You reached your phones count limit. Please contact system administrator.'
            });
          } else if (response.message === 'balance') {
            this.setState({'buyError': true, errorMessage: 'Your balance is too low'});
          } else {
            this.setState({'buyError': true, errorMessage: 'Account not funded'});
          }
        }
      });
  }

  onTextFieldChanged(event, field) {
    this.setState({
      [field]: event.target.value
    });
  }

  onCheckChanged(event, field) {
    var selectedPhones = this.state.selectedPhones.slice();

    if (event.target.checked) {
      selectedPhones.push(field.PhoneNumber);
    } else {
      selectedPhones.splice(selectedPhones.indexOf(field.PhoneNumber), 1);
    }

    this.setState({'selectedPhones': selectedPhones});
  }

  onSelectAllChanged(event) {
    var selectAll = event.target ? event.target.checked : event;

    if (selectAll) {
      var selectedPhones = [];
      var phones = this.state.phones.slice();
      for (var i = 0; i < phones.length; i++) {
        selectedPhones.push(phones[i].PhoneNumber);
      }

      this.setState({'selectedPhones': selectedPhones, 'selectAll': selectAll});
    } else {
      this.setState({'selectedPhones': [], 'selectAll': selectAll});
    }
  }

  onValueChanged(event, field) {
    this.setState({
      [field]: event.target.innerText
    });
  }

  render() {
    return (
      <Dialog
        open
        onClose={this.props.handleClose}
        aria-labelledby="form-dialog-title"
        className="custom-sender-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">{this.state.title}</DialogTitle>

        <DialogContent>
          <Autocomplete
            id="state_autocomplete"
            className={this.state.loadingSearch ? 'area-code-wrapper-loading' : 'area-code-wrapper'}
            options={AreaCodeHelper.getStates()}
            getOptionLabel={option => option}
            value={this.state.state}
            onChange={(e) => this.onValueChanged(e, "state")}
            renderInput={params => (
              <TextField
                {...params}
                margin="dense"
                id="state"
                label="Select state"
                type="text"
                error={this.state.searchError}
                className={this.state.loadingSearch ? 'area-code-input-loading' : 'area-code-input'}
              />
            )}
          />

          {!this.state.loadingSearch &&
            <Button
              variant="contained"
              className="search-button"
              disabled={!this.state.state || this.state.state.length === 0 || this.state.loadingSearch}
              onClick={(e) => this.searchPhoneNumbers()} color="primary">
              Search
            </Button>
          }

          {this.state.loadingSearch &&
            <CircularProgress className="right margin-right-10 margin-top-20 custom-progress-phone-details"/>
          }

          {this.state.searchError &&
            <Typography color="error" component="h5" variant="subtitle1">
              Nothing was found. Try another state.
            </Typography>
          }

          {this.state.buyError &&
            <Typography color="error" component="h5" variant="subtitle1">
              {this.state.errorMessage}
            </Typography>
          }

          <div className='space'/>

          <Table>
            <TableBody className='phones-table'>
              {this.state.phones.length > 0 && <TableRow>
                <TableCell className='action-cell-checkbox'>
                  <Checkbox
                    color="primary"
                    checked={this.state.selectAll}
                    onChange={(e) => this.onSelectAllChanged(e)}/>
                </TableCell>
                <TableCell className='pointer'
                           onClick={(e) => this.onSelectAllChanged(!this.state.selectAll)}>Select all</TableCell>
              </TableRow>}
              {this.state.phones.map((row) => (
                <TableRow key={row.PhoneNumber}>
                  <TableCell className='action-cell-checkbox'>
                    <Checkbox
                      color="primary"
                      checked={this.state.selectedPhones.indexOf(row.PhoneNumber) !== -1}
                      onChange={(e) => this.onCheckChanged(e, row)}/>
                  </TableCell>
                  <TableCell>{row.PhoneNumber}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>

        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.handleClose()} color="primary">
            Cancel
          </Button>

          <Button
            disabled={this.state.selectedPhones.length === 0 || this.state.loadingBuying}
            onClick={(e) => this.buyPhones()} color="primary">
            Buy Phones
          </Button>

          {this.state.loadingBuying &&
            <CircularProgress className="custom-progress-phone-details"/>
          }

        </DialogActions>
      </Dialog>
    )
  }
}

export default PhoneDetailsModal;
